package ru.keich.mon.automation.scripting;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.logging.Level;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class LogManager {

	private Consumer<Line> callBack;

	@Getter
	public static class Line {
		private static final String PATTERN_FORMAT = "HH.mm.ss.SSS";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault());

		private final Instant time = Instant.now();
		private final Level level;
		private final String msg;

		public Line(Level level, String msg) {
			this.level = level;
			this.msg = msg;
		}

		public String getTimeFormatter() {
			return formatter.format(time);
		}

	}

	public void info(String msg) {
		log.log(Level.INFO, msg);
		callBack.accept(new Line(Level.INFO, msg));
	}

	public void warning(String msg) {
		log.log(Level.WARNING, msg);
		callBack.accept(new Line(Level.WARNING, msg));
	}

	public void severe(String msg) {
		log.log(Level.SEVERE, msg);
		callBack.accept(new Line(Level.SEVERE, msg));
	}

	public void setCallBack(Consumer<Line> callBack) {
		this.callBack = callBack;
	}

}
