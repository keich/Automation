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
import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;
import ru.keich.mon.automation.httpdatasource.ui.HttpDataSourceEdit;
import ru.keich.mon.automation.httplistner.HttpListnerService;
import ru.keich.mon.automation.httplistner.ui.HttpListnerEdit;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.schedule.ui.ScheduleEdit;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptsEdit;
import ru.keich.mon.automation.security.SecurityService;
import ru.keich.mon.automation.snmp.SnmpService;
import ru.keich.mon.automation.snmp.ui.SnmpEdit;

@PermitAll
@Route
@RouteAlias("/dbdatasource")
@RouteAlias("/schedule")
@RouteAlias("/scripts")
@RouteAlias("/snmp")
@RouteAlias("/http")
@RouteAlias("/httpdatasource")
@Log
public class MainView extends AppLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1237287395694809506L;

	public static final String TAB_DATASOURCE_NAME = "DB DataSource";
	public static final String TAB_HTTPDATASOURCE_NAME = "Http DataSource";
	public static final String TAB_SCHEDULE_NAME = "Schedule";
	public static final String TAB_SCRIPTS_NAME = "Scripts";
	public static final String TAB_SNMP_NAME = "Snmp";
	public static final String TAB_HTTP_NAME = "Http input";

	public static final String TAB_DATASOURCE_PATH = "dbdatasource";
	public static final String TAB_HTTPDATASOURCE_PATH = "httpdatasource";
	public static final String TAB_SCHEDULE_PATH = "schedule";
	public static final String TAB_SCRIPTS_PATH = "scripts";
	public static final String TAB_SNMP_PATH = "snmp";
	public static final String TAB_HTTP_PATH = "http";

	private final DBDataSourceEdit dataSourceView;
	private final ScheduleEdit scheduleEdit;
	private final ScriptsEdit scriptsView;
	private final SnmpEdit snmpEdit;
	private final HttpDataSourceEdit httpDataSourceEdit;
	private final HttpListnerEdit httpEdit;

	public MainView(SecurityService securityService, DBDataSourceService dataSourceService, ScriptService scriptService,
			ScheduleService scheduleService, SnmpService snmpService, HttpDataSourceService httpDataSourceService, HttpListnerService httpListnerService) {
		super();
		dataSourceView = new DBDataSourceEdit(dataSourceService);
		
		scheduleEdit = new ScheduleEdit(scheduleService, scriptService);

		scriptsView = new ScriptsEdit(scriptService, scheduleService);
		
		snmpEdit = new SnmpEdit(snmpService, scriptService);
		
		httpDataSourceEdit = new HttpDataSourceEdit(httpDataSourceService);
		
		httpEdit = new HttpListnerEdit(httpListnerService, scriptService);

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
				new SideNavItem(TAB_HTTPDATASOURCE_NAME, TAB_HTTPDATASOURCE_PATH, VaadinIcon.OUTBOX.create()),
				new SideNavItem(TAB_SCHEDULE_NAME, TAB_SCHEDULE_PATH, VaadinIcon.TIMER.create()),
				new SideNavItem(TAB_SNMP_NAME, TAB_SNMP_PATH, VaadinIcon.PAPERPLANE.create()),
				new SideNavItem(TAB_HTTP_NAME, TAB_HTTP_PATH, VaadinIcon.INBOX.create()),
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
		case TAB_SNMP_PATH:
			this.setContent(snmpEdit);
			break;
		case TAB_HTTPDATASOURCE_PATH:
			this.setContent(httpDataSourceEdit);
			break;
		case TAB_HTTP_PATH:
			this.setContent(httpEdit);
			break;
		}
	}

}
