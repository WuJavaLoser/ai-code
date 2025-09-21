package com.yt.aicode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wys17
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public String healthCheck() {
        return "ok";
    }
}
