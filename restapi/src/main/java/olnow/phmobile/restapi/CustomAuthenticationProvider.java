package olnow.phmobile.restapi;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;


@Component
class CustomAuthenticationProvider implements AuthenticationProvider {
    //@Autowired
    private ActiveDirectoryLdapAuthenticationProvider adAuthProvider;

    public CustomAuthenticationProvider() {
        adAuthProvider =
                new ActiveDirectoryLdapAuthenticationProvider(
                        AppProperties.getStringValue("LDAP_DOMAIN"),
                        AppProperties.getStringValue("LDAP_URL"),
                        AppProperties.getStringValue("LDAP_BASE"));
        adAuthProvider.setUseAuthenticationRequestCredentials(true);
        adAuthProvider.setAuthoritiesMapper(new NullAuthoritiesMapper());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (AppProperties.getStringValue("ALLOWED_USERS").toLowerCase().contains(username.toLowerCase())) {
            return adAuthProvider.authenticate(authentication);
                    //new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
        } else {
            throw new BadCredentialsException("Authentication failed");
        }
    }
    @Override
    public boolean supports(Class<?>aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
