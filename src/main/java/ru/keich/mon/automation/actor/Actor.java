package ru.keich.mon.automation.actor;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.keich.mon.automation.ui.simpleEdit.SimpleEditItem;
import ru.keich.mon.automation.ui.simpleEdit.SimpleEditRight.FormFieldBuiler;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Actor implements SimpleEditItem {

	public static final String SCHEDULE_NAME_ID = "SCHEDULE_NAME";
	public static final String SCHEDULE_FIXRATE_ID = "SCHEDULE_FIXRATE";
	public static final String TYPE_ID = "TYPE";

	public static final String SCHEDULE_NAME_NAME = "Name";
	public static final String SCHEDULE_FIXRATE_NAME = "Fix rate";

	@Id
	private String name;

	private ActorType type;

	private Long fixedRate;

	@Override
	public String getGroup() {
		return type.name();
	}

	@Override
	public Map<String, String> toMap() {
		var ret = new HashMap<String, String>();
		ret.put(SCHEDULE_NAME_ID, name);
		ret.put(TYPE_ID, type.name());
		ret.put(SCHEDULE_FIXRATE_ID, fixedRate.toString());
		return ret;
	}

	@Override
	public FormFieldBuiler getFormFields() {
		return new FormFieldBuiler().addText(SCHEDULE_NAME_ID, ActorType.SCHEDULE.name(), SCHEDULE_NAME_NAME)
				.addText(SCHEDULE_FIXRATE_ID, ActorType.SCHEDULE.name(), SCHEDULE_FIXRATE_NAME);
	}

	@Override
	public void fromMap(String type, Map<String, String> data) {
		if (type != null && !"".equals(type)) {
			this.setType(ActorType.valueOf(type));
		}
		if (this.getType() != null) {
			switch (this.getType()) {
			case SCHEDULE:
				this.setName(data.get(SCHEDULE_NAME_ID));
				try {
					this.setFixedRate(Long.valueOf(data.get(SCHEDULE_FIXRATE_ID)));
				} catch (Exception e) {
					fixedRate = null;
				}
			}
		}
	}

	@Override
	public boolean validate() {
		if (type == null) {
			return false;
		}
		switch (type) {
		case SCHEDULE:
			return !"".equals(name) && fixedRate != null && fixedRate > 0;
		}
		return false;
	}

}
