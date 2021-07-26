package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.repositories.ProjectRepository;
import com.cybertek.repositories.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {


    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private MapperUtil mapperUtil;
    private UserService userService;
    private TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, MapperUtil mapperUtil, UserService userService, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return mapperUtil.convert(projectRepository.findByProjectCode(code),new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll();
        return list.stream().map(project -> {return mapperUtil.convert(project,new ProjectDTO());}).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {

        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());

        if(foundProject != null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        dto.setProjectStatus(Status.OPEN);
        Project entity = mapperUtil.convert(dto,new Project());
        entity.setAssignedManager(mapperUtil.convert(dto.getAssignedManager(),new User()));
        Project savedProject = projectRepository.save(entity);

        return mapperUtil.convert(savedProject,new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        if(project != null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        Project converted = mapperUtil.convert(dto,new Project());
//        converted.setId(project.getId());
//        converted.setProjectStatus(project.getProjectStatus());
        Project updatedProject = projectRepository.save(converted);
        return mapperUtil.convert(updatedProject,new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        Project entity = projectRepository.findByProjectCode(code);
        if(entity != null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        entity.setIsDeleted(true);
        entity.setProjectCode(entity.getProjectCode() + "-" + entity.getId());
        projectRepository.save(entity);

        taskService.deleteByProject(mapperUtil.convert(entity,new ProjectDTO()));

    }

    @Override
    public ProjectDTO complete(String projectCode) throws TicketingProjectException {
        Project entity = projectRepository.findByProjectCode(projectCode);
        if(entity != null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        entity.setProjectStatus(Status.COMPLETE);
        Project completed = projectRepository.save(entity);
        return mapperUtil.convert(completed,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException {
        //getting the username based on the logged in user
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId = Long.parseLong(id);

        User user = userRepository.findById(currentId).orElseThrow(()->new TicketingProjectException("This manager does not exist!!"));

        List<Project> list = projectRepository.findAllByAssignedManager(user);

        if(list.size() == 0){
            throw new TicketingProjectException("No project were found for this manager");
        }

        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project, new ProjectDTO());
            obj.setUnfinishedTaskCount(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCount(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(project -> mapperUtil.convert(project,new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {
        List<Project> list = projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE);
        return list.stream().map(project -> mapperUtil.convert(project,new ProjectDTO())).collect(Collectors.toList());
    }
}
