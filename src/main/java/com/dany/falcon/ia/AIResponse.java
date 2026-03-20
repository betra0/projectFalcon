
package com.dany.falcon.ia;
import java.util.List;

public class AIResponse {
    private Message reply;
    private List<String> functions; 

    public AIResponse(Message reply, List<String> functions) {
        this.reply = reply;
        this.functions = functions;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public Message getReply() {
        return reply;
    }

    public List<String> getFunctions() {
        return functions;
    }
    
    
}