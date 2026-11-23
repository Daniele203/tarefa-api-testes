package br.com.ada.tarefas.integration;

import br.com.ada.tarefas.model.Tarefa;
import br.com.ada.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TarefaIntegracaoTest {

    // Aqui Spring Boot inicia o contexto e usa o H2 em memoria.
    @Autowired
    private MockMvc mockMvc;

    // O Repository e usado apenas para preparar/conferir dados no teste de integracao.
    @Autowired
    private TarefaRepository tarefaRepository;

    @BeforeEach
    void limparDados() {
        tarefaRepository.deleteAll();
    }

    @Test
    void givenTarefaValida_whenCadastrarPorPost_thenRetornar201ESalvarNoH2() throws Exception {
        // Given: JSON valido de cadastro
        String json = """
                {"titulo": "Estudar JUnit", "descricao": "Resolver exercicios da aula 01", "concluida": false}
                """;

        // When + Then: POST retorna 201 com o corpo esperado
        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Estudar JUnit"))
                .andExpect(jsonPath("$.descricao").value("Resolver exercicios da aula 01"))
                .andExpect(jsonPath("$.concluida").value(false));

        // Then: a tarefa realmente foi gravada no H2
        List<Tarefa> salvas = tarefaRepository.findAll();
        assertEquals(1, salvas.size());
        assertEquals("Estudar JUnit", salvas.get(0).getTitulo());
        assertEquals("Resolver exercicios da aula 01", salvas.get(0).getDescricao());
    }

    @Test
    void givenTarefaExistente_whenExcluirPorDelete_thenRetornar204ERemoverDoH2() throws Exception {
        // Given: uma tarefa ja gravada no banco
        Tarefa existente = tarefaRepository.save(new Tarefa("Tarefa temporaria", "Sera excluida", false));
        Long id = existente.getId();

        // When + Then: DELETE retorna 204
        mockMvc.perform(delete("/api/tarefas/{id}", id))
                .andExpect(status().isNoContent());

        // Then: a tarefa deixou de existir no H2
        assertFalse(tarefaRepository.findById(id).isPresent());

        // E uma nova exclusao do mesmo ID retorna 404
        mockMvc.perform(delete("/api/tarefas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("TAREFA_NAO_ENCONTRADA"));
    }
}
