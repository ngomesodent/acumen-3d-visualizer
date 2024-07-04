package lu.list.itis.ready.edge.scenarios;

import cesiumlanguagewriter.*;
import lu.list.itis.ready.edge.scenarios.trajectories.Trajectory;
import lu.list.itis.ready.edge.scenarios.trajectories.TrajectoryDataManager;
import lu.list.itis.ready.edge.utils.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

// Main class that generates the CZML file anf store it in the frontend's public directory
public class ScenarioGenerator {
    public static final Logger logger = LoggerFactory.getLogger(ScenarioGenerator.class);

    public static void main(String[] args) {
        try {
            TrajectoryDataManager trajectoryDataManager = new TrajectoryDataManager();
            Path trajectoryFilePath = Resources.getFilePath(Resources.getTrajectories());
            List<Trajectory> trajectories = trajectoryDataManager.buildTrajectoryDataBean(trajectoryFilePath, Trajectory.class);
            ScenarioWriter scenarioWriter = new ScenarioWriter();
            PacketCesiumWriter document = scenarioWriter.writeDocument(Resources.getFilename(Resources.getScenario()));
            String start = trajectoryDataManager.getStartDatetime(trajectories);
            String stop = trajectoryDataManager.getStopDatetime(trajectories);
            TimeInterval interval = trajectoryDataManager.toTimeInterval(start, stop);
            JulianDate currentTime = trajectoryDataManager.toJulianDate(start);
            double multiplier = 1.0;
            ClockRange range = ClockRange.LOOP_STOP;
            ClockStep step = ClockStep.TICK_DEPENDENT;
            scenarioWriter.writeClock(document, interval, currentTime, multiplier, range, step);
            Map<Long, List<JulianDate>> groupedDates = trajectoryDataManager.grouptoJulianDate(trajectories);
            Map<Long, List<Cartographic>> groupedCoordinates = trajectoryDataManager.groupToCartographic(trajectories);
            scenarioWriter.writeEntities(groupedDates, groupedCoordinates);
            scenarioWriter.closeAndExport(Resources.getScenario());
        } catch (IOException e) {
            logger.error("Failed or interrupted I/O operation", e);
        } catch (URISyntaxException e) {
            logger.error("Could not be parsed as a URI reference", e);
        }
    }
}