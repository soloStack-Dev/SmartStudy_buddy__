package com.example.demo.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final DocumentStudyService documentStudyService;
    private final WebSearchStudyService webSearchStudyService;

    public ChatController(
            ChatService chatService,
            DocumentStudyService documentStudyService,
            WebSearchStudyService webSearchStudyService
    ) {
        this.chatService = chatService;
        this.documentStudyService = documentStudyService;
        this.webSearchStudyService = webSearchStudyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/chat")
    @ResponseBody
    public String chat(@RequestParam("message") String message) {
        String answer = chatService.chat(message);
        return """
                <div class="message user">%s</div>
                <div class="message ai">%s</div>
                """.formatted(escape(message), escape(answer));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    @PostMapping("/upload-notes")
    @ResponseBody
    public String uploadNotes(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        String summary = documentStudyService.summarizeNotes(file);
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "your notes";
        return """
                <div class="message user">Uploaded notes: %s</div>
                <div class="message ai">%s</div>
                """.formatted(escape(fileName), escape(summary));
    }

    @PostMapping("/search-chat")
    @ResponseBody
    public String searchChat(@RequestParam("message") String message) {
        String answer = webSearchStudyService.answerWithSearch(message);
        return """
                <div class="message user">%s</div>
                <div class="message ai">%s</div>
                """.formatted(escape(message + " (with web search)"), answer);
    }
}

