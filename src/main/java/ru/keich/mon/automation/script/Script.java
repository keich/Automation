package ru.keich.mon.automation.script;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Script implements Serializable {

	private static final long serialVersionUID = 6605375504690770716L;

	@Id
	private String name;

	private ScriptType type = ScriptType.JS;

	@Column(columnDefinition = "TEXT")
	private String code;

	// private Set<String> child = Collections.emptySet();
	private String parent;

	@Override
	public String toString() {
		return "Script [name=" + name + ", type=" + type + ", code=" + code + "]";
	}

}
