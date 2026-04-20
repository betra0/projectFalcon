
package com.dany.falcon.ia;


public class Message {
    
    public enum SenderType{
        AI,
        USER,
        GUEST,
        SYSTEM
    }

    private String content;
    private SenderType sender;
    private Long id;
    private Integer position;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    //new message
    public Message(String content, SenderType sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
    }
    // recrear de la db
    public Message(String content, SenderType sender, Long id, Integer position, long timestamp) {
        this.content = content;
        this.sender = sender;
        this.id = id;
        this.position = position;
        this.timestamp = timestamp;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SenderType getSender() {
        return sender;
    }
    
    @Override
    public String toString() {
        return sender + ": " + content;
    }

    
    
    

    
    

    
}
