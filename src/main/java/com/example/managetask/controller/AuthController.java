package com.example.managetask.controller;

import com.example.managetask.dao.request.SignUpRequest;
import com.example.managetask.dao.request.SigninRequest;
import com.example.managetask.dao.response.JwtAuthenticationResponse;
import com.example.managetask.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Sign up a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed up successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class)) })
    })
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }


    @Operation(summary = "Sign in an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed in successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class)) })
    })
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }
}
