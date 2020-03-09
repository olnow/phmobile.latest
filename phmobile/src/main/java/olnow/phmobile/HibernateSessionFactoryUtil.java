package olnow.phmobile;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;
    private static String config;

    private HibernateSessionFactoryUtil() {}

    private HibernateSessionFactoryUtil(String config) {
        this.config = config;
    }

    public static void setConfig(String conf) {
        config = conf;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration;
                if (config == null || config.isEmpty())
                    configuration = new Configuration().configure();
                else
                    configuration = new Configuration().configure(new File(config));
                //AnnotationConfiguration configuration = new AnnotationConfiguration().
                configuration.addAnnotatedClass(Phones.class);
                configuration.addAnnotatedClass(History.class);
                configuration.addAnnotatedClass(HistoryType.class);
                configuration.addAnnotatedClass(People.class);
                configuration.addAnnotatedClass(PhoneCash.class);
                configuration.addAnnotatedClass(Roaming.class);
                configuration.addAnnotatedClass(CallCategory.class);
                configuration.addAnnotatedClass(Description.class);
                configuration.addAnnotatedClass(Reference.class);
                configuration.addAnnotatedClass(PhoneDetail.class);
                configuration.addAnnotatedClass(Tariff.class);
                configuration.addAnnotatedClass(Contracts.class);
                configuration.addAnnotatedClass(ImportTemplate.class);
                configuration.addAnnotatedClass(ImportHistory.class);
                configuration.addAnnotatedClass(Service.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("Исключение!" + e);
            }
        }
        return sessionFactory;
    }
}