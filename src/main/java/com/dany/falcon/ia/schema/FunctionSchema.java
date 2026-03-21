package com.dany.falcon.ia.schema;

import java.util.List;
import java.util.Map;

public class FunctionSchema {
    private Map<String, ParamSchema> properties;
    private List<String> required;

    public FunctionSchema(Map<String, ParamSchema> properties, List<String> required) {
        this.properties = properties;
        this.required = required;
    }

    public Map<String, ParamSchema> getProperties() {
        return properties;
    }

    public List<String> getRequired() {
        return required;
    }
}