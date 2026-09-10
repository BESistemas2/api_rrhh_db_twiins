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
import com.fabribat.apiNomina.entities.rrhh.RefCanton;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.entities.security.RefCargoAlt;
import com.fabribat.apiNomina.entities.security.RefCantonAlt;
import com.fabribat.apiNomina.entities.security.RefDepartamentoAlt;
import com.fabribat.apiNomina.entities.security.RefProvinciaAlt;
import com.fabribat.apiNomina.repositories.rrhh.BkpUsuarioRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCiudadRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCantonRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefProvinciaRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;
import com.fabribat.apiNomina.repositories.security.RefCargoRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.RefCantonRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.RefDepartamentoRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.RefProvinciaRepositoryAlt;

@Service
public class SincronizacionServiceAlt {
	
	private static final Logger log = LoggerFactory.getLogger(SincronizacionServiceAlt.class);

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
	private RefCantonRepository cantonRepo;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private BkpUsuarioRepository bkpRepo;
	
	@Autowired
	private RefDepartamentoRepositoryAlt departamentoRepoAlt;

	@Autowired
	private RefCargoRepositoryAlt cargoRepoAlt;

	@Autowired
	private RefProvinciaRepositoryAlt provinciaRepoAlt;

	@Autowired
	private RefCantonRepositoryAlt cantonRepoAlt;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;
	
	@Autowired
	private OrpheusSoapClient orpheusSoapClient;
	
	@org.springframework.beans.factory.annotation.Value("${sync.automatica.alt.habilitada:true}")
	private boolean automatizacionHabilitada;

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

	    // Asegura que no sobrepase los 250 caracteres si el XML de respuesta es muy largo
	    if (resultado != null && resultado.length() > 250) {
	        resultado = resultado.substring(0, 245) + "...";
	    }

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
	public String sincronizarDepartamentoAlt(String codDepartamento) {
		return sincronizarDepartamentoAlt(codDepartamento, true);
	}

