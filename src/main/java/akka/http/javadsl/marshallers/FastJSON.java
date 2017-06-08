/*
 * Copyright (c) 2017 SuperDream Inc. <http://www.superdream.me>
 */

package akka.http.javadsl.marshallers;

import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.function.Function;

/**
 * 基于FastJSON实现Akka Http处理HttpEntity
 *
 * @author <a href="mailto:517926804@qq.com">Mike Chen</a>
 * @version 1.0.0
 * @since 1.0.0
 */
public class FastJSON {
    private static <T> Marshaller<T, RequestEntity>  warp(Function<T, String> warpFunction) {
        return Marshaller.wrapEntity(warpFunction, Marshaller.stringToEntity(), MediaTypes.APPLICATION_JSON);
    }

    public static <T> Marshaller<T, RequestEntity> marshaller() {
        return warp(FastJSON::toJSON);
    }

    public static <T> Marshaller<T, RequestEntity> marshaller(SerializerFeature... features) {
        return warp(data -> toJSON(data, features));
    }

    public static <T> Marshaller<T, RequestEntity> marshaller(SerializeFilter filter, SerializerFeature... features) {
        return warp(data -> toJSON(data, filter, features));
    }

    public static <T> Marshaller<T, RequestEntity> marshaller(SerializeFilter[] filters, SerializerFeature... features) {
        return warp(data -> toJSON(data, filters, features));
    }

    public static <T> Unmarshaller<HttpEntity, T> unmarshaller(final Class<T> expectedType, Feature... features) {
        return Unmarshaller.forMediaType(MediaTypes.APPLICATION_JSON, Unmarshaller.entityToString())
                .thenApply(s -> fromJSON(s, expectedType));
    }

    public static <T> Unmarshaller<HttpEntity, T> unmarshaller(final TypeReference<T> expectedType, Feature... features) {
        return Unmarshaller.forMediaType(MediaTypes.APPLICATION_JSON, Unmarshaller.entityToString())
                .thenApply(s -> fromJSON(s, expectedType));
    }

    private static <T> String toJSON(T object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot marshal to JSON: " + object, e);
        }
    }

    private static <T> String toJSON(T object, SerializerFeature... features) {
        try {
            return JSON.toJSONString(object, features);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot marshal to JSON: " + object, e);
        }
    }

    private static <T> String toJSON(T object, SerializeFilter filter, SerializerFeature... features) {
        return toJSON(object, new SerializeFilter[] {filter}, features);
    }

    private static <T> String toJSON(T object, SerializeFilter[] filters, SerializerFeature... features) {
        try {
            return JSON.toJSONString(object, filters, features);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot marshal to JSON: " + object, e);
        }
    }

    private static <T> T fromJSON(String json, Class<T> expectedType, Feature... features) {
        try {
            return JSON.parseObject(json, expectedType, features);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot unmarshal JSON as " + expectedType.getSimpleName(), e);
        }
    }

    private static <T> T fromJSON(String json, TypeReference<T> valueTypeRef, Feature... features) {
        try {
            return JSON.parseObject(json, valueTypeRef, features);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot unmarshal JSON as " + valueTypeRef.getType().getTypeName(), e);
        }
    }
}
