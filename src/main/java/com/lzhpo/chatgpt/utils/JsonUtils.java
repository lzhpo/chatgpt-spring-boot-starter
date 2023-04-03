/*
 * Copyright 2023 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.chatgpt.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * @author lzhpo
 */
@UtilityClass
public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Replace the default {@link JsonUtils#mapper}.
     *
     * @param mapper {@link ObjectMapper}
     */
    public static void setMapper(ObjectMapper mapper) {
        JsonUtils.mapper = mapper;
    }

    /**
     * Object to json string and format and beautify
     *
     * @param obj the object for which Json representation is to be created setting for Gson
     * @return the pretty json text of object.
     */
    @SneakyThrows
    public static <T> String toJsonPrettyString(T obj) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * Parses the specified JSON string to a JsonNode
     *
     * @param json JSON content to parse to build the JSON tree.
     * @return a JsonNode, if valid JSON content found; null if input has no content to bind
     */
    @SneakyThrows
    public static JsonNode toJsonNode(String json) {
        return mapper.readTree(json);
    }

    /**
     * Serializes the specified object to Json text.
     *
     * @param object the object for which Json representation is to be created setting for Gson
     * @return the json text of object.
     */
    @SneakyThrows
    public static String toJsonString(Object object) {
        return mapper.writeValueAsString(object);
    }

    /**
     * Parse the JsonNode into an object of the specified type.
     *
     * @param jsonNode the JsonNode object
     * @param valueType the class of the desired object
     * @param <T> the type of the desired object
     * @return an object of type T
     */
    @SneakyThrows
    public static <T> T parse(JsonNode jsonNode, Class<T> valueType) {
        return mapper.treeToValue(jsonNode, valueType);
    }

    /**
     * Parse the json string into an object of the specified type.
     *
     * @param content the json string
     * @param valueType the class of the desired object
     * @param <T> the type of the desired object
     * @return an object of type T
     */
    @SneakyThrows
    public static <T> T parse(String content, Class<T> valueType) {
        return mapper.readValue(content, valueType);
    }

    /**
     * Convert json {@code content} into a collection of required objects
     *
     * @param content the json string
     * @param valueType the class of the desired object
     * @param <T> the type of the desired object
     * @return List of type T
     */
    @SneakyThrows
    public static <T> List<T> parseArray(String content, Class<T> valueType) {
        return mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }
}
