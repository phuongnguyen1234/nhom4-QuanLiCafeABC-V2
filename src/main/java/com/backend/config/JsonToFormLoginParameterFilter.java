package com.backend.config; 

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JsonToFormLoginParameterFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JsonToFormLoginParameterFilter.class);

    public JsonToFormLoginParameterFilter() {
        // Log này sẽ xuất hiện một lần khi ứng dụng khởi động nếu bean được tạo.
        log.info("JsonToFormLoginParameterFilter has been instantiated by Spring.");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathRequestMatcher loginRequestMatcher = new AntPathRequestMatcher("/auth/login", HttpMethod.POST.name());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (loginRequestMatcher.matches(request) &&
            request.getContentType() != null &&
            request.getContentType().toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE.toLowerCase())) {

            // Log này sẽ xuất hiện nếu request khớp URL và Content-Type
            log.debug("JsonToFormLoginParameterFilter is processing the request for: {}", request.getRequestURI());

            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
           log.debug("Request body: {}", body);

            if (StringUtils.hasText(body)) {
                try {
                    Map<String, String> jsonMap = objectMapper.readValue(body, new TypeReference<>() {});
                    log.debug("Parsed JSON map: {}", jsonMap);

                    ParameterRequestWrapper wrappedRequest = new ParameterRequestWrapper(request, jsonMap);
                    filterChain.doFilter(wrappedRequest, response);
                    return; 
                } catch (IOException e) {
                    log.error("Error parsing JSON request body: {}", body, e);
                }
            } else {
                log.warn("Request body is empty for JSON login request.");
            }
        } else {
            // Log này sẽ xuất hiện nếu request KHÔNG khớp URL hoặc Content-Type.
            // Đảm bảo TRACE được bật cho class này để thấy log này.
            log.trace("JsonToFormLoginParameterFilter is SKIPPING request for URL: {} with Content-Type: {}", request.getRequestURI(), request.getContentType());
        }
         filterChain.doFilter(request, response);
    }

    private static class ParameterRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> modifiableParameters;


        public ParameterRequestWrapper(HttpServletRequest request, Map<String, String> customParameters) {
            super(request);
            this.modifiableParameters = new HashMap<>(request.getParameterMap()); // Start with original params
            // Thêm các custom parameters vào modifiableParameters
            if (customParameters != null) {
                for (Map.Entry<String, String> entry : customParameters.entrySet()) {
                    this.modifiableParameters.put(entry.getKey(), new String[]{entry.getValue()});
                }
            }
        }

        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            String value = (values != null && values.length > 0) ? values[0] : null;
            log.trace("ParameterRequestWrapper.getParameter('{}') returning: '{}'", name, value);
            return value;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            log.trace("ParameterRequestWrapper.getParameterMap() returning: {}", this.modifiableParameters);
            return Collections.unmodifiableMap(this.modifiableParameters);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            log.trace("ParameterRequestWrapper.getParameterNames() returning names for: {}", this.modifiableParameters.keySet());
            return Collections.enumeration(this.modifiableParameters.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
             String[] values = this.modifiableParameters.get(name);
            log.trace("ParameterRequestWrapper.getParameterValues('{}') returning: {}", name, values != null ? java.util.Arrays.toString(values) : null);
            return values;
        }
    }
}
