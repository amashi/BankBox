package org.cto.VVS3Box.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cto.VVS3Box.AndroidClientAPIStub;

/**
 * Contains information about the current session. The session is initialized by
 * {@link AndroidClientAPI.Login}
 * 
 * @author lior
 * 
 */
public class VVSessionInfo {

	private final String identityKey;
	private final String sessionKey;
	private final Date startTime;
	private final Date expiresAt;

	public VVSessionInfo(String identityKey, String sessionKey, Date startTime,
			Date expiresAt) {
		super();
		this.identityKey = identityKey;
		this.sessionKey = sessionKey;
		this.startTime = startTime;
		this.expiresAt = expiresAt;
	}

	/**
	 * Returns the session Identity Key. The Identity Key is obtained from the
	 * Web Login process (after a successful login) and used to initialize the
	 * session with the V.V server
	 * 
	 * @return
	 */
	public String getIdentityKey() {
		return identityKey;
	}

	/**
	 * Returns the current session key with the V.V server. the Session is
	 * guaranteed to be valid for the current identity key.
	 * 
	 * @return
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Returns the date/time the session started at
	 * 
	 * @return
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Returns the date/time the session expires at. after this time the session
	 * should not be used and operations would fail. When a session expires the
	 * client should call {@link AndroidClientAPIStub.Login} again.
	 * 
	 * @return
	 */
	public Date getExpiresAt() {
		return expiresAt;
	}

	@Override
	public String toString() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return String
				.format("identity Key: %s\nsession key: %s\nstarted at: %s\nexpires at: %s",
						identityKey, sessionKey, dateFormat.format(startTime),
						dateFormat.format(expiresAt));
	}

}
