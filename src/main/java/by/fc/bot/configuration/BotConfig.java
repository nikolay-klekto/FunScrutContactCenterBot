//package by.fc.bot.configuration;
//
//import by.fc.bot.component.TelegramBot;
//import by.fc.bot.repository.async.ButtonRepository;
//import by.fc.bot.repository.async.MainBotInfoRepository;
//import by.fc.bot.repository.blocking.MenuInfoBlockingRepository;
//import by.fc.bot.repository.blocking.UserBlockingRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
//public class BotConfig {
//
//    @Bean
//    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        try {
//            botsApi.registerBot(telegramBot);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//        return botsApi;
//    }
//
//    @Bean
//    public TelegramBot telegramBot(
//            MainBotInfoRepository mainBotInfoRepository,
//            MenuInfoBlockingRepository menuInfoBlockingRepository,
//            ButtonRepository buttonRepository,
//            UserBlockingRepository userBlockingRepository
//    ) {
//        return new TelegramBot(mainBotInfoRepository, menuInfoBlockingRepository, buttonRepository, userBlockingRepository);
//    }
//}
