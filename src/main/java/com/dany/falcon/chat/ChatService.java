package com.dany.falcon.chat;

import com.dany.falcon.database.Database;
import com.dany.falcon.ia.*;
import com.dany.falcon.ia.functions.AddMemoryFunction;
import com.sun.tools.jconsole.JConsoleContext;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ChatService {

    private Map<String, ConversationPreview> prevConvs;
    private AIService aiService;
    private Conversation chat;
    private MemoryManager memories;
    private final Database db;
    private List<AIExecutableFunction> availableFunctions;


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
        this.db = Database.getInstance();
        this.loadMemories();
        this.loadConvPreviws();
        this.availableFunctions = List.of(
                new AddMemoryFunction(this)
        );

    }

    public void setAIService(AIService aiService) {
        this.aiService = aiService;
    }
    private String getInitPrompt(){
        return systemPrompt + "\n" + personality;
    }

    private void loadConvPreviws(){
        this.prevConvs = db.getAllConvsPreviws();
    }

    private void loadMemories(){

        List<MemoryItem> mems = this.db.getAllMemories();
        this.memories= new MemoryManager(mems);
        System.out.println(memories.getMemoriesAsPrompt());
    }
    // crara un recuerdo persitente
    public void newPersistenMemory(String content, MemoryItem.ImportanceType importanceType){
        System.out.println("Se esta ejecutando la funcion save Memory");
        this.memories.addMemory(new MemoryItem(content, MemoryItem.MemoryType.LONG_TERM, importanceType, 0));
    }

            
    private void executeFunctions(List<AIFunctionCall> listF){
        for (AIFunctionCall funcall :listF){
            String name = funcall.getName();
            Map<String, Object> args = funcall.getArgs();
            AIExecutableFunction executeFunction = null;
            for (AIExecutableFunction efun: availableFunctions){
                if (efun.getName().equals(name)){
                    executeFunction=efun;
                    break;
                }
            }
            if(executeFunction ==null){
                System.out.print("ERROR no se a encontrado la funcion: "+ name);
                continue;
            }
            try{
                String res = executeFunction.execute(args);
            } catch (Exception e) {
                System.out.println("Error ejecutando función " + name + ": " + e.getMessage());
            }


        }
    }
    public String sendMessage(String content) {
        Message mes = new Message(content, Message.SenderType.USER);
        // añadir mensaje del user
        addMessageInChat(mes);

        AIRequest req = new AIRequest(chat, this.getInitPrompt(), availableFunctions, memories);
        AIResponse res = aiService.sendMessage(req);
        List<AIFunctionCall> funclist = res.functions();
        if (funclist != null && !funclist.isEmpty()) {
            this.executeFunctions(funclist);
        }
        Message reply = res.reply();
        // añadir respuesta de la ia a chat o conversasion
        addMessageInChat(reply);
        return res.reply().toString();
    }

    private void addMessageInChat(Message mess){
        synchronized (chat.getLock()) {
            if(chat.getConvFuture() == null){
                chat.setTimestamp(System.currentTimeMillis());
                chat.setConvFuture(CompletableFuture.runAsync(()->{saveConversationInDb(chat);}));
            }
            chat.addMessage(mess);
            mess.setPosition(chat.getMessages().size()-1);
            chat.getConvFuture().thenRunAsync(()->{saveMessageInDb(mess, chat);});
        }

    }

    public void saveConversationInDb(Conversation chat){
        db.saveOrUpdateConversation(chat.getId(), chat.getTimestamp(), chat.getName(), chat.getDescription());
        this.prevConvs.put(chat.getId(), new ConversationPreview(chat.getId(), chat.getName(), chat.getDescription(), chat.getTimestamp()));
    }
    public void saveMessageInDb(Message message, Conversation conv){
        try{
            Long memId = db.saveMessage(message, conv.getId());
            message.setId(memId);

        }catch (Exception e){
            System.out.println("error al guardar: " + e.toString());
        }

    }
}


