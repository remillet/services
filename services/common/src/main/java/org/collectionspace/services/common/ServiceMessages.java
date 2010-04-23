/**
 *  This document is a part of the source code and related artifacts
 *  for CollectionSpace, an open source collections management system
 *  for museums and related institutions:

 *  http://www.collectionspace.org
 *  http://wiki.collectionspace.org

 *  Copyright 2010 University of California at Berkeley

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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.collectionspace.services.common;

/**
 * Common service messages
 * @author
 */
public class ServiceMessages {

    private static final String FAILED = "failed : ";
    public static final String POST_FAILED = "POST " + FAILED;
    public static final String GET_FAILED = "GET " + FAILED;
    public static final String PUT_FAILED = "PUT " + FAILED;
    public static final String DELETE_FAILED = "DELETE " + FAILED;
    public static final String LIST_FAILED = "LIST " + FAILED;
    public static final String SEARCH_FAILED = "GET (query) " + FAILED;
    
    public static final String UNKNOWN_ERROR_MSG = "Unknown error ";
    public static final String VALIDATION_FAILURE = "Validation failure ";
    public static final String MISSING_INVALID_CSID = "missing/invalid csid=";

}
