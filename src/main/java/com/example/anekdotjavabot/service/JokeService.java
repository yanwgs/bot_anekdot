package com.example.anekdotjavabot.service;

import com.example.anekdotjavabot.model.Joke;
import com.example.anekdotjavabot.model.JokeCall;
import com.example.anekdotjavabot.repository.JokeRepository;
import com.example.anekdotjavabot.repository.JokeCallRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JokeService {

    private final JokeRepository jokeRepository;
    private final JokeCallRepository jokeCallRepository;

    public Page<Joke> getAllJokes(Pageable pageable) {
        return jokeRepository.findAll(pageable);
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

    public List<Joke> getTop5PopularJokes() {
        Pageable pageable = PageRequest.of(0, 5);
        return jokeRepository.findTop5PopularJokes(pageable).getContent();
    }

    public Optional<Joke> getRandomJoke() {
        return jokeRepository.findRandomJoke();
    }

    public void logJokeCall(Long userId, Joke joke) {
        JokeCall jokeCall = JokeCall.builder()
                .userId(userId)
                .callTime(LocalDateTime.now())
                .joke(joke)
                .build();
        jokeCallRepository.save(jokeCall);
    }
}
