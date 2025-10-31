package ru.keich.mon.automation.snmp;

import org.snmp4j.smi.OID;

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
	private String enterprise;
	private String specificTrap;
	private String genericTrap;
	private String scriptName;
	private boolean enable;

	public boolean isValid() {
		try {
			if(name == null || "".equals(name)) {
				return false;
			}
			if(enterprise == null || "".equals(enterprise)) {
				return false;
			}
			if(!"*".equals(enterprise)) {
				try {
					new OID(enterprise);
				} catch (RuntimeException e) {
					return false;
				}
			}
			if(specificTrap == null) {
				return false;
			}
			if(!"*".equals(specificTrap)) {
				var num = Integer.valueOf(specificTrap);
				if(num < 0) {
					return false;
				}
			}
			if(genericTrap == null) {
				return false;
			}
			if(!"*".equals(genericTrap)) {
				var num = Integer.valueOf(genericTrap);
				if(num < 0 || num > 6) {
					return false;
				}
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

	public SnmpScriptMapping setEnterprise(String enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public SnmpScriptMapping setSpecificTrap(String specificTrap) {
		this.specificTrap = specificTrap;
		return this;
	}

	public SnmpScriptMapping setGenericTrap(String genericTrap) {
		this.genericTrap = genericTrap;
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
