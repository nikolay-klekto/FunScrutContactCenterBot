package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.pojos.Users;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Map;

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

    public int update(Users users) {
        return dsl.update(USERS)
                .set(USERS.IS_VERIFIED, users.getIsVerified())
                .where(USERS.TELEGRAM_ID.eq(users.getTelegramId()))
                .execute();
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

    public Map<Long, Boolean> getUserVerifiedStatus() {
        return dsl.select(USERS.TELEGRAM_ID, USERS.IS_VERIFIED)
                .from(USERS)
                .fetch()
                .intoMap(USERS.TELEGRAM_ID, USERS.IS_VERIFIED);
    }
}
