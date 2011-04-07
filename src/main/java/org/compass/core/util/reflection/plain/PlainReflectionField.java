/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.core.util.reflection.plain;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.compass.core.util.reflection.ReflectionField;

/**
 * A plain implemenation of {@link org.compass.core.util.reflection.ReflectionField}
 * that simply delegates operations to {@link java.lang.reflect.Field}.
 *
 * @author kimchy
 */
public class PlainReflectionField implements ReflectionField {

    private Field field;

    public PlainReflectionField(Field field) {
        this.field = field;
    }

    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return field.get(obj);
    }

    public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        field.set(obj, value);
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public int getModifiers() {
        return field.getModifiers();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Type getGenericType() {
        return field.getGenericType();
    }

    public Field getField() {
        return field;
    }
}
