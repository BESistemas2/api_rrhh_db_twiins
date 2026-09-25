package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import java.util.Objects;

public class NomiRelUsuariocentrocostoPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private String usrUsuario;
	private Integer codCentrocosto; // Cambiado de int a Integer

	public NomiRelUsuariocentrocostoPK() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NomiRelUsuariocentrocostoPK that = (NomiRelUsuariocentrocostoPK) o;
		return Objects.equals(codCentrocosto, that.codCentrocosto) &&
				Objects.equals(usrUsuario, that.usrUsuario);
	}

	@Override
	public int hashCode() {
		return Objects.hash(usrUsuario, codCentrocosto);
	}
}