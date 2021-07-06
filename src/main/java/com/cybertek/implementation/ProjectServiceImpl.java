package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repositories.ProjectRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectMapper projectMapper;
    private ProjectRepository projectRepository;
    private UserMapper userMapper;
    private UserService userService;
    private TaskService taskService;

    public ProjectServiceImpl(ProjectMapper projectMapper, ProjectRepository projectRepository, UserMapper userMapper, UserService userService, TaskService taskService) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.userMapper = userMapper;
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public ProjectDTO getByProjectCode(String code) {
        return projectMapper.convertToDTO(projectRepository.findByProjectCode(code));
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll();
        return list.stream().map(project -> {return projectMapper.convertToDTO(project);}).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {

        dto.setProjectStatus(Status.OPEN);
        Project entity = projectMapper.convertToEntity(dto);
        entity.setAssignedManager(userMapper.convertToEntity(dto.getAssignedManager()));
        projectRepository.save(entity);
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        Project converted = projectMapper.convertToEntity(dto);
        converted.setId(project.getId());
        converted.setProjectStatus(project.getProjectStatus());
        projectRepository.save(converted);
    }

    @Override
    public void delete(String code) {
        Project entity = projectRepository.findByProjectCode(code);
        entity.setIsDeleted(true);
        entity.setProjectCode(entity.getProjectCode() + "-" + entity.getId());
        projectRepository.save(entity);

        taskService.deleteByProject(projectMapper.convertToDTO(entity));

    }

    @Override
    public void complete(String projectCode) {
        Project entity = projectRepository.findByProjectCode(projectCode);
        entity.setProjectStatus(Status.COMPLETE);
        projectRepository.save(entity);
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        //getting the username based on the logged in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserDTO currentUserDTO = userService.findByUserName(username);
        User user = userMapper.convertToEntity(currentUserDTO);
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(project -> {
            ProjectDTO obj = projectMapper.convertToDTO(project);
            obj.setUnfinishedTaskCount(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCount(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(project -> projectMapper.convertToDTO(project)).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {
        List<Project> list = projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE);
        return list.stream().map(project -> projectMapper.convertToDTO(project)).collect(Collectors.toList());
    }
}
