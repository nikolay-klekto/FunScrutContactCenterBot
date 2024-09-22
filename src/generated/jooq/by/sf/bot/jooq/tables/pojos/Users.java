/*
 * This file is generated by jOOQ.
 */
package by.sf.bot.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * Таблица для хранения информации о пользователях
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer   userId;
    private Long      telegramId;
    private String    username;
    private LocalDate dateCreated;

    public Users() {}

    public Users(Users value) {
        this.userId = value.userId;
        this.telegramId = value.telegramId;
        this.username = value.username;
        this.dateCreated = value.dateCreated;
    }

    public Users(
        Integer   userId,
        Long      telegramId,
        String    username,
        LocalDate dateCreated
    ) {
        this.userId = userId;
        this.telegramId = telegramId;
        this.username = username;
        this.dateCreated = dateCreated;
    }

    /**
     * Getter for <code>contact_center_bot.users.user_id</code>. Уникальный
     * идентификатор пользователя
     */
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * Setter for <code>contact_center_bot.users.user_id</code>. Уникальный
     * идентификатор пользователя
     */
    public Users setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Getter for <code>contact_center_bot.users.telegram_id</code>.
     * Идентификатор пользователя в Telegram
     */
    public Long getTelegramId() {
        return this.telegramId;
    }

    /**
     * Setter for <code>contact_center_bot.users.telegram_id</code>.
     * Идентификатор пользователя в Telegram
     */
    public Users setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    /**
     * Getter for <code>contact_center_bot.users.username</code>. Имя
     * пользователя в Telegram
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Setter for <code>contact_center_bot.users.username</code>. Имя
     * пользователя в Telegram
     */
    public Users setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Getter for <code>contact_center_bot.users.date_created</code>. Дата
     * создания записи
     */
    public LocalDate getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Setter for <code>contact_center_bot.users.date_created</code>. Дата
     * создания записи
     */
    public Users setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Users (");

        sb.append(userId);
        sb.append(", ").append(telegramId);
        sb.append(", ").append(username);
        sb.append(", ").append(dateCreated);

        sb.append(")");
        return sb.toString();
    }
}
