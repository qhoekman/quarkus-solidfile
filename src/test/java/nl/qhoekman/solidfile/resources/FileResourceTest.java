package nl.qhoekman.solidfile.resources;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FileResourceTest {
  // TODO Not sure how to test with authenticated user
  @Test
  void testGetFiles() {
    given()
        .when().get("/api/files")
        .then()
        .statusCode(401);
  }

}
