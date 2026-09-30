package ru.keich.mon.automation.script.ui;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;

import de.f0rce.ace.AceEditor;
import lombok.extern.java.Log;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.scripting.logging.LogManager;
import ru.keich.mon.automation.scripting.logging.ScriptCallBack;
import ru.keich.mon.automation.scripting.logging.ScriptLogRepository;
import ru.keich.mon.automation.scripting.logging.LogManager.Line;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Log
public class ScriptsEditRight extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver {

	private static final long serialVersionUID = 344660752148918720L;

	public static final String NAME = "Name";
	public static final String PARENT = "Parent";
	
	public static final String LOG_DIALOG_CLOSE_BUTTON_TEXT = "Close";
	
	public static final String SAVE_DIALOG_TEXT = "Unsaved Changes";
	public static final String SAVE_DIALOG_SAVE = "Save";
	public static final String SAVE_DIALOG_CLOSE = "X";
	public static final String SAVE_DIALOG_LEAVE = "Leave";
	
	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";
	
	public static final String LOG_MSG_RUN_OK = "Result: ";
	public static final String LOG_MSG_RUN_ERR = "Error: ";
	
	public static final String TOOLTIP_TEXT_SAVE = "Save";
	public static final String TOOLTIP_TEXT_DEL = "Delete";
	public static final String TOOLTIP_TEXT_RUN = "Run";
	
	public static final String SETFIELD_DIALOG_TEXT = "Change Name";
	public static final String SETFIELD_DIALOG_SAVE = "Change Name";

	private static final double SPLIT_POS = 80;

	private final AceEditor textArea;
	private final Grid<LogManager.Line> logsConsole;
	private final TextField nameField;
	private final ComboBox<String> parentField;
	
	private int _scriptCodeHash = "".hashCode();
	private void updateCodeHash() {
		this._scriptCodeHash = getScript().getCode().hashCode();
	}
	private boolean _hasChanges() {
		System.out.println("firstCode: "+_scriptCodeHash+"; editor:"+getScript().getCode().hashCode());
		return _scriptCodeHash != getScript().getCode().hashCode();
	}
	
	private final Consumer<Boolean> _setButtonSaveEnable;
	private final Consumer<Boolean> _setButtonPlayEnable;
	private final Consumer<Boolean> _setButtonDeleteEnable;

	private final Dialog deleteDialog;

	private final LinkedList<LogManager.Line> logs = new LinkedList<>();
	 
	private final Consumer<Script> _save;
	
