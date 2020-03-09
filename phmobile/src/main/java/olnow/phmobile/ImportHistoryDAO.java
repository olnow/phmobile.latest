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
import java.util.ArrayList;

public class ImportHistoryDAO extends RootDAO<ImportHistory> {
    Logger logger = LoggerFactory.getLogger(ImportHistoryDAO.class);

    public ImportHistory find(String fileName, int year) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<ImportHistory> criteriaQuery = criteria.createQuery(ImportHistory.class);
        Root<ImportHistory> root = criteriaQuery.from(ImportHistory.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteria.equal(root.get(ImportHistory_.fileName), fileName);
        predicates[1] = criteria.equal(criteria.function("year", int.class, root.get(ImportHistory_.month)), year);

        criteriaQuery.select(root).where(predicates);
        Query<ImportHistory> query = session.createQuery(criteriaQuery);
        try {
            ImportHistory object = query.getSingleResult();
            if (object != null) return object;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            logger.error("[find] fileName: {}, year: {}", fileName, year, e);
            throw e;
        }
        finally {
            session.close();
        }
        return null;
    }

    public ArrayList<ImportHistory> get(int type, int year) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<ImportHistory> criteriaQuery = criteria.createQuery(ImportHistory.class);
        Root<ImportHistory> root = criteriaQuery.from(ImportHistory.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteria.equal(root.get(ImportHistory_.type), type);
        predicates[1] = criteria.equal(criteria.function("year", int.class, root.get(ImportHistory_.month)), year);
        criteriaQuery.orderBy(criteria.asc(root.get(ImportHistory_.month)));

        criteriaQuery.select(root).where(predicates);
        Query<ImportHistory> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<ImportHistory> object = (ArrayList<ImportHistory>) query.getResultList();
            if (object != null) return object;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (HibernateException e) {
            logger.error("[get] type: {}, year: {}", type, year, e);
            throw e;
        }
        finally {
            session.close();
        }
        return null;
    }
}
