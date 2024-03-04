package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.CardApplyDTO;
import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UtilService utilService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public ResponseEntity<List<CardDTO>> getAllCards(){
        List<Card> cards = cardRepository.findAll();
        return new ResponseEntity<>(cards.stream()
                .map(card -> new CardDTO(card))
                .collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable("id") Long id){
        Card card = cardRepository.findById(id).orElse(null);
        if (card == null){
            String notFound = "card not found";
            return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
        }
        else {
            CardDTO cardDTO = new CardDTO(card);
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        }
    }


    @PostMapping("current/applyCard")
    public ResponseEntity<?> applyCard ( @RequestBody CardApplyDTO cardApplyDTO){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientRepository.findByEmail(userMail);

            if (cardApplyDTO.cardColor().isBlank()) {
                return ResponseEntity.status(400).body("color is required");
            }
            if (cardApplyDTO.cardType().isBlank()) {
                return ResponseEntity.status(400).body("type is required");
            }

            if (cardRepository.countByTypeAndClient(CardType.valueOf(cardApplyDTO.cardType()), client) >= 3) {
                return ResponseEntity.status(403).body("Customer already has 3 cards of this type. Maximum limit reached.");
            }

            if (cardRepository.existsCardByColorAndTypeAndClient(CardColor.valueOf(cardApplyDTO.cardColor()),CardType.valueOf(cardApplyDTO.cardType()), client)) {
                return ResponseEntity.status(403).body("Customer already has a card with this color and type combination.");
            }

            String cardNumberRandom = utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999);
            int cvvRandom = utilService.getRandomNumber(100,999);

            Card card = new Card(client,CardType.valueOf(cardApplyDTO.cardType()), CardColor.valueOf(cardApplyDTO.cardColor()),cardNumberRandom,cvvRandom,LocalDate.now(),LocalDate.now().plusYears(5));
            client.addCard(card);
            cardRepository.save(card);

            return ResponseEntity.status(201).body("card created successfully");
        }catch (Exception e){
            return new ResponseEntity<>("Error processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/current/cards")
    public ResponseEntity<List<CardDTO>> getCards(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(userMail);

        Set<Card> cards = client.getCards();

        return new ResponseEntity<>(cards.stream().map(CardDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

}
