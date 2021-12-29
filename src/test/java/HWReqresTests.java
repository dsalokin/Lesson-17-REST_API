import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class HWReqresTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void listUsersTest() {
        Response response =
                get("api/users?page=2")
                        .then()
                        .extract().response();

        assertThat(response.path("total").toString()).isEqualTo("12");
        assertThat((Integer) response.path("page")).isEqualTo(2);
        assertThat((Integer) response.path("per_page")).isEqualTo(6);
        assertThat(response.path("data.first_name").toString()).contains("George");
        assertThat(response.path("data.email").toString()).contains("tobias.funke@reqres.in");
        assertThat((List<?>) response.path("data.email")).hasSize(6);
    }

    @Test
    void listResourceTest() {
        Response response =
                get("api/unknown")
                        .then()
                        .extract().response();

        assertThat(response.path("support.text").toString()).startsWith("To").endsWith("ed!");
        assertThat(response.path("data.name").toString()).contains("aqua sky");
        assertThat((List<?>) response.path("data.id")).hasSize(6);
    }

    @Test
    void listSingleResourceNotFoundTest() {
        get("api/unknown/23")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteTest() {
        delete("api/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    void negativeLogin() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("api/users")
                .then()
                .statusCode(201)
                .body("job", is("leader"))
                .body("createdAt", is(notNullValue()));
    }
}
