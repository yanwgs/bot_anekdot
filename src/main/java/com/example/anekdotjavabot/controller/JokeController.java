// src/main/java/com/example/anekdotjavabot/controller/JokeController.java

package com.example.anekdotjavabot.controller;

import com.example.anekdotjavabot.model.Joke;
import com.example.anekdotjavabot.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/jokes")
@RequiredArgsConstructor
public class JokeController {

    private final JokeService jokeService;

    @GetMapping
    public List<Joke> getAllJokes() {
        return jokeService.getAllJokes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Joke> getJokeById(@PathVariable Long id) {
        return jokeService.getJokeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Joke createJoke(@RequestBody Joke joke) {
        return jokeService.createJoke(joke);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Joke> updateJoke(@PathVariable Long id, @RequestBody Joke updatedJoke) {
        return jokeService.updateJoke(id, updatedJoke)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoke(@PathVariable Long id) {
        jokeService.deleteJoke(id);
        return ResponseEntity.noContent().build();
    }
}
