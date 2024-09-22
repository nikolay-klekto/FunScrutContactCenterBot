package by.fc.bot.controller;

import by.fc.bot.repository.async.ButtonRepository;
import by.sf.bot.jooq.tables.pojos.Buttons;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ButtonController {

    private final ButtonRepository buttonRepository;

    public ButtonController(ButtonRepository buttonRepository) {
        this.buttonRepository = buttonRepository;
    }

    @QueryMapping
    public Flux<Buttons> getAllButtonsInfo() {
        return buttonRepository.getAllButtonsInfo();
    }

    @QueryMapping
    public Flux<Buttons> getButtonsByMenuId(@Argument int menuId) {
        return Flux.fromIterable(buttonRepository.getAllButtonsByMenuId(menuId));
    }

    @MutationMapping
    public Mono<Buttons> addButton(@Argument Buttons button) {
        return buttonRepository.save(button);
    }

    @MutationMapping
    public Mono<Boolean> updateButton(@Argument int menuId, @Argument String label, @Argument Buttons button) {
        return buttonRepository.update(menuId, label, button);
    }

    @MutationMapping
    public Mono<Boolean> deleteButton(@Argument int menuId, @Argument String label) {
        return buttonRepository.delete(menuId, label);
    }
}
