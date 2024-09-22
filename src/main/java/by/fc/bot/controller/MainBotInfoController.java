package by.fc.bot.controller;

import by.fc.bot.repository.async.MainBotInfoRepository;
import by.sf.bot.jooq.tables.pojos.MainBotInfo;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MainBotInfoController {

    private final MainBotInfoRepository mainBotInfoRepository;

    public MainBotInfoController(MainBotInfoRepository mainBotInfoRepository) {
        this.mainBotInfoRepository = mainBotInfoRepository;
    }

    @MutationMapping
    public Mono<MainBotInfo> addMainBotInfo(@Argument MainBotInfo mainBotInfo) {
        return mainBotInfoRepository.save(mainBotInfo);
    }

    @QueryMapping
    public Mono<MainBotInfo> getMainBotInfoByKey(@Argument String key) {
        return mainBotInfoRepository.getMainBotInfoByKey(key);
    }
}
