package com.mindhub.homebanking.services.ServicesManager;

import com.mindhub.homebanking.DTO.LoginDTO;
import com.mindhub.homebanking.DTO.RegisterDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@Service
public class AuthServiceManager {

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
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.email());
            final String jwt = jwtUtilService.generateToken(userDetails);

            if (loginDTO.email().isBlank()){
                return ResponseEntity.status(400).body("email is required");
            }
            if (loginDTO.password().isBlank()){
                return ResponseEntity.status(400).body("password is required");
            }

            return ResponseEntity.ok(jwt);

        }catch (Exception e){
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

            return new ResponseEntity<>("Registered",HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>("Internal server error.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> test(){
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok("Hola "+ mail);
    }

}
