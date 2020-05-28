package com.thinkerwolf.gamer.swagger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkerwolf.gamer.swagger.dto.ApiDescriptor;
import com.thinkerwolf.gamer.swagger.dto.ApiListing;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 本地模型转化成swagger
 *
 * @author wukai
 */
public class DtoToSwagger2Mapper {


    public Swagger mapDocument(Document document) {
        Swagger swagger = new Swagger();
        swagger.setPaths(mapApiListing(document.getApiListingMap()));
        swagger.setBasePath("/");
        swagger.setHost("127.0.0.1");
        swagger.setInfo(new Info());
        swagger.setTags(tags(document.getTags()));
        return swagger;
    }


    protected Map<String, Path> mapApiListing(Map<String, ApiListing> listingMap) {
        Map<String, Path> paths = Maps.newHashMap();
        for (ApiListing listing : listingMap.values()) {
            for (ApiDescriptor api : listing.getApis()) {
                Path path = paths.get(api.getPath());
                if (path == null) {
                    paths.put(api.getPath(), mapOperations(new Path(), api.getOperations()));
                } else {
                    paths.put(api.getPath(), mapOperations(path, api.getOperations()));
                }
            }
        }
        return paths;
    }

    protected Path mapOperations(Path path, List<com.thinkerwolf.gamer.swagger.dto.Operation> operations) {
        for (com.thinkerwolf.gamer.swagger.dto.Operation operation : operations) {
            path.set(Optional.ofNullable(operation.getMethod()).orElse("get"), mapOperation(operation));
        }
        return path;
    }

    protected Operation mapOperation(com.thinkerwolf.gamer.swagger.dto.Operation operation) {
        Operation op = new Operation();
        op.setTags(Lists.newArrayList(operation.getTags()));
        op.setSummary(operation.getSummary());
        op.setDescription(operation.getNotes());
        for (String p : operation.getProtocols()) {
            op.addScheme(Scheme.forValue(p));
        }
        for (com.thinkerwolf.gamer.swagger.dto.Parameter parameter : operation.getParameters()) {
            op.addParameter(mapParameter(parameter));
        }
        return op;
    }

    protected Parameter mapParameter(com.thinkerwolf.gamer.swagger.dto.Parameter p) {
        Parameter parameter = new BodyParameter()
                .name(p.getName()).description(p.getDescription());
        parameter.setRequired(p.isRequired());
        parameter.setAccess(p.getAccess());
        return parameter;
    }

    protected List<Tag> tags(Set<com.thinkerwolf.gamer.swagger.dto.Tag> tagSet) {
        List<Tag> tags = Lists.newArrayList();
        for (com.thinkerwolf.gamer.swagger.dto.Tag _t : tagSet) {
            Tag tag = new Tag();
            tag.setName(_t.getName());
            tag.setDescription(_t.getDescription());
            tags.add(tag);
        }
        return tags;
    }


}
