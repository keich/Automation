package ru.keich.mon.automation.snmp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;

import lombok.extern.java.Log;
import ru.keich.mon.automation.schedule.ScheduleService;

@Service
@Log
public class SnmpService {
	
	public final String SNMPTRAP_PEER_ADDRESS_KEY = "peerAddress";
	public final String SNMPTRAP_SECURITY_NAME_KEY = "securityName";
	public final String SNMPTRAP_TYPE_KEY = "type";
	public final String SNMPTRAP_TYPE_V1TRAP_VALUE = "V1TRAP";
	public final String SNMPTRAP_TYPE_INFORM_VALUE = "INFORM";
	public final String SNMPTRAP_TYPE_TRAP_VALUE = "TRAP";
	
	private final DefaultUdpTransportMapping transport;
	private final TreeUtils treeUtils;
	private final SnmpRepository snmpRepository;
	private final Snmp snmp;
	private final String snmpAddr;
	
	private final Map<String, SnmpScriptMapping> cache = new ConcurrentHashMap<>();

	public SnmpService(SnmpRepository snmpRepository, @Value("${snmp.interface:udp:127.0.0.1/162}") String snmpAddr) throws IOException {
		this.snmpRepository = snmpRepository;
		this.snmpAddr = snmpAddr;

		snmpRepository.findAll().forEach(sm -> {
			if(sm.isEnable()) {
				cache.put(sm.getName(), sm);
			} else {
				cache.remove(sm.getName());
			}
		});
		
		transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		
		snmp.listen();
	}
	
	public void setScheduleService(ScheduleService scheduleService) {
		var trapProcess = new CommandResponder() {
			@Override
			public synchronized <A extends Address> void processPdu(CommandResponderEvent<A> event) {
				getScriptName(event)
						.map(SnmpScriptMapping::getScriptName)
						.ifPresent(scriptName -> {
							scheduleService.execute(scriptName, snmpTrapToMap(event), l -> {});
						});
			}
		};
		
		snmp.addNotificationListener(GenericAddress.parse(snmpAddr), trapProcess);
	}
	
	private <A extends Address> Optional<SnmpScriptMapping> getScriptName(CommandResponderEvent<A> event) {
		var pdu = event.getPDU();
		if (pdu == null) {
			log.warning("SNMP TRAP - PDU is null. From: " + event.getPeerAddress().toString());
			return Optional.empty();
		}
		return cache.entrySet().stream()
				.filter(e -> isMatchOID(pdu, e.getValue()))
				.map(Map.Entry::getValue)
				.findFirst();
	}
	
	private boolean isMatchOID(PDU pdu, SnmpScriptMapping sm) {
		if (pdu == null) {
			return false;
		}
		var o = pdu.getVariable(sm.getMatchOid());
		if (o instanceof OID) {
			var oid = (OID) o;
			if (oid.leftMostCompare(sm.getCompareOid().size(), sm.getCompareOid()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private <A extends Address> Map<String, Object> snmpTrapToMap(CommandResponderEvent<A> event) {
		var data = new HashMap<String, Object>();
		data.put(SNMPTRAP_PEER_ADDRESS_KEY, event.getPeerAddress().toString());
		data.put(SNMPTRAP_SECURITY_NAME_KEY, new String(event.getSecurityName(), Charset.defaultCharset()));

		var source = event.getPDU();
		if (source == null) {
			return data;
		}
		if (source.getType() == PDU.V1TRAP) {
			data.put(SNMPTRAP_TYPE_KEY, SNMPTRAP_TYPE_V1TRAP_VALUE);
			var sourceV1 = (PDUv1) source;
			var oid = new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.getTrapOID(sourceV1.getEnterprise(),
					sourceV1.getGenericTrap(), sourceV1.getSpecificTrap()));
			data.put(SnmpConstants.sysUpTime.toString(), sourceV1.getTimestamp());
			data.put(SnmpConstants.snmpTrapOID.toString(), oid);
			data.put(SnmpConstants.snmpTrapAddress.toString(), sourceV1.getAgentAddress());
			data.put(SnmpConstants.snmpTrapEnterprise.toString(), sourceV1.getEnterprise());
			data.put(SnmpConstants.snmpTrapCommunity.toString(),
					new String(event.getSecurityName(), Charset.defaultCharset()));
		} else if (source.getType() == PDU.INFORM) {
			data.put(SNMPTRAP_TYPE_KEY, SNMPTRAP_TYPE_INFORM_VALUE);
		} else if (source.getType() == PDU.TRAP) {
			data.put(SNMPTRAP_TYPE_KEY, SNMPTRAP_TYPE_TRAP_VALUE);
		}

		source.getAll().forEach(b -> {
			b.getVariable();
			data.put(b.getOid().toString(), b.getVariable());
		});
		return data;
	}

	public void walk(List<SnmpTarget> targets, Consumer<SnmpResult> consumer) {
		var listner = new SnmpListner();
		targets.forEach(target -> {
			var key = target.getKey();
			listner.addForWaiting(key);
			treeUtils.walk(target.getCommunityTarget(), target.getOids(), key, listner);
		});
		synchronized (listner) {
			while (!listner.isFinished()) {
				try {
					listner.wait();
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Walk retrieval interrupted: " + e.getMessage());
				}
				while (listner.hasFinished()) {
					consumer.accept(listner.getFinished());
				}
			}
			while (listner.hasFinished()) {
				consumer.accept(listner.getFinished());
			}
		}
	}

	public Stream<SnmpScriptMapping> getAll(Query<SnmpScriptMapping, Void> q) {
		return snmpRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}

	public int getCount(Query<SnmpScriptMapping, Void> q) {
		return Math.toIntExact(snmpRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}

	public void save(SnmpScriptMapping sm) {
		snmpRepository.save(sm);
		if(sm.isEnable()) {
			cache.put(sm.getName(), sm);
		} else {
			cache.remove(sm.getName());
		}
	}

	public void delete(SnmpScriptMapping sm) {
		snmpRepository.delete(sm);
		cache.remove(sm.getName());
	}

}
