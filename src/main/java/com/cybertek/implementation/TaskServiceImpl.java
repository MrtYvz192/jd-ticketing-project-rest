package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
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

    private TaskRepository taskRepository;
    private MapperUtil mapperUtil;
    private UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, MapperUtil mapperUtil, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.mapperUtil = mapperUtil;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO findById(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(()->new TicketingProjectException("Task does not exist!"));
        return mapperUtil.convert(task,new TaskDTO());

    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> list = taskRepository.findAll();
        //return list.stream().map(task -> taskMapper.convertToDTO(task)).collect(Collectors.toList());
        return list.stream().map(task->mapperUtil.convert(task,new TaskDTO())).collect(Collectors.toList()); // same as above
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        dto.setAssignedDate(LocalDate.now());
        dto.setTaskStatus(Status.OPEN);
        Task task = mapperUtil.convert(dto,new Task());
        return mapperUtil.convert(taskRepository.save(task),new TaskDTO());
    }

    @Override
    public TaskDTO update(TaskDTO dto) throws TicketingProjectException {
        Task task = taskRepository.findById(dto.getId()).orElseThrow(()-> new TicketingProjectException("Task does not exist!"));
        Task converted = mapperUtil.convert(dto, new Task());

//            converted.setTaskStatus(task.get().getTaskStatus());
//            converted.setAssignedDate(task.get().getAssignedDate());
//            converted.setId(task.get().getId());
        return mapperUtil.convert(taskRepository.save(converted),new TaskDTO());

    }

    @Override
    public void delete(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(()-> new TicketingProjectException("Task does not exist!"));

            task.setIsDeleted(true);
            taskRepository.save(task);

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
        taskDTOS.forEach(taskDTO -> {
            try {
                delete(taskDTO.getId());
            } catch (TicketingProjectException e) {
                e.printStackTrace();
            }
        });


    }

    public List<TaskDTO> listAllByProject(ProjectDTO project){
        List<Task> list = taskRepository.findAllByProject(mapperUtil.convert(project,new Project()));
        return list.stream().map(task -> mapperUtil.convert(task,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException {
        //getting the id based on the logged in user
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(()-> new TicketingProjectException("User does not exist!"));
        List<Task>  list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status,user);
        return list.stream().map(task -> mapperUtil.convert(task,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException {
        //getting the id based on the logged in user
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(()-> new TicketingProjectException("This user does not exist!"));
        List<Task> list = taskRepository.findAllByProjectAssignedManager(user);
        return list.stream().map(task -> mapperUtil.convert(task,new TaskDTO())).collect(Collectors.toList());
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
        return list.stream().map(task -> mapperUtil.convert(task,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByEmployee(User user) {
        List<Task> list = taskRepository.findAllByAssignedEmployee(user);
        return list.stream().map(task-> mapperUtil.convert(task, new TaskDTO())).collect(Collectors.toList());
    }


}
