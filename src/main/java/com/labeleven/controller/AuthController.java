package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.AuthDTO;
import com.labeleven.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "로그인, 회원가입 등 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AuthDTO.LoginResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 이메일 또는 비밀번호"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "로그인 정보 (이메일, 비밀번호)",
                required = true,
                content = @Content(schema = @Schema(implementation = AuthDTO.LoginRequest.class))
            )
            @RequestBody AuthDTO.LoginRequest request) {
        try {
            AuthDTO.LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "회원가입",
        description = "새로운 사용자 계정을 생성합니다. 사용자명, 이메일, 비밀번호가 필요합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "회원가입 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "중복된 이메일 또는 사용자명"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO.RegisterResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "회원가입 정보 (사용자명, 이메일, 비밀번호)",
                required = true
            )
            @RequestBody AuthDTO.RegisterRequest request) {
        try {
            AuthDTO.RegisterResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "사용자명 중복 확인",
        description = "입력한 사용자명이 이미 사용 중인지 확인합니다. true면 사용 가능, false면 중복"
    )
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(
            @Parameter(description = "확인할 사용자명", required = true, example = "hong123")
            @RequestParam String username) {
        boolean exists = authService.checkUsername(username);
        return ResponseEntity.ok(ApiResponse.success(!exists));
    }
    
    @Operation(
        summary = "이메일 중복 확인",
        description = "입력한 이메일이 이미 사용 중인지 확인합니다. true면 사용 가능, false면 중복"
    )
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @Parameter(description = "확인할 이메일", required = true, example = "user@example.com")
            @RequestParam String email) {
        boolean exists = authService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.success(!exists));
    }
}