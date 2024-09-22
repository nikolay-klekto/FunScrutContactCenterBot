package by.fc.bot.repository.async;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static by.sf.bot.jooq.tables.Users.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Mono<Long> getAllUsersCount() {
        return Mono.fromSupplier(() ->
                dsl.selectCount().from(USERS)
                        .fetchOne(0, Long.class)
        ).subscribeOn(Schedulers.boundedElastic());
    }
}

