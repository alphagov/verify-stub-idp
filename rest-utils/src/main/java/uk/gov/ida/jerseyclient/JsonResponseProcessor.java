package uk.gov.ida.jerseyclient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.ErrorStatusDto;
import uk.gov.ida.common.ExceptionType;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static uk.gov.ida.exceptions.ApplicationException.createExceptionFromErrorStatusDto;
import static uk.gov.ida.exceptions.ApplicationException.createUnauditedException;

public class JsonResponseProcessor {

    private final ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(JsonResponseProcessor.class);

    @Inject
    public JsonResponseProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T getJsonEntity(URI uri, GenericType<T> genericType, Class<T> clazz, Response clientResponse) {
        Response successResponse = filterErrorResponses(uri, clientResponse);
        try {
            if ((clazz != null && clazz == Response.class) || (genericType != null && genericType.getRawType() == Response.class)) {
                throw createUnauditedException(ExceptionType.INVALID_CLIENTRESPONSE_PARAM, UUID.randomUUID());
            } else if (clazz == null && genericType == null) {
                return (T) new Object();
            }
            return getEntity(genericType, clazz, successResponse);
        } finally {
            clientResponse.close(); //Do this to avoid any possibility of a connection leak.
        }
    }

    private <T> T getEntity(GenericType<T> genericType, Class<T> entityClazz, Response response) {
        try {
            if (response.hasEntity()) {
                if (entityClazz != null) {
                    return response.readEntity(entityClazz);
                } else if (genericType != null) {
                    return response.readEntity(genericType);
                }
            }
        }
        catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e);
        }
        throw new IllegalArgumentException("Client response has no entity.");
    }

    private Response filterErrorResponses(URI uri, Response response) {
        switch (response.getStatusInfo().getFamily()) {
            case SERVER_ERROR:
                throw createErrorStatus(response, ExceptionType.REMOTE_SERVER_ERROR, uri);
            case CLIENT_ERROR:
                throw createErrorStatus(response, ExceptionType.CLIENT_ERROR, uri);
            default:
                return response;
        }
    }

    private ApplicationException createErrorStatus(Response response, ExceptionType exceptionType, URI uri) {
        String entity = response.readEntity(String.class);
        try {
            return createExceptionFromErrorStatusDto(objectMapper.readValue(entity, ErrorStatusDto.class), uri);
        } catch (JsonParseException | JsonMappingException e) {
            return createExceptionFromErrorStatusDto(ErrorStatusDto.createUnauditedErrorStatus(UUID.randomUUID(), exceptionType, entity), uri);
        } catch (IOException e) {
            LOG.error("Unexpected status code [{}] returned from service using URI: {}. Body: {}",
                    response.getStatus(), uri, entity);
            return createUnauditedException(exceptionType, UUID.randomUUID(), e, uri);
        }
    }

}
