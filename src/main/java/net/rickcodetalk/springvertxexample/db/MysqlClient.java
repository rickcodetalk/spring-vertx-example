package net.rickcodetalk.springvertxexample.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;

@Component
public class MysqlClient {
    @Autowired
    private Environment env;

    AsyncSQLClient client;

    public void init(Vertx vertx) {

        HashMap<String, Object> options = new HashMap<>();

        options.put("host", env.getProperty("mysql.host"));
        options.put("port", Integer.parseInt(Objects.requireNonNull(env.getProperty("mysql.port"))));
        options.put("maxPoolSize", Integer.parseInt(Objects.requireNonNull(env.getProperty("mysql.maxPoolSize"))));
        options.put("username", env.getProperty("mysql.username"));
        options.put("password", env.getProperty("mysql.password"));
        options.put("database", env.getProperty("mysql.database"));

        this.client = MySQLClient.createShared(vertx, new JsonObject(options));
    }

    public Mono<ResultSet> query(String sql, JsonArray params) {

        Future<ResultSet> future = Future.future();

        client.queryWithParams(sql, (params == null) ? new JsonArray() : params, future.completer());

        return Mono.create(sink -> {
            future.setHandler(ar -> {
                if(ar.succeeded()) {
                    sink.success(ar.result());
                } else {
                    sink.error(ar.cause());
                }
            });
        });
    }
}
