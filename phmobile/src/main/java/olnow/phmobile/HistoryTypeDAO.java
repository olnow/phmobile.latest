package olnow.phmobile;

import org.hibernate.Session;

import java.util.ArrayList;

public class HistoryTypeDAO {
    public void addHistoryType(HistoryType historyType) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(historyType);
        session.getTransaction().commit();
        session.close();
    }

    public void updateHistoryType(HistoryType historyType) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(historyType);
        session.getTransaction().commit();
        session.close();
    }

    public HistoryType findHistoryType(int idhistorytype) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        HistoryType histType = session.get(HistoryType.class, idhistorytype);
        session.close();
        return histType;
    }

    public ArrayList<HistoryType> getHistoryType() {
        ArrayList<HistoryType> historyType = (ArrayList<HistoryType>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From HistoryType").list();
        return historyType;
    }


}
