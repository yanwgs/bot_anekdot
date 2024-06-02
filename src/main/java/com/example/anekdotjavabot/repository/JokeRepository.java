package com.example.anekdotjavabot.repository;

import com.example.anekdotjavabot.model.Joke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JokeRepository extends JpaRepository<Joke, Long> {
    Page<Joke> findAll(Pageable pageable);

    @Query("SELECT j FROM Joke j LEFT JOIN JokeCall jc ON j.id = jc.joke.id GROUP BY j.id ORDER BY COUNT(jc.id) DESC")
    Page<Joke> findTop5PopularJokes(Pageable pageable);

    @Query(value = "SELECT * FROM joke ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Joke> findRandomJoke();
}
