
package com.dany.falcon.ia;
import java.util.List;

public record AIResponse(Message reply, List<AIFunctionCall> functions) {


}