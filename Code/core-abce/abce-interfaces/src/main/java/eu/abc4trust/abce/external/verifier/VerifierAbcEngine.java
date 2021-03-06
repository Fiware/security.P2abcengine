//* Licensed Materials - Property of IBM, Miracle A/S, and            *
//* Alexandra Instituttet A/S                                         *
//* eu.abc4trust.pabce.1.0                                            *
//* (C) Copyright IBM Corp. 2012. All Rights Reserved.                *
//* (C) Copyright Miracle A/S, Denmark. 2012. All Rights Reserved.    *
//* (C) Copyright Alexandra Instituttet A/S, Denmark. 2012. All       *
//* Rights Reserved.                                                  *
//* US Government Users Restricted Rights - Use, duplication or       *
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp. *
//*                                                                   *
//* This file is licensed under the Apache License, Version 2.0 (the  *
//* "License"); you may not use this file except in compliance with   *
//* the License. You may obtain a copy of the License at:             *
//*   http://www.apache.org/licenses/LICENSE-2.0                      *
//* Unless required by applicable law or agreed to in writing,        *
//* software distributed under the License is distributed on an       *
//* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY            *
//* KIND, either express or implied.  See the License for the         *
//* specific language governing permissions and limitations           *
//* under the License.                                                *
//*/**/****************************************************************

package eu.abc4trust.abce.external.verifier;

import java.net.URI;

import eu.abc4trust.cryptoEngine.CryptoEngineException;
import eu.abc4trust.exceptions.TokenVerificationException;
import eu.abc4trust.xml.PresentationPolicyAlternatives;
import eu.abc4trust.xml.PresentationToken;
import eu.abc4trust.xml.PresentationTokenDescription;
import eu.abc4trust.xml.RevocationInformation;

public interface VerifierAbcEngine {


    /**
     * This method, on input a presentation policy p and a presentation token t,
     * checks whether the token t satisfies the policy p and checks the validity
     * of the cryptographic evidence included in token t. If both checks succeed
     * and store is set to true, this method stores the token in a dedicated
     * store and returns a description of the token that includes a unique
     * identifier by means of which the token can later be retrieved from the
     * store. If one of the checks fails, this method returns a list of error
     * messages.
     * 
     * @param p
     * @param t
     * @param store
     * @return
     * @throws CryptoEngineException
     */
    public PresentationTokenDescription verifyTokenAgainstPolicy(
            PresentationPolicyAlternatives p, PresentationToken t, boolean store)
                    throws TokenVerificationException, CryptoEngineException;

    /**
     * This method looks up a previously verified presentation token. The unique token identifier
     * tokenuid is the identifier that was included in the token description that was returned by the
     * PolicyTokenMatcher.verifyToken method when the token was verified.
     * 
     * @param tokenUid
     * @return
     */
    public PresentationToken getToken(URI tokenUid);

    /**
     * This method deletes the previously verified presentation token referenced by the unique
     * identifier tokenuid. It returns true in case of successful deletion, and false otherwise.
     * 
     * @param tokenUid
     * @return
     */
    public boolean deleteToken(URI tokenUid);

    /**
     * This method retrives the latest revocation information from the
     * revocation authority.
     * 
     * @params revParamsUid
     * @return
     * @throws CryptoEngineException
     */
    public RevocationInformation getLatestRevocationInformation(URI revParamsUid)
            throws CryptoEngineException;

}
