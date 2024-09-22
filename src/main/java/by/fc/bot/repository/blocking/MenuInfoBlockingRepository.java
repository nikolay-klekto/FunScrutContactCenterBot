package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.pojos.MenuInfo;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static by.sf.bot.jooq.tables.MenuInfo.MENU_INFO;

@Repository
public class MenuInfoBlockingRepository {

    private final DSLContext dsl;

    public MenuInfoBlockingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public MenuInfo getMenuInfoById(int menuId) {
        return dsl.select(MENU_INFO.MENU_ID, MENU_INFO.DESCRIPTION)
                .from(MENU_INFO)
                .where(MENU_INFO.MENU_ID.eq(menuId))
                .fetchOne()
                .into(MenuInfo.class);
    }

    public List<Integer> getAllMenuIds() {
        return dsl.select(MENU_INFO.MENU_ID)
                .from(MENU_INFO)
                .fetchInto(Integer.class);
    }

    public List<MenuInfo> getAllMenuModels() {
        return dsl.select(MENU_INFO.asterisk())
                .from(MENU_INFO)
                .fetchInto(MenuInfo.class);
    }
}
