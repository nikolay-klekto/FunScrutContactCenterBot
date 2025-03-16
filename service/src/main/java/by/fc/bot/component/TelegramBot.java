package by.fc.bot.component;

import by.fc.bot.repository.async.ButtonRepository;
import by.fc.bot.repository.async.MainBotInfoRepository;
import by.fc.bot.repository.blocking.CallRequestRepository;
import by.fc.bot.repository.blocking.MainBotInfoBlockingRepository;
import by.fc.bot.repository.blocking.MenuInfoBlockingRepository;
import by.fc.bot.repository.blocking.UserBlockingRepository;
import by.fc.bot.service.PasswordService;
import by.sf.bot.jooq.tables.pojos.Buttons;
import by.sf.bot.jooq.tables.pojos.MenuInfo;
import by.sf.bot.jooq.tables.pojos.Users;
import jakarta.annotation.PostConstruct;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {

    private final MainBotInfoRepository mainBotInfoRepository;
    private final MenuInfoBlockingRepository menuInfoBlockingRepository;
    private final ButtonRepository buttonRepository;
    private final UserBlockingRepository userBlockingRepository;

    private final CallRequestRepository callRequestRepository;

    private final MainBotInfoBlockingRepository mainBotInfoBlockingRepository;

    private String botUsername = "";
    private String botToken = "";
    private Map<Integer, Map<Integer, Buttons>> menuWithButtonsCollection = new HashMap<>();
    private List<MenuInfo> menuInfoList = new ArrayList<>();

    private final Map<Long, Boolean> waitingForRequestId = new HashMap<>();


    private Map<Long, Boolean> usersVerificationStates = new HashMap<>();

    public TelegramBot(MainBotInfoRepository mainBotInfoRepository,
                       MenuInfoBlockingRepository menuInfoBlockingRepository,
                       ButtonRepository buttonRepository,
                       UserBlockingRepository userBlockingRepository, PasswordService passwordService, CallRequestRepository callRequestRepository, MainBotInfoBlockingRepository mainBotInfoBlockingRepository) {
        this.mainBotInfoRepository = mainBotInfoRepository;
        this.menuInfoBlockingRepository = menuInfoBlockingRepository;
        this.buttonRepository = buttonRepository;
        this.userBlockingRepository = userBlockingRepository;
        this.callRequestRepository = callRequestRepository;
        this.mainBotInfoBlockingRepository = mainBotInfoBlockingRepository;
    }

    @PostConstruct
    public void init() {
        botUsername = mainBotInfoRepository.getMainBotInfoByKey(BOT_USERNAME).block().getValue();
        botToken = mainBotInfoRepository.getMainBotInfoByKey(BOT_TOKEN).block().getValue();
        menuInfoList = menuInfoBlockingRepository.getAllMenuModels();
        usersVerificationStates = userBlockingRepository.getUserVerifiedStatus();

        List<Integer> listMenuIds = menuInfoBlockingRepository.getAllMenuIds();

        listMenuIds.forEach(menuId -> {
            List<Buttons> currentButtons = buttonRepository.getAllButtonsByMenuId(menuId);
            currentButtons.forEach(currentButton -> {
                menuWithButtonsCollection.computeIfAbsent(menuId, k -> new HashMap<>())
                        .put(currentButton.getPosition(), currentButton);
            });
        });
    }

//    private InputFile loadImage(String resourcePath) throws Exception {
//        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
//        if (resourceStream == null) {
//            throw new IllegalArgumentException("Image not found: " + resourcePath);
//        }
//
//        File tempFile = File.createTempFile("temp", ".jpg");
//        try (resourceStream; InputStream input = resourceStream) {
//            input.transferTo(tempFile.toPath());
//        }
//        return new InputFile(tempFile);
//    }

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
            String callbackQueryId = update.getCallbackQuery().getId();
            String callbackData = update.getCallbackQuery().getData();
            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
            waitingForRequestId.remove(callbackChatId);

            answerCallbackQuery(callbackQueryId);

            switch (callbackData) {
                case CALLBACK_DATA_MENU_ID_3 -> {
                    sendMenuInfo(callbackChatId, 3);
                    waitingForRequestId.put(callbackChatId, true);
                }
                case CALLBACK_DATA_ONE_DAY -> sendAllIssuesByPeriodMessage(callbackChatId, DAY);
                case CALLBACK_DATA_TWO_DAYS -> sendAllIssuesByPeriodMessage(callbackChatId, TWO_DAYS);
                case CALLBACK_DATA_ONE_WEEK -> sendAllIssuesByPeriodMessage(callbackChatId, WEEK);
            }
            return;
        }

        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        String messageText = update.getMessage().getText();
        Map<Integer, Buttons> startButtons = menuWithButtonsCollection.get(PAGE_AFTER_LOGIN_MENU_ID);

        if(!usersVerificationStates.containsKey(chatId)){
            usersVerificationStates.put(chatId, false);
            Users newUser = new Users(
                    null, chatId, username, LocalDate.now(), false
            );
            userBlockingRepository.save(newUser);
        }

        if (START_MESSAGE.equals(messageText)) {
            try {
                if(usersVerificationStates.get(chatId)){
                    sendStartMessageAfterVerification(chatId);
                }else{
                    usersVerificationStates.putIfAbsent(chatId, false);
                    sendStartMessage(chatId);
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        else if (!usersVerificationStates.get(chatId)) {
            handleVerifyingUser(messageText, chatId);
        }
        else if (startButtons != null) {
            if (startButtons.get(1).getLabel().equals(messageText)) {
                waitingForRequestId.remove(chatId);
                sendNotCompletedIssuesMessage(chatId);
            } else if (startButtons.get(2).getLabel().equals(messageText)) {
                if(callRequestRepository.getClientNotCompletedRequests().isEmpty()){
                    sendMenuInfo(chatId, 6);
                }else{
                    waitingForRequestId.put(chatId, true);
                    sendMenuInfo(chatId, 3);
                }
            } else if (waitingForRequestId.getOrDefault(chatId, false)) {
                try {
                    Long requestId = Long.parseLong(messageText);
                    completeIssue(requestId);
                    sendMessage(chatId, "Заявка " + requestId + " обработана успешно.");
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Некорректный ID.");
                }
                sendMenuInfo(chatId, MENU_ID_CONTINUE_WORK);
                waitingForRequestId.remove(chatId);
            }
            else if (startButtons.get(3).getLabel().equals(messageText)) {
                sendMenuInfo(chatId, 4);
            }
            else{
                waitingForRequestId.remove(chatId);
                sendMenuInfo(chatId, MENU_ID_COMPLETE_ISSUES);

            }
        }
    }

    private void handleVerifyingUser(String message, long chatId) {
        if(mainBotInfoBlockingRepository.verifyPassword(message)){
            usersVerificationStates.replace(chatId, true);
            Users updatableUser = new Users(null, chatId, null, null, true);
            userBlockingRepository.update(updatableUser);
            sendStartMessageAfterVerification(chatId);
        }else {
            sendMessage(chatId, VERIFICATION_FAIL_MESSAGE);
        }
    }

    private void answerCallbackQuery(String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStartMessage(long chatId) throws TelegramApiException {
        MenuInfo currentMenuModel = menuInfoList.stream()
                .filter(menuInfo -> menuInfo.getParentId() == null)
                .findFirst()
                .orElse(null);

        String text = currentMenuModel.getDescription();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        execute(message);
    }

    private void sendNotCompletedIssuesMessage(long chatId) {

        List<String> allNotCompletedIssues = callRequestRepository.getClientNotCompletedRequests();

        if(!allNotCompletedIssues.isEmpty()){
        allNotCompletedIssues.forEach(it -> {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(it);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });

        sendMenuInfo(chatId, MENU_ID_CONTINUE_WORK);
        }else{
            sendMenuInfo(chatId, MENU_ID_ALL_ISSUES_COMPLETED);
        }
    }

    private void sendAllIssuesByPeriodMessage(long chatId, int daysQuantity) {

        List<String> allIssues = callRequestRepository.getClientRequestsByPeriod(daysQuantity);

        if(!allIssues.isEmpty()){
            allIssues.forEach(it -> {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText(it);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });

            sendMenuInfo(chatId, MENU_ID_CONTINUE_WORK);
        }else{
            sendMenuInfo(chatId, MENU_ID_IS_NOT_NEW_ISSUES_BY_PERIOD);
        }
    }

    private void completeIssue(Long issueId){
        boolean result = callRequestRepository.completeRequest(issueId);
        if(!result){
            throw new NumberFormatException();
        }
    }

    private void sendStartMessageAfterVerification(long chatId) {
        MenuInfo currentMenuModel = menuInfoList.stream()
                .filter(menuInfo ->
                                menuInfo.getParentId() != null && menuInfo.getParentId() == START_PAGE_MENU_ID
                )
                .findFirst()
                .orElse(null);

        if (currentMenuModel == null) {
            System.out.println("Error: No menu model found for the given parent ID.");
            return; // Не выполняем дальнейшие действия, если объект не найден
        }

        Map<Integer, Buttons> currentButtonList = menuWithButtonsCollection.get(currentMenuModel.getMenuId());

        if (currentButtonList == null) {
            System.out.println("Error: No buttons found for menu ID: " + currentMenuModel.getMenuId());
            return;
        }

        String text = currentMenuModel.getDescription();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(currentButtonList.get(1).getLabel());
        row1.add(currentButtonList.get(2).getLabel());
        row1.add(currentButtonList.get(3).getLabel());

        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendMenuInfo(long chatId, int menuId) {

        MenuInfo currentMenuModel = menuInfoList.stream()
                .filter(menuInfo -> menuInfo.getMenuId() == menuId)
                .findFirst()
                .orElse(null);

        Map<Integer, Buttons> currentButtonList = menuWithButtonsCollection.get(currentMenuModel.getMenuId());
        String text = currentMenuModel.getDescription();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if(currentButtonList != null){
            currentButtonList.values().forEach(button -> {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(button.getLabel());

                if (ACTION_TYPE_CALLBACK.equals(button.getActionType()) && button.getActionData() != null) {
                    inlineKeyboardButton.setCallbackData(button.getActionData());
                    keyboard.add(List.of(inlineKeyboardButton));
                }
            });
        }

        if (!keyboard.isEmpty()) {
            inlineKeyboardMarkup.setKeyboard(keyboard);
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        if (!keyboard.isEmpty()) {
            message.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int START_PAGE_MENU_ID = 1;
    private static final int PAGE_AFTER_LOGIN_MENU_ID = 2;
    private static final String BOT_USERNAME = "botUsername";
    private static final String BOT_TOKEN = "botToken";
    private static final String START_MESSAGE = "/start";

    private static final String ACTION_TYPE_CALLBACK = "callback";

    private static final String VERIFICATION_FAIL_MESSAGE = "Пароль неверный, проверьте, и попробуйте снова";

    private static final String CALLBACK_DATA_MENU_ID_3 = "menu_id:3";

    private static final String CALLBACK_DATA_ONE_DAY = "oneDay";

    private static final String CALLBACK_DATA_TWO_DAYS = "twoDays";

    private static final String CALLBACK_DATA_ONE_WEEK = "oneWeek";

    private static final int MENU_ID_CONTINUE_WORK = 5;

    private static final int MENU_ID_ALL_ISSUES_COMPLETED = 6;

    private static final int MENU_ID_IS_NOT_NEW_ISSUES_BY_PERIOD = 8;

    private static final int MENU_ID_COMPLETE_ISSUES = 7;

    private static final int DAY = 1;

    private static final int TWO_DAYS = 2;
    private static final int WEEK = 7;


}
