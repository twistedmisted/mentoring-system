package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }

//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        HttpServletRequest request = (HttpServletRequest) req;
//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Methods", "POST, PUT");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers",
//                "Date, Content-Type, Accept, X-Requested-With, Authorization, From, X-Auth-Token, Request-Id");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Content-Type", "application/json");
//        response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
////        response.addHeader("Content-Type", "charset=utf-8");
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(req, res);
//        }
//    }

}
