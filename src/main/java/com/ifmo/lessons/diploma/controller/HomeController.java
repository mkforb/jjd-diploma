package com.ifmo.lessons.diploma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by User on 15.06.2021.
 */
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
}
