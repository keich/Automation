package ru.keich.mon.automation.script.ui;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.BackEndDataProvider;

import lombok.extern.java.Log;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.scripting.LogManager;

@Log
public class ScriptsEditRight extends VerticalLayout {

	private static final long serialVersionUID = 344660752148918720L;

	public static final String NAME = "Name";
	public static final String PARENT = "Parent";
	
	public static final String LOG_DIALOG_CLOSE_BUTTON_TEXT = "Close";

	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";
	
	private static final double SPLIT_POS = 80;

	private final TextArea textArea;
	private final Grid<LogManager.Line> logsConsole;
	private final TextField nameField;
	private final ComboBox<String> parentField;

	private final Dialog deleteDialog;

	private final LinkedList<LogManager.Line> logs = new LinkedList<>();

	public ScriptsEditRight(BackEndDataProvider<String, String> dataProvider, Consumer<Script> save,
			Function<Script, Boolean> delete, BiConsumer<Script, Consumer<LogManager.Line>> run) {
		var header = new Header();
		header.setWidthFull();

		logsConsole = new Grid<>(LogManager.Line.class, false);
		logsConsole.addColumn(LogManager.Line::getTimeFormatter).setFlexGrow(2);// .getStyle().setMaxWidth("8em");
		logsConsole.addColumn(LogManager.Line::getLevel).setFlexGrow(1);
		logsConsole.addColumn(LogManager.Line::getMsg).setFlexGrow(20);
		
		var logDialogDetails = new Dialog();
		logDialogDetails.setDraggable(true);
		logDialogDetails.setResizable(true);
		logDialogDetails.setWidth("50%");
		logDialogDetails.setHeight("50%");
		var logDetails = new TextArea();
		logDetails.setSizeFull();
		logDetails.setReadOnly(true);
		var logDialogDetailsLayout = new VerticalLayout(logDetails);
		logDialogDetailsLayout.setSizeFull();
		
		logDialogDetails.add(logDialogDetailsLayout);
		
		
		logsConsole.addItemDoubleClickListener(event -> {
			logDialogDetails.setHeaderTitle(event.getItem().getLevel().toString());
			logDetails.setValue(event.getItem().getMsg());
			logDialogDetails.open();
		});
		
		logsConsole.setItems(logs);
		logsConsole.setSizeFull();

		var saveButton = new Button(new Icon(VaadinIcon.DOWNLOAD));
		saveButton.addClickListener(e -> save.accept(getScript()));
		header.add(saveButton);

		deleteDialog = createDeleteDialog(() -> delete.apply(getScript()));

		var deleteButton = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE));
		deleteButton.addClickListener(e -> openDeleteDialog());
		header.add(deleteButton);

		var playButton = new Button(new Icon(VaadinIcon.PLAY));
		playButton.addClickListener(e -> run.accept(getScript(), this::addLogLine));
		header.add(playButton);

		var formLayout = new FormLayout();
		formLayout.setWidthFull();

		nameField = new TextField();
		formLayout.addFormItem(nameField, NAME);

		parentField = new ComboBox<String>();
		parentField.setItems(dataProvider);
		formLayout.addFormItem(parentField, PARENT);

		header.add(formLayout);

		textArea = new TextArea();
		textArea.setWidthFull();

		add(header);

		var split = new SplitLayout(textArea, logsConsole);
		split.setOrientation(SplitLayout.Orientation.VERTICAL);
		split.setSplitterPosition(SPLIT_POS);
		split.setWidthFull();

		addAndExpand(split);
	}

	public void addLogLine(LogManager.Line line) {
		logsConsole.getUI().ifPresent(ui -> {
			ui.access(() ->{
				logs.addFirst(line);
				logsConsole.getDataProvider().refreshAll();
			});
		});
	}

	public boolean addNew() {
		clearAll();
		return true;
	}

	private void clearAll() {
		textArea.clear();
		nameField.clear();
		parentField.clear();
	}

	private Script getScript() {
		var ret = new Script();
		ret.setName(nameField.getValue());
		ret.setCode(textArea.getValue());
		parentField.getOptionalValue().ifPresent(parent -> {
			ret.setParent(parent);
		});
		return ret;
	}

	public void setScript(Script script) {
		nameField.setValue(script.getName());
		textArea.setValue(script.getCode());
		parentField.setValue(script.getParent());
	}

	private void openDeleteDialog() {
		deleteDialog.setHeaderTitle(DELETE_DIALOG_TEXT + nameField.getValue() + "?");
		deleteDialog.open();
	}

	private static Dialog createDeleteDialog(Supplier<Boolean> doOnDelet) {
		var dialog = new Dialog();
		var yesBtn = new Button(DELETE_DIALOG_YES, e -> {
			doOnDelet.get();
			dialog.close();
		});
		yesBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		var noBtn = new Button(DELETE_DIALOG_NO, e -> {
			dialog.close();
		});
		dialog.getFooter().add(yesBtn, noBtn);
		return dialog;
	}

}
