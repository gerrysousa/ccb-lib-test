package bases.utils;

import static bases.utils.EnvConfig.generateTestVariables;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class Hooks {
  private Scenario scenario;

  @BeforeAll
  public static void setup() {
    String env = System.getProperty("env", "qa");
    String test = EnvConfig.get("base.url");

    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("MEU ENV:" + env);
    System.out.println("MEU EnvConfig.get(base.url):" + test);
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  }

  @Before
  public void beforeTest(final Scenario scenario) {
    this.scenario = scenario;
    generateTestVariables();
  }

  @After
  public void after(final Scenario scenario) {
    scenario.log("This is a log message from the Hooks class");
  }
}
