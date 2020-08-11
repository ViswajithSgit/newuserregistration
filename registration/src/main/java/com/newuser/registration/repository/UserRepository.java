package com.newuser.registration.repository;

import com.newuser.registration.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User where id=:id")
    public User findByUserDetails(@Param("id")Long id);
}
