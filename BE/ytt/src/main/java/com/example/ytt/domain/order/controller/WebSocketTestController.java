package com.example.ytt.domain.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketTestController {

    @GetMapping("/websocket-test")
    public String testPage(Model model) {
        return "websocket-test";
    }
}
