package ru.keich.mon.automation.schedule;

import org.springframework.scheduling.support.CronExpression;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Schedule {

	@Id
	private String name;
	private String expression;
	private String scriptName;
	private boolean enable;

	public Schedule setName(String name) {
		this.name = name;
		return this;
	}

	public Schedule setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	
	public Schedule setScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public Schedule setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public boolean isValid() {
		return name != null && !"".equals(name) && scriptName != null && !"".equals(scriptName) && CronExpression.isValidExpression(expression);
	}

	@Override
	public String toString() {
		return "Schedule [name=" + name + ", expression=" + expression + "]";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		return name.equals(other.name);
	}

}
