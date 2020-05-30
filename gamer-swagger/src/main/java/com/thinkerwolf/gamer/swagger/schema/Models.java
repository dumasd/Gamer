package com.thinkerwolf.gamer.swagger.schema;


import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;

public class Models {

    public static ModelReference create(ResolvedType resolvedType) {
        return containerRef(resolvedType).or(mapRef(resolvedType)).or(modelRef(resolvedType));
    }

    private static Optional<ModelReference> containerRef(ResolvedType resolvedType) {
        if (Types.isCollection(resolvedType)) {
            ResolvedType element = Types.collectionElementType(resolvedType);
            ModelRef ref = new ModelRef(Types.getCollectionName(resolvedType), create(element), null);
            return Optional.of(ref);
        }
        return Optional.absent();
    }

    private static Optional<ModelReference> mapRef(ResolvedType resolvedType) {
        if (Types.isMap(resolvedType)) {
            ModelRef ref = new ModelRef("Map", create(Types.mapValueType(resolvedType)), null, true);
            return Optional.of(ref);
        }
        return Optional.absent();
    }

    private static ModelReference modelRef(ResolvedType resolvedType) {
        if (Void.class.equals(resolvedType.getErasedType()) || Void.TYPE.equals(resolvedType.getErasedType())) {
            return new ModelRef("void");
        }
        return new ModelRef(Types.typeNameFor(resolvedType), null);
    }

}
