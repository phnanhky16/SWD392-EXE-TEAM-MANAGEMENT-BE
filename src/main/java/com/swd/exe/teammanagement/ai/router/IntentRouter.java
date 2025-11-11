// com.swd.exe.teammanagement.ai.router.IntentRouter
package com.swd.exe.teammanagement.ai.router;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.entity.User;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class IntentRouter {

    Map<String, AiIntentHandler> handlers;

    // Spring sẽ autowire List<AiIntentHandler> vào đây
    public IntentRouter(List<AiIntentHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        AiIntentHandler::intentName,
                        h -> h
                ));
    }

    public Optional<IntentExecutionResult> execute(String intent, User user, JsonNode args) {
        AiIntentHandler handler = handlers.get(intent);
        if (handler == null) {
            return Optional.empty();
        }
        return Optional.of(handler.execute(user, args));
    }
}
