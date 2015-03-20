package ch.zhaw.ficore.p2abc.services.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URLEncoder;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import ch.zhaw.ficore.p2abc.configuration.ConnectionParameters;
import ch.zhaw.ficore.p2abc.services.helpers.RESTHelper;
import ch.zhaw.ficore.p2abc.xml.PresentationPolicyAlternativesCollection;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.TestConstants;

import eu.abc4trust.xml.ObjectFactory;
import eu.abc4trust.xml.PresentationPolicyAlternatives;

public class TestVerifierAPI extends JerseyTest {

    private String verificationServiceURL = "verification/protected/";
    private String verificationServiceURLUnprot = "verification/";

    public TestVerifierAPI() throws Exception {
        super("ch.zhaw.ficore.p2abc.services");
        verificationServiceURL = getBaseURI() + verificationServiceURL;
        verificationServiceURLUnprot = getBaseURI() + verificationServiceURLUnprot;
    }

    private static String getBaseURI() {
        return "http://localhost:" + TestConstants.JERSEY_HTTP_PORT + "/";
    }

    static File storageFile;
    static String dbName = "URIBytesStorage";
    ObjectFactory of = new ObjectFactory();

    @BeforeClass
    public static void initJNDI() throws Exception {        
        System.out.println("init [TestVerificationAPI]");
        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        try {
            ic.destroySubcontext("java:");
        } catch (Exception e) {
        }

        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");
        ic.createSubcontext("java:/comp/env/cfg");
        ic.createSubcontext("java:/comp/env/cfg/Source");
        ic.createSubcontext("java:/comp/env/cfg/ConnectionParameters");

        ConnectionParameters cp = new ConnectionParameters();
        ic.bind("java:/comp/env/cfg/ConnectionParameters/attributes", cp);
        ic.bind("java:/comp/env/cfg/ConnectionParameters/authentication", cp);
        ic.bind("java:/comp/env/cfg/Source/attributes", "FAKE");
        ic.bind("java:/comp/env/cfg/Source/authentication", "FAKE");
        ic.bind("java:/comp/env/cfg/bindQuery", "FAKE");
        ic.bind("java:/comp/env/cfg/restAuthPassword", "");
        ic.bind("java:/comp/env/cfg/restAuthUser", "issuerapi");
        ic.bind("java:/comp/env/cfg/verificationServiceURL", "");
        ic.bind("java:/comp/env/cfg/userServiceURL", "");
        ic.bind("java:/comp/env/cfg/issuanceServiceURL", "");
        ic.bind("java:/comp/env/cfg/verifierIdentity", "unknown");

        SQLiteDataSource ds = new SQLiteDataSource();

        storageFile = File.createTempFile("test", "sql");

        ds.setUrl("jdbc:sqlite:" + storageFile.getPath());
        System.out.println(ds.getUrl());
        ic.rebind("java:/comp/env/jdbc/" + dbName, ds);
        ic.bind("java:/comp/env/cfg/useDbLocking", new Boolean(true));
        
        ic.close();

    }
    
    @Before
    public void doReset() throws Exception {
        RESTHelper.postRequest(verificationServiceURL + "reset"); //make sure the service is *clean* before each test.
    }

    @AfterClass
    public static void cleanup() throws Exception {
        storageFile.delete();
    }
    
    
  
    /** Tests verifier status.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Verifier set up and running.
     * 
     * @fiware-unit-test-test This test tests that a running verifier responds
     * to a status request. 
     * 
     * @fiware-unit-test-expected-outcome HTTP 200.
     */
    @Test
    public void testStatus() throws Exception {
        RESTHelper.getRequest(verificationServiceURL +"status");
    }
    
    /** Tests creation of resources.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Verifier set up and running, no
     * resource with name "test" registered.
     * 
     * @fiware-unit-test-test This test tests that resources can be correctly
     * registered. In particular, this test creates a resource with name
     * "test", retrieves it again and then tests if, for the retrieved resource
     * 
     * * The number of URIs is 1.
     * * The URI is "test". 
     * * The number of redirect URLs is 1.
     * * The redirect URL is "http://localhost/foo".
     * * The number of policy alternatives is 1.
     * 
     * @fiware-unit-test-expected-outcome HTTP 200 for all requests, correct
     * values for all object attributes.
     */
    @Test
    public void testCreateResource() throws Exception {
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("redirectURI", "http://localhost/foo");
        
        RESTHelper.putRequest(verificationServiceURL + "resource/create/test", params);
        
        PresentationPolicyAlternativesCollection ppac = 
                (PresentationPolicyAlternativesCollection) RESTHelper.getRequest(verificationServiceURL + "presentationPolicyAlternatives/list",
                PresentationPolicyAlternativesCollection.class);
        
        assertEquals(ppac.uris.size(), 1);
        assertEquals(ppac.uris.get(0),"test");
        assertEquals(ppac.redirectURIs.size(), 1);
        assertEquals(ppac.redirectURIs.get(0),"http://localhost/foo");
        assertEquals(ppac.presentationPolicyAlternatives.size(), 1);
    }
    
