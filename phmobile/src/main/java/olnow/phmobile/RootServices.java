package olnow.phmobile;

import org.hibernate.HibernateException;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

public class RootServices<T> {
    private RootDAO<T> rootDAO = new RootDAO<>();

    public void add(T obj) throws HibernateException {
        rootDAO.add(obj);
    }

    public void update(T obj) throws HibernateException {
        rootDAO.update(obj);
    }

    public T find(Class<T> className, int id) throws HibernateException {
        return rootDAO.find(className, id);
    }

    public T findName(Class<T> tClass, String name) throws HibernateException {
        return rootDAO.findName(tClass, name);
    }

    public ArrayList<T> get(String tableName) throws HibernateException {
        return rootDAO.get(tableName);
    }

    public ArrayList<T> get() throws  HibernateException {
        return rootDAO.get();
    }

    public T find(Class className, SingularAttribute attr, String name) throws HibernateException {
        return rootDAO.find(className, attr, name);
    }

}