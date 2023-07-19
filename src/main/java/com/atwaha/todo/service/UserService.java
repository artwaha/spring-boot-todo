package com.atwaha.todo.service;

import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.LoginRequestDTO;
import com.atwaha.todo.model.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CollaboratorService collaboratorService;
    private final BCryptPasswordEncoder passwordEncoder;

    //    Transform User to UserResponseDTO
    private UserResponseDTO createUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        return userResponseDTO;
    }

    public ResponseEntity<UserResponseDTO> createUser(User user) {
        try {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
//            convert to lowercase - email should be case-insensitive
            user.setEmail(user.getEmail().toLowerCase());
            User newUser = userRepository.save(user);

//            Convert to UserResponseDTO
            UserResponseDTO userResponseDTO = createUserResponseDTO(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<UserResponseDTO> login(LoginRequestDTO loginRequest) {
        try {
//            Convert to lowercase - email should be case-insensitive
            String username = loginRequest.getUsername().toLowerCase();
            String rawPassword = loginRequest.getPassword();

            User user = userRepository.findByEmail(username);

            if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
//                Convert to UserResponseDTO
                UserResponseDTO userResponseDTO = createUserResponseDTO(user);
                return ResponseEntity.ok(userResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    public ResponseEntity<List<User>> getUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //    LOGIC: users to invite -> All users that are not collaborating in that task(task_id) including the owner of the task(user_id)
    //    Owner of the task can't be collaborator
    public ResponseEntity<List<User>> getUsersToInvite(Long userId, Long taskId) {
        try {
            List<Long> collaboratingUserIds = new ArrayList<>();
            List<Collaborator> collaborators = collaboratorService.getTaskCollaborators(taskId, userId).getBody();

            if (collaborators != null && !collaborators.isEmpty()) {
                collaboratingUserIds = collaborators
                        .stream()
                        .map(collaborator -> collaborator.getUser().getId())
                        .collect(Collectors.toList());
            }
//          Adding owner into the exclusion list
            collaboratingUserIds.add(userId);

            List<User> usersToInvite = userRepository.findByIdNotIn(collaboratingUserIds);
            return ResponseEntity.ok(usersToInvite);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

