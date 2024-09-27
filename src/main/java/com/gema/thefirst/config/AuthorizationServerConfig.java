package com.gema.thefirst.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

import com.gema.thefirst.config.customgrant.CustomPasswordAuthenticationConverter;
import com.gema.thefirst.config.customgrant.CustomPasswordAuthenticationProvider;
import com.gema.thefirst.config.customgrant.CustomUserAuthorities;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

// Este código configura um servidor de autorização que utiliza OAuth2 para autenticação, gerencia tokens JWT e permite personalizar a geração de tokens. Ele usa uma combinação de serviços em memória e métodos de configuração para garantir que a autenticação e a autorização sejam tratadas adequadamente em uma aplicação que requer segurança robusta.

@Configuration
// @Configuration: Indica que a classe contém métodos de configuração que geram
// beans que serão gerenciados pelo Spring.
public class AuthorizationServerConfig {

  // @Value: Injeta valores de configuração (como clientId, clientSecret e
  // jwtDurationSeconds) a partir de um arquivo de propriedades.
  @Value("${security.client-id}")
  private String clientId;

  @Value("${security.client-secret}")
  private String clientSecret;

  @Value("${security.jwt.duration}")
  private Integer jwtDurationSeconds;

  // @Autowired: Injeta o UserDetailsService, que é usado para buscar informações
  // do usuário para autenticação.
  @Autowired
  private UserDetailsService userDetailsService;

  // Configura o HttpSecurity para o servidor de autorização.
  // Aplica configurações padrão para o OAuth2
  // Define como as solicitações de token de acesso são tratadas, utilizando um
  // CustomPasswordAuthenticationConverter e um
  // CustomPasswordAuthenticationProvider.
  // Habilita o suporte a recursos OAuth2 e configura o servidor de recursos para
  // usar JWT.
  @Bean
  @Order(2)
  public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {

    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    // @formatter:off
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.tokenEndpoint(tokenEndpoint -> tokenEndpoint
				.accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
				.authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService(), tokenGenerator(), userDetailsService, passwordEncoder())));

		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
		// @formatter:on

    return http.build();
  }

  // Cria um serviço de autorização em memória para gerenciar autorizações.
  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  // Cria um serviço para gerenciar consentimentos de autorização, também em
  // memória.
  @Bean
  public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
    return new InMemoryOAuth2AuthorizationConsentService();
  }

  // Define o algoritmo de codificação de senhas, usando o BCrypt.
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Cria um repositório de clientes registrados em memória, configurando um
  // cliente com ID, segredo, escopos e tipo de concessão de autorização.
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    // @formatter:off
		RegisteredClient registeredClient = RegisteredClient
			.withId(UUID.randomUUID().toString())
			.clientId(clientId)
			.clientSecret(passwordEncoder().encode(clientSecret))
			.scope("read")
			.scope("write")
			.authorizationGrantType(new AuthorizationGrantType("password"))
			.tokenSettings(tokenSettings())
			.clientSettings(clientSettings())
			.build();
		// @formatter:on

    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  // Define as configurações do token, como o formato e a duração do token de
  // acesso (definido por jwtDurationSeconds).
  @Bean
  public TokenSettings tokenSettings() {
    // @formatter:off
		return TokenSettings.builder()
			.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
			.accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds))
			.build();
		// @formatter:on
  }

  // ClientSettings clientSettings() e AuthorizationServerSettings
  // authorizationServerSettings(): Criam instâncias de configurações de cliente e
  // servidor de autorização com configurações padrão.
  @Bean
  public ClientSettings clientSettings() {
    return ClientSettings.builder().build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  // Configura um gerador de tokens que utiliza um JwtEncoder e um JwtGenerator.
  // Isso inclui personalizações para os tokens gerados.
  @Bean
  public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
    NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(tokenCustomizer());
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator);
  }

  // Customiza os tokens JWT adicionando informações como autoridades e nome de
  // usuário ao token de acesso.
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
      OAuth2ClientAuthenticationToken principal = context.getPrincipal();
      CustomUserAuthorities user = (CustomUserAuthorities) principal.getDetails();
      List<String> authorities = user.getAuthorities().stream().map(x -> x.getAuthority()).toList();
      if (context.getTokenType().getValue().equals("access_token")) {
        // @formatter:off
				context.getClaims()
					.claim("authorities", authorities)
					.claim("username", user.getUsername());
				// @formatter:on
      }
    };
  }

  // Cria um decodificador JWT que pode ser usado para validar tokens recebidos.
  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  // Cria um conjunto de chaves públicas e privadas RSA para assinatura de tokens.
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  // generateRsa() e generateRsaKey(): Métodos utilitários para gerar um par de
  // chaves RSA (pública e privada) para a assinatura dos tokens.
  private static RSAKey generateRsa() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }
}
