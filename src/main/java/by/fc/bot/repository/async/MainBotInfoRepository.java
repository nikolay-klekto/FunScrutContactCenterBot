package by.fc.bot.repository.async;

import by.sf.bot.jooq.tables.pojos.MainBotInfo;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static by.sf.bot.jooq.tables.MainBotInfo.MAIN_BOT_INFO;


@Repository
public class MainBotInfoRepository {

    private final DSLContext dsl;

    public MainBotInfoRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Mono<MainBotInfo> save(MainBotInfo mainBotInfo) {
        return Mono.fromSupplier(() -> {
            var newMainBotInfoRecord = dsl.newRecord(MAIN_BOT_INFO);
            newMainBotInfoRecord.from(mainBotInfo);
            newMainBotInfoRecord.reset(MAIN_BOT_INFO.ID_INFO);
            newMainBotInfoRecord.store();
            return newMainBotInfoRecord.into(MainBotInfo.class);
        });
    }

    public Mono<MainBotInfo> getMainBotInfoByKey(String key) {
        return Mono.fromSupplier(() ->
                dsl.select(MAIN_BOT_INFO.asterisk())
                        .from(MAIN_BOT_INFO)
                        .where(MAIN_BOT_INFO.KEY.eq(key))
                        .fetchOptional()
                        .map(record -> record.into(MainBotInfo.class))
                        .orElse(null)
        );
    }
}
