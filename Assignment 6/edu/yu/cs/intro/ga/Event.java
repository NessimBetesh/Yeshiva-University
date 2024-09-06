package edu.yu.cs.intro.ga;

public class Event {
    private String path;
    private int duration;
    private double conversion;
    private String acquisition;

    public Event(String path, int duration, double conversion, String acquisition) {
        if (!Validators.isValidPath(path) || !Validators.isValidAcquisition(acquisition) || duration < 0 || conversion < 0) {
            throw new IllegalArgumentException();
        }
        this.path = path;
        this.duration = duration;
        this.conversion = conversion;
        this.acquisition = acquisition;
    }

    public String getPath(){
        return this.path;
    }
    public String getAcquisition(){
        return this.acquisition;
    }
    public int getDuration(){
        return this.duration;
    }
    public double getConversion(){
        return this.conversion;
    }
}
