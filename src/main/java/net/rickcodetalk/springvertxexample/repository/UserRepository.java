package net.rickcodetalk.springvertxexample.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import net.rickcodetalk.springvertxexample.db.MysqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRepository {
    @Autowired
    protected MysqlClient mysqlClient;

    public Mono<ResultSet> findByUsername(String username) {
        return mysqlClient.query("select * from user where username = ?", (new JsonArray()).add(username));
    }
}
