package com.atwaha.todo.dao;

import com.atwaha.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);

    List<User> findByIdNotIn(List<Long> collaboratingUserIds);
}