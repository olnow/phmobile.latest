package olnow.phmobile;

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CallCategoryDAO {
    /*
    public void addCallCategory(CallCategory callCategory) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(callCategory);
            session.getTransaction().commit();
            session.close();
        }
        catch (Exception e) {
            System.out.println("addCallCategory exception: " + e.toString());
        }
    }

    public void updateCallCategory(CallCategory callCategory) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(callCategory);
        session.getTransaction().commit();
        session.close();
    }

    public CallCategory findCallCategory(int idCallCategory) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CallCategory callCategory = session.get(CallCategory.class, idCallCategory);
        session.close();
        return callCategory;
    }

    public CallCategory findCallCategory(String callCategoryName) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<CallCategory> criteriaQuery = criteria.createQuery(CallCategory.class);
        Root<CallCategory> root = criteriaQuery.from(CallCategory.class);
        EntityType type = session.getMetamodel().entity(CallCategory.class);

        criteriaQuery.select(root).where(criteria.equal(
                root.get(CallCategory_.callcategory), callCategoryName));
        criteriaQuery.select(root);

        Query<CallCategory> query = session.createQuery(criteriaQuery);
        try {
            CallCategory callCategory = (CallCategory) query.getSingleResult();
            if (callCategory != null) return callCategory;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("findCallCategory exception: " + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }

    public ArrayList<CallCategory> getCallCategory() {
        ArrayList<CallCategory> callCategories = (ArrayList<CallCategory>)
                HibernateSessionFactoryUtil.
                        getSessionFactory().
                        openSession().
                        createQuery("From CallCategory").list();
        return callCategories;
    }

    public Object find(Class className, SingularAttribute attr, String name, int type) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder criteria = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteria.createQuery(className);
        Root<Object> root = criteriaQuery.from(className);
        //EntityType type = session.getMetamodel().entity(className);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteria.equal(root.get("idcategorytype"),type);
        predicates[1] = criteria.equal(root.get(attr), name);
        criteriaQuery.select(root).where(predicates);
        //criteriaQuery.select(root);

        Query<Object> query = session.createQuery(criteriaQuery);
        try {
            Object object = query.getSingleResult();
            if (object != null) return object;
            else return null;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.out.println("findRoaming exception: " + e.toString());
            return null;
        }
        finally {
            session.close();
        }
    }
    */
}
