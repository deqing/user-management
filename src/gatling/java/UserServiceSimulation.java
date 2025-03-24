import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UserServiceSimulation extends Simulation {

    private final String USERS_PATH = "/api/v1/users";

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:19001")
            .contentTypeHeader("application/json");

    private static String generateIdentifier() {
        return UUID.randomUUID().toString();
    }

    // 1️⃣ 发送 100 次 POST /users
    private final ScenarioBuilder createUsers = scenario("Create Users")
            .repeat(100).on(
                    exec(session -> {
                        String uniqueId = generateIdentifier();
                        String jsonBody = String.format("{\"identifier\": \"%s\", \"name\": \"User-%s\", \"age\": 21}", uniqueId, uniqueId.substring(0, 2));
                        return session.set("userPayload", jsonBody);
                    })
                            .exec(http("Create User")
                                    .post(USERS_PATH)
                                    .body(StringBody("#{userPayload}"))
                                    .check(status().is(200)) // 确保返回 ok
                            )
            );

    // 2️⃣ 发送 1000 次 GET /users
    private final ScenarioBuilder getUsers = scenario("Get Users")
            .repeat(1000).on(
                    exec(http("Get Users")
                            .get(USERS_PATH)
                            .check(status().is(200)) // 确保返回 OK
                    )
            );

    // 设定 Gatling 运行计划
    {
        setUp(
                createUsers.injectOpen(atOnceUsers(10)), // 10 个用户并发创建用户
                getUsers.injectOpen(rampUsers(50).during(10)) // 10 秒内逐步增加到 50 并发请求
        ).protocols(httpProtocol);
    }

//    ScenarioBuilder scn = scenario("Get Users")
//            .exec(http("Get Users").get("/api/v1/users").check(status().is(200)));
//
//    {
//        setUp(scn.injectOpen(atOnceUsers(10))).protocols(httpProtocol);
//    }
}
