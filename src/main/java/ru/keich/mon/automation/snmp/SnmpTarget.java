package ru.keich.mon.automation.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

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
