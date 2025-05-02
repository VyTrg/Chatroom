package com.example.chatroom.controller;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.User;

import com.example.chatroom.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * API endpoint to retrieve all users.
     *
     * @return List of all users.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * API endpoint to retrieve user information by ID.
     *
     * @param userId ID of the user to retrieve.
     * @return UserWithContactsDTO containing user information.
     */
    @GetMapping("/id/{id}")
    public UserWithContactsDTO getUserInforById(@PathVariable(value = "id") Long userId) {
        return userService.getUserWithContactsDTOById(userId);
    }

    /**
     * API endpoint to retrieve user by username.
     *
     * @param username Username of the user to retrieve.
     * @return Optional User object.
     */
    @GetMapping("/username/{username}")
    public UserWithContactsDTO getUserByUsername(@PathVariable(value = "username") String username) {
        return userService.getUserWithContactsDTOByUsername(username);
    }

    /**
     * API endpoint to create a new user.
     *
     * @param user User object to create.
     * @return Created User object.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * API endpoint to update an existing user.
     *
     * @param userId      ID of the user to update.
     * @param userDetails User object containing updated information.
     * @return ResponseEntity containing the updated User object.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId, @RequestBody User userDetails) {
//        User user = userService.getUserById(userId);
//        user.setFirstName(userDetails.getFirstName());
//        user.setLastName(userDetails.getLastName());
//        user.setEmail(userDetails.getEmail());
//        user.setUsername(userDetails.getUsername());
//        user.setHashPassword(userDetails.getHashPassword());
//        user.setProfilePicture(userDetails.getProfilePicture());
//
//        final User updatedUser = userService.updateUser(user);
//        return ResponseEntity.ok(updatedUser);
        return null;
    }

    /**
     * API endpoint to delete a user.
     *
     * @param userId ID of the user to delete.
     * @return ResponseEntity indicating the result of the deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Long userId) {
//        User user = userService.getUserById(userId);
//        userService.deleteUser(user);
//        return ResponseEntity.ok().build();
        return null;
    }

    /**
     * API endpoint to retrieve all conversations a user is participating in.
     *
     * @param userId ID of the user to retrieve conversations for.
     * @return List of Conversation objects.
     */
    @GetMapping("/conversations/{userId}")
    public List<Conversation> getConversationsOfUser(@PathVariable Long userId) {
        return userService.getConversationsOfUser(userId);
    }
}
