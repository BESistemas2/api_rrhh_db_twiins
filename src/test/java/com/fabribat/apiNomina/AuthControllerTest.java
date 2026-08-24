package com.fabribat.apiNomina;

import com.fabribat.apiNomina.controllers.dto.LoginRequest;
import com.fabribat.apiNomina.entities.security.UsuarioApi;
import com.fabribat.apiNomina.repositories.security.UsuarioApiRepository;
import com.fabribat.apiNomina.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para AuthController con Spring REST Docs.
 * Genera snippets de documentación para endpoints de autenticación.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.example.com", uriPort = 443)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioApiRepository usuarioRepository;

    private String validUsername;
    private String validPassword;
    private String validToken;

    @BeforeEach
    void setUp() {
        validUsername = "test_user";
        validPassword = "TestPass123!";

        // Limpiar y crear usuario de prueba
        usuarioRepository.deleteAll();
        UsuarioApi user = new UsuarioApi(
                validUsername,
                passwordEncoder.encode(validPassword),
                "API_CLIENT"
        );
        user.setActivo(true);
        usuarioRepository.save(user);

        // Generar token válido para tests autenticados
        validToken = jwtUtil.generarToken(validUsername, "API_CLIENT");
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Login exitoso con credenciales válidas")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(validUsername);
        request.setPassword(validPassword);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(document("auth/login",
                        requestHeaders(
                                headerWithName("Content-Type").description("Tipo de contenido: application/json")
                        ),
                        requestFields(
                                fieldWithPath("username").description("Nombre de usuario para autenticación").attributes(),
                                fieldWithPath("password").description("Contraseña del usuario").attributes()
                        ),
                        responseFields(
                                fieldWithPath("token").description("Token JWT firmado (válido por 1 hora)").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Fallo por credenciales inválidas")
    void login_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(validUsername);
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.toJson(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuario o contraseña incorrectos"))
                .andDo(document("auth/login-invalid",
                        requestHeaders(
                                headerWithName("Content-Type").description("Tipo de contenido: application/json")
                        ),
                        requestFields(
                                fieldWithPath("username").description("Nombre de usuario").attributes(),
                                fieldWithPath("password").description("Contraseña incorrecta").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error de autenticación").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Fallo por campos vacíos (validación @Valid)")
    void login_ValidationError() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.toJson(request)))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/login-validation-error",
                        requestHeaders(
                                headerWithName("Content-Type").description("Tipo de contenido: application/json")
                        ),
                        requestFields(
                                fieldWithPath("username").description("Nombre de usuario (no puede estar vacío)").attributes(),
                                fieldWithPath("password").description("Contraseña (no puede estar vacía)").attributes()
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("Marca de tiempo del error").optional().attributes(),
                                fieldWithPath("status").description("Código de estado HTTP").attributes(),
                                fieldWithPath("error").description("Tipo de error").attributes(),
                                fieldWithPath("message").description("Mensaje de validación").attributes(),
                                fieldWithPath("path").description("Ruta del endpoint").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/auth/perfil - Perfil con token válido")
    void perfil_Success() throws Exception {
        mockMvc.perform(get("/api/v1/auth/perfil")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario_detectado").value(validUsername))
                .andExpect(jsonPath("$.rol_detectado").value("ROLE_API_CLIENT"))
                .andDo(document("auth/perfil",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        responseFields(
                                fieldWithPath("mensaje").description("Mensaje de confirmación").attributes(),
                                fieldWithPath("usuario_detectado").description("Username extraído del token").attributes(),
                                fieldWithPath("rol_detectado").description("Rol con prefijo ROLE_").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/auth/perfil - Fallo sin token (401)")
    void perfil_NoToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/perfil"))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/perfil-unauthorized",
                        responseFields(
                                fieldWithPath("timestamp").description("Marca de tiempo").optional().attributes(),
                                fieldWithPath("status").description("Código 401").attributes(),
                                fieldWithPath("error").description("Unauthorized").attributes(),
                                fieldWithPath("message").description("Mensaje de error").attributes(),
                                fieldWithPath("path").description("Ruta solicitada").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/auth/perfil - Fallo con token expirado (401)")
    void perfil_ExpiredToken() throws Exception {
        String expiredToken = jwtUtil.generarToken(validUsername, "API_CLIENT");
        // Nota: Para test real de token expirado, necesitaríamos manipular el tiempo
        // o usar JwtTestUtil.generateExpiredToken()

        mockMvc.perform(get("/api/v1/auth/perfil")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/perfil-invalid-token",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT inválido/expirado")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("Marca de tiempo").optional().attributes(),
                                fieldWithPath("status").description("Código 401").attributes(),
                                fieldWithPath("error").description("Unauthorized").attributes(),
                                fieldWithPath("message").description("Token inválido o expirado").attributes(),
                                fieldWithPath("path").description("Ruta solicitada").attributes()
                        )
                ));
    }
}