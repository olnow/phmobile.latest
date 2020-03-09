package olnow.phmobile;

import org.hibernate.HibernateException;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

public interface RootServicesInterface<T> {
    RootDAO<T> getRootDAO();

    default void add(T obj) throws HibernateException {
        getRootDAO().add(obj);
    }

    default void addOrUpdate(T obj) throws HibernateException {
        getRootDAO().addOrUpdate(obj);
    }

    default void update(T obj) throws HibernateException {
        getRootDAO().update(obj);
    }

    default T find(Class<T> className, int id) throws HibernateException {
        return getRootDAO().find(className, id);
    }

    default T findName(Class<T> tClass, String name) throws HibernateException {
        return getRootDAO().findName(tClass, name);
    }

    default ArrayList<T> get(String tableName) throws HibernateException {
        return getRootDAO().get(tableName);
    }

    default ArrayList<T> get() throws  HibernateException {
        return getRootDAO().get();
    }

    default T find(Class className, SingularAttribute attr, String name) throws HibernateException {
        return getRootDAO().find(className, attr, name);
    }

}