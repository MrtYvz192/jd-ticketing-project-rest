package com.cybertek.dto;

import com.cybertek.entity.Role;
import com.cybertek.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String confirmPassword;
    private boolean enabled;
    private String phone;
    private RoleDTO role;
    private Gender gender;

}
