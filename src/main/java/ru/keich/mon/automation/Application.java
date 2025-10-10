package ru.keich.mon.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

@SpringBootApplication
@Push
public class Application implements AppShellConfigurator {

	private static final long serialVersionUID = -551285312005788829L;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
