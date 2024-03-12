package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.CardApplyDTO;
import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.services.ServicesManager.CardServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardServiceManager cardServiceManager;


    @GetMapping("/")
    public ResponseEntity<List<CardDTO>> getAllCards(){
        return cardServiceManager.getAllCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable("id") Long id){
        return cardServiceManager.getCardById(id);
    }


    @PostMapping("current/applyCard")
    public ResponseEntity<?> applyCard ( @RequestBody CardApplyDTO cardApplyDTO){
        return cardServiceManager.applyCard(cardApplyDTO);
    }

    @GetMapping("/current/cards")
    public ResponseEntity<List<CardDTO>> getCards(){
        return cardServiceManager.getCards();
    }

}
