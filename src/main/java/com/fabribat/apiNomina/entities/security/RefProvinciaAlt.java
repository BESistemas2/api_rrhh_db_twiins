package com.fabribat.apiNomina.entities.security;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ref_provincia_alt database table.
 * 
 */
@Entity
@Table(name="ref_provincia_alt")
@NamedQuery(name="RefProvinciaAlt.findAll", query="SELECT r FROM RefProvinciaAlt r")
public class RefProvinciaAlt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_provincia")
	private short codProvincia;

	@Column(name="cod_areaprovincia")
	private String codAreaprovincia;

	@Column(name="cod_pais")
	private short codPais;

	@Column(name="cod_sriprovincia")
	private String codSriprovincia;

	@Column(name="est_provincia")
	private String estProvincia;

	@Column(name="nom_provincia")
	private String nomProvincia;

	public RefProvinciaAlt() {
	}

	public short getCodProvincia() {
		return this.codProvincia;
	}

	public void setCodProvincia(short codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getCodAreaprovincia() {
		return this.codAreaprovincia;
	}

	public void setCodAreaprovincia(String codAreaprovincia) {
		this.codAreaprovincia = codAreaprovincia;
	}

	public short getCodPais() {
		return this.codPais;
	}

	public void setCodPais(short codPais) {
		this.codPais = codPais;
	}

	public String getCodSriprovincia() {
		return this.codSriprovincia;
	}

	public void setCodSriprovincia(String codSriprovincia) {
		this.codSriprovincia = codSriprovincia;
	}

	public String getEstProvincia() {
		return this.estProvincia;
	}

	public void setEstProvincia(String estProvincia) {
		this.estProvincia = estProvincia;
	}

	public String getNomProvincia() {
		return this.nomProvincia;
	}

	public void setNomProvincia(String nomProvincia) {
		this.nomProvincia = nomProvincia;
	}

}