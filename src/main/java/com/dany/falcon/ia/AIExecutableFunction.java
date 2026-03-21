package com.dany.falcon.ia;

import com.dany.falcon.ia.schema.FunctionSchema;

import java.util.List;
import java.util.Map;

public interface AIExecutableFunction {
    String getName();
    String getDescription();
    List<String> getParameters();
    FunctionSchema getSchema();
    String execute(Map<String, Object> args);
}