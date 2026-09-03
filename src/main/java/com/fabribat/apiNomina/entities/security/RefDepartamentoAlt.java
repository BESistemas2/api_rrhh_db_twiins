package com.fabribat.apiNomina.entities.security;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ref_departamento_alt database table.
 * 
 */
@Entity
@Table(name="ref_departamento_alt")
@NamedQuery(name="RefDepartamentoAlt.findAll", query="SELECT r FROM RefDepartamentoAlt r")
public class RefDepartamentoAlt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_departamento")
	private short codDepartamento;

	@Column(name="cod_area")
	private short codArea;

	@Column(name="cod_empresa")
	private short codEmpresa;

	@Column(name="des_departamento")
	private String desDepartamento;

	@Column(name="est_departamento")
	private String estDepartamento;

	@Column(name="ide_departamento")
	private String ideDepartamento;

	@Column(name="nom_departamento")
	private String nomDepartamento;

	@Column(name="obj_espedepartamento")
	private String objEspedepartamento;

	@Column(name="obj_estrdepartamento")
	private String objEstrdepartamento;

	@Column(name="org_departamento")
	private String orgDepartamento;

	@Column(name="pri_departamento")
	private String priDepartamento;

	@Column(name="res_departamento")
	private String resDepartamento;

	@Column(name="tip_departamento")
	private String tipDepartamento;

	@Column(name="usr_gerentecost")
	private String usrGerentecost;

	@Column(name="usr_gerentesier")
	private String usrGerentesier;

	public RefDepartamentoAlt() {
	}

	public short getCodDepartamento() {
		return this.codDepartamento;
	}

	public void setCodDepartamento(short codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public short getCodArea() {
		return this.codArea;
	}

	public void setCodArea(short codArea) {
		this.codArea = codArea;
	}

	public short getCodEmpresa() {
		return this.codEmpresa;
	}

	public void setCodEmpresa(short codEmpresa) {
		this.codEmpresa = codEmpresa;
	}

	public String getDesDepartamento() {
		return this.desDepartamento;
	}

	public void setDesDepartamento(String desDepartamento) {
		this.desDepartamento = desDepartamento;
	}

	public String getEstDepartamento() {
		return this.estDepartamento;
	}

	public void setEstDepartamento(String estDepartamento) {
		this.estDepartamento = estDepartamento;
	}

	public String getIdeDepartamento() {
		return this.ideDepartamento;
	}

	public void setIdeDepartamento(String ideDepartamento) {
		this.ideDepartamento = ideDepartamento;
	}

	public String getNomDepartamento() {
		return this.nomDepartamento;
	}

	public void setNomDepartamento(String nomDepartamento) {
		this.nomDepartamento = nomDepartamento;
	}

	public String getObjEspedepartamento() {
		return this.objEspedepartamento;
	}

	public void setObjEspedepartamento(String objEspedepartamento) {
		this.objEspedepartamento = objEspedepartamento;
	}

	public String getObjEstrdepartamento() {
		return this.objEstrdepartamento;
	}

	public void setObjEstrdepartamento(String objEstrdepartamento) {
		this.objEstrdepartamento = objEstrdepartamento;
	}

	public String getOrgDepartamento() {
		return this.orgDepartamento;
	}

	public void setOrgDepartamento(String orgDepartamento) {
		this.orgDepartamento = orgDepartamento;
	}

	public String getPriDepartamento() {
		return this.priDepartamento;
	}

	public void setPriDepartamento(String priDepartamento) {
		this.priDepartamento = priDepartamento;
	}

	public String getResDepartamento() {
		return this.resDepartamento;
	}

	public void setResDepartamento(String resDepartamento) {
		this.resDepartamento = resDepartamento;
	}

	public String getTipDepartamento() {
		return this.tipDepartamento;
	}

	public void setTipDepartamento(String tipDepartamento) {
		this.tipDepartamento = tipDepartamento;
	}

	public String getUsrGerentecost() {
		return this.usrGerentecost;
	}

	public void setUsrGerentecost(String usrGerentecost) {
		this.usrGerentecost = usrGerentecost;
	}

	public String getUsrGerentesier() {
		return this.usrGerentesier;
	}

	public void setUsrGerentesier(String usrGerentesier) {
		this.usrGerentesier = usrGerentesier;
	}

}