package lu.list.itis.ready.edge.scenarios.trajectories;

import com.opencsv.bean.CsvBindByName;

public class PickupPosition {
    @CsvBindByName(column = "pickup_longitude")
    protected double longitude;
    @CsvBindByName(column = "pickup_latitude")
    protected double latitude;
    @CsvBindByName(column = "pickup_datetime")
    protected String timestamp;

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