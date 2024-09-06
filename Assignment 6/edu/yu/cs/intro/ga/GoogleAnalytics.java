package edu.yu.cs.intro.ga;

public class GoogleAnalytics{
    private Resource[] resources = new Resource[0];
    private String path;

    public Resource getResourceForPath(String path){
        /**
        * @param path the path whose resource is being requested
        * @return the resource for the given path, or null if there is none
        */
        for (Resource resource : resources){
            //revisa si el path es igual al resource
            if(resource !=  null && path.equals(resource.getPath())){
                return resource;
            }
        }
        return null;
    }
    public Resource[] getResourcesSortedByPath(){
        /**
        * @return All the resources, sorted (lowest to highest) by path. The Array which is
        returned must not contain any null elements.
        */
        int noNull = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNull++;
            }
        }
        Resource[] noNullResources = new Resource[noNull];
		int index = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNullResources[index++] = resource;
            }
        }

		int n = noNull;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
                if (noNullResources[j].getPath().compareTo(noNullResources[j + 1].getPath()) > 0) {
                    // Swap noNullResources[j] and noNullResources[j + 1]
                    Resource temp = noNullResources[j];
                    noNullResources[j] = noNullResources[j + 1];
                    noNullResources[j + 1] = temp;
                }
            }
        }
        return noNullResources;
    }
    public Resource[] getResourcesSortedByTotalDuration(){
        /**
        * @return All the resources, sorted (lowest to highest) by total duration per resource.
        The Array which is returned must not contain any null elements.
        */
        int noNull = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNull++;
            }
        }
        Resource[] noNullResources = new Resource[noNull];
		int index = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNullResources[index++] = resource;
            }
        }

		int n = noNull;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
                if (noNullResources[j].getTotalDuration() > noNullResources[j + 1].getTotalDuration()) {
                    // Swap
                    Resource temp = noNullResources[j];
                    noNullResources[j] = noNullResources[j + 1];
                    noNullResources[j + 1] = temp;
                }
            }
        }
        return noNullResources;
    }
    public Resource[] getResourcesSortedByTotalConversion(){
        /**
        * @return All the resources, sorted (lowest to highest) by total conversion value per
        resource. The Array which is returned must not contain any null elements.
        */
        int noNull = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNull++;
            }
        }
        Resource[] noNullResources = new Resource[noNull];
		int index = 0;
        for (Resource resource : resources) {
            if (resource != null) {
                noNullResources[index++] = resource;
            }
        }

		int n = noNull;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++){
                if (noNullResources[j].getTotalConversion() > noNullResources[j + 1].getTotalConversion()) {
                    // Swap
                    Resource temp = noNullResources[j];
                    noNullResources[j] = noNullResources[j + 1];
                    noNullResources[j + 1] = temp;
                }
            }
        }
        return noNullResources;
    }
    public int[] getAcquisitionTotals(){
        int totalSearch = 0;
        int totalDirect = 0;
        int totalReferral = 0;

        for (Resource resource : resources) {
            if (resource != null) {
                int[] counts = resource.getTotalAcquisitionCounts();
                totalSearch += counts[0];
                totalDirect += counts[1];
                totalReferral += counts[2];
            }
        }
        int[] acquisitionTotals = {totalSearch, totalDirect, totalReferral};
        return acquisitionTotals;
    }
    public void addEvent(String path, int duration, double conversion, String acquisition){
        addEvent(new Event(path, duration, conversion, acquisition));
    }
    public void addEvent(Event e){
        if (e == null){
            throw new IllegalArgumentException();
        }
        if (resources.length == 0) {
            resources = new Resource[1];
        } 
        /*loop to run through all the resources
            * if it is not null and this path equals to the resource's path then put the event in resource
            * if they are not the same then addEvent similarly as we did before
        */
        for (Resource resource : resources){
            //revisa si el path es igual al resource
            if(resource !=  null && e.getPath().equals(resource.getPath())){
                //mete el event de e en resource, y terminamos
                resource.addEvent(e);
                return;
            }
        }
        
        //Passed through every resource and found zero matches
        //hay q crear un resource nuevo
        // es de resources.length + 1 pq quieres poner el path nuevo en resources tonces tiene q ser mas grande para que quepa
        Resource[] newArray = new Resource[resources.length + 1];
        //copio lo viejo nuevo
        for (int i = 0; i < resources.length; i++) {
            newArray[i] = resources[i];
        }
        //creo otro object de resource y le meto el path q no era igual(e)
        Resource resource = new Resource(e.getPath());
        //aqui le meto event(e) en resource 
        resource.addEvent(e);
        //aqui mete el resource con el path que no era igual en el array nuevo
        newArray[resources.length] = resource;
        // y aqui lo revierte y pone el array nuevo q tiene resource viejo en resources y esta todo completo
        resources = newArray;

        }
}