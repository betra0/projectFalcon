package com.dany.falcon.ia;

public class ConversationPreview {
    String id;
    String name;
    String description;
    long timestamp;

    public ConversationPreview(String id, String name, String description, long timestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
