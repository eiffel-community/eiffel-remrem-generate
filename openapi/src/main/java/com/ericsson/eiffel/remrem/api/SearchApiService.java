package com.ericsson.eiffel.remrem.api;

import com.ericsson.eiffel.remrem.model.InlineResponse2001;
import com.ericsson.eiffel.remrem.model.SearchParameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class SearchApiService implements SearchApiDelegate {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return SearchApiDelegate.super.getRequest();
    }

    @Override
    @Operation(
            operationId = "searchUsingPOST",
            summary = "To get upstream/downstream events for an event based on the searchParameters passed",
            tags = { "API" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation =  InlineResponse2001.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "422", description = "Content Too Large - use the limit flag to limit the amount of events")
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/search/{id}",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<InlineResponse2001> searchUsingPOST(
            @Parameter(name = "id", description = "Id of the event.", required = true, schema = @Schema(description = "")) @PathVariable("id") String id,
            @Parameter(name = "limit", description = "Determines the maximum amount of events to be fetched. Use `-1` for maximum amount of the events the server can provide. ", schema = @Schema(description = "", defaultValue = "-1")) @Valid @RequestParam(value = "limit", required = false, defaultValue = "-1") Integer limit,
            @Parameter(name = "levels", description = "Determines the maximum amount of levels to search. Use `-1` for maximum amount of levels the server can search ", schema = @Schema(description = "", defaultValue = "-1")) @Valid @RequestParam(value = "levels", required = false, defaultValue = "-1") Integer levels,
            @Parameter(name = "searchParameters", description = "Select what types of links you want upstream/downstream search to follow.  Examples:    * Select `CAUSE` if you only want the search to follow `CAUSE` links     disregarding other links.   * Select `CONTEXT` and `ACTIVITY_EXECUTION` if you want to follow both `CONTEXT`     and `ACTIVITY_EXECUTION` links.  Link Types:    - CAUSE   - CONTEXT   - FLOW_CONTEXT   - ACTIVITY_EXECUTION   - PREVIOUS_ACTIVITY_EXECUTION   - PREVIOUS_VERSION   - COMPOSITION   - ENVIRONMENT   - ARTIFACT   - SUBJECT   - ELEMENT   - BASE   - CHANGE   - TEST_SUITE_EXECUTION   - TEST_CASE_EXECUTION   - IUT   - TERC   - MODIFIED_ANNOUNCEMENT   - SUB_CONFIDENCE_LEVEL   - REUSED_ARTIFACT   - VERIFICATION_BASIS   - PRECURSOR   - ORIGINAL_TRIGGER   - CONFIGURATION   - ALL    **Example**    In the following example `dlt` stands for downlink and `ult` stands for uplink ", schema = @Schema(description = "")) @Valid @RequestBody(required = false) SearchParameters searchParameters
    ) {
        return SearchApiDelegate.super.searchUsingPOST(id, limit, levels, searchParameters);
    }
}
