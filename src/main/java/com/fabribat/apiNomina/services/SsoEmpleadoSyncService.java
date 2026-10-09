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
import com.fabribat.apiNomina.entities.rrhh.NomiRelUsuariocentrocosto;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.BkpUsuarioRepository;
import com.fabribat.apiNomina.repositories.rrhh.NomiRelUsuariocentrocostoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SsoEmpleadoSyncService {

	private static final Logger log = LoggerFactory.getLogger(SsoEmpleadoSyncService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private BkpUsuarioRepository bkpRepo;

	@Autowired
	private NomiRelUsuariocentrocostoRepository nomiRelUsuariocentrocostoRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

	@Autowired
	private SsoDepartamentoSyncService deptoSyncService;

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

		if (resultado != null && resultado.length() > 250) {
			resultado = resultado.substring(0, 245) + "...";
		}

		logEntity.setResultado(resultado);
		syncLogRepo.save(logEntity);
	}

	private String traducirEstadoCivil(String codCivilBD) {
		if (codCivilBD == null)
			return "1";

		return switch (codCivilBD.toUpperCase()) {
		case "S" -> "2";
		case "C" -> "3";
		case "V" -> "4";
		case "D" -> "5";
		case "U" -> "6";
		default -> "1";
		};
	}

	private Map<String, Object> buildEmpleadoPayload(RefUsuario usuario, BkpUsuario bkp,
			NomiRelUsuariocentrocosto nomiRelUsrCentroCosto, boolean includeEntidad) {
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
		if (bkp.getCodProvinciaVive() == null || "-1".equals(bkp.getCodProvinciaVive().toString())) {
			payload.put("provincia", "17");
		} else {
			payload.put("provincia", bkp.getCodProvinciaVive().toString());
		}
		if (bkp.getCodCiudadVive() == null || "-1".equals(bkp.getCodCiudadVive().toString())) {
			payload.put("ciudad", "17");
		} else {
			payload.put("ciudad", bkp.getCodCiudadVive().toString());
		}
		// payload.put("provincia", bkp.getCodProvinciaVive() != null ?
		// bkp.getCodProvinciaVive().toString() : "17");
		// payload.put("ciudad", bkp.getCodCiudadVive() != null ?
		// bkp.getCodCiudadVive().toString() : "1");
		payload.put("local", "001");

		// 🎯 Obtenemos el Área a partir del cod_departamento del usuario
		String codAreaVal = deptoSyncService.obtenerCodAreaDesdeDepartamento(usuario.getCodDepartamento());
		payload.put("departamento", codAreaVal); // Se envía el código de Área como departamento a Orpheus
		// if(usuario.getCodDepartamento()== null ||
		// "-1".equals(usuario.getCodDepartamento().toString())){
		// payload.put("departamento", "1000");
		// }else {
		// payload.put("departamento", usuario.getCodDepartamento().toString());
		// }
		if (usuario.getCodCargentiexte() == null || "-1".equals(usuario.getCodCargentiexte().toString())) {
			payload.put("puesto", "1000");
		} else {
			payload.put("puesto", usuario.getCodCargentiexte().toString());
		}
		// payload.put("departamento", usuario.getCodDepartamento() != null ?
		// usuario.getCodDepartamento().toString() : "");
		// payload.put("puesto", String.valueOf(usuario.getCodCargentiexte()));
		payload.put("ingreso", bkp.getFechaIngreso() != null ? bkp.getFechaIngreso().format(dtf) : "");
		payload.put("salida", bkp.getFechaSalida() != null ? bkp.getFechaSalida().format(dtf) : "");

		StringBuilder direccionCompleta = new StringBuilder();
		if (bkp.getDireccionPrincipal() != null)
			direccionCompleta.append(bkp.getDireccionPrincipal());
		if (bkp.getDireccionNumero() != null)
			direccionCompleta.append(" ").append(bkp.getDireccionNumero());
		if (bkp.getDireccionSecundaria() != null)
			direccionCompleta.append(" Y ").append(bkp.getDireccionSecundaria());
		if (bkp.getDireccionBarrio() != null)
			direccionCompleta.append(" - ").append(bkp.getDireccionBarrio());
		if (bkp.getDireccionReferencia() != null)
			direccionCompleta.append(" - REF: ").append(bkp.getDireccionReferencia());

		payload.put("direccion", direccionCompleta.toString().trim());
		payload.put("telefono", "");
		payload.put("correo", usuario.getEmaUsuario() != null ? usuario.getEmaUsuario() : "");
		payload.put("status", usuario.getEstUsuario() != null ? usuario.getEstUsuario() : "A");
		payload.put("celular", bkp.getCelular() != null ? bkp.getCelular() : "");
		payload.put("cedula_nueva", "");

		return payload;
	}

	public String sincronizarEmpleado(String cedula, boolean forzar) {
		Optional<RefUsuario> usuarioOpt = usuarioRepo.findFirstByCedUsuarioAndEstUsuario(cedula, "A");
		if (usuarioOpt.isEmpty()) {
			return "ERROR: Empleado no encontrado con cédula " + cedula;
		}
		RefUsuario usuario = usuarioOpt.get();

		Optional<BkpUsuario> bkpOpt = bkpRepo.findFirstByCedUsuarioOrderByCambFechaDesc(cedula);
		BkpUsuario bkp = bkpOpt.orElse(new BkpUsuario());

		Optional<NomiRelUsuariocentrocosto> nomiRelUsrCentroCostoOptional = nomiRelUsuariocentrocostoRepo
				.findFirstByUsrUsuario(usuario.getUsrUsuario());
		NomiRelUsuariocentrocosto nomiRelUsrCentroCosto = nomiRelUsrCentroCostoOptional
				.orElse(new NomiRelUsuariocentrocosto());

		Map<String, Object> payload = buildEmpleadoPayload(usuario, bkp, nomiRelUsrCentroCosto, false);
		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("EMPLEADO", cedula, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = null;
		int maxReintentos = 3;
		int intento = 0;
		boolean conexionExitosa = false;

		while (intento < maxReintentos && !conexionExitosa) {
			respuesta = orpheusClient.setEmpleado(payload);

			if (respuesta == null || respuesta.contains("header parser received no bytes")
					|| respuesta.contains("I/O error")) {
				intento++;
				log.warn("Fallo de red al sincronizar empleado {} (Intento {} de {}). Reintentando en 3 segundos...",
						cedula, intento, maxReintentos);

				if (intento < maxReintentos) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			} else {
				conexionExitosa = true;
			}
		}

		if (respuesta != null && respuesta.contains("TRUE")) {
			log.info("Empleado sincronizado exitosamente: cédula={}, nombre={} {}", cedula, usuario.getNomUsuario(),
					usuario.getApeUsuario());
			registrarSincronizacion("EMPLEADO", cedula, hash, respuesta);
		} else {
			log.warn("Respuesta inesperada de ORPHEUS al sincronizar empleado: cédula={}, respuesta={}", cedula,
					respuesta);
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
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
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

	public void procesarNovedadesEmpleados() {
		SincronizacionLog tracker = syncLogRepo.findByTipoEntidadAndCodigoEntidad("TRACKER", "BKP_USUARIO")
				.orElseGet(() -> crearNuevoTracker("BKP_USUARIO"));

		Long ultimoCodigo = Long.parseLong(tracker.getResultado());
		List<BkpUsuario> novedades = bkpRepo.findByCambCodigoGreaterThanOrderByCambCodigoAsc(ultimoCodigo);

		if (!novedades.isEmpty()) {
			log.info("Se encontraron {} novedades de empleados en bkp_usuario.", novedades.size());
			for (BkpUsuario novedad : novedades) {
				sincronizarEmpleado(novedad.getCedUsuario(), false);
				ultimoCodigo = novedad.getCambCodigo();
			}
			tracker.setResultado(String.valueOf(ultimoCodigo));
			tracker.setFechaUltimoSync(LocalDateTime.now());
			syncLogRepo.save(tracker);
		} else {
			log.info("No hay nuevas actualizaciones de empleados en bkp_usuario.");
		}
	}

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

		Optional<NomiRelUsuariocentrocosto> nomiRelUsrCentroCostoOptional = nomiRelUsuariocentrocostoRepo
				.findFirstByUsrUsuario(usuario.getUsrUsuario());
		NomiRelUsuariocentrocosto nomiRelUsrCentroCosto = nomiRelUsrCentroCostoOptional
				.orElse(new NomiRelUsuariocentrocosto());

		return buildEmpleadoPayload(usuario, bkp, nomiRelUsrCentroCosto, true);
	}

	public List<Map<String, Object>> obtenerPayloadTodosLosEmpleados() {
		List<RefUsuario> activos = usuarioRepo.findByEstUsuario("A");
		List<Map<String, Object>> listaPayloads = new ArrayList<>();

		for (RefUsuario u : activos) {
			try {
				listaPayloads.add(obtenerPayloadEmpleado(u.getCedUsuario()));
			} catch (Exception e) {
				log.error("❌ Error procesando empleado con Cédula {}: {}", u.getCedUsuario(), e.getMessage(), e);
			}
		}
		return listaPayloads;
	}

	private SincronizacionLog crearNuevoTracker(String codigoEntidad) {
		SincronizacionLog nuevo = new SincronizacionLog();
		nuevo.setTipoEntidad("TRACKER");
		nuevo.setCodigoEntidad(codigoEntidad);
		nuevo.setHashContenido("N/A");
		nuevo.setResultado("0");
		nuevo.setFechaUltimoSync(LocalDateTime.now());
		return nuevo;
	}
}