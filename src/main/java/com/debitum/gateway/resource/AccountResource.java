package com.debitum.gateway.resource;


import com.debitum.gateway.application.security.AuthenticationApplicationService;
import com.debitum.gateway.port.adapter.security.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(description = "Actions on user authentication/authorization")
@RestController
@RefreshScope
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private AuthenticationApplicationService authenticationApplicationService;

    /**
     * GET  /authenticate - check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get logged in user login")
    @ApiResponses(value = {
            @ApiResponse(code = 400, response = ErrorDTO.class, message = "Invalid request. Possible error codes:  | " +
                    ApiErrorCodes.INVALID_REQUEST_CONTENT),
            @ApiResponse(code = 500, message = "Unexpected error."),
            @ApiResponse(code = 200, message = "Successful.")})
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account - get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get logged in user info")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, response = ErrorDTO.class, message = "Invalid request. Possible error codes:  | " +
                    ApiErrorCodes.INVALID_REQUEST_CONTENT),
            @ApiResponse(code = 500, message = "Unexpected error."),
            @ApiResponse(code = 200, message = "Successful.")})
    public ResponseEntity<?> getAccount(@RequestParam(name = "refresh", required = false) String refresh) {
        if (refresh != null) {
            authenticationApplicationService.refreshUserDetails();
        }
        return Optional.ofNullable(SecurityUtils.getCurrentUser())
                .map(user -> new ResponseEntity<>(
                        new UserDTO(
                                user.getId(),
                                user.getLogin(),
                                user.getPhone(),
                                user.getCompany(),
                                user.getName(),
                                user.getStatus(),
                                user.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList())),
                        HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
    }


}
