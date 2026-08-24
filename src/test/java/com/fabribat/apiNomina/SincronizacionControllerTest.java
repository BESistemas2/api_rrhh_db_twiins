package com.fabribat.apiNomina;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para SincronizacionController con Spring REST Docs.
 * Endpoints protegidos que requieren token JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.example.com", uriPort = 443)
@ActiveProfiles("test")
class SincronizacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String validToken;

    @BeforeEach
    void setUp() {
        // Generar token válido para tests
        validToken = jwtTestUtil.generateValidToken("sync_user");
    }

    @Test
    @DisplayName("POST /api/v1/sincronizacion/sucursal-matriz - Sincroniza sucursal matriz")
    void syncSucursalMatriz_Success() throws Exception {
        mockMvc.perform(post("/api/v1/sincronizacion/sucursal-matriz")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sincronizacion/sucursal-matriz",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("").description("Respuesta de ORPHEUS (esperado: 'TRUE' en caso de éxito)").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/sincronizacion/departamento/{codDepartamento} - Sincroniza departamento")
    void syncDepartamento_Success() throws Exception {
        String codDepartamento = "001";

        mockMvc.perform(post("/api/v1/sincronizacion/departamento/{codDepartamento}", codDepartamento)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sincronizacion/departamento",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codDepartamento").description("Código del departamento a sincronizar (ej. '001')")
                        ),
                        responseFields(
                                fieldWithPath("").description("Respuesta de ORPHEUS al sincronizar departamento").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/sincronizacion/cargo/{codCargo} - Sincroniza cargo")
    void syncCargo_Success() throws Exception {
        String codCargo = "001";

        mockMvc.perform(post("/api/v1/sincronizacion/cargo/{codCargo}", codCargo)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sincronizacion/cargo",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codCargo").description("Código del cargo/puesto a sincronizar (ej. '001')")
                        ),
                        responseFields(
                                fieldWithPath("").description("Respuesta de ORPHEUS al sincronizar cargo").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/sincronizacion/empleado/{cedula} - Sincroniza empleado")
    void syncEmpleado_Success() throws Exception {
        String cedula = "1712345678";

        mockMvc.perform(post("/api/v1/sincronizacion/empleado/{cedula}", cedula)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sincronizacion/empleado",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("cedula").description("Cédula del empleado a sincronizar (formato: 10 dígitos)")
                        ),
                        responseFields(
                                fieldWithPath("").description("Respuesta de ORPHEUS al sincronizar empleado (esperado: 'TRUE')").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/sincronizacion/empleado/{cedula} - Fallo sin autenticación (401)")
    void syncEmpleado_Unauthorized() throws Exception {
        String cedula = "1712345678";

        mockMvc.perform(post("/api/v1/sincronizacion/empleado/{cedula}", cedula)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(document("sincronizacion/empleado-unauthorized",
                        requestHeaders(
                                headerWithName("Content-Type").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("cedula").description("Cédula del empleado").attributes()
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("Marca de tiempo").optional().attributes(),
                                fieldWithPath("status").description("Código 401").attributes(),
                                fieldWithPath("error").description("Unauthorized").attributes(),
                                fieldWithPath("message").description("Token JWT requerido").attributes(),
                                fieldWithPath("path").description("Ruta solicitada").attributes()
                        )
                ));
    }
}