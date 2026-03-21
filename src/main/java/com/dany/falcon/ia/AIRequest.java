
package com.dany.falcon.ia;

import java.util.List;
import java.util.Map;

public class AIRequest {
    

    private final MemoryManager memories;
    private final Conversation conversation;
    private final String initPrompt;
    private final List<AIExecutableFunction> availableFunctions;

    

    public AIRequest(Conversation conversation, String initPrompt, List<AIExecutableFunction> availableFunctions, MemoryManager memories) {
        this.conversation = conversation;
        this.initPrompt = initPrompt;
        this.availableFunctions = availableFunctions;
        this.memories = memories;
    }

    public List<AIExecutableFunction> getAvailableFunctions() {
        return availableFunctions;
    }

    public String getFunctionPrompt() {
        StringBuilder sb = new StringBuilder("Funciones disponibles para la IA:\n");
        for (AIExecutableFunction func : availableFunctions) {
            sb.append(func.getName())
                    .append("(")
                    .append(String.join(", ", func.getParameters()))
                    .append(") - ")
                    .append(func.getDescription())
                    .append("\n");
        }
        return sb.toString();
    }

    public MemoryManager getMemories() {
        return memories;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getInitPrompt() {
        return initPrompt;
    }

 
}