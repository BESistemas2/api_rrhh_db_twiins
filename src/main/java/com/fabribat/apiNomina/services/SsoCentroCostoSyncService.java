package com.fabribat.apiNomina.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

import com.fabribat.apiNomina.entities.rrhh.NomiRefCentrodecosto;
import com.fabribat.apiNomina.entities.rrhh.NomiRelUsuariocentrocosto;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.NomiRefCentrodecostoRepository;
import com.fabribat.apiNomina.repositories.rrhh.NomiRelUsuariocentrocostoRepository;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SsoCentroCostoSyncService {

	private static final Logger log = LoggerFactory.getLogger(SsoCentroCostoSyncService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private NomiRefCentrodecostoRepository centrodecostoRepo;

	@Autowired
	private NomiRelUsuariocentrocostoRepository nomiRelUsuariocentrocostoRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

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

	// =========================================================================
	// SINCRONIZAR CENTRO DE COSTO
	// =========================================================================

	public String sincronizarCentrodecosto(String codCentrodecosto) {
		return sincronizarCentrodecosto(codCentrodecosto, true);
	}

	public String sincronizarCentrodecosto(String codCentrodecosto, boolean forzar) {
		Optional<NomiRefCentrodecosto> centroOpt = centrodecostoRepo.findById(Short.parseShort(codCentrodecosto));

		if (centroOpt.isEmpty()) {
			return "ERROR: Centro de Costo no encontrado en BD con código " + codCentrodecosto;
		}

		NomiRefCentrodecosto centro = centroOpt.get();
		String codigoStr = String.valueOf(centro.getCodCentrodecosto());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", centro.getNomCentrodecosto());

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("CENTRODECOSTO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setDepartamento(payload);
		registrarSincronizacion("CENTRODECOSTO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosCentrosdecosto(boolean soloModificados) {
		List<NomiRefCentrodecosto> centros = centrodecostoRepo.findByEstCentrodecosto("A");
		int total = centros.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (NomiRefCentrodecosto c : centros) {
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String res = sincronizarCentrodecosto(String.valueOf(c.getCodCentrodecosto()), !soloModificados);
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
	// BUSCAR CENTROS DE COSTO POR USUARIO (NomiRelUsuariocentrocosto)
	// =========================================================================

	public List<NomiRelUsuariocentrocosto> obtenerCentrosCostoPorUsuario(String usrUsuario) {
		return nomiRelUsuariocentrocostoRepo.findByUsrUsuario(usrUsuario);
	}

	// =========================================================================
	// CONSULTA DE CATÁLOGOS - CENTRO DE COSTO
	// =========================================================================

	public List<Map<String, Object>> obtenerTodosLosCentrosdecosto() {
		List<NomiRefCentrodecosto> centros = centrodecostoRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (NomiRefCentrodecosto c : centros) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCentrodecosto());
			item.put("nombre", c.getNomCentrodecosto());
			item.put("estado", c.getEstCentrodecosto());
			item.put("descripcion", c.getDesCentrodecosto());
			item.put("codigoEmpresa", c.getCodEmpresa());
			item.put("codigoRegion", c.getCodRegion());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerCentrodecostoPorCodigo(String codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<NomiRefCentrodecosto> opt = centrodecostoRepo.findById(Short.parseShort(codigo));

		if (opt.isEmpty()) {
			item.put("error", "Centro de costo no encontrado con código " + codigo);
			return item;
		}

		NomiRefCentrodecosto c = opt.get();
		item.put("codigo", c.getCodCentrodecosto());
		item.put("nombre", c.getNomCentrodecosto());
		item.put("estado", c.getEstCentrodecosto());
		item.put("descripcion", c.getDesCentrodecosto());
		item.put("codigoEmpresa", c.getCodEmpresa());
		item.put("codigoRegion", c.getCodRegion());
		item.put("codigoCiudad", c.getCodCiudad());
		item.put("codigoDistribucion", c.getCodDistribucion());
		item.put("ideCentrodecosto", c.getIdeCentrodecosto());
		item.put("porCentrodecosto", c.getPorCentrodecosto());
		item.put("tipCentrodecosto", c.getTipCentrodecosto());
		return item;
	}
}