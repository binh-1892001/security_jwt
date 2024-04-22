package com.ra.config;

import com.ra.constants.RoleName;
import com.ra.security.jwt.AccessDenied;
import com.ra.security.jwt.JwtEntrypoint;
import com.ra.security.jwt.JwtTokenFilter;
import com.ra.security.user_principal.UserDetailServiceCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtEntrypoint jwtEntrypoint;
	private final AccessDenied accessDenied;
	private final UserDetailServiceCustom userDetailServiceCustom;
	private final JwtTokenFilter jwtTokenFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				  .cors(auth -> auth.configurationSource(request -> {
					  CorsConfiguration config = new CorsConfiguration();
					  config.setAllowedOrigins(List.of("http://localhost:5173/"));
					  config.setAllowedHeaders(List.of("*"));
					  config.setAllowCredentials(true);
					  config.setAllowedMethods(List.of("*"));
					  config.setExposedHeaders(List.of("Authorization"));
					  return config;
				  }))
				  .csrf(AbstractHttpConfigurer::disable)
				  .authorizeHttpRequests(
							 request -> request.requestMatchers("/auth/**").permitAll()
										.requestMatchers("/admin/**").hasAuthority(String.valueOf(RoleName.ROLE_ADMIN))
										.requestMatchers("/user/**").hasAuthority(String.valueOf(RoleName.ROLE_USER))
										.anyRequest().authenticated()
				  )
				  .exceptionHandling(
							 exception -> exception.authenticationEntryPoint(jwtEntrypoint)
										.accessDeniedHandler(accessDenied)
				  )
				  .authenticationProvider(authenticationProvider())
				  .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				  .build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailServiceCustom);
		return provider;
	}
	
}
