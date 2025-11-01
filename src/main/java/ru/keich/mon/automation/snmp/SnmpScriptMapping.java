package ru.keich.mon.automation.snmp;

import org.snmp4j.smi.OID;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SnmpScriptMapping {

	@Id
	private String name;
	
	@Convert(converter = SnmpOIDConverter.class)
	private OID matchOid;
	
	@Convert(converter = SnmpOIDConverter.class)
	private OID compareOid;
	
	private String scriptName;
	private boolean enable;

	public boolean isValid() {
		try {
			if(name == null || "".equals(name)) {
				return false;
			}
			
			if(matchOid == null) {
				return false;
			}
			if(compareOid == null) {
				return false;
			}			
			if(scriptName == null || "".equals(scriptName)) {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public SnmpScriptMapping setName(String name) {
		this.name = name;
		return this;
	}

	public SnmpScriptMapping setCompareOid(String str) {
		try {
			this.compareOid = new OID(str);
		} catch (RuntimeException e) {
			this.compareOid = null;
		}
		return this;
	}

	public SnmpScriptMapping setMatchOid(String str) {
		try {
			this.matchOid = new OID(str);
		} catch (RuntimeException e) {
			this.matchOid = null;
		}
		return this;
	}

	public SnmpScriptMapping setScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public SnmpScriptMapping setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
	
}
