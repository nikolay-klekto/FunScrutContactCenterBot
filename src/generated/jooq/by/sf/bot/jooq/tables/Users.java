/*
 * This file is generated by jOOQ.
 */
package by.sf.bot.jooq.tables;


import by.sf.bot.jooq.ContactCenterBot;
import by.sf.bot.jooq.Keys;
import by.sf.bot.jooq.tables.records.UsersRecord;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * Таблица для хранения информации о пользователях
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Users extends TableImpl<UsersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>contact_center_bot.users</code>
     */
    public static final Users USERS = new Users();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UsersRecord> getRecordType() {
        return UsersRecord.class;
    }

    /**
     * The column <code>contact_center_bot.users.user_id</code>. Уникальный
     * идентификатор пользователя
     */
    public final TableField<UsersRecord, Integer> USER_ID = createField(DSL.name("user_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "Уникальный идентификатор пользователя");

    /**
     * The column <code>contact_center_bot.users.telegram_id</code>.
     * Идентификатор пользователя в Telegram
     */
    public final TableField<UsersRecord, Long> TELEGRAM_ID = createField(DSL.name("telegram_id"), SQLDataType.BIGINT, this, "Идентификатор пользователя в Telegram");

    /**
     * The column <code>contact_center_bot.users.username</code>. Имя
     * пользователя в Telegram
     */
    public final TableField<UsersRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR, this, "Имя пользователя в Telegram");

    /**
     * The column <code>contact_center_bot.users.date_created</code>. Дата
     * создания записи
     */
    public final TableField<UsersRecord, LocalDate> DATE_CREATED = createField(DSL.name("date_created"), SQLDataType.LOCALDATE, this, "Дата создания записи");

    private Users(Name alias, Table<UsersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Users(Name alias, Table<UsersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Таблица для хранения информации о пользователях"), TableOptions.table());
    }

    /**
     * Create an aliased <code>contact_center_bot.users</code> table reference
     */
    public Users(String alias) {
        this(DSL.name(alias), USERS);
    }

    /**
     * Create an aliased <code>contact_center_bot.users</code> table reference
     */
    public Users(Name alias) {
        this(alias, USERS);
    }

    /**
     * Create a <code>contact_center_bot.users</code> table reference
     */
    public Users() {
        this(DSL.name("users"), null);
    }

    public <O extends Record> Users(Table<O> child, ForeignKey<O, UsersRecord> key) {
        super(child, key, USERS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ContactCenterBot.CONTACT_CENTER_BOT;
    }

    @Override
    public Identity<UsersRecord, Integer> getIdentity() {
        return (Identity<UsersRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<UsersRecord> getPrimaryKey() {
        return Keys.USERS_PKEY;
    }

    @Override
    public Users as(String alias) {
        return new Users(DSL.name(alias), this);
    }

    @Override
    public Users as(Name alias) {
        return new Users(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Users rename(String name) {
        return new Users(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Users rename(Name name) {
        return new Users(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, Long, String, LocalDate> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
