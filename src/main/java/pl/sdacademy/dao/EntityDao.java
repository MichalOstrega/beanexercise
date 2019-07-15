package pl.sdacademy.dao;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.Collection;

public class EntityDao<T> {
    @PersistenceContext
    private EntityManager entityManager;
    private Class<T> clazz;

    public EntityDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Transactional
    public void save(T entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void delete(T entity) {

        entityManager.merge(entity);
        entityManager.remove(entity);
    }


    @Transactional
    public void deleteById(Integer id) {
        entityManager.remove(findById(id));
    }

    @Transactional
    public void update(T entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public T findById(int id) {
        T entity = entityManager.find(clazz, id);
        return entity;
    }

    public Collection<T> findAll() {
        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> rootEntry = cq.from(clazz);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = session.createQuery(all);
        return allQuery.getResultList();
    }
}

