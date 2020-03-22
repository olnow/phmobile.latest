package olnow.phmobile.restapi;

import olnow.phmobile.People;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import olnow.phmobile.LdapUtils;
import olnow.phmobile.Phones;
import olnow.phmobile.PhonesMobile;

import java.util.ArrayList;

@Service
class ADUtils {
    Logger logger = LoggerFactory.getLogger(ADUtils.class);
    PhonesMobile phmobileServices = new PhonesMobile();
    //LdapUtils ldapUtils;

    @Autowired
    Mail mail;

    private LdapUtils initLdap() {
        String username = null;
        String password = null;
        if (StringUtils.isBlank((String) AppProperties.getValue("AD_SYNC_USER_NAME")) ||
                StringUtils.isBlank((String) AppProperties.getValue("AD_SYNC_PASSWORD"))) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                Object credentials = authentication.getCredentials();

                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername() + "@" + (String) AppProperties.getValue("LDAP_DOMAIN");
                    password = credentials == null ? null : credentials.toString();
                } else {
                    username = principal.toString();
                }
                if (password == null) {
                    logger.error("[syncADState] empty ldap username/password: ", principal);
                }
            }
            else {
                logger.error("[syncADState] empty ldap username/password, empty auth object");
            }
        }
        else {
            username = AppProperties.getStringValue("AD_SYNC_USER_NAME");
            password = AppProperties.getStringValue("AD_SYNC_PASSWORD");
        }

        LdapUtils ldapUtils = new LdapUtils(AppProperties.getStringValue("LDAP_URL"),
                username, password,
                AppProperties.getStringValue("LDAP_BASE"));
        return ldapUtils;
    }

    public boolean syncADState() {
        LdapUtils ldapUtils = initLdap();
        if (ldapUtils == null) return false;

        ArrayList<Phones> inactivePhones = phmobileServices.checkADAllPeople(ldapUtils);

        //Send report to EMail
        if (inactivePhones != null && inactivePhones.size() > 0) {
            StringBuilder subject = new StringBuilder("Отчет о заблокированных пользователях");

            StringBuilder text = new StringBuilder("Здравствуйте!")
                    .append("\n")
                    .append("\nПеречень заблокированных пользователей:");
            inactivePhones.forEach(phone -> {
                text.append("\n")
                        .append(phone.getPhone());
                if (phone.getPeople() != null)
                    text.append(": ")
                            .append(phone.getPeople().getFio())
                            .append(" [")
                            .append(phone.getPeople().getDepartment())
                            .append(", ")
                            .append(phone.getPeople().getPosition())
                            .append("]");
            });
            text.append("\n")
                    .append("\n---")
                    .append("\nНе отвечайте на данное сообщение,")
                    .append("\nсообщение сформированно автоматически.");

            mail.sendEMail(AppProperties.getStringValue("MAIL_TO"), subject.toString(), text.toString());
        }
        return true;
    }

    public ArrayList<People> find(String fio) {
        LdapUtils ldapUtils = initLdap();
        if (ldapUtils == null) return null;
        ldapUtils.setGroupSuffix(AppProperties.getStringValue("LDAP_GROUP_SUFFIX"));

        return ldapUtils.find(fio);
    }
}
