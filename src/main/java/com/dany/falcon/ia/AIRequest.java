
package com.dany.falcon.ia;

import java.util.Map;

public class AIRequest {
    
    private Message message;
    private Map<String, Object> memory;
    private Conversation conversation;
    private String initPrompt;
    
    public AIRequest(Message message, Map<String, Object> memory, Conversation conversation, String initPrompt) {
        this.message = message;
        this.memory = memory;
        this.conversation = conversation;
        this.initPrompt = initPrompt;
    }
    public AIRequest(Message message, Conversation conversation, String initPrompt) {
        this.message = message;
        this.conversation = conversation;
        this.initPrompt = initPrompt;
                
    }
    public AIRequest(Message message, String initPrompt) {
        this.message = message;
        this.conversation = new Conversation();
        this.initPrompt = initPrompt;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setMemory(Map<String, Object> memory) {
        this.memory = memory;
    }
    

    public Message getMessage() {
        return message;
    }

    public Map<String, Object> getMemory() {
        return memory;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getInitPrompt() {
        return initPrompt;
    }
    
    
    
    
    
 
}