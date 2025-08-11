package com.ericsson.eiffel.remrem.api;

import com.ericsson.eiffel.remrem.model.InlineResponse200;
import com.ericsson.eiffel.remrem.model.EiffelEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RestController
public class EventsApiService implements EventsApiDelegate {
    static private List<String> events = new ArrayList();

    static {
        loadEventsFromFiles();
    }

    public static void loadEventsFromFiles() {
        String dir = "src/test/resources/eventrepository/events";
        File directory = new File(dir);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (!file.isFile())
                continue;

            String filename = file.getAbsolutePath();

            try {
                String event = loadEventFromFile(filename);
                events.add(event);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String loadEventFromFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/events/{id}",
            produces = { "application/json" }
    )
    public ResponseEntity<EiffelEvent> getEventUsingGET(
            @Parameter(name = "id", description = "Id of the event.", required = true, schema = @Schema(description = "")) @PathVariable("id") String id
    ) {

        for (String event : events) {
            try {
                Object document = Configuration.defaultConfiguration().jsonProvider().parse(event);
                Object value = JsonPath.read(document, "$.id");
                if (value.equals(id)) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return new ResponseEntity<>(mapper.readValue(event, EiffelEvent.class), HttpStatus.OK);
                    } catch (JsonProcessingException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            } catch (PathNotFoundException e) {
                // The given property path doesn't exist. No need to continue, the event doesn't match
                // given criteria...
                continue;
            } catch (JsonPathException e) {
                // The given path is mangled. Maybe handling of this, more general exception, is sufficient,
                // but I split the handling as I don't know if it can be useful in a future...
                continue;
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/events",
            produces = { "application/json" }
    )
    public ResponseEntity<InlineResponse200> getEventsUsingGET(
        @Parameter(name = "pageSize", description = "The number of events to be displayed per page.", schema = @Schema(description = "", defaultValue = "500")) @Valid @RequestParam(value = "pageSize", required = false, defaultValue = "500") Integer pageSize,
        @Parameter(name = "params", description = "", schema = @Schema(description = "")) @Valid @RequestParam(value = "", required = false) Map<String, String> params) {
        String keysToIgnore[] = { "pageSize", "shallow" };
        ObjectMapper mapper = new ObjectMapper();
        List<EiffelEvent> matchedEvents = new ArrayList<>();
        for (String event : events) {
            boolean matches = true;
            try {
                Object document = Configuration.defaultConfiguration().jsonProvider().parse(event);
                process_keys:
                for (String key : params.keySet()) {
                    for (String keyToIgnore : keysToIgnore) {
                        if (key.equals(keyToIgnore))
                            // This isn't a property path; take another key.
                            continue process_keys;
                    }

                    String expected = params.get(key);
                    Object value = JsonPath.read(document, "$." + key);
                    if (expected == null || expected.equals(""))
                        // If value of query parameter is not present, don't compare value, just test
                        // if the property (given by key) exists. As the flow reached this point it means
                        // that the property exists (otherwise PathNotFoundException would have been thrown).
                        continue;

                    if (!value.equals(expected)) {
                        matches = false;
                        // The criteria is not matched. As they're treated as AND, no need to continue.
                        // Try another event.
                        break;
                    }
                }
            }
            catch (PathNotFoundException e) {
                // The given property path doesn't exist. No need to continue, the event doesn't match
                // given criteria...
                continue;
            }
            catch (JsonPathException e) {
                // The given path is mangled. Maybe handling of this, more general exception, is sufficient,
                // but I split the handling as I don't know if it can be useful in a future...
                continue;
            }

            if (matches) {
                try {
                    matchedEvents.add(mapper.readValue(event, EiffelEvent.class));
                }
                catch (JsonProcessingException e) {
                    // Something went wrong...
                    // TODO: Should INTERNAL SERVER ERROR be responded?
                    e.printStackTrace();
                }
            }
        }

        InlineResponse200 response = new InlineResponse200();
        response.pageSize(1).pageNo(1).items(matchedEvents);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}