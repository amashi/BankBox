package com.poalim.util.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationTokenFilter implements Filter {


    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            // do nothing
        } else {
            Map<String,String[]> params = req.getParameterMap();
            if (!params.isEmpty() && params.containsKey("auth_token")) {
                String token = params.get("auth_token")[0];
                if (token != null) {
                    Authentication auth = new TokenAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        fc.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }

    class TokenAuthentication implements Authentication {
        private String token;
        private TokenAuthentication(String token) {
            this.token = token;
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return new ArrayList<GrantedAuthority>(0);
        }
        @Override
        public Object getCredentials() {
            return token;
        }
        @Override
        public Object getDetails() {
            return null;
        }
        @Override
        public Object getPrincipal() {
            return null;
        }
        @Override
        public boolean isAuthenticated() {
            return false;
        }
        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }
        @Override
        public String getName() {
			return token;
            // your custom logic here
        }
    }

 }