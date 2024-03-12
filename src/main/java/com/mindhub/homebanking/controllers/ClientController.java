package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.services.ServicesManager.ClientServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientServiceManager clientServiceManager;


    @GetMapping("/")
    public ResponseEntity<List<ClientDTO>> getAllClients(){
        return clientServiceManager.getAllClients();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable("id") Long id){
        return clientServiceManager.getClientById(id);
    }
    @GetMapping("/current")
    public ResponseEntity<?> getClient(){
        return clientServiceManager.getClient();
    }

}
