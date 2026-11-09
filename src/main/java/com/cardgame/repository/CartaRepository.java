package com.cardgame.repository;

import com.cardgame.model.Carta;
import com.cardgame.model.Raridade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaRepository extends JpaRepository<Carta, Long> {

    List<Carta> findByNomeContainingIgnoreCase(String nome);

    List<Carta> findByRaridade(Raridade raridade);

    List<Carta> findByNomeContainingIgnoreCaseAndRaridade(String nome, Raridade raridade);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
