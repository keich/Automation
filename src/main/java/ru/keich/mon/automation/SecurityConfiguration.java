package ru.keich.mon.automation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import ru.keich.mon.automation.httplistner.HttpListnerService;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, HttpListnerService httpListnerService) throws Exception {
		// Configure your static resources with public access
		
		var mather = new RequestMatcher() {
			@Override
			public boolean matches(HttpServletRequest request) {
				var arr = request.getRequestURI().split("/", 4);
				if (arr.length > 2 && "httplistner".equals(arr[1])) {
					return httpListnerService.getHttListner(arr[2]).map(h -> h.isEnable() && h.isPermitAllAccess()).orElse(false);
				}
				return false;
			}

		};
		
		http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/public/**").permitAll();
			auth.requestMatchers(mather).permitAll();
		});
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        //this will allow frames with same origin which is much more safe
        http.headers(headers -> headers.frameOptions( frame -> frame.sameOrigin()));
		
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
