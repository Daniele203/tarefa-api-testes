package br.com.ada.tarefas.service;

import br.com.ada.tarefas.dto.TarefaRequest;
import br.com.ada.tarefas.dto.TarefaResponse;
import br.com.ada.tarefas.exception.TarefaNaoEncontradaException;
import br.com.ada.tarefas.model.Tarefa;
import br.com.ada.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TarefaServiceTest {

    // Simula o Repository. Nenhum banco H2 e iniciado.
    @Mock
    private TarefaRepository tarefaRepository;

    // Cria o Service com o Repository simulado.
    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void givenTarefaValida_whenCadastrar_thenSalvarTarefa() {
        // Given: um DTO valido e um repositorio que devolve a mesma tarefa recebida
        TarefaRequest request = new TarefaRequest("Estudar JUnit", "Resolver exercicios da aula", false);
        when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        // When: cadastrar
        TarefaResponse response = tarefaService.cadastrar(request);

        // Then: save() foi chamado com os dados esperados e o response os reflete
        ArgumentCaptor<Tarefa> captor = ArgumentCaptor.forClass(Tarefa.class);
        verify(tarefaRepository).save(captor.capture());
        Tarefa tarefaSalva = captor.getValue();
        assertEquals("Estudar JUnit", tarefaSalva.getTitulo());
        assertEquals("Resolver exercicios da aula", tarefaSalva.getDescricao());
        assertFalse(tarefaSalva.isConcluida());

        assertEquals("Estudar JUnit", response.getTitulo());
        assertEquals("Resolver exercicios da aula", response.getDescricao());
        assertFalse(response.isConcluida());
    }

    @Test
    void givenTarefaExistente_whenBuscarPorId_thenRetornarResponse() {
        // Given: o repositorio devolve uma tarefa para o ID 1
        Tarefa tarefa = new Tarefa("Estudar Mockito", "Criar mocks", true);
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        // When: buscar por ID
        TarefaResponse response = tarefaService.buscarPorId(1L);

        // Then: o response tem os dados da tarefa
        assertEquals("Estudar Mockito", response.getTitulo());
        assertEquals("Criar mocks", response.getDescricao());
        assertTrue(response.isConcluida());
    }

    @Test
    void givenTarefaInexistente_whenBuscarPorId_thenLancarExcecao() {
        // Given: o repositorio nao encontra o ID 99
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // When: buscar por ID
        TarefaNaoEncontradaException erro = assertThrows(TarefaNaoEncontradaException.class,
                () -> tarefaService.buscarPorId(99L));

        // Then: a mensagem identifica o ID
        assertEquals("Tarefa nao encontrada: 99", erro.getMessage());
    }
}
