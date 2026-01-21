package com.woofly.companymanagementapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
public class GitHubWebhookController {

    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubEvent(@RequestBody String payload,
                                                    @RequestHeader("X-GitHub-Event") String eventType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            String pusherName = root.path("pusher").path("name").asText();

            String branch = root.path("ref").asText();

            String commitMessage = root.path("head_commit").path("message").asText();

            String repoName = root.path("repository").path("name").asText();

            log.info("======= GITHUB MƏLUMATI =======");
            log.info("Repozitoriya: {}", repoName);
            log.info("İstifadəçi: {}", pusherName);
            log.info("Branch: {}", branch);
            log.info("Mesaj: {}", commitMessage);
            log.info("===============================");

        } catch (Exception e) {
            log.error("Parsing xətası: {}", e.getMessage());
        }
        return ResponseEntity.ok("Webhook uğurla alındı!");
    }
}
