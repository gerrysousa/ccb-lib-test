package bases.utils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvConfig {
  private static final Properties props = new Properties();
  private static final Properties testProps = new Properties();

  static {
    String env = System.getProperty("env", "qa");
    String fileName = "envs/env-" + env + ".properties";
    try {
      props.load(EnvConfig.class.getClassLoader().getResourceAsStream(fileName));
      System.out.println("✅ Configs loaded: " + fileName);
    } catch (IOException | NullPointerException e) {
      throw new RuntimeException("Error loading file: " + fileName, e);
    }
  }

  public static String get(String key) {
    return props.getProperty(key);
  }

  public static void generateTestVariables() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter timestampWithMillis =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    DateTimeFormatter numericTimestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    testProps.setProperty("gen:today.date", now.format(DateTimeFormatter.ISO_LOCAL_DATE));
    testProps.setProperty("gen:today.numeric", now.format(DateTimeFormatter.BASIC_ISO_DATE));
    testProps.setProperty("gen:timestamp", now.format(timestampWithMillis));
    testProps.setProperty("gen:id.timestamp", now.format(numericTimestamp));
    testProps.setProperty("gen:id.epoch", String.valueOf(Instant.now().toEpochMilli()));
    testProps.setProperty("gen:id.uuid", UUID.randomUUID().toString());
    testProps.setProperty("gen:id.string", generateRandomString(10));
    testProps.setProperty("gen:test.id", now.format(numericTimestamp));

    System.out.println("+++++++++++++++++++ Test Variables Generated +++++++++++++++++");
    testProps.forEach((key, value) -> System.out.println(key + ": " + value));
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  }

  public static String getTestVar(String key) {
    return testProps.getProperty(key);
  }

  public static String generateRandomString(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder builder = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      builder.append(characters.charAt(random.nextInt(characters.length())));
    }
    return builder.toString();
  }

  public static String replaceTestVariables(String stringToProcess) {
    Pattern pattern = Pattern.compile("gen:[a-zA-Z\\.]+");
    Matcher matcher = pattern.matcher(stringToProcess);
    while (matcher.find()) {
      String variable = matcher.group(0);
      String value = EnvConfig.getTestVar(variable);
      if (value != null) {
        stringToProcess = stringToProcess.replace(variable, value);
      }
    }

    return stringToProcess;
  }

  public static Map<String, String> replaceMapTestVariables(Map<String, String> stringMap) {
    Map<String, String> mapProcessed = new HashMap<>();
    for (Map.Entry<String, String> entry : stringMap.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      value = replaceTestVariables(value);
      mapProcessed.put(key, value);
    }

    return mapProcessed;
  }
}
