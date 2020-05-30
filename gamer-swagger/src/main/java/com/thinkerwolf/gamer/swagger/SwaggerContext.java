package com.thinkerwolf.gamer.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.SessionParam;
import com.thinkerwolf.gamer.core.mvc.ActionInvocation;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.swagger.dto.ApiDescriptor;
import com.thinkerwolf.gamer.swagger.dto.ApiListing;
import com.thinkerwolf.gamer.swagger.dto.Operation;
import com.thinkerwolf.gamer.swagger.dto.Parameter;
import com.thinkerwolf.gamer.swagger.schema.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Swagger context
 *
 * @author wukai
 */
public final class SwaggerContext {

    private static TypeResolver typeResolver = new TypeResolver();

    private static DocumentCache documentCache = new DocumentCache();

    /**
     * ApiListing
     */
    private static Map<String, ApiListing> apiListingMap = new ConcurrentHashMap<>();

    public static Document getDocument(String group) {
        return documentCache.getCache(group);
    }

    public static void init(Multimap<Class<?>, ActionInvocation> actionInv) {
        Map<Class<?>, Collection<ActionInvocation>> map = actionInv.asMap();
        for (Map.Entry<Class<?>, Collection<ActionInvocation>> entry : map.entrySet()) {
            Class<?> clazz = entry.getKey();
            Api api = clazz.getAnnotation(Api.class);
            String key = api != null ? api.toString() : clazz.getName();
            ApiListing apiListing = apiListingMap.computeIfAbsent(key, s -> {
                String apiVersion = "2.0";
                Set<String> tags;
                Set<String> protocols = Sets.newHashSet();
                String description;
                boolean hidden = false;
                if (api != null) {
                    hidden = api.hidden();
                    if (Annotations.isBlank(api.tags())) {
                        tags = Sets.newHashSet(api.value());
                    } else {
                        tags = Sets.newHashSet(api.tags());
                    }
                    description = api.description();
                    parseProtocols(api.protocols(), protocols);
                } else {
                    description = clazz.getSimpleName();
                    tags = new HashSet<>();
                }
                return new ApiListing(apiVersion, "/", hidden, protocols, Lists.newArrayList(), tags, description);
            });
            for (ActionInvocation invocation : entry.getValue()) {
                createApi(apiListing, invocation);
            }
        }

        Document document = new Document(Docket.DEFAULT_GROUP_NAME, apiListingMap);
        documentCache.addCache(document);
    }


    public static ApiDescriptor createApi(ApiListing listing, ActionInvocation actionInv) {
        Method method = actionInv.getMethod();
        ApiDescriptor apiDescriptor = new ApiDescriptor("/" + actionInv.getCommand(), Lists.newArrayList());
        ApiOperation aop = method.getAnnotation(ApiOperation.class);
        Operation operation = new Operation();
        Set<String> protocols = Sets.newHashSet();
        if (aop != null) {
            operation.setHidden(aop.hidden());
            operation.setCode(aop.code());
            operation.setSummary(aop.value());
            operation.setNotes(aop.notes());
            if (Annotations.isBlank(aop.tags())) {
                operation.setTags(listing.getTags());
            } else {
                operation.setTags(Sets.newHashSet(aop.tags()));
            }
            parseProtocols(aop.protocols(), protocols);
        } else {
            operation.setCode(200);
            operation.setTags(listing.getTags());
            operation.setNotes(method.getName());
            operation.setSummary(method.getName());
        }
        operation.setProtocols(protocols);
        List<Parameter> parameters = Lists.newArrayList();
        parseParameters(method, parameters);

        operation.setParameters(parameters);
        apiDescriptor.getOperations().add(operation);
        listing.getApis().add(apiDescriptor);
        return apiDescriptor;
    }

    private static void parseProtocols(String protocols, Set<String> set) {
        for (String protocol : Constants.COMMA_SPLIT_PATTERN.split(protocols.trim())) {
            if (StringUtils.isNotBlank(protocol)) {
                set.add(protocol);
            }
        }
    }

    private static void parseParameters(Method method, List<Parameter> parameters) {
        ApiImplicitParams aip = method.getAnnotation(ApiImplicitParams.class);
        if (aip == null || aip.value().length == 0) {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            Class[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class paramType = parameterTypes[i];
                if (Request.class.isAssignableFrom(paramType)
                        || Response.class.isAssignableFrom(paramType)) {
                    continue;
                }
                if (ClassUtils.getAnnotation(paramAnnotations[i], SessionParam.class) != null) {
                    continue;
                }
                RequestParam rp = ClassUtils.getAnnotation(paramAnnotations[i], RequestParam.class);
                if (rp == null) {
                    continue;
                }
                Parameter parameter = new Parameter();
                parameter.setName(rp.value());
                parameter.setDescription(rp.value());
                parameter.setDataType(paramType.getSimpleName());
                parameter.setDataTypeClass(paramType);
                parameter.setRequired(rp.required());
                parameter.setModelRef(createModel(parameter));
                parameters.add(parameter);
            }
        } else {
            for (int i = 0; i < aip.value().length; i++) {
                ApiImplicitParam a = aip.value()[i];
                Parameter parameter = new Parameter();
                parameter.setName(a.name());
                parameter.setRequired(a.required());
                parameter.setDataType(a.dataType());
                parameter.setDefaultValue(a.defaultValue());
                parameter.setDataTypeClass(a.dataTypeClass());
                parameter.setDescription(a.value());
                parameter.setModelRef(createModel(parameter));
                parameters.add(parameter);
            }
        }
    }


    private static ModelReference createModel(Parameter parameter) {
        Class<?> clazz = parameter.getDataTypeClass();
        if (clazz == null) {
            try {
                clazz = ClassUtils.forName(parameter.getDataType());
            } catch (Exception e) {
                return new ModelRef(parameter.getDataType());
            }
        }
        return Models.create(typeResolver.resolve(clazz));

    }


}
