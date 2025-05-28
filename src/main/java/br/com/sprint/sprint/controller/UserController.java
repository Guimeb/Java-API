package br.com.sprint.sprint.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/id")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
}
