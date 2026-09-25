package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The persistent class for the nomi_ref_centrodecosto database table.
 * 
 */
@Entity
@Table(name="nomi_ref_centrodecosto")
@NamedQuery(name="NomiRefCentrodecosto.findAll", query="SELECT n FROM NomiRefCentrodecosto n")
public class NomiRefCentrodecosto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_centrodecosto")
	private Short codCentrodecosto; // Cambiado de short a Short

	@Column(name="cod_ciudad")
	private Short codCiudad; // Cambiado de short a Short

	@Column(name="cod_distribucion")
	private Short codDistribucion; // Cambiado de short a Short

	@Column(name="cod_empresa")
	private Short codEmpresa; // Cambiado de short a Short

	@Column(name="cod_region")
	private Short codRegion; // Cambiado de short a Short

	@Column(name="des_centrodecosto")
	private String desCentrodecosto;

	@Column(name="est_centrodecosto")
	private String estCentrodecosto;

	@Column(name="ide_centrodecosto")
	private String ideCentrodecosto;

	@Column(name="nom_centrodecosto")
	private String nomCentrodecosto;

	@Column(name="por_centrodecosto")
	private Double porCentrodecosto; // Cambiado de double a Double

	@Column(name="tip_centrodecosto")
	private String tipCentrodecosto;

	public NomiRefCentrodecosto() {
	}

	public Short getCodCentrodecosto() {
		return this.codCentrodecosto;
	}

	public void setCodCentrodecosto(Short codCentrodecosto) {
		this.codCentrodecosto = codCentrodecosto;
	}

	public Short getCodCiudad() {
		return this.codCiudad;
	}

	public void setCodCiudad(Short codCiudad) {
		this.codCiudad = codCiudad;
	}

	public Short getCodDistribucion() {
		return this.codDistribucion;
	}

	public void setCodDistribucion(Short codDistribucion) {
		this.codDistribucion = codDistribucion;
	}

	public Short getCodEmpresa() {
		return this.codEmpresa;
	}

	public void setCodEmpresa(Short codEmpresa) {
		this.codEmpresa = codEmpresa;
	}

	public Short getCodRegion() {
		return this.codRegion;
	}

	public void setCodRegion(Short codRegion) {
		this.codRegion = codRegion;
	}

	public String getDesCentrodecosto() {
		return this.desCentrodecosto;
	}

	public void setDesCentrodecosto(String desCentrodecosto) {
		this.desCentrodecosto = desCentrodecosto;
	}

	public String getEstCentrodecosto() {
		return this.estCentrodecosto;
	}

	public void setEstCentrodecosto(String estCentrodecosto) {
		this.estCentrodecosto = estCentrodecosto;
	}

	public String getIdeCentrodecosto() {
		return this.ideCentrodecosto;
	}

	public void setIdeCentrodecosto(String ideCentrodecosto) {
		this.ideCentrodecosto = ideCentrodecosto;
	}

	public String getNomCentrodecosto() {
		return this.nomCentrodecosto;
	}

	public void setNomCentrodecosto(String nomCentrodecosto) {
		this.nomCentrodecosto = nomCentrodecosto;
	}

	public Double getPorCentrodecosto() {
		return this.porCentrodecosto;
	}

	public void setPorCentrodecosto(Double porCentrodecosto) {
		this.porCentrodecosto = porCentrodecosto;
	}

	public String getTipCentrodecosto() {
		return this.tipCentrodecosto;
	}

	public void setTipCentrodecosto(String tipCentrodecosto) {
		this.tipCentrodecosto = tipCentrodecosto;
	}
}