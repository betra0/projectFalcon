package com.dany.falcon.ia.schema;


import java.util.List;

public class ParamSchema {
    private String type; // "string", "number", etc
    private List<String> enumValues; // opcional

    public ParamSchema(String type, List<String> enumValues) {
        this.type = type;
        this.enumValues = enumValues;
    }

    public String getType() {
        return type;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }
}
