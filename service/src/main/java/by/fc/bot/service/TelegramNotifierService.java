package by.fc.bot.service;

import by.fc.bot.component.TelegramBot;
import by.fc.bot.repository.blocking.UserBlockingRepository;
import com.fs.call_models.jooq.tables.pojos.CallRequests;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramNotifierService {

    private final TelegramBot telegramBot;
    private final UserBlockingRepository userBlockingRepository;

    public TelegramNotifierService(TelegramBot telegramBot, UserBlockingRepository userBlockingRepository) {
        this.telegramBot = telegramBot;
        this.userBlockingRepository = userBlockingRepository;
    }

    @RabbitListener(queues = "call_requests_queue")
    public void processCallRequest(CallRequests callRequest) {
        // Логика отправки в Telegram
        List<Long> verifyingUsers = userBlockingRepository.getAllVerifyingUsersIds();
        verifyingUsers.forEach(it ->{
            telegramBot.sendMenuInfo(it, 9);
                }
        );
        System.out.println("Отправка заявки в Telegram: " + callRequest);
    }
}

