package com.thinkerwolf.gamer.swagger.mapper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.thinkerwolf.gamer.swagger.schema.AllowableValues;
import com.thinkerwolf.gamer.swagger.schema.ModelReference;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.*;

import java.util.Comparator;
import java.util.Map;

import static com.google.common.base.Functions.forMap;
import static com.google.common.base.Strings.nullToEmpty;

public class Properties {

    private static final Map<String, Function<String, ? extends Property>> typeFactory
            = ImmutableMap.<String, Function<String, ? extends Property>>builder()
            .put("int", newInstanceOf(IntegerProperty.class))
            .put("long", newInstanceOf(LongProperty.class))
            .put("float", newInstanceOf(FloatProperty.class))
            .put("double", newInstanceOf(DoubleProperty.class))
            .put("string", newInstanceOf(StringProperty.class))
            .put("boolean", newInstanceOf(BooleanProperty.class))
            .put("date", newInstanceOf(DateProperty.class))
            .put("date-time", newInstanceOf(DateTimeProperty.class))
            .put("bigdecimal", newInstanceOf(DecimalProperty.class))
            .put("biginteger", newInstanceOf(BaseIntegerProperty.class))
            .put("uuid", newInstanceOf(UUIDProperty.class))
            .put("object", newInstanceOf(ObjectProperty.class))
            .put("byte", bytePropertyFactory())
            .put("file", filePropertyFactory())
            .build();

    private Properties() {
        throw new UnsupportedOperationException();
    }

    public static Property property(final String typeName) {
        String safeTypeName = nullToEmpty(typeName);
        Function<String, Function<String, ? extends Property>> propertyLookup
                = forMap(typeFactory, voidOrRef(safeTypeName));
        return propertyLookup.apply(safeTypeName.toLowerCase()).apply(safeTypeName);
    }

    public static Property property(final ModelReference modelRef) {
        if (modelRef.isMap()) {
            return new MapProperty(property(modelRef.itemModel().get()));
        } else if (modelRef.isCollection()) {
            return new ArrayProperty(
                    maybeAddAllowableValues(itemTypeProperty(modelRef.itemModel().get()), modelRef.getAllowableValues()));
        }
        return property(modelRef.getType());
    }

    public static Property itemTypeProperty(ModelReference paramModel) {
        if (paramModel.isCollection()) {
            return new ArrayProperty(
                    maybeAddAllowableValues(itemTypeProperty(paramModel.itemModel().get()), paramModel.getAllowableValues()));
        }
        return property(paramModel.getType());
    }

    static Property maybeAddAllowableValues(Property property, AllowableValues allowableValues) {
        return property;
    }



    private static <T extends Property> Function<String, T> newInstanceOf(final Class<T> clazz) {
        return new Function<String, T>() {
            @Override
            public T apply(String input) {
                try {
                    return clazz.newInstance();
                } catch (Exception e) {
                    //This is bad! should never come here
                    throw new IllegalStateException(e);
                }
            }
        };
    }


    private static Function<String, ? extends Property> voidOrRef(final String typeName) {
        return new Function<String, Property>() {
            @Override
            public Property apply(String input) {
                if (typeName.equalsIgnoreCase("void")) {
                    return null;
                }
                return new RefProperty(typeName);
            }
        };
    }

    private static Function<String, ? extends Property> bytePropertyFactory() {
        return new Function<String, Property>() {
            @Override
            public Property apply(String input) {
                StringProperty byteArray = new StringProperty();
                byteArray.setFormat("byte");
                return byteArray;
            }
        };
    }

    private static Function<String, ? extends Property> filePropertyFactory() {
        return new Function<String, Property>() {
            @Override
            public Property apply(String input) {
                return new FileProperty();
            }
        };
    }

    private static Comparator<String> byName() {
        return new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                return first.compareTo(second);
            }
        };
    }


}
