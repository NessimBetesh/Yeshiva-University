public class GoogleAnalytics{
	public static void main(String[] args){
		boolean errors = false;
		String currentPath = "";

		for (String event : args) {
        if (!isValidEvent(event)) {
            errors = true;
        }
    }
	if (errors)
	return;

	String[] sortedEvents = getSortedEvents(args);

		for (String event : sortedEvents) {
  
            String path = event.split(":")[0];

            if (!path.equals(currentPath)) {
                // si no es el primer path imprine otra inea
                if (!currentPath.isEmpty()) {
                    System.out.println();
                }
                // cambia el current path
                currentPath = path;
            }
            // Print the event
            System.out.println(event);
        }
   // }
}
	public static boolean isValidEvent(String event){
		if (event == null){
			System.out.println("null event");
			return false;
		}
			String[] components = event.split(":");
			if (components.length != 4) {
				System.out.println("Event is missing at least one component: " + event);
				return false;
			}
	  		if (!isValidPath(components[0])){
	  			return false;
	  		}
			if (!isValidTimeOnPage(components[1])){
	  			return false;
	  		}
	  		if (!isValidConversionValue(components[2])){
	  			return false;
	  		}
	  		if (!isValidAcquisition(components[3])){
	  			return false;
	  		}
	  		return true;
		}
	public static boolean isValidAcquisition(String acquisition){
		if (acquisition == null){
			System.out.println("null acquisition");
			return false;
		}
		if (acquisition.equals("search") || acquisition.equals("direct") || acquisition.equals("referral"))
			return true;
		else{
			System.out.println("Acquisition must be one of search, direct, or referral. Invalid acquisition: " + acquisition);
			return false;
		}
	}
	public static boolean isValidConversionValue(String value){
		if (value == null){
			System.out.println("The conversion value must be a non-negative dollar amount. Invalid conversion value: " + value);
			return false;
		}
		if (getValidDollarAmount(value) == -1){
			System.out.println("The conversion value must be a non-negative dollar amount. Invalid conversion value: " + value);
			return false;
		}
		return true;
	}
	public static boolean isValidTimeOnPage(String time){
		if (time == null){
			System.out.println("Invalid time on page: " + time);
			return false;
		}
		try{
			int newTime = Integer.parseInt(time);
			if (newTime < 0){
				throw new NumberFormatException();
			}
			return true;
		}
		catch (NumberFormatException e){
			System.out.println("Invalid time on page: " + time);
			return false;
		}
	}
	public static boolean isValidPath(String path){
		String allowedCharacters = ("^[a-zA-Z0-9\\-./]+$");
		if (path == null){
			System.out.println("null path");
			return false;
		}
		if(!path.endsWith(".html")){
			System.out.println("Paths must end with \".html\". Invalid path: " + path);
			return false;
		}
		if (!path.matches(allowedCharacters)){
			System.out.println("Paths may only be made of letters, numbers, dashes, periods, and slashes. Invalid path: " + path);
			return false;
		}
		return true;
		}
	
	public static int toPositiveInt(String num){
		try{
			int numInt = Integer.parseInt(num);
			if (numInt < 0){
				return -1;
			}
			return numInt;
			}
		catch
			(NumberFormatException e){
			return -1;
			}
		}
		
	public static double getValidDollarAmount(String dollars){
		double dollarsDouble = -1;
		    	if (dollars == null || !dollars.startsWith("$")){
		    	return -1;
		    }
    		try {
        		if (dollars.startsWith("$")) {
            	dollars = dollars.substring(1);
        	}
        		dollarsDouble = Double.parseDouble(dollars);

        		if (dollarsDouble < 0.0) {
            		return -1;
        			}

        				String[] parts = dollars.split("\\.");

        				if (parts.length == 2){
        					if (Integer.parseInt(parts[1]) > 99){

        						return -1;
        					}
        				}
        					return dollarsDouble;
    						} catch (NumberFormatException e) {
        						return -1;
    						}
    					}
	public static String[] getSortedEvents(String[] events){
		String[] sortedEvents = new String[events.length];
		for (int i = 0; i < events.length; i++){
			sortedEvents[i] = events[i];
		}
		int n = sortedEvents.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
			String path1 = sortedEvents[j].split(":")[0];
            String path2 = sortedEvents[j + 1].split(":")[0];

            if (path1.compareTo(path2) > 0) {
                // Swap events
                String temp = sortedEvents[j];
                sortedEvents[j] = sortedEvents[j + 1];
                sortedEvents[j + 1] = temp;
            }
        }
    }

    return sortedEvents;
}
}