package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.TaskMapper;
import com.cybertek.repositories.TaskRepository;
import com.cybertek.repositories.UserRepository;
import com.cybertek.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskMapper taskMapper;
    ProjectMapper projectMapper;
    UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectMapper projectMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()) return taskMapper.convertToDTO(task.get());
        return  null;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> list = taskRepository.findAll();
        //return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList()); // same as above
    }

    @Override
    public Task save(TaskDTO dto) {
        dto.setAssignedDate(LocalDate.now());
        dto.setTaskStatus(Status.OPEN);
        Task task = taskMapper.convertToEntity(dto);
        return taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());
        Task converted = taskMapper.convertToEntity(dto);
        if (task.isPresent()){
            converted.setTaskStatus(task.get().getTaskStatus());
            converted.setAssignedDate(task.get().getAssignedDate());
            converted.setId(task.get().getId());
            taskRepository.save(converted);
        }

    }

    @Override
    public void delete(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            task.get().setIsDeleted(true);
            taskRepository.save(task.get());
        }
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);

    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> taskDTOS = listAllByProject(project);
        taskDTOS.forEach(taskDTO -> delete(taskDTO.getId()));


    }

    public List<TaskDTO> listAllByProject(ProjectDTO project){
        List<Task> list = taskRepository.findAllByProject(projectMapper.convertToEntity(project));
        return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        //getting the username based on the logged in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        List<Task>  list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status,user);
        return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() {
        //getting the username based on the logged in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        List<Task> list = taskRepository.findAllByProjectAssignedManager(user);
        return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());
        if(task.isPresent()){
            task.get().setTaskStatus(dto.getTaskStatus());
            taskRepository.save(task.get());
        }
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {
        //getting the username based on the logged in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        List<Task>  list = taskRepository.findAllByTaskStatusIsAndAssignedEmployee(status,user);
        return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByEmployee(User user) {
        List<Task> list = taskRepository.findAllByAssignedEmployee(user);
        return list.stream().map(task-> taskMapper.convertToDTO(task)).collect(Collectors.toList());
    }


}
