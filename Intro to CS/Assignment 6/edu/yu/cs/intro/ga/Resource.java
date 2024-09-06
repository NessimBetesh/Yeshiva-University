package edu.yu.cs.intro.ga;

public class Resource {
    private Event[] events = new Event[0];
    private String path;

    public Resource(String path){
        this.path = path;
        if(!Validators.isValidPath(path)){
            throw new IllegalArgumentException();
        }
    }
    public String getPath(){
        return path;
    }
    protected void addEvent(Event e){
        if(e == null || (!e.getPath().equals(this.getPath()))){
            throw new IllegalArgumentException();
            /**
            * @throws IllegalArgumentException if the event is null or if the event's path doesn't
            match this resources path
            * @param e the event to add to this resource
            */
            }
            //hace el array de events mas grande
            if (events.length == 0) {
                events = new Event[1];
            } 
            else {
                Event[] newArray = new Event[events.length + 1];
                for (int i = 0; i < events.length; i++) {
                    newArray[i] = events[i];
                }
                events = newArray;
            }
            // agrega event al events array
            events[events.length - 1] = e;
        }

    public Event[] getEventsInChronologicalOrder(){
       Event[] eventscopy = new Event[events.length];
        // copia los events a eventscopy
        System.arraycopy(events, 0, eventscopy, 0, events.length);
        return eventscopy;
       
        /**  Event[] eventscopy = new Event[events.length];
        for (int i = 0; i < events.length; i++) {
            eventscopy[i] = events[i];
        }
        return eventscopy;
        */
    }
    public int getTotalDuration(){
      
        int totalDuration = 0;
        for (Event event : events) {
            if (event != null) {
                totalDuration += event.getDuration();
            }
        }
        return totalDuration;
    }
    public double getTotalConversion(){
        double totalConversion = 0.0;
        for (Event event : events) {
            if (event != null) {
                totalConversion += event.getConversion();
            }
        }
        return totalConversion;
    }
    public int[] getTotalAcquisitionCounts(){
        
        int[] count = new int[3];
        int totalSearch = 0;
        int totalDirect = 0;
        int totalRefferal = 0;
        /**
        * @return an array of length 3, in which [0] holds the count of events acquired via
        search, [1] holds the total for direct, [2] holds the total for referral
        * @see Validators#A_SEARCH
        * @see Validators#A_DIRECT
        * @see Validators#A_REFERRAL
        */
        for(Event event : events){
            
            String acquisition = event.getAcquisition();

            if(acquisition.equals(Validators.A_SEARCH)){
                totalSearch++;
            }
            if(acquisition.equals(Validators.A_DIRECT)){
                totalDirect++;
            }
            if(acquisition.equals(Validators.A_REFERRAL)){
                totalRefferal++;
            }
        }
        count[0] = totalSearch;
        count[1] = totalDirect;
        count[2] = totalRefferal;
        return count;
    }
}