package pl.edu.uj.dusinski.dao;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class DirectionRefreshDetails {

    @Id
    private final long id;
    private final LocalDateTime updatingTime;
    private final int updatedDirectionsNumber;

    public DirectionRefreshDetails(long id, LocalDateTime updatingTime, int updatedDirectionsNumber) {
        this.id = id;
        this.updatingTime = updatingTime;
        this.updatedDirectionsNumber = updatedDirectionsNumber;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getUpdatingTime() {
        return updatingTime;
    }

    public int getUpdatedDirectionsNumber() {
        return updatedDirectionsNumber;
    }
}
