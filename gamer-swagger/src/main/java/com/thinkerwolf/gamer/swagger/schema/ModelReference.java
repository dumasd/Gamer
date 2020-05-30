package com.thinkerwolf.gamer.swagger.schema;


import com.google.common.base.Optional;

public interface ModelReference {

    String getType();

    boolean isMap();

    boolean isCollection();

    String getItemType();

    Optional<ModelReference> itemModel();

    AllowableValues getAllowableValues();

}
