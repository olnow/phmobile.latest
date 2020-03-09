package olnow.phmobile;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class PhoneDetailDAO {
    Session temporarySession;

    public void openSession() {
        temporarySession = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        temporarySession.beginTransaction();
    }

    public void closeSession() {
        //temporarySession.getTransaction().commit();
        temporarySession.close();
    }

    public void commit() {
        temporarySession.getTransaction().commit();
    }

    public void addPhoneDetail(PhoneDetail phoneDetail) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(phoneDetail);
            session.getTransaction().commit();
            session.close();
        }
        catch (Exception e) {
            System.out.println("addPhoneDetail exception: " + e.toString());
        }
    }

    public void addPhoneDetailToCurrentSession(PhoneDetail phoneDetail) {
        try {
            //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            //session.beginTransaction();
            if (temporarySession != null)
                temporarySession.save(phoneDetail);
            //session.getTransaction().commit();
            //session.close();
        }
        catch (Exception e) {
            System.out.println("ToCurrentSession exception: " + e.toString());
        }
    }

    public void updatePhoneDetail(PhoneDetail phoneDetail) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(phoneDetail);
        session.getTransaction().commit();
        session.close();
    }

    public PhoneDetail findPhoneDetail(int idphonedetail) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        PhoneDetail phoneDetail = session.get(PhoneDetail.class, idphonedetail);
        session.close();
        return phoneDetail;
    }

    public ArrayList<PhoneDetail> getPhoneDetail() {
        ArrayList<PhoneDetail> phoneDetails = (ArrayList<PhoneDetail>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From PhoneDetail").list();
        return phoneDetails;
    }

    //Type - 0 all, 1 - in, 2 - out
    public ArrayList<PhoneDetail> getPhoneDetailMonth(int year, int month, int selectType) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<PhoneDetail> criteriaQuery = criteria.createQuery(PhoneDetail.class);
        Root<PhoneDetail> root = criteriaQuery.from(PhoneDetail.class);
        Join<PhoneDetail, Description> descriptionJoin = root.join("description", JoinType.LEFT);
        Calendar startMonth = Calendar.getInstance();
        startMonth.set(year, month-1, 1, 0, 0, 0);
        Calendar endMonth = Calendar.getInstance();
        endMonth.set(year, month-1, startMonth.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Timestamp startMonthStamp = new Timestamp(startMonth.getTimeInMillis());
        Timestamp endMonthStamp = new Timestamp(endMonth.getTimeInMillis());
        Predicate[] predicates = new Predicate[selectType == 0 ? 1 : 2];
        if (selectType > 0)
            predicates[1] = criteria.equal(descriptionJoin.get(Description_.direction), Description.DIRECTION_OUT);
        predicates[0] = criteria.between(root.get(PhoneDetail_.datetime), startMonthStamp, endMonthStamp);
        criteriaQuery.select(root).where(predicates);
        criteriaQuery.orderBy(criteria.asc(root.get(PhoneDetail_.phone)),
                criteria.asc(root.get(PhoneDetail_.datetime)));
        Query<PhoneDetail> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<PhoneDetail> phoneDetails = (ArrayList<PhoneDetail>) query.getResultList();
            if (phoneDetails != null) return phoneDetails;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("HistoryDAO:getPhoneDetailMonth:" + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }

    // Return phone detail for month, only local calls, sms and mms
    public ArrayList<PhoneDetail> getPhoneDetailLocalMonth(int year, int month) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<PhoneDetail> criteriaQuery = criteria.createQuery(PhoneDetail.class);
        Root<PhoneDetail> root = criteriaQuery.from(PhoneDetail.class);
        Join<PhoneDetail, Description> descriptionJoin = root.join("description", JoinType.LEFT);
        Calendar startMonth = Calendar.getInstance();
        startMonth.set(year, month-1, 1, 0, 0, 0);
        Calendar endMonth = Calendar.getInstance();
        endMonth.set(year, month-1, startMonth.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Timestamp startMonthStamp = new Timestamp(startMonth.getTimeInMillis());
        Timestamp endMonthStamp = new Timestamp(endMonth.getTimeInMillis());
        Predicate[] predicates = new Predicate[3];
        predicates[0] = criteria.between(root.get(PhoneDetail_.datetime), startMonthStamp, endMonthStamp);
        predicates[1] = criteria.equal(descriptionJoin.get(Description_.direction), Description.DIRECTION_OUT);
        predicates[2] = criteria.or(criteria.equal(descriptionJoin.get(Description_.calltype), Description.CALLTYPE_LOCAL_CALLS),
                                    criteria.equal(descriptionJoin.get(Description_.calltype), Description.CALLTYPE_LOCAL_SMS),
                                    criteria.equal(descriptionJoin.get(Description_.calltype), Description.CALLTYPE_LOCAL_MMS));
        criteriaQuery.select(root).where(predicates);
        criteriaQuery.orderBy(criteria.asc(root.get(PhoneDetail_.phone)),
                criteria.asc(root.get(PhoneDetail_.datetime)));
        Query<PhoneDetail> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<PhoneDetail> phoneDetails = (ArrayList<PhoneDetail>) query.getResultList();
            if (phoneDetails != null) return phoneDetails;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("HistoryDAO:getPhoneDetailMonth:" + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }
}
