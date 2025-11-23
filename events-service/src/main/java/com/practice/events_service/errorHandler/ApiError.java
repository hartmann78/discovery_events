package com.practice.events_service.errorHandler;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Getter
@JsonPropertyOrder({"reason", "message", "status", "timestamp", "errors"})
public class ApiError {
    private final String reason;
    private final String message;
    private final String status;
    private final String timestamp;
    private final List<String> errors;

    public ApiError(String reason, String message, int code, StackTraceElement[] errors) {
        this.reason = reason;
        this.message = message;
        this.status = ApiError.HttpStatus.getStatusFromCode(code);
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.errors = Arrays.stream(errors).map(StackTraceElement::toString).toList();
    }

    @Getter
    private enum HttpStatus {
        CONTINUE(100, "CONTINUE"),
        SWITCHING_PROTOCOLS(101, "SWITCHING_PROTOCOLS"),
        PROCESSING(102, "PROCESSING"),
        CHECKPOINT(103, "CHECKPOINT"),
        OK(200, "OK"),
        CREATED(201, "CREATED"),
        ACCEPTED(202, "ACCEPTED"),
        NON_AUTHORITATIVE_INFORMATION(203, "NON_AUTHORITATIVE_INFORMATION"),
        NO_CONTENT(204, "NO_CONTENT"),
        RESET_CONTENT(205, "RESET_CONTENT"),
        PARTIAL_CONTENT(206, "PARTIAL_CONTENT"),
        MULTI_STATUS(207, "MULTI_STATUS"),
        ALREADY_REPORTED(208, "ALREADY_REPORTED"),
        IM_USED(226, "IM_USED"),
        MULTIPLE_CHOICES(300, "MULTIPLE_CHOICES"),
        MOVED_PERMANENTLY(301, "MOVED_PERMANENTLY"),
        FOUND(302, "FOUND"),
        MOVED_TEMPORARILY(302, "MOVED_TEMPORARILY"),
        SEE_OTHER(303, "SEE_OTHER"),
        NOT_MODIFIED(304, "NOT_MODIFIED"),
        USE_PROXY(305, "USE_PROXY"),
        TEMPORARY_REDIRECT(307, "TEMPORARY_REDIRECT"),
        PERMANENT_REDIRECT(308, "PERMANENT_REDIRECT"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        PAYMENT_REQUIRED(402, "Payment Required"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
        URI_TOO_LONG(414, "URI Too Long"),
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        I_AM_A_TEAPOT(418, "I'm a teapot"),
        INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"),
        METHOD_FAILURE(420, "Method Failure"),
        DESTINATION_LOCKED(421, "Destination Locked"),
        UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
        LOCKED(423, "Locked"),
        FAILED_DEPENDENCY(424, "Failed Dependency"),
        TOO_EARLY(425, "Too Early"),
        UPGRADE_REQUIRED(426, "Upgrade Required"),
        PRECONDITION_REQUIRED(428, "Precondition Required"),
        TOO_MANY_REQUESTS(429, "Too Many Requests"),
        REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
        UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported"),
        VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
        INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
        LOOP_DETECTED(508, "Loop Detected"),
        BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
        NOT_EXTENDED(510, "Not Extended"),
        NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),

        UNKNOWN(-1, "UNKNOWN STATUS");

        private final int code;
        private final String description;

        HttpStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getStatusFromCode(int code) {
            for (ApiError.HttpStatus status : ApiError.HttpStatus.values()) {
                if (code == status.code) {
                    return status.description;
                }
            }
            return UNKNOWN.toString();
        }
    }
}
