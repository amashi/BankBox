package com.poalim.util.sso;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.util.NestedServletException;

public class CustomAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	private static final String SECURITY_TOKEN_KEY = "token";
	private static final String SECURITY_TOKEN_HEADER = "X-Token";
	private static final String FILTER_APPLIED = "BNHP_FILTER_APPLIED";
	private String token = null;

	
	
	protected CustomAuthenticationFilter() {
		super("/");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		this.token = request.getParameter(SECURITY_TOKEN_KEY);
		// or this.token = request.getHeader(SECURITY_TOKEN_HEADER);

		if (request.getAttribute(FILTER_APPLIED) != null) {
			chain.doFilter(request, response);
			return;
		}
		
		final boolean debug = logger.isDebugEnabled();

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

//		String actionParameter="action";
//		
//		for (Enumeration iterator = request.getParameterNames(); iterator.hasMoreElements();) {
//			String type = (String) iterator.nextElement();
//			System.out.println("param:"+type+request.getParameter(type));
//		}
//		
//		if (request.getParameter(actionParameter) != null
//				&& request.getParameter(actionParameter).equals("logout")) {
//			SecurityContextHolder.clearContext();
//			return;
//		}

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);
			return;
		}

		Authentication authResult;
		try {
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				return;
			}
		} catch (AuthenticationException failed) {
			unsuccessfulAuthentication(request, response, failed);
			return;
		}

		try {
			successfulAuthentication(request, response, chain, authResult);
		} catch (NestedServletException e) {
			if (e.getCause() instanceof AccessDeniedException) {
				unsuccessfulAuthentication(request, response,
						new LockedException("Forbidden"));
			}
		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {

		AbstractAuthenticationToken userAuthenticationToken = authUserByToken(this.token);
		if (userAuthenticationToken == null)
			throw new AuthenticationServiceException(MessageFormat.format(
					"Error | {0}", "Bad Token"));

		return userAuthenticationToken;
	}

	private AbstractAuthenticationToken authUserByToken(String tokenRaw) {
		AbstractAuthenticationToken authToken = null;
		try {
			
			
	       if (tokenRaw!=null)
	       {
		
			// check your input token, identify the user
			// if success create AbstractAuthenticationToken for user to return
			// eg:
//			Object username = "shdmitry";
//			Object userHash = null;
//			Collection<? extends GrantedAuthority> userAuthorities = null;
//			authToken = new UsernamePasswordAuthenticationToken(username,
//					userHash, userAuthorities);
			Object username = "shdmitry";
			Object password = "madi2000";
			authToken = new UsernamePasswordAuthenticationToken(username,password);
	       }

		} catch (Exception e) {
			logger.error("Error during authUserByToken", e);
		}
		return authToken;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
			throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult);

		getSuccessHandler().onAuthenticationSuccess(request, response,
				authResult);
	}

}