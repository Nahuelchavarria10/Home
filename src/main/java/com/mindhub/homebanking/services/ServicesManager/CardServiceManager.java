package com.mindhub.homebanking.services.ServicesManager;

import com.mindhub.homebanking.DTO.CardApplyDTO;
import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.securityServices.UtilService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class CardServiceManager {

    @Autowired
    private CardService cardService;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UtilService utilService;

    public ResponseEntity<List<CardDTO>> getAllCards(){
        List<Card> cards = cardService.getAllCards();
        return new ResponseEntity<>(cards.stream()
                .map(card -> new CardDTO(card))
                .collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    public ResponseEntity<?> getCardById(@PathVariable("id") Long id){
        Card card = cardService.getCardById(id);
        if (card == null){
            String notFound = "card not found";
            return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
        }
        else {
            CardDTO cardDTO = new CardDTO(card);
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> applyCard ( @RequestBody CardApplyDTO cardApplyDTO){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientService.getClientByEmail(userMail);

            if (cardApplyDTO.cardColor().isBlank()) {
                return ResponseEntity.status(400).body("color is required");
            }
            if (cardApplyDTO.cardType().isBlank()) {
                return ResponseEntity.status(400).body("type is required");
            }

            if (cardService.countByTypeAndClient(CardType.valueOf(cardApplyDTO.cardType()), client) >= 3) {
                return ResponseEntity.status(403).body("Customer already has 3 cards of this type. Maximum limit reached.");
            }

            if (cardService.existsCardByColorAndTypeAndClient(CardColor.valueOf(cardApplyDTO.cardColor()),CardType.valueOf(cardApplyDTO.cardType()), client)) {
                return ResponseEntity.status(403).body("Customer already has a card with this color and type combination.");
            }

            String cardNumberRandom = utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999) + "-" + utilService.getRandomNumber(1000,9999);
            String cvvRandom = utilService.generateCvv();

            Card card = new Card(client,CardType.valueOf(cardApplyDTO.cardType()), CardColor.valueOf(cardApplyDTO.cardColor()),cardNumberRandom,cvvRandom, LocalDate.now(),LocalDate.now().plusYears(5));
            client.addCard(card);
            cardService.saveCard(card);

            return ResponseEntity.status(201).body("card created successfully");
        }catch (Exception e){
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<CardDTO>> getCards(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientService.getClientByEmail(userMail);

        Set<Card> cards = client.getCards();

        return new ResponseEntity<>(cards.stream().map(CardDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }
}
