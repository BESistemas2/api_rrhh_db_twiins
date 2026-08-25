package com.fabribat.apiNomina.entities.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sincronizacion_log")
public class SincronizacionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tipo_entidad", nullable = false, length = 30)
	private String tipoEntidad;

	@Column(name = "codigo_entidad", nullable = false, length = 50)
	private String codigoEntidad;

	@Column(name = "hash_contenido", nullable = false, length = 64)
	private String hashContenido;

	@Column(name = "fecha_ultimo_sync", nullable = false)
	private LocalDateTime fechaUltimoSync;

	@Column(name = "resultado", length = 255)
	private String resultado;

	public SincronizacionLog() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipoEntidad() {
		return tipoEntidad;
	}

	public void setTipoEntidad(String tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public String getCodigoEntidad() {
		return codigoEntidad;
	}

	public void setCodigoEntidad(String codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}

	public String getHashContenido() {
		return hashContenido;
	}

	public void setHashContenido(String hashContenido) {
		this.hashContenido = hashContenido;
	}

	public LocalDateTime getFechaUltimoSync() {
		return fechaUltimoSync;
	}

	public void setFechaUltimoSync(LocalDateTime fechaUltimoSync) {
		this.fechaUltimoSync = fechaUltimoSync;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
}