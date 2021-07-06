package com.cybertek.repositories;

import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <User,Long> {

    User findByUserName(String username);

    List<User> findAllByRoleDescriptionIgnoreCase(String role);

    @Transactional // In order to create another session for DDL operation
    void deleteByUserName(String username);
}
