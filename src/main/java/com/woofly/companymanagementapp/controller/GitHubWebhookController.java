package com.woofly.companymanagementapp.controller;

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
        log.info("------- YENİ BİLDİRİŞ GƏLDİ! -------");
        log.info("Hadisə növü: {}", eventType);
        log.debug("Gələn Məlumat (Payload): {}", payload);
        log.info("------------------------------------");

        // GitHub-a hər şeyin qaydasında olduğunu bildirmək üçün 200 OK qaytarırıq
        return ResponseEntity.ok("Webhook uğurla alındı!");
    }
}
