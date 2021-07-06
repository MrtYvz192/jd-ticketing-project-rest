package com.cybertek.mapper;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;

public class MainMapper<T,DTO> {

    @Autowired
    private ModelMapper modelMapper;

    public T convertToEntity(DTO dto){
        T entity = null;
        return modelMapper.map(dto, (Type) entity.getClass());
    }

    public DTO convertToDTO(T entity){
        DTO dto = null;
        modelMapper.map(entity,dto);
        return dto;
    }
}
