package com.example.anekdotjavabot.service;

import com.example.anekdotjavabot.model.Joke;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TelegramBotService {

    private final TelegramBot telegramBot;
    private final JokeService jokeService;

    private static final int MIN_JOKE_LENGTH = 10;
    private static final int MAX_JOKE_LENGTH = 500;

    @Autowired
    public TelegramBotService(TelegramBot telegramBot, JokeService jokeService) {
        this.telegramBot = telegramBot;
        this.jokeService = jokeService;
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::handleUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    @PostConstruct
    public void init() {
        setBotCommands();
    }

    private void setBotCommands() {
        BotCommand[] commands = {
                new BotCommand("/start", "Запустить бота"),
                new BotCommand("/joke", "Получить случайный анекдот"),
                new BotCommand("/topjokes", "Получить топ-5 анекдотов"),
                new BotCommand("/addjoke", "Добавить анекдот. Используйте формат: /addjoke <текст анекдота>"),
                new BotCommand("/deletejoke", "Удалить анекдот по id. Используйте формат: /deletejoke <id>"),
                new BotCommand("/updatejoke", "Обновить анекдот по id. Используйте формат: /updatejoke <id> <новый текст анекдота>"),
                new BotCommand("/alljokes", "Получить все анекдоты ")
        };
        telegramBot.execute(new SetMyCommands(commands));
    }

    private void handleUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String messageText = update.message().text();
            long chatId = update.message().chat().id();
            Long userId = update.message().from().id();  // Получение userId

            if ("/start".equals(messageText)) {
                sendWelcomeMessage(chatId);
            } else if ("/joke".equals(messageText)) {
                sendRandomJoke(chatId, userId);
            } else if ("/topjokes".equals(messageText)) {
                sendTopJokes(chatId);
            } else if ("/alljokes".equals(messageText)) {
                sendAllJokes(chatId);
            } else if (messageText.startsWith("/addjoke ")) {
                addJoke(chatId, messageText.substring(9));
            } else if (messageText.startsWith("/deletejoke ")) {
                deleteJoke(chatId, Long.parseLong(messageText.substring(12)));
            } else if (messageText.startsWith("/updatejoke ")) {
                updateJoke(chatId, messageText.substring(12));
            } else {
                sendUnknownCommandMessage(chatId);
            }
        }
    }

    private void sendWelcomeMessage(long chatId) {
        sendMessage(chatId, "Привет! Я бот для анекдотов. Используй команды /joke для получения случайного анекдота.");
    }

    private void sendRandomJoke(long chatId, Long userId) {
        jokeService.getRandomJoke().ifPresentOrElse(
                joke -> {
                    sendMessage(chatId, joke.getText());
                    jokeService.logJokeCall(userId, joke);  // Логируем вызов анекдота
                },
                () -> sendMessage(chatId, "Анекдотов пока нет.")
        );
    }

    private void sendTopJokes(long chatId) {
        List<Joke> jokes = jokeService.getTop5PopularJokes();
        if (jokes.isEmpty()) {
            sendMessage(chatId, "Анекдотов пока нет.");
        } else {
            jokes.forEach(joke -> sendMessage(chatId, "Анекдот #" + joke.getId() + ":\n" + joke.getText()));
        }
    }

    private void sendAllJokes(long chatId) {
        List<Joke> jokes = jokeService.getAllJokes(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        if (jokes.isEmpty()) {
            sendMessage(chatId, "Анекдотов пока нет.");
        } else {
            jokes.forEach(joke -> sendMessage(chatId, "Анекдот #" + joke.getId() + ":\n" + joke.getText()));
        }
    }

    private void addJoke(long chatId, String jokeText) {
        if (isJokeValid(jokeText)) {
            Joke joke = new Joke();
            joke.setText(jokeText);
            jokeService.createJoke(joke);
            sendMessage(chatId, "Анекдот добавлен.");
        } else {
            sendMessage(chatId, "Анекдот должен быть длиной от " + MIN_JOKE_LENGTH + " до " + MAX_JOKE_LENGTH + " символов.");
        }
    }

    private void deleteJoke(long chatId, Long jokeId) {
        jokeService.deleteJoke(jokeId);
        sendMessage(chatId, "Анекдот удален.");
    }

    private void updateJoke(long chatId, String messageText) {
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(chatId, "Используйте формат: /updatejoke <id> <новый текст анекдота>");
            return;
        }
        Long jokeId = Long.parseLong(parts[0]);
        String jokeText = parts[1];
        if (isJokeValid(jokeText)) {
            Joke updatedJoke = new Joke();
            updatedJoke.setText(jokeText);
            jokeService.updateJoke(jokeId, updatedJoke);
            sendMessage(chatId, "Анекдот обновлен.");
        } else {
            sendMessage(chatId, "Анекдот должен быть длиной от " + MIN_JOKE_LENGTH + " до " + MAX_JOKE_LENGTH + " символов.");
        }
    }

    private void sendUnknownCommandMessage(long chatId) {
        sendMessage(chatId, "Неизвестная команда. Используй /joke для получения случайного анекдота.");
    }

    private boolean isJokeValid(String jokeText) {
        return jokeText.length() >= MIN_JOKE_LENGTH && jokeText.length() <= MAX_JOKE_LENGTH;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        telegramBot.execute(request);
    }
}
