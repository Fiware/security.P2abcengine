package ch.zhaw.ficore.p2abc.services.issuance;

public class QueryHelper {

    private QueryHelper() {

    }

    /**
     * This method substitutes "_UID_" with the user's actual uid
     * 
     * @param query
     *            Query
     * @param uid
     *            User's uid
     * @return resulting query
     */
    public static String buildQuery(final String query, final String uid) {
        /*
         * TODO: Provide some mechanism to actually allow someone to use "_UID_"
         * in the query without being replaced by uid.
         */
        return query.replaceAll("_UID_", uid);
    }

    /**
     * Sanitizes input for usage in LDAP-Queries
     * 
     * @param input
     *            Input string
     * @return sanitized input
     */
    public static String ldapSanitize(final String input) {
        // TODO: Read the ldap RFC to correctly sanitize strings....
        return input.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * Sanitizes input for usage in SQL-Queries
     * 
     * @param input
     *            Input string
     * @return sanitized input
     */
    public static String sqlSanitize(final String input) {
        // TODO: Read some RFC or google how to properly sanitize this....
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }
}
