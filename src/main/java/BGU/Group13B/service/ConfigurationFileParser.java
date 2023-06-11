package BGU.Group13B.service;

import com.nimbusds.jose.shaded.gson.*;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationFileParser {
    private static final HashMap<String/*functionName*/, List<Object>> results = new HashMap<>();

    public static void main(String[] args) {
        // Read the configuration file
        parse();
    }

    public static void parse() {
        JsonObject config = loadJsonObject();
        if (config == null) return;

        // Get the "functions" array from the configuration
        JsonArray functions = config.getAsJsonArray("functions");
        boolean persist = config.get("persist").getAsBoolean();
        if(!persist){
            return;
        }
        for (int i = 0; i < functions.size(); i++) {
            parseFunction(functions.get(i).getAsJsonObject());
        }
    }

    private static JsonObject loadJsonObject() {
        JsonObject config;
        try (FileReader reader = new FileReader("src/main/resources/configurationFile.json")) {
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            config = element.getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

    private static void parseFunction(JsonObject function) {
        String functionName = function.get("name").getAsString();
        JsonArray arguments = function.getAsJsonArray("arguments");
        JsonArray types = function.getAsJsonArray("types");

        // Get the argument types as Class objects
        Class<?>[] argumentTypes = getArgumentTypes(types);

        // Get the actual argument values
        Object[] argumentValues = getArgumentValues(arguments, types);

        // Call the function dynamically using reflection
        callFunction(functionName, argumentTypes, argumentValues);
    }

    private static void callFunction(String functionName, Class<?>[] argumentTypes, Object[] argumentValues) {
        try {
            Class<?> clazz = Session.class; // Change to the appropriate class containing your functions
            Method method = clazz.getDeclaredMethod(functionName, argumentTypes);
            Session session = SingletonCollection.getSession();
            Object result = method.invoke(session, argumentValues); // Change null to an instance if the method is non-static
            result = getResultFromResponse(result);
            if (result == null)
                return;
            // Store the result for future reference
            var newList = results.getOrDefault(functionName, new ArrayList<>());
            newList.add(result);
            results.put(functionName, newList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object[] getArgumentValues(JsonArray arguments, JsonArray types) {
        Object[] argumentValues = new Object[arguments.size()];
        for (int j = 0; j < arguments.size(); j++) {
            String argument = arguments.get(j).getAsString();
            if (argument.startsWith("{{") && argument.endsWith("}}")) {
                // Argument is a reference to a previous result
                String[] split = argument.substring(2, argument.length() - 2).split(",");
                String argumentFunctionName = split[0];
                int index = Integer.parseInt(split[1]);
                argumentValues[j] = results.get(argumentFunctionName).get(index);
            } else {
                // Argument is a literal value
                argumentValues[j] = parseValue(argument, types.get(j).getAsString());
            }
        }
        return argumentValues;
    }

    private static Class<?>[] getArgumentTypes(JsonArray types) {
        Class<?>[] argumentTypes = new Class<?>[types.size()];
        for (int j = 0; j < types.size(); j++) {
            String typeName = types.get(j).getAsString();
            argumentTypes[j] = getTypeClass(typeName);
        }
        return argumentTypes;
    }

    private static Object getResultFromResponse(Object result) {
        if (result instanceof Response) {
            return ((Response<?>) result).getData();
        }
        return result;
    }


    // Helper method to get Class objects from type names
    private static Class<?> getTypeClass(String typeName) {
        return switch (typeName) {
            case "int" -> int.class;
            case "double" -> double.class;
            case "String" -> String.class;
            case "LocalDate" -> LocalDate.class;
            // Add support for other types as needed
            default -> throw new IllegalArgumentException("Unsupported type: " + typeName);
        };
    }

    private static Object parseValue(String value, String type) {
        return switch (type) {
            case "int" -> Integer.parseInt(value);
            case "double" -> Double.parseDouble(value);
            case "String" -> value;
            case "LocalDate" -> parseDate(value);
            // Add support for other types as needed
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    private static Object parseDate(String value) {
        try {
            //format dd/MM/yyyy
            String[] split = value.split("/");
            int day = Integer.parseInt(split[0]);
            int month = Integer.parseInt(split[1]);
            int year = Integer.parseInt(split[2]);
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return value;
        }
    }
}
