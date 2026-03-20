package com.dany.falcon.ia.impl;


import com.dany.falcon.ia.AIRequest;
import com.dany.falcon.ia.AIResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import com.dany.falcon.ia.AIService;
import com.dany.falcon.ia.Message;
import com.dany.falcon.ia.Conversation;

/**
 *
 * @author bytrayed
 */
public class GeminiService implements AIService {

    private final String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    private String createJsonRequest(Conversation chat, String initPrompt) {
        StringBuilder sb = new StringBuilder();

        if (initPrompt != null && !initPrompt.isEmpty()) {
            String init = """
                    {
                        "role": "%s",
                        "parts":[{"text": %s}]
                    },
                    """.formatted(
                    "user",
                    new Gson().toJson(initPrompt)
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
                    new Gson().toJson(msg.getContent()) // 🔥 esto escapa bien el texto
            );

            sb.append(part);

            // evitar coma final
            if (i < chat.getMessages().size() - 1) {
                sb.append(",");
            }

            sb.append("\n");
        }

        return """
                {
                  "contents": [
                    %s
                  ]
                }
                """.formatted(sb.toString());
    }

    private String sendPrompt(String prompt, Conversation chat, String initPrompt) {
        HttpClient client = HttpClient.newHttpClient();

        String jsonRequest = createJsonRequest(chat, initPrompt);

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

            String text = json
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            //System.out.println(text);
            return text;

        } catch (Exception e) {
            System.out.println("Error al conectar con Gemini: " + e.getMessage());
        }
        return null;

    }

    @Override
    public AIResponse sendMessage(AIRequest request) {
        String res = sendPrompt(request.getMessage().getContent(), request.getConversation(), request.getInitPrompt());
        Message reply = new Message(res, Message.SenderType.AI);
        return new AIResponse(reply, null);
    }
}