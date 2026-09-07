package com.fabribat.apiNomina.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrpheusSoapClient {

    private final RestTemplate restTemplate;
    private static final String ORPHEUS_SOAP_URI = "https://www.bateriasecuador.orpheus2.com.ec/sso/soap";

    public OrpheusSoapClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Método auxiliar genérico para envolver y enviar peticiones SOAP XML.
     */
    private String enviarPeticionSoap(String soapAction, String xmlBody) {
        String xmlPayload = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
               <soapenv:Header/>
               <soapenv:Body>
                  %s
               </soapenv:Body>
            </soapenv:Envelope>
            """.formatted(xmlBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.add("SOAPAction", soapAction);

        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);

        try {
            String response = restTemplate.postForObject(ORPHEUS_SOAP_URI, request, String.class);
            
            // Si la respuesta contiene <return>1</return> u equivale a éxito[cite: 2]
            if (response != null && response.contains(">1<")) {
                return "1";
            }
            return response; // Retorna el XML de error si el elemento está en uso o falla[cite: 2]
        } catch (Exception e) {
            return "ERROR_HTTP: " + e.getMessage();
        }
    }

    /**
     * Trae los datos de un empleado[cite: 2].
     */
    public String muestraEmpleado(int entidad, String cedulaActual) {
        String body = """
            <muestraEmpleado>
               <entidad>%d</entidad>
               <cedula_actual>%s</cedula_actual>
            </muestraEmpleado>
            """.formatted(entidad, cedulaActual);
        return enviarPeticionSoap("muestraEmpleado", body);
    }

    /**
     * Inserta o actualiza una provincia[cite: 2].
     */
    public String setProvincia(int entidad, int codigo, String nombre) {
        String body = """
            <setProvincia>
               <entidad>%d</entidad>
               <codigo>%d</codigo>
               <nombre>%s</nombre>
            </setProvincia>
            """.formatted(entidad, codigo, nombre);
        return enviarPeticionSoap("setProvincia", body);
    }

    /**
     * Elimina una provincia[cite: 2].
     */
    public String eliminaProvincia(int entidad, int codigo) {
        String body = """
            <eliminaProvincia>
               <entidad>%d</entidad>
               <codigo>%d</codigo>
            </eliminaProvincia>
            """.formatted(entidad, codigo);
        return enviarPeticionSoap("eliminaProvincia", body);
    }

    /**
     * Inserta o actualiza una ciudad[cite: 2].
     */
    public String setCiudad(int entidad, int codigo, String nombre, int provincia) {
        String body = """
            <setCiudad>
               <entidad>%d</entidad>
               <codigo>%d</codigo>
               <nombre>%s</nombre>
               <provincia>%d</provincia>
            </setCiudad>
            """.formatted(entidad, codigo, nombre, provincia);
        return enviarPeticionSoap("setCiudad", body);
    }

    /**
     * Elimina una ciudad[cite: 2].
     */
    public String eliminaCiudad(int entidad, int codigo) {
        String body = """
            <eliminaCiudad>
               <entidad>%d</entidad>
               <codigo>%d</codigo>
            </eliminaCiudad>
            """.formatted(entidad, codigo);
        return enviarPeticionSoap("eliminaCiudad", body);
    }

    /**
     * Inserta o actualiza una sucursal[cite: 2].
     */
    public String setSucursal(int entidad, String codigo, String nombre, int provincia, int ciudad, String status) {
        String body = """
            <setSucursal>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
               <nombre>%s</nombre>
               <provincia>%d</provincia>
               <ciudad>%d</ciudad>
               <status>%s</status>
            </setSucursal>
            """.formatted(entidad, codigo, nombre, provincia, ciudad, status);
        return enviarPeticionSoap("setSucursal", body);
    }

    /**
     * Elimina una sucursal[cite: 2].
     */
    public String eliminaSucursal(int entidad, String codigo) {
        String body = """
            <eliminaSucursal>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
            </eliminaSucursal>
            """.formatted(entidad, codigo);
        return enviarPeticionSoap("eliminaSucursal", body);
    }

    /**
     * Inserta o actualiza un departamento[cite: 2].
     */
    public String setDepartamento(int entidad, String codigo, String nombre) {
        String body = """
            <setDepartamento>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
               <nombre>%s</nombre>
            </setDepartamento>
            """.formatted(entidad, codigo, nombre);
        return enviarPeticionSoap("setDepartamento", body);
    }

    /**
     * Elimina un departamento[cite: 2].
     */
    public String eliminaDepartamento(int entidad, String codigo) {
        String body = """
            <eliminaDepartamento>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
            </eliminaDepartamento>
            """.formatted(entidad, codigo);
        return enviarPeticionSoap("eliminaDepartamento", body);
    }

    /**
     * Inserta o actualiza un puesto (cargo)[cite: 2].
     */
    public String setPuesto(int entidad, String codigo, String nombre, String departamento, String status, Integer tipo) {
        String body = """
            <setPuesto>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
               <nombre>%s</nombre>
               <departamento>%s</departamento>
               <status>%s</status>
               <tipo>%s</tipo>
            </setPuesto>
            """.formatted(entidad, codigo, nombre, 
                          departamento != null ? departamento : "", 
                          status, 
                          tipo != null ? tipo : "564"); // 564 por defecto[cite: 2]
        return enviarPeticionSoap("setPuesto", body);
    }

    /**
     * Elimina un puesto (cargo)[cite: 2].
     */
    public String eliminaPuesto(int entidad, String codigo) {
        String body = """
            <eliminaPuesto>
               <entidad>%d</entidad>
               <codigo>%s</codigo>
            </eliminaPuesto>
            """.formatted(entidad, codigo);
        return enviarPeticionSoap("eliminaPuesto", body);
    }

    /**
     * Inserta o actualiza un empleado[cite: 2].
     */
    public String setEmpleado(
            int entidad, String cedulaActual, String nombres, String apellidos, String nacimiento,
            String sexo, int estadoCivil, int instruccion, int provincia, int ciudad,
            String local, String departamento, String puesto, String ingreso, String salida,
            String direccion, String telefono, String correo, String status, String celular, String cedulaNueva) {

        String body = """
            <setEmpleado>
               <entidad>%d</entidad>
               <cedula_actual>%s</cedula_actual>
               <nombres>%s</nombres>
               <apellidos>%s</apellidos>
               <nacimiento>%s</nacimiento>
               <sexo>%s</sexo>
               <estado_civil>%d</estado_civil>
               <instruccion>%d</instruccion>
               <provincia>%d</provincia>
               <ciudad>%d</ciudad>
               <local>%s</local>
               <departamento>%s</departamento>
               <puesto>%s</puesto>
               <ingreso>%s</ingreso>
               <salida>%s</salida>
               <direccion>%s</direccion>
               <telefono>%s</telefono>
               <correo>%s</correo>
               <status>%s</status>
               <celular>%s</celular>
               <cedula_nueva>%s</cedula_nueva>
            </setEmpleado>
            """.formatted(
                entidad, cedulaActual, nombres, apellidos, nacimiento, sexo,
                estadoCivil, instruccion, provincia, ciudad, local, departamento,
                puesto, ingreso, salida, direccion, telefono, correo, status,
                celular, cedulaNueva != null ? cedulaNueva : ""
            );
        return enviarPeticionSoap("setEmpleado", body);
    }
}