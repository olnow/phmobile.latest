package olnow.phmobile;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

public class RoamingDAO {
    public void addRoaming(Roaming roaming) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(roaming);
        session.getTransaction().commit();
        session.close();
    }

    public void updateRoaming(Roaming roaming) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(roaming);
        session.getTransaction().commit();
        session.close();
    }

    public Roaming findRoaming(int idroaming) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Roaming roaming = session.get(Roaming.class, idroaming);
        session.close();
        return roaming;
    }

    public Roaming findRoaming(String roamingname) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Roaming roaming = session.get(Roaming.class, roamingname);
        session.close();
        return roaming;
    }

    public ArrayList<Roaming> getRoaming() {
        ArrayList<Roaming> roamings = (ArrayList<Roaming>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From Roaming").list();
        return roamings;
    }

    public Object findRoaming(Class className, SingularAttribute attr, String name) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery(className);
        Root<Object> root = criteriaQuery.from(className);
        EntityType type = session.getMetamodel().entity(className);

        criteriaQuery.select(root).where(criteria.equal(
                root.get(attr), name));
        criteriaQuery.select(root);

        Query<Object> query = session.createQuery(criteriaQuery);
        try {
            Object object = query.getSingleResult();
            if (object != null) return object;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("findRoaming exception: " + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }

}
