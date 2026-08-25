package com.fabribat.apiNomina.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.fabribat.apiNomina.entities.rrhh.BkpUsuario;
import com.fabribat.apiNomina.entities.rrhh.RefCargo;
import com.fabribat.apiNomina.entities.rrhh.RefCiudad;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.BkpUsuarioRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCiudadRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefProvinciaRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SincronizacionService {
	
	private static final Logger log = LoggerFactory.getLogger(SincronizacionService.class);

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

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

	// =========================================================================
	// METODOS AUXILIARES DE CONTROL LOCAL
	// =========================================================================

	private String generarHash(Map<String, Object> payload) {
		try {
			return DigestUtils.md5DigestAsHex(payload.toString().getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			return String.valueOf(payload.hashCode());
		}
	}

	private boolean esRegistroModificado(String tipoEntidad, String codigo, String nuevoHash) {
		Optional<SincronizacionLog> logOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad(tipoEntidad, codigo);
		if (logOpt.isEmpty()) {
			return true;
		}
		return !nuevoHash.equals(logOpt.get().getHashContenido());
	}

	private void registrarSincronizacion(String tipoEntidad, String codigo, String hash, String resultado) {
		Optional<SincronizacionLog> logOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad(tipoEntidad, codigo);
		SincronizacionLog logEntity = logOpt.orElse(new SincronizacionLog());
		logEntity.setTipoEntidad(tipoEntidad);
		logEntity.setCodigoEntidad(codigo);
		logEntity.setHashContenido(hash);
		logEntity.setFechaUltimoSync(LocalDateTime.now());
		logEntity.setResultado(resultado);
		syncLogRepo.save(logEntity);
	}

	// =========================================================================
	// 1. SINCRONIZAR SUCURSAL
	// =========================================================================
	public String sincronizarSucursalPorDefecto() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", "001");
		payload.put("nombre", "MATRIZ");
		payload.put("provincia", "17");
		payload.put("ciudad", "1");
		payload.put("status", "A");

		String hash = generarHash(payload);
		String respuesta = orpheusClient.setSucursal(payload);
		registrarSincronizacion("SUCURSAL", "001", hash, respuesta);
		return respuesta;
	}

	// =========================================================================
	// 2. SINCRONIZAR DEPARTAMENTO
	// =========================================================================
	public String sincronizarDepartamento(String codDepartamento) {
		return sincronizarDepartamento(codDepartamento, true);
	}

	public String sincronizarDepartamento(String codDepartamento, boolean forzar) {
		Optional<RefDepartamento> deptoOpt = departamentoRepo.findById(Short.parseShort(codDepartamento));

		if (deptoOpt.isEmpty()) {
			return "ERROR: Departamento no encontrado en BD Proveedor con código " + codDepartamento;
		}

		RefDepartamento depto = deptoOpt.get();
		String codigoStr = String.valueOf(depto.getCodDepartamento());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", depto.getNomDepartamento());

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("DEPARTAMENTO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setDepartamento(payload);
		registrarSincronizacion("DEPARTAMENTO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosDepartamentos(boolean soloModificados) {
		List<RefDepartamento> deptos = departamentoRepo.findAll();
		int total = deptos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefDepartamento d : deptos) {
			String res = sincronizarDepartamento(String.valueOf(d.getCodDepartamento()), !soloModificados);
			if (res.startsWith("SKIPPED")) {
				omitidos++;
			} else if ("TRUE".equalsIgnoreCase(res != null ? res.trim() : "")) {
				procesados++;
			} else {
				errores++;
			}
		}

		Map<String, Object> resumen = new HashMap<>();
		resumen.put("total", total);
		resumen.put("procesados", procesados);
		resumen.put("omitidos", omitidos);
		resumen.put("errores", errores);
		return resumen;
	}

	// =========================================================================
	// 3. SINCRONIZAR CARGO
	// =========================================================================
	public String sincronizarCargo(String codCargo) {
		return sincronizarCargo(codCargo, true);
	}

	public String sincronizarCargo(String codCargo, boolean forzar) {
		Optional<RefCargo> cargoOpt = cargoRepo.findById(Short.parseShort(codCargo));

		if (cargoOpt.isEmpty()) {
			return "ERROR: Cargo no encontrado en BD Proveedor con código " + codCargo;
		}

		RefCargo cargo = cargoOpt.get();
		String codigoStr = String.valueOf(cargo.getCodCargo());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", cargo.getNomCargo());
		payload.put("departamento", cargo.getCodDepartamento() != null ? String.valueOf(cargo.getCodDepartamento()) : "");

		String estado = (cargo.getEstCargo() != null && cargo.getEstCargo().equals("A")) ? "A" : "I";
		payload.put("status", estado);
		payload.put("tipo", "564");

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("CARGO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setCargo(payload);
		registrarSincronizacion("CARGO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosCargos(boolean soloModificados) {
		List<RefCargo> cargos = cargoRepo.findAll();
		int total = cargos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefCargo c : cargos) {
			String res = sincronizarCargo(String.valueOf(c.getCodCargo()), !soloModificados);
			if (res.startsWith("SKIPPED")) {
				omitidos++;
			} else if ("TRUE".equalsIgnoreCase(res != null ? res.trim() : "")) {
				procesados++;
			} else {
				errores++;
			}
		}

		Map<String, Object> resumen = new HashMap<>();
		resumen.put("total", total);
		resumen.put("procesados", procesados);
		resumen.put("omitidos", omitidos);
		resumen.put("errores", errores);
		return resumen;
	}

	// =========================================================================
	// 4. SINCRONIZAR EMPLEADO
	// =========================================================================
	public String sincronizarEmpleado(String cedula) {
		return sincronizarEmpleado(cedula, true);
	}

	public String sincronizarEmpleado(String cedula, boolean forzar) {
		Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula, "A");
		if (usuarioOpt.isEmpty()) {
			return "ERROR: Empleado no encontrado con cédula " + cedula;
		}
		RefUsuario usuario = usuarioOpt.get();

		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario());

		Map<String, Object> payload = buildEmpleadoPayload(usuario, bkp, false);
		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("EMPLEADO", cedula, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setEmpleado(payload);
		
		if (respuesta != null && respuesta.trim().equalsIgnoreCase("TRUE")) {
			log.info("Empleado sincronizado exitosamente: cédula={}, nombre={} {}", 
				cedula, usuario.getNomUsuario(), usuario.getApeUsuario());
			registrarSincronizacion("EMPLEADO", cedula, hash, respuesta);
		} else {
			log.warn("Respuesta inesperada de ORPHEUS al sincronizar empleado: cédula={}, respuesta={}", 
				cedula, respuesta);
			registrarSincronizacion("EMPLEADO", cedula, hash, "ERROR: " + respuesta);
		}
		
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosEmpleados(boolean soloModificados) {
		List<RefUsuario> activos = usuarioRepo.findByEstUsuario("A");
		int total = activos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefUsuario u : activos) {
			String res = sincronizarEmpleado(u.getCedUsuario(), !soloModificados);
			if (res.startsWith("SKIPPED")) {
				omitidos++;
			} else if ("TRUE".equalsIgnoreCase(res != null ? res.trim() : "")) {
				procesados++;
			} else {
				errores++;
			}
		}

		Map<String, Object> resumen = new HashMap<>();
		resumen.put("total", total);
		resumen.put("procesados", procesados);
		resumen.put("omitidos", omitidos);
		resumen.put("errores", errores);
		return resumen;
	}

	public Map<String, Object> sincronizarTodoMasivo(boolean soloModificados) {
		sincronizarSucursalPorDefecto();
		Map<String, Object> deptos = sincronizarTodosLosDepartamentos(soloModificados);
		Map<String, Object> cargos = sincronizarTodosLosCargos(soloModificados);
		Map<String, Object> empleados = sincronizarTodosLosEmpleados(soloModificados);

		Map<String, Object> resumenGeneral = new HashMap<>();
		resumenGeneral.put("departamentos", deptos);
		resumenGeneral.put("cargos", cargos);
		resumenGeneral.put("empleados", empleados);
		return resumenGeneral;
	}

	// =========================================================================
	// UTILERIAS & PAYLOAD HELPERS
	// =========================================================================

	private String traducirEstadoCivil(String codCivilBD) {
		if (codCivilBD == null) return "1";

		return switch (codCivilBD.toUpperCase()) {
			case "S" -> "2";
			case "C" -> "3";
			case "V" -> "4";
			case "D" -> "5";
			case "U" -> "6";
			default -> "1";
		};
	}

	private Map<String, Object> buildEmpleadoPayload(RefUsuario usuario, BkpUsuario bkp, boolean includeEntidad) {
		Map<String, Object> payload = new HashMap<>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (includeEntidad) {
			payload.put("entidad", "47");
		}

		payload.put("cedula", usuario.getCedUsuario());
		payload.put("nombres", usuario.getNomUsuario());
		payload.put("apellidos", usuario.getApeUsuario());
		payload.put("nacimiento", bkp.getFechaNacimiento() != null ? bkp.getFechaNacimiento().format(dtf) : "");
		payload.put("sexo", usuario.getSexUsuario() != null ? usuario.getSexUsuario() : "M");
		payload.put("estado_civil", traducirEstadoCivil(bkp.getEstadoCivil()));
		payload.put("instruccion", "7");
		payload.put("provincia", bkp.getCodProvinciaVive() != null ? bkp.getCodProvinciaVive().toString() : "17");
		payload.put("ciudad", bkp.getCodCiudadVive() != null ? bkp.getCodCiudadVive().toString() : "1");
		payload.put("local", "001");
		payload.put("departamento", usuario.getCodDepartamento() != null ? usuario.getCodDepartamento().toString() : "");
		payload.put("puesto", String.valueOf(usuario.getCodCargentiexte()));
		payload.put("ingreso", bkp.getFechaIngreso() != null ? bkp.getFechaIngreso().format(dtf) : "");
		payload.put("salida", bkp.getFechaSalida() != null ? bkp.getFechaSalida().format(dtf) : "");

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
	// CONSULTA DE CATALOGOS
	// =========================================================================

	public Map<String, Object> obtenerPayloadEmpleado(String cedula) {
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

	public List<Map<String, Object>> obtenerPayloadTodosLosEmpleados() {
		List<RefUsuario> activos = usuarioRepo.findByEstUsuario("A");
		List<Map<String, Object>> listaPayloads = new ArrayList<>();

		for (RefUsuario u : activos) {
			listaPayloads.add(obtenerPayloadEmpleado(u.getCedUsuario()));
		}
		return listaPayloads;
	}

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