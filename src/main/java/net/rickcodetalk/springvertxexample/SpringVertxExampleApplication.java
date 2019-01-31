package net.rickcodetalk.springvertxexample;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.rickcodetalk.springvertxexample.repository.UserRepository;
import net.rickcodetalk.springvertxexample.verticles.DbVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@SpringBootApplication
public class SpringVertxExampleApplication {

	@Autowired
	private DbVerticle dbVerticle;

	@Autowired
	private UserRepository userRepository;

	Vertx vertx;

	public static void main(String[] args) {
		SpringApplication.run(SpringVertxExampleApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		vertx = Vertx.vertx();

		vertx.deployVerticle(dbVerticle, res-> {
            if (res.succeeded()) {
                System.out.println("dbVerticle deployed");

                /* Query via DBVertical's db client directly */
                userRepository.findByUsername("ricklee")
						.doOnSuccess(ar -> {
						    System.out.println("success");
						    System.out.println(ar.toJson().getJsonArray("rows"));
                        })
                        .doOnError(err -> err.printStackTrace())
						.subscribe();

                /* Query via eventbus */
                JsonObject query = new JsonObject();

                query.put("sql", "select * from user where username = ?");
                query.put("params", (new JsonArray()).add("maryjohnson"));

                vertx.eventBus().send("db.query", query, ar -> {
                    if(ar.succeeded()) {
                        System.out.println(ar.result().body());
                    }
                });
			} else {
				System.out.println("Deployment failed!");
			}
		});
	}
}

