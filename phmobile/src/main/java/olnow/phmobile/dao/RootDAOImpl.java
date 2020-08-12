package olnow.phmobile.dao;

import olnow.phmobile.HibernateSessionFactoryUtil;
import olnow.phmobile.IRootDAO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

@Repository
public abstract class RootDAOImpl<T> implements IRootDAO<T> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected SessionFactory sessionFactory;

    protected final Class<T> tClass;

    RootDAOImpl(Class<T> tClass) {
        logger.debug("Constructor run, tClass: {}", tClass);
        this.tClass = tClass;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void add(T obj) throws HibernateException {
        try {
            // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            // session.beginTransaction();
            getCurrentSession().save(obj);
            // session.getTransaction().commit();
            // session.close();
        }
        catch (HibernateException e) {
            logger.error("Error in add", e);
            throw e;
        }
    }

    public void addOrUpdate(T obj) throws HibernateException {
        try {
            // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            // session.beginTransaction();
            getCurrentSession().saveOrUpdate(obj);
            // session.getTransaction().commit();
            // session.close();
        }
        catch (HibernateException e) {
            logger.error("Error in addOrUpdate", e);
            throw e;
        }
    }


    public void update(T obj) throws HibernateException {
        // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        // session.beginTransaction();
        getCurrentSession().saveOrUpdate(obj);
        // session.getTransaction().commit();
        // session.close();
    }

    public T find(int id) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        T obj = session.get(tClass, id);
        session.close();
        return obj;
    }

    public T findName(String name) throws HibernateException {
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
            logger.error("RootDAO: findName exception ", e);
            // System.out.println("RootDAO: findName exception: " + e.toString());
            throw e;
        }
        finally {
            session.close();
        }
    }

    @Deprecated
    public ArrayList<T> get(String tableName) throws HibernateException {
        // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        //sessionFactory.openSession();
        try {
            ArrayList<T> obj = (ArrayList<T>) getCurrentSession().
                    createQuery("From " + tableName).list();
            return obj;
        }
        catch (NoResultException e) {
            logger.info("NoResult: ", e);
            // System.out.println("RootDAO: get: " + e.toString());
        }
        finally {
            sessionFactory.close();
        }
        return null;
    }

    public ArrayList<T> get() throws HibernateException {
        // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        // sessionFactory.openSession();
        Table table = tClass.getAnnotation(Table.class);
        String tableName = table.name();
        logger.debug("Table name: {}, Class simple name: {}", tableName, tClass.getSimpleName());
        try {
            ArrayList<T> obj = (ArrayList<T>) getCurrentSession().
                    createQuery("From " + tClass.getSimpleName()).list();
            return obj;
        }
        catch (NoResultException e) {
            logger.info("[NoResultException]");
            // System.out.println("RootDAO: get: " + e.toString());
        }
        finally {
            // session.close();
        }
        return null;
    }


    public ArrayList<T> getSorted(SingularAttribute orderBy) throws HibernateException {
        // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery(tClass);
        Root<T> root = criteriaQuery.from(tClass);
        criteriaQuery.orderBy(criteria.asc(root.get(orderBy)));
        criteriaQuery.select(root);

        Query<T> query = getCurrentSession().createQuery(criteriaQuery);
        try {
            ArrayList<T> obj = (ArrayList<T>) ((Query) query).getResultList();
            if (obj != null) return obj;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            logger.error("findName exception");
            // System.out.println("RootDAO: findName exception: " + e.toString());
            throw e;
        }
        finally {
            // session.close();
        }
    }

    @Deprecated
    public T find(Class className, SingularAttribute attr, String name) throws HibernateException {
        // Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery(className);
        Root<T> root = criteriaQuery.from(className);
        Predicate[] predicates = new Predicate[1];
        predicates[0] = criteria.equal(root.get(attr), name);
        criteriaQuery.select(root).where(predicates);
        Query<T> query = getCurrentSession().createQuery(criteriaQuery);
        try {
            T object = query.getSingleResult();
            if (object != null) return object;
        }
        catch (NoResultException e) {
        }
        catch (HibernateException e) {
            logger.info("find exception");
            // System.out.println("RootDAO: find exception: " + e.toString());
        }
        finally {
            // session.close();
        }
        return null;
    }

    public T find(SingularAttribute attr, String name) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteria.createQuery((Class) tClass);
        Root<T> root = criteriaQuery.from((Class) tClass);
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
            logger.info("find exception");
            // System.out.println("RootDAO: find exception: " + e.toString());
        }
        finally {
            session.close();
        }
        return null;
    }


}
