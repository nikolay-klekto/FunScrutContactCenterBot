package by.fc.bot.service;

import by.fc.bot.component.TelegramBot;
import by.fc.bot.repository.blocking.UserBlockingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fs.call_models.jooq.tables.pojos.CallRequests;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramNotifierService {

    private final TelegramBot telegramBot;
    private final UserBlockingRepository userBlockingRepository;
    private final ObjectMapper objectMapper;


    public TelegramNotifierService(TelegramBot telegramBot, UserBlockingRepository userBlockingRepository) {
        this.telegramBot = telegramBot;
        this.userBlockingRepository = userBlockingRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new KotlinModule())  // Поддержка Kotlin-классов
                .registerModule(new JavaTimeModule());  // Поддержка LocalDateTime и других Java 8+ типов
    }

    @RabbitListener(queues = "call_requests_queue")
    public void receiveMessage(String jsonMessage) {
        try {
            CallRequests callRequest = objectMapper.readValue(jsonMessage, CallRequests.class);
            System.out.println("Получен запрос: " + callRequest);

            // Логика отправки в Telegram
            List<Long> verifyingUsers = userBlockingRepository.getAllVerifyingUsersIds();
            verifyingUsers.forEach(it -> {
                        telegramBot.sendMenuInfo(it, 9);
                    }
            );

            System.out.println("Отправка заявки в Telegram: " + callRequest);

        } catch (Exception e) {
            System.err.println("Ошибка десериализации: " + e.getMessage());
        }

    }
}

