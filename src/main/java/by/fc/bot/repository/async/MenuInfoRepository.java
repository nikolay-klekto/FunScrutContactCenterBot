package by.fc.bot.repository.async;

import by.fc.bot.repository.blocking.MenuInfoBlockingRepository;
import by.sf.bot.jooq.tables.pojos.MenuInfo;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

import static by.sf.bot.jooq.tables.Buttons.BUTTONS;
import static by.sf.bot.jooq.tables.MenuInfo.MENU_INFO;

@Repository
public class MenuInfoRepository {

    private final MenuInfoBlockingRepository menuInfoBlockingRepository;
    private final DSLContext dsl;

    public MenuInfoRepository(MenuInfoBlockingRepository menuInfoBlockingRepository, DSLContext dsl) {
        this.menuInfoBlockingRepository = menuInfoBlockingRepository;
        this.dsl = dsl;
    }

    public Flux<MenuInfo> getAllMenuInfo() {
        return Flux.fromIterable(
                dsl.selectFrom(MENU_INFO)
                        .fetchInto(MenuInfo.class)
        );
    }

    public Mono<MenuInfo> getMenuInfo(int menuId) {
        return Mono.fromSupplier(() ->
                dsl.select(MENU_INFO.MENU_ID, MENU_INFO.DESCRIPTION)
                        .from(MENU_INFO)
                        .where(MENU_INFO.MENU_ID.eq(menuId))
                        .fetchOneInto(MenuInfo.class)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<MenuInfo> save(MenuInfo menuInfo) {
        return Mono.fromSupplier(() -> {
            menuInfo.setDateCreated(LocalDate.now());
            var newMenuInfoRecord = dsl.newRecord(MENU_INFO);
            newMenuInfoRecord.from(menuInfo);
            newMenuInfoRecord.reset(MENU_INFO.MENU_ID);
            newMenuInfoRecord.store();
            return newMenuInfoRecord.into(MenuInfo.class);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> update(MenuInfo menuInfo) {
        return Mono.fromSupplier(() -> {
            MenuInfo oldMenuModel = menuInfoBlockingRepository.getMenuInfoById(menuInfo.getMenuId());

            return dsl.update(MENU_INFO)
                    .set(MENU_INFO.DESCRIPTION, menuInfo.getDescription() != null ? menuInfo.getDescription() : oldMenuModel.getDescription())
                    .set(MENU_INFO.PARENT_ID, menuInfo.getParentId() != null ? menuInfo.getParentId() : oldMenuModel.getParentId())
                    .set(MENU_INFO.DATE_CREATED, LocalDate.now())
                    .where(MENU_INFO.MENU_ID.eq(menuInfo.getMenuId()))
                    .execute() == 1;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> delete(int menuId) {
        return Mono.fromSupplier(() -> {
            dsl.deleteFrom(BUTTONS)
                    .where(BUTTONS.MENU_ID.eq(menuId))
                    .execute();

            return dsl.deleteFrom(MENU_INFO)
                    .where(MENU_INFO.MENU_ID.eq(menuId))
                    .execute() == 1;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
