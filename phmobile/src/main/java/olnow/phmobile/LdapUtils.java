package olnow.phmobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class LdapUtils {
    private DirContext dirContext;
    private String base;
    public static final int ACCOUNT_DISABLED = 0;
    public static final  int ACCOUNT_ENABLED = 1;
    public static final  int ACCOUNT_UNKNOWN = 2;
    private int accountControl;
    private String department, title, groups;
    Logger logger = LoggerFactory.getLogger(LdapUtils.class);
    private String groupSuffix = "";

    public String getGroupSuffix() {
        return groupSuffix;
    }

    public void setGroupSuffix(String groupSuffix) {
        this.groupSuffix = groupSuffix;
    }

    private enum UAC_BIT {
        ACCOUNT_DISABLED(0x02),
        ACCOUNT_LOCKOUT(0x0010),
        PASSWORD_EXPIRED(0x800000),
        PASSWORD_CANT_CHANGE(0x0040),
        DONT_EXPIRE_PASSWORD(0x10000);

        private int id;
        UAC_BIT(int id) { this.id = id; }
        public int get() { return id; }
        }

    public static boolean isAccountActive(int uac) {
        return !isBit(uac, UAC_BIT.ACCOUNT_DISABLED);
    }
    public static boolean isBit(int uac, final UAC_BIT uacBit) {
        return (uac & uacBit.get()) == uacBit.get();
    }

    public String getDepartment() { return department; }
    public String getTitle() { return title; }
    public String getGroup() { return groups; }

    public void setBase(String base) {
        this.base = base;
    }

    public LdapUtils(String URL, String user, String pass, String base) {
        try {
            Properties env = new Properties();

            env.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
            env.setProperty(Context.PROVIDER_URL, URL);
            env.setProperty(Context.SECURITY_PRINCIPAL, user);
            env.setProperty(Context.SECURITY_CREDENTIALS, pass);
            env.setProperty(Context.REFERRAL, "ignore");
            env.setProperty("java.naming.ldap.version", "3");
//            env.setProperty(Context.SECURITY_PROTOCOL, "ssl");

            //String baseutf8 = new String(base.getBytes(), "UTF-8");
            setBase(base);
            //setBase(new String(baseutf8.getBytes("UTF-8"), "windows-1251"));

            dirContext = new InitialDirContext(env);
            System.out.println("AD Connection succesfuly");

        }
        catch (Exception e) {
            System.out.println(this.getClass().getName() + "Can't connect to LDAP:" + URL + user + pass + base + e.toString());
            logger.error(this.getClass().getName(), "Can't connect to LDAP:", URL,user, pass, base);
            logger.error(this.getClass().getName(), e);
        }
    }

    public boolean isLdapConnected() {
        return dirContext != null;
    }

    public boolean loadAccoutInfo(String account) {
        if (account == null || account.isEmpty() || dirContext == null)
            return false;
        String searchbase = "sAMAccountName=" + account;// + "," + base;
        SearchControls sc = new SearchControls();
        String[] returnattrs = {"sAMAccountName", "userAccountControl","title", "department", "memberOf"};
        sc.setReturningAttributes(returnattrs);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            NamingEnumeration searchresult = dirContext.search(base,
                    "(& (objectClass=user)(" + searchbase + "))", sc);
            if(searchresult.hasMore()) {
                SearchResult sr = (SearchResult) searchresult.next();
                Attributes attr = sr.getAttributes();
                accountControl = Integer.parseInt(attr.get("userAccountControl").get().toString());
                department = attr.get("department").get().toString();
                title = attr.get("title").get().toString();
                groups = attr.get("memberOf").get().toString();
                return true;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    public int isActiveUser() {
        if (accountControl == 0)
            return ACCOUNT_UNKNOWN;
        if (isAccountActive(accountControl))
            return ACCOUNT_ENABLED;
        else
            return ACCOUNT_DISABLED;
    }

    public int isActiveUser(String account) {
        if (account == null || account.isEmpty() || dirContext == null)
            return ACCOUNT_UNKNOWN;
        String searchbase = "sAMAccountName=" + account;// + "," + base;
        SearchControls sc = new SearchControls();
        String[] returnattrs = {"sAMAccountName", "userAccountControl"};
        sc.setReturningAttributes(returnattrs);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        try {

            NamingEnumeration searchresult = dirContext.search(base,
                    "(& (objectClass=user)(" + searchbase + "))", sc);
            if(searchresult.hasMore()) {
                SearchResult sr = (SearchResult) searchresult.next();
                Attributes attr = sr.getAttributes();

                int accountControl = Integer.parseInt(attr.get("userAccountControl").get().toString());
                if (isAccountActive(accountControl)) {
                    System.out.println("Account: " + account + " is active");
                    return ACCOUNT_ENABLED;
                }
                else {
                    System.out.println("Account: " + account + " is NOT active");
                    return ACCOUNT_DISABLED;
                }

            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return ACCOUNT_UNKNOWN;
    }

    public ArrayList<People> find(String fio) {
        if (fio == null || fio.isEmpty() || dirContext == null)
            return null;
        ArrayList<People> peopleArrayList = new ArrayList<>();
        String searchbase = "cn=" + fio + "*";// + "*," + base;
        SearchControls sc = new SearchControls();
        String[] returnattrs = {"sAMAccountName",
                "userAccountControl",
                "sn", "givenName", "middleName",
                "title", "department",
                "memberOf"};
        sc.setReturningAttributes(returnattrs);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        try {

            NamingEnumeration searchresult = dirContext.search(base,
                    "(& (objectClass=user)(" + searchbase + "))", sc);
            while (searchresult.hasMore()) {
                SearchResult sr = (SearchResult) searchresult.next();
                Attributes attr = sr.getAttributes();

                People people = new People();

                int accountControl = Integer.parseInt(attr.get("userAccountControl").get().toString());
                String account = attr.get("sAMAccountName").get().toString();
                String lastName = attr.get("sn") != null ? attr.get("sn").get().toString(): "";
                String firstName = attr.get("givenName") != null ? attr.get("givenName").get().toString(): "";
                String middleName = attr.get("middleName") != null ? attr.get("middleName").get().toString(): "";
                String title = attr.get("title") != null ? attr.get("title").get().toString(): "";
                String department = attr.get("department") != null ? attr.get("department").get().toString(): "";

                StringBuilder rootDepartment = null;

                if (attr.get("memberOf") != null) {
                    NamingEnumeration<?> memberOf = attr.get("memberOf").getAll();
                    while (memberOf.hasMore()) {
                        String group = (String) memberOf.next();
                        int start;
                        if ((start = group.indexOf(department)) >= 0) {
                            StringBuilder str = new StringBuilder(group.substring(start, group.indexOf(groupSuffix)-1));
                            rootDepartment = new StringBuilder(str.substring(str.lastIndexOf("OU=")+3));
                        }
                    }
                }

                people.setFio(new StringBuilder(lastName)
                        .append(" ")
                        .append(firstName)
                        .append(" ")
                        .append(middleName)
                        .toString());
                people.setPosition(title);
                if (rootDepartment != null) department = rootDepartment.toString();
                people.setAccount(account);
                people.setADState(isAccountActive(accountControl) ? ACCOUNT_ENABLED: ACCOUNT_DISABLED);
                people.setDepartment(department);
                peopleArrayList.add(people);

            }

        } catch (NamingException e) {
            logger.info("[find] not found fio: {}", fio);
        } catch (Exception e) {
            logger.error("[find] exception: {}", fio, e);
        }
        return peopleArrayList;
    }
}
