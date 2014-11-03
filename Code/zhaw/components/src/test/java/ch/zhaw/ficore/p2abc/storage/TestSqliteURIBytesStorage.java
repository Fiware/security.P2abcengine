package ch.zhaw.ficore.p2abc.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Tests basic functions of SQLite URI storage.
 * 
 * Be careful when extending this class with more tests: A new SQLite
 * database file is created for <em>every</em> test.
 * 
 * @author Stephan Neuhaus &lt;stephan.neuhaus@zhaw.ch&gt;
 * @version 1.0
 */
public class TestSqliteURIBytesStorage {
    private static final String table = "TestTable";

    private SqliteURIBytesStorage storage;
    private File storageFile;
    private URI myUri;

    @Before
    public void setUp() throws Exception {
        storageFile = File.createTempFile("test", "sql", new File("."));
        storage = new SqliteURIBytesStorage(storageFile.getPath(), table);
        myUri = new URI("http://www.zhaw.ch");
    }

    @After
    public void tearDown() throws Exception {
        storageFile.delete();
    }

    @Test(expected=UnsafeTableNameException.class)
    public void testInvalidTableName() throws Exception {
        String tableName = "users; DROP TABLE customers";
        SqliteURIBytesStorage invalidStorage = new SqliteURIBytesStorage("hi", tableName);
        invalidStorage.get(myUri);
    }
    
    @Test
    public void testPutNew() throws Exception {
        assertTrue(storage.putNew("foobar", "barfoo".getBytes()));
        assertFalse(storage.putNew("foobar", "barfoo".getBytes()));
    }
    
    @Test
    public void testURIString() throws Exception {
        storage.put("http://zhaw.ch/foo bar", new byte[]{1,2,3});
        storage.put("http://zhaw.ch/foo%20bar", new byte[]{5,4,3});
        byte[] ret = storage.get(new URI("http://zhaw.ch/foo%20bar"));
        assertTrue(Arrays.equals(ret, new byte[]{5,4,3}));
        ret = storage.get("http://zhaw.ch/foo bar");
        assertTrue(Arrays.equals(ret, new byte[]{1,2,3}));
        List<URI> uris = storage.keys();
        assertTrue(uris.size() == 1);
        List<String> keys = storage.keysAsStrings();
        assertTrue(keys.size() == 2);
    }
    
    @Test
    public void testValuesAndKeys() throws Exception {
        storage.put(new URI("urn:foobar"), new byte[]{1,9,9});
        storage.put(new URI("urn:barfoo"), new byte[]{0,0,0});
        
        List<URI> keys = storage.keys();
        assertTrue(keys.size() == 2);
        
        assertTrue(keys.contains(new URI("urn:foobar")));
        assertTrue(keys.contains(new URI("urn:barfoo")));
        
        assertTrue(Arrays.equals(new byte[]{1,9,9}, storage.get(new URI("urn:foobar"))));
        assertTrue(Arrays.equals(new byte[]{0,0,0}, storage.get(new URI("urn:barfoo"))));
        
        List<byte[]> values = storage.values();
        assertTrue(values.size() == 2);
        
        assertTrue(Arrays.equals(new byte[]{1,9,9}, values.get(0)));
        assertTrue(Arrays.equals(new byte[]{0,0,0}, values.get(1)));
    }

    @Test
    public void testEmptyKeyOnEmptyStorage() throws Exception {
        assertFalse(storage.containsKey(new URI("")));
    }

    @Test
    public void testUrnKeyOnEmptyStorage() throws Exception {
        assertFalse(storage.containsKey(new URI("urn:abc4trust:1.0:encoding:integer:signed")));
    }

    @Test
    public void testHttpKeyOnEmptyStorage() throws Exception {
        assertFalse(storage.containsKey(myUri));
    }

    @Test
    public void testContainsSimpleKey() throws Exception {
        byte[] stored = new byte[] { 0x01 };

        storage.putNew(myUri, stored);
        assertTrue(storage.containsKey(myUri));
    }

    @Test
    public void testSimpleKeyValue() throws Exception {
        byte[] stored = new byte[] { 0x01 };

        storage.putNew(myUri, stored);
        byte[] value = storage.get(myUri);
        assertTrue(value != null);
        assertTrue(value.length == stored.length);
        assertTrue(Arrays.equals(value, stored));
    }

    @Test
    public void testSomeNonexistentKey() throws Exception {
        storage.putNew(myUri, new byte[] { 0x01 });
        assertFalse(storage.containsKey(new URI("http://www.apple.com")));
    }

    @Test
    public void testStringStorage() throws Exception {
        String testString = "Hello, world";

        storage.put(myUri, testString.getBytes());
        String retrievedString = new String(storage.get(myUri));

        assertTrue(retrievedString != null);
        assertTrue(retrievedString.equals(testString));
    }
}
