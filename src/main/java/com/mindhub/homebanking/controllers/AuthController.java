package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.LoginDTO;
import com.mindhub.homebanking.DTO.RegisterDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.securityServices.JwtUtilService;
import com.mindhub.homebanking.securityServices.UtilService;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtilService jwtUtilService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.email());
            final String jwt = jwtUtilService.generateToken(userDetails);

            return ResponseEntity.ok(jwt);

        }catch (Exception e){
            return new ResponseEntity<>("error (nombre del error pa)", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO){
        try {

            if (registerDTO.firstName().isBlank() || registerDTO.lastName().isBlank() || registerDTO.email().isBlank() || registerDTO.password().isBlank()){
                return ResponseEntity.status(400).body("All fields is required");
            }

            if (clientService.getClientByEmail(registerDTO.email()) != null) {
                return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
            }

            Client client = new Client(registerDTO.firstName(), registerDTO.lastName(), registerDTO.email(), passwordEncoder.encode(registerDTO.password()));

            Account account = new Account("VIN-" + utilService.getRandomNumber(100,99999999) , LocalDate.now(),0);

            client.addAccount(account);
            clientService.saveClient(client);
            accountService.saveAccount(account);

            return new ResponseEntity<>("created",HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>("datos incorrectos pa", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> test(){
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok("Hola "+ mail);
    }

}
