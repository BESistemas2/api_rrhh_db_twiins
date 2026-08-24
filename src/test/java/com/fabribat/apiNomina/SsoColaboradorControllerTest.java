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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para SsoColaboradorController con Spring REST Docs.
 * Endpoints de catálogos protegidos con JWT para SSO Colaborador.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.example.com", uriPort = 443)
@ActiveProfiles("test")
class SsoColaboradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = jwtTestUtil.generateValidToken("sso_user");
    }

    // =========================================================================
    // EMPLEADOS (Colaboradores)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-colaboradores - Lista todos los colaboradores")
    void getTodosLosColaboradores_Success() throws Exception {
        mockMvc.perform(get("/api/v1/sso-colaboradores")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/colaboradores",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-colaborador/{cedula} - No encontrado (404)")
    void getColaboradorPorCedula_NotFound() throws Exception {
        String cedula = "9999999999";

        mockMvc.perform(get("/api/v1/sso-colaborador/{cedula}", cedula)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/colaborador-por-cedula-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("cedula").description("Cédula inexistente")
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error").attributes()
                        )
                ));
    }

    // =========================================================================
    // PROVINCIAS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-provincias - Lista todas las provincias")
    void getTodasLasProvincias_Success() throws Exception {
        mockMvc.perform(get("/api/v1/sso-provincias")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/provincias",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-provincia/{codigo} - No encontrada (404)")
    void getProvinciaPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/sso-provincia/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/provincia-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente")
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error").attributes()
                        )
                ));
    }

    // =========================================================================
    // CIUDADES
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-ciudades - Lista todas las ciudades")
    void getTodasLasCiudades_Success() throws Exception {
        mockMvc.perform(get("/api/v1/sso-ciudades")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/ciudades",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-ciudad/{codigo} - No encontrada (404)")
    void getCiudadPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/sso-ciudad/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/ciudad-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente")
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error").attributes()
                        )
                ));
    }

    // =========================================================================
    // CARGOS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-cargos - Lista todos los cargos")
    void getTodosLosCargos_Success() throws Exception {
        mockMvc.perform(get("/api/v1/sso-cargos")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/cargos",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-cargo/{codigo} - No encontrado (404)")
    void getCargoPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/sso-cargo/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/cargo-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente")
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error").attributes()
                        )
                ));
    }

    // =========================================================================
    // DEPARTAMENTOS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-departamentos - Lista todos los departamentos")
    void getTodosLosDepartamentos_Success() throws Exception {
        mockMvc.perform(get("/api/v1/sso-departamentos")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/departamentos",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-departamento/{codigo} - No encontrado (404)")
    void getDepartamentoPorCodigo_NotFound() throws Exception {
        String codigo = "999";

        mockMvc.perform(get("/api/v1/sso-departamento/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/departamento-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente")
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje de error").attributes()
                        )
                ));
    }

    // =========================================================================
    // TESTS DE SEGURIDAD (Sin token)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-colaboradores - Fallo sin token (403)")
    void getColaboradores_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/sso-colaboradores")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()) // Cambiado a isForbidden (403)
                .andDo(document("sso-colaborador/colaboradores-unauthorized"));
    }
}