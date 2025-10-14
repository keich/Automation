package ru.keich.mon.automation.schedule.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.schedule.Schedule;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptDataProvider;

public class ScheduleEdit extends Div {

	private static final long serialVersionUID = -1821458437205081304L;
	
	private static final double SPLIT_POS = 20;

	private final ScheduleService scheduleService;
	private final ScheduleRight right;
	private final ScheduleLeft left;

	public ScheduleEdit(ScheduleService scheduleService, ScriptService scriptService) {
		super();
		
		this.setSizeFull();
		this.setHeightFull();
		
		this.scheduleService = scheduleService;
		var dataProvider = new ScriptDataProvider(scriptService);
		
		this.right = new ScheduleRight(dataProvider, this::save, this::delete);
		this.left = new ScheduleLeft(DataProvider.fromCallbacks(scheduleService::getAll, scheduleService::getCount),
				right::open, right::add);
		
		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(Schedule schedule) {
		scheduleService.save(schedule);
		left.refresh();
	}

	private void delete(Schedule schedule) {
		scheduleService.delete(schedule);
		left.refresh();
	}

}
