package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins="*")
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @GetMapping("/")
    public ResponseEntity<List<ClientDTO>> getAllClients(){
        return new ResponseEntity<>(clientService.getAllClientsDTO(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable("id") Long id){
        Client client = clientService.getClientById(id);
        if (client == null){
            String notFound = "client not found";
            return new ResponseEntity<>(notFound,HttpStatus.NOT_FOUND);
        }
        else {
            ClientDTO clientDTO = new ClientDTO(client);
            return new ResponseEntity<>(clientDTO,HttpStatus.OK);
        }
    }
    @GetMapping("/current")
    public ResponseEntity<?> getClient(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientService.getClientByEmail(userMail);

        return ResponseEntity.ok(new ClientDTO(client));
    }

}
