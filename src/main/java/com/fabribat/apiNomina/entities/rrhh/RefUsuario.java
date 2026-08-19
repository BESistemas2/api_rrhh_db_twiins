package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the ref_usuario database table.
 * 
 */
@Entity
@Table(name="ref_usuario")
@NamedQuery(name="RefUsuario.findAll", query="SELECT r FROM RefUsuario r")
public class RefUsuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="usr_usuario")
	private String usrUsuario;

	@Column(name="amb_usuario")
	private String ambUsuario;

	@Column(name="ape_usuario")
	private String apeUsuario;

	@Column(name="apr_jefeusuario")
	private String aprJefeusuario;

	@Column(name="blo_usuario")
	private String bloUsuario;

	@Column(name="ced_usuario")
	private String cedUsuario;

	@Column(name="cel_usuario")
	private String celUsuario;

	@Column(name="cod_cargentiexte")
	private short codCargentiexte;

	@Column(name="cod_departamento")
	private Short codDepartamento;

	@Column(name="cod_referencia")
	private short codReferencia;

	@Column(name="ema_geneusuario")
	private String emaGeneusuario;

	@Column(name="ema_usuario")
	private String emaUsuario;

	@Column(name="est_usuario")
	private String estUsuario;

	@Temporal(TemporalType.DATE)
	@Column(name="fec_regusuario")
	private Date fecRegusuario;

	@Column(name="fir_usuario")
	private String firUsuario;

	@Column(name="fot_usuario")
	private String fotUsuario;

	@Column(name="gen_usuario")
	private String genUsuario;

	@Column(name="ind_usuario")
	private String indUsuario;

	@Column(name="log_usuario")
	private short logUsuario;

	@Column(name="nom_compusuario")
	private String nomCompusuario;

	@Column(name="nom_usuario")
	private String nomUsuario;

	@Column(name="pas_usuario")
	private String pasUsuario;

	@Column(name="por_usuario")
	private String porUsuario;

	@Column(name="reg_usuario")
	private String regUsuario;

	@Column(name="sex_usuario")
	private String sexUsuario;

	@Column(name="usr_2_supervisor")
	private String usr2Supervisor;

	@Column(name="usr_3_supervisor")
	private String usr3Supervisor;

	@Column(name="usr_4_supervisor")
	private String usr4Supervisor;

	@Column(name="usr_supervisor")
	private String usrSupervisor;

	public RefUsuario() {
	}

	public String getUsrUsuario() {
		return this.usrUsuario;
	}

	public void setUsrUsuario(String usrUsuario) {
		this.usrUsuario = usrUsuario;
	}

	public String getAmbUsuario() {
		return this.ambUsuario;
	}

	public void setAmbUsuario(String ambUsuario) {
		this.ambUsuario = ambUsuario;
	}

	public String getApeUsuario() {
		return this.apeUsuario;
	}

	public void setApeUsuario(String apeUsuario) {
		this.apeUsuario = apeUsuario;
	}

	public String getAprJefeusuario() {
		return this.aprJefeusuario;
	}

	public void setAprJefeusuario(String aprJefeusuario) {
		this.aprJefeusuario = aprJefeusuario;
	}

	public String getBloUsuario() {
		return this.bloUsuario;
	}

	public void setBloUsuario(String bloUsuario) {
		this.bloUsuario = bloUsuario;
	}

	public String getCedUsuario() {
		return this.cedUsuario;
	}

	public void setCedUsuario(String cedUsuario) {
		this.cedUsuario = cedUsuario;
	}

	public String getCelUsuario() {
		return this.celUsuario;
	}

	public void setCelUsuario(String celUsuario) {
		this.celUsuario = celUsuario;
	}

	public short getCodCargentiexte() {
		return this.codCargentiexte;
	}

	public void setCodCargentiexte(short codCargentiexte) {
		this.codCargentiexte = codCargentiexte;
	}

	public Short getCodDepartamento() {
		return this.codDepartamento;
	}

	public void setCodDepartamento(Short codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public short getCodReferencia() {
		return this.codReferencia;
	}

	public void setCodReferencia(short codReferencia) {
		this.codReferencia = codReferencia;
	}

	public String getEmaGeneusuario() {
		return this.emaGeneusuario;
	}

	public void setEmaGeneusuario(String emaGeneusuario) {
		this.emaGeneusuario = emaGeneusuario;
	}

	public String getEmaUsuario() {
		return this.emaUsuario;
	}

	public void setEmaUsuario(String emaUsuario) {
		this.emaUsuario = emaUsuario;
	}

	public String getEstUsuario() {
		return this.estUsuario;
	}

	public void setEstUsuario(String estUsuario) {
		this.estUsuario = estUsuario;
	}

	public Date getFecRegusuario() {
		return this.fecRegusuario;
	}

	public void setFecRegusuario(Date fecRegusuario) {
		this.fecRegusuario = fecRegusuario;
	}

	public String getFirUsuario() {
		return this.firUsuario;
	}

	public void setFirUsuario(String firUsuario) {
		this.firUsuario = firUsuario;
	}

	public String getFotUsuario() {
		return this.fotUsuario;
	}

	public void setFotUsuario(String fotUsuario) {
		this.fotUsuario = fotUsuario;
	}

	public String getGenUsuario() {
		return this.genUsuario;
	}

	public void setGenUsuario(String genUsuario) {
		this.genUsuario = genUsuario;
	}

	public String getIndUsuario() {
		return this.indUsuario;
	}

	public void setIndUsuario(String indUsuario) {
		this.indUsuario = indUsuario;
	}

	public short getLogUsuario() {
		return this.logUsuario;
	}

	public void setLogUsuario(short logUsuario) {
		this.logUsuario = logUsuario;
	}

	public String getNomCompusuario() {
		return this.nomCompusuario;
	}

	public void setNomCompusuario(String nomCompusuario) {
		this.nomCompusuario = nomCompusuario;
	}

	public String getNomUsuario() {
		return this.nomUsuario;
	}

	public void setNomUsuario(String nomUsuario) {
		this.nomUsuario = nomUsuario;
	}

	public String getPasUsuario() {
		return this.pasUsuario;
	}

	public void setPasUsuario(String pasUsuario) {
		this.pasUsuario = pasUsuario;
	}

	public String getPorUsuario() {
		return this.porUsuario;
	}

	public void setPorUsuario(String porUsuario) {
		this.porUsuario = porUsuario;
	}

	public String getRegUsuario() {
		return this.regUsuario;
	}

	public void setRegUsuario(String regUsuario) {
		this.regUsuario = regUsuario;
	}

	public String getSexUsuario() {
		return this.sexUsuario;
	}

	public void setSexUsuario(String sexUsuario) {
		this.sexUsuario = sexUsuario;
	}

	public String getUsr2Supervisor() {
		return this.usr2Supervisor;
	}

	public void setUsr2Supervisor(String usr2Supervisor) {
		this.usr2Supervisor = usr2Supervisor;
	}

	public String getUsr3Supervisor() {
		return this.usr3Supervisor;
	}

	public void setUsr3Supervisor(String usr3Supervisor) {
		this.usr3Supervisor = usr3Supervisor;
	}

	public String getUsr4Supervisor() {
		return this.usr4Supervisor;
	}

	public void setUsr4Supervisor(String usr4Supervisor) {
		this.usr4Supervisor = usr4Supervisor;
	}

	public String getUsrSupervisor() {
		return this.usrSupervisor;
	}

	public void setUsrSupervisor(String usrSupervisor) {
		this.usrSupervisor = usrSupervisor;
	}

}