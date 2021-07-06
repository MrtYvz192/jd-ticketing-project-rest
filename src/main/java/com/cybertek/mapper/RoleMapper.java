package com.cybertek.mapper;


import com.cybertek.dto.RoleDTO;
import com.cybertek.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    @Autowired
    private ModelMapper modelMapper;

    public RoleDTO convertToDTO(Role entity){
       return modelMapper.map(entity,RoleDTO.class);
    }

    public Role convertToEntity(RoleDTO dto){
        return modelMapper.map(dto,Role.class);
    }
}