    /** Tests addition of presentation policy alternatives.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Verifier set up and running, a
     * resource with name "test" registered.
     * 
     * @fiware-unit-test-test This test tests that policy alternatives can be
     * added for resources. In this case, we add a policy alternative named
     * "urn:policy", retrieve it again and then check that
     * 
     * * The number of policy alternatives is 1.
     * * The policy name is "urn:policy".
     * 
     * @fiware-unit-test-expected-outcome HTTP 200 for all requests, correct
     * values for all object attributes.
     */
    @Test
    public void testAddPPA() throws Exception {
        testCreateResource();
        
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("puid", "urn:policy");
        
        
        RESTHelper.postRequest(verificationServiceURL + "presentationPolicyAlternatives/addPolicyAlternative/test", params);
        
        PresentationPolicyAlternatives ppas = (PresentationPolicyAlternatives) RESTHelper.getRequest(
                verificationServiceURL + "presentationPolicyAlternatives/get/test",
                PresentationPolicyAlternatives.class);
        
        assertEquals(ppas.getPresentationPolicy().size(), 1);
        assertEquals(ppas.getPresentationPolicy().get(0).getPolicyUID().toString(),"urn:policy");
    }
    
    /** Tests deletion of presentation policy alternatives.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Verifier set up and running, a
     * resource with name "test" registered, having policy alternative with
     * name "urn:policy".
     * 
     * @fiware-unit-test-test This test tests that policy alternatives can be
     * deleted. In this case, we delete a policy alternative named
     * "urn:policy".
     * 
     * @fiware-unit-test-expected-outcome HTTP 200 for all requests.
     */
    @Test
    public void testDeletePPA() throws Exception {
        testAddPPA();
        
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("puid", "urn:policy");
        
        RESTHelper.postRequest(
                verificationServiceURL + "presentationPolicyAlternatives/deletePolicyAlternative/test",
                params);
        
        PresentationPolicyAlternatives ppas = (PresentationPolicyAlternatives) RESTHelper.getRequest(
                verificationServiceURL + "presentationPolicyAlternatives/get/test",
                PresentationPolicyAlternatives.class);
        
        assertEquals(ppas.getPresentationPolicy().size(), 0);
    }
    
    /** Tests deletion of resources.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Verifier set up and running, a
     * resource with name "test" registered.
     * 
     * @fiware-unit-test-test This test tests that resources can be
     * deleted. In this case, we delete a resource named "test".
     * 
     * @fiware-unit-test-expected-outcome HTTP 200 for all requests, resource
     * "test" gone after deletion.
     */
    @Test
    public void testDeleteResource() throws Exception {
        testCreateResource();
        
        RESTHelper.deleteRequest(verificationServiceURL + "resource/delete/test");
        
        PresentationPolicyAlternativesCollection ppac = 
                (PresentationPolicyAlternativesCollection) RESTHelper.getRequest(verificationServiceURL + "presentationPolicyAlternatives/list",
                PresentationPolicyAlternativesCollection.class);
        
        assertEquals(ppac.uris.size(), 0);
    }
    
    /** Tests adding an alias to a PresentationPolicyAlternative of a resource.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition Requires a resource with name "test" and a PresentationPolicyAlternative with "urn:policy".
     * 
     * @fiware-unit-test-test This test tests that an alias can be added. 
     * 
     * @fiware-unit-test-expected-outcome HTTP 200.
     * 
     * @throws Exception
     */
    @Test
    public void testAddAlias() throws Exception {
        testAddPPA();
        
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("al", "somealias");
        
        RESTHelper.postRequest(verificationServiceURL + "presentationPolicyAlternatives/addAlias/test/" + URLEncoder.encode("urn:policy","UTF-8"),
                params);
    }
    
    /** Tests adding an issuer alternative.
     * 
     * @fiware-unit-test-feature FIWARE.Feature.Security.Privacy.Verification.Verification
     * 
     * @fiware-unit-test-initial-condition depends on {@link testAddPPA}.
     * 
     * @fiware-unit-test-test This test tests than an issuer alternative can be added.
     * 
     * @fiware-unit-test-expected-outcome HTTP 200.
     * 
     * @throws Exception
     */
    @Test
    public void testAddIssuerAlternative() throws Exception {
        testAddAlias();
        
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("al", "somealias");
        params.add("ip", "urn:issuer-alt");
        
        RESTHelper.postRequest(verificationServiceURL +
                "presentationPolicyAlternatives/addIssuerAlternative/test/" + URLEncoder.encode("urn:policy","UTF-8"),
                params);
    }
    
}