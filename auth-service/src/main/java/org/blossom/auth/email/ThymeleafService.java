package org.blossom.auth.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class ThymeleafService {
    @Autowired
    private TemplateEngine templateEngine;

    public String createContent(String template, Map<String, Object> variables) {
        final Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(template, context);
    }
}
