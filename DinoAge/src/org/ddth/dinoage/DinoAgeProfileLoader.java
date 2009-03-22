/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage;

import java.io.File;
import java.io.IOException;

import org.ddth.dinoage.grabber.yahoo.YahooProfile;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.ProfileLoader;

public class DinoAgeProfileLoader implements ProfileLoader {

	public Profile loadProfile(File profileFile) throws IOException {
		Profile profile = new YahooProfile();
		profile.load(profileFile);
		return profile;
	}
}
