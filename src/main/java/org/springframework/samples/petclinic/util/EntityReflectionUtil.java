/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.stereotype.Component;

/**
 * Utility class that demonstrates reflection usage with entity classes. This class will
 * fail in GraalVM native image without proper reflection configuration.
 */
@Component
public class EntityReflectionUtil {

	private static final String BASE_PACKAGE = "org.springframework.samples.petclinic.model.";

	/**
	 * Dynamically loads a class by name from the model package.
	 * @param className the simple name of the class (without package)
	 * @return the loaded Class object
	 * @throws ClassNotFoundException if the class cannot be found
	 */
	public Class<?> loadClassByName(String className) throws ClassNotFoundException {
		String fullClassName = BASE_PACKAGE + className;
		return Class.forName(fullClassName);
	}

	/**
	 * Creates a new instance of the specified class using reflection.
	 * @param className the simple name of the class (without package)
	 * @return a new instance of the class
	 * @throws Exception if instantiation fails
	 */
	public Object createInstance(String className) throws Exception {
		Class<?> clazz = loadClassByName(className);
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		return constructor.newInstance();
	}

	/**
	 * Gets all declared methods of a class.
	 * @param className the simple name of the class (without package)
	 * @return an array of method names
	 * @throws ClassNotFoundException if the class cannot be found
	 */
	public String[] getMethodNames(String className) throws ClassNotFoundException {
		Class<?> clazz = loadClassByName(className);
		Method[] methods = clazz.getDeclaredMethods();
		return Arrays.stream(methods).map(Method::getName).toArray(String[]::new);
	}

	/**
	 * Gets all declared fields of a class.
	 * @param className the simple name of the class (without package)
	 * @return an array of field names
	 * @throws ClassNotFoundException if the class cannot be found
	 */
	public String[] getFieldNames(String className) throws ClassNotFoundException {
		Class<?> clazz = loadClassByName(className);
		Field[] fields = clazz.getDeclaredFields();
		return Arrays.stream(fields).map(Field::getName).toArray(String[]::new);
	}

	/**
	 * Sets a property value on an entity using reflection.
	 * @param entity the entity object
	 * @param propertyName the name of the property to set
	 * @param value the value to set
	 * @throws Exception if setting the property fails
	 */
	public void setProperty(Object entity, String propertyName, Object value) throws Exception {
		Class<?> clazz = entity.getClass();
		String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
				method.invoke(entity, value);
				return;
			}
		}

		throw new NoSuchMethodException("Setter method not found: " + setterName);
	}

	/**
	 * Gets a property value from an entity using reflection.
	 * @param entity the entity object
	 * @param propertyName the name of the property to get
	 * @return the property value
	 * @throws Exception if getting the property fails
	 */
	public Object getProperty(Object entity, String propertyName) throws Exception {
		Class<?> clazz = entity.getClass();
		String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(getterName) && method.getParameterCount() == 0) {
				return method.invoke(entity);
			}
		}

		throw new NoSuchMethodException("Getter method not found: " + getterName);
	}

	/**
	 * Inspects an entity class and returns information about its structure.
	 * @param className the simple name of the class (without package)
	 * @return a map containing information about the class
	 * @throws ClassNotFoundException if the class cannot be found
	 */
	public Map<String, Object> inspectClass(String className) throws ClassNotFoundException {
		Class<?> clazz = loadClassByName(className);
		Map<String, Object> info = new HashMap<>();

		info.put("className", clazz.getSimpleName());
		info.put("fullClassName", clazz.getName());
		info.put("isEntity", BaseEntity.class.isAssignableFrom(clazz));
		info.put("superclass", clazz.getSuperclass().getSimpleName());
		info.put("methods", getMethodNames(className));
		info.put("fields", getFieldNames(className));
		info.put("interfaces", Arrays.stream(clazz.getInterfaces()).map(Class::getSimpleName).toArray(String[]::new));

		return info;
	}

}
