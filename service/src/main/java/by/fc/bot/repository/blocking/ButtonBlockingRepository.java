package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.MenuInfo;

import by.sf.bot.jooq.tables.pojos.Buttons;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static by.sf.bot.jooq.tables.Buttons.BUTTONS;

@Repository
public class ButtonBlockingRepository {

    private final DSLContext dsl;

    public ButtonBlockingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Buttons getButtonByMenuAndLabel(int menuId, String label) {
        return dsl.select(BUTTONS.asterisk()).from(BUTTONS)
                .where(BUTTONS.MENU_ID.eq(
                        dsl.select(MenuInfo.MENU_INFO.MENU_ID)
                                .from(MenuInfo.MENU_INFO)
                                .where(MenuInfo.MENU_INFO.MENU_ID.eq(menuId))
                ).and(BUTTONS.LABEL.eq(label)))
                .fetchOptional()
                .map(record -> record.into(Buttons.class))
                .orElse(null);
    }

    public Buttons save(Buttons button) {
        button.setDateCreated(LocalDate.now());
        var newButtonRecord = dsl.newRecord(BUTTONS);
        newButtonRecord.from(button);
        newButtonRecord.reset(BUTTONS.BUTTON_ID);
        newButtonRecord.store();
        return newButtonRecord.into(Buttons.class);
    }

    public Integer getNextPositionByMenuId(int menuId) {
        var t1 = BUTTONS.as("t1");
        var t2 = BUTTONS.as("t2");

        return dsl.select(DSL.coalesce(DSL.min(t1.POSITION.add(1)), 1))
                .from(t1)
                .leftJoin(t2)
                .on(t1.POSITION.add(1).eq(t2.POSITION).and(t1.MENU_ID.eq(t2.MENU_ID)))
                .where(t1.MENU_ID.eq(menuId).and(t2.POSITION.isNull()))
                .fetchOneInto(Integer.class);
    }
}
