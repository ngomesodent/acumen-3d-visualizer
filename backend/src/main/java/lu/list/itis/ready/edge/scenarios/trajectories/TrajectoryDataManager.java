package lu.list.itis.ready.edge.scenarios.trajectories;

import cesiumlanguagewriter.Cartographic;
import cesiumlanguagewriter.JulianDate;
import cesiumlanguagewriter.TimeInterval;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import me.tongfei.progressbar.ProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrajectoryDataManager {
    public static final Logger logger = LoggerFactory.getLogger(TrajectoryDataManager.class);
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    // Instanciates all the Trajectory objects based on the CSV file content
    public List<Trajectory> buildTrajectoryDataBean(
            Path path,
            Class<Trajectory> clazz
    ) throws IOException {
        Reader reader = Files.newBufferedReader(path);
        CsvToBean<Trajectory> csvToBean = new CsvToBeanBuilder<Trajectory>(reader)
                .withType(clazz)
                .build();
        List<Trajectory> trajectories = csvToBean.parse();
        for (Trajectory trajectory : ProgressBar.wrap(trajectories, "Adding intermediate positions")) {
            try {
                trajectory.addIntermediatePositions(this.getIntermediatePositions(trajectory));
            } catch (IOException e) {
                logger.error("Failed or interrupted I/O operation", e);
            } catch (URISyntaxException e) {
                logger.error("Could not be parsed as a URI reference", e);
            }
        }
        return trajectories;
    }

    /* Creates all the IntermediatePosition objects with the response of the OSM API
     * and calculates an average interval to mock a timestamp for each position given */
    public List<IntermediatePosition> getIntermediatePositions(
            Trajectory trajectory
    ) throws URISyntaxException, IOException {
        JSONArray route = this.getRoute(
                trajectory.getPickupPosition().getLongitude(),
                trajectory.getPickupPosition().getLatitude(),
                trajectory.getDropoffPosition().getLongitude(),
                trajectory.getDropoffPosition().getLatitude()
        );
        List<IntermediatePosition> result = new ArrayList<>();
        String pickupDatetime = trajectory.getPickupPosition().getTimestamp();
        String dropoffDatetime = trajectory.getDropoffPosition().getTimestamp();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
        LocalDateTime pickupLocalDatetime = LocalDateTime.parse(pickupDatetime, formatter);
        LocalDateTime dropoffLocalDatetime = LocalDateTime.parse(dropoffDatetime, formatter);
        long totalMilliseconds = ChronoUnit.MILLIS.between(pickupLocalDatetime, dropoffLocalDatetime);
        long interval = totalMilliseconds / route.length();
        for (int i = 0; i < route.length(); i++) {
            double longitude = route.getJSONArray(i).getDouble(0);
            double latitude = route.getJSONArray(i).getDouble(1);
            String timestamp = pickupLocalDatetime.plus(interval * i, ChronoUnit.MILLIS).format(formatter);
            result.add(new IntermediatePosition(longitude, latitude, timestamp));
        }
        return result;
    }

    public Map<Long, List<Cartographic>> groupToCartographic(List<Trajectory> trajectories) {
        Map<Long, List<Cartographic>> groupedCoordinates = new HashMap<>();
        trajectories.forEach(trajectory -> {
            List<Cartographic> coordinates = new ArrayList<>();
            double pickupPositionLongitude = trajectory.getPickupPosition().getLongitude();
            double pickupPositionLatitude = trajectory.getPickupPosition().getLatitude();
            double height = 0.0;
            coordinates.add(new Cartographic(pickupPositionLongitude, pickupPositionLatitude, height));
            trajectory.getIntermediatePositions().forEach(intermediatePosition -> {
                double intermediatePositionLongitude = intermediatePosition.getLongitude();
                double intermediatePositionLatitude = intermediatePosition.getLatitude();
                coordinates.add(new Cartographic(intermediatePositionLongitude, intermediatePositionLatitude, height));
            });
            double dropoffPositionLongitude = trajectory.getDropoffPosition().getLongitude();
            double dropoffPositionLatitude = trajectory.getDropoffPosition().getLatitude();
            coordinates.add(new Cartographic(dropoffPositionLongitude, dropoffPositionLatitude, height));
            groupedCoordinates.put(trajectory.getId(), coordinates);
        });
        return groupedCoordinates;
    }

    public Map<Long, List<JulianDate>> grouptoJulianDate(List<Trajectory> trajectories) {
        Map<Long, List<JulianDate>> groupedDates = new HashMap<>();
        trajectories.forEach(trajectory -> {
            List<JulianDate> dates = new ArrayList<>();
            dates.add(this.toJulianDate(trajectory.getPickupPosition().getTimestamp()));
            trajectory.getIntermediatePositions().forEach(intermediatePosition -> dates.add(this.toJulianDate(intermediatePosition.getTimestamp())));
            dates.add(this.toJulianDate(trajectory.getDropoffPosition().getTimestamp()));
            groupedDates.put(trajectory.getId(), dates);
        });
        return groupedDates;
    }

    public JulianDate toJulianDate(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return new JulianDate(zonedDateTime);
    }

    public TimeInterval toTimeInterval(String start, String stop) {
        JulianDate julianStart = this.toJulianDate(start);
        JulianDate julianStop = this.toJulianDate(stop);
        return new TimeInterval(julianStart, julianStop);
    }

    // Sorts all timestamps to get the earliest one which will be the start time of the simulation
    public String getStartDatetime(List<Trajectory> trajectories) {
        Timestamp start = new Timestamp(Long.MAX_VALUE);
        for (Trajectory trajectory : trajectories) {
            Timestamp current = Timestamp.valueOf(trajectory.getPickupPosition().getTimestamp());
            if (current.before(start)) {
                start = current;
            }
        }
        return new SimpleDateFormat(TIMESTAMP_PATTERN).format(start);
    }

    // Sorts all timestamps to get the latest one which will be the stop time of the simulation
    public String getStopDatetime(List<Trajectory> trajectories) {
        Timestamp stop = new Timestamp(0);
        for (Trajectory trajectory : trajectories) {
            Timestamp current = Timestamp.valueOf(trajectory.getDropoffPosition().getTimestamp());
            if (current.after(stop)) {
                stop = current;
            }
        }
        return new SimpleDateFormat(TIMESTAMP_PATTERN).format(stop);
    }

    // Calls the OSM API and returns a set of coordinates to go from pickup to dropoff
    private JSONArray getRoute(
            double pickupLongitude,
            double pickupLatitude,
            double dropoffLongitude,
            double dropoffLatitude
    ) throws URISyntaxException, IOException {
        URL url = new URI(
                "https://router.project-osrm.org/route/v1/car/"
                        + pickupLongitude
                        + ","
                        + pickupLatitude
                        + ";"
                        + dropoffLongitude
                        + ","
                        + dropoffLatitude
                        + "?geometries=geojson&overview=full"
        ).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        JSONObject jsonContent = new JSONObject(content.toString());
        return jsonContent
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates");
    }
}