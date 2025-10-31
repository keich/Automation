package ru.keich.mon.automation.snmp.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptDataProvider;
import ru.keich.mon.automation.snmp.SnmpScriptMapping;
import ru.keich.mon.automation.snmp.SnmpService;

public class SnmpEdit extends Div {

	private static final long serialVersionUID = 6198772943856361352L;

	private static final double SPLIT_POS = 20;

	private final SnmpService snmpService;
	private final SnmpRight right;
	private final SnmpLeft left;

	public SnmpEdit(SnmpService snmpService, ScriptService scriptService) {
		super();

		this.setSizeFull();
		this.setHeightFull();

		this.snmpService = snmpService;

		var dataProvider = new ScriptDataProvider(scriptService);
		
		this.right = new SnmpRight(dataProvider, this::save, this::delete);

		this.left = new SnmpLeft(DataProvider.fromCallbacks(snmpService::getAll, snmpService::getCount), right::open,
				right::add);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(SnmpScriptMapping sm) {
		snmpService.save(sm);
		left.refresh();
	}

	private void delete(SnmpScriptMapping sm) {
		snmpService.delete(sm);
		left.refresh();
	}

}
