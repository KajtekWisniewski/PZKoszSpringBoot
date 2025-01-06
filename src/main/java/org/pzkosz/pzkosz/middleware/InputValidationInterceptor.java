package org.pzkosz.pzkosz.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.*;

@Component
public class InputValidationInterceptor implements HandlerInterceptor {

    private static final Set<String> OPTIONAL_FIELDS = Set.of("teamId");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);

                if (OPTIONAL_FIELDS.contains(paramName)) {
                    continue;
                }

                if (paramValue != null && paramValue.trim().isEmpty()) {
                    String errorMessage = "Invalid input: Field '" + paramName + "' cannot be empty.";
                    response.sendRedirect("/error?errorMessage=" + errorMessage);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }
}
