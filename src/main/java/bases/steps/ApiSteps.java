package bases.steps;

import static bases.utils.EnvConfig.replaceMapTestVariables;
import static bases.utils.EnvConfig.replaceTestVariables;
import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import bases.utils.EnvConfig;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ApiSteps {

  private RequestSpecification request;
  private Response response;
  private String baseUrl;
  private String endpoint;
  private String requestBody;
  private Scenario scenario;
  private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private PrintStream requestPrintStream = new PrintStream(outputStream);
  private LogConfig logConfigRequest = new LogConfig(requestPrintStream, true);

  public ApiSteps() {
    request = given().relaxedHTTPSValidation().log().all();
    request.config(RestAssured.config().logConfig(logConfigRequest)).log().all();
  }

  public static String getJsonAsString(final String filePath) throws IOException {
    try {
      return new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/" + filePath)));
    } catch (IOException e) {
      throw new RuntimeException("Error reading payload file: " + filePath, e);
    }
  }

  @Before
  public void before(final Scenario scenario) {
    this.scenario = scenario;
  }

  @Given("I set the base URL to {string}")
  public void iSetTheBaseURLTo(final String baseUrl) {
    setBaseUrl(baseUrl);
  }

  @Given("I set the endpoint to {string}")
  public void iSetTheEndpointTo(final String endpoint) {
    setEndpoint(endpoint);
  }

  @Given("I set the request body with JSON file {string}")
  public void iSetTheRequestBodyWithFile(final String filePath) {
    setJsonBody(filePath);
  }

  @Given("I set the headers")
  public void iSetTheHeaders(final Map<String, String> headers) {
    setHeaders(headers);
  }

  @Given("I set the query params")
  public void iSetTheQueryParameters(final Map<String, String> queryParams) {
    setQueryParams(queryParams);
  }

  @Given("I set the form params")
  public void iSetTheFormParameters(final Map<String, String> formParams) {
    setFormParams(formParams);
  }

  @Given("I set the multiparts")
  public void iSetTheMultiParts(final Map<String, String> multiparts) {
    multiparts.forEach(
        (key, value) -> {
          File file = new File("src/test/resources/multipart/" + value);
          request.multiPart(key, file);
        });
  }

  @When("I send a {string} request to {string} endpoint {string}")
  public void iSendRequest(final String httpMethod, final String baseUrl, final String endpoint) {
    response = sendRequest(httpMethod, baseUrl, endpoint);
  }

  @When("I send a {string} request to {string} endpoint {string} with query params")
  public void iSendRequestWithQueryParams(
      final String httpMethod,
      final String baseUrl,
      final String endpoint,
      final Map<String, String> queryParams) {
    response = sendRequest(httpMethod, baseUrl, endpoint, null, queryParams, null, null);
  }

  @When("I send a {string} request to {string} endpoint {string} with form params")
  public void iSendRequestWithFormParams(
      final String httpMethod, final String baseUrl, final String endpoint) {
    response = sendRequest(httpMethod, baseUrl, endpoint);
  }

  @When("I send a {string} request to {string} endpoint {string} with headers")
  public void iSendRequestWithHeaders(
      final String httpMethod,
      final String baseUrl,
      final String endpoint,
      final Map<String, String> headers) {
    response = sendRequest(httpMethod, baseUrl, endpoint, headers, null, null, null);
  }

  @When("I send a {string} request to {string} endpoint {string} with multiparts")
  public void iSendRequestWithMultiParts(
      final String httpMethod, final String baseUrl, final String endpoint) {
    response = sendRequest(httpMethod, baseUrl, endpoint);
  }

  @When("I send a {string} request to {string} endpoint {string} with JSON payload {string}")
  public void iSendRequestWithPayloadFile(
      final String httpMethod, final String baseUrl, final String endpoint, final String filePath) {
    response = sendRequest(httpMethod, baseUrl, endpoint, null, null, null, filePath);
  }

  @When(
      "I send a {string} request to {string} endpoint {string} with JSON payload {string} and query params")
  public void iSendRequestWithPayloadFileAndQueryParams(
      final String httpMethod,
      final String baseUrl,
      final String endpoint,
      final String filePath,
      final Map<String, String> queryParams) {
    response = sendRequest(httpMethod, baseUrl, endpoint, null, queryParams, null, filePath);
  }

  @When("I send a {string} request to endpoint {string}")
  public void iSendRequest(final String httpMethod, final String endpoint) {
    response = sendRequest(httpMethod, baseUrl, endpoint);
  }

  @When("I send a {string} request to endpoint {string} with JSON payload {string}")
  public void iSendRequestWithPayloadFile(
      final String httpMethod, final String endpoint, final String filePath) {
    response = sendRequest(httpMethod, baseUrl, endpoint, null, null, null, filePath);
  }

  @Then("the response body should contain {string}")
  public void theResponseBodyShouldContain(final String text) {
    response.then().body(containsString(replaceTestVariables(text)));
  }

  @Then("the response body should be equal JSON {string}")
  public void theResponseBodyShouldBeEqual(final String filePath) throws IOException {
    response.then().body(equalTo(getJsonAsString(filePath)));
  }

  @Then("the response status code should be {int}")
  public void theResponseStatusCodeShouldBe(int statusCode) {
    response.then().statusCode(statusCode);
  }

  @Then("the response status code should be {string}")
  public void theResponseStatusCodeShouldBe(final String statusCode) {
    response.then().statusCode(Integer.parseInt(statusCode));
  }

  @Then("the JSON path {string} should be {string}")
  public void theJsonPathShouldBe(final String jsonPath, final String expectedValue) {
    response.then().body(jsonPath, equalTo(expectedValue));
  }

  @Then("the JSON path {string} should be number {double}")
  public void theJsonPathShouldBeNumber(final String jsonPath, final double expectedValue) {
    response.then().body(jsonPath, equalTo(expectedValue));
  }

  @Then("the JSON path {string} should exist")
  public void theJsonPathShouldExist(final String jsonPath) {
    response.then().body(jsonPath, notNullValue());
  }

  @Then("the JSON path {string} should not exist")
  public void theJsonPathShouldNotExist(final String jsonPath) {
    response.then().body(jsonPath, nullValue());
  }

  /** HELPERS METHODS * */
  private void setBaseUrl(final String baseUrl) {
    final String processedBaseUrl = firstNonNull(EnvConfig.get(baseUrl), baseUrl);
    this.baseUrl = processedBaseUrl;
    RestAssured.baseURI = processedBaseUrl;
  }

  private void setEndpoint(final String endpoint) {
    final String processedEndpoint = replaceTestVariables(endpoint);
    this.endpoint = processedEndpoint;
    RestAssured.basePath = processedEndpoint;
  }

  private void setHeaders(final Map<String, String> headers) {
    if (headers != null) {
      request.headers(replaceMapTestVariables(headers));
    }
  }

  private void setQueryParams(final Map<String, String> queryParams) {
    if (queryParams != null) {
      request.queryParams(replaceMapTestVariables(queryParams));
    }
  }

  private void setFormParams(final Map<String, String> formParams) {
    if (formParams != null) {
      request.formParams(replaceMapTestVariables(formParams));
    }
  }

  private void setJsonBody(final String filePath) {
    if (filePath != null) {
      try {
        requestBody = replaceTestVariables(getJsonAsString(filePath));
        request.body(requestBody).header("Content-Type", "application/json");
      } catch (IOException e) {
        throw new RuntimeException("Error reading payload file: " + filePath, e);
      }
    }
  }

  private Response sendRequest(
      final String httpMethod, final String baseUrl, final String endpoint) {
    return sendRequest(httpMethod, baseUrl, endpoint, null, null, null, null);
  }

  private Response sendRequest(
      final String httpMethod,
      final String baseUrl,
      final String endpoint,
      final Map<String, String> headers,
      final Map<String, String> queryParams,
      final Map<String, String> formParams,
      final String filePathAsJsonBody) {
    outputStream.reset();

    setBaseUrl(baseUrl);
    setEndpoint(endpoint);
    setHeaders(headers);
    setQueryParams(queryParams);
    setFormParams(formParams);
    setJsonBody(filePathAsJsonBody);
    final String uri = this.baseUrl + this.endpoint;
    switch (httpMethod.toUpperCase()) {
      case "GET":
        response = request.when().get(uri);
        break;
      case "POST":
        response = request.when().post(uri);
        break;
      case "PUT":
        response = request.when().put(uri);
        break;
      case "PATCH":
        response = request.when().patch(uri);
        break;
      case "DELETE":
        response = request.when().delete(uri);
        break;
      default:
        throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
    }
    response.then().log().all(true);
    logRequestResponse();
    resetRequest();
    return response;
  }

  private void logRequestResponse() {
    scenario.log(
        "---------------------------------- Captured Request ----------------------------\n"
            + outputStream
            + "\n--------------------------------------------------------------------------------");
  }

  private void resetRequest() {
    request = given().relaxedHTTPSValidation().log().all();
    request.config(RestAssured.config().logConfig(logConfigRequest)).log().all();
  }
}
