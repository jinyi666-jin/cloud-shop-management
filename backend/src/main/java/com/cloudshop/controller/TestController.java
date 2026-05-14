package com.cloudshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public void hello(HttpServletResponse response) throws IOException {
        String json = "{\"code\":200,\"message\":\"success\",\"data\":\"Hello World\"}";
        log.info("About to send response: {}", json);
        log.info("Response length: {}", json.length());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();
        
        log.info("Response sent successfully");
    }
}