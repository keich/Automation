package ru.keich.mon.automation.scripting;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.logging.Level;

import lombok.Getter;
import lombok.extern.java.Log;

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
