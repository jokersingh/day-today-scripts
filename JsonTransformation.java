import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonTransformation {

    public static void main(String[] args) {
        // Step 1: Read JSON
        String jsonString1 = "{ \"A\": {\"Object1\":\"Object 1\",\"key1\": \"read_file\"}, \"B\":[{\"Object11\":\"Object 1.1\",\"key11\": \"SQL\"},{\"Object11\":\"Object 1.2\",\"key12\": \"CREATE_TABLE\"}]}";
        String jsonString2 = "{ \"C\": {\"Object1\":\"Object 1\",\"key1\": \"read_file\"}, \"D\":[{\"Object11\":\"Object 1.1\",\"key11\": \"READ_JDBC\"},{\"Object11\":\"Object 1.2\",\"key12\": \"ALTER_TABLE\"}]}";

        JSONObject json1 = new JSONObject(jsonString1);
        JSONObject json2 = new JSONObject(jsonString2);

        // Step 2: Split JSON object and read its attributes
        splitAndReadAttributes(json1);
        splitAndReadAttributes(json2);

        // Step 3: Transform into another JSON based upon mapping
        JSONObject transformedData = transformAndCreateMapping(json1, json2);

        // Step 4: Create a mapping of JSON object
        Map<String, String> objectMapping = createObjectMapping(json1);

        // Display the transformed data
        System.out.println("\nTransformed Data:");
        System.out.println(transformedData.toString(2));

        // Display the separate map of objects
        System.out.println("\nObject Mapping:");
        for (Map.Entry<String, String> entry : objectMapping.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void splitAndReadAttributes(JSONObject json) {
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);

            if (value instanceof JSONObject) {
                System.out.println("Object: " + key);
                JSONObject innerObject = (JSONObject) value;
                innerObject.keySet().forEach(innerKey -> System.out.println("  " + innerKey + ": " + innerObject.get(innerKey)));
            } else if (value instanceof JSONArray) {
                System.out.println("List: " + key);
                JSONArray array = (JSONArray) value;
                for (Object obj : array) {
                    if (obj instanceof JSONObject) {
                        JSONObject innerObject = (JSONObject) obj;
                        innerObject.keySet().forEach(innerKey -> System.out.println("  " + innerKey + ": " + innerObject.get(innerKey)));
                    }
                }
            }
        }
    }

    private static JSONObject transformAndCreateMapping(JSONObject json1, JSONObject json2) {
        JSONObject transformedData = new JSONObject();

        JSONObject objectC = json2.getJSONObject("C");
        JSONObject objectD = new JSONObject();
        JSONArray arrayD = new JSONArray();

        objectD.put("Object1", objectC.getString("Object1"));
        objectD.put("key1", objectC.getString("key1"));

        JSONArray arrayB = json1.getJSONArray("B");
        for (int i = 0; i < arrayB.length(); i++) {
            JSONObject objB = arrayB.getJSONObject(i);
            if ("SQL".equals(objB.getString("key11"))) {
                JSONObject objD = new JSONObject();
                objD.put("Object11", objB.getString("Object11"));
                objD.put("key11", "READ_JDBC");
                arrayD.put(objD);
            }
        }

        transformedData.put("C", objectC);
        transformedData.put("D", arrayD);

        return transformedData;
    }

    private static Map<String, String> createObjectMapping(JSONObject json) {
        Map<String, String> objectMapping = new HashMap<>();
        JSONArray arrayB = json.getJSONArray("B");

        for (int i = 0; i < arrayB.length(); i++) {
            JSONObject objB = arrayB.getJSONObject(i);
            if (objB.has("key11")) {
                objectMapping.put(objB.getString("key11"), objB.getString("Object11"));
            }
        }

        return objectMapping;
    }
}
