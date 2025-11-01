package ru.keich.mon.automation.schedule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.scripting.LogManager;

@Service
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	
	private final ScriptService scriptService;

	private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

	private final ConcurrentHashMap<Schedule, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

	public ScheduleService(ScheduleRepository scheduleRepository, ScriptService scriptService, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
		this.scheduleRepository = scheduleRepository;
		this.threadPoolTaskScheduler = threadPoolTaskScheduler;
		this.scriptService = scriptService;

		scheduleRepository.findAll().stream()
				.filter(Schedule::isValid)
				.filter(Schedule::isEnable)
				.forEach(this::schedule);
		
		scriptService.setScheduleService(this);
	}

	public Stream<Schedule> getAll(Query<Schedule, Void> q) {
		return scheduleRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}

	public int getCount(Query<Schedule, Void> q) {
		return Math.toIntExact(scheduleRepository.count());
	}

	public void save(Schedule schedule) {
		scheduleRepository.save(schedule);
		cancelSchedule(schedule);
		if(schedule.isEnable()) {
			schedule(schedule);
		}
	}

	public void delete(Schedule schedule) {
		scheduleRepository.delete(schedule);
		cancelSchedule(schedule);
	}

	private void schedule(Schedule schedule) {
		var trigger = new CronTrigger(schedule.getExpression());
		var future = threadPoolTaskScheduler.schedule(() -> {
			execute(schedule.getScriptName(), null, l -> {});
		}, trigger);
		tasks.put(schedule, future);
	}

	private void cancelSchedule(Schedule schedule) {
		tasks.computeIfPresent(schedule, (s, future) -> {
			future.cancel(false);
			return null;
		});
	}
	
	public void execute(Script script, Object param, Consumer<LogManager.Line> clackBack) {
		threadPoolTaskScheduler.execute(() -> {
			scriptService.run(script, param, clackBack);
		});
	}
	
	public void execute(String scriptName, Object param, Consumer<LogManager.Line> clackBack) {
		threadPoolTaskScheduler.execute(() -> {
			scriptService.run(scriptName, param, clackBack);
		});
	}

}
