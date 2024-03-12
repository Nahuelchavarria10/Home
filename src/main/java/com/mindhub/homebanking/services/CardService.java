package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;

import java.util.List;
public interface CardService {

    List<Card> getAllCards();
    Card getCardById(Long id);

    Boolean existsCardByColorAndTypeAndClient(CardColor color, CardType type, Client client);
    int countByTypeAndClient(CardType type, Client client);
    void saveCard(Card card);
}
