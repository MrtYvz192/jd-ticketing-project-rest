package com.cybertek.converter;

import com.cybertek.dto.TaskDTO;
import com.cybertek.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class TaskDTOConverter implements Converter<String, TaskDTO> {

    @Autowired
    private TaskService taskService;


    @Override
    public TaskDTO convert(String s) {

        Long id = Long.parseLong(s);
        return taskService.findById(id);
    }
}
