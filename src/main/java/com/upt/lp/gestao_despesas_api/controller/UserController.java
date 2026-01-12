package com.upt.lp.gestao_despesas_api.controller;

import com.upt.lp.gestao_despesas_api.model.User;
import com.upt.lp.gestao_despesas_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

   @PostMapping("/registar")
    public String registar(@RequestBody User user) {
        userService.registar(user);
        return "Utilizador registado com sucesso!";
    }


    @PostMapping("/login")
    public User login(@RequestParam String email, @RequestParam String password) {
        return userService.login(email, password);
    }
    
    @PostMapping("/logout")
    public String logout() {
        return "Sessão terminada com sucesso";
    }

}