package com.example.movies.external_api;

import com.example.movies.exceptions.EntityNotFoundException;
import com.example.movies.exceptions.ExternalApiException;
import com.example.movies.exceptions.MissingEntityInformationException;
import com.example.movies.model.OmdbResponse;
import com.example.movies.constants.StringBundle.DefaultConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import com.example.movies.constants.StringBundle.ExceptionStrings;

@Component
public class OmdbClient {
    private final RestClient restClient;

    @Value("${external.omdb.api-key}")
    private String restApiKey;

    public OmdbClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public OmdbResponse fetchByTitleOrImdbId(Optional<String> title, Optional<String> imdbId) {
        if (isInvalid(title) && isInvalid(imdbId)) {
            throw new MissingEntityInformationException("Movie", "title", "imdbid");
        }

        return restClient.get().uri(uriBuilder -> {
                            uriBuilder.queryParam("apikey", restApiKey);

                            if (isValid(imdbId)) {
                                uriBuilder.queryParam("i", imdbId.get());
                                return uriBuilder.build();
                            }

                            if (isValid(title)) {
                                uriBuilder.queryParam("t", title.get());
                            }

                            return uriBuilder.build();
                        }
                ).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (r, e) ->
                        {
                            int statusCode = e.getStatusCode().value();
                            switch (statusCode) {
                                case 401 -> throw new ExternalApiException(ExceptionStrings.EXTERNAL_API_UNAUTHORIZED);
                                case 403 -> throw new ExternalApiException(ExceptionStrings.EXTERNAL_API_FORBIDDEN);
                                case 404 -> throw new EntityNotFoundException(DefaultConstants.MOVIE_ENTITY);
                                case 429 -> throw new ExternalApiException(ExceptionStrings.EXTERNAL_API_RATE_LIMIT_EXCEEDED);
                                default ->
                                        throw new ExternalApiException(ExceptionStrings.EXTERNAL_API_DEFAULT_400 + statusCode);
                            }
                        }

                ).onStatus(HttpStatusCode::is5xxServerError, (r, e) ->
                        {
                            throw new ExternalApiException("Server is down or failing to respond with code " + e.getStatusCode());
                        }
                ).body(OmdbResponse.class);
    }

    private boolean isValid(Optional<String> element) {
        return element.isPresent() && !element.get().isBlank();
    }

    private boolean isInvalid(Optional<String> element) {
        return element.isPresent() && element.get().isBlank();
    }
}
