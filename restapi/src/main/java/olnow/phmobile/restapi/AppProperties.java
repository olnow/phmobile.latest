package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AppProperties {
    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    private static Context ctx;

    private enum DefaultValues {
        HIBERNATE_CONFIG_FILE("java:comp/env/hibernate.config.file", null),
        LDAP_DOMAIN("java:comp/env/ldap_domain", "example.ru"),
        LDAP_URL("java:comp/env/ldap_url", "ldaps://dc.example.ru/"),
        LDAP_BASE("java:comp/env/ldap_base", "DC=example,DC=ru"),
        LDAP_GROUP_SUFFIX("java:comp/env/ldap_group_suffix", "OU=ORGANIZATION,DC=example,DC=ru"),
        ALLOWED_USERS("java:comp/env/allowedUsers", "username1,username2"),
        ORIGINS("java:comp/env/origins", "http://localhost:8080"),
        SMTP_PORT("java:comp/env/smtp_port", 587),
        SMTP_HOST("java:comp/env/smtp_host", ""),
        SMTP_AUTH("java:comp/env/smtp_auth", true),
        SMTP_SSL("java:comp/env/smtp_ssl", "false"),
        SMTP_START_TLS("java:comp/env/smtp_start_tls", "true"),
        MAIL_USER_NAME("java:comp/env/mail_user_name", "username"),
        MAIL_PASSWORD("java:comp/env/mail_password", "password"),
        SMTP_DEBUG("java:comp/env/smtp_debug", false),
        MAIL_FROM("java:comp/env/mail_from", "from"),
        MAIL_TO("java:comp/env/mail_to", ""),
        AD_SYNC_USER_NAME("java:comp/env/ad_sync_user_name", "ad_username"),
        AD_SYNC_PASSWORD("java:comp/env/ad_sync_password", "ad_password"),
        AD_SYNC_STATE_CRON("java:comp/env/ad_sync_state_cron", "0 30 3 ? * * *");

        private Object defaultValue;
        private String valuePath;

        DefaultValues(String path, Object o) {
            defaultValue = o;
            valuePath = path;
        }

        String getValuePath() {
            return valuePath;
        }

        Object getDefaultValue() {
            return defaultValue;
        }
    }

    static  {
        try {
            ctx = new InitialContext();
        }
        catch (Exception e) {
            logger.error("[Constructor] Error creating InitialContext", e);
        }
    }

    public static Object getValue(String name) {
        if (ctx == null) {
            logger.error("[getValue] InitialContext is null, name: {}", name);
            return null;
        }
        try {
            DefaultValues o = DefaultValues.valueOf(name);

            Object value = o.getDefaultValue();
            String valuePath = o.getValuePath();
            if (valuePath != null && !valuePath.isEmpty()) {
                try {
                    if ((value = ctx.lookup(valuePath)) != null)
                        return value;
                }
                catch (NamingException e) {
                    logger.info("[getValue lookup] error, use default value {}: {}", name, value);
                }
                catch (NullPointerException e) {
                    logger.info("[getValue lookup] null pointer exception, use default value {}: {} ", name, value);
                }
            }
            return value;
        }
        catch (IllegalArgumentException e) {
            logger.error("[getValue] no such value name: {}", name, e);
        }
        catch (NullPointerException e) {
            logger.error("[getValue] Null Pointer Exception,  value name: {}", name, e);
        }
        return  null;
    }

    public static String getStringValue(String name) {
        return (String) getValue(name);
    }

}
