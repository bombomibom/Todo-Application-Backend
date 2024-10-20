package com.bom.todo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bom.todo.model.TodoEntity;
import com.bom.todo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TodoService {

	@Autowired
	private TodoRepository repository;
	
	public List<TodoEntity> create(TodoEntity entity) {
		// validations
		validate(entity);
		
		repository.save(entity);
		
		log.info("Entity Id : {} is saved.", entity.getId());
		
		return repository.findByUserId(entity.getUserId());
	}
	
	public List<TodoEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}
	
	public List<TodoEntity> update(final TodoEntity entity) {
		validate(entity);
		
		Optional<TodoEntity> original = repository.findById(entity.getId());
		original.ifPresent(todo -> {
			todo.setId(entity.getId());
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.getDone());
			
			repository.save(todo);
		});
		
		return retrieve(entity.getUserId());
	}
	
	public List<TodoEntity> delete(final TodoEntity entity) {
		validate(entity);
		
		try {
			repository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity ", entity.getId(), e);
			throw new RuntimeException("error deleting entity " + entity.getId());
		}
		
		return retrieve(entity.getUserId());
	}
	
	private void validate(TodoEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannot be null.");
		}
		
		if(entity.getUserId() == null) {
			log.warn("Unknown User.");
			throw new RuntimeException("Unknown User.");
		}
	}
}
