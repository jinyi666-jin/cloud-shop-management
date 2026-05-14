package com.cloudshop.controller;

import com.cloudshop.dto.ApiResponse;
import com.cloudshop.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestBody Map<String, Object> request) {
        
        String message = (String) request.get("message");
        Long userId = null;
        if (request.containsKey("userId")) {
            Object userIdObj = request.get("userId");
            if (userIdObj != null && userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            }
        }
        
        String reply = aiService.chat(message, userId);
        
        String jsonResponse = "{\"code\":200,\"message\":\"success\",\"data\":{\"reply\":\"" + escapeJson(reply) + "\",\"type\":\"text\"}}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return ResponseEntity.ok().headers(headers).body(jsonResponse);
    }

    @PostMapping("/recommend")
    public ResponseEntity<String> recommend(
            @RequestBody Map<String, Object> request) {
        
        String query = (String) request.get("query");
        Long userId = request.containsKey("userId") ? 
            ((Number) request.get("userId")).longValue() : null;
        
        String reply = aiService.recommendProducts(query, userId);
        
        String jsonResponse = "{\"code\":200,\"message\":\"success\",\"data\":{\"reply\":\"" + escapeJson(reply) + "\",\"type\":\"recommend\"}}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return ResponseEntity.ok().headers(headers).body(jsonResponse);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(
            @RequestBody Map<String, Object> request) {
        
        String query = (String) request.get("query");
        Long userId = request.containsKey("userId") ? 
            ((Number) request.get("userId")).longValue() : null;
        
        String reply = aiService.analyzeOrder(query, userId);
        
        String jsonResponse = "{\"code\":200,\"message\":\"success\",\"data\":{\"reply\":\"" + escapeJson(reply) + "\",\"type\":\"analysis\"}}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return ResponseEntity.ok().headers(headers).body(jsonResponse);
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}