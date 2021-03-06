package ch.zhaw.ficore.p2abc.services.issuance;

import javax.naming.NamingException;

import ch.zhaw.ficore.p2abc.configuration.IssuanceConfiguration;
import ch.zhaw.ficore.p2abc.xml.AuthenticationInformation;

/**
 * Serves as a Factory for AuthenticationProviders.
 * 
 * An AuthenticationProvider provides the Authentifaction for an identity
 * source. Services may use such providers to authenticate users for
 * initializing an issuance protocol.
 * 
 * @author mroman
 */
public abstract class AuthenticationProvider {

    public AuthenticationProvider(final IssuanceConfiguration configuration) {
    }

    /**
     * Factory method to construct an AuthenticationProvider for a given
     * ServiceConfiguration. The AuthenticationProvider will receive a copy of
     * the ServiceConfiguration.
     * 
     * @param configuration
     *            a ServiceConfiguration
     * @return an implementation of an AuthenticationProvider
     */
    public static AuthenticationProvider getAuthenticationProvider(
            final IssuanceConfiguration configuration) {
        switch (configuration.getAuthenticationSource()) {
        case FAKE:
            return new FakeAuthenticationProvider(configuration);
        case LDAP:
            return new LdapAuthenticationProvider(configuration);
        case JDBC:
            return new JdbcAuthenticationProvider(configuration);
        case KEYROCK:
            return new KeyrockAuthenticationProvider(configuration);
        default:
            throw new RuntimeException(configuration.getAttributeSource()
                    + " is not a supported AuthenticationProvider!");
        }
    }

    /**
     * This method allows to verify the AuthenticationInformation provided by a
     * user.
     * 
     * @param authInfo
     *            AuthenticationInformation as given by a user.
     * @return true if AuthenticationInformation is correct (e.g. username,
     *         password is correct)
     * @throws NamingException
     */
    public abstract boolean authenticate(AuthenticationInformation authInfo)
            throws NamingException;

    /**
     * Called when this AuthenticationProvider is no longer required. Providers
     * should close open sockets/connections/files etc. on shutdown.
     */
    public abstract void shutdown();

    /**
     * Returns a sequence of characters that uniquely identifies a user (i.e. a
     * username). This UserID may be used in queries given to
     * AttributeValueProviders. Note: AttributeValueProviders may impose
     * restrictions on UserIDs.
     * 
     * @throws IllegalStateException
     *             if called before or after an unsuccessful authenticate.
     * @return UserID
     */
    public abstract String getUserID() throws IllegalStateException;
}
