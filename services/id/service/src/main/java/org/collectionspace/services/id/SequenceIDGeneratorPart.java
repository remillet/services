/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright © 2009 Regents of the University of California
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * SequenceIDGeneratorPart, interface for a component part of an
 * ID Generator that can generate identifiers (IDs) that are part
 * of a sequence.
 *
 * $LastChangedRevision: 625 $
 * $LastChangedDate$
 */
package org.collectionspace.services.id;

public interface SequenceIDGeneratorPart
    extends StoredValueIDGeneratorPart {
    
	// Returns the next ID in the sequence, and sets
	// the current value to that ID.
	public String nextID() throws IllegalStateException;
		
}
