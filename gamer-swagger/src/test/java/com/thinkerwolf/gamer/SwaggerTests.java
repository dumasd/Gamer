package com.thinkerwolf.gamer;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
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

}
