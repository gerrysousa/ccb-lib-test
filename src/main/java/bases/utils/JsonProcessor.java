package bases.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.Map.Entry;

public class JsonProcessor {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String processJson(String jsonString) {
    try {
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      replaceVariables(jsonNode);
      return objectMapper.writeValueAsString(jsonNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void replaceVariables(JsonNode jsonNode) {
    Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();

    while (fields.hasNext()) {
      Entry<String, JsonNode> field = fields.next();
      if (field.getValue().isTextual()) {
        String value = field.getValue().asText();
        if (value.startsWith("gen:")) {
          // Use getTestVar to get the value
          String replacementValue = EnvConfig.getTestVar(value);
          ((ObjectNode) jsonNode).put(field.getKey(), replacementValue);
        }
      } else if (field.getValue().isObject()) {
        replaceVariables(field.getValue());
      }
    }
  }
}
