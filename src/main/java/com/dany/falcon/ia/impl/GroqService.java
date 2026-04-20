package com.dany.falcon.ia.impl;

import com.dany.falcon.ia.*;
import com.dany.falcon.ia.schema.FunctionSchema;
import com.dany.falcon.ia.schema.ParamSchema;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroqService implements AIService {
    // esta implementacion "groq" no la programe yo, para no perder tiempo con cada implementacion por cada api de IA

    private final String apiKey;
    private final String MODEL = "llama-3.3-70b-versatile"; // Modelo potente y compatible con tools

    public GroqService(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public AIResponse sendMessage(AIRequest request) {
        return sendPrompt(request.getConversation(), request.getInitPrompt(), request.getAvailableFunctions(), request.getMemories());
    }

    private AIResponse sendPrompt(Conversation chat, String initPrompt, List<AIExecutableFunction> functions, MemoryManager memories) {
        HttpClient client = HttpClient.newHttpClient();
        String jsonRequest = createJsonRequest(chat, initPrompt, functions, memories);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);

            // Log de error si no hay choices
            if (!json.has("choices")) {
                System.out.println("Error de Groq: " + response.body());
                return null;
            }

            JsonObject messageObj = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message");

            String replyString = messageObj.has("content") && !messageObj.get("content").isJsonNull()
                    ? messageObj.get("content").getAsString() : null;

            List<AIFunctionCall> funcionesCall = new ArrayList<>();

            // Procesar llamadas a funciones (tool_calls)
            if (messageObj.has("tool_calls")) {
                JsonArray toolCalls = messageObj.getAsJsonArray("tool_calls");
                for (JsonElement tcElement : toolCalls) {
                    JsonObject tc = tcElement.getAsJsonObject().getAsJsonObject("function");
                    String name = tc.get("name").getAsString();
                    JsonObject argsJson = new Gson().fromJson(tc.get("arguments").getAsString(), JsonObject.class);

                    Map<String, Object> args = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry : argsJson.entrySet()) {
                        args.put(entry.getKey(), entry.getValue().getAsString());
                    }
                    funcionesCall.add(new AIFunctionCall(name, args));
                }
            }

            if (replyString == null && !funcionesCall.isEmpty()) {
                replyString = "[Function execution requested]";
            }

            return new AIResponse(new Message(replyString, Message.SenderType.AI), funcionesCall);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createJsonRequest(Conversation chat, String initPrompt, List<AIExecutableFunction> functions, MemoryManager memories) {
        JsonObject root = new JsonObject();
        root.addProperty("model", MODEL);

        JsonArray messages = new JsonArray();

        // 1. System Prompt (Combinando instrucciones y memoria)
        String systemContent = "System: " + initPrompt +
                "\nMemorias: [" + memories.getMemoriesAsPrompt() + "]";
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemContent);
        messages.add(systemMsg);

        // 2. Historial de chat
        for (Message msg : chat.getMessages()) {
            JsonObject m = new JsonObject();
            m.addProperty("role", msg.getSender() == Message.SenderType.USER ? "user" : "assistant");
            m.addProperty("content", msg.getContent());
            messages.add(m);
        }

        root.add("messages", messages);

        // 3. Herramientas (Tools)
        if (functions != null && !functions.isEmpty()) {
            JsonArray tools = new JsonArray();
            for (AIExecutableFunction func : functions) {
                JsonObject tool = new JsonObject();
                tool.addProperty("type", "function");

                JsonObject functionDetails = new JsonObject();
                functionDetails.addProperty("name", func.getName());
                functionDetails.addProperty("description", func.getDescription());
                functionDetails.add("parameters", convertSchemaToStandard(func.getSchema()));

                tool.add("function", functionDetails);
                tools.add(tool);
            }
            root.add("tools", tools);
            root.addProperty("tool_choice", "auto");
        }

        return new Gson().toJson(root);
    }

    private JsonObject convertSchemaToStandard(FunctionSchema schema) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "object");

        JsonObject props = new JsonObject();
        for (Map.Entry<String, ParamSchema> entry : schema.getProperties().entrySet()) {
            JsonObject p = new JsonObject();
            p.addProperty("type", entry.getValue().getType());
            props.add(entry.getKey(), p);
        }
        root.add("properties", props);

        JsonArray required = new JsonArray();
        for (String req : schema.getRequired()) {
            required.add(req);
        }
        root.add("required", required);

        return root;
    }
}