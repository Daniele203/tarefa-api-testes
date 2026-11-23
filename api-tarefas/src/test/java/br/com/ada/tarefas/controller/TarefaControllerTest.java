package br.com.ada.tarefas.controller;

import br.com.ada.tarefas.dto.TarefaResponse;
import br.com.ada.tarefas.exception.ApiExceptionHandler;
import br.com.ada.tarefas.exception.TarefaNaoEncontradaException;
import br.com.ada.tarefas.service.TarefaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TarefaControllerTest {

    // O Controller conhece o Service, mas NAO usa Entity nem Repository.
    @Mock
    private TarefaService tarefaService;

    @InjectMocks
    private TarefaController tarefaController;

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        // Teste de Controller isolado, sem iniciar toda a aplicacao.
        this.mockMvc = MockMvcBuilders.standaloneSetup(tarefaController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void givenTarefasExistentes_whenListar_thenRetornarStatus200() throws Exception {
        // Given: o Service simulado devolve duas tarefas
        List<TarefaResponse> tarefas = List.of(
                new TarefaResponse(1L, "Estudar JUnit", "Aula 01", false),
                new TarefaResponse(2L, "Estudar Mockito", "Aula 02", true));
        when(tarefaService.listar()).thenReturn(tarefas);

        // When + Then: GET /api/tarefas retorna 200 com o JSON esperado
        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Estudar JUnit"))
                .andExpect(jsonPath("$[1].concluida").value(true));
    }

    @Test
    void givenTarefaInexistente_whenBuscarPorId_thenRetornarStatus404() throws Exception {
        // Given: o Service simulado lanca a excecao de tarefa nao encontrada
        when(tarefaService.buscarPorId(99L))
                .thenThrow(new TarefaNaoEncontradaException("Tarefa nao encontrada: 99"));

        // When + Then: GET /api/tarefas/99 retorna 404 com o JSON de erro padronizado
        mockMvc.perform(get("/api/tarefas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("TAREFA_NAO_ENCONTRADA"))
                .andExpect(jsonPath("$.mensagem").value("Tarefa nao encontrada: 99"))
                .andExpect(jsonPath("$.path").value("/api/tarefas/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
