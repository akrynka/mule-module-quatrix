package org.quatrix.model;

import io.swagger.client.model.FileResp;

import java.math.BigDecimal;
import java.util.UUID;

//@Getter
//@EqualsAndHashCode
//@ToString
public class FileInfo {

    private UUID id;
    private BigDecimal created;
    private BigDecimal modified;
    private String name;
    private UUID parentId;
    private BigDecimal size;
    private BigDecimal modifiedMs;
    private String type;
    private Object metadata;
    private BigDecimal operations;
    private String subType;

    public UUID getId() {
        return id;
    }

    public BigDecimal getCreated() {
        return created;
    }

    public BigDecimal getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal getModifiedMs() {
        return modifiedMs;
    }

    public String getType() {
        return type;
    }

    public Object getMetadata() {
        return metadata;
    }

    public BigDecimal getOperations() {
        return operations;
    }

    public String getSubType() {
        return subType;
    }

    public static FileInfo from(FileResp fileResp) {
        final FileInfo info = new FileInfo();
        info.id = fileResp.getId();
        info.created = fileResp.getCreated();
        info.modified = fileResp.getModified();
        info.name = fileResp.getName();
        info.parentId = fileResp.getParentId();
        info.size = fileResp.getSize();
        info.modifiedMs = fileResp.getModifiedMs();
        info.type = fileResp.getType();
        info.metadata = fileResp.getMetadata();
        info.operations = fileResp.getOperations();
        info.subType = fileResp.getSubType();

        return info;
    }
}
