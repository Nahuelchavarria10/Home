package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.LoginDTO;
import com.mindhub.homebanking.DTO.RegisterDTO;
import com.mindhub.homebanking.services.ServicesManager.AuthServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceManager authServiceManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        return authServiceManager.login(loginDTO);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO){
        return authServiceManager.register(registerDTO);
    }
    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return authServiceManager.test();
    }

}
