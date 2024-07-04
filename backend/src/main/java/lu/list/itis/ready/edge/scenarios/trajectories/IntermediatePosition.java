package lu.list.itis.ready.edge.scenarios.trajectories;

public class IntermediatePosition {
    protected double longitude;
    protected double latitude;
    protected String timestamp;

    public IntermediatePosition(double longitude, double latitude, String timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}