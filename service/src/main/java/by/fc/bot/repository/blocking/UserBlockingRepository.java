package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.pojos.Users;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static by.sf.bot.jooq.tables.Users.USERS;

@Repository
public class UserBlockingRepository {

    private final DSLContext dsl;

    public UserBlockingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void save(Users user) {
        var newUserRecord = dsl.newRecord(USERS);
        newUserRecord.from(user);
        newUserRecord.reset(USERS.USER_ID);
        newUserRecord.store();
    }

    public void update(Users users) {
        dsl.update(USERS)
                .set(USERS.IS_VERIFIED, users.getIsVerified())
                .where(USERS.TELEGRAM_ID.eq(users.getTelegramId()))
                .execute();
    }

    public boolean isUserExist(Long chatId) {
        Long count = dsl.selectCount()
                .from(USERS)
                .where(USERS.TELEGRAM_ID.eq(chatId))
                .fetchOne(0, Long.class);

        return count != null && count > 0;
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

    public List<Long> getAllVerifyingUsersIds(){
        return dsl.select(USERS.TELEGRAM_ID)
                .from(USERS)
                .where(USERS.IS_VERIFIED.eq(Boolean.TRUE))
                .fetchInto(Long.class);
    }
}
