package com.cybertek.controller;


import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Project Controller", description = "Project API")
public class ProjectController {


    ProjectService projectService;
    UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read All Projects")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readAll(){
        List<ProjectDTO> projects = projectService.listAllProjects();

        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved successfully",projects));
    }


    @GetMapping("/{projectCode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read By Project Code")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readByProjectCode(@PathVariable("projectCode") String projectCode){
        ProjectDTO project = projectService.getByProjectCode(projectCode);

        return ResponseEntity.ok(new ResponseWrapper("Project is retrieved successfully",project));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create a Project")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        ProjectDTO project = projectService.save(projectDTO);

        return ResponseEntity.ok(new ResponseWrapper("Project has been created successfully",project));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update a Project")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        ProjectDTO updatedProject = projectService.update(projectDTO);

        return ResponseEntity.ok(new ResponseWrapper("Project has been created successfully",updatedProject));
    }

    @DeleteMapping("/{projectCode}")
    @DefaultExceptionMessage(defaultMessage = "Failed to delete the project!")
    @Operation(summary = "Delete a Project")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String projectCode) throws TicketingProjectException {
        projectService.delete(projectCode);

        return ResponseEntity.ok(new ResponseWrapper("Project has been successfully deleted"));
    }

    @PutMapping("/complete/{projectCode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Complete a Project")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> completeProject(@PathVariable("projectCode") String projectCode) throws TicketingProjectException {
        ProjectDTO project = projectService.complete(projectCode);

        return ResponseEntity.ok(new ResponseWrapper("Project has been successfully completed",project));
    }

    @GetMapping("/details")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read All Project Details")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllProjectDetails() throws TicketingProjectException {
        List<ProjectDTO> projects = projectService.listAllProjectDetails();


        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved successfully",projects));
    }

}
