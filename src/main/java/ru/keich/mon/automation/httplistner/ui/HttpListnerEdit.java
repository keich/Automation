package ru.keich.mon.automation.httplistner.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.httplistner.HttpListner;
import ru.keich.mon.automation.httplistner.HttpListnerService;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.ui.ScriptDataProvider;

public class HttpListnerEdit extends Div {

	private static final long serialVersionUID = -4816016102385748090L;
	
	private static final double SPLIT_POS = 20;
	
	private final HttpListnerEditLeft left;
	
	private final HttpListnerEditRight right;
	
	private final HttpListnerService httpListnerService;

	public HttpListnerEdit(HttpListnerService httpListnerService, ScriptService scriptService) {
		super();
		this.httpListnerService = httpListnerService;
		this.setSizeFull();
		this.setHeightFull();
		
		var dataProvider = new ScriptDataProvider(scriptService);
		
		this.right = new HttpListnerEditRight(dataProvider, this::save, this::delete);
		
		this.left = new HttpListnerEditLeft(DataProvider.fromCallbacks(httpListnerService::getAll, httpListnerService::getCount), right::open, right::add);
		
		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(HttpListner httpListner) {
		httpListnerService.save(httpListner);
		left.refresh();
	}

	private void delete(HttpListner httpListner) {
		httpListnerService.delete(httpListner);
		left.refresh();
	}

}
