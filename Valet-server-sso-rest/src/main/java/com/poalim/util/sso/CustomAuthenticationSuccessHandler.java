package com.poalim.util.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	  @Override
	  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
	    return request.getServletPath();
	  }

	  @Override
	  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
	    request.getRequestDispatcher(request.getServletPath()).forward(request, response);
	  }
	}