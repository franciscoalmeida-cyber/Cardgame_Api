package com.cardgame.controller;

import com.cardgame.dto.CartaPatchRequest;
import com.cardgame.dto.CartaRequest;
import com.cardgame.dto.CartaResponse;
import com.cardgame.model.Carta;
import com.cardgame.model.Raridade;
import com.cardgame.service.CartaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cartas")
public class CartaController {

    private final CartaService service;

    public CartaController(CartaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CartaResponse> listar(@RequestParam(required = false) String nome,
                                      @RequestParam(required = false) Raridade raridade) {
        return service.listar(nome, raridade).stream().map(CartaResponse::de).toList();
    }

    @GetMapping("/{id}")
    public CartaResponse buscarPorId(@PathVariable Long id) {
        return CartaResponse.de(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CartaResponse> criar(@Valid @RequestBody CartaRequest req) {
        Carta criada = service.criar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(local).body(CartaResponse.de(criada));
    }

    @PutMapping("/{id}")
    public CartaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CartaRequest req) {
        return CartaResponse.de(service.atualizar(id, req));
    }

    @PatchMapping("/{id}")
    public CartaResponse atualizarParcial(@PathVariable Long id, @Valid @RequestBody CartaPatchRequest req) {
        return CartaResponse.de(service.atualizarParcial(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
