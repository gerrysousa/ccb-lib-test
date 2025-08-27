package bases.steps;

import static junit.framework.TestCase.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WiremockSteps {

  private static final String WIREMOCK_URL =
      "http://localhost:9090"; // System.getenv("WIREMOCK_URL");
  private final List<String> executedRequests = new ArrayList<>();
  private Scenario scenario;

  private static boolean validateIfListsAreEquals(
      final List<String> expectedMocks, final List<String> uniqueExecuted) {
    return new HashSet<>(uniqueExecuted).equals(new HashSet<>(expectedMocks));
  }

  @Before
  public void before(final Scenario scenario) {
    this.scenario = scenario;
  }

  @Given("I reset the count of wiremock requests")
  public void resetWiremockRequests() {
    RestAssured.baseURI = WIREMOCK_URL;
    RestAssured.delete(WIREMOCK_URL + "/__admin/requests").then().statusCode(200);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Then("the API mocks were called")
  public void theApiMocksWereCalled(List<String> expectedMocks) {
    boolean success = false;

    for (int i = 0; i < 10; i++) {
      if (checkMocksCalled(expectedMocks)) {
        success = true;
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertTrue(success);
  }

  private boolean checkMocksCalled(List<String> expectedMocks) {
    Response response =
        RestAssured.get(WIREMOCK_URL + "/__admin/requests")
            .then()
            .statusCode(200)
            .extract()
            .response();
    List<Map<String, Object>> requests = response.jsonPath().getList("requests");
    executedRequests.clear();
    for (Map<String, Object> req : requests) {
      Map<String, Object> stubMapping = (Map<String, Object>) req.get("stubMapping");
      if (stubMapping != null && stubMapping.get("name") != null) {
        executedRequests.add(stubMapping.get("name").toString());
      }
    }
    List<String> uniqueExecuted = executedRequests.stream().distinct().collect(Collectors.toList());
    final boolean isEquals = validateIfListsAreEquals(expectedMocks, uniqueExecuted);
    if (!isEquals) {
      logMocksExpectedAndExecuted(expectedMocks, uniqueExecuted);
    }
    return isEquals;
  }

  private void logMocksExpectedAndExecuted(
      final List<String> expectedMocks, final List<String> uniqueExecuted) {
    scenario.log(
        "---------------------------------- Wiremock Validation  ------------------------\n"
            + "Expected mocks: "
            + expectedMocks
            + ", but executed mocks were: "
            + uniqueExecuted
            + "\n--------------------------------------------------------------------------------");
    System.out.println(
        "❌ Expected mocks: " + expectedMocks + ", but executed mocks were: " + uniqueExecuted);
  }
}
