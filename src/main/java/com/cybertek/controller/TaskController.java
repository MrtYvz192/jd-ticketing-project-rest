package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.enums.Status;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1//task")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again later!")
    @Operation(summary = "Read all tasks")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll(){
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all the tasks",taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again later!")
    @Operation(summary = "Read all tasks by project manager")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager(){
        List<TaskDTO> taskList = taskService.listAllTasksByProjectManager();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved tasks by project manager",taskList));
    }


}
