package ru.keich.mon.automation.script.version;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class ScriptVersion {
	@Id
	private Instant id;
	@Column()
	private int version;
	@Column()
	private String note;
	@Column()
	private String name;
	@Column()
	private String code;
	
	public ScriptVersion(int version, String note,String name,String code) {
		this.id = Instant.now();
		this.version = version;
		this.note = note;
		this.name = name;
		this.code = code;
	}
}