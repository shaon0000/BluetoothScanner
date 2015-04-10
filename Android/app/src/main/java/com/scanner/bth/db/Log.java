package com.scanner.bth.db;

import java.util.UUID;

public class Log {

    public Long getTimeCreated() {
        return timeCreated;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public String getOwner() {
        return owner;
    }

    public Long getLastSynced() {
        return lastSynced;
    }

    public Long getLocationId() {
        return locationId;
    }

    public UUID getUuid() { return uuid; }


    private UUID uuid;
    private Long timeCreated;
    private Long lastUpdated;
    private String owner;

    public void setLastSynced(Long lastSynced) {
        this.lastSynced = lastSynced;
    }

    // Only from DB classes can we change this.
    void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    private Long lastSynced;
    private Boolean finished;


    private Long locationId;

    public Log(UUID uuid, Long timeCreated, Long last_updated, String owner, Long lastSynced, Boolean finished, Long locationId) {
        this.uuid = uuid;
        this.timeCreated = timeCreated;
        this.lastUpdated = last_updated;
        this.owner = owner;
        this.lastSynced = lastSynced;
        this.finished = finished;
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "Log{" +
                "uuid=" + uuid +
                ", timeCreated=" + timeCreated +
                ", lastUpdated=" + lastUpdated +
                ", owner='" + owner + '\'' +
                ", lastSynced=" + lastSynced +
                ", finished=" + finished +
                ", locationId=" + locationId +
                '}';
    }

    public Boolean getFinished() {
        return finished;
    }
}
