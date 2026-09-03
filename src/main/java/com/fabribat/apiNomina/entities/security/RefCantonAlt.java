package com.fabribat.apiNomina.entities.security;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The persistent class for the ref_canton_alt database table.
 * 
 */
@Entity
@Table(name="ref_canton_alt")
@NamedQuery(name="RefCantonAlt.findAll", query="SELECT r FROM RefCantonAlt r")
public class RefCantonAlt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_canton")
	private Short codCanton;

	@Column(name="cod_provincia")
	private short codProvincia;

	@Column(name="nom_canton")
	private String nomCanton;

	@Column(name="est_canton")
	private String estCanton;

	public RefCantonAlt() {
	}

	public Short getCodCanton() {
		return this.codCanton;
	}

	public void setCodCanton(Short codCanton) {
		this.codCanton = codCanton;
	}

	public short getCodProvincia() {
		return this.codProvincia;
	}

	public void setCodProvincia(short codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getNomCanton() {
		return this.nomCanton;
	}

	public void setNomCanton(String nomCanton) {
		this.nomCanton = nomCanton;
	}

	public String getEstCanton() {
		return this.estCanton;
	}

	public void setEstCanton(String estCanton) {
		this.estCanton = estCanton;
	}

}