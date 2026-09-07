package com.example.ticketsystem.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.dto.IssueDto;
import com.example.ticketsystem.service.IssueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<List<IssueDto.Response>> getAll() {
        return ResponseEntity.ok(issueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IssueDto.Response> create(@Valid @RequestBody IssueDto.CreateRequest request) {
        IssueDto.Response created = issueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}