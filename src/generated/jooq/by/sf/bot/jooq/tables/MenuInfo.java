/*
 * This file is generated by jOOQ.
 */
package by.sf.bot.jooq.tables;


import by.sf.bot.jooq.ContactCenterBot;
import by.sf.bot.jooq.Keys;
import by.sf.bot.jooq.tables.records.MenuInfoRecord;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * Таблица для хранения информации о меню
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MenuInfo extends TableImpl<MenuInfoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>contact_center_bot.menu_info</code>
     */
    public static final MenuInfo MENU_INFO = new MenuInfo();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MenuInfoRecord> getRecordType() {
        return MenuInfoRecord.class;
    }

    /**
     * The column <code>contact_center_bot.menu_info.menu_id</code>. Уникальный
     * идентификатор меню
     */
    public final TableField<MenuInfoRecord, Integer> MENU_ID = createField(DSL.name("menu_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "Уникальный идентификатор меню");

    /**
     * The column <code>contact_center_bot.menu_info.description</code>.
     * Описание меню
     */
    public final TableField<MenuInfoRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.VARCHAR, this, "Описание меню");

    /**
     * The column <code>contact_center_bot.menu_info.parent_id</code>.
     * Идентификатор родительского меню
     */
    public final TableField<MenuInfoRecord, Integer> PARENT_ID = createField(DSL.name("parent_id"), SQLDataType.INTEGER, this, "Идентификатор родительского меню");

    /**
     * The column <code>contact_center_bot.menu_info.date_created</code>. Дата
     * создания записи
     */
    public final TableField<MenuInfoRecord, LocalDate> DATE_CREATED = createField(DSL.name("date_created"), SQLDataType.LOCALDATE, this, "Дата создания записи");

    /**
     * The column <code>contact_center_bot.menu_info.photo_name</code>.
     */
    public final TableField<MenuInfoRecord, String> PHOTO_NAME = createField(DSL.name("photo_name"), SQLDataType.VARCHAR, this, "");

    private MenuInfo(Name alias, Table<MenuInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private MenuInfo(Name alias, Table<MenuInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Таблица для хранения информации о меню"), TableOptions.table());
    }

    /**
     * Create an aliased <code>contact_center_bot.menu_info</code> table
     * reference
     */
    public MenuInfo(String alias) {
        this(DSL.name(alias), MENU_INFO);
    }

    /**
     * Create an aliased <code>contact_center_bot.menu_info</code> table
     * reference
     */
    public MenuInfo(Name alias) {
        this(alias, MENU_INFO);
    }

    /**
     * Create a <code>contact_center_bot.menu_info</code> table reference
     */
    public MenuInfo() {
        this(DSL.name("menu_info"), null);
    }

    public <O extends Record> MenuInfo(Table<O> child, ForeignKey<O, MenuInfoRecord> key) {
        super(child, key, MENU_INFO);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ContactCenterBot.CONTACT_CENTER_BOT;
    }

    @Override
    public Identity<MenuInfoRecord, Integer> getIdentity() {
        return (Identity<MenuInfoRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<MenuInfoRecord> getPrimaryKey() {
        return Keys.MENU_INFO_PKEY;
    }

    @Override
    public List<ForeignKey<MenuInfoRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MENU_INFO__MENU_INFO_PARENT_ID_FKEY);
    }

    private transient MenuInfo _menuInfo;

    /**
     * Get the implicit join path to the
     * <code>contact_center_bot.menu_info</code> table.
     */
    public MenuInfo menuInfo() {
        if (_menuInfo == null)
            _menuInfo = new MenuInfo(this, Keys.MENU_INFO__MENU_INFO_PARENT_ID_FKEY);

        return _menuInfo;
    }

    @Override
    public MenuInfo as(String alias) {
        return new MenuInfo(DSL.name(alias), this);
    }

    @Override
    public MenuInfo as(Name alias) {
        return new MenuInfo(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MenuInfo rename(String name) {
        return new MenuInfo(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MenuInfo rename(Name name) {
        return new MenuInfo(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, Integer, LocalDate, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
