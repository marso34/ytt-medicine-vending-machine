package com.example.ytt.domain.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketTestController {

    @GetMapping("/websocket-vm-test")
    public String testPage1(Model model) {
        return "websocket-vm-test";
    }

    @GetMapping("/websocket-user-test")
    public String testPage2(Model model) {
        return "websocket-user-test";
    }

}
