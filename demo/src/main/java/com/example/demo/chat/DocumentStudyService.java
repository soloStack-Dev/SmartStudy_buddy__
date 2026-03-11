package com.example.demo.chat;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentStudyService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final DocumentParser documentParser = new ApacheTikaDocumentParser();

    public DocumentStudyService(ChatLanguageModel chatModel, EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    public String summarizeNotes(MultipartFile file) {
        String text = extractText(file);

        if (text == null || text.isBlank()) {
            return "I couldn't read any text from this file. Please try another document.";
        }

        // Create an embedding for the whole document.
        dev.langchain4j.model.output.Response<Embedding> embeddingResponse = embeddingModel.embed(text);
        Embedding embedding = embeddingResponse.content();

        // Use the text content with the chat model to generate a study-friendly summary.
        String prompt = """
                You are SmartStudy Buddy, an AI study assistant.

                A student has uploaded study notes. Read the notes and produce:
                - A concise summary (3-6 bullet points)
                - The 3 most important concepts to remember
                - One simple practice question they could answer to test understanding

                Keep your language clear and beginner friendly.

                --- STUDY NOTES START ---
                %s
                --- STUDY NOTES END ---
                """.formatted(text);

        String response = chatModel.generate(prompt);
        return response;
    }

    private String extractText(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.toLowerCase().endsWith(".txt")) {
                byte[] bytes = file.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
            Document document = documentParser.parse(file.getInputStream());
            return document.text();
        } catch (IOException e) {
            return null;
        }
    }
}

