package ru.keich.mon.automation.schedule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.ScriptService;

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
			scriptService.run(schedule.getScriptName());
		}, trigger);
		tasks.put(schedule, future);
	}

	private void cancelSchedule(Schedule schedule) {
		tasks.computeIfPresent(schedule, (s, future) -> {
			future.cancel(false);
			return null;
		});
	}

}
