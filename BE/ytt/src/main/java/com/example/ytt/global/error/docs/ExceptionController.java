package com.example.ytt.global.error.docs;

import com.example.ytt.global.error.util.ErrorCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error-docs")
@RequiredArgsConstructor
public class ExceptionController {

    @GetMapping
    public String error(Model model) {
        model.addAttribute("groupedErrors", ErrorCodeUtil.getGroupedErrors());

        return "errorDocs";
    }

}
