package com.mindhub.homebanking.services.ServicesImpl;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;
    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean existsCardByColorAndTypeAndClient(CardColor color, CardType type, Client client) {
        return cardRepository.existsCardByColorAndTypeAndClient(color, type, client);
    }

    @Override
    public int countByTypeAndClient(CardType type, Client client) {
        return cardRepository.countByTypeAndClient(type, client);
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }
}
