package br.studyleague.api.spring.web;

import br.studyleague.api.model.RequestLog;
import br.studyleague.api.repository.RequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {
    private final RequestLogRepository requestLogRepository;

    public RequestLoggerInterceptor(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequestLog loggingRequestLog = new RequestLog();

        loggingRequestLog.setMethod(request.getMethod());
        loggingRequestLog.setPath(request.getRequestURI());
        loggingRequestLog.setQueryString(request.getQueryString());

        loggingRequestLog.setTimestamp(LocalDateTime.now());

        requestLogRepository.save(loggingRequestLog);

        return true;
    }
}
