package dev.manyroads.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /*
     ServerHttpSecurity is similar to Spring Security's HttpSecurity but for WebFlux
     */
    @Bean
    public SecurityWebFilterChain configSecurityWebFilterChain(ServerHttpSecurity auth) throws Exception{

        auth
                .csrf(c -> c.disable())
                .authorizeExchange(a -> a
                        //.pathMatchers("/oauth2/**").permitAll()
                        //.pathMatchers("/login/**").permitAll()
                        //.pathMatchers("/error/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));

        return auth.build();
    }
}
