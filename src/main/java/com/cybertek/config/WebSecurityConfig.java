package com.cybertek.config;

//import com.cybertek.service.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    private SecurityFilter securityFilter;
//
//    public WebSecurityConfig(SecurityFilter securityFilter) {
//        this.securityFilter = securityFilter;
//    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception { // --> specific to API security
        return super.authenticationManagerBean();
    }

    private static final String[] permittedURLs ={ //--> a different way to create a list of publicly allowed accesses
            "/authenticate",
            "/create-user",
            "/api/p1/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(permittedURLs)
                .permitAll()
                .anyRequest()
                .authenticated();

//        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
