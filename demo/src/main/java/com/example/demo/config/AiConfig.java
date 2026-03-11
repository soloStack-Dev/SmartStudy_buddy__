package com.example.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${smartstudy.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${smartstudy.ollama.model-name:llama3.2}") String modelName
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(
            @Value("${smartstudy.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${smartstudy.ollama.embedding-model-name:nomic-embed-text:latest}") String embeddingModelName
    ) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embeddingModelName)
                .build();
    }

    @Bean
    public WebSearchEngine webSearchEngine(
            @Value("${smartstudy.tavily.api-key:}") String tavilyApiKey
    ) {
        if (tavilyApiKey == null || tavilyApiKey.isBlank()) {
            // Web search will be effectively disabled; callers should handle empty/failed search.
            return query -> {
                throw new IllegalStateException("Tavily API key is not configured. Please set 'smartstudy.tavily.api-key'.");
            };
        }

        return TavilyWebSearchEngine.builder()
                .apiKey(tavilyApiKey)
                .build();
    }
}

