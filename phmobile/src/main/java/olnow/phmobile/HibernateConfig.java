package olnow.phmobile;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {
    @Bean
    public SessionFactory sessionFactory() {
        return HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Bean
    public DataSource dataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup("java:/comp/env/jdbc/mobile");
    }

    @Bean
    @Autowired
    public HibernateTransactionManager hibernateTransactionManager(
            SessionFactory sessionFactory) {

        HibernateTransactionManager txManager
                = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }
}
