package lu.list.itis.ready.edge.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "resource")
public class Resources {
    private static String model;
    private static String trajectories;
    private static String scenario;

    public static String getModel() {
        return model;
    }

    public static String getTrajectories() {
        return trajectories;
    }

    public static String getScenario() {
        return scenario;
    }

    public void setModel(String model) {
        Resources.model = model;
    }

    public void setTrajectories(String trajectories) {
        Resources.trajectories = trajectories;
    }

    public void setScenario(String scenario) {
        Resources.scenario = scenario;
    }

    public static Path getFilePath(String uri) throws URISyntaxException {
        return Paths.get(
                ClassLoader.getSystemResource(uri).toURI()
        );
    }

    public static String getFilename(String uri) {
        String[] slashSplitURI = uri.split("/");
        String filenameWithExtension = slashSplitURI[slashSplitURI.length - 1];
        return filenameWithExtension.split("\\.")[0];
    }
}