package ch.zhaw.ficore.p2abc.filters;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class PrivacyRespFilter implements ContainerResponseFilter {
    public ContainerResponse filter(final ContainerRequest req,
            final ContainerResponse resp) {

        String accesstoken = req.getHeaderValue("X-P2ABC-ACCESSTOKEN");

        List<Object> ls = resp.getHttpHeaders().get(HttpHeaders.SET_COOKIE);
        if (ls == null) {
            ls = new ArrayList<Object>();
        }
        ls.add("x-p2abc-accesstoken=" + accesstoken);
        resp.getHttpHeaders().put(HttpHeaders.SET_COOKIE, ls);
        return resp;
    }
}
