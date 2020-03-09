package olnow.phmobile;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Arrays;

public class PhonesDAO {
    private String phone_filter = "";
    private String fio_filter = "";
    private int peopleType = -1;
    Logger logger = LoggerFactory.getLogger(PhonesDAO.class);

    public void setPhoneFilter(String filter) {
        phone_filter = filter;
    }
    public void clearPhoneFilter() { phone_filter = null; }
    public void setFIOFilter(String filter) { fio_filter = filter; }
    public void clearFIOFilter() {fio_filter = null; }
    public void setPeopleType(int type) { peopleType = type; }
    public void clearPeopleType() { peopleType = -1; }


    public void addPhone(Phones phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(phone);
        session.getTransaction().commit();
        session.close();
    }

    public void updatePhone(Phones phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(phone);
        session.getTransaction().commit();
        session.close();
    }

    public void updatePhoneAndPeople(Phones phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(phone);
        session.saveOrUpdate(phone.getPeople());
        session.getTransaction().commit();
        session.close();
    }

    public Phones findPhone(String phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Phones> criteriaQuery = criteria.createQuery(Phones.class);
        Root<Phones> root = criteriaQuery.from(Phones.class);
        EntityType type = session.getMetamodel().entity(Phones.class);

        criteriaQuery.select(root).where(criteria.equal(
                root.get(type.getDeclaredSingularAttribute("phone", String.class)),phone));
        criteriaQuery.select(root);

        Query<Phones> query = session.createQuery(criteriaQuery);


        try {
            ArrayList<Phones> ph = (ArrayList<Phones>)query.getResultList();
            if (ph != null) return ph.get(0);
            else return null;
        }
        catch (Exception e) {
            return null;
        }
        finally {
            session.close();
        }
    }

    public Phones findPhone(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Phones.class, id);
    }

    public ArrayList<Phones> getPhones() {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder criteria = session.getCriteriaBuilder();
            CriteriaQuery<Phones> criteriaQuery = criteria.createQuery(Phones.class);
            Root<Phones> root = criteriaQuery.from(Phones.class);
            Predicate[] predicats = null;
            if (phone_filter != null && !phone_filter.equals("")) {
                predicats = new Predicate[1];
                predicats[0] = criteria.like(root.get("phone"),
                                "%" + phone_filter + "%");
            }
            if (fio_filter != null && !fio_filter.equals("")) {
                if (predicats == null) predicats = new Predicate[1];
                else {
                    Predicate[] new_predicats = new Predicate[2];
                    new_predicats[0] = predicats[0];
                    predicats = new_predicats;
                }
                if (fio_filter.equals("null"))
                    predicats[predicats.length - 1] = criteria.isNull(root.get("people"));
                else {
                    Join<Phones, People> peoplejoin = root.join(Phones_.people);
                    predicats[predicats.length - 1] = criteria.like(peoplejoin.get("fio"), "%" + fio_filter + "%");
                }
            }
            if (peopleType >= 0) {
                if (predicats == null) predicats = new Predicate[1];
                else {
                    Predicate[] new_predicats = new Predicate[predicats.length+1];
                    for (int i = 0; i < predicats.length; i++) {
                        new_predicats[i] = predicats[i];
                    }
                    //new_predicats[0] = predicats[0];
                    predicats = new_predicats;
                }
                Join<Phones, People> peoplejoin = root.join(Phones_.people);
                predicats[predicats.length - 1] = criteria.equal(peoplejoin.get("adstate"),peopleType);
            }
            if (predicats != null)
                criteriaQuery.select(root).where(predicats);
            else
                criteriaQuery.select(root);
            Query<Phones> query = session.createQuery(criteriaQuery);
            ArrayList<Phones> phones = (ArrayList<Phones>) query.getResultList();
            session.close();
            return phones;
        }
        catch (Exception e) {
            logger.error("getPhones error", e);
            System.out.println(this.getClass().getName() + ": " +
                    e.toString() + ": " +
                    Arrays.toString(e.getCause().getStackTrace()));
        }

        return null;
    }

}
