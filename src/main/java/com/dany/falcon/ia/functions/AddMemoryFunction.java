package com.dany.falcon.ia.functions;

import com.dany.falcon.chat.ChatService;
import com.dany.falcon.ia.AIExecutableFunction;
import com.dany.falcon.ia.MemoryItem;
import com.dany.falcon.ia.schema.FunctionSchema;
import com.dany.falcon.ia.schema.ParamSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMemoryFunction implements AIExecutableFunction {

    private final ChatService chatService;

    public AddMemoryFunction(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public String getName() {
        return "AddMemory";
    }

    @Override
    public String getDescription() {
        return "Agrega un recuerdo persistente al chat, esta funcion no retorna feadback";
    }

    @Override
    public List<String> getParameters() {
        return List.of("content", "importance(HIGH, MEDIUM, LOW)"); // lo que la IA debe enviar
    }

    @Override
    public FunctionSchema getSchema() {
        Map<String, ParamSchema> props = new HashMap<>();

        props.put("content", new ParamSchema("string", null));
        props.put("importance", new ParamSchema("string", List.of("HIGH","MEDIUM","LOW")));

        return new FunctionSchema(props, List.of("content", "importance"));
    }

    @Override
    public String execute(Map<String, Object> args) {
        System.out.println(".execute");
        System.out.println();

        String content = (String) args.get("content");
        String importanceStr = (String) args.get("importance");

        if (content == null || importanceStr == null) {
            return "Error: faltan argumentos (content, importance)";
        }

        MemoryItem.ImportanceType importance;
        try {
            importance = MemoryItem.ImportanceType.valueOf(importanceStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            importance = MemoryItem.ImportanceType.MEDIUM;
        }

        chatService.newPersistenMemory(content, importance);

        return "Memoria agregada: " + content;
    }
}
