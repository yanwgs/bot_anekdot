// src/main/java/com/example/anekdotjavabot/service/JokeService.java

package com.example.anekdotjavabot.service;

import com.example.anekdotjavabot.model.Joke;
import com.example.anekdotjavabot.repository.JokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JokeService {

    private final JokeRepository jokeRepository;

    public List<Joke> getAllJokes() {
        return jokeRepository.findAll();
    }

    public Optional<Joke> getJokeById(Long id) {
        return jokeRepository.findById(id);
    }

    public Joke createJoke(Joke joke) {
        joke.setCreatedAt(LocalDateTime.now());
        joke.setUpdatedAt(LocalDateTime.now());
        return jokeRepository.save(joke);
    }

    public Optional<Joke> updateJoke(Long id, Joke updatedJoke) {
        return jokeRepository.findById(id)
                .map(joke -> {
                    joke.setText(updatedJoke.getText());
                    joke.setUpdatedAt(LocalDateTime.now());
                    return jokeRepository.save(joke);
                });
    }

    public void deleteJoke(Long id) {
        jokeRepository.deleteById(id);
    }
}
