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
                        ),
                        responseFields(
                                fieldWithPath("[].cedula").description("Cédula del empleado").attributes(),
                                fieldWithPath("[].nombres").description("Nombres del empleado").attributes(),
                                fieldWithPath("[].apellidos").description("Apellidos del empleado").attributes(),
                                fieldWithPath("[].nacimiento").description("Fecha de nacimiento (yyyy-MM-dd)").attributes(),
                                fieldWithPath("[].sexo").description("Sexo: M/F").attributes(),
                                fieldWithPath("[].estado_civil").description("Código estado civil ORPHEUS (1-6)").attributes(),
                                fieldWithPath("[].instruccion").description("Nivel instrucción (7=No se conoce)").attributes(),
                                fieldWithPath("[].provincia").description("Código provincia de residencia").attributes(),
                                fieldWithPath("[].ciudad").description("Código ciudad de residencia").attributes(),
                                fieldWithPath("[].local").description("Código sucursal (001=MATRIZ)").attributes(),
                                fieldWithPath("[].departamento").description("Código departamento").attributes(),
                                fieldWithPath("[].puesto").description("Código cargo/puesto").attributes(),
                                fieldWithPath("[].ingreso").description("Fecha ingreso (yyyy-MM-dd)").attributes(),
                                fieldWithPath("[].salida").description("Fecha salida (yyyy-MM-dd)").attributes(),
                                fieldWithPath("[].direccion").description("Dirección completa formateada").attributes(),
                                fieldWithPath("[].telefono").description("Teléfono convencional").attributes(),
                                fieldWithPath("[].correo").description("Email corporativo").attributes(),
                                fieldWithPath("[].status").description("Estado empleado (A/I)").attributes(),
                                fieldWithPath("[].celular").description("Número celular").attributes(),
                                fieldWithPath("[].cedula_nueva").description("Cédula nueva (vacío)").attributes(),
                                fieldWithPath("[].entidad").description("ID entidad ORPHEUS (47)").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/sso-colaborador/{cedula} - Obtiene colaborador por cédula")
    void getColaboradorPorCedula_Success() throws Exception {
        String cedula = "1712345678";

        mockMvc.perform(get("/api/v1/sso-colaborador/{cedula}", cedula)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(cedula))
                .andDo(document("sso-colaborador/colaborador-por-cedula",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("cedula").description("Cédula del empleado a consultar (10 dígitos)")
                        ),
                        responseFields(
                                fieldWithPath("cedula").description("Cédula del empleado").attributes(),
                                fieldWithPath("nombres").description("Nombres del empleado").attributes(),
                                fieldWithPath("apellidos").description("Apellidos del empleado").attributes(),
                                fieldWithPath("nacimiento").description("Fecha de nacimiento (yyyy-MM-dd)").attributes(),
                                fieldWithPath("sexo").description("Sexo: M/F").attributes(),
                                fieldWithPath("estado_civil").description("Código estado civil ORPHEUS (1-6)").attributes(),
                                fieldWithPath("instruccion").description("Nivel instrucción (7=No se conoce)").attributes(),
                                fieldWithPath("provincia").description("Código provincia de residencia").attributes(),
                                fieldWithPath("ciudad").description("Código ciudad de residencia").attributes(),
                                fieldWithPath("local").description("Código sucursal (001=MATRIZ)").attributes(),
                                fieldWithPath("departamento").description("Código departamento").attributes(),
                                fieldWithPath("puesto").description("Código cargo/puesto").attributes(),
                                fieldWithPath("ingreso").description("Fecha ingreso (yyyy-MM-dd)").attributes(),
                                fieldWithPath("salida").description("Fecha salida (yyyy-MM-dd)").attributes(),
                                fieldWithPath("direccion").description("Dirección completa formateada").attributes(),
                                fieldWithPath("telefono").description("Teléfono convencional").attributes(),
                                fieldWithPath("correo").description("Email corporativo").attributes(),
                                fieldWithPath("status").description("Estado empleado (A/I)").attributes(),
                                fieldWithPath("celular").description("Número celular").attributes(),
                                fieldWithPath("cedula_nueva").description("Cédula nueva (vacío)").attributes(),
                                fieldWithPath("entidad").description("ID entidad ORPHEUS (47)").attributes()
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
                                parameterWithName("cedula").description("Cédula inexistente").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje: 'No existe un empleado activo con la cédula ...'").attributes()
                        )
                ));
    }

    // =========================================================================
    // PROVINCIAS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/provincias - Lista todas las provincias")
    void getTodasLasProvincias_Success() throws Exception {
        mockMvc.perform(get("/api/v1/provincias")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/provincias",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("[].codigo").description("Código de provincia").attributes(),
                                fieldWithPath("[].nombre").description("Nombre de provincia").attributes(),
                                fieldWithPath("[].estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("[].codigoSri").description("Código SRI provincia").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/provincia/{codigo} - Obtiene provincia por código")
    void getProvinciaPorCodigo_Success() throws Exception {
        Long codigo = 17L;

        mockMvc.perform(get("/api/v1/provincia/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sso-colaborador/provincia-por-codigo",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código numérico de provincia").attributes()
                        ),
                        responseFields(
                                fieldWithPath("codigo").description("Código de provincia").attributes(),
                                fieldWithPath("nombre").description("Nombre de provincia").attributes(),
                                fieldWithPath("estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("codigoSri").description("Código SRI provincia").attributes(),
                                fieldWithPath("codigoPais").description("Código país").attributes(),
                                fieldWithPath("codigoArea").description("Código área").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/provincia/{codigo} - No encontrada (404)")
    void getProvinciaPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/provincia/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/provincia-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje: 'Provincia no encontrada con código ...'").attributes()
                        )
                ));
    }

    // =========================================================================
    // CIUDADES
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/ciudades - Lista todas las ciudades")
    void getTodasLasCiudades_Success() throws Exception {
        mockMvc.perform(get("/api/v1/ciudades")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/ciudades",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("[].codigo").description("Código de ciudad").attributes(),
                                fieldWithPath("[].nombre").description("Nombre de ciudad").attributes(),
                                fieldWithPath("[].estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("[].codigoProvincia").description("Código provincia padre").attributes(),
                                fieldWithPath("[].codigoCanton").description("Código cantón").attributes(),
                                fieldWithPath("[].codigoRegion").description("Código región").attributes(),
                                fieldWithPath("[].codigoSri").description("Código SRI ciudad").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/ciudad/{codigo} - Obtiene ciudad por código")
    void getCiudadPorCodigo_Success() throws Exception {
        Long codigo = 1L;

        mockMvc.perform(get("/api/v1/ciudad/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sso-colaborador/ciudad-por-codigo",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código numérico de ciudad").attributes()
                        ),
                        responseFields(
                                fieldWithPath("codigo").description("Código de ciudad").attributes(),
                                fieldWithPath("nombre").description("Nombre de ciudad").attributes(),
                                fieldWithPath("estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("codigoProvincia").description("Código provincia").attributes(),
                                fieldWithPath("codigoCanton").description("Código cantón").attributes(),
                                fieldWithPath("codigoRegion").description("Código región").attributes(),
                                fieldWithPath("codigoSri").description("Código SRI ciudad").attributes(),
                                fieldWithPath("ideCiudad").description("IDE ciudad").attributes(),
                                fieldWithPath("traCiudad").description("Tránsito ciudad").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/ciudad/{codigo} - No encontrada (404)")
    void getCiudadPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/ciudad/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/ciudad-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje: 'Ciudad no encontrada con código ...'").attributes()
                        )
                ));
    }

    // =========================================================================
    // CARGOS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/cargos - Lista todos los cargos")
    void getTodosLosCargos_Success() throws Exception {
        mockMvc.perform(get("/api/v1/cargos")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/cargos",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("[].codigo").description("Código de cargo").attributes(),
                                fieldWithPath("[].nombre").description("Nombre de cargo/puesto").attributes(),
                                fieldWithPath("[].estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("[].codigoDepartamento").description("Código departamento asociado").attributes(),
                                fieldWithPath("[].codigoNivel").description("Código nivel").attributes(),
                                fieldWithPath("[].tipo").description("Tipo cargo").attributes(),
                                fieldWithPath("[].valor").description("Valor/salario referencial").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/cargo/{codigo} - Obtiene cargo por código")
    void getCargoPorCodigo_Success() throws Exception {
        Long codigo = 1L;

        mockMvc.perform(get("/api/v1/cargo/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sso-colaborador/cargo-por-codigo",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código numérico de cargo").attributes()
                        ),
                        responseFields(
                                fieldWithPath("codigo").description("Código de cargo").attributes(),
                                fieldWithPath("nombre").description("Nombre de cargo").attributes(),
                                fieldWithPath("estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("codigoDepartamento").description("Código departamento").attributes(),
                                fieldWithPath("codigoNivel").description("Código nivel").attributes(),
                                fieldWithPath("codigoRiesgo").description("Código riesgo").attributes(),
                                fieldWithPath("codigoRol").description("Código rol").attributes(),
                                fieldWithPath("codigoSectorial").description("Código sectorial").attributes(),
                                fieldWithPath("codigoEmprcargo").description("Código empresa cargo").attributes(),
                                fieldWithPath("conCargo").description("Contrato cargo").attributes(),
                                fieldWithPath("criCargo").description("Criterio cargo").attributes(),
                                fieldWithPath("insCargo").description("Instrucción cargo").attributes(),
                                fieldWithPath("tipo").description("Tipo cargo").attributes(),
                                fieldWithPath("tipoEmba").description("Tipo embarque cargo").attributes(),
                                fieldWithPath("valor").description("Valor cargo").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/cargo/{codigo} - No encontrado (404)")
    void getCargoPorCodigo_NotFound() throws Exception {
        Long codigo = 999L;

        mockMvc.perform(get("/api/v1/cargo/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/cargo-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje: 'Cargo no encontrado con código ...'").attributes()
                        )
                ));
    }

    // =========================================================================
    // DEPARTAMENTOS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/departamentos - Lista todos los departamentos")
    void getTodosLosDepartamentos_Success() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos")
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sso-colaborador/departamentos",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("[].codigo").description("Código de departamento").attributes(),
                                fieldWithPath("[].nombre").description("Nombre de departamento").attributes(),
                                fieldWithPath("[].estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("[].descripcion").description("Descripción").attributes(),
                                fieldWithPath("[].codigoEmpresa").description("Código empresa").attributes(),
                                fieldWithPath("[].codigoArea").description("Código área").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/departamento/{codigo} - Obtiene departamento por código")
    void getDepartamentoPorCodigo_Success() throws Exception {
        String codigo = "001";

        mockMvc.perform(get("/api/v1/departamento/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("sso-colaborador/departamento-por-codigo",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>"),
                                headerWithName("Accept").description("application/json")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código de departamento (String)").attributes()
                        ),
                        responseFields(
                                fieldWithPath("codigo").description("Código de departamento").attributes(),
                                fieldWithPath("nombre").description("Nombre de departamento").attributes(),
                                fieldWithPath("estado").description("Estado (A/I)").attributes(),
                                fieldWithPath("descripcion").description("Descripción").attributes(),
                                fieldWithPath("codigoEmpresa").description("Código empresa").attributes(),
                                fieldWithPath("codigoArea").description("Código área").attributes(),
                                fieldWithPath("ideDepartamento").description("IDE departamento").attributes(),
                                fieldWithPath("objEspedepartamento").description("Objetivo específico").attributes(),
                                fieldWithPath("objEstrdepartamento").description("Objetivo estratégico").attributes(),
                                fieldWithPath("orgDepartamento").description("Organización").attributes(),
                                fieldWithPath("priDepartamento").description("Prioridad").attributes(),
                                fieldWithPath("resDepartamento").description("Responsable").attributes(),
                                fieldWithPath("tipDepartamento").description("Tipo departamento").attributes(),
                                fieldWithPath("usrGerentecost").description("Gerente costos").attributes(),
                                fieldWithPath("usrGerentesier").description("Gerente sistemas").attributes()
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/departamento/{codigo} - No encontrado (404)")
    void getDepartamentoPorCodigo_NotFound() throws Exception {
        String codigo = "999";

        mockMvc.perform(get("/api/v1/departamento/{codigo}", codigo)
                        .header("Authorization", "Bearer " + validToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andDo(document("sso-colaborador/departamento-por-codigo-notfound",
                        requestHeaders(
                                headerWithName("Authorization").description("Token JWT: Bearer <token>")
                        ),
                        pathParameters(
                                parameterWithName("codigo").description("Código inexistente").attributes()
                        ),
                        responseFields(
                                fieldWithPath("error").description("Mensaje: 'Departamento no encontrado con código ...'").attributes()
                        )
                ));
    }

    // =========================================================================
    // TESTS DE SEGURIDAD (Sin token)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sso-colaboradores - Fallo sin token (401)")
    void getColaboradores_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/sso-colaboradores")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(document("sso-colaborador/colaboradores-unauthorized",
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