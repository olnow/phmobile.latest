package olnow.phmobile;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryDAO {
    Logger logger = LoggerFactory.getLogger(HistoryDAO.class);

    public void addHistory(History history) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(history);
        session.getTransaction().commit();
        session.close();
    }

    public void updateHistory(History history) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(history);
        session.getTransaction().commit();
        session.close();
    }

    public ArrayList<History> getHistory() {
        ArrayList<History> history = (ArrayList<History>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From History").list();
        return history;
    }

    public History getActiveHistory(Phones phone) {
        if (phone == null || phone.getPeople() ==  null)
            return null;
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<History> criteriaQuery = criteria.createQuery(History.class);
        Root<History> root = criteriaQuery.from(History.class);
        Predicate[] predicates = new Predicate[4];
        predicates[0] = criteria.equal(root.get("phone"), phone.getId());//  root.equals(People_.phones, phone);
        predicates[1] = criteria.equal(root.get("people"), phone.getPeople().getIdpeople());
        predicates[2] = criteria.greaterThanOrEqualTo(root.get("datestart"), new Date().hashCode());
        predicates[3] = criteria.isNull(root.get("dateend"));

        criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<History> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<History> ph = (ArrayList<History>)query.getResultList();
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

    public People getPeopleAtDate(Phones phone, Timestamp month) {
        if (phone == null)
            return null;
        Session session = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder criteria = session.getCriteriaBuilder();
            CriteriaQuery<History> criteriaQuery = criteria.createQuery(History.class);
            Root<History> root = criteriaQuery.from(History.class);
            Calendar endMonth = Calendar.getInstance();
            endMonth.setTimeInMillis(month.getTime());
            endMonth.set(Calendar.DAY_OF_MONTH, endMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            Timestamp endMonthStamp = new Timestamp(endMonth.getTimeInMillis());
            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteria.equal(root.get("phone"), phone.getId());//  root.equals(People_.phones, phone);
            //predicates[1] = criteria.equal(root.get("people"), phone.getPeople().getIdpeople());
            predicates[1] = criteria.lessThanOrEqualTo(root.get("datestart"), endMonthStamp);
            predicates[2] = criteria.or(criteria.isNull(root.get("dateend")), criteria.greaterThanOrEqualTo(root.get("dateend"), month));

            criteriaQuery.select(root).where(predicates);
            //criteriaQuery.select(root);

            Query<History> query = session.createQuery(criteriaQuery);
            try {
                History hist = (History) query.getSingleResult();
                if (hist != null) return hist.getPeople();
                else return null;
            } catch (NoResultException e) {
                return null;
            }
        }
        catch (HibernateException e) {
            System.out.println("HistoryDAO:getPeopleAtDate:" + e.toString());
            return null;
        }
        finally {
            if (session != null ) session.close();
        }
    }

    public ArrayList<History> getHistory(Phones phone) {
        if (phone == null)
            return null;
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<History> criteriaQuery = criteria.createQuery(History.class);
        Root<History> root = criteriaQuery.from(History.class);
        Predicate[] predicates = new Predicate[1];
        predicates[0] = criteria.equal(root.get("phone"), phone.getId());//  root.equals(People_.phones, phone);
        //predicates[1] = criteria.equal(root.get("people"), phone.getPeople().getIdpeople());
        //predicates[2] = criteria.greaterThanOrEqualTo(root.get("datestart"), new Date().hashCode());
        //predicates[3] = criteria.isNull(root.get("dateend"));

        criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<History> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<History> ph = (ArrayList<History>)query.getResultList();
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

    public ArrayList<Object> getLastHistoryWithCashAnalize() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery(Object.class);
        Root<Phones> root = criteriaQuery.from(Phones.class);
        //Root<History> historyRoot = criteriaQuery.from(History.class);
        Join<Phones, History> historyJoin = root.join(Phones_.history, JoinType.LEFT);
        //Root<History> root = criteriaQuery.from(History.class);

        Subquery<Double> sumLastMonth = criteriaQuery.subquery(Double.class);
        sumLastMonth.correlate(root);
        Root<PhoneCash> phoneCashRootMonth = sumLastMonth.from(PhoneCash.class);

        Subquery<Double> sumLastyear = criteriaQuery.subquery(Double.class);
        sumLastyear.correlate(root);
        Root<PhoneCash> phoneCashRootYear = sumLastyear.from(PhoneCash.class);

        Subquery<Double> sumLast6Month = criteriaQuery.subquery(Double.class);
        sumLast6Month.correlate(root);
        Root<PhoneCash> phoneCashRoot6Month = sumLast6Month.from(PhoneCash.class);

        Subquery<Timestamp> lastDateStart = criteriaQuery.subquery(Timestamp.class);
        lastDateStart.correlate(root);
        Root<History> rootLastDateStart = lastDateStart.from(History.class);

        Calendar lastyear = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        lastyear.set(Calendar.DAY_OF_MONTH, 1);
        lastyear.set(Calendar.MINUTE, 0);
        lastyear.set(Calendar.HOUR_OF_DAY, 0);
        lastyear.set(Calendar.SECOND, 0);
        lastyear.set(Calendar.MILLISECOND, 0);
        lastyear.add(Calendar.MONTH, -13);
        //lastyear.roll(Calendar.YEAR, false);
        Timestamp timestampLastYear = new Timestamp(lastyear.getTime().getTime());

        Calendar last6month = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        last6month.set(Calendar.DAY_OF_MONTH, 1);
        last6month.set(Calendar.MINUTE, 0);
        last6month.set(Calendar.HOUR_OF_DAY, 0);
        last6month.set(Calendar.SECOND, 0);
        last6month.set(Calendar.MILLISECOND, 0);
        last6month.add(Calendar.MONTH, -7);
        //last6month.roll(Calendar.YEAR, false);
        //System.out.println("6 month: " + last6month.getTimeInMillis());
        Timestamp timestampLast6Month = new Timestamp(last6month.getTime().getTime());

        Calendar lastmonth = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        lastmonth.set(Calendar.DAY_OF_MONTH, 1);
        lastmonth.set(Calendar.MINUTE, 0);
        lastmonth.set(Calendar.HOUR_OF_DAY, 0);
        lastmonth.set(Calendar.SECOND, 0);
        lastmonth.set(Calendar.MILLISECOND, 0);
        lastmonth.add(Calendar.MONTH, -1);
        //System.out.println("Last month: " + lastmonth.getTime());
        Timestamp timestampLastMonth = new Timestamp(lastmonth.getTime().getTime());

        sumLastMonth
                .select(criteria.sum(phoneCashRootMonth.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRootMonth.get("month"), timestampLastMonth)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRootMonth.get(PhoneCash_.phone)));
        sumLast6Month
                .select(criteria.sum(phoneCashRoot6Month.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRoot6Month.get("month"), timestampLast6Month)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRoot6Month.get(PhoneCash_.phone)));
        sumLastyear
                .select(criteria.sum(phoneCashRootYear.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRootYear.get("month"), timestampLastYear)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRootYear.get(PhoneCash_.phone)));

        lastDateStart
                .select(criteria.function("max", Timestamp.class, rootLastDateStart.get(History_.datestart)))
                .where(criteria.equal(historyJoin.get(History_.phone), rootLastDateStart.get(History_.phone)));

        criteriaQuery.multiselect(
                root,
                historyJoin,
                sumLastMonth,
                sumLast6Month,
                sumLastyear)
                .where(criteria.or(criteria.isNull(historyJoin.get(History_.datestart)),
                       criteria.equal(historyJoin.get(History_.datestart), lastDateStart)))
                .orderBy(criteria.asc(root.get(Phones_.phone)));

        Query query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object> ph = (ArrayList<Object>)query.getResultList();
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

    public ArrayList<Object> getLastHistoryWithCashAnalizeAndServices() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery(Object.class);
        Root<Phones> root = criteriaQuery.from(Phones.class);

        //Root<History> historyRoot = criteriaQuery.from(History.class);
        Join<Phones, History> historyJoin = root.join(Phones_.history, JoinType.LEFT);
        //ListJoin <Phones, Service> serviceJoin = root.joinList("services", JoinType.LEFT);
        //root.fetch(Phones_.services, JoinType.LEFT);
        //Root<History> root = criteriaQuery.from(History.class);

        Subquery<Double> sumLastMonth = criteriaQuery.subquery(Double.class);
        sumLastMonth.correlate(root);
        Root<PhoneCash> phoneCashRootMonth = sumLastMonth.from(PhoneCash.class);

        Subquery<Double> sumLastyear = criteriaQuery.subquery(Double.class);
        sumLastyear.correlate(root);
        Root<PhoneCash> phoneCashRootYear = sumLastyear.from(PhoneCash.class);

        Subquery<Double> sumLast6Month = criteriaQuery.subquery(Double.class);
        sumLast6Month.correlate(root);
        Root<PhoneCash> phoneCashRoot6Month = sumLast6Month.from(PhoneCash.class);

        Subquery<Timestamp> lastDateStart = criteriaQuery.subquery(Timestamp.class);
        lastDateStart.correlate(root);
        Root<History> rootLastDateStart = lastDateStart.from(History.class);

        Calendar lastyear = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        lastyear.set(Calendar.DAY_OF_MONTH, 1);
        lastyear.set(Calendar.MINUTE, 0);
        lastyear.set(Calendar.HOUR_OF_DAY, 0);
        lastyear.set(Calendar.SECOND, 0);
        lastyear.set(Calendar.MILLISECOND, 0);
        lastyear.add(Calendar.MONTH, -13);
        //lastyear.roll(Calendar.YEAR, false);
        Timestamp timestampLastYear = new Timestamp(lastyear.getTime().getTime());

        Calendar last6month = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        last6month.set(Calendar.DAY_OF_MONTH, 1);
        last6month.set(Calendar.MINUTE, 0);
        last6month.set(Calendar.HOUR_OF_DAY, 0);
        last6month.set(Calendar.SECOND, 0);
        last6month.set(Calendar.MILLISECOND, 0);
        last6month.add(Calendar.MONTH, -7);
        //last6month.roll(Calendar.YEAR, false);
        //System.out.println("6 month: " + last6month.getTimeInMillis());
        Timestamp timestampLast6Month = new Timestamp(last6month.getTime().getTime());

        Calendar lastmonth = Calendar.getInstance();
        //lastyear.set(Calendar.MONTH, 10);
        lastmonth.set(Calendar.DAY_OF_MONTH, 1);
        lastmonth.set(Calendar.MINUTE, 0);
        lastmonth.set(Calendar.HOUR_OF_DAY, 0);
        lastmonth.set(Calendar.SECOND, 0);
        lastmonth.set(Calendar.MILLISECOND, 0);
        lastmonth.add(Calendar.MONTH, -1);
        //System.out.println("Last month: " + lastmonth.getTime());
        Timestamp timestampLastMonth = new Timestamp(lastmonth.getTime().getTime());

        sumLastMonth
                .select(criteria.sum(phoneCashRootMonth.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRootMonth.get("month"), timestampLastMonth)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRootMonth.get(PhoneCash_.phone)));
        sumLast6Month
                .select(criteria.sum(phoneCashRoot6Month.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRoot6Month.get("month"), timestampLast6Month)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRoot6Month.get(PhoneCash_.phone)));
        sumLastyear
                .select(criteria.sum(phoneCashRootYear.get(PhoneCash_.sum)))
                .where(criteria.and(
                        criteria.greaterThanOrEqualTo(phoneCashRootYear.get("month"), timestampLastYear)),
                        criteria.equal(historyJoin.get(History_.phone), phoneCashRootYear.get(PhoneCash_.phone)));

        lastDateStart
                .select(criteria.function("max", Timestamp.class, rootLastDateStart.get(History_.datestart)))
                .where(criteria.equal(historyJoin.get(History_.phone), rootLastDateStart.get(History_.phone)));

        criteriaQuery.multiselect(
                root,
                historyJoin,
                sumLastMonth,
                sumLast6Month,
                sumLastyear)
                .where(criteria.or(criteria.isNull(historyJoin.get(History_.datestart)),
                        criteria.equal(historyJoin.get(History_.datestart), lastDateStart)))
                .orderBy(criteria.asc(root.get(Phones_.phone)));

        Query query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object> ph = (ArrayList<Object>)query.getResultList();
            if (ph != null) {
                return ph;
            }
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            logger.error("[getLastHistoryWithCashAnalizeAndServices] execute query ", e);
            return null;
        }
        finally {
            session.close();
        }
    }


}
