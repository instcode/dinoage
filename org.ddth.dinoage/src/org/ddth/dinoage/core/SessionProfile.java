package org.ddth.dinoage.core;

import java.util.Properties;


public abstract class SessionProfile extends Profile {

	@Override
	protected void store(Properties properties) {
	}
	
	@Override
	protected void load(Properties properties) {
	}
	
	public void saveURL(String url) {
	}
	
	public void load() {
	}
	
	public boolean isNewlyCreated() {
		return false;
	}
	
	public String getBeginningURL() {
		return null;
	}
}
