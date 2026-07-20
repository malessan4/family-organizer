package com.familyorganizer.backend.service;

import com.familyorganizer.backend.model.Task;
import com.familyorganizer.backend.model.TaskStatus;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.repository.TaskRepository;
import com.familyorganizer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        task.setStatus(TaskStatus.TODO);
        // Guardamos el displayName del creador — nunca cambiará aunque el usuario edite su nombre
        task.setCreatedByName(creator.getDisplayName());
        task.setInProgressByName(null);
        task.setCompletedByName(null);
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus, String moverDisplayName) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(newStatus);

        switch (newStatus) {
            case TODO:
                // Vuelve atrás: se limpia quién la estaba haciendo/completó
                // pero se mantiene createdByName intacto
                task.setInProgressByName(null);
                task.setCompletedByName(null);
                break;
            case IN_PROGRESS:
                // Alguien la tomó: registramos quién
                task.setInProgressByName(moverDisplayName);
                task.setCompletedByName(null);
                break;
            case DONE:
                // Alguien la completó: registramos quién y limpiamos "en progreso"
                task.setCompletedByName(moverDisplayName);
                task.setInProgressByName(null);
                break;
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task updateDueDate(Long taskId, LocalDate dueDate) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    public Task updateDescription(Long taskId, String description) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setDescription(description);
        return taskRepository.save(task);
    }
}
