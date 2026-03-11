package com.example.demo.chat;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebSearchStudyService {

    private final WebSearchEngine webSearchEngine;
    private final ChatLanguageModel chatModel;

    public WebSearchStudyService(WebSearchEngine webSearchEngine, ChatLanguageModel chatModel) {
        this.webSearchEngine = webSearchEngine;
        this.chatModel = chatModel;
    }

    public String answerWithSearch(String question) {
        WebSearchTool webSearchTool = WebSearchTool.from(webSearchEngine);
        String searchContext = webSearchTool.searchWeb(question);

        String prompt = """
                You are SmartStudy Buddy, a very helpful study assistant.
                You are given web search results related to a student's question.

                Your task:
                - Read the student's question.
                - Read the web search results text.
                - Produce ONLY a list of links with short explanations.

                Output format (HTML, no extra text before or after):
                - For each relevant resource, output a separate line like:
                  <a href="FULL_URL" target="_blank">Title or short label</a> - brief, beginner-friendly explanation
                - Do not wrap the whole list in <ul> or <ol>.
                - Do not add any other commentary outside those lines.

                Student question:
                %s

                Web search results text:
                %s
                """.formatted(question, searchContext);

        return chatModel.generate(prompt);
    }
}

