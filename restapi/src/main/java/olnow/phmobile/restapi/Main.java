package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import olnow.phmobile.HibernateSessionFactoryUtil;

//extends SpringBootServletInitializer
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableAsync
// @EnableAutoConfiguration
@ComponentScan(basePackages = "olnow.phmobile")
//@PropertySource("file:${CATALINA_BASE}/conf/Catalina/localhost/web.phmobile.xml")
class Main extends SpringBootServletInitializer {
    private static final Logger rootLogger = LoggerFactory.getLogger(Main.class);
    //@Override
    //protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    //    return application.sources(Main.class);
    //}
    /*
    private final ApplicationContext applicationContext;

    @Autowired
    public Main(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    */
    public static void main(String[] args) {
        //rootLogger.info("Starting Servlet");
        System.out.println("System: Starting Servlet");
        // rootLogger.info(hibernateConfigFile);
        //debug
        //if (rootLogger.isDebugEnabled()) {
        //    rootLogger.debug("RootLogger: In debug message");
        //}
        SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        rootLogger.info("Configure Servlet");
        //System.out.println("System: Configure Servlet");
        //System.out.println(javax.xml.parsers.SAXParserFactory.class.getResource("SAXParserFactory.class"));
        //debug
        if (rootLogger.isDebugEnabled()) {
            rootLogger.debug("RootLogger: In debug message");
        }

        //Init Vars from tomcat config file
        //AppProperties.loadVars();
        //Set hibernate config file path
        HibernateSessionFactoryUtil.setConfig(AppProperties.getStringValue("HIBERNATE_CONFIG_FILE"));
        return application.sources(Main.class);
    }

    /*
    @PostConstruct
    private void setReferralForContext() {
        LdapTemplate ldapTemplate = applicationContext.getBean(LdapTemplate.class);// necessary for LdapContextSource to be created
        LdapContextSource ldapContextSource = applicationContext.getBean(LdapContextSource.class);
        ldapContextSource.setReferral("ignore");
        ldapContextSource.afterPropertiesSet();
    }*/

    /*
    @PostConstruct
    private void setReferralForContext() {
        LdapTemplate ldapTemplate = ap.getBean(LdapTemplate.class);// necessary for LdapContextSource to be created
        LdapContextSource ldapContextSource = applicationContext.getBean(LdapContextSource.class);
        ldapContextSource.setReferral("ignore");
        ldapContextSource.afterPropertiesSet();
    }*/

    /*
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        servletContext.getServletRegistration(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
                .setInitParameter("dispatchOptionsRequest", "true");

    }
    */

    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/


    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        return executor;
    }
}
