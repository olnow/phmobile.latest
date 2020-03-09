package olnow.phmobile;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class PhoneCashDAO {

    public void addPhoneCash(PhoneCash phoneCash) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(phoneCash);
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ": " +
                    e.toString() + ": " +
                    Arrays.toString(e.getCause().getStackTrace()));
        }
    }

    public void updatePhoneCash(PhoneCash phoneCash) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(phoneCash);
        session.getTransaction().commit();
        session.close();
    }

    public ArrayList<Object[]> getPhoneCashYear(int year) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.multiselect(phonesJoin.get("phone"),
                peopleJoin.get("fio"),
                peopleJoin.get(People_.department),
                criteria.sum(root.get("sum")));
        criteriaQuery.groupBy(phonesJoin.get("phone"), peopleJoin.get("fio"), peopleJoin.get("department"));
        criteriaQuery.orderBy(criteria.asc(peopleJoin.get("fio")));
        Predicate[] predicates = new Predicate[1];
        Calendar datestart = Calendar.getInstance();
        datestart.set(year,Calendar.JANUARY,1, 0, 0, 0);
        //datestart.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        //System.out.println(datestart.getTime().getTime());
        Calendar dateend = Calendar.getInstance();
        dateend.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        //System.out.println(dateend.getTime().getTime());
        //System.out.println(new Timestamp(dateend.getTime().getTime()));
        predicates[0] = criteria.between(root.get("month"),
                new Timestamp(datestart.getTime().getTime()),
                new Timestamp(dateend.getTime().getTime()));

        criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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

    public ArrayList<Object[]> getPhoneCashArray(int year, String phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.multiselect(peopleJoin.get("fio"),
                peopleJoin.get(People_.department),
                root.get("month"),
                root.get("sum"));
        //criteriaQuery.groupBy(phonesJoin.get("phone"), peopleJoin.get("fio"));
        criteriaQuery.orderBy(criteria.asc(root.get("month")));
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteria.equal(phonesJoin.get("phone"),phone);
        predicates[1] = criteria.equal(criteria.function("year", Integer.class, root.get(PhoneCash_.month)), year);

        criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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

    public double getPhoneCash(int year, int month, String phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery(Object.class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        //Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.select(root.get("sum"));
        /*criteriaQuery.multiselect(peopleJoin.get("fio"),
                peopleJoin.get(People_.department),
                root.get("month"),
                root.get("sum"));*/
        //criteriaQuery.groupBy(phonesJoin.get("phone"), peopleJoin.get("fio"));
        // criteriaQuery.orderBy(criteria.asc(root.get("month")));
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteria.equal(phonesJoin.get("phone"),phone);
        predicates[1] = criteria.equal(criteria.function("month", int.class, root.get(PhoneCash_.month)),month);

        criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object> query = session.createQuery(criteriaQuery);
        try {
            Object phoneCashes = query.getSingleResult();
            if (phoneCashes != null) return (double)phoneCashes;
            else return 0;
        }
        catch (Exception e) {
            System.out.println(this.getClass().getName() + ": " +
                    e.toString() + ": " +
                    Arrays.toString(e.getCause().getStackTrace()));
            return 0;
        }
        finally {
            session.close();
        }
    }

    public PhoneCash getPhoneCashDetail(int year, int month, String phone) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<PhoneCash> criteriaQuery = criteria.createQuery(PhoneCash.class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        //Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        //criteriaQuery.select(root.get("sum"));
        /*criteriaQuery.multiselect(peopleJoin.get("fio"),
                peopleJoin.get(People_.department),
                root.get("month"),
                root.get("sum"));*/
        //criteriaQuery.groupBy(phonesJoin.get("phone"), peopleJoin.get("fio"));
        // criteriaQuery.orderBy(criteria.asc(root.get("month")));
        Predicate[] predicates = new Predicate[3];
        predicates[0] = criteria.equal(phonesJoin.get("phone"),phone);
        predicates[1] = criteria.equal(criteria.function("month", int.class, root.get(PhoneCash_.month)),month);
        predicates[2] = criteria.equal(criteria.function("year", int.class, root.get(PhoneCash_.month)),year);

        criteriaQuery.select(root).where(predicates);
        /*
        this.discounts = discounts;
        this.onetime = onetime;
        this.sum = sum;
        this.vat = vat;
        this.fullsum = fullsum;
         */
        /*
        criteriaQuery.multiselect(
                root.get(PhoneCash_.phone),
                root.get(PhoneCash_.people),
                root.get(PhoneCash_.month),
                root.get(PhoneCash_.internationalcalls),
                root.get(PhoneCash_.longcalls),
                root.get(PhoneCash_.localcalls),
                root.get(PhoneCash_.localsms),
                root.get(PhoneCash_.gprs),
                root.get(PhoneCash_.internationalroamingcalls),
                root.get(PhoneCash_.internationalroamingsms),
                root.get(PhoneCash_.internationalgprsroaming),
                root.get(PhoneCash_.internationalroamingcash),
                root.get(PhoneCash_.russiaroamingcalls),
                root.get(PhoneCash_.russiaroamingsms),
                root.get(PhoneCash_.russiaroaminginet),
                root.get(PhoneCash_.russiaroamingtraffic),
                root.get(PhoneCash_.subscriptionfee),
                root.get(PhoneCash_.russiaroamingcalls),
                root.get(PhoneCash_.subscriptionfeeaddon),
                root.get(PhoneCash_.discounts),
                root.get(PhoneCash_.onetime),
                criteria.sum(root.get(PhoneCash_.sum)),
                root.get(PhoneCash_.vat),
                root.get(PhoneCash_.fullsum)
                ).where(predicates);
                */
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<PhoneCash> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<PhoneCash> phoneCashes = (ArrayList<PhoneCash>) query.getResultList();
            if (phoneCashes != null) {
                if (phoneCashes.size() == 1)
                    return phoneCashes.get(0);
                PhoneCash res = new PhoneCash();
                phoneCashes.forEach(phoneCash -> res.sum(phoneCash));
                return res;
            }
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("CashDAO: getPhoneCashDetail: " + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<Object[]> getContractCashInfo() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.multiselect(phonesJoin.get(Phones_.contract),
                root.get(PhoneCash_.month),
                criteria.count(root.get(PhoneCash_.idphonecash)),
                criteria.sum(root.get(PhoneCash_.sum)));
        criteriaQuery.groupBy(phonesJoin.get(Phones_.contract), root.get(PhoneCash_.month));
        criteriaQuery.orderBy(criteria.asc(phonesJoin.get(Phones_.contract)),
                criteria.asc(root.get(PhoneCash_.month)));
        //criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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

    public ArrayList<Object[]> getContractCashAndDetailInfo() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();

        Query<Object[]> query = session.createQuery("select Phones.contract, PhoneCash.month, count(PhoneCash.idphone), " +
                "(select count(PhoneDetail.idphone) from PhoneDetail " +
                "where month(PhoneCash.month) = month(PhoneDetail.datetime) " +
                ") as detail, " +
                "0 as sum " +
                "from PhoneCash " +
                "left join Phones on PhoneCash.idphone = Phones.id " +
                "group by Phones.contract, PhoneCash.month");
        /*
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaBuilder criteriaDetail = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        CriteriaQuery<Object> criteriaQueryDetail = criteria.createQuery(Object.class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Root<PhoneDetail> detailRoot = criteriaQueryDetail.from(PhoneDetail.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));

        criteriaQueryDetail.select(criteriaDetail.count(detailRoot.get(PhoneDetail_.idphonedetail)));
        criteriaQueryDetail.where(criteriaDetail.equal(
                criteriaDetail.function("month", int.class,
                        detailRoot.get(PhoneDetail_.datetime)),
                criteriaDetail.function("month", int.class,
                        root.get(PhoneCash_.month))));

        criteriaQuery.multiselect(phonesJoin.get(Phones_.contract),
                root.get(PhoneCash_.month),
                criteria.count(root.get(PhoneCash_.idphonecash)),
                criteria.sum(root.get(PhoneCash_.sum)),
                criteriaQueryDetail.subquery(PhoneDetail.class));
        criteriaQuery.groupBy(phonesJoin.get(Phones_.contract), root.get(PhoneCash_.month));
        criteriaQuery.orderBy(criteria.asc(phonesJoin.get(Phones_.contract)),
                criteria.asc(root.get(PhoneCash_.month)));
        //criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        */
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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



    public ArrayList<Object[]> getDepartmentCashYear(int year) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.multiselect(peopleJoin.get(People_.department),
                criteria.sum(root.get("sum")));
        criteriaQuery.groupBy(peopleJoin.get(People_.department));
        criteriaQuery.orderBy(criteria.asc(peopleJoin.get(People_.department)));
        Predicate[] predicates = new Predicate[1];
        Calendar datestart = Calendar.getInstance();
        datestart.set(year,Calendar.JANUARY,1, 0, 0, 0);
        Calendar dateend = Calendar.getInstance();
        dateend.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        predicates[0] = criteria.between(root.get("month"),
                new Timestamp(datestart.getTime().getTime()),
                new Timestamp(dateend.getTime().getTime()));

        criteriaQuery.where(predicates);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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


    public ArrayList<Object[]> getPhoneCashMonth(int year, int month, boolean skipZeroSum) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteria.createQuery(Object[].class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        criteriaQuery.multiselect(phonesJoin.get("phone"),
                peopleJoin.get("fio"),
                peopleJoin.get(People_.department),
                criteria.sum(root.get("sum")));
        criteriaQuery.groupBy(phonesJoin.get("phone"),
                peopleJoin.get("fio"),
                peopleJoin.get(People_.department));
        criteriaQuery.orderBy(criteria.asc(peopleJoin.get("fio")));
        Predicate[] predicates = new Predicate[skipZeroSum ? 3 : 2];
        /*Calendar datestart = Calendar.getInstance();
        datestart.set(year,month,1, 0, 0, 0);
        //System.out.println(datestart.getTime().getTime());
        Calendar dateend = Calendar.getInstance();
        dateend.set(year, month, 1, 23, 59, 59);
        //System.out.println(dateend.getTime().getTime());
        //System.out.println(new Timestamp(dateend.getTime().getTime()));
        //predicates[0] = criteria.between(root.get("month"),
        //        new Timestamp(datestart.getTime().getTime()),
        //        new Timestamp(dateend.getTime().getTime()));
        */
        predicates[0] = criteria.equal(criteria.function("month", int.class, root.get(PhoneCash_.month)),month);
        predicates[1] = criteria.equal(criteria.function("year", int.class, root.get(PhoneCash_.month)),year);
        if (skipZeroSum)
            predicates[2] = criteria.notEqual(root.get(PhoneCash_.sum), 0);

        criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object[]> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<Object[]> phoneCashes = (ArrayList<Object[]>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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


    public ArrayList<PhoneCash> getPhoneCashWOPeople() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<PhoneCash> criteriaQuery = criteria.createQuery(PhoneCash.class);
        Root<PhoneCash> root = criteriaQuery.from(PhoneCash.class);
        //Join<PhoneCash, Phones> phonesJoin = root.join("phone", JoinType.LEFT);
        //phonesJoin.on(criteria.equal(root.get("phone"), phonesJoin.get("id")));
        //Join<PhoneCash, People> peopleJoin = root.join("people", JoinType.LEFT);
        //peopleJoin.on(criteria.equal(root.get("people"), peopleJoin.get("idpeople")));
        //criteriaQuery.multiselect(phonesJoin.get("phone"),
        //        peopleJoin.get("fio"),
        //        peopleJoin.get(People_.department),
        //        criteria.sum(root.get("sum")));
        //criteriaQuery.groupBy(phonesJoin.get("phone"), peopleJoin.get("fio"));
        //criteriaQuery.orderBy(criteria.asc(peopleJoin.get("fio")));
        Predicate[] predicates = new Predicate[1];
        //Calendar datestart = Calendar.getInstance();
        //datestart.set(year,Calendar.JANUARY,1, 0, 0, 0);
        //datestart.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        //System.out.println(datestart.getTime().getTime());
        //Calendar dateend = Calendar.getInstance();
        //dateend.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        //System.out.println(dateend.getTime().getTime());
        //System.out.println(new Timestamp(dateend.getTime().getTime()));
        predicates[0] = criteria.isNull(root.get("people"));

        criteriaQuery.where(predicates);
        //criteriaQuery.select(root).where(predicates);
        criteriaQuery.select(root);

        Query<PhoneCash> query = session.createQuery(criteriaQuery);
        try {
            ArrayList<PhoneCash> phoneCashes = (ArrayList<PhoneCash>) query.getResultList();
            if (phoneCashes != null) return phoneCashes;
            else return null;
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

}
