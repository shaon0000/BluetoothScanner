package com.scanner.bth.db;

public class Log {

    public Integer getId() {
        return id;
    }

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

    private Integer id;
    private Long timeCreated;
    private Long lastUpdated;
    private String owner;
    private Long lastSynced;
    private Boolean finished;

    public Log(Integer id, Long timeCreated, Long last_updated, String owner, Long lastSynced, Boolean finished) {
        this.id = id;
        this.timeCreated = timeCreated;
        this.lastUpdated = last_updated;
        this.owner = owner;
        this.lastSynced = lastSynced;
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", lastUpdated=" + lastUpdated +
                ", owner='" + owner + '\'' +
                ", lastSynced=" + lastSynced +
                ", finished=" + finished +
                '}';
    }

    public Boolean getFinished() {
        return finished;
    }
}
