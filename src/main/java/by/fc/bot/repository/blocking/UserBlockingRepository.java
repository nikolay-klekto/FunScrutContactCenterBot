package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.Users;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static by.sf.bot.jooq.tables.Users.USERS;

@Repository
public class UserBlockingRepository {

    private final DSLContext dsl;

    public UserBlockingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Integer save(Users user) {
        var newUserRecord = dsl.newRecord(USERS);
        newUserRecord.from(user);
        newUserRecord.reset(USERS.USER_ID);
        newUserRecord.store();
        return newUserRecord.getUserId();
    }

    public boolean isUserExist(Long chatId) {
        return dsl.selectCount()
                .from(USERS)
                .where(USERS.TELEGRAM_ID.eq(chatId))
                .fetchOne(0, Long.class) > 0;
    }

    public Integer getUserIdByTelegramId(Long chatId) {
        return dsl.select(USERS.USER_ID)
                .from(USERS)
                .where(USERS.TELEGRAM_ID.eq(chatId))
                .fetchOneInto(Integer.class);
    }
}
