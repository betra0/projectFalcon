
package com.dany.falcon.ia;
import com.dany.falcon.config.Config;
import com.dany.falcon.ia.impl.GeminiService;


public class AIServiceFactory {
    private static final Config config = Config.getInstance();

    public static AIService create(AIProvider provider) {
        switch (provider) {
            case GEMINI:
                return new GeminiService(config.getGeminiApiKey());
            default:
                throw new IllegalArgumentException("Provider no soportado");
        }
    }
}