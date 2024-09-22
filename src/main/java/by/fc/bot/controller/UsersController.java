package by.fc.bot.controller;

import by.fc.bot.repository.async.UserRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
public class UsersController {

    private final UserRepository userRepository;

    public UsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    public Mono<Long> getAllUsersCount() {
        return userRepository.getAllUsersCount()
                .subscribeOn(Schedulers.boundedElastic());
    }
}
