package com.example.ticketsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketsystem.entity.IssueStatus;

public interface IssueStatusRepository extends JpaRepository<IssueStatus, Long>{
   
}
