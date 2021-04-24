package utils;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashSet;
import java.util.Set;

public class ClassInstanceSetBuilder {

    private ClassInstanceSetBuilder(){}

    private static <T> T newTargetClassInstance(Class<? extends T> type) {
        T instance = null;
        try {
            instance = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static <T> Set<T> build(String scanPackage, Class<T> type) {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scanPackage)));

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(type);
        Set<T> set = new HashSet<>(classes.size());

        for (Class<? extends T> aClass : classes) {
            set.add(newTargetClassInstance(aClass));
        }
        return set;
    }
}
