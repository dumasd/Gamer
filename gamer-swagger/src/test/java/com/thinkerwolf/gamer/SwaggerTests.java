package com.thinkerwolf.gamer;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.thinkerwolf.gamer.swagger.schema.Types;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SwaggerTests {

    @Test
    public void testResolvedType() {
        TypeResolver typeResolver = new TypeResolver();
        List<Integer> list = new ArrayList<>();
        ResolvedType resolvedType = typeResolver.resolve(list.getClass());
        System.out.println(resolvedType.getArrayElementType());
    }

    @Test
    public void testTypes() {
        TypeResolver typeResolver = new TypeResolver();
        ResolvedType type = typeResolver.resolve(int.class);
        System.out.println(Types.typeNameFor(type));
    }

}
