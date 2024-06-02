package com.example.anekdotjavabot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.example.anekdotjavabot.model.JokeCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeCallRepository extends JpaRepository<JokeCall, Long> {
}

