package olnow.phmobile;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Arrays;

public class PeopleDAO {
    private String people_filter = "";
    Logger logger = LoggerFactory.getLogger(PeopleDAO.class);

    public void setPeopleFilter(String filter) {
        people_filter = filter;
    }
    public void clearPeopleFilter() { people_filter = null; }


    public void addPeople(People people) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(people);
        session.getTransaction().commit();
        session.close();
    }

    public void updatePeople(People people) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(people);
        session.getTransaction().commit();
        session.close();
    }

    public People findPeopleByFirst(String first) {

        return null;
    }

    public People findPeople(String fio) {
        if (fio == null || fio.isEmpty())
            return null;
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<People> criteriaQuery = criteria.createQuery(People.class);
        Root<People> root = criteriaQuery.from(People.class);
        EntityType type = session.getMetamodel().entity(People.class);

        criteriaQuery.select(root).where(criteria.equal(
                root.get(type.getDeclaredSingularAttribute("fio", String.class)),fio));
        criteriaQuery.select(root);

        Query<People> query = session.createQuery(criteriaQuery);
        try {
            People ph = query.getSingleResult();
            if (ph != null) return ph;
            else return null;
        }
        catch (NonUniqueResultException | NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println(this.getClass().getName() + ": " +
                    e.toString() + ": " +
                    Arrays.toString(e.getCause().getStackTrace()));
            return null;
        }
        finally {
            session.close();
        }
    }

    public People findPeople(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(People.class, id);
    }

    public ArrayList<People> getPeople() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<People> criteriaQuery = criteria.createQuery(People.class);
        Root<People> root = criteriaQuery.from(People.class);
        if (people_filter != null && !people_filter.isEmpty())
            criteriaQuery.select(root).where(criteria.like(
                    root.get(People_.fio),"%" + people_filter + "%"));
        //criteriaQuery.select(root);

        Query<People> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<People> ph = (ArrayList<People>)query.getResultList();
            if (ph != null) return ph;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            logger.error("[getPeople] exception", e);
            return null;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<String> getDepartments() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteria.createQuery(String.class);
        Root<People> root = criteriaQuery.from(People.class);
        criteriaQuery.distinct(true);
        criteriaQuery.select(root.get("department"));
        criteriaQuery.orderBy(criteria.asc(root.get("department")));
        Query<String> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<String> ph = (ArrayList<String>)query.getResultList();
            if (ph != null) return ph;
            else return null;
        }
        catch (Exception e) {
            return null;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<String> getPositions() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteria.createQuery(String.class);
        Root<People> root = criteriaQuery.from(People.class);
        criteriaQuery.distinct(true);
        criteriaQuery.select(root.get("position"));
        criteriaQuery.orderBy(criteria.asc(root.get("position")));
        Query<String> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<String> ph = (ArrayList<String>)query.getResultList();
            if (ph != null) return ph;
            else return null;
        }
        catch (Exception e) {
            return null;
        }
        finally {
            session.close();
        }
    }
}
