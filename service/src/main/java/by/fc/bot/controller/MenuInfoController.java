package by.fc.bot.controller;

import by.fc.bot.repository.async.MenuInfoRepository;
import by.sf.bot.jooq.tables.pojos.MenuInfo;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MenuInfoController {

    private final MenuInfoRepository menuInfoService;

    public MenuInfoController(MenuInfoRepository menuInfoService) {
        this.menuInfoService = menuInfoService;
    }

    @QueryMapping
    public Flux<MenuInfo> getAllMenuInfo() {
        return menuInfoService.getAllMenuInfo();
    }

    @QueryMapping
    public Mono<MenuInfo> getMenuInfoById(@Argument int menuId) {
        return menuInfoService.getMenuInfo(menuId);
    }

    @MutationMapping
    public Mono<MenuInfo> addMenuInfo(@Argument MenuInfo menuInfo) {
        return menuInfoService.save(menuInfo);
    }

    @MutationMapping
    public Mono<Boolean> updateMenuInfo(@Argument MenuInfo menuInfo) {
        return menuInfoService.update(menuInfo);
    }

    @MutationMapping
    public Mono<Boolean> deleteMenuInfoById(@Argument int menuId) {
        return menuInfoService.delete(menuId);
    }
}
