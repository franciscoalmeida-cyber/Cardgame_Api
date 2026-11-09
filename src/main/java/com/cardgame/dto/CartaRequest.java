package com.cardgame.dto;

import com.cardgame.model.Raridade;
import com.cardgame.model.TipoCarta;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo usado em POST e PUT (atualizacao completa). */
public record CartaRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        String nome,

        @NotNull(message = "O tipo é obrigatório (CRIATURA, FEITICO ou ARTEFATO)")
        TipoCarta tipo,

        @NotNull(message = "A raridade é obrigatória (COMUM, INCOMUM, RARA, EPICA ou LENDARIA)")
        Raridade raridade,

        @NotNull(message = "O custo de mana é obrigatório")
        @Min(value = 0, message = "O custo de mana mínimo é 0")
        @Max(value = 10, message = "O custo de mana máximo é 10")
        Integer custoMana,

        @Min(value = 0, message = "O ataque não pode ser negativo")
        @Max(value = 99, message = "O ataque máximo é 99")
        Integer ataque,

        @Min(value = 0, message = "A defesa não pode ser negativa")
        @Max(value = 99, message = "A defesa máxima é 99")
        Integer defesa,

        @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres")
        String descricao
) {
}
