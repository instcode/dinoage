package org.ddth.http.core;

public class SessionChangeEvent {
	public static final int SESSION_START = 0;
	public static final int SESSION_END = 1;
	
	private Session session;
	private int type;
	
	public SessionChangeEvent(Session session, int type) {
		this.session = session;
		this.type = type;
	}
	
	public Session getSession() {
		return session;
	}
	
	public int getType() {
		return type;
	}
	
}