	public ScriptsEditRight(BackEndDataProvider<String, String> dataProvider, Consumer<Script> save,
			Function<Script, Boolean> delete, BiConsumer<Script, ScriptCallBack> run, ScriptLogRepository logRepository) {
		
		this._save = s-> {
			
			save.accept(s);
		};
		
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
		saveButton.setTooltipText(TOOLTIP_TEXT_SAVE);
		this._setButtonSaveEnable = saveButton::setEnabled;
		saveButton.addClickListener(e -> {
			save.accept(getScript());
		});
		saveButton.setEnabled(false);
		header.add(saveButton);

		deleteDialog = createDeleteDialog(() -> {
			//this._hasChangeScript = false;
			return delete.apply(getScript());}
		);

		var deleteButton = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE));
		this._setButtonDeleteEnable = deleteButton::setEnabled;
		deleteButton.setTooltipText(TOOLTIP_TEXT_DEL);
		deleteButton.addClickListener(e -> openDeleteDialog());
		deleteButton.setEnabled(false);
		header.add(deleteButton);
		
		var playButton = new Button(new Icon(VaadinIcon.PLAY));
		this._setButtonPlayEnable = playButton::setEnabled;
		var callBack = new ScriptCallBack(logRepository) {
			@Override
			public void onLog(Line line) {
				super.onLog(line);
				addLogLine(line);
				
			}

			@Override
			public void onResult(String data) {
				super.onResult(data);
				addLogLine(new Line(Level.INFO, LOG_MSG_RUN_OK + data));
			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				addLogLine(new Line(Level.SEVERE, LOG_MSG_RUN_ERR + e.getMessage()));
			}
		};

		playButton.addClickListener(e -> run.accept(getScript(), callBack));
		playButton.setTooltipText(TOOLTIP_TEXT_RUN);
		playButton.setEnabled(false);
		header.add(playButton);

		var formLayout = new FormLayout();
		formLayout.setWidthFull();

		nameField = new TextField(this::validate);
		formLayout.addFormItem(nameField, NAME);

		parentField = new ComboBox<String>();
		parentField.setItems(dataProvider);
		formLayout.addFormItem(parentField, PARENT);

		header.add(formLayout);

		textArea = new AceEditor();

		add(header);

		var split = new SplitLayout(textArea, logsConsole);
		split.setOrientation(SplitLayout.Orientation.VERTICAL);
		split.setSplitterPosition(SPLIT_POS);
		split.setWidthFull();

		addAndExpand(split);
	}
	
	public void addLogLine(LogManager.Line line) {
		logsConsole.getUI().ifPresent(ui -> {
			try {
				ui.access(() ->{
					logs.addFirst(line);
					logsConsole.getDataProvider().refreshAll();
				});
			} catch(Exception e) {
				System.out.println("UIDetachedException on ScriptEditRight.addLogLine methode.");
			}
		});
	}

	public boolean addNew() {
		if (_hasChanges()) {
			_createSaveDialog(
					()->{
						clearAll();
						return true;
					},
					()->{
						return true;
					}
				).open();
		} else {
			clearAll();
		}
		
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
		Supplier<Boolean> export = ()->{
			nameField.setValue(script.getName());
			textArea.setValue(script.getCode());
			parentField.setValue(script.getParent());
			validatePickScript();
			this.updateCodeHash();
			return true;
		};
		
		System.err.println("setScript "+this._hasChanges());
		if (_hasChanges()) {
			_createSaveDialog(
				()->{
					export.get();
					return true;
				},
				()->{
					return true;
				}
			).open();
		} else {
			export.get();
		}
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
	
	private void validate(Object event) {
		System.err.println("validate: "+this._hasChanges());
		boolean bVisible = !nameField.getValue().isEmpty();
	
		this._setButtonSaveEnable.accept(bVisible);
		this._setButtonPlayEnable.accept(bVisible);
	}
	
	private void validatePickScript() {
		this._setButtonDeleteEnable.accept(true);
	}

	private Dialog _createSaveDialog(Supplier<Boolean> afterPositiveDialog,Supplier<Boolean> afterNegativeDialog) {
		Dialog d = new Dialog(SAVE_DIALOG_TEXT);
		Supplier<Boolean> exitNegative = ()-> {
			afterNegativeDialog.get();
			d.close();
			return true;
		};
		Supplier<Boolean> exitPositive = ()-> {
			afterPositiveDialog.get();
			d.close();
			return true;
		};
		d.getHeader().add(new Button(SAVE_DIALOG_CLOSE,i->{d.close();}));
		d.getFooter().add(
				new Button(SAVE_DIALOG_SAVE, i->{
					if (nameField.getValue().isBlank()) {
						nameField.addClassName("err-field");
						exitNegative.get();
						return;
					}	
					_save.accept(getScript());
					exitPositive.get();
				}),
				new Button(SAVE_DIALOG_LEAVE,i->exitPositive.get())
			);
				
		return d;
	}
	@Override
	public void beforeEnter(BeforeEnterEvent e) {
		clearAll();
	}
	@Override
	public void beforeLeave(BeforeLeaveEvent e) {
		if (_hasChanges()) {
			var action = e.postpone();
			_createSaveDialog(
				()->{
					action.proceed();
					return true;
				},
				() ->{
					action.cancel();
					return true;
				}
			).open();
		}
	}
	
}
