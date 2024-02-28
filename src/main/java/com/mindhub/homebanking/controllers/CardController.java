package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.CardApplyDTO;
import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.JwtUtilService;
import io.micrometer.common.util.StringUtils;
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
    private JwtUtilService jwtUtilService;

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

            if (cardApplyDTO.colorCardType().isBlank()) {
                return ResponseEntity.status(400).body("color is required");
            }
            if (cardApplyDTO.cardType().isBlank()) {
                return ResponseEntity.status(400).body("type is required");
            }

            if (isMaxCard(client)) {
                return ResponseEntity.status(400).body("error el cliente no puede tener mas de 3 tarjetas");
            }

            String numberCardRandom = jwtUtilService.getRandomNumber(1000,9999) + "-" + jwtUtilService.getRandomNumber(1000,9999) + "-" + jwtUtilService.getRandomNumber(1000,9999) + "-" + jwtUtilService.getRandomNumber(1000,9999);
            int cvvRandom = jwtUtilService.getRandomNumber(100,999);
            Card card = new Card(client,CardType.valueOf(cardApplyDTO.cardType()),ColorCardType.valueOf(cardApplyDTO.colorCardType()),numberCardRandom,cvvRandom,LocalDate.now(),LocalDate.now().plusYears(5));
            client.addCard(card);
            cardRepository.save(card);

            return ResponseEntity.status(201).body("card creada");
        }catch (Exception e){
            return new ResponseEntity<>("datos incorrectos pa", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/current/cards")
    public ResponseEntity<List<CardDTO>> getCards(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(userMail);

        Set<Card> cards = client.getCards();

        return new ResponseEntity<>(cards.stream().map(CardDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    private boolean isMaxCard(Client client) {
        int cantidadDeCard = client.getCards().size();
        return cantidadDeCard >= 3;
    }

}
