package com.familyorganizer.backend.controller;

import com.familyorganizer.backend.model.Task;
import com.familyorganizer.backend.model.TaskStatus;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.security.CustomUserDetails;
import com.familyorganizer.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getFamilyTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long familyId = userDetails.getUser().getFamily().getId();
        return ResponseEntity.ok(taskService.getTasksByFamilyId(familyId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(taskService.createTask(task, user.getFamily().getId(), user.getUsername()));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
