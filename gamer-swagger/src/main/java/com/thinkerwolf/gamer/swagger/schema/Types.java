package com.thinkerwolf.gamer.swagger.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

/**
 * 类型工具
 */
public final class Types {

    private static final Set<String> baseTypes = newHashSet(
            "int",
            "date",
            "string",
            "double",
            "float",
            "boolean",
            "byte",
            "object",
            "long",
            "date-time",
            "file",
            "biginteger",
            "bigdecimal");
    private static final Map<Type, String> typeNameLookup = ImmutableMap.<Type, String>builder()
            .put(Long.TYPE, "long")
            .put(Short.TYPE, "int")
            .put(Integer.TYPE, "int")
            .put(Double.TYPE, "double")
            .put(Float.TYPE, "float")
            .put(Byte.TYPE, "byte")
            .put(Boolean.TYPE, "boolean")
            .put(Character.TYPE, "string")
            .put(Date.class, "date-time")
            .put(java.sql.Date.class, "date")
            .put(String.class, "string")
            .put(Object.class, "object")
            .put(Long.class, "long")
            .put(Integer.class, "int")
            .put(Short.class, "int")
            .put(Double.class, "double")
            .put(Float.class, "float")
            .put(Boolean.class, "boolean")
            .put(Byte.class, "byte")
            .put(BigDecimal.class, "bigdecimal")
            .put(BigInteger.class, "biginteger")
            .put(Currency.class, "string")
            .put(UUID.class, "string")
            .build();


    public static boolean isBaseType(String typeName) {
        return baseTypes.contains(typeName);
    }

    public static boolean isBaseType(ResolvedType type) {
        return baseTypes.contains(typeNameFor(type));
    }

    public static boolean isVoid(ResolvedType returnType) {
        return Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType());
    }

    public static String typeNameFor(ResolvedType type) {
        return Optional.fromNullable(typeNameLookup.get(type.getErasedType())).or(type.getTypeName());
    }


    public static boolean isCollection(ResolvedType resolvedType) {
        if (List.class.isAssignableFrom(resolvedType.getErasedType())
                || Set.class.isAssignableFrom(resolvedType.getErasedType())
                || resolvedType.isArray()) {
            return true;
        }
        return false;
    }

    public static String getCollectionName(ResolvedType resolvedType) {
        if (List.class.isAssignableFrom(resolvedType.getErasedType())) {
            return "List";
        } else if (Set.class.isAssignableFrom(resolvedType.getErasedType())) {
            return "Set";
        } else if (resolvedType.isArray()) {
            return "Array";
        }
        throw new UnsupportedOperationException();
    }


    public static ResolvedType collectionElementType(ResolvedType type) {
        if (List.class.isAssignableFrom(type.getErasedType())) {
            return elementType(type, List.class);
        } else if (Set.class.isAssignableFrom(type.getErasedType())) {
            return elementType(type, Set.class);
        } else if (type.isArray()) {
            return type.getArrayElementType();
        } else {
            return null;
        }
    }

    private static <T extends Collection> ResolvedType elementType(ResolvedType container, Class<T> collectionType) {
        List<ResolvedType> resolvedTypes = container.typeParametersFor(collectionType);
        if (resolvedTypes.size() == 1) {
            return resolvedTypes.get(0);
        }
        return new TypeResolver().resolve(Object.class);
    }

    public static boolean isMap(ResolvedType resolvedType) {
        if (Map.class.isAssignableFrom(resolvedType.getErasedType())) {
            return true;
        }
        return false;
    }


    public static ResolvedType mapValueType(ResolvedType type) {
        if (Map.class.isAssignableFrom(type.getErasedType())) {
            return mapValueType(type, Map.class);
        } else {
            return new TypeResolver().resolve(Object.class);
        }
    }

    private static ResolvedType mapValueType(ResolvedType container, Class<Map> mapClass) {
        List<ResolvedType> resolvedTypes = container.typeParametersFor(mapClass);
        if (resolvedTypes.size() == 2) {
            return resolvedTypes.get(1);
        }
        return new TypeResolver().resolve(Object.class);
    }
}
