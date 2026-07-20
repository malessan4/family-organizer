package com.familyorganizer.backend.controller;

import com.familyorganizer.backend.model.Task;
import com.familyorganizer.backend.model.TaskStatus;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.security.CustomUserDetails;
import com.familyorganizer.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// CORS manejado globalmente en SecurityConfig
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getFamilyTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long familyId = userDetails.getUser().getFamily().getId();
        return ResponseEntity.ok(taskService.getTasksByFamilyId(familyId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody Task task,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(taskService.createTask(task, user.getFamily().getId(), user.getUsername()));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Pasamos el displayName del usuario que está moviendo la tarea
        String moverDisplayName = userDetails.getUser().getDisplayName();
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status, moverDisplayName));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // Permite establecer o borrar la fecha de vencimiento (null = sin vencimiento)
    @PatchMapping("/{taskId}/due-date")
    public ResponseEntity<Task> updateDueDate(
            @PathVariable Long taskId,
            @RequestParam(required = false) String dueDate) {
        LocalDate date = (dueDate != null && !dueDate.isBlank()) ? LocalDate.parse(dueDate) : null;
        return ResponseEntity.ok(taskService.updateDueDate(taskId, date));
    }

    @PatchMapping("/{taskId}/description")
    public ResponseEntity<Task> updateDescription(
            @PathVariable Long taskId,
            @RequestBody String description) {
        return ResponseEntity.ok(taskService.updateDescription(taskId, description));
    }
}
