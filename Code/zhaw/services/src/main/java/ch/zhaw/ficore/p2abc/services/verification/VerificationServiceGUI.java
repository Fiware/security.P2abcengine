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

//This is a copy of the original UserService from the Code/core-abce/abce-services tree.

package ch.zhaw.ficore.p2abc.services.verification;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.zhaw.ficore.p2abc.configuration.ServicesConfiguration;
import ch.zhaw.ficore.p2abc.services.ExceptionDumper;
import ch.zhaw.ficore.p2abc.services.helpers.RESTHelper;
import ch.zhaw.ficore.p2abc.services.helpers.verification.VerificationGUI;
import ch.zhaw.ficore.p2abc.xml.AuthInfoSimple;
import ch.zhaw.ficore.p2abc.xml.AuthenticationRequest;
import ch.zhaw.ficore.p2abc.xml.CredentialCollection;
import ch.zhaw.ficore.p2abc.xml.IssuanceRequest;
import ch.zhaw.ficore.p2abc.xml.Settings;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Br;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Form;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.H2;
import com.hp.gagawa.java.elements.H3;
import com.hp.gagawa.java.elements.H4;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Input;
import com.hp.gagawa.java.elements.Label;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Option;
import com.hp.gagawa.java.elements.P;
import com.hp.gagawa.java.elements.Select;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Title;
import com.hp.gagawa.java.elements.Tr;
import com.hp.gagawa.java.elements.Ul;

import eu.abc4trust.returnTypes.IssuanceReturn;
import eu.abc4trust.returnTypes.ObjectFactoryReturnTypes;
import eu.abc4trust.returnTypes.UiIssuanceArguments;
import eu.abc4trust.returnTypes.UiIssuanceReturn;
import eu.abc4trust.returnTypes.UiPresentationArguments;
import eu.abc4trust.returnTypes.UiPresentationReturn;
import eu.abc4trust.returnTypes.ui.TokenCandidatePerPolicy;
import eu.abc4trust.xml.Attribute;
import eu.abc4trust.xml.AttributeDescription;
import eu.abc4trust.xml.AttributeDescriptions;
import eu.abc4trust.xml.Credential;
import eu.abc4trust.xml.CredentialDescription;
import eu.abc4trust.xml.CredentialSpecification;
import eu.abc4trust.xml.IssuanceMessage;
import eu.abc4trust.xml.IssuanceMessageAndBoolean;
import eu.abc4trust.xml.ObjectFactory;
import eu.abc4trust.xml.PresentationPolicyAlternatives;
import eu.abc4trust.xml.PresentationToken;

@Path("/verification-gui")
public class VerificationServiceGUI {
    
    @Context
    HttpServletRequest request;
    
    private Logger log = LogManager.getLogger();
    private static String verificationServiceURL = ServicesConfiguration.getVerificationServiceURL();
    
    @GET()
    @Path("/profile/")
    public Response profile() {
        log.entry();

        try {
            Html html = VerificationGUI.getHtmlPramble("Profile", request);
            Div mainDiv = new Div().setCSSClass("mainDiv");
            html.appendChild(VerificationGUI.getBody(mainDiv));
            mainDiv.appendChild(new H2().appendChild(new Text("Profile")));

            String text = "Welcome to your profile! Here you can edit and manage your personal data and settings.";
            P p = new P().setCSSClass("info");
            mainDiv.appendChild(p);
            p.appendChild(new Text(text));
            p.appendChild(new Br());
            text = "Credential specifications specify what attributes a credential can or has to contain.";
            p.appendChild(new Text(text));

            Ul ul = new Ul();
            ul.appendChild(new Li().appendChild(new A()
                    .setHref("./credentials").appendChild(
                            new Text("Manage credentials"))));
            ul.appendChild(new Li().appendChild(new A().setHref(
                    "./credentialSpecifications").appendChild(
                    new Text("Manage credential specifications"))));

            mainDiv.appendChild(ul);

            return log.exit(Response.ok(html.write()).build());

        } catch (Exception e) {
            log.catching(e);
            return log.exit(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(VerificationGUI.errorPage(
                            ExceptionDumper.dumpExceptionStr(e, log), request)
                            .write()).build());
        }
    }
    
    @GET()
    @Path("/credentialSpecifications/")
    public Response credentialSpecifications() {
        log.entry();

        try {
            Settings settings = (Settings) RESTHelper.getRequest(verificationServiceURL
                    + "getSettings/", Settings.class);

            List<CredentialSpecification> credSpecs = settings.credentialSpecifications;

            Html html = VerificationGUI.getHtmlPramble("Profile", request);
            Div mainDiv = new Div().setCSSClass("mainDiv");
            html.appendChild(VerificationGUI.getBody(mainDiv));

            mainDiv.appendChild(new H2().appendChild(new Text("Profile")));
            mainDiv.appendChild(new H3().appendChild(new Text(
                    "Credential Specifications")));

            for (CredentialSpecification credSpec : credSpecs) {

                Div credDiv = new Div().setCSSClass("credDiv");
                mainDiv.appendChild(credDiv);

                AttributeDescriptions attribDescs = credSpec
                        .getAttributeDescriptions();
                List<AttributeDescription> attrDescs = attribDescs
                        .getAttributeDescription();

                Table tbl = new Table();
                credDiv.appendChild(new H4().appendChild(new Text(credSpec
                        .getSpecificationUID().toString())));
                credDiv.appendChild(tbl);
                Tr tr = null;
                tr = new Tr()
                        .setCSSClass("heading")
                        .appendChild(new Td().appendChild(new Text("Name")))
                        .appendChild(new Td().appendChild(new Text("Type")))
                        .appendChild(new Td().appendChild(new Text("Encoding")));
                tbl.appendChild(tr);

                for (AttributeDescription attrDesc : attrDescs) {
                    String name = attrDesc.getFriendlyAttributeName().get(0)
                            .getValue();
                    String encoding = attrDesc.getEncoding().toString();
                    String type = attrDesc.getDataType().toString();
                    tr = new Tr()
                            .appendChild(new Td().appendChild(new Text(name)))
                            .appendChild(new Td().appendChild(new Text(type)))
                            .appendChild(
                                    new Td().appendChild(new Text(encoding)));
                    tbl.appendChild(tr);
                }
            }

            return log.exit(Response.ok(html.write()).build());

        } catch (Exception e) {
            log.catching(e);
            return log.exit(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(VerificationGUI.errorPage(
                            ExceptionDumper.dumpExceptionStr(e, log), request)
                            .write()).build());
        }
    }
}