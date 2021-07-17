package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.repositories.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ProjectService projectService;
    private TaskService taskService;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, @Lazy ProjectService projectService, TaskService taskService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll();

        return list.stream().map(user -> {return mapperUtil.convert(user, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserName(username);

        return mapperUtil.convert(user, new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketingProjectException {

        User foundUser = userRepository.findByUserName(dto.getUserName());
        if(foundUser != null){
            throw new TicketingProjectException("User already exists!");
        }
        dto.setEnabled(false);

        User user = mapperUtil.convert(dto, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return mapperUtil.convert(saved,new UserDTO());
    }

    @Override
    public UserDTO update(UserDTO dto) {
        User user = userRepository.findByUserName(dto.getUserName());
        User convertedUser = mapperUtil.convert(dto, new User());

        convertedUser.setId(user.getId());
        convertedUser.setPassword(passwordEncoder.encode(convertedUser.getPassword()));
        convertedUser.setEnabled(true);
        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());
    }

    @Override // Delete can do both ways
    public void delete(String username) throws TicketingProjectException {
        User user = userRepository.findByUserName(username);
        if(user==null){
            throw new TicketingProjectException("User Does Not Exist");
        }
        if(!checkIfUserCanBeDeleted(user)){
            throw new TicketingProjectException("User cannot be deleted. It is linked to a project or a task");
        }
        user.setUserName(user.getUserName() + "-" + user.getId());


        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override  // Hard Delete!!
    public void deleteByUsername(String username) {
        userRepository.deleteByUserName(username);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
       List<User> list = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return list.stream().map(user -> {return mapperUtil.convert(user, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {

        switch(user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projects = projectService.readAllByAssignedManager(user);
                return projects.size()==0;
            case "Employee":
                List<TaskDTO> tasks = taskService.readAllByEmployee(user);
                return tasks.size()==0;
            default:
                return true;
        }


    }

    @Override
    public UserDTO confirm(User user) {
        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);

        return mapperUtil.convert(confirmedUser, new UserDTO());
    }
}
