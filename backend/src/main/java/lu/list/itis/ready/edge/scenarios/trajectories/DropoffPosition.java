package lu.list.itis.ready.edge.scenarios.trajectories;

import com.opencsv.bean.CsvBindByName;

public class DropoffPosition {
    @CsvBindByName(column = "dropoff_longitude")
    protected double longitude;
    @CsvBindByName(column = "dropoff_latitude")
    protected double latitude;
    @CsvBindByName(column = "dropoff_datetime")
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