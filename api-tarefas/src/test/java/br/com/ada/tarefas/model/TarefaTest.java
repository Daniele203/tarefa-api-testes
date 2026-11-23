package br.com.ada.tarefas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testes unitarios da Entity Tarefa: apenas JUnit 5, sem Spring e sem banco.
public class TarefaTest {

    @Test
    void givenTituloNulo_whenCriarTarefa_thenLancarExcecao() {
        // Given: titulo nulo e descricao valida
        String titulo = null;
        String descricao = "Resolver exercicios da aula";

        // When: criar a tarefa
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> new Tarefa(titulo, descricao, false));

        // Then: a excecao informa o problema do titulo
        assertEquals("O titulo nao pode ser nulo.", erro.getMessage());
    }

    @Test
    void givenDescricaoNula_whenCriarTarefa_thenLancarExcecao() {
        // Given: titulo valido e descricao nula
        String titulo = "Estudar JUnit";
        String descricao = null;

        // When: criar a tarefa
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> new Tarefa(titulo, descricao, false));

        // Then: a excecao informa o problema da descricao
        assertEquals("A descricao nao pode ser nula.", erro.getMessage());
    }

    @Test
    void givenDadosValidos_whenCriarTarefa_thenTarefaCriadaCorretamente() {
        // Given: dados validos
        String titulo = "Estudar JUnit";
        String descricao = "Resolver exercicios da aula";

        // When: criar a tarefa
        Tarefa tarefa = new Tarefa(titulo, descricao, true);

        // Then: os campos refletem os dados informados (id so existe apos salvar no banco)
        assertEquals("Estudar JUnit", tarefa.getTitulo());
        assertEquals("Resolver exercicios da aula", tarefa.getDescricao());
        assertTrue(tarefa.isConcluida());
        assertNull(tarefa.getId());
    }
}
