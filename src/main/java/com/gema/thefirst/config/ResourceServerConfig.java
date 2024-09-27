package com.gema.thefirst.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
// import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// Este código configura um servidor de recursos com segurança usando o Spring Security, incluindo o suporte para CORS (Cross-Origin Resource Sharing) e autenticação baseada em JWT (JSON Web Token).
@Configuration
// @Configuration: Indica que a classe contém métodos de configuração que geram
// beans gerenciados pelo Spring.
@EnableWebSecurity
// @EnableWebSecurity: Ativa a segurança da web no aplicativo.
@EnableMethodSecurity
// @EnableMethodSecurity: Permite a segurança em métodos, o que significa que
// você pode usar anotações de segurança em métodos individuais.
public class ResourceServerConfig {
  // @Value("${cors.origins}"): Injeta a configuração de origens permitidas para
  // CORS a partir do arquivo de propriedades.
  @Value("${cors.origins}")
  private String corsOrigins;

  // Configura um filtro de segurança específico para o console H2 (um banco de
  // dados em memória).
  // Desabilita CSRF (Cross-Site Request Forgery) e permite que os frames sejam
  // exibidos no console H2.
  // @Profile("test"): Este bean é ativado apenas quando o perfil "test" está
  // ativo.
  @Bean
  @Profile("test")
  @Order(1)
  public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {

    http.securityMatcher(PathRequest.toH2Console()).csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
    return http.build();
  }

  // Configura a segurança para o servidor de recursos.
  // Desabilita CSRF para simplificar a configuração (embora em um ambiente de
  // produção isso não seja recomendado).
  // Permite todas as requisições sem autenticação (anyRequest().permitAll()).
  // Habilita o servidor de recursos OAuth2 com suporte a JWT.
  // Configura o CORS usando uma fonte de configuração definida em
  // corsConfigurationSource().
  @Bean
  @Order(3)
  public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable());
    http.authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
    http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }

  // Cria um conversor de autenticação JWT.
  // Define um conversor de autoridades que extrai as autoridades do token JWT. A
  // propriedade authorities é usada como o nome do claim para as autoridades e
  // não utiliza um prefixo.@Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
    grantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  // Configura as regras de CORS.
  // Permite todas as origens (*) e métodos HTTP especificados (POST, GET, PUT,
  // DELETE, PATCH).
  // Permite credenciais e define os cabeçalhos permitidos (Authorization,
  // Content-Type).
  // Registra a configuração para todas as URLs (/**).
  @Bean
  CorsConfigurationSource corsConfigurationSource() {

    // String[] origins = corsOrigins.split(",");

    CorsConfiguration corsConfig = new CorsConfiguration();
    // Permitir apenas cors.origins do application.properties
    // corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));
    // Permitir todas as origens
    corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }

  // @Bean
  // FilterRegistrationBean<CorsFilter> corsFilter() {
  // FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
  // new CorsFilter(corsConfigurationSource()));
  // bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
  // return bean;
  // }

  // Cria e retorna um filtro CORS baseado na configuração definida em
  // corsConfigurationSource().
  @Bean
  public CorsFilter corsFilter() {
    return new CorsFilter(corsConfigurationSource());
  }
}
