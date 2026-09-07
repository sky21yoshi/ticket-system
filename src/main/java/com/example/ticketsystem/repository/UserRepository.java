package com.example.ticketsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketsystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
