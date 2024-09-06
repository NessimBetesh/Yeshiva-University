package edu.yu.cs.intro.ga;

public class tester{
    public static void main(String args[]) {
        // Create a Resource instance
        GoogleAnalytics eventsName = new GoogleAnalytics();

        // Create Event instances (replace these with actual values)
        Event event1 = new Event("bananas3.HTmL", 3, 4.5, "search");
        Event event2 = new Event("bananas3.html", 4, 5.5, "direct");
        Event event3 = new Event("bananas7.html", 1, 5.4, "direct");
        Event event4 = new Event("bananas4.html", 7, 10, "direct");
        Event event5 = new Event("bananas3.html", 3, 3.5, "search");
        Event event6 = new Event("bananas7.html", 4, 7.5, "direct");
        Event event7 = new Event("bananas7.html", 1, 5.6, "direct");
        Event event8 = new Event("bananas4.html", 7, 10, "direct");
        // Add the events to the Resource
        eventsName.addEvent(event1);
        eventsName.addEvent(event2);
        eventsName.addEvent(event3);
        eventsName.addEvent(event4);
        eventsName.addEvent(event5);
        eventsName.addEvent(event6);
        eventsName.addEvent(event7);
        eventsName.addEvent(event8);

    //    System.out.println(eventsName.getAcquisitionTotals());

        for (Resource i : eventsName.getAcquisitionTotals()){
            System.out.println("Printing resource data:\n");
            for(Event event : i.getEventsInChronologicalOrder()){

                System.out.printf("Event: path: %s, duration = %s, conversion=%s, acquisition=%s\n", 
                event.getPath(), event.getDuration(), event.getConversion(), event.getAcquisition());
            }
            System.out.println("\n done with this resource\n");
        }

        /* 
        // Print the added events
        System.out.println("Added Events:");

        Resource[] sortedbyconversion = eventsName.getResourcesSortedByTotalConversion();
        for (Event event : addEvent) {
            if (event != null) {
                System.out.println("Event: " + event.toString());
            }
        }
        */
    }
}