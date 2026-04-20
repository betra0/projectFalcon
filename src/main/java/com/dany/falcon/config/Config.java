package com.dany.falcon.config;

import io.github.cdimascio.dotenv.Dotenv;


public class Config {
    private static Config instance;
    private final Dotenv dotenv;

    private Config() {
        dotenv = Dotenv.load();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public String getGeminiApiKey() {
        return dotenv.get("GEMINI_API_KEY");
    }
    public String getGroqApiKey() {
        return dotenv.get("GROQ_API_KEY");
    }
}

