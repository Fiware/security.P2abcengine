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

package eu.abc4trust.cryptoEngine.idemix.util;

import java.util.List;

import org.w3c.dom.Element;

import eu.abc4trust.xml.ObjectFactory;
import eu.abc4trust.xml.SystemParameters;
import eu.abc4trust.xml.util.XmlUtils;

/**
 * @author hgk
 *
 * Handles reading and parsing Idemix specific SystemParamters
 * They can either be represented by a Ibm Java class or as XML.
 * Handles mixing parameters with UProve Parameters inside same ABC4Trust SystemParameter XML/JaxB
 */
public class IdemixSystemParameters {

    private final SystemParameters systemParameters;
    private com.ibm.zurich.idmx.utils.SystemParameters idemixSystemParameters = null;
    private com.ibm.zurich.idmx.utils.GroupParameters idemixGroupParameters = null;
    @SuppressWarnings("unused")
    private static final String IDEMIX_NAMESPACE = "http://www.zurich.ibm.com/security/idemix";

    public IdemixSystemParameters(SystemParameters syspars) {
        this.systemParameters = syspars;
        this.findIdemixSpecificParameters();
    }

    public com.ibm.zurich.idmx.utils.SystemParameters getSystemParameters() {
        if (this.idemixSystemParameters != null) {
            return this.idemixSystemParameters;
        }
        try {
            throw new IllegalStateException("Idemix specific SystemParameters not found "
                    + this.systemParameters);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to print debug of (Idemix) SystemParameters : " + this.systemParameters.getAny(), e);
        }
    }

    public com.ibm.zurich.idmx.utils.GroupParameters getGroupParameters() {
        if (this.idemixGroupParameters != null) {
            return this.idemixGroupParameters;
        }
        try {
            throw new IllegalStateException("Idemix specific GroupParameters not found "
                    + XmlUtils.toNormalizedXML(new ObjectFactory().createSystemParameters(this.systemParameters)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to print debug of (Idemix) GroupParameters : " + this.systemParameters.getAny(), e);
        }
    }

    private void findIdemixSpecificParameters() {
        com.ibm.zurich.idmx.utils.Parser parser = com.ibm.zurich.idmx.utils.Parser.getInstance();

        List<Object> list = this.systemParameters.getAny();
        for (Object o : list) {
            if (o instanceof com.ibm.zurich.idmx.utils.SystemParameters) {
                this.idemixSystemParameters = (com.ibm.zurich.idmx.utils.SystemParameters) o;
                continue;
            }
            if (o instanceof com.ibm.zurich.idmx.utils.GroupParameters) {
                this.idemixGroupParameters = (com.ibm.zurich.idmx.utils.GroupParameters) o;
                continue;
            }
            if (o instanceof Element) {
                Element element = (Element) o;
                String localName = element.getLocalName();
                if (localName == null) {
                    localName = element.getNodeName();
                }
                if ("SystemParameters".equals(localName)) {
                    this.idemixSystemParameters =
                            (com.ibm.zurich.idmx.utils.SystemParameters) parser.parse(element);
                    continue;
                }
                //       if (element.getNamespaceURI().equals(IDEMIX_NAMESPACE)) {
                if ("GroupParameters".equals(localName)) {
                    this.idemixGroupParameters =
                            (com.ibm.zurich.idmx.utils.GroupParameters) parser.parse(element);
                    continue;
                }
                //        }
            }
        }
    }

}
