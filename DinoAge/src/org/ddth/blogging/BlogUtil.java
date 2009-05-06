/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import com.ibm.icu.text.Normalizer;

public class BlogUtil {
	
	public static String normalize(String text) {
		String dD = "\u0110\u0111";
		String normalizedText = text.replace(dD.charAt(1), 'D').replace(dD.charAt(0), 'd');
		normalizedText = Normalizer.normalize(normalizedText, Normalizer.NFD);
		normalizedText = normalizedText.replaceAll("[\u0100-\uffff]+", "");
		normalizedText = normalizedText.replaceAll("\\W+", "-").replaceAll("^_+", "").replaceAll("_+$", "").replaceAll("_+", "-");
		return normalizedText;
	}
}
