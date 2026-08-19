package com.fabribat.apiNomina.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fabribat.apiNomina.entities.rrhh.BkpUsuario;
import com.fabribat.apiNomina.entities.rrhh.RefCargo;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;
import com.fabribat.apiNomina.repositories.rrhh.RefCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.BkpUsuarioRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;

@Service
public class SincronizacionService {

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private RefDepartamentoRepository departamentoRepo;

	@Autowired
	private RefCargoRepository cargoRepo;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private BkpUsuarioRepository bkpRepo;

	// =========================================================================
	// 1. SINCRONIZAR SUCURSAL (Datos Quemados - MVP)
	// =========================================================================
	public String sincronizarSucursalPorDefecto() {

		// Armamos el Payload con los datos fijos "MATRIZ"
		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", "001"); // Código fijo para la matriz
		payload.put("nombre", "MATRIZ"); // Nombre fijo
		payload.put("provincia", "17"); // Código de provincia (ej. Pichincha si aplica, o "1")
		payload.put("ciudad", "1"); // Código de ciudad
		payload.put("status", "A"); // Activo

		// Enviamos directamente a ORPHEUS
		return orpheusClient.setSucursal(payload);
	}

	// =========================================================================
	// 2. SINCRONIZAR DEPARTAMENTO (Lee del Proveedor)
	// =========================================================================
	public String sincronizarDepartamento(String codDepartamento) {
		// Buscamos en la BD de RRHH (Solo lectura)
		Optional<RefDepartamento> deptoOpt = departamentoRepo.findById(codDepartamento);

		if (deptoOpt.isEmpty()) {
			return "ERROR: Departamento no encontrado en BD Proveedor con código " + codDepartamento;
		}

		RefDepartamento depto = deptoOpt.get();

		// Payload ORPHEUS
		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", depto.getCodDepartamento());
		payload.put("nombre", depto.getNomDepartamento());

		return orpheusClient.setDepartamento(payload);
	}

	// =========================================================================
	// 3. SINCRONIZAR CARGO / PUESTO (Lee del Proveedor)
	// =========================================================================
	public String sincronizarCargo(String codCargo) {
		Optional<RefCargo> cargoOpt = cargoRepo.findById(codCargo);

		if (cargoOpt.isEmpty()) {
			return "ERROR: Cargo no encontrado en BD Proveedor con código " + codCargo;
		}

		RefCargo cargo = cargoOpt.get();

		// Payload ORPHEUS
		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", cargo.getCodCargo());
		payload.put("nombre", cargo.getNomCargo());

		// ORPHEUS permite enviar departamento en blanco si no aplica
		payload.put("departamento", cargo.getCodDepartamento() != null ? cargo.getCodDepartamento() : "");

		// Asumimos campo de estado, si no existe en tu entidad, envía "A" por defecto
		String estado = (cargo.getEstCargo() != null && cargo.getEstCargo().equals("A")) ? "A" : "I";
		payload.put("status", estado);

		// ORPHEUS requiere un código tipo. El doc dice 564 = "Puesto general"
		payload.put("tipo", "564");

		return orpheusClient.setCargo(payload);
	}

	// =========================================================================
	// 4. SINCRONIZAR EMPLEADO (Unión RefUsuario + BkpUsuario)
	// =========================================================================
	public String sincronizarEmpleado(String cedula) {

		// 1. Obtener datos base del empleado
		Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula,"A");
		if (usuarioOpt.isEmpty()) {
			return "ERROR: Empleado no encontrado con cédula " + cedula;
		}
		RefUsuario usuario = usuarioOpt.get();

		// 2. Obtener datos extendidos del LOG de Auditoría (El más reciente)
		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario()); // Si no hay log, instanciamos uno vacío para evitar
															// NullPointer

		// Formateador de fechas a String (aaaa-mm-dd)
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// 3. Armar el Payload para ORPHEUS
		Map<String, Object> payload = new HashMap<>();

		payload.put("cedula", usuario.getCedUsuario());
		payload.put("nombres", usuario.getNomUsuario());
		payload.put("apellidos", usuario.getApeUsuario());

		// Fecha de Nacimiento (Desde BKP)
		payload.put("nacimiento", bkp.getFechaNacimiento() != null ? bkp.getFechaNacimiento().format(dtf) : "");

		payload.put("sexo", usuario.getSexUsuario() != null ? usuario.getSexUsuario() : "M"); // 'M' o 'F'

		// Estado Civil (Traducción de letras de BKP a números de ORPHEUS)
		payload.put("estado_civil", traducirEstadoCivil(bkp.getEstadoCivil()));

		// Nivel de instrucción (Como no lo tenemos en la BD, mandamos 7 = "No se
		// conoce")
		payload.put("instruccion", "7");

		// TODO: Estos códigos geográficos idealmente se cruzan con
		// Provincia y Ciudad de Residencia desde BkpUsuario
        payload.put("provincia", bkp.getCodProvinciaVive() != null ? bkp.getCodProvinciaVive().toString() : "17");
        payload.put("ciudad", bkp.getCodCiudadVive() != null ? bkp.getCodCiudadVive().toString() : "1");

		// LA SUCURSAL QUEMADA ("MATRIZ" = 001)
		payload.put("local", "001");

		payload.put("departamento", usuario.getCodDepartamento() != null ? usuario.getCodDepartamento() : "");
		// Asumiendo que RefUsuario tiene codCargo, si no lo tiene, ajústalo a tu
		// entidad real
		payload.put("puesto", String.valueOf(usuario.getCodCargentiexte())); // Ajustar según cómo mapeaste el cargo en RefUsuario

		// Fechas Laborales (Desde BKP)
		payload.put("ingreso", bkp.getFechaIngreso() != null ? bkp.getFechaIngreso().format(dtf) : "");
		payload.put("salida", bkp.getFechaSalida() != null ? bkp.getFechaSalida().format(dtf) : "");

		// --- CONSTRUCCIÓN DE LA DIRECCIÓN COMPLETA ---
        StringBuilder direccionCompleta = new StringBuilder();
        
        if (bkp.getDireccionPrincipal() != null) direccionCompleta.append(bkp.getDireccionPrincipal());
        if (bkp.getDireccionNumero() != null) direccionCompleta.append(" ").append(bkp.getDireccionNumero());
        if (bkp.getDireccionSecundaria() != null) direccionCompleta.append(" Y ").append(bkp.getDireccionSecundaria());
        if (bkp.getDireccionReferencia() != null) direccionCompleta.append(" - REF: ").append(bkp.getDireccionReferencia());
        if (bkp.getDireccionBarrio() != null) direccionCompleta.append(" - ").append(bkp.getDireccionBarrio());

        payload.put("direccion", direccionCompleta.toString().trim());
		payload.put("telefono", ""); // Puedes agregar el convencional si lo mapeas
		payload.put("correo", usuario.getEmaUsuario() != null ? usuario.getEmaUsuario() : "");

		payload.put("status", usuario.getEstUsuario() != null ? usuario.getEstUsuario() : "A");
		payload.put("celular", bkp.getCelular() != null ? bkp.getCelular() : "");
		payload.put("cedula_nueva", ""); // Siempre vacío a menos que se cambie la cédula

		// 4. Enviar a ORPHEUS
		return orpheusClient.setEmpleado(payload);
	}

	/**
	 * Helper para traducir el estado civil de tu BD a los códigos de ORPHEUS.
	 * ORPHEUS: 1=No conoce, 2=Soltero, 3=Casado, 4=Viudo, 5=Divorciado, 6=Unión
	 * Libre.
	 */
	private String traducirEstadoCivil(String codCivilBD) {
		if (codCivilBD == null)
			return "1"; // No se conoce

		return switch (codCivilBD.toUpperCase()) {
		case "S" -> "2"; // Soltero
		case "C" -> "3"; // Casado
		case "V" -> "4"; // Viudo
		case "D" -> "5"; // Divorciado
		case "U" -> "6"; // Unión Libre
		default -> "1"; // No se conoce
		};
	}

	// =========================================================================
	// MÉTODOS DE PRUEBA (SOLO GENERAN EL PAYLOAD, NO ENVÍAN A ORPHEUS)
	// =========================================================================

	/**
	 * Arma el JSON (Payload) de un empleado específico
	 */
	public Map<String, Object> obtenerPayloadEmpleado(String cedula) {
        Map<String, Object> payload = new HashMap<>();

        // Buscamos explícitamente el registro con estado 'A'
        Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula, "A");
        
        if (usuarioOpt.isEmpty()) {
            payload.put("error", "No existe un empleado activo con la cédula " + cedula);
            return payload;
        }
        
        RefUsuario usuario = usuarioOpt.get();

		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario());

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// Inyectamos la entidad solo para la visualización de la prueba
		payload.put("entidad", "47");

		payload.put("cedula", usuario.getCedUsuario());
		payload.put("nombres", usuario.getNomUsuario());
		payload.put("apellidos", usuario.getApeUsuario());
		payload.put("nacimiento", bkp.getFechaNacimiento() != null ? bkp.getFechaNacimiento().format(dtf) : "");
		payload.put("sexo", usuario.getSexUsuario() != null ? usuario.getSexUsuario() : "M");
		payload.put("estado_civil", traducirEstadoCivil(bkp.getEstadoCivil()));
		payload.put("instruccion", "7");
		// Provincia y Ciudad de Residencia desde BkpUsuario
        payload.put("provincia", bkp.getCodProvinciaVive() != null ? bkp.getCodProvinciaVive().toString() : "17");
        payload.put("ciudad", bkp.getCodCiudadVive() != null ? bkp.getCodCiudadVive().toString() : "1");
		payload.put("local", "001");
		payload.put("departamento",
				usuario.getCodDepartamento() != null ? usuario.getCodDepartamento().toString() : "");
		payload.put("puesto", String.valueOf(usuario.getCodCargentiexte())); // Ajustar si tienes el cargo
		payload.put("ingreso", bkp.getFechaIngreso() != null ? bkp.getFechaIngreso().format(dtf) : "");
		payload.put("salida", bkp.getFechaSalida() != null ? bkp.getFechaSalida().format(dtf) : "");
		// --- CONSTRUCCIÓN DE LA DIRECCIÓN COMPLETA ---
        StringBuilder direccionCompleta = new StringBuilder();
        
        if (bkp.getDireccionPrincipal() != null) direccionCompleta.append(bkp.getDireccionPrincipal());
        if (bkp.getDireccionNumero() != null) direccionCompleta.append(" ").append(bkp.getDireccionNumero());
        if (bkp.getDireccionSecundaria() != null) direccionCompleta.append(" Y ").append(bkp.getDireccionSecundaria());
        if (bkp.getDireccionReferencia() != null) direccionCompleta.append(" - REF: ").append(bkp.getDireccionReferencia());
        if (bkp.getDireccionBarrio() != null) direccionCompleta.append(" - ").append(bkp.getDireccionBarrio());

        payload.put("direccion", direccionCompleta.toString().trim());
		payload.put("telefono", "");
		payload.put("correo", usuario.getEmaUsuario() != null ? usuario.getEmaUsuario() : "");
		payload.put("status", usuario.getEstUsuario() != null ? usuario.getEstUsuario() : "A");
		payload.put("celular", bkp.getCelular() != null ? bkp.getCelular() : "");
		payload.put("cedula_nueva", "");

		return payload;
	}

	/**
	 * Recorre TODOS los empleados y arma una lista con sus payloads
	 */
	public List<Map<String, Object>> obtenerPayloadTodosLosEmpleados() {
        // Traemos solo la lista de usuarios activos
        List<RefUsuario> activos = usuarioRepo.findByEstUsuario("A");
        List<Map<String, Object>> listaPayloads = new ArrayList<>();

        for (RefUsuario u : activos) {
            listaPayloads.add(obtenerPayloadEmpleado(u.getCedUsuario()));
        }
        return listaPayloads;
    }

}