package repository;

import jakarta.persistence.TypedQuery;
import models.PagedResult;
import models.Produto;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.List;

public class ProdutoDAO {

    private final JPAApi jpaApi;

    @Inject
    public ProdutoDAO(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    /**
     * 1. LISTAR (Com Paginação e Busca)
     */
    public PagedResult<Produto> findList(int page, int pageSize, String termo) {
        return jpaApi.withTransaction(em -> {
            // Montagem da Query Dinâmica
            String hql = "FROM Produto p WHERE 1=1";
            if (termo != null && !termo.trim().isEmpty()) {
                hql += " AND (lower(p.descricao) LIKE :termo)";
            }
            hql += " ORDER BY p.id DESC"; // Ordenar pelo mais recente

            // Query de Dados
            TypedQuery<Produto> query = em.createQuery(hql, Produto.class);
            if (termo != null && !termo.trim().isEmpty()) {
                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            }

            // Paginação
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            List<Produto> data = query.getResultList();

            // Query de Contagem (Total de registros para calcular páginas)
            String countHql = "SELECT count(p) FROM Produto p WHERE 1=1";
            if (termo != null && !termo.trim().isEmpty()) {
                countHql += " AND (lower(p.descricao) LIKE :termo)";
            }
            TypedQuery<Long> countQuery = em.createQuery(countHql, Long.class);
            if (termo != null && !termo.trim().isEmpty()) {
                countQuery.setParameter("termo", "%" + termo.toLowerCase() + "%");
            }
            Long total = countQuery.getSingleResult();

            return new PagedResult<>(data, total, page, pageSize);
        });
    }

    /**
     * 2. BUSCAR POR ID
     */
    public Produto findById(Long id) {
        return jpaApi.withTransaction(em -> {
            return em.find(Produto.class, id);
        });
    }

    /**
     * 3. CRIAR (Create)
     */
    public Produto create(Produto produto) {
        return jpaApi.withTransaction(em -> {
            // O ID deve ser nulo para criar um novo
            produto.id = null;
            em.persist(produto);
            return produto;
        });
    }

    /**
     * 4. ATUALIZAR (Update)
     */
    public Produto update(Produto produto) {
        return jpaApi.withTransaction(em -> {
            return em.merge(produto);
        });
    }

    /**
     * 5. DELETAR (Delete)
     */
    public boolean delete(Long id) {
        return jpaApi.withTransaction(em -> {
            Produto p = em.find(Produto.class, id);
            if (p != null) {
                em.remove(p);
                return true;
            }
            return false;
        });
    }
}