package com.dany.falcon.chat;

import com.dany.falcon.ia.AIService;
import com.dany.falcon.ia.AIRequest;
import com.dany.falcon.ia.AIResponse;
import com.dany.falcon.ia.Message;
import com.dany.falcon.ia.Conversation;

public class ChatService {

    private AIService aiService;
    private Conversation chat;
    private String systemPrompt = """
Eres un asistente de inteligencia artificial diseñado para ayudar de forma clara y eficiente.

Reglas:
- Sé directo y conciso.
- Evita explicaciones innecesarias a menos que se pidan.
- Entrega información útil y correcta.
- Si no sabes algo, dilo claramente.
- Mantén coherencia en tus respuestas.
""";
    private String personality = """
Tu nombre es Falcon.

Personalidad:
- Eres tranquilo, seguro y ligeramente informal.
- Hablas de forma natural, como una persona real.
- Puedes ser un poco ingenioso, pero sin exagerar.
- Priorizas la claridad sobre ser demasiado amable.

Estilo:
- Respuestas cortas pero con contenido.
- Evita sonar robótico.
""";

    public ChatService(AIService aiService) {
        this.aiService = aiService;
        this.chat = new Conversation();
    }

    public void setAIService(AIService aiService) {
        this.aiService = aiService;
    }
    private String getInitPrompt(){
        return systemPrompt + "\n" + personality;
    }
            
            

    public String sendMessage(String content) {
        Message mes = new Message(content, Message.SenderType.USER);
        chat.addMessage(mes);
        AIRequest req = new AIRequest(mes, chat, this.getInitPrompt());
        AIResponse res = aiService.sendMessage(req);
        Message reply = res.getReply();
        chat.addMessage(reply);
        return res.getReply().toString();
    }
}
