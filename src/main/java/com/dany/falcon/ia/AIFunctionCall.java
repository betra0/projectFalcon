package com.dany.falcon.ia;

import java.util.List;
import java.util.Map;

public class AIFunctionCall {
    private String name;
    private Map<String, Object> args;

    public AIFunctionCall(String name, Map<String, Object> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}
