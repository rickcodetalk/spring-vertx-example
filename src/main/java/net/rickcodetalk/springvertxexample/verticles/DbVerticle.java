package net.rickcodetalk.springvertxexample.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.rickcodetalk.springvertxexample.db.MysqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class DbVerticle extends AbstractVerticle {

    @Autowired
    private MysqlClient mysqlClient;

    @Override
    public void start() throws Exception {

        super.start();

        mysqlClient.init(vertx);

        vertx.eventBus().<JsonObject>consumer("db.query")
                .handler(this::handleQuery);
    }

    void handleQuery(Message message) {

        JsonObject query = (JsonObject) message.body();

        mysqlClient.query(query.getString("sql"), query.getJsonArray("params"))
                .subscribe(ar -> {
                    message.reply(ar.toJson().getJsonArray("rows"));
                });
    }

    @Bean
    public MysqlClient getMysqlClient() {
        return mysqlClient;
    }
}
