package lu.list.itis.ready.edge.scenarios;

import cesiumlanguagewriter.*;
import lu.list.itis.ready.edge.utils.Resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

// This class contains all the methods to write a complete CZML file
public class ScenarioWriter {
    private final StringWriter stringWriter;
    private final CesiumOutputStream cesiumOutputStream;
    private final CesiumStreamWriter cesiumStreamWriter;

    public ScenarioWriter() {
        this.stringWriter = new StringWriter();
        this.cesiumOutputStream = new CesiumOutputStream(this.stringWriter);
        this.cesiumOutputStream.setPrettyFormatting(true);
        this.cesiumOutputStream.writeStartSequence();
        this.cesiumStreamWriter = new CesiumStreamWriter();
    }

    public PacketCesiumWriter writeDocument(String name) {
        PacketCesiumWriter document = this.cesiumStreamWriter.openPacket(this.cesiumOutputStream);
        document.writeId("document");
        document.writeName(name);
        document.writeVersion("1.0");
        return document;
    }

    public void writeClock(
            PacketCesiumWriter document,
            TimeInterval interval,
            JulianDate currentTime,
            double multiplier,
            ClockRange range,
            ClockStep step
    ) {
        ClockCesiumWriter clock = document.openClockProperty();
        clock.writeInterval(interval);
        clock.writeCurrentTime(currentTime);
        clock.writeMultiplier(multiplier);
        clock.writeRange(range);
        clock.writeStep(step);
        clock.close();
        document.close();
    }

    public PacketCesiumWriter writeEntity(String id) {
        PacketCesiumWriter entity = this.cesiumStreamWriter.openPacket(this.cesiumOutputStream);
        entity.writeId(id);
        return entity;
    }

    public void writeModel(
            PacketCesiumWriter entity,
            CesiumResourceBehavior behavior,
            CesiumHeightReference heightReference
    ) {
        ModelCesiumWriter model = entity.openModelProperty();
        model.writeGltfProperty(Resources.getModel(), behavior);
        model.writeHeightReferenceProperty(heightReference);
        model.close();
    }

    public void writePosition(
            PacketCesiumWriter entity,
            CesiumInterpolationAlgorithm interpolationAlgorithm,
            CesiumExtrapolationType forwardExtrapolationType,
            List<JulianDate> dates,
            List<Cartographic> coordinates
    ) {
        PositionCesiumWriter position = entity.openPositionProperty();
        position.writeInterpolationAlgorithm(interpolationAlgorithm);
        position.writeForwardExtrapolationType(forwardExtrapolationType);
        position.writeCartographicDegrees(dates, coordinates);
        position.close();
        entity.close();
    }

    public void writeEntities(
            Map<Long, List<JulianDate>> groupedDates,
            Map<Long, List<Cartographic>> groupedCoordinates
    ) {
        CesiumResourceBehavior behavior = CesiumResourceBehavior.LINK_TO;
        CesiumHeightReference heightReference = CesiumHeightReference.CLAMP_TO_GROUND;
        CesiumInterpolationAlgorithm interpolationAlgorithm = CesiumInterpolationAlgorithm.LINEAR;
        CesiumExtrapolationType forwardExtrapolationType = CesiumExtrapolationType.HOLD;
        groupedDates.keySet().forEach(trackId -> {
            List<JulianDate> dates = groupedDates.get(trackId);
            List<Cartographic> coordinates = groupedCoordinates.get(trackId);
            PacketCesiumWriter entity = this.writeEntity("Vehicle_" + trackId);
            this.writeModel(entity, behavior, heightReference);
            this.writePosition(entity, interpolationAlgorithm, forwardExtrapolationType, dates, coordinates);
        });
    }

    public void closeAndExport(String destination) throws IOException {
        this.stringWriter.close();
        this.cesiumOutputStream.writeEndSequence();
        PrintWriter czmlFile = new PrintWriter(destination);
        czmlFile.println(this.stringWriter);
        czmlFile.close();
    }
}