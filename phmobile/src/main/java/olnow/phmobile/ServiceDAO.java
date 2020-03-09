package olnow.phmobile;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;

public class ServiceDAO extends RootDAO<Service> {
    Logger logger = LoggerFactory.getLogger(ServiceDAO.class);

    public Service find(Phones phones, String name, String code) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Service> criteriaQuery = criteria.createQuery(Service.class);
        Root<Service> root = criteriaQuery.from(Service.class);

        criteriaQuery.select(root)
                .where(criteria.and(
                    criteria.equal(root.get(Service_.phone), phones),
                    criteria.equal(root.get(Service_.name), name),
                    criteria.equal(root.get(Service_.code), code)));
        criteriaQuery.select(root);

        Query<Service> query = session.createQuery(criteriaQuery);
        try {
            Service obj = query.getSingleResult();
            if (obj != null) return obj;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            logger.error("[find] Hibernate exception, phone: {}, name: {}, code{}", phones, name, code, e);
            throw e;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<Service> get(Phones phones) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Service> criteriaQuery = criteria.createQuery(Service.class);
        Root<Service> root = criteriaQuery.from(Service.class);

        criteriaQuery.select(root)
                .where(criteria.equal(root.get(Service_.phone), phones));
        criteriaQuery.select(root);

        Query<Service> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Service> obj = (ArrayList<Service>) query.getResultList();
            if (obj != null) return obj;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            logger.error("[get] Hibernate exception, phone: {}, name: {}, code{}", phones, e);
            throw e;
        }
        finally {
            session.close();
        }

    }

    @Override
    public ArrayList<Service> get() {
        return get("service");
    }
}
