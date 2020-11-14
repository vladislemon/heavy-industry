package net.lemoncookie.heavyindustry.content;

import net.lemoncookie.heavyindustry.api.content.DoNotRegister;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class StaticContentRegister {
    private final String namespace;
    private final Class<?>[] contentClasses;
    private final AtomicBoolean complete = new AtomicBoolean(false);

    public StaticContentRegister(String namespace, Class<?>... contentClasses) {
        if (contentClasses == null || contentClasses.length < 1) {
            throw new IllegalArgumentException("No content classes provided");
        }
        this.namespace = namespace;
        this.contentClasses = contentClasses;
    }

    public void register() {
        if (complete.getAndSet(true)) {
            throw new IllegalStateException("Content already registered");
        }
        Map<Type, Registry<?>> registryMap = getRegistryMap();
        Method registerMethod = getRegisterMethod();
        Field[] contentFields = Arrays
                .stream(contentClasses)
                .flatMap(contentClass -> Arrays.stream(contentClass.getFields()))
                .filter(field -> !field.isAnnotationPresent(DoNotRegister.class))
                .toArray(Field[]::new);
        for (Field field : contentFields) {
            Registry<?> registry = getContentFieldRegistry(field, registryMap);
            Object object = getObjectFromStaticField(field);
            String path = field.getName();
            register(registry, registerMethod, object, path);
        }
    }

    private void register(Registry<?> registry, Method registerMethod, Object object, String path) {
        Identifier identifier = new Identifier(namespace, path);
        try {
            registerMethod.invoke(null, registry, identifier, object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(String.format("Content registration failed at element '%s'", path), e);
        }
    }

    private static Registry<?> getContentFieldRegistry(Field field, Map<Type, Registry<?>> registryMap) {
        Class<?> fieldType = field.getType();
        while (!registryMap.containsKey(fieldType)) {
            fieldType = fieldType.getSuperclass();
            if (fieldType == null) {
                throw new IllegalStateException(String.format("No registry for type '%s'", field.getType()));
            }
        }
        return registryMap.get(fieldType);
    }

    private static Map<Type, Registry<?>> getRegistryMap() {
        Map<Type, Registry<?>> map = new IdentityHashMap<>();
        for (Field field : Registry.class.getFields()) {
            if (Registry.class.isAssignableFrom(field.getType())) {
                Type type = getFieldFirstGenericParameterType(field);
                Registry<?> registry = (Registry<?>) getObjectFromStaticField(field);
                map.put(type, registry);
            }
        }
        return map;
    }

    private static Type getFieldFirstGenericParameterType(Field field) {
        Type generic = field.getGenericType();
        while (!(generic instanceof ParameterizedType)) {
            generic = generic.getClass().getGenericSuperclass();
            if (generic == null) {
                throw new IllegalArgumentException(String.format("Type of field '%s' is not parameterized", field));
            }
        }
        return ((ParameterizedType) generic).getActualTypeArguments()[0];
    }

    private static Method getRegisterMethod() {
        for (Method method : Registry.class.getMethods()) {
            if (isRegisterMethodParameterTypesMatch(method.getParameterTypes())) {
                return method;
            }
        }
        throw new IllegalStateException("Unable to find valid content register method");
    }

    private static boolean isRegisterMethodParameterTypesMatch(Type[] parameterTypes) {
        if (parameterTypes.length != 3) {
            return false;
        }
        if (parameterTypes[0] != Registry.class) {
            return false;
        }
        return parameterTypes[1] == Identifier.class;
    }

    private static Object getObjectFromStaticField(Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
