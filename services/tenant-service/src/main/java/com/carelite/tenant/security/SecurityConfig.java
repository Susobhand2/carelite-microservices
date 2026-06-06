package com.carelite.tenant.security;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private static final String COGNITO_GROUPS_CLAIM = "cognito:groups";
  private static final String ACCESS_TOKEN_USE = "access";
  private static final String TOKEN_USE_CLAIM = "token_use";

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          List<String> groups = jwt.getClaimAsStringList(COGNITO_GROUPS_CLAIM);
          if (groups == null) {
            return List.<GrantedAuthority>of();
          }

          return groups.stream()
              .filter(Objects::nonNull)
              .map(String::trim)
              .filter(group -> !group.isBlank())
              .map(group -> (GrantedAuthority) new SimpleGrantedAuthority(group))
              .toList();
        });
    return converter;
  }

  @Bean
  public JwtDecoder jwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

    OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> accessTokenValidator =
        jwt -> {
          if (ACCESS_TOKEN_USE.equals(jwt.getClaimAsString(TOKEN_USE_CLAIM))) {
            return OAuth2TokenValidatorResult.success();
          }

          return OAuth2TokenValidatorResult.failure(
              new OAuth2Error("invalid_token", "Only Cognito access tokens are accepted", null));
        };

    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(defaultValidator, accessTokenValidator));
    return decoder;
  }
}
