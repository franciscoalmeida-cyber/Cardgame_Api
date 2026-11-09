package com.cardgame.dto;

import com.cardgame.model.Carta;
import com.cardgame.model.Raridade;
import com.cardgame.model.TipoCarta;

import java.time.LocalDateTime;

public record CartaResponse(
        Long id,
        String nome,
        TipoCarta tipo,
        Raridade raridade,
        Integer custoMana,
        Integer ataque,
        Integer defesa,
        String descricao,
        LocalDateTime dataCriacao
) {
    public static CartaResponse de(Carta c) {
        return new CartaResponse(c.getId(), c.getNome(), c.getTipo(), c.getRaridade(),
                c.getCustoMana(), c.getAtaque(), c.getDefesa(), c.getDescricao(), c.getDataCriacao());
    }
}
