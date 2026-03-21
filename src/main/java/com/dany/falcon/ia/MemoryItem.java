package com.dany.falcon.ia;
import java.util.UUID;

public class MemoryItem {

    public enum MemoryType {
        LONG_TERM,
        SHORT_TERM,
        REMINDER
    }

    public enum ImportanceType {
        HIGH,
        MEDIUM,
        LOW
    }
    private String id;
    private String content;
    private long timestamp;
    private MemoryType type;
    private ImportanceType importance;
    private long expiry;

    // cando carga de la bd
    public MemoryItem(String id, String content, long timestamp, MemoryType type, ImportanceType importance, long expiry) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.importance = importance;
        this.expiry = expiry;

    }
    // cuando se genera recien
    public MemoryItem(String content, MemoryType type, ImportanceType importance, long expiry) {
        this.content = content;
        this.type = type;
        this.importance = importance;
        this.expiry = expiry;
        this.timestamp = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
    }


    public boolean isExpired() {
        if (type == MemoryType.LONG_TERM || expiry <= 0) {
            return false;
        }
        return System.currentTimeMillis() - timestamp > expiry;
    }
    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MemoryType getType() {
        return type;
    }

    public ImportanceType getImportance() {
        return importance;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImportance(ImportanceType importance) {
        this.importance = importance;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }
}
