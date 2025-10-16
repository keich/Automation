package ru.keich.mon.automation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;


@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Configure your static resources with public access
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/public/**").permitAll());

		// Configure Vaadin's security using VaadinSecurityConfigurer
		http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
			// This is important to register your login view to the
			// navigation access control mechanism:
			configurer.loginView(LoginView.class);

			// You can add any possible extra configurations of your own
			// here (the following is just an example):
			// configurer.enableCsrfConfiguration(false);
		});

		return http.build();
	}
}
