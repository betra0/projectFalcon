
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

    public Message(String content, SenderType sender) {
        this.content = content;
        this.sender = sender;
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
