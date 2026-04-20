
package com.dany.falcon.ia;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Conversation {
    
    private final List<Message> messages;
    private String id;
    private CompletableFuture<Void> convFuture;
    private String name;
    private String description = "";
    private long timestamp;
    private final Object lock = new Object();

    public Object getLock() {
        return lock;
    }

    // cuando se genera un nuevo chat
    public Conversation() {
        this.messages = new ArrayList<>();
        this.id = UUID.randomUUID().toString();
        this.name = java.time.LocalDateTime.now().toString();
        this.timestamp=System.currentTimeMillis();

    }



    // cuando se extrae de db
    public Conversation(List<Message> messages, String id, String name, String description, long timestamp){

        this.messages = messages;
        this.id = id;
        this.description = description;
        this.name = name;
        this.timestamp = timestamp;
        this.convFuture = CompletableFuture.completedFuture(null);
    }
    public CompletableFuture<Void> getConvFuture() {
        return convFuture;
    }

    public void setConvFuture(CompletableFuture<Void> convFuture) {
        this.convFuture = convFuture;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addMessage(Message mess){
        messages.add(mess);
    }
    public void newMessage(String content, Message.SenderType sender){
        this.addMessage(new Message(content, sender));
    }

    public List<Message> getMessages() {
        return messages;
    }
    public String getHistory() {
       StringBuilder sb = new StringBuilder();
        for (Message msg : messages) {
            sb.append(msg.toString()).append("\n");
        }
        return sb.toString();
    }
       
}
