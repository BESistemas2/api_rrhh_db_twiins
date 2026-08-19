package com.fabribat.apiNomina.entities.rrhh;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bkp_usuario")
@Immutable // 🛡️ CRÍTICO: Bloquea cualquier intento de escritura (UPDATE/INSERT/DELETE)
public class BkpUsuario {

	@Id
	@Column(name = "camb_codigo", updatable = false, nullable = false)
	private Long cambCodigo; // ID del registro del log

	@Column(name = "camb_fecha", updatable = false)
	private LocalDateTime cambFecha; // Para saber cuál es el registro más reciente

	@Column(name = "ced_usuario", length = 13, updatable = false)
	private String cedUsuario; // Identificador del empleado

	// --- DATOS PERSONALES QUE EXIGE ORPHEUS ---

	@Column(name = "per_fechnaciusuaempl", updatable = false)
	private LocalDate fechaNacimiento;

	@Column(name = "per_civiusuaempl", length = 1, updatable = false)
	private String estadoCivil; // Ej: 'S' (Soltero), 'C' (Casado)

	@Column(name = "per_direprinusuaempl", length = 255, updatable = false)
	private String direccionPrincipal;

	@Column(name = "per_celuusuaempl", length = 20, updatable = false)
	private String celular;

	// --- DATOS LABORALES QUE EXIGE ORPHEUS ---

	@Column(name = "lab_fechingrusuaempl", updatable = false)
	private LocalDate fechaIngreso;

	@Column(name = "lab_fechsaliusuaempl", updatable = false)
	private LocalDate fechaSalida;

	@Column(name = "lab_motitempusuaempl", length = 255, updatable = false)
	private String motivoContrato; // Sirve como referencia

	@Column(name = "per_direnumeusuaempl", length = 255, updatable = false)
	private String direccionNumero;

	@Column(name = "per_diresecuusuaempl", length = 255, updatable = false)
	private String direccionSecundaria;

	@Column(name = "per_direrefeusuaempl", length = 255, updatable = false)
	private String direccionReferencia;

	@Column(name = "per_direbarrusuaempl", length = 255, updatable = false)
	private String direccionBarrio;

	@Column(name = "per_codiprovlugaviveusuaempl", updatable = false)
	private Short codProvinciaVive;

	@Column(name = "per_codicantlugaviveusuaempl", updatable = false)
	private Short codCiudadVive;

	// Constructores vacíos para Hibernate
	public BkpUsuario() {
	}

	// Getters
	public Long getCambCodigo() {
		return cambCodigo;
	}

	public LocalDateTime getCambFecha() {
		return cambFecha;
	}

	public String getCedUsuario() {
		return cedUsuario;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public String getDireccionPrincipal() {
		return direccionPrincipal;
	}

	public String getCelular() {
		return celular;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public LocalDate getFechaSalida() {
		return fechaSalida;
	}

	public String getMotivoContrato() {
		return motivoContrato;
	}

	public String getDireccionNumero() {
		return direccionNumero;
	}

	public String getDireccionSecundaria() {
		return direccionSecundaria;
	}

	public String getDireccionReferencia() {
		return direccionReferencia;
	}

	public String getDireccionBarrio() {
		return direccionBarrio;
	}

	public Short getCodProvinciaVive() {
		return codProvinciaVive;
	}

	public Short getCodCiudadVive() {
		return codCiudadVive;
	}

}