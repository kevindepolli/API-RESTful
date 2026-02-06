package repository;

import model.Categoria;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CategoriaRepository {

    private final JPAApi jpa;

    @Inject
    public CategoriaRepository(JPAApi jpa) {
        this.jpa = jpa;
    }

    public List<Categoria> findAll() {
        return jpa.withTransaction(entityManager -> {
            TypedQuery<Categoria> query = entityManager
                    .createQuery("SELECT c FROM Categoria c", Categoria.class);
            return query.getResultList();
        });
    }

    public Categoria findById(Long id) {
        return jpa.withTransaction(entityManager -> {
            return entityManager.find(Categoria.class, id);
        });
    }

    public Categoria create(Categoria categoria) {
        return jpa.withTransaction(entityManager -> {
            entityManager.persist(categoria);
            return categoria;
        });
    }

    public Categoria update(Categoria categoria) {
        return jpa.withTransaction(entityManager -> {
            return entityManager.merge(categoria);
        });
    }

    public void delete(Long id) {
        jpa.withTransaction(entityManager -> {
            Categoria categoria = entityManager.find(Categoria.class, id);
            if (categoria != null) {
                entityManager.remove(categoria);
            }
            return null;
        });
    }
}
