package dev.manyroads.productcomposite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;


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
                        // Convention OAuth2.0 scopes to be Prefixed with SCOPE_ in Spring Security
                        .pathMatchers(POST, "/composite/**").hasAuthority("SCOPE_product:write")
                        .pathMatchers(GET, "/composite/**").hasAuthority("SCOPE_product:read")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));

        return auth.build();
    }
}
