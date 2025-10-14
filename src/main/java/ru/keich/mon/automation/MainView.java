package ru.keich.mon.automation;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import lombok.extern.java.Log;
import ru.keich.mon.automation.actor.ActorService;
import ru.keich.mon.automation.datasource.DataSource;
import ru.keich.mon.automation.datasource.DataSourceService;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.schedule.ui.ScheduleEdit;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptsEdit;
import ru.keich.mon.automation.ui.simpleEdit.SimpleEdit;

@PermitAll
@Route
@RouteAlias("/datasource")
@RouteAlias("/schedule")
@RouteAlias("/scripts")
@Log
public class MainView extends AppLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1237287395694809506L;

	public static final String TAB_DATASOURCE_NAME = "DataSources";
	public static final String TAB_SCHEDULE_NAME = "Schedule";
	public static final String TAB_SCRIPTS_NAME = "Scripts";

	public static final String TAB_DATASOURCE_PATH = "datasource";
	public static final String TAB_SCHEDULE_PATH = "schedule";
	public static final String TAB_SCRIPTS_PATH = "scripts";

	private SimpleEdit<DataSource> dataSourceView;
	//private SimpleEdit<Actor> servicesView;
	private ScheduleEdit scheduleEdit;
	private ScriptsEdit scriptsView;

	public MainView(DataSourceService dataSourceService, ActorService actorService, ScriptService scriptService, ScheduleService scheduleService) {
		super();
		dataSourceView = new SimpleEdit<DataSource>(dataSourceService, DataSource.class);

		dataSourceView.addColumn(DataSource::getName);
		
		scheduleEdit = new ScheduleEdit(scheduleService, scriptService);

		scriptsView = new ScriptsEdit(scriptService);

		var toggle = new DrawerToggle();
		var scroller = new Scroller(getSideNav());

		addToDrawer(scroller);
		addToNavbar(toggle);

	}

	private SideNav getSideNav() {
		SideNav sideNav = new SideNav();
		sideNav.addItem(new SideNavItem(TAB_DATASOURCE_NAME, TAB_DATASOURCE_PATH, VaadinIcon.DATABASE.create()),
				new SideNavItem(TAB_SCHEDULE_NAME, TAB_SCHEDULE_PATH, VaadinIcon.TIMER.create()),
				new SideNavItem(TAB_SCRIPTS_NAME, TAB_SCRIPTS_PATH, VaadinIcon.FILE_CODE.create()));
		return sideNav;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		switch (event.getLocation().getPath()) {
		case TAB_DATASOURCE_PATH:
			this.setContent(dataSourceView);
			break;
		case TAB_SCHEDULE_PATH:
			this.setContent(scheduleEdit);
			break;
		case TAB_SCRIPTS_PATH:
			this.setContent(scriptsView);
			break;
		}
	}

}
