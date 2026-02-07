package services;

import model.Categoria;
import repository.CategoriaRepository;

import javax.inject.Inject;
import java.util.List;

public class CategoriaService {

    private final CategoriaRepository repository;

    @Inject
    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarCategorias() {
        return repository.findAll();
    }

    public Categoria buscarCategoria(Long id) {
        Categoria categoria = repository.findById(id);
        if (categoria == null) {
            throw new RuntimeException("Categoria não encontrada");
        }
        return categoria;
    }

    public Categoria criarCategoria(Categoria categoria) {

        // Normalizar texto
        String desc = categoria.getDescricao() == null ? null : categoria.getDescricao().trim();

        // Validação: obrigatório
        if (desc == null || desc.isBlank()) {
            throw new RuntimeException("Descrição é obrigatória");
        }

        // Validação: tamanho mínimo
        if (desc.length() < 3) {
            throw new RuntimeException("A descrição deve ter pelo menos 3 caracteres");
        }

        // Validação: tamanho máximo
        if (desc.length() > 255) {
            throw new RuntimeException("A descrição deve ter no máximo 255 caracteres");
        }

        // Validação: duplicidade
        List<Categoria> existentes = repository.findAll();
        boolean jaExiste = existentes.stream()
                .anyMatch(c -> c.getDescricao().equalsIgnoreCase(desc));

        if (jaExiste) {
            throw new RuntimeException("Já existe uma categoria com esta descrição");
        }

        // Aplicar a versão normalizada
        categoria.setDescricao(desc);

        return repository.create(categoria);
    }

    public Categoria atualizarCategoria(Long id, Categoria dadosAtualizados) {
        Categoria existente = repository.findById(id);
        if (existente == null) {
            throw new RuntimeException("Categoria não encontrada");
        }

        String novaDescricao = dadosAtualizados.getDescricao();

        if (novaDescricao == null || novaDescricao.isBlank()) {
            throw new RuntimeException("Descrição é obrigatória");
        }

        String descNormalizada = novaDescricao.trim();

        if (descNormalizada.length() < 3) {
            throw new RuntimeException("A descrição deve ter pelo menos 3 caracteres");
        }

        if (descNormalizada.length() > 255) {
            throw new RuntimeException("A descrição deve ter no máximo 255 caracteres");
        }

        // Checar duplicidade (exceto se for a mesma categoria)
        List<Categoria> existentes = repository.findAll();
        boolean jaExiste = existentes.stream()
                .anyMatch(c -> c.getDescricao().equalsIgnoreCase(descNormalizada)
                        && !c.getId().equals(id));

        if (jaExiste) {
            throw new RuntimeException("Já existe outra categoria com esta descrição");
        }

        existente.setDescricao(descNormalizada);

        return repository.update(existente);
    }

    public void deletarCategoria(Long id) {
        Categoria categoria = repository.findById(id);
        if (categoria == null) {
            throw new RuntimeException("Categoria não encontrada");
        }
        repository.delete(id);
    }
}
