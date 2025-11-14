package ru.keich.mon.automation.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

public class SnmpTarget {
	 
	private final String key;
	private final OID[] oids;
	private final CommunityTarget<UdpAddress> communityTarget;
	
	public SnmpTarget(String key, String addtargetr, String community, int version, long timeout, OID[] oids) {
		this.key = key;
		int snmpVersion = SnmpConstants.version2c;
		if(version == 1) {
			snmpVersion = SnmpConstants.version1;
		}
		communityTarget = new CommunityTarget<>(new UdpAddress(addtargetr), new OctetString(community));
		communityTarget.setVersion(snmpVersion);
		communityTarget.setTimeout(timeout);
		
		this.oids = oids;
	}

	public String getKey() {
		return key;
	}
	
	public OID[] getOids() {
		return oids;
	}

	public CommunityTarget<UdpAddress> getCommunityTarget() {
		return communityTarget;
	}
	
	
}
