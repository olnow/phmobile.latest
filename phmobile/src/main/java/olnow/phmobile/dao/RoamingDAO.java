package olnow.phmobile.dao;

import olnow.phmobile.IRoamingDAO;
import olnow.phmobile.Roaming;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("RoamingDAO")
public class RoamingDAO extends RootDAOImpl<Roaming> implements IRoamingDAO<Roaming> {

    RoamingDAO() {
        super(Roaming.class);
    }

    /*
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


    public Object findRoaming(SingularAttribute attr, String name) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery((Class) tClass);
        Root<Object> root = criteriaQuery.from((Class) tClass);
        EntityType type = session.getMetamodel().entity((Class) tClass);

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
    }*/

}
