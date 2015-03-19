package com.scanner.bth.db;

/**
 * Created by shaon on 3/19/2015.
 */
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

    private Integer id;
    private Long timeCreated;
    private Long lastUpdated;
    private String owner;

    public Log(Integer id, Long timeCreated, Long last_updated, String owner) {
        this.id = id;
        this.timeCreated = timeCreated;
        this.lastUpdated = last_updated;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", lastUpdated=" + lastUpdated +
                ", owner='" + owner + '\'' +
                '}';
    }
}
