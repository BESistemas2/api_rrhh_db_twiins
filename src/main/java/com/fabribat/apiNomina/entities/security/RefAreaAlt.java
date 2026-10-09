package com.fabribat.apiNomina.entities.security;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name="ref_area_alt")
public class RefAreaAlt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_area")
	private Short codArea;

	@Column(name="cod_empresa")
	private Short codEmpresa;

	@Column(name="usr_gerente")
	private String usrGerente;

	@Column(name="ide_area")
	private String ideArea;

	@Column(name="nom_area")
	private String nomArea;

	@Column(name="tip_area")
	private String tipArea;

	@Column(name="est_area")
	private String estArea;

	public RefAreaAlt() {
	}

	public Short getCodArea() { return this.codArea; }
	public void setCodArea(Short codArea) { this.codArea = codArea; }

	public Short getCodEmpresa() { return this.codEmpresa; }
	public void setCodEmpresa(Short codEmpresa) { this.codEmpresa = codEmpresa; }

	public String getUsrGerente() { return this.usrGerente; }
	public void setUsrGerente(String usrGerente) { this.usrGerente = usrGerente; }

	public String getIdeArea() { return this.ideArea; }
	public void setIdeArea(String ideArea) { this.ideArea = ideArea; }

	public String getNomArea() { return this.nomArea; }
	public void setNomArea(String nomArea) { this.nomArea = nomArea; }

	public String getTipArea() { return this.tipArea; }
	public void setTipArea(String tipArea) { this.tipArea = tipArea; }

	public String getEstArea() { return this.estArea; }
	public void setEstArea(String estArea) { this.estArea = estArea; }
}