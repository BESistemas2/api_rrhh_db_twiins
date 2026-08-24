package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ref_ciudad database table.
 * 
 */
@Entity
@Table(name="ref_ciudad")
@NamedQuery(name="RefCiudad.findAll", query="SELECT r FROM RefCiudad r")
public class RefCiudad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_ciudad")
	private Short codCiudad;

	@Column(name="cod_canton")
	private Short codCanton;

	@Column(name="cod_provincia")
	private short codProvincia;

	@Column(name="cod_region")
	private short codRegion;

	@Column(name="cod_sriciudad")
	private String codSriciudad;

	@Column(name="est_ciudad")
	private String estCiudad;

	@Column(name="ide_ciudad")
	private String ideCiudad;

	@Column(name="nom_ciudad")
	private String nomCiudad;

	@Column(name="tra_ciudad")
	private String traCiudad;

	public RefCiudad() {
	}

	public short getCodCiudad() {
		return this.codCiudad;
	}

	public void setCodCiudad(short codCiudad) {
		this.codCiudad = codCiudad;
	}

	public short getCodCanton() {
		return this.codCanton;
	}

	public void setCodCanton(short codCanton) {
		this.codCanton = codCanton;
	}

	public short getCodProvincia() {
		return this.codProvincia;
	}

	public void setCodProvincia(short codProvincia) {
		this.codProvincia = codProvincia;
	}

	public short getCodRegion() {
		return this.codRegion;
	}

	public void setCodRegion(short codRegion) {
		this.codRegion = codRegion;
	}

	public String getCodSriciudad() {
		return this.codSriciudad;
	}

	public void setCodSriciudad(String codSriciudad) {
		this.codSriciudad = codSriciudad;
	}

	public String getEstCiudad() {
		return this.estCiudad;
	}

	public void setEstCiudad(String estCiudad) {
		this.estCiudad = estCiudad;
	}

	public String getIdeCiudad() {
		return this.ideCiudad;
	}

	public void setIdeCiudad(String ideCiudad) {
		this.ideCiudad = ideCiudad;
	}

	public String getNomCiudad() {
		return this.nomCiudad;
	}

	public void setNomCiudad(String nomCiudad) {
		this.nomCiudad = nomCiudad;
	}

	public String getTraCiudad() {
		return this.traCiudad;
	}

	public void setTraCiudad(String traCiudad) {
		this.traCiudad = traCiudad;
	}

}