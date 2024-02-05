package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;
    @GetMapping("/hello")
    public String getClients(){
        return "hello clients!";
    }
    @GetMapping("/")
    public ResponseEntity<List<ClientDTO>> getAllClients(){
        List<Client> clients= clientRepository.findAll();
        return new ResponseEntity<>(clients.stream().map(ClientDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id){
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        else {
            ClientDTO clientDTO = new ClientDTO(client);
            return new ResponseEntity<>(clientDTO,HttpStatus.OK);
        }
    }
}
