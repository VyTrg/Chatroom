package com.example.chatroom.repository;



import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from contact_with where contact_one_id = ?1 and contact_two_id <> ?1 or contact_two_id = ?1 and contact_one_id <> ?1",nativeQuery = true)
    List<ContactWith> findAllContacts(Long userId);

    @Query(value = """
    select * from contact_with 
    where (contact_one_id = (select id from [user] where username = ?1) and contact_two_id <> (select id from [user] where username = ?1))
       or (contact_two_id = (select id from [user] where username = ?1) and contact_one_id <> (select id from [user] where username = ?1))
""", nativeQuery = true)
    List<ContactWith> findAllContactsByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.* FROM [user] u " +
            "INNER JOIN contact_with cw " +
            "ON (cw.contact_one_id = u.id AND cw.contact_two_id = :currentUserId) " +
            "OR (cw.contact_two_id = u.id AND cw.contact_one_id = :currentUserId) " +
            "WHERE (u.username = :search OR u.email = :search) " +
            "AND u.id != :currentUserId",
            nativeQuery = true)
    User findByUsernameOrEmailWithInDiscussion(
            @Param("search") String search,
            @Param("currentUserId") Long currentUserId
    );

    @Query(value = "SELECT u.* FROM [user] u " +
            "LEFT JOIN contact_with cw " +
            "ON (cw.contact_one_id = u.id AND cw.contact_two_id = :currentUserId) " +
            "OR (cw.contact_two_id = u.id AND cw.contact_one_id = :currentUserId) " +
            "WHERE (u.username = :search OR u.email = :search) " +
            "AND u.id != :currentUserId " +
            "AND cw.id IS NULL",
            nativeQuery = true)
    User findByUsernameOrEmailNotInContactWith(
            @Param("search") String search,
            @Param("currentUserId") Long currentUserId
    );
}
