package ru.keich.mon.automation.snmp;

import org.snmp4j.smi.OID;

import jakarta.persistence.AttributeConverter;

public class SnmpOIDConverter implements AttributeConverter<OID, String> {

	@Override
	public String convertToDatabaseColumn(OID attribute) {
		return attribute.toDottedString();
	}

	@Override
	public OID convertToEntityAttribute(String dbData) {
		return new OID(dbData);
	}
	
}