	public String sincronizarDepartamentoAlt(String codDepartamento, boolean forzar) {
		Optional<RefDepartamentoAlt> deptoOpt = departamentoRepoAlt.findById(Short.parseShort(codDepartamento));

		if (deptoOpt.isEmpty()) {
			return "ERROR: Departamento no encontrado en BD Proveedor con código " + codDepartamento;
		}

		RefDepartamentoAlt depto = deptoOpt.get();
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

	public Map<String, Object> sincronizarTodosLosDepartamentosAlt(boolean soloModificados) throws InterruptedException {
		List<RefDepartamentoAlt> deptos = departamentoRepoAlt.findByEstDepartamento("A");
		int total = deptos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefDepartamentoAlt d : deptos) {
			Thread.sleep(600);
			String res = sincronizarDepartamentoAlt(String.valueOf(d.getCodDepartamento()), !soloModificados);
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
	public String sincronizarCargoAlt(String codCargo) {
		return sincronizarCargoAlt(codCargo, true);
	}

	public String sincronizarCargoAlt(String codCargo, boolean forzar) {
		Optional<RefCargoAlt> cargoOpt = cargoRepoAlt.findById(Short.parseShort(codCargo));

		if (cargoOpt.isEmpty()) {
			return "ERROR: Cargo no encontrado en BD Proveedor con código " + codCargo;
		}

		RefCargoAlt cargo = cargoOpt.get();
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

	public Map<String, Object> sincronizarTodosLosCargosAlt(boolean soloModificados) throws InterruptedException {
		List<RefCargoAlt> cargos = cargoRepoAlt.findByEstCargo("A");
		int total = cargos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefCargoAlt c : cargos) {
			Thread.sleep(600);
			String res = sincronizarCargoAlt(String.valueOf(c.getCodCargo()), !soloModificados);
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
		
		if (respuesta != null && respuesta.contains("TRUE")) {
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

	public Map<String, Object> sincronizarTodosLosEmpleados(boolean soloModificados) throws InterruptedException {
		List<RefUsuario> activos = usuarioRepo.findByEstUsuario("A");
		int total = activos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefUsuario u : activos) {
			Thread.sleep(600);
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

	public Map<String, Object> sincronizarTodoMasivo(boolean soloModificados){
		sincronizarSucursalPorDefecto();
		Map<String, Object> deptos;
		Map<String, Object> cargos;
		Map<String, Object> empleados;
		Map<String, Object> resumenGeneral = new HashMap<>();
		String matriz;
		try {
			matriz = sincronizarSucursalPorDefecto();
			deptos = sincronizarTodosLosDepartamentosAlt(soloModificados);
			cargos = sincronizarTodosLosCargosAlt(soloModificados);
			empleados = sincronizarTodosLosEmpleados(soloModificados);
			
			resumenGeneral.put("matriz", matriz);
			resumenGeneral.put("departamentos", deptos);
			resumenGeneral.put("cargos", cargos);
			resumenGeneral.put("empleados", empleados);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			payload.put("entidad", "114");
		}

		payload.put("cedula", usuario.getCedUsuario());
		payload.put("nombres", usuario.getNomUsuario());
		payload.put("apellidos", usuario.getApeUsuario());
		payload.put("nacimiento", bkp.getFechaNacimiento() != null ? bkp.getFechaNacimiento().format(dtf) : "");
		payload.put("sexo", usuario.getGenUsuario() != null ? usuario.getGenUsuario() : "M");
		payload.put("estado_civil", traducirEstadoCivil(bkp.getEstadoCivil()));
		payload.put("instruccion", "7");
		if(bkp.getCodProvinciaVive()== null || "-1".equals(bkp.getCodProvinciaVive().toString())){
			payload.put("provincia", "17");
		}else {
			payload.put("provincia", bkp.getCodProvinciaVive().toString());
		}
		if(bkp.getCodCiudadVive()== null || "-1".equals(bkp.getCodCiudadVive().toString())){
			payload.put("ciudad", "17");
		}else {
			payload.put("ciudad", bkp.getCodCiudadVive().toString());
		}
		// payload.put("provincia", bkp.getCodProvinciaVive() != null ? bkp.getCodProvinciaVive().toString() : "17");
		// payload.put("ciudad", bkp.getCodCiudadVive() != null ? bkp.getCodCiudadVive().toString() : "1");
		payload.put("local", "001");
		
		if(usuario.getCodDepartamento()== null || "-1".equals(usuario.getCodDepartamento().toString())){
			payload.put("departamento", "1000");
		}else {
			payload.put("departamento", usuario.getCodDepartamento().toString());
		}
		if(usuario.getCodCargentiexte()== null || "-1".equals(usuario.getCodCargentiexte().toString())){
			payload.put("puesto", "1000");
		}else {
			payload.put("puesto", usuario.getCodCargentiexte().toString());
		}		
		//payload.put("departamento", usuario.getCodDepartamento() != null ? usuario.getCodDepartamento().toString() : "");
		//payload.put("puesto", String.valueOf(usuario.getCodCargentiexte()));
		payload.put("ingreso", bkp.getFechaIngreso() != null ? bkp.getFechaIngreso().format(dtf) : "");
		payload.put("salida", bkp.getFechaSalida() != null ? bkp.getFechaSalida().format(dtf) : "");

		StringBuilder direccionCompleta = new StringBuilder();
		if (bkp.getDireccionPrincipal() != null) direccionCompleta.append(bkp.getDireccionPrincipal());
		if (bkp.getDireccionNumero() != null) direccionCompleta.append(" ").append(bkp.getDireccionNumero());
		if (bkp.getDireccionSecundaria() != null) direccionCompleta.append(" Y ").append(bkp.getDireccionSecundaria());
		if (bkp.getDireccionBarrio() != null) direccionCompleta.append(" - ").append(bkp.getDireccionBarrio());
		if (bkp.getDireccionReferencia() != null) direccionCompleta.append(" - REF: ").append(bkp.getDireccionReferencia());


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
	
	public List<Map<String, Object>> obtenerTodosLosCantones() {
		List<RefCanton> cantones = cantonRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();
		
		for (RefCanton c : cantones) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCanton());
			item.put("nombre", c.getNomCanton());
			item.put("estado", c.getEstCanton());
			item.put("codigoProvincia", c.getCodProvincia());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerCantonPorCodigo(Short codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCanton> opt = cantonRepo.findById(codigo);
		
		if (opt.isEmpty()) {
			item.put("error", "Canton no encontrado con código " + codigo);
			return item;
		}
		
		RefCanton c = opt.get();
		item.put("codigo", c.getCodCanton());
		item.put("nombre", c.getNomCanton());
		item.put("estado", c.getEstCanton());
		item.put("codigoProvincia", c.getCodProvincia());

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
	
	// =========================================================================
	// ELIMINACIÓN MASIVA VÍA SOAP
	// =========================================================================

	public Map<String, Object> eliminarDepartamentosInactivosSoap() throws InterruptedException {
	    // Busca registros en BD con estado 'I' o 'X'
	    List<RefDepartamentoAlt> inactivos = departamentoRepoAlt.findByEstDepartamentoIn(List.of("I", "X"));
	    
	    int total = inactivos.size();
	    int procesados = 0;
	    int errores = 0;

	    for (RefDepartamentoAlt d : inactivos) {
	        Thread.sleep(50);
	        String codigoStr = String.valueOf(d.getCodDepartamento());
	        
	        // Petición SOAP enviando entidad 47[cite: 2]
	        String res = orpheusSoapClient.eliminaDepartamento(114, codigoStr);
	        
	        if ("1".equals(res) || (res != null && res.contains(">1<"))) {
	            procesados++;
	            registrarSincronizacion("DEPARTAMENTOe", codigoStr, "ELIMINADO", res);
	        } else {
	            errores++;
	            registrarSincronizacion("DEPARTAMENTOe E", codigoStr, "ERROR", res);
	        }
	    }

	    Map<String, Object> resumen = new HashMap<>();
	    resumen.put("total_inactivos_db", total);
	    resumen.put("eliminados_exitosos", procesados);
	    resumen.put("errores_o_en_uso", errores); // ORPHEUS rechaza si el depto está asignado a un empleado[cite: 2]
	    return resumen;
	}

	public Map<String, Object> eliminarCargosInactivosSoap() throws InterruptedException {
	    // Busca registros en BD con estado 'I' o 'X'
	    List<RefCargoAlt> inactivos = cargoRepoAlt.findByEstCargoIn(List.of("I", "X"));
	    
	    int total = inactivos.size();
	    int procesados = 0;
	    int errores = 0;

	    for (RefCargoAlt c : inactivos) {
	        Thread.sleep(50);
	        String codigoStr = String.valueOf(c.getCodCargo());
	        
	        // Petición SOAP enviando entidad 47 (ejecuta eliminaPuesto)[cite: 2]
	        String res = orpheusSoapClient.eliminaPuesto(114, codigoStr);
	        
	        if ("1".equals(res) || (res != null && res.contains(">1<"))) {
	            procesados++;
	            registrarSincronizacion("CARGOe", codigoStr, "ELIMINADO", res);
	        } else {
	            errores++;
	            registrarSincronizacion("CARGOe E", codigoStr, "ERROR", res);
	        }
	    }

	    Map<String, Object> resumen = new HashMap<>();
	    resumen.put("total_inactivos_db", total);
	    resumen.put("eliminados_exitosos", procesados);
	    resumen.put("errores_o_en_uso", errores);
	    return resumen;
	}
	
	// ====================================================================
	// SINCRONIZACIÓN AUTOMATICA
	// ====================================================================
	
		// ====================================================================
		// PUENTE: AUTO-LLENADO DE TABLAS ALT (MIDDLEWARE)
		// ====================================================================
		public void refrescarDepartamentosAlt() {
			log.info("Sincronizando tabla Original de Departamentos hacia tabla Alt...");
			List<RefDepartamento> originales = departamentoRepo.findAll();
			
			for (RefDepartamento orig : originales) {
				Optional<RefDepartamentoAlt> altOpt = departamentoRepoAlt.findById(orig.getCodDepartamento());
				
				if (altOpt.isEmpty()) {
					// 1. REGLA: Es NUEVO. Lo insertamos en la tabla Alt.
					RefDepartamentoAlt nuevoAlt = new RefDepartamentoAlt();
					nuevoAlt.setCodDepartamento(orig.getCodDepartamento());
					nuevoAlt.setNomDepartamento(orig.getNomDepartamento());
					nuevoAlt.setEstDepartamento(orig.getEstDepartamento());
					// Nota: Si tu entidad RefDepartamentoAlt exige más campos NOT NULL (ej. descripcion), agrégalos aquí.
					departamentoRepoAlt.save(nuevoAlt);
					log.info("Nuevo departamento clonado en Alt: {}", orig.getCodDepartamento());
				} else {
					// Ya existe. Revisamos si hay que actualizar.
					RefDepartamentoAlt alt = altOpt.get();
					boolean cambiado = false;
					
					// 2. REGLA DE ORO: Si en el original se inactivó, obligatoriamente lo inactivamos en Alt.
					if (("I".equals(orig.getEstDepartamento()) || "X".equals(orig.getEstDepartamento())) 
							&& "A".equals(alt.getEstDepartamento())) {
						alt.setEstDepartamento(orig.getEstDepartamento());
						cambiado = true;
						log.info("Departamento {} inactivado desde la BD Original", orig.getCodDepartamento());
					}
					
					// 3. REGLA: Si cambiaron el nombre en el sistema original, lo actualizamos.
					if (orig.getNomDepartamento() != null && !orig.getNomDepartamento().equals(alt.getNomDepartamento())) {
						alt.setNomDepartamento(orig.getNomDepartamento());
						cambiado = true;
					}
					
					// Si hubo cambios válidos, guardamos. (Si en Alt estaba 'I' y en orig 'A', NO entra aquí).
					if (cambiado) {
						departamentoRepoAlt.save(alt);
					}
				}
			}
		}

		public void refrescarCargosAlt() {
			log.info("Sincronizando tabla Original de Cargos hacia tabla Alt...");
			List<RefCargo> originales = cargoRepo.findAll();
			
			for (RefCargo orig : originales) {
				Optional<RefCargoAlt> altOpt = cargoRepoAlt.findById(orig.getCodCargo());
				
				if (altOpt.isEmpty()) {
					// 1. REGLA: Es NUEVO. Lo insertamos en la tabla Alt.
					RefCargoAlt nuevoAlt = new RefCargoAlt();
					nuevoAlt.setCodCargo(orig.getCodCargo());
					nuevoAlt.setNomCargo(orig.getNomCargo());
					nuevoAlt.setEstCargo(orig.getEstCargo());
					nuevoAlt.setCodDepartamento(orig.getCodDepartamento());
					// Nota: Si tu entidad exige más campos NOT NULL, agrégalos aquí.
					cargoRepoAlt.save(nuevoAlt);
					log.info("Nuevo cargo clonado en Alt: {}", orig.getCodCargo());
				} else {
					// Ya existe.
					RefCargoAlt alt = altOpt.get();
					boolean cambiado = false;
					
					// 2. REGLA DE ORO: Si en el original se inactivó, obligatoriamente inactivamos en Alt.
					if (("I".equals(orig.getEstCargo()) || "X".equals(orig.getEstCargo())) 
							&& "A".equals(alt.getEstCargo())) {
						alt.setEstCargo(orig.getEstCargo());
						cambiado = true;
						log.info("Cargo {} inactivado desde la BD Original", orig.getCodCargo());
					}
					
					// 3. REGLA: Si cambiaron el nombre o el departamento al que pertenece, lo actualizamos.
					if (orig.getNomCargo() != null && !orig.getNomCargo().equals(alt.getNomCargo())) {
						alt.setNomCargo(orig.getNomCargo());
						cambiado = true;
					}
					if (orig.getCodDepartamento() != null && !orig.getCodDepartamento().equals(alt.getCodDepartamento())) {
						alt.setCodDepartamento(orig.getCodDepartamento());
						cambiado = true;
					}

					if (cambiado) {
						cargoRepoAlt.save(alt);
					}
				}
			}
		}
		
		// ====================================================================
		// SINCRONIZACIÓN AUTOMATICA CRON JOB
		// ====================================================================
	
		// Ejecuta cada 5 minutos (300,000 ms).
		@org.springframework.scheduling.annotation.Scheduled(fixedDelay = 300000)
		public void orquestadorSincronizacionAutomatica() {
		    // 🛑 El Kill Switch: Si está apagado, nos salimos inmediatamente
		    if (!automatizacionHabilitada) {
		        log.info("⏳ Sincronización automática en pausa por configuración.");
		        return;
		    }
			log.info("--- INICIANDO CICLO DE SINCRONIZACIÓN AUTOMÁTICA ---");

			try {
				// ====================================================================
				// 1. AUTO-LLENADO DEL MIDDLEWARE (Original -> Alt)
				// ====================================================================
				log.info("Iniciando puentes de datos Original a Alt...");
				refrescarDepartamentosAlt();
				refrescarCargosAlt();

				// ====================================================================
				// 2. SINCRONIZACIÓN DE CATÁLOGOS A ORPHEUS (Desde Alt)
				// ====================================================================
				log.info("Verificando cambios de Alt hacia Orpheus en Departamentos...");
				sincronizarTodosLosDepartamentosAlt(true);

				log.info("Verificando cambios de Alt hacia Orpheus en Cargos...");
				sincronizarTodosLosCargosAlt(true);

				// ====================================================================
				// 3. SINCRONIZACIÓN DE EMPLEADOS (Hijos)
				// ====================================================================
				log.info("Verificando novedades de Empleados en BkpUsuario...");
				SincronizacionLog tracker = syncLogRepo.findByTipoEntidadAndCodigoEntidad("TRACKER", "BKP_USUARIO")
						.orElseGet(() -> {
							SincronizacionLog nuevo = new SincronizacionLog();
							nuevo.setTipoEntidad("TRACKER");
							nuevo.setCodigoEntidad("BKP_USUARIO");
							nuevo.setHashContenido("N/A"); 
							nuevo.setResultado("0");       
							nuevo.setFechaUltimoSync(LocalDateTime.now());
							return nuevo;
						});

				Long ultimoCodigo = Long.parseLong(tracker.getResultado());
				List<BkpUsuario> novedades = bkpRepo.findByCambCodigoGreaterThanOrderByCambCodigoAsc(ultimoCodigo);
				
				if (!novedades.isEmpty()) {
					log.info("Se encontraron {} novedades de empleados.", novedades.size());
					for (BkpUsuario novedad : novedades) {
						sincronizarEmpleado(novedad.getCedUsuario(), true);
						ultimoCodigo = novedad.getCambCodigo();
					}
					tracker.setResultado(String.valueOf(ultimoCodigo));
					tracker.setFechaUltimoSync(LocalDateTime.now());
					syncLogRepo.save(tracker);
				} else {
					log.info("No hay nuevas actualizaciones de empleados en bkp_usuario.");
				}

				log.info("--- CICLO DE SINCRONIZACIÓN FINALIZADO EXITOSAMENTE ---");

			} catch (Exception e) {
				log.error("Error crítico durante el ciclo de sincronización automática", e);
			}
		}
}