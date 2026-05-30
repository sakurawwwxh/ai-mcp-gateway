package cn.tomato.ai.domain.session.model.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Wxh
 * @date 2026年05月29日 15:42
 */
@Slf4j
public class McpSchemaVO {

    private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REF = new TypeReference<>() {
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static JSONRPCMessage deserializeJsonRpcMessage(String jsonText)
            throws IOException {

        log.debug("Received JSON message: {}", jsonText);

        var map = objectMapper.readValue(jsonText, MAP_TYPE_REF);

        if (map.containsKey("method") && map.containsKey("id")) {
            return objectMapper.convertValue(map, JSONRPCRequest.class);
        } else if (map.containsKey("result") || map.containsKey("error")) {
            return objectMapper.convertValue(map, JSONRPCResponse.class);
        }

        throw new IllegalArgumentException("Cannot deserialize JSONRPCMessage: " + jsonText);
    }

    public sealed interface JSONRPCMessage permits JSONRPCRequest,JSONRPCResponse {
        String jsonrpc ();
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCRequest(
           @JsonProperty("jsonrpc") String jsonrpc,
           @JsonProperty("method") String method,
           @JsonProperty("id") Object id,
           @JsonProperty("params") Object params
    )implements JSONRPCMessage {
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCResponse(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("id") Object id,
            @JsonProperty("result") Object result,
            @JsonProperty("error") JSONRPCError error
    ) implements JSONRPCMessage {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record JSONRPCError(
                @JsonProperty("code") int code,
                @JsonProperty("message") String message,
                @JsonProperty("data") Object data) {
        }
    }
}
