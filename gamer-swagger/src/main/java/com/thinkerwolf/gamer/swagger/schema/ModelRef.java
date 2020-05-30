package com.thinkerwolf.gamer.swagger.schema;


import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ModelRef implements ModelReference {

    private boolean map;
    private String type;
    private Optional<ModelReference> itemModel;
    private Optional<AllowableValues> allowableValues;

    public ModelRef(String type) {
        this(type, null, null);
    }

    public ModelRef(String type, AllowableValues allowableValues) {
        this(type, null, allowableValues);
    }

    public ModelRef(String type, ModelReference itemType, AllowableValues allowableValues) {
        this(type, itemType, allowableValues, false);
    }

    public ModelRef(String type, ModelReference itemType, boolean isMap) {
        this(type, itemType, null, isMap);
    }

    public ModelRef(String type, ModelReference itemModel, AllowableValues allowableValues, boolean isMap) {
        this.type = type;
        this.map = isMap;
        this.allowableValues = Optional.fromNullable(allowableValues);
        this.itemModel = Optional.fromNullable(itemModel);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isMap() {
        return map;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getItemType() {
        return itemModel.transform(new Function<ModelReference, String>() {
            @Nullable
            @Override
            public String apply(@Nullable ModelReference input) {
                return input.getType();
            }
        }).orNull();
    }

    @Override
    public Optional<ModelReference> itemModel() {
        return itemModel;
    }

    @Override
    public AllowableValues getAllowableValues() {
        return allowableValues.orNull();
    }


}
