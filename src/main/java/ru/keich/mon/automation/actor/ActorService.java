package ru.keich.mon.automation.actor;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.ui.simpleEdit.SimpleEditService;

@Service
public class ActorService implements SimpleEditService<Actor> {

	private final ActorRepository actorRepository;

	public ActorService(ActorRepository actorRepository) {
		super();
		this.actorRepository = actorRepository;
	}

	@Override
	public Stream<Actor> getAll(Query<Actor, Void> q) {
		return actorRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}

	@Override

	public int getCount(Query<Actor, Void> q) {
		return Math.toIntExact(actorRepository.count());
	}

	@Override
	public void save(Actor actor) {
		actorRepository.save(actor);
	}

	@Override
	public void delete(Actor actor) {
		actorRepository.delete(actor);
	}

	public Optional<Actor> get(String id) {
		return actorRepository.findById(id);
	}

}
