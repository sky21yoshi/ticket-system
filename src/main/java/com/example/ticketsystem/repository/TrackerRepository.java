package com.example.ticketsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketsystem.entity.Tracker;

public interface TrackerRepository extends JpaRepository<Tracker, Long>{

}
