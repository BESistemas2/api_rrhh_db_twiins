package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name="nomi_rel_usuariocentrocosto")
@IdClass(NomiRelUsuariocentrocostoPK.class)
@NamedQuery(name="NomiRelUsuariocentrocosto.findAll", query="SELECT n FROM NomiRelUsuariocentrocosto n")
public class NomiRelUsuariocentrocosto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="usr_usuario")
	private String usrUsuario;

	@Id
	@Column(name="cod_centrocosto")
	private Integer codCentrocosto; // Cambiado de int a Integer

	@Column(name="por_centrocosto")
	private Float porCentrocosto; // Cambiado de float a Float

	public NomiRelUsuariocentrocosto() {
	}

	public String getUsrUsuario() {
		return this.usrUsuario;
	}

	public void setUsrUsuario(String usrUsuario) {
		this.usrUsuario = usrUsuario;
	}

	public Integer getCodCentrocosto() {
		return this.codCentrocosto;
	}

	public void setCodCentrocosto(Integer codCentrocosto) {
		this.codCentrocosto = codCentrocosto;
	}

	public Float getPorCentrocosto() {
		return this.porCentrocosto;
	}

	public void setPorCentrocosto(Float porCentrocosto) {
		this.porCentrocosto = porCentrocosto;
	}
}