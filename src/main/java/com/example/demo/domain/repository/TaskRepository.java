package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
	List<Task> findByCompleted(boolean b);
	Optional<Task> findByDatabaseId(Long b);
}
