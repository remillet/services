/**
 *  This document is a part of the source code and related artifacts
 *  for CollectionSpace, an open source collections management system
 *  for museums and related institutions:

 *  http://www.collectionspace.org
 *  http://wiki.collectionspace.org

 *  Copyright 2009 University of California at Berkeley

 *  Licensed under the Educational Community License (ECL), Version 2.0.
 *  You may not use this file except in compliance with this License.

 *  You may obtain a copy of the ECL 2.0 License at

 *  https://source.collectionspace.org/collection-space/LICENSE.txt

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.collectionspace.services.authorization;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl;
//import org.collectionspace.services.common.context.RemoteServiceContextImpl;
import org.collectionspace.services.common.ServiceMessages;
import org.collectionspace.services.common.context.ServiceContext;
import org.collectionspace.services.common.context.ServiceContextFactory;
import org.collectionspace.services.common.context.RemoteServiceContextFactory;
import org.collectionspace.services.common.document.BadRequestException;
import org.collectionspace.services.common.document.DocumentFilter;
import org.collectionspace.services.common.document.DocumentNotFoundException;
import org.collectionspace.services.common.document.DocumentHandler;
import org.collectionspace.services.common.security.UnauthorizedException;
import org.collectionspace.services.common.storage.StorageClient;
import org.collectionspace.services.common.storage.jpa.JpaStorageClientImpl;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RoleResource.
 */
