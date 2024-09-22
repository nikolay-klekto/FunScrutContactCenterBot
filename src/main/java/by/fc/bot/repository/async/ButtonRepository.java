package by.fc.bot.repository.async;

import by.fc.bot.repository.blocking.ButtonBlockingRepository;
import by.sf.bot.jooq.tables.MenuInfo;
import by.sf.bot.jooq.tables.pojos.Buttons;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.List;

import static by.sf.bot.jooq.tables.Buttons.BUTTONS;

@Repository
public class ButtonRepository {

    private final DSLContext dsl;
    private final ButtonBlockingRepository buttonBlockingRepository;

    public ButtonRepository(DSLContext dsl, ButtonBlockingRepository buttonBlockingRepository) {
        this.dsl = dsl;
        this.buttonBlockingRepository = buttonBlockingRepository;
    }

    public Flux<Buttons> getAllButtonsInfo() {
        return Flux.fromIterable(
                dsl.selectFrom(BUTTONS)
                        .fetchInto(Buttons.class)
        );
    }

    public List<Buttons> getAllButtonsByMenuId(int menuId) {
        return dsl.selectFrom(BUTTONS)
                .where(BUTTONS.MENU_ID.eq(menuId))
                .orderBy(BUTTONS.POSITION.asc())
                .fetchInto(Buttons.class);
    }

    public Mono<Buttons> save(Buttons button) {
        return Mono.fromSupplier(() -> {
            button.setDateCreated(LocalDate.now());
            var newButtonRecord = dsl.newRecord(BUTTONS);
            newButtonRecord.from(button);
            newButtonRecord.reset(BUTTONS.BUTTON_ID);
            newButtonRecord.store();
            return newButtonRecord.into(Buttons.class);
        }).subscribeOn(Schedulers.boundedElastic()); // Оборачиваем в асинхронный планировщик
    }

    public Mono<Boolean> update(int menuId, String label, Buttons button) {
        return Mono.fromSupplier(() -> {
            Buttons oldButton = buttonBlockingRepository.getButtonByMenuAndLabel(menuId, label);
            if (oldButton == null) {
                return false;
            }

            return dsl.update(BUTTONS)
                    .set(BUTTONS.MENU_ID, button.getMenuId() != null ? button.getMenuId() : oldButton.getMenuId())
                    .set(BUTTONS.POSITION, button.getPosition() != null ? button.getPosition() : oldButton.getPosition())
                    .set(BUTTONS.LABEL, button.getLabel() != null ? button.getLabel() : oldButton.getLabel())
                    .set(BUTTONS.ACTION_TYPE, button.getActionType() != null ? button.getActionType() : oldButton.getActionType())
                    .set(BUTTONS.ACTION_DATA, button.getActionData() != null ? button.getActionData() : oldButton.getActionData())
                    .set(BUTTONS.DATE_CREATED, LocalDate.now())
                    .where(BUTTONS.BUTTON_ID.eq(oldButton.getButtonId()))
                    .execute() == 1;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> delete(int menuId, String label) {
        return Mono.fromSupplier(() -> {
            return dsl.deleteFrom(BUTTONS)
                    .where(BUTTONS.MENU_ID.eq(
                            dsl.select(MenuInfo.MENU_INFO.MENU_ID)
                                    .from(MenuInfo.MENU_INFO)
                                    .where(MenuInfo.MENU_INFO.MENU_ID.eq(menuId))
                    ))
                    .and(BUTTONS.LABEL.eq(label))
                    .execute() == 1;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
