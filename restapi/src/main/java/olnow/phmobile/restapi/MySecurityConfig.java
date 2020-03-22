package olnow.phmobile.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = false)
class MySecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MySecurityConfig.class);
    /*
    private final UserService userService;
    private final ObjectMapper objectMapper;
    */
    public MySecurityConfig(UserService userService, ObjectMapper objectMapper) {
        //this.userService = userService;
        //this.objectMapper = objectMapper;
    }

    @Autowired
    private CustomAccessSuccessHandler accessSuccessHandler;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private SimpleUrlAuthenticationFailureHandler accessFailureHandler =
            new SimpleUrlAuthenticationFailureHandler();

    class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
                throws IOException, ServletException {
            response.getWriter().print("You don't have required role to perform this action.");
            logger.error(this.getClass().getName(), request, arg2);
            //System.out.println(request.getHeader("X-XSRF-TOKEN") + response.getHeader("X-XSRF-TOKEN") + arg2);
        }

    }

    @Component
    class CustomAccessSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
            //implements AuthenticationSuccessHandler {
        private RequestCache requestCache = new HttpSessionRequestCache();

        @Override
        public void onAuthenticationSuccess(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication)
                throws ServletException, IOException {

            logger.info(this.getClass().getName());
            SavedRequest savedRequest
                    = requestCache.getRequest(request, response);

            if (savedRequest == null) {
                clearAuthenticationAttributes(request);
                return;
            }
            String targetUrlParam = getTargetUrlParameter();
            if (isAlwaysUseDefaultTargetUrl()
                    || (targetUrlParam != null
                    && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
                requestCache.removeRequest(request, response);
                clearAuthenticationAttributes(request);
                return;
            }

            clearAuthenticationAttributes(request);
        }

        public void setRequestCache(RequestCache requestCache) {
            this.requestCache = requestCache;
        }
    }

    /*
    class NoRedirectStrategy implements RedirectStrategy {
        @Override
        public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url) throws IOException {
            // No redirect is required with pure REST
        }
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }*/

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        //super.configure(http);
        http.cors()
                .and()
                .csrf().disable()
                //.and()//.disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                //.csrf().csrfTokenRepository(new CookieCsrfTokenRepository().withHttpOnlyFalse())
                //.and()
                .httpBasic().disable()
                .authorizeRequests()
                //.antMatchers("/**").permitAll()
                .antMatchers("/getOptions").permitAll()
                .antMatchers("/**").authenticated() //.permitAll()
                //.antMatchers("/login").fullyAuthenticated()
                .and()
                .formLogin()
                .successHandler(accessSuccessHandler)
                .failureHandler(accessFailureHandler)
                .and()
                .logout()
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .deleteCookies("JSESSIONID")
                .and()
                //.authenticationProvider()
                //.userDetailsService(userService)
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());

        //http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
        //http.csrf().disable();
        // @formatter:on

        /*
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index", "/about").permitAll()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }

         */
    }

    @Configuration
    public static class AuthenticationConfiguration extends
            GlobalAuthenticationConfigurerAdapter {

        @Override
        public void init(AuthenticationManagerBuilder auth) {
            try {
                //String ldapDomain = "ncfu.ru";
                //String ldap_url = "ldaps://dc-3.ncfu.net/";
                //String ldap_base = "DC=ncfu,DC=net";
                /*logger.debug("Ldap auth:");
                ActiveDirectoryLdapAuthenticationProvider ad =
                        new ActiveDirectoryLdapAuthenticationProvider(
                                AppProperties.getLdapDomain(),
                                AppProperties.getLdapUrl(),
                                AppProperties.getLdapBase());
                ad.setUseAuthenticationRequestCredentials(true);

                ad.setAuthoritiesMapper(new NullAuthoritiesMapper());
                */
                auth.eraseCredentials(false);
                //auth.authenticationProvider(ad);
                auth.authenticationProvider(new CustomAuthenticationProvider());
            }
            catch (Exception e) {
                logger.error(this.getClass().getName(), e);
            }
        }
    }

    /*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.debug(true);
    }*/

    /*
    public Authentication authenticate(Authentication auth) {
        try {
            // UsernamePasswordAuthenticationToken userToken =
            //         new UsernamePasswordAuthenticationToken("", "");
            // String ldapDomain = "ncfu.net";
            // String ldap_url = "ldap://dc-3.ncfu.net/";

            //ActiveDirectoryLdapAuthenticationProvider ad =
            //        new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldap_url);
            //auth = ad.authenticate(userToken);
            logger.info(this.getClass().getName(), "Try auth: ", auth.get);
        }
        catch (Exception e) {
            //logger.error(this.getClass().getName(), e);
        }
        return auth;
    }*/

    /*
    public boolean authenticate(String userDn, String credentials) {
        logger.info(this.getClass().getName(), "Try auth: ", userDn);
        return true;
    }*/

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        configuration.setAllowedOrigins(Arrays.asList(AppProperties.getStringValue("ORIGINS")));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS"));
        configuration.addAllowedHeader("Access-Control-Allow-Origin");
        configuration.addAllowedHeader("Access-Control-Allow-Credentials");
        configuration.addAllowedHeader("Content-Type");
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("X-XSRF-TOKEN");
        configuration.addAllowedHeader("X-CSRF-TOKEN");
        //configuration.addAllowedHeader("_csrf");
        configuration.addExposedHeader("X-CSRF-TOKEN");
        //configuration.addExposedHeader("X-XSRF-TOKEN");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
