package ru.keich.mon.automation.snmp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SnmpRepository extends JpaRepository<SnmpScriptMapping, String> {

}