@Path("/authorization/roles")
@Consumes("application/xml")
@Produces("application/xml")
public class RoleResource
        extends AbstractCollectionSpaceResourceImpl {

    /** The service name. */
    final private String serviceName = "authorization/roles";
    /** The logger. */
    final Logger logger = LoggerFactory.getLogger(RoleResource.class);
    /** The storage client. */
    final StorageClient storageClient = new JpaStorageClientImpl();

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getVersionString()
     */
    @Override
    protected String getVersionString() {
        /** The last change revision. */
        final String lastChangeRevision = "$LastChangedRevision: 1165 $";
        return lastChangeRevision;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getServiceName()
     */
    @Override
    public String getServiceName() {
        return serviceName;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.CollectionSpaceResource#getCommonPartClass()
     */
    @Override
    public Class<RoleResource> getCommonPartClass() {
        return RoleResource.class;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.CollectionSpaceResource#getServiceContextFactory()
     */
    @Override
    public ServiceContextFactory getServiceContextFactory() {
        return RemoteServiceContextFactory.get();
    }

//    private <T> ServiceContext createServiceContext(T obj) throws Exception {
//        ServiceContext ctx = new RemoteServiceContextImpl<T, T>(getServiceName());
//        ctx.setInput(obj);
//        ctx.setDocumentType(Role.class.getPackage().getName()); //persistence unit
//        ctx.setProperty("entity-name", Role.class.getName());
//        return ctx;
//    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getStorageClient(org.collectionspace.services.common.context.ServiceContext)
     */
    @Override
    public StorageClient getStorageClient(ServiceContext ctx) {
        //FIXME use ctx to identify storage client
        return storageClient;
    }

//    @Override
//    public DocumentHandler createDocumentHandler(ServiceContext ctx) throws Exception {
//        DocumentHandler docHandler = ctx.getDocumentHandler();
//        docHandler.setCommonPart(ctx.getInput());
//        return docHandler;
//    }
    /**
     * Creates the role.
     *
     * @param input the input
     *
     * @return the response
     */
    @POST
    public Response createRole(Role input) {
        try {
            ServiceContext ctx = createServiceContext(input, Role.class);
            DocumentHandler handler = createDocumentHandler(ctx);
            String csid = getStorageClient(ctx).create(ctx, handler);
            UriBuilder path = UriBuilder.fromResource(RoleResource.class);
            path.path("" + csid);
            Response response = Response.created(path.build()).build();
            return response;
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity(ServiceMessages.POST_FAILED
                    + bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity(ServiceMessages.POST_FAILED +
                    ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in createRole", e);
            }
            logger.error(ServiceMessages.UNKNOWN_ERROR_MSG, e);
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity(ServiceMessages.POST_FAILED +
                    ServiceMessages.UNKNOWN_ERROR_MSG).type("text/plain").build();
            throw new WebApplicationException(response);
        }
    }

    /**
     * Gets the role.
     * 
     * @param csid the csid
     * 
     * @return the role
     */
    @GET
    @Path("{csid}")
    public Role getRole(
            @PathParam("csid") String csid) {
        if (logger.isDebugEnabled()) {
            logger.debug("getRole with csid=" + csid);
        }
        if (csid == null || "".equals(csid)) {
            logger.error("getRole: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    ServiceMessages.GET_FAILED + "role csid=").type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        Role result = null;
        try {
            ServiceContext ctx = createServiceContext((Role) null, Role.class);
            DocumentHandler handler = createDocumentHandler(ctx);
            getStorageClient(ctx).get(ctx, csid, handler);
            result = (Role) ctx.getOutput();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity(ServiceMessages.GET_FAILED +
                    ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getRole", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    ServiceMessages.GET_FAILED + "role csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getRole", e);
            }
            logger.error(ServiceMessages.UNKNOWN_ERROR_MSG, e);
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity(ServiceMessages.GET_FAILED +
                    ServiceMessages.UNKNOWN_ERROR_MSG).type("text/plain").build();
            throw new WebApplicationException(response);
        }

        if (result == null) {
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    ServiceMessages.GET_FAILED + "role csid=" + csid + ": was not found.").type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    /**
     * Gets the role list.
     * 
     * @param ui the ui
     * 
     * @return the role list
     */
    @GET
    @Produces("application/xml")
    public RolesList getRoleList(
            @Context UriInfo ui) {
        RolesList roleList = new RolesList();
        try {
            ServiceContext ctx = createServiceContext((RolesList) null, Role.class);
            DocumentHandler handler = createDocumentHandler(ctx);
            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
            DocumentFilter myFilter = handler.createDocumentFilter();
            myFilter.setPagination(queryParams);
            myFilter.setQueryParams(queryParams);
            handler.setDocumentFilter(myFilter);
            getStorageClient(ctx).getFiltered(ctx, handler);
            roleList = (RolesList) handler.getCommonPartList();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity(ServiceMessages.LIST_FAILED +
                    ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);

        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in getRoleList", e);
            }
            logger.error(ServiceMessages.UNKNOWN_ERROR_MSG, e);
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity(ServiceMessages.LIST_FAILED +
                    ServiceMessages.UNKNOWN_ERROR_MSG).type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return roleList;
    }

    /**
     * Update role.
     * 
     * @param csid the csid
     * @param theUpdate the the update
     * 
     * @return the role
     */
    @PUT
    @Path("{csid}")
    public Role updateRole(
            @PathParam("csid") String csid,
            Role theUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateRole with csid=" + csid);
        }
        if (csid == null || "".equals(csid)) {
            logger.error("updateRole: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    ServiceMessages.PUT_FAILED + "role " +
                    ServiceMessages.MISSING_INVALID_CSID + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        Role result = null;
        try {
            ServiceContext ctx = createServiceContext(theUpdate, Role.class);
            DocumentHandler handler = createDocumentHandler(ctx);
            getStorageClient(ctx).update(ctx, csid, handler);
            result = (Role) ctx.getOutput();
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity(ServiceMessages.PUT_FAILED +
                    bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity(ServiceMessages.PUT_FAILED  +
                    ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caugth exception in updateRole", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    ServiceMessages.PUT_FAILED + "role csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            logger.error(ServiceMessages.UNKNOWN_ERROR_MSG, e);
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity(
                    ServiceMessages.PUT_FAILED +
                    ServiceMessages.UNKNOWN_ERROR_MSG).type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    /**
     * Delete role.
     * 
     * @param csid the csid
     * 
     * @return the response
     */
    @DELETE
    @Path("{csid}")
    public Response deleteRole(@PathParam("csid") String csid) {

        if (logger.isDebugEnabled()) {
            logger.debug("deleteRole with csid=" + csid);
        }
        if (csid == null || "".equals(csid)) {
            logger.error("deleteRole: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    ServiceMessages.DELETE_FAILED + "role csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        try {
            ServiceContext ctx = createServiceContext((Role) null, Role.class);
            ((JpaStorageClientImpl) getStorageClient(ctx)).deleteWhere(ctx, csid);
            return Response.status(HttpResponseCodes.SC_OK).build();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity(ServiceMessages.DELETE_FAILED  + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);

        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught exception in deleteRole", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    ServiceMessages.DELETE_FAILED + "role csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            logger.error(ServiceMessages.UNKNOWN_ERROR_MSG, e);
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity(
                    ServiceMessages.DELETE_FAILED + ServiceMessages.UNKNOWN_ERROR_MSG).type("text/plain").build();
            throw new WebApplicationException(response);
        }

    }
}
