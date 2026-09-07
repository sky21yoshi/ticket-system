package com.example.ticketsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketsystem.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
