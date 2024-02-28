package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.ColorCardType;

public record CardApplyDTO(String cardType, String colorCardType) {
}
