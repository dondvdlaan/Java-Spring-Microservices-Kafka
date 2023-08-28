package dev.manyroads.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig  {

    String username ;
    String password ;

    @Autowired
    public SecurityConfig(String username, String password) {
        this.username = "u";
        this.password = "p";
    }

    /*
    User is defined as follows
    */
    @Bean
    public void configure(AuthenticationManagerBuilder auth) throws Exception{

        auth.inMemoryAuthentication()
                .withUser(username).password(password)
                .authorities("USER");
    }

    /*
    All APIs and web pages are protected w Basic authentication
     */
    @Bean
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf(c -> c.disable())
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .httpBasic(withDefaults());
    }
}
