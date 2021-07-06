package com.cybertek.mapper;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MappaerUtil {

    private ModelMapper modelMapper;

    public MappaerUtil(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

//    public <T> T convertToEntity(Object objectToBeConverted, T convertedObject){
//        return modelMapper.map(objectToBeConverted, (Type) convertedObject.getClass());
//    }
//
//    public <T> T convertToDTO(Object objectToBeConverted, T convertedObject){
//        return modelMapper.map(objectToBeConverted, (Type) convertedObject.getClass())
//    }

    public <T> T convert(Object objectToBeConverted, T convertedObject){
        return modelMapper.map(objectToBeConverted, (Type) convertedObject.getClass());
    }
}
