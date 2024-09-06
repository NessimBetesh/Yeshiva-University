package edu.yu.cs.intro.ga;

public class Validators{
    public static final String A_SEARCH = "search";
    public static final String A_DIRECT = "direct";
    public static final String A_REFERRAL = "referral";

    public static boolean isValidAcquisition(String acquisition){
        if (acquisition == null){
			return false;
		}

		return (acquisition.equals("search") || acquisition.equals("direct") || acquisition.equals("referral"));

    }
    public static boolean isValidPath(String path){
        String allowedCharacters = ("^[a-zA-Z0-9\\-./]+$");
        if (path == null){
            return false;
        }
        if(!path.toLowerCase().endsWith(".html") && !path.toLowerCase().endsWith(".mp3") && !path.toLowerCase().endsWith(".m4a") && !path.toLowerCase().endsWith(".mp4")){
            return false;
        }
        if (!path.matches(allowedCharacters)){
            return false;
        }
        return true;
    }
}