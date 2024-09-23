//package by.fc.bot.component;
//
//import by.fc.bot.repository.async.ButtonRepository;
//import by.fc.bot.repository.async.MainBotInfoRepository;
//import by.fc.bot.repository.blocking.MenuInfoBlockingRepository;
//import by.fc.bot.repository.blocking.UserBlockingRepository;
//import by.sf.bot.jooq.tables.pojos.Buttons;
//import by.sf.bot.jooq.tables.pojos.MenuInfo;
//import jakarta.annotation.PostConstruct;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.InputFile;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
//
//import java.io.File;
//import java.io.InputStream;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TelegramBot extends TelegramLongPollingBot {
//
//    private final MainBotInfoRepository mainBotInfoRepository;
//    private final MenuInfoBlockingRepository menuInfoBlockingRepository;
//    private final ButtonRepository buttonRepository;
//    private final UserBlockingRepository userBlockingRepository;
//
//    private String botUsername = "";
//    private String botToken = "";
//    private Map<Integer, Map<Integer, Buttons>> menuWithButtonsCollection = new HashMap<>();
//    private List<MenuInfo> menuInfoList = new ArrayList<>();
//
//    public TelegramBot(MainBotInfoRepository mainBotInfoRepository,
//                       MenuInfoBlockingRepository menuInfoBlockingRepository,
//                       ButtonRepository buttonRepository,
//                       UserBlockingRepository userBlockingRepository) {
//        this.mainBotInfoRepository = mainBotInfoRepository;
//        this.menuInfoBlockingRepository = menuInfoBlockingRepository;
//        this.buttonRepository = buttonRepository;
//        this.userBlockingRepository = userBlockingRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        botUsername = mainBotInfoRepository.getMainBotInfoByKey(BOT_USERNAME).block().getValue();
//        botToken = mainBotInfoRepository.getMainBotInfoByKey(BOT_TOKEN).block().getValue();
//        menuInfoList = menuInfoBlockingRepository.getAllMenuModels();
//
//        List<Integer> listMenuIds = menuInfoBlockingRepository.getAllMenuIds();
//
//        listMenuIds.forEach(menuId -> {
//            List<Buttons> currentButtons = buttonRepository.getAllButtonsByMenuId(menuId);
//            currentButtons.forEach(currentButton -> {
//                menuWithButtonsCollection.computeIfAbsent(menuId, k -> new HashMap<>())
//                        .put(currentButton.getPosition(), currentButton);
//            });
//        });
//    }
//
////    private InputFile loadImage(String resourcePath) throws Exception {
////        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
////        if (resourceStream == null) {
////            throw new IllegalArgumentException("Image not found: " + resourcePath);
////        }
////
////        File tempFile = File.createTempFile("temp", ".jpg");
////        try (resourceStream; InputStream input = resourceStream) {
////            input.transferTo(tempFile.toPath());
////        }
////        return new InputFile(tempFile);
////    }
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasCallbackQuery()) {
//            String callbackQueryId = update.getCallbackQuery().getId();
//            String callbackData = update.getCallbackQuery().getData();
//            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
//
//            answerCallbackQuery(callbackQueryId);
//
//            switch (callbackData) {
//                case REMINDER_MESSAGE_ALL -> sendReminderOptions(callbackChatId, MESSAGE_ALL);
//                case REMINDER_MESSAGE_DELETE -> sendReminderOptions(callbackChatId, null);
//                case CALLBACK_DATA_MENU_ID_6 -> sendMenuInfo(callbackChatId, 6);
//                case CALLBACK_DATA_MENU_ID_7 -> sendMenuInfo(callbackChatId, 7);
//                case CALLBACK_DATA_MENU_ID_3 -> sendMenuInfo(callbackChatId, 3);
//                case CALLBACK_DATA_MENU_ID_9 -> sendMenuInfo(callbackChatId, 9);
//                case CALLBACK_DATA_MENU_ID_10 -> sendMenuInfo(callbackChatId, 10);
//                case CALLBACK_DATA_MENU_ID_11 -> sendMenuInfo(callbackChatId, 11);
//                case CALLBACK_DATA_MENU_ID_13 -> startSurvey(callbackChatId);
//                case CALLBACK_DATA_SHOW_MATCHES, CALLBACK_DATA_NEXT_MATCH -> showNextMatch(callbackChatId);
//                case CALLBACK_DATA_DELETE_PROFILE -> deleteMatchProfile(callbackChatId);
//                default -> {
//                    if (callbackData.startsWith(REMINDER_MESSAGE_YES)) {
//                        String reminders = callbackData.split(REMINDER_MESSAGE_DELIMITER)[1];
//                        sendReminderOptions(callbackChatId, reminders);
//                    } else {
//                        handleCallbackQuery(callbackChatId, callbackData);
//                    }
//                }
//            }
//            return;
//        }
//
//        long chatId = update.getMessage().getChatId();
//        String messageText = update.getMessage().getText();
//        Map<Integer, Buttons> startButtons = menuWithButtonsCollection.get(START_PAGE_MENU_ID);
//
//        if (START_MESSAGE.equals(messageText)) {
//            sendStartMessage(chatId);
//        } else if (startButtons != null) {
//            if (startButtons.get(1).getLabel().equals(messageText)) {
//                sendMenuInfo(chatId, 2);
//            } else if (startButtons.get(2).getLabel().equals(messageText)) {
//                sendMenuInfo(chatId, 3);
//            } else if (startButtons.get(3).getLabel().equals(messageText)) {
//                sendMenuInfo(chatId, 4);
//            } else if (startButtons.get(4).getLabel().equals(messageText)) {
//                sendMenuInfo(chatId, 12);
//            } else {
//                handleUserResponse(chatId, messageText);
//            }
//        }
//    }
//
//    private void answerCallbackQuery(String callbackQueryId) {
//        AnswerCallbackQuery answer = new AnswerCallbackQuery();
//        answer.setCallbackQueryId(callbackQueryId);
//        execute(answer);
//    }
//
//    public void sendMessage(long chatId, String text) {
//        SendMessage message = new SendMessage(String.valueOf(chatId), text);
//        execute(message);
//    }
//
//    private void sendStartMessage(long chatId) {
//        MenuInfo currentMenuModel = menuInfoList.stream()
//                .filter(menuInfo -> menuInfo.getParentId() == null)
//                .findFirst()
//                .orElse(null);
//
//        Map<Integer, Buttons> currentButtonList = menuWithButtonsCollection.get(currentMenuModel.getMenuId());
//
//        String text = currentMenuModel.getDescription();
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> keyboard = new ArrayList<>();
//
//        KeyboardRow row1 = new KeyboardRow();
//        row1.add(currentButtonList.get(1).getLabel());
//        row1.add(currentButtonList.get(2).getLabel());
//
//        KeyboardRow row2 = new KeyboardRow();
//        row2.add(currentButtonList.get(4).getLabel());
//
//        KeyboardRow row3 = new KeyboardRow();
//        row3.add(currentButtonList.get(3).getLabel());
//
//        keyboard.add(row1);
//        keyboard.add(row2);
//        keyboard.add(row3);
//
//        keyboardMarkup.setKeyboard(keyboard);
//        keyboardMarkup.setResizeKeyboard(true);
//
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.setReplyMarkup(keyboardMarkup);
//        execute(message);
//    }
//
//    private void sendMenuInfo(long chatId, int menuId) {
//        MenuInfo currentMenuModel = menuInfoList.stream()
//                .filter(menuInfo -> menuInfo.getMenuId() == menuId)
//                .findFirst()
//                .orElse(null);
//
//        Map<Integer, Buttons> currentButtonList = menuWithButtonsCollection.get(currentMenuModel.getMenuId());
//        String text = currentMenuModel.getDescription();
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//
//        currentButtonList.values().forEach(button -> {
//            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//            inlineKeyboardButton.setText(button.getLabel());
//
//            if (ACTION_TYPE_URL.equals(button.getActionType()) && button.getActionData() != null) {
//                inlineKeyboardButton.setUrl(button.getActionData());
//                keyboard.add(List.of(inlineKeyboardButton));
//            } else if (ACTION_TYPE_CALLBACK.equals(button.getActionType()) && button.getActionData() != null) {
//                inlineKeyboardButton.setCallbackData(button.getActionData());
//                keyboard.add(List.of(inlineKeyboardButton));
//            }
//        });
//
//        if (!keyboard.isEmpty()) {
//            inlineKeyboardMarkup.setKeyboard(keyboard);
//        }
//
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        if (!keyboard.isEmpty()) {
//            message.setReplyMarkup(inlineKeyboardMarkup);
//        }
//        execute(message);
//    }
//
//    // Implement other methods like handleUserResponse, handleCallbackQuery, etc.
//
//    private static final int START_PAGE_MENU_ID = 1;
//    private static final String BOT_USERNAME = "botUsername";
//    private static final String BOT_TOKEN = "botToken";
//    private static final String START_MESSAGE = "/start";
//    private static final String REMINDER_MESSAGE_YES = "reminder_yes";
//    private static final String REMINDER_MESSAGE_ALL = "reminder_all";
//    private static final String REMINDER_MESSAGE_DELETE = "reminder_delete";
//    private static final String MESSAGE
