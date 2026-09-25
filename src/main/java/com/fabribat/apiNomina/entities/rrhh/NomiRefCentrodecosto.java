package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The persistent class for the nomi_ref_centrodecosto database table.[cite: 1]
 * 
 */
@Entity
@Table(name = "nomi_ref_centrodecosto")
@NamedQuery(name = "NomiRefCentrodecosto.findAll", query = "SELECT n FROM NomiRefCentrodecosto n")
public class NomiRefCentrodecosto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "cod_centrodecosto")
	private short codCentrodecosto;

	@Column(name = "cod_ciudad")
	private short codCiudad;

	@Column(name = "cod_distribucion")
	private short codDistribucion;

	@Column(name = "cod_empresa")
	private short codEmpresa;

	@Column(name = "cod_region")
	private short codRegion;

	@Column(name = "des_centrodecosto")
	private String desCentrodecosto;

	@Column(name = "est_centrodecosto")
	private String estCentrodecosto;

	@Column(name = "ide_centrodecosto")
	private String ideCentrodecosto;

	@Column(name = "nom_centrodecosto")
	private String nomCentrodecosto;

	@Column(name = "por_centrodecosto")
	private double porCentrodecosto;

	@Column(name = "tip_centrodecosto")
	private String tipCentrodecosto;

	public NomiRefCentrodecosto() {
	}

	public short getCodCentrodecosto() {
		return this.codCentrodecosto;
	}

	public void setCodCentrodecosto(short codCentrodecosto) {
		this.codCentrodecosto = codCentrodecosto;
	}

	public short getCodCiudad() {
		return this.codCiudad;
	}

	public void setCodCiudad(short codCiudad) {
		this.codCiudad = codCiudad;
	}

	public short getCodDistribucion() {
		return this.codDistribucion;
	}

	public void setCodDistribucion(short codDistribucion) {
		this.codDistribucion = codDistribucion;
	}

	public short getCodEmpresa() {
		return this.codEmpresa;
	}

	public void setCodEmpresa(short codEmpresa) {
		this.codEmpresa = codEmpresa;
	}

	public short getCodRegion() {
		return this.codRegion;
	}

	public void setCodRegion(short codRegion) {
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

	public double getPorCentrodecosto() {
		return this.porCentrodecosto;
	}

	public void setPorCentrodecosto(double porCentrodecosto) {
		this.porCentrodecosto = porCentrodecosto;
	}

	public String getTipCentrodecosto() {
		return this.tipCentrodecosto;
	}

	public void setTipCentrodecosto(String tipCentrodecosto) {
		this.tipCentrodecosto = tipCentrodecosto;
	}

}