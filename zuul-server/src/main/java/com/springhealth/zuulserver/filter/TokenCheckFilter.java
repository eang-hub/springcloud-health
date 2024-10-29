package com.springhealth.zuulserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
@Component
@Order(1)
public class TokenCheckFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";  // 前置过滤器
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("Authorization");

        if (token != null) {
            System.out.println("Token found in request header: " + token);
        } else {
            System.out.println("No token found in request header.");
        }

        return null;
    }
}
