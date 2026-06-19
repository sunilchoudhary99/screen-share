package com.example.MyWebsite.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "index";   // index.html open hoga
    }

    @PostMapping("/add")
    public String add(@RequestParam int num1,
                      @RequestParam int num2,
                      Model model) {

        int result = num1 + num2;
        model.addAttribute("result", result);
        return "index";
    }
}
