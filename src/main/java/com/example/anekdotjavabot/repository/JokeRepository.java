// src/main/java/com/example/anekdotjavabot/repository/JokeRepository.java

package com.example.anekdotjavabot.repository;

import com.example.anekdotjavabot.model.Joke;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
