package by.fc.bot.repository.blocking;

import by.fc.bot.service.PasswordService;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static by.sf.bot.jooq.tables.MainBotInfo.MAIN_BOT_INFO;

@Repository
public class MainBotInfoBlockingRepository {

    private final DSLContext dsl;
    private final PasswordService passwordService;

    public MainBotInfoBlockingRepository(DSLContext dsl, PasswordService passwordService) {
        this.dsl = dsl;
        this.passwordService = passwordService;
    }

    public boolean verifyPassword(String password){
        if(password != null && !password.isEmpty()){
            var encryptedPassword = dsl.select(MAIN_BOT_INFO.VALUE)
                    .from(MAIN_BOT_INFO)
                    .where(MAIN_BOT_INFO.KEY.eq("password"))
                    .fetchOptional()
                    .map(record -> record.get(MAIN_BOT_INFO.VALUE, String.class))
                    .orElse("error");

            return passwordService.verifyPassword(password, encryptedPassword);

        }else return false;
    }

}
