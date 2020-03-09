package olnow.phmobile;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

public class RootDAO<T> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void add(T obj) throws HibernateException {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
            session.close();
        }
        catch (HibernateException e) {
            logger.error("Error in add", e);
            throw e;
        }
    }

    public void addOrUpdate(T obj) throws HibernateException {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(obj);
            session.getTransaction().commit();
            session.close();
        }
        catch (HibernateException e) {
            logger.error("Error in addOrUpdate", e);
            throw e;
        }
    }


    public void update(T obj) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(obj);
        session.getTransaction().commit();
        session.close();
    }

    public T find(Class<T> className, int id) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        T obj = session.get(className, id);
        session.close();
        return obj;
    }

    public T findName(Class<T> tClass, String name) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery(tClass);
        Root<T> root = criteriaQuery.from(tClass);
        EntityType type = session.getMetamodel().entity(tClass);

        criteriaQuery.select(root).where(criteria.equal(
                root.get("name"), name));
        criteriaQuery.select(root);

        Query<T> query = session.createQuery(criteriaQuery);
        try {
            T obj = (T) query.getSingleResult();
            if (obj != null) return obj;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            System.out.println("RootDAO: findName exception: " + e.toString());
            throw e;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<T> get() throws HibernateException {
        return null;
    }

    public ArrayList<T> get(String tableName) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        try {
            ArrayList<T> obj = (ArrayList<T>) session.
                            createQuery("From " + tableName).list();
            return obj;
        }
        catch (NoResultException e) {
            System.out.println("RootDAO: get: " + e.toString());
        }
        finally {
            session.close();
        }
        return null;
    }

    public ArrayList<T> get(Class<T> tClass, SingularAttribute orderBy) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery(tClass);
        Root<T> root = criteriaQuery.from(tClass);
        criteriaQuery.orderBy(criteria.asc(root.get(orderBy)));
        criteriaQuery.select(root);

        Query<T> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<T> obj = (ArrayList<T>) ((Query) query).getResultList();
            if (obj != null) return obj;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            System.out.println("RootDAO: findName exception: " + e.toString());
            throw e;
        }
        finally {
            session.close();
        }
    }

    public T find(Class className, SingularAttribute attr, String name) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery(className);
        Root<T> root = criteriaQuery.from(className);
        Predicate[] predicates = new Predicate[1];
        predicates[0] = criteria.equal(root.get(attr), name);
        criteriaQuery.select(root).where(predicates);
        Query<T> query = session.createQuery(criteriaQuery);
        try {
            T object = query.getSingleResult();
            if (object != null) return object;
        }
        catch (NoResultException e) {
        }
        catch (HibernateException e) {
            System.out.println("RootDAO: find exception: " + e.toString());
        }
        finally {
            session.close();
        }
        return null;
    }

}
