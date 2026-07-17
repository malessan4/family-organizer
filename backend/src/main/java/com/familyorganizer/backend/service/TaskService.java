package com.familyorganizer.backend.service;

import com.familyorganizer.backend.model.Task;
import com.familyorganizer.backend.model.TaskStatus;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.repository.TaskRepository;
import com.familyorganizer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getTasksByFamilyId(Long familyId) {
        return taskRepository.findByFamilyId(familyId);
    }

    public Task createTask(Task task, Long familyId, String username) {
        User creator = userRepository.findByUsername(username).orElseThrow();
        task.setFamily(creator.getFamily());
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
