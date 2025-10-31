package ru.keich.mon.automation;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import lombok.extern.java.Log;
import ru.keich.mon.automation.dbdatasource.DBDataSourceService;
import ru.keich.mon.automation.dbdatasource.ui.DBDataSourceEdit;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.schedule.ui.ScheduleEdit;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptsEdit;
import ru.keich.mon.automation.security.SecurityService;

@PermitAll
@Route
@RouteAlias("/dbdatasource")
@RouteAlias("/schedule")
@RouteAlias("/scripts")
@Log
public class MainView extends AppLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1237287395694809506L;

	public static final String TAB_DATASOURCE_NAME = "DB DataSource";
	public static final String TAB_SCHEDULE_NAME = "Schedule";
	public static final String TAB_SCRIPTS_NAME = "Scripts";

	public static final String TAB_DATASOURCE_PATH = "dbdatasource";
	public static final String TAB_SCHEDULE_PATH = "schedule";
	public static final String TAB_SCRIPTS_PATH = "scripts";

	private final DBDataSourceEdit dataSourceView;
	private final ScheduleEdit scheduleEdit;
	private final ScriptsEdit scriptsView;

	public MainView(SecurityService securityService, DBDataSourceService dataSourceService, ScriptService scriptService,
			ScheduleService scheduleService) {
		super();
		dataSourceView = new DBDataSourceEdit(dataSourceService);
		
		scheduleEdit = new ScheduleEdit(scheduleService, scriptService);

		scriptsView = new ScriptsEdit(scriptService, scheduleService);

		var toggle = new DrawerToggle();
		var scroller = new Scroller(getSideNav());

		addToDrawer(scroller);

		var header = new HorizontalLayout();
		header.setWidthFull();

		if (securityService.getAuthenticatedUser() != null) {
			var logout = new Button("Logout", click -> securityService.logout());
			header.addToEnd(logout);
		}

		addToNavbar(toggle, header);

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
