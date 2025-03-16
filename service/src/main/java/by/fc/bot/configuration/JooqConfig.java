package by.fc.bot.configuration;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@Configuration
public class JooqConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://45.135.234.61:15432/fun_scrut")
                .username("username")
                .password("password")
                .build();
    }

    @Bean
    public DSLContext dsl(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }
}
