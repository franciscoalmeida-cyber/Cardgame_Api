package com.cardgame;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.seed.enabled=false")
@AutoConfigureMockMvc
class CartaControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String CARTA_VALIDA = """
            {"nome":"Cavaleiro Sombrio","tipo":"CRIATURA","raridade":"EPICA",
             "custoMana":5,"ataque":6,"defesa":4,"descricao":"Teste"}
            """;

    @Test
    void fluxoCompletoDoCrud() throws Exception {
        MvcResult criada = mvc.perform(post("/api/cartas")
                        .contentType(MediaType.APPLICATION_JSON).content(CARTA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        int id = JsonPath.read(criada.getResponse().getContentAsString(), "$.id");

        mvc.perform(get("/api/cartas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cavaleiro Sombrio"));

        mvc.perform(patch("/api/cartas/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ataque\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ataque").value(9))
                .andExpect(jsonPath("$.defesa").value(4));

        mvc.perform(delete("/api/cartas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/cartas/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void nomeDuplicadoRetorna409() throws Exception {
        String json = CARTA_VALIDA.replace("Cavaleiro Sombrio", "Carta Duplicada");
        mvc.perform(post("/api/cartas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/cartas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        String invalido = "{\"nome\":\"\",\"tipo\":\"CRIATURA\",\"raridade\":\"COMUM\",\"custoMana\":99}";
        mvc.perform(post("/api/cartas").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.custoMana").exists());
    }

    @Test
    void raridadeInexistenteRetorna400() throws Exception {
        String json = CARTA_VALIDA.replace("EPICA", "MITICA");
        mvc.perform(post("/api/cartas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void idInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/cartas/99999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/cartas/99999")).andExpect(status().isNotFound());
    }
}
