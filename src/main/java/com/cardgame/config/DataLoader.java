package com.cardgame.config;

import com.cardgame.model.Carta;
import com.cardgame.model.Raridade;
import com.cardgame.model.TipoCarta;
import com.cardgame.repository.CartaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final CartaRepository repository;

    public DataLoader(CartaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.save(nova("Dragão das Cinzas", TipoCarta.CRIATURA, Raridade.LENDARIA, 8, 9, 7,
                "Ao entrar em campo, causa 3 de dano a todas as criaturas inimigas."));
        repository.save(nova("Goblin Batedor", TipoCarta.CRIATURA, Raridade.COMUM, 1, 2, 1,
                "Uma criatura rápida e barulhenta."));
        repository.save(nova("Bola de Fogo", TipoCarta.FEITICO, Raridade.INCOMUM, 4, null, null,
                "Causa 5 de dano a um alvo."));
        repository.save(nova("Amuleto da Aurora", TipoCarta.ARTEFATO, Raridade.RARA, 3, null, null,
                "Cura 2 pontos de vida no início de cada turno."));
    }

    private Carta nova(String nome, TipoCarta tipo, Raridade raridade, int custo,
                       Integer ataque, Integer defesa, String descricao) {
        Carta c = new Carta();
        c.setNome(nome);
        c.setTipo(tipo);
        c.setRaridade(raridade);
        c.setCustoMana(custo);
        c.setAtaque(ataque);
        c.setDefesa(defesa);
        c.setDescricao(descricao);
        return c;
    }
}
