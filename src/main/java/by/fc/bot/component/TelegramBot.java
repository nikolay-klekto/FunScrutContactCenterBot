package by.fc.bot.component;

import by.fc.bot.repository.async.MainBotInfoRepository;
import jakarta.annotation.PostConstruct;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    private final MainBotInfoRepository mainBotInfoRepository;
    private String botUsername = "";
    private String botToken = "";

    public TelegramBot(MainBotInfoRepository mainBotInfoRepository) {
        this.mainBotInfoRepository = mainBotInfoRepository;
    }

    @PostConstruct
    public void init() {
        botUsername = mainBotInfoRepository.getMainBotInfoByKey(BOT_USERNAME).block().getValue();
        botToken = mainBotInfoRepository.getMainBotInfoByKey(BOT_TOKEN).block().getValue();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
            return;
        }

        Long chatId = update.getMessage().getChatId();
        // Handle the incoming message as needed
    }

    private void answerCallbackQuery(String callbackQueryId) throws TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        execute(answer);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String BOT_USERNAME = "botUsername";
    private static final String BOT_TOKEN = "botToken";
}
