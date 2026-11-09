package com.cardgame.service;

import com.cardgame.dto.CartaPatchRequest;
import com.cardgame.dto.CartaRequest;
import com.cardgame.exception.ConflitoException;
import com.cardgame.exception.RecursoNaoEncontradoException;
import com.cardgame.model.Carta;
import com.cardgame.model.Raridade;
import com.cardgame.repository.CartaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartaService {

    private final CartaRepository repository;

    public CartaService(CartaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Carta> listar(String nome, Raridade raridade) {
        boolean temNome = nome != null && !nome.isBlank();
        if (temNome && raridade != null) {
            return repository.findByNomeContainingIgnoreCaseAndRaridade(nome.trim(), raridade);
        }
        if (temNome) {
            return repository.findByNomeContainingIgnoreCase(nome.trim());
        }
        if (raridade != null) {
            return repository.findByRaridade(raridade);
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Carta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carta com id " + id + " não encontrada"));
    }

    @Transactional
    public Carta criar(CartaRequest req) {
        if (repository.existsByNomeIgnoreCase(req.nome().trim())) {
            throw new ConflitoException("Já existe uma carta com o nome '" + req.nome().trim() + "'");
        }
        Carta carta = new Carta();
        preencher(carta, req);
        return repository.save(carta);
    }

    @Transactional
    public Carta atualizar(Long id, CartaRequest req) {
        Carta carta = buscarPorId(id);
        if (repository.existsByNomeIgnoreCaseAndIdNot(req.nome().trim(), id)) {
            throw new ConflitoException("Já existe uma carta com o nome '" + req.nome().trim() + "'");
        }
        preencher(carta, req);
        return repository.save(carta);
    }

    @Transactional
    public Carta atualizarParcial(Long id, CartaPatchRequest req) {
        Carta carta = buscarPorId(id);

        if (req.nome() != null) {
            String novoNome = req.nome().trim();
            if (repository.existsByNomeIgnoreCaseAndIdNot(novoNome, id)) {
                throw new ConflitoException("Já existe uma carta com o nome '" + novoNome + "'");
            }
            carta.setNome(novoNome);
        }
        if (req.tipo() != null) carta.setTipo(req.tipo());
        if (req.raridade() != null) carta.setRaridade(req.raridade());
        if (req.custoMana() != null) carta.setCustoMana(req.custoMana());
        if (req.ataque() != null) carta.setAtaque(req.ataque());
        if (req.defesa() != null) carta.setDefesa(req.defesa());
        if (req.descricao() != null) carta.setDescricao(req.descricao());

        return repository.save(carta);
    }

    @Transactional
    public void deletar(Long id) {
        Carta carta = buscarPorId(id);
        repository.delete(carta);
    }

    private void preencher(Carta carta, CartaRequest req) {
        carta.setNome(req.nome().trim());
        carta.setTipo(req.tipo());
        carta.setRaridade(req.raridade());
        carta.setCustoMana(req.custoMana());
        carta.setAtaque(req.ataque());
        carta.setDefesa(req.defesa());
        carta.setDescricao(req.descricao());
    }
}
