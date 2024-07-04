package lu.list.itis.ready.edge.scenarios.trajectories;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;

import java.util.ArrayList;
import java.util.List;

// A complete trajectory, containing the pickup. the dropoff, and all the intermediates mocked positions using OSM API
public class Trajectory {
    @CsvBindByName(column = "medallion")
    protected long id;
    @CsvRecurse
    protected PickupPosition pickupPosition;
    @CsvRecurse
    protected DropoffPosition dropoffPosition;
    protected List<IntermediatePosition> intermediatePositions = new ArrayList<>();

    public Trajectory() {
    }

    public Trajectory(long id, PickupPosition pickupPosition, DropoffPosition dropoffPosition) {
        this.id = id;
        this.pickupPosition = pickupPosition;
        this.dropoffPosition = dropoffPosition;
    }

    public void addIntermediatePositions(List<IntermediatePosition> intermediatePositions) {
        this.intermediatePositions.addAll(intermediatePositions);
    }

    public long getId() {
        return this.id;
    }

    public PickupPosition getPickupPosition() {
        return this.pickupPosition;
    }

    public DropoffPosition getDropoffPosition() {
        return this.dropoffPosition;
    }

    public List<IntermediatePosition> getIntermediatePositions() {
        return this.intermediatePositions;
    }
}