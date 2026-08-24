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
import com.fabribat.apiNomina.entities.rrhh.RefCiudad;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;
import com.fabribat.apiNomina.repositories.rrhh.RefCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCiudadRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefProvinciaRepository;
import com.fabribat.apiNomina.repositories.rrhh.BkpUsuarioRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;

@Service

public class SincronizacionService {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SincronizacionService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private RefDepartamentoRepository departamentoRepo;

	@Autowired
	private RefCargoRepository cargoRepo;

	@Autowired
	private RefProvinciaRepository provinciaRepo;

	@Autowired
	private RefCiudadRepository ciudadRepo;

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
		Optional<RefDepartamento> deptoOpt = departamentoRepo.findById(Short.parseShort(codDepartamento));

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
		Optional<RefCargo> cargoOpt = cargoRepo.findById(Short.parseShort(codCargo));

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
		Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula, "A");
		if (usuarioOpt.isEmpty()) {
			return "ERROR: Empleado no encontrado con cédula " + cedula;
		}
		RefUsuario usuario = usuarioOpt.get();

		// 2. Obtener datos extendidos del LOG de Auditoría (El más reciente)
		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario());

		// 3. Construir payload y enviar a ORPHEUS
		Map<String, Object> payload = buildEmpleadoPayload(usuario, bkp, false);
		String respuesta = orpheusClient.setEmpleado(payload);
		
		// 4. Log de éxito
		if (respuesta != null && respuesta.trim().equalsIgnoreCase("TRUE")) {
			log.info("Empleado sincronizado exitosamente: cédula={}, nombre={} {}", 
				cedula, usuario.getNomUsuario(), usuario.getApeUsuario());
		} else {
			log.warn("Respuesta inesperada de ORPHEUS al sincronizar empleado: cédula={}, respuesta={}", 
				cedula, respuesta);
		}
		
		return respuesta;
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

	/**
	 * Construye el payload común para un empleado (usado tanto en sincronización como en prueba).
	 * 
	 * @param usuario Entidad RefUsuario con datos base
	 * @param bkp Entidad BkpUsuario con datos extendidos (puede ser vacío)
	 * @param includeEntidad Si true, agrega campo "entidad" = "47" (para testing)
	 * @return Map con el payload completo
	 */
	private Map<String, Object> buildEmpleadoPayload(RefUsuario usuario, BkpUsuario bkp, boolean includeEntidad) {
		Map<String, Object> payload = new HashMap<>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (includeEntidad) {
			payload.put("entidad", "47");
		}

		payload.put("cedula", usuario.getCedUsuario());
		payload.put("nombres", usuario.getNomUsuario());
		payload.put("apellidos", usuario.getApeUsuario());

		// Fecha de Nacimiento (Desde BKP)
		payload.put("nacimiento", bkp.getFechaNacimiento() != null ? bkp.getFechaNacimiento().format(dtf) : "");

		payload.put("sexo", usuario.getSexUsuario() != null ? usuario.getSexUsuario() : "M");

		// Estado Civil (Traducción de letras de BKP a números de ORPHEUS)
		payload.put("estado_civil", traducirEstadoCivil(bkp.getEstadoCivil()));

		// Nivel de instrucción
		payload.put("instruccion", "7");

		// Provincia y Ciudad de Residencia desde BkpUsuario
		payload.put("provincia", bkp.getCodProvinciaVive() != null ? bkp.getCodProvinciaVive().toString() : "17");
		payload.put("ciudad", bkp.getCodCiudadVive() != null ? bkp.getCodCiudadVive().toString() : "1");

		// LA SUCURSAL QUEMADA ("MATRIZ" = 001)
		payload.put("local", "001");

		payload.put("departamento", usuario.getCodDepartamento() != null ? usuario.getCodDepartamento().toString() : "");
		payload.put("puesto", String.valueOf(usuario.getCodCargentiexte()));

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
		payload.put("telefono", "");
		payload.put("correo", usuario.getEmaUsuario() != null ? usuario.getEmaUsuario() : "");
		payload.put("status", usuario.getEstUsuario() != null ? usuario.getEstUsuario() : "A");
		payload.put("celular", bkp.getCelular() != null ? bkp.getCelular() : "");
		payload.put("cedula_nueva", "");

		return payload;
	}

	// =========================================================================
	// MÉTODOS DE PRUEBA (SOLO GENERAN EL PAYLOAD, NO ENVÍAN A ORPHEUS)
	// =========================================================================

	/**
	 * Arma el JSON (Payload) de un empleado específico
	 */
	public Map<String, Object> obtenerPayloadEmpleado(String cedula) {
		// Buscamos explícitamente el registro con estado 'A'
		Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula, "A");

		if (usuarioOpt.isEmpty()) {
			Map<String, Object> errorPayload = new HashMap<>();
			errorPayload.put("error", "No existe un empleado activo con la cédula " + cedula);
			return errorPayload;
		}

		RefUsuario usuario = usuarioOpt.get();

		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario());

		return buildEmpleadoPayload(usuario, bkp, true);
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

	// =========================================================================
	// MÉTODOS PARA CONSULTA DE CATÁLOGOS (Provincia, Ciudad, Cargo)
	// =========================================================================

	/**
	 * Obtiene todas las provincias activas
	 */
	public List<Map<String, Object>> obtenerTodasLasProvincias() {
		List<RefProvincia> provincias = provinciaRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();
		
		for (RefProvincia p : provincias) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", p.getCodProvincia());
			item.put("nombre", p.getNomProvincia());
			item.put("estado", p.getEstProvincia());
			item.put("codigoSri", p.getCodSriprovincia());
			lista.add(item);
		}
		return lista;
	}

	/**
	 * Obtiene una provincia por su código
	 */
	public Map<String, Object> obtenerProvinciaPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefProvincia> opt = provinciaRepo.findById(codigo);
		
		if (opt.isEmpty()) {
			item.put("error", "Provincia no encontrada con código " + codigo);
			return item;
		}
		
		RefProvincia p = opt.get();
		item.put("codigo", p.getCodProvincia());
		item.put("nombre", p.getNomProvincia());
		item.put("estado", p.getEstProvincia());
		item.put("codigoSri", p.getCodSriprovincia());
		item.put("codigoPais", p.getCodPais());
		item.put("codigoArea", p.getCodAreaprovincia());
		return item;
	}

	/**
	 * Obtiene todas las ciudades activas
	 */
	public List<Map<String, Object>> obtenerTodasLasCiudades() {
		List<RefCiudad> ciudades = ciudadRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();
		
		for (RefCiudad c : ciudades) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCiudad());
			item.put("nombre", c.getNomCiudad());
			item.put("estado", c.getEstCiudad());
			item.put("codigoProvincia", c.getCodProvincia());
			item.put("codigoCanton", c.getCodCanton());
			item.put("codigoRegion", c.getCodRegion());
			item.put("codigoSri", c.getCodSriciudad());
			lista.add(item);
		}
		return lista;
	}

	/**
	 * Obtiene una ciudad por su código
	 */
	public Map<String, Object> obtenerCiudadPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCiudad> opt = ciudadRepo.findById(codigo);
		
		if (opt.isEmpty()) {
			item.put("error", "Ciudad no encontrada con código " + codigo);
			return item;
		}
		
		RefCiudad c = opt.get();
		item.put("codigo", c.getCodCiudad());
		item.put("nombre", c.getNomCiudad());
		item.put("estado", c.getEstCiudad());
		item.put("codigoProvincia", c.getCodProvincia());
		item.put("codigoCanton", c.getCodCanton());
		item.put("codigoRegion", c.getCodRegion());
		item.put("codigoSri", c.getCodSriciudad());
		item.put("ideCiudad", c.getIdeCiudad());
		item.put("traCiudad", c.getTraCiudad());
		return item;
	}

	/**
	 * Obtiene todos los cargos activos
	 */
	public List<Map<String, Object>> obtenerTodosLosCargos() {
		List<RefCargo> cargos = cargoRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();
		
		for (RefCargo c : cargos) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCargo());
			item.put("nombre", c.getNomCargo());
			item.put("estado", c.getEstCargo());
			item.put("codigoDepartamento", c.getCodDepartamento());
			item.put("codigoNivel", c.getCodNivel());
			item.put("tipo", c.getTipCargo());
			item.put("valor", c.getValCargo());
			lista.add(item);
		}
		return lista;
	}

	/**
	 * Obtiene un cargo por su código
	 */
	public Map<String, Object> obtenerCargoPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCargo> opt = cargoRepo.findById(codigo.shortValue());
		
		if (opt.isEmpty()) {
			item.put("error", "Cargo no encontrado con código " + codigo);
			return item;
		}
		
		RefCargo c = opt.get();
		item.put("codigo", c.getCodCargo());
		item.put("nombre", c.getNomCargo());
		item.put("estado", c.getEstCargo());
		item.put("codigoDepartamento", c.getCodDepartamento());
		item.put("codigoNivel", c.getCodNivel());
		item.put("codigoRiesgo", c.getCodRiescargo());
		item.put("codigoRol", c.getCodRol());
		item.put("codigoSectorial", c.getCodSectorial());
		item.put("codigoEmprcargo", c.getCodEmprcargo());
		item.put("conCargo", c.getConCargo());
		item.put("criCargo", c.getCriCargo());
		item.put("insCargo", c.getInsCargo());
		item.put("tipo", c.getTipCargo());
		item.put("tipoEmba", c.getTipEmbacargo());
		item.put("valor", c.getValCargo());
		return item;
	}

	// =========================================================================
	// DEPARTAMENTOS
	// =========================================================================

	/**
	 * Obtiene todos los departamentos activos
	 */
	public List<Map<String, Object>> obtenerTodosLosDepartamentos() {
		List<RefDepartamento> departamentos = departamentoRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();
		
		for (RefDepartamento d : departamentos) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", d.getCodDepartamento());
			item.put("nombre", d.getNomDepartamento());
			item.put("estado", d.getEstDepartamento());
			item.put("descripcion", d.getDesDepartamento());
			item.put("codigoEmpresa", d.getCodEmpresa());
			item.put("codigoArea", d.getCodArea());
			lista.add(item);
		}
		return lista;
	}

	/**
	 * Obtiene un departamento por su código
	 */
	public Map<String, Object> obtenerDepartamentoPorCodigo(String codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefDepartamento> opt = departamentoRepo.findById(Short.parseShort(codigo));
		
		if (opt.isEmpty()) {
			item.put("error", "Departamento no encontrado con código " + codigo);
			return item;
		}
		
		RefDepartamento d = opt.get();
		item.put("codigo", d.getCodDepartamento());
		item.put("nombre", d.getNomDepartamento());
		item.put("estado", d.getEstDepartamento());
		item.put("descripcion", d.getDesDepartamento());
		item.put("codigoEmpresa", d.getCodEmpresa());
		item.put("codigoArea", d.getCodArea());
		item.put("ideDepartamento", d.getIdeDepartamento());
		item.put("objEspedepartamento", d.getObjEspedepartamento());
		item.put("objEstrdepartamento", d.getObjEstrdepartamento());
		item.put("orgDepartamento", d.getOrgDepartamento());
		item.put("priDepartamento", d.getPriDepartamento());
		item.put("resDepartamento", d.getResDepartamento());
		item.put("tipDepartamento", d.getTipDepartamento());
		item.put("usrGerentecost", d.getUsrGerentecost());
		item.put("usrGerentesier", d.getUsrGerentesier());
		return item;
	}

}