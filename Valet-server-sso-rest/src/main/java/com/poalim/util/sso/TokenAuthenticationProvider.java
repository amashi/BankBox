package com.poalim.util.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired private UserValidationService userSvc;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.isAuthenticated())
            return auth;

        String token = auth.getCredentials().toString();
        User user = userSvc.validateApiAuthenticationToken(token);
        if (user != null) {
            auth = new PreAuthenticatedAuthenticationToken(user, token);
            auth.setAuthenticated(true);
//            logger.debug("Token authentication. Token: " + token + "; user: " + user.getDisplayName());
        } else
            throw new BadCredentialsException("Invalid token " + token);
        return auth;
    }

	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	static class UserValidationService
	{

		public User validateApiAuthenticationToken(String token) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}