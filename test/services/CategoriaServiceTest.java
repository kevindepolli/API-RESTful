package services;

import models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CategoriaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoriaServiceTest {

    private CategoriaRepository repositoryMock;
    private CategoriaService service;

    @BeforeEach
    public void setup() {
        repositoryMock = mock(CategoriaRepository.class);
        service = new CategoriaService(repositoryMock);
    }

    @Test
    public void deveCriarCategoriaComSucesso() {
        Categoria nova = new Categoria();
        nova.setDescricao("Eletrônicos");

        when(repositoryMock.findAll()).thenReturn(List.of());
        when(repositoryMock.create(nova)).thenReturn(nova);

        Categoria criada = service.criarCategoria(nova);

        assertNotNull(criada);
        assertEquals("Eletrônicos", criada.getDescricao());
        verify(repositoryMock, times(1)).create(nova);
    }

    @Test
    public void deveFalharAoCriarCategoriaVazia() {
        Categoria cat = new Categoria();
        cat.setDescricao("   ");

        assertThrows(RuntimeException.class, () -> service.criarCategoria(cat));
    }

    @Test
    public void deveFalharAoCriarCategoriaDuplicada() {
        Categoria nova = new Categoria();
        nova.setDescricao("Eletrônicos");

        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setDescricao("Eletrônicos");

        when(repositoryMock.findAll()).thenReturn(List.of(existente));

        assertThrows(RuntimeException.class, () -> service.criarCategoria(nova));
    }

    @Test
    public void deveAtualizarCategoriaComSucesso() {
        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setDescricao("Antiga");

        Categoria atualizada = new Categoria();
        atualizada.setDescricao("Nova");

        when(repositoryMock.findById(1L)).thenReturn(existente);
        when(repositoryMock.findAll()).thenReturn(List.of(existente));
        when(repositoryMock.update(existente)).thenReturn(existente);

        Categoria result = service.atualizarCategoria(1L, atualizada);

        assertEquals("Nova", result.getDescricao());
        verify(repositoryMock).update(existente);
    }

    @Test
    public void deveFalharAoAtualizarCategoriaInexistente() {
        when(repositoryMock.findById(1L)).thenReturn(null);

        Categoria atualizada = new Categoria();
        atualizada.setDescricao("Nova");

        assertThrows(RuntimeException.class, () ->
                service.atualizarCategoria(1L, atualizada)
        );
    }

    @Test
    public void deveDeletarCategoria() {
        Categoria existente = new Categoria();
        existente.setId(1L);

        when(repositoryMock.findById(1L)).thenReturn(existente);

        service.deletarCategoria(1L);

        verify(repositoryMock).delete(1L);
    }

    @Test
    public void deveFalharAoDeletarCategoriaInexistente() {
        when(repositoryMock.findById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                service.deletarCategoria(1L)
        );
    }
}
