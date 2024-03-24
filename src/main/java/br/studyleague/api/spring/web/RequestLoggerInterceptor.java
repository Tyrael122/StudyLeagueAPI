package br.studyleague.api.spring.web;

import br.studyleague.api.model.RequestLog;
import br.studyleague.api.repository.RequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {
    private final static int MAX_EXCEPTION_MESSAGE_LENGTH = 250;

    private final RequestLogRepository requestLogRepository;

    public RequestLoggerInterceptor(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        saveRequestLog(request, null);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex == null) {
            HandlerInterceptor.super.afterCompletion(request, response, handler, null);
        }

        saveRequestLog(request, ex);

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void saveRequestLog(HttpServletRequest request, Exception ex) {
        RequestLog loggingRequestLog = new RequestLog();

        loggingRequestLog.setMethod(request.getMethod());
        loggingRequestLog.setPath(request.getRequestURI());
        loggingRequestLog.setQueryString(request.getQueryString());

        loggingRequestLog.setTimestamp(LocalDateTime.now());

        loggingRequestLog.setException(getExceptionMessage(ex));

        requestLogRepository.save(loggingRequestLog);
    }

    @NotNull
    private static String getExceptionMessage(Exception ex) {
        StringBuilder exceptionMessage = new StringBuilder();
        if (ex != null) {
            exceptionMessage.append(ex.getMessage());

            for (StackTraceElement element : ex.getStackTrace()) {
                if (exceptionMessage.length() + element.toString().length() > MAX_EXCEPTION_MESSAGE_LENGTH) {
                    break;
                }

                exceptionMessage.append("\n").append(element);
            }
        }

        return exceptionMessage.toString();
    }
}
