
package com.dany.falcon.ia;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    
    private List<Message> messages;

    public Conversation() {
        this.messages = new ArrayList<>();
    }
    
    public void addMessage(Message mess){
        messages.add(mess);
    }
    public void newMessage(String content, Message.SenderType sender){
        messages.add(new Message(content, sender));
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
