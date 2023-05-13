package BGU.Group13B.service.testsManager;


import org.reflections.Reflections;

import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Set;

public class TestSummaryGenerator {
    public static void main(String[] args) {
        try {
            generateSummary("testSummary.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateSummary(String fileName) throws Exception {
        Set<Class<?>> testClasses = getAllTestClasses();
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Class<?> testClass : testClasses) {
                writer.write(testClass.getName() + "\n");
                for (Method method : testClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(TestRequirements.class)) {
                        TestRequirements annotation = method.getAnnotation(TestRequirements.class);
                        String[] requirements = annotation.value();
                        writer.write("\t" + method.getName() + ": " + String.join(", ", requirements) + "\n");
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static Set<Class<?>> getAllTestClasses() {
        Reflections reflections = new Reflections("src/main.java/BGU/Group13B");
        return reflections.getTypesAnnotatedWith(TestClass.class);
    }

}
