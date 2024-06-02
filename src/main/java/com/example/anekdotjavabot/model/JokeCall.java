package com.example.anekdotjavabot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JokeCall {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "joke_call_seq")
    @SequenceGenerator(name = "joke_call_seq", sequenceName = "joke_call_seq", allocationSize = 1)
    private Long id;

    private Long userId;
    private LocalDateTime callTime;

    @ManyToOne
    @JoinColumn(name = "joke_id")
    private Joke joke;
}
