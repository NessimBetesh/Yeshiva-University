public class test2{
	public static void main(String[] args){
		boolean errors = false;
		String currentPath = "";
		boolean empieza = true;
		if (args.length == 0){
        return;
    }
		for (String event : args){
			if (empieza){
				empieza = false;
				continue;
			}
			if (!isValidEvent(event)) {
				errors = true;
			}
			if(event.isEmpty()){
				return;
			}
		}
			if (errors){
				return;
			}
		if(args[0].equals("-ttpp") || (args[0].equals("-dppp"))|| (args[0].equals("-dppd")) || (args[0].equals("-tp"))){	
			addition(args);
			}
			else{
				String[] sortedEvents = getSortedEvents(args);
				String[] array2 = new String[sortedEvents.length - 1];
					for (String event : array2) {
						if (!isValidEvent(event)) {
							errors = true;
						}
					}  
				    for (String event : args) {
				    if (!isValidEvent(event)) {
					  errors = true;
				  }
			  }
			  if (errors)
			  return;
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
			  }
		  }

		  private static void addition(String[] args) {
			
			if (args[0].equals("-ttpp")) {
				// Extract a subarray excluding the first element
				String[] copies = new String[args.length - 1];
				System.arraycopy(args, 1, copies, 0, args.length - 1);
				
				String[] sortedEvents = getSortedEvents(copies);
				String[] components = sortedEvents[0].split(":");
				String previousGroup = components[0];
				int previousTime = Integer.parseInt(components[1]);
	
				for (int i = 1; i < sortedEvents.length; i++) {
					String event = sortedEvents[i];
					components = event.split(":");
					if (components.length < 4) {
						continue; // Skip invalid events
					}
					int time = Integer.parseInt(components[1]);
					String path = components[0];
	
					if (!path.equals(previousGroup)) {
						// New path begins, reset the temporary variable and total time counter
						System.out.println("PATH: " + previousGroup + "\t" + "TIME: " + previousTime);
						previousGroup = path;
						previousTime = time;
					} else {
						previousTime += time;
					}
				}
				System.out.println("PATH: " + previousGroup + "\t" + "TIME: " + previousTime);
			}
			if (args[0].equals("-dppp")) {
				// Extract a subarray excluding the first element
				String[] copies = new String[args.length - 1];
				System.arraycopy(args, 1, copies, 0, args.length - 1);
				
				String[] sortedEvents = getSortedEvents(copies);
				String[] components = sortedEvents[0].split(":");
				String previousGroup = components[0];
				double totalDollars = 0.0;

				for (int i = 0; i < sortedEvents.length; i++) {
					String event = sortedEvents[i];
					components = event.split(":");
					if (components.length < 4) {
						continue; // Skip invalid events
					}
					double dollars = Double.parseDouble(components[2].substring(1));
					String path = components[0];
	
					if (i == 0 || !path.equals(sortedEvents[i - 1].split(":")[0])) {
						// New path begins, reset the total dollars counter
						if (i != 0) {
							System.out.println("PATH: " + previousGroup + "\t" + "DOLLARS: $" + totalDollars);
						}
						previousGroup = path;
						totalDollars = dollars;
					} else {
						totalDollars += dollars;
					}
				}			
				System.out.println("PATH: " + previousGroup + "\t" + "DOLLARS: $" + totalDollars);
			}
			if (args[0].equals("-dppd")) {
				String[] paths = new String[args.length - 1];
				double[] dollars = new double[args.length - 1];
				int count = 0;
	
				for (int i = 1; i < args.length; i++) {
					String[] parts = args[i].split(":");
					String path = parts[0];
					double dollarValue = Double.parseDouble(parts[2].substring(1)); // quita dollar sign
	
					// check si el path ya existe
					int index = -1;
					for (int j = 0; j < count; j++) {
						if (paths[j].equals(path)) {
							index = j;
							break;
						}
					}

					if (index == -1) { // New path
						paths[count] = path;
						dollars[count] = dollarValue;
						count++;
					} else { // Existing path
						dollars[index] += dollarValue;
					}
				}

				//Bubble Sort
				for (int i = 0; i < count - 1; i++) {
					for (int j = 0; j < count - i - 1; j++) {
						if (dollars[j] > dollars[j + 1]) {
							//Swap dollars
							double tempDollar = dollars[j];
							dollars[j] = dollars[j + 1];
							dollars[j + 1] = tempDollar;
	
							//Swap paths
							String tempPath = paths[j];
							paths[j] = paths[j + 1];
							paths[j + 1] = tempPath;
						}
					}
				}

				for (int i = 0; i < count; i++) {
					System.out.println("PATH: " + paths[i] + "\t" + "DOLLARS: $" + dollars[i]);
				}
			}
			if (args[0].equals("-tp")){
				int totalDirect = 0;
				int totalRefferal = 0;
				int totalSearch = 0;
				String[] copies = new String[args.length - 1];
				System.arraycopy(args, 1, copies, 0, args.length - 1);

				String[] sortedEvents = getSortedEvents(copies);
				for (String event : sortedEvents) {
				String[] components = event.split(":");
				String buy = components[3];
				switch(buy){
					case "direct": 
						totalDirect++;
						break;
					case "referral": 
						totalRefferal ++;
						break;
					case "search": 
						totalSearch++;
						break;
				}
			}
			System.out.println("direct: " + totalDirect);
			System.out.println("referral: " + totalRefferal);
			System.out.println("search: " + totalSearch);
		}
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
	/*private static String[] getPlataSorted(String[] previousGroup, double[] totalDollars){
		Double[] plataSorted = new Double[totalDollars.length];
		for (int i = 0; i < totalDollars.length; i++){
			plataSorted[i] = totalDollars[i];
		}
		String[] sortedEvents = new String[previousGroup.length];

		int n = plataSorted.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
			double plata1 = plataSorted[j].split(":")[2];
            double plata2 = plataSorted[j + 1].split(":")[2];
            String group1 = sortedEvents[j].split(":")[2];
            String group1 = sortedEvents[j + 1].split(":")[2];

            if (plata1 < plata2) {
                // Swap events
                String temp = plataSorted[j];
                plataSorted[j] = plataSorted[j + 1];
                plataSorted[j + 1] = temp;

                
                String temp1 = sortedEvents[j];
                sortedEvents[j] = sortedEvents[j + 1];
                sortedEvents[j + 1] = temp1;
            }
        }
    }
    return plataSorted;
}*/
	private static String[] getPlataSorted (String [] plata){
		String[] sortedPlata = new String[plata.length];
		for (int i = 0; i < plata.length; i++){
			sortedPlata[i] = plata[i];
		}
		int n = sortedPlata.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
				try {
					Double path1 = Double.parseDouble (sortedPlata[j].split(":")[2]);
					Double path2 = Double.parseDouble (sortedPlata[j + 1].split(":")[2]);

					if (path1 < path2) {
						// Swap events
						String temp = sortedPlata[j];
						sortedPlata[j] = sortedPlata[j + 1];
						sortedPlata[j + 1] = temp;
					}
				} catch (NumberFormatException e) {
					
				}
	        }
	    }
	    return sortedPlata;
	}
}