package ru.keich.mon.automation.snmp;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;

import lombok.extern.java.Log;

@Service
@Log
public class SnmpService {
	
	private final DefaultUdpTransportMapping transport;
	private final Snmp snmp;
	private final TreeUtils treeUtils;
	private final SnmpRepository snmpRepository;

	public SnmpService(SnmpRepository snmpRepository) throws IOException {
		this.snmpRepository = snmpRepository;
		transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		snmp.listen();
		treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
	}

	public void walk(List<SnmpTarget> targets, Consumer<SnmpResult> consumer) {
		var listner = new SnmpListner();
		targets.forEach(target -> {
			var key = target.getKey();
			listner.addForWaiting(key);
			treeUtils.walk(target.getCommunityTarget(), new OID[] { target.getOid() }, key, listner);
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
	}

	public void delete(SnmpScriptMapping sm) {
		snmpRepository.delete(sm);
	}

}
