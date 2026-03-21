package com.dany.falcon.ia.impl;


import com.dany.falcon.ia.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dany.falcon.ia.schema.FunctionSchema;
import com.dany.falcon.ia.schema.ParamSchema;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 *
 * @author bytrayed
 */
public class GeminiService implements AIService {

    private final String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    private String createJsonRequest(Conversation chat, String initPrompt, List<AIExecutableFunction> functions, MemoryManager memories) {
        StringBuilder sb = new StringBuilder();

        if (initPrompt != null && !initPrompt.isEmpty()) {
            String init = """
                    {
                        "role": "%s",
                        "parts":[{"text": %s }]
                    },
                    """.formatted(
                    "user",
                    new Gson().toJson(initPrompt + memories.getMemoriesAsPrompt())
            );
            sb.append(init);
        }


        for (int i = 0; i < chat.getMessages().size(); i++) {
            Message msg = chat.getMessages().get(i);

            String role = msg.getSender() == Message.SenderType.USER ? "user" : "model";

            String part = """
                    {
                        "role": "%s",
                        "parts":[{"text": %s}]
                    }
                    """.formatted(
                    role,
                    new Gson().toJson(msg.getContent())
            );

            sb.append(part);

            // evitar coma final
            if (i < chat.getMessages().size() - 1) {
                sb.append(",");
            }

            sb.append("\n");
        }

        String toolsJson = createToolsJson(functions);

        return """
        {
          "contents": [
            %s
          ]%s
        }
        """.formatted(
                sb.toString(),
                toolsJson.isEmpty() ? "" : ",\n" + toolsJson
        );
    }

    private String createToolsJson(List<AIExecutableFunction> functions) {
        if (functions == null || functions.isEmpty()) return "";

        JsonArray functionDeclarations = new JsonArray();

        for (AIExecutableFunction func : functions) {
            JsonObject f = new JsonObject();

            f.addProperty("name", func.getName());
            f.addProperty("description", func.getDescription());

            // convertir tu schema → JSON Gemini
            f.add("parameters", convertSchemaToJson(func.getSchema()));

            functionDeclarations.add(f);
        }

        JsonObject tool = new JsonObject();
        tool.add("functionDeclarations", functionDeclarations);

        JsonArray toolsArray = new JsonArray();
        toolsArray.add(tool);

        return "\"tools\": " + toolsArray.toString();
    }

    private JsonObject convertSchemaToJson(FunctionSchema schema) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "object");

        JsonObject properties = new JsonObject();

        for (Map.Entry<String, ParamSchema> entry : schema.getProperties().entrySet()) {
            ParamSchema param = entry.getValue();

            JsonObject p = new JsonObject();
            p.addProperty("type", param.getType());

            if (param.getEnumValues() != null) {
                JsonArray enumArray = new JsonArray();
                for (String val : param.getEnumValues()) {
                    enumArray.add(val);
                }
                p.add("enum", enumArray);
            }

            properties.add(entry.getKey(), p);
        }

        root.add("properties", properties);

        JsonArray required = new JsonArray();
        for (String req : schema.getRequired()) {
            required.add(req);
        }

        root.add("required", required);

        return root;
    }



    private AIResponse sendPrompt(Conversation chat, String initPrompt, List<AIExecutableFunction> functions, MemoryManager memories) {
        HttpClient client = HttpClient.newHttpClient();

        String jsonRequest = createJsonRequest(chat, initPrompt, functions, memories);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", this.apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);

            System.out.println(json);


            JsonArray parts = json
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts");

            String replyString = null;
            List<AIFunctionCall> funcionesCall = new ArrayList<>();

            for (JsonElement element : parts) {
                JsonObject part = element.getAsJsonObject();

                // 🔹 caso normal (texto)
                if (part.has("text")) {
                    replyString = part.get("text").getAsString();
                }

                // 🔹 caso function call
                if (part.has("functionCall")) {
                    JsonObject fc = part.getAsJsonObject("functionCall");

                    String name = fc.get("name").getAsString();
                    JsonObject argsJson = fc.getAsJsonObject("args");

                    Map<String, Object> args = new HashMap<>();

                    for (Map.Entry<String, JsonElement> entry : argsJson.entrySet()) {
                        args.put(entry.getKey(), entry.getValue().getAsString());
                    }

                    funcionesCall.add(new AIFunctionCall(name, args));




                }
            }

            if (replyString ==null && !funcionesCall.isEmpty()){
                replyString = "[Function executed]";
            }
            return new AIResponse(new Message(replyString, Message.SenderType.AI), funcionesCall);





        } catch (Exception e) {
            System.out.println("Error al conectar con Gemini: " + e.getMessage());
        }
        return null;

    }

    @Override
    public AIResponse sendMessage(AIRequest request) {
        return sendPrompt(request.getConversation(), request.getInitPrompt(), request.getAvailableFunctions(), request.getMemories());


    }
}