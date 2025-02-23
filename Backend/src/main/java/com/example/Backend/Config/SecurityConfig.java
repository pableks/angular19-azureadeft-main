package com.example.Backend.Config;
 
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.Customizer;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
 import org.springframework.web.client.RestTemplate;
 import org.springframework.security.oauth2.jwt.JwtDecoder;
 import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
 import org.springframework.security.oauth2.jwt.JwtDecoders;

 import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
 import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
 import org.springframework.core.convert.converter.Converter;
 import org.springframework.security.core.Authentication;
 import org.springframework.security.oauth2.jwt.Jwt;
 import org.springframework.security.authentication.AbstractAuthenticationToken;
 import org.springframework.security.oauth2.core.OAuth2TokenValidator;
 import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
 import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
 import java.time.Duration;

 @Configuration
 @EnableWebSecurity
 public class SecurityConfig {
 
     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http.cors(Customizer.withDefaults())
             .authorizeHttpRequests(authorize -> authorize
                 .requestMatchers(new AntPathRequestMatcher("/ws-alertas/**")).permitAll()
                 .requestMatchers(new AntPathRequestMatcher("/api/signos-vitales")).permitAll()
                 .anyRequest().authenticated())
             .oauth2ResourceServer(oauth2 -> oauth2
                 .jwt(jwt -> jwt
                     .decoder(jwtDecoder())
                     .jwtAuthenticationConverter((Converter<Jwt, ? extends AbstractAuthenticationToken>) jwtAuthenticationConverter())
                 )
             );
         return http.build();
     }
 
     @Bean
     public JwtDecoder jwtDecoder() {
         String jwkSetUri = "https://duocetcdemo.b2clogin.com/duocetcdemo.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1_demoazureetc_login";
         NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
             .jwsAlgorithm(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256) // Use fully qualified name
             .build();
         
         // Create validators


         return jwtDecoder;
     }
 
     @Bean
     public JwtAuthenticationConverter jwtAuthenticationConverter() {
         JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
         grantedAuthoritiesConverter.setAuthoritiesClaimName("extension_roles");
         grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
         
         var jwtConverter = new JwtAuthenticationConverter();
         jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
         return jwtConverter;
     }
 
     @Bean
     public RestTemplate restTemplate() {
         return new RestTemplate();
     }
 }
