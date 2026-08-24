package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ref_cargo database table.
 * 
 */
@Entity
@Table(name="ref_cargo")
@NamedQuery(name="RefCargo.findAll", query="SELECT r FROM RefCargo r")
public class RefCargo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_cargo")
	private short codCargo;

	@Column(name="cod_departamento")
	private Short codDepartamento;

	@Column(name="cod_emprcargo")
	private String codEmprcargo;

	@Column(name="cod_nivel")
	private short codNivel;

	@Column(name="cod_riescargo")
	private short codRiescargo;

	@Column(name="cod_rol")
	private short codRol;

	@Column(name="cod_sectorial")
	private short codSectorial;

	@Column(name="con_cargo")
	private String conCargo;

	@Column(name="cri_cargo")
	private String criCargo;

	@Column(name="est_cargo")
	private String estCargo;

	@Column(name="ins_cargo")
	private String insCargo;

	@Column(name="nom_cargo")
	private String nomCargo;

	@Column(name="tip_cargo")
	private String tipCargo;

	@Column(name="tip_embacargo")
	private String tipEmbacargo;

	@Column(name="val_cargo")
	private Double valCargo;

	public RefCargo() {
	}

	public short getCodCargo() {
		return this.codCargo;
	}

	public void setCodCargo(short codCargo) {
		this.codCargo = codCargo;
	}

	public Short getCodDepartamento() {
		return this.codDepartamento;
	}

	public void setCodDepartamento(Short codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public String getCodEmprcargo() {
		return this.codEmprcargo;
	}

	public void setCodEmprcargo(String codEmprcargo) {
		this.codEmprcargo = codEmprcargo;
	}

	public short getCodNivel() {
		return this.codNivel;
	}

	public void setCodNivel(short codNivel) {
		this.codNivel = codNivel;
	}

	public short getCodRiescargo() {
		return this.codRiescargo;
	}

	public void setCodRiescargo(short codRiescargo) {
		this.codRiescargo = codRiescargo;
	}

	public short getCodRol() {
		return this.codRol;
	}

	public void setCodRol(short codRol) {
		this.codRol = codRol;
	}

	public short getCodSectorial() {
		return this.codSectorial;
	}

	public void setCodSectorial(short codSectorial) {
		this.codSectorial = codSectorial;
	}

	public String getConCargo() {
		return this.conCargo;
	}

	public void setConCargo(String conCargo) {
		this.conCargo = conCargo;
	}

	public String getCriCargo() {
		return this.criCargo;
	}

	public void setCriCargo(String criCargo) {
		this.criCargo = criCargo;
	}

	public String getEstCargo() {
		return this.estCargo;
	}

	public void setEstCargo(String estCargo) {
		this.estCargo = estCargo;
	}

	public String getInsCargo() {
		return this.insCargo;
	}

	public void setInsCargo(String insCargo) {
		this.insCargo = insCargo;
	}

	public String getNomCargo() {
		return this.nomCargo;
	}

	public void setNomCargo(String nomCargo) {
		this.nomCargo = nomCargo;
	}

	public String getTipCargo() {
		return this.tipCargo;
	}

	public void setTipCargo(String tipCargo) {
		this.tipCargo = tipCargo;
	}

	public String getTipEmbacargo() {
		return this.tipEmbacargo;
	}

	public void setTipEmbacargo(String tipEmbacargo) {
		this.tipEmbacargo = tipEmbacargo;
	}

	public Double getValCargo() {
		return this.valCargo;
	}

	public void setValCargo(Double valCargo) {
		this.valCargo = valCargo;
	}

}