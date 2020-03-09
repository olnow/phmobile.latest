package olnow.phmobile;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

public class ReferenceDAO {
    public void add(Reference reference) throws HibernateException {
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(reference);
            session.getTransaction().commit();
            session.close();
        }
        catch (HibernateException e) {
            System.out.println("addReference exception: " + e.toString());
            throw e;
        }
        finally {
            if (session != null)
                session.close();
        }
    }

    public void updateReference(Reference reference) throws HibernateException {
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(reference);
            session.getTransaction().commit();
            session.close();
        }
        catch (HibernateException e) {
            System.out.println("addReference exception: " + e.toString());
            throw e;
        }
        finally {
            if (session != null)
                session.close();
        }
    }

    public Reference find(int id) throws HibernateException {
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Reference reference = session.get(Reference.class, id);
            session.close();
            return reference;
        }
        catch (NoResultException e) {

        }
        catch (HibernateException e) {
            System.out.println("addReference exception: " + e.toString());
            throw e;
        }
        finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    public Reference findName(String name) throws HibernateException {
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder criteria = session.getCriteriaBuilder();
            CriteriaQuery<Reference> criteriaQuery = criteria.createQuery(Reference.class);
            Root<Reference> root = criteriaQuery.from(Reference.class);
            EntityType type = session.getMetamodel().entity(Reference.class);

            criteriaQuery.select(root).where(criteria.equal(
                    root.get(Reference_.name), name));
            criteriaQuery.select(root);

            Query<Reference> query = session.createQuery(criteriaQuery);
            try {
                Reference reference = (Reference) query.getSingleResult();
                if (reference != null) return reference;
                else return null;
            } catch (NoResultException e) {
                return null;
            }
        }
        catch (HibernateException e) {
            System.out.println("findreference exception: " + e.toString());
            throw e;
        }
        finally {
            if (session != null)
                session.close();
        }
    }

    public ArrayList<Reference> getReferences() {
        ArrayList<Reference> references = (ArrayList<Reference>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From Reference").list();
        return references;
    }

    public Object find(Class className, SingularAttribute attr, String name, int type) throws HibernateException {
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder criteria = session.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = criteria.createQuery(className);
            Root<Object> root = criteriaQuery.from(className);
            //EntityType type = session.getMetamodel().entity(className);

            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteria.equal(root.get("idtype"), type);
            predicates[1] = criteria.equal(root.get(attr), name);
            criteriaQuery.select(root).where(predicates);
            //criteriaQuery.select(root);

            Query<Object> query = session.createQuery(criteriaQuery);
            try {
                Object object = query.getSingleResult();
                if (object != null) return object;
                else return null;
            } catch (NoResultException e) {
                return null;
            }
        }
        catch (HibernateException e) {
            System.out.println("findReference exception: " + e.toString());
            throw e;
        }
        finally {
            if (session != null)
                session.close();
        }
    }

}
