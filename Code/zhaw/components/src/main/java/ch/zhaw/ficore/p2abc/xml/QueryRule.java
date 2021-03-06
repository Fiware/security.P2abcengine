package ch.zhaw.ficore.p2abc.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** The XML for query rules.
 *
 * @author Roman M&uuml;ntener &lt;roman.muentener@zhaw.ch&gt;
 *
 */
@XmlRootElement(name = "query-rule", namespace = "http://abc4trust.eu/wp2/abcschemav1.0")
public class QueryRule implements Serializable {
    private static final long serialVersionUID = 9154661099255513606L;

    @XmlElement(name = "query-string", required = true, namespace = "http://abc4trust.eu/wp2/abcschemav1.0")
    public String queryString;

    public QueryRule() {
    }

    public QueryRule(final String queryString) {
        this.queryString = queryString;
    }
}
