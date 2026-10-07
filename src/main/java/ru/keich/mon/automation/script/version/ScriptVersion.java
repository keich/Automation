package ru.keich.mon.automation.script.version;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.version.dataProviderDto.ScriptVersionSave;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
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
	
	public static ScriptVersion fromScript(ScriptVersionSave svs) {
		return new ScriptVersion(0,svs.note(),svs.script().getName(),svs.script().getCode());
	}
}