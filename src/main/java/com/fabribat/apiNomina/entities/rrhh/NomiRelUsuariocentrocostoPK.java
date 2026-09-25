package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key class for nomi_rel_usuariocentrocosto.
 * Required by JPA when a table has a composite primary key.
 */
public class NomiRelUsuariocentrocostoPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private String usrUsuario;
	private int codCentrocosto;

	public NomiRelUsuariocentrocostoPK() {
	}

	public String getUsrUsuario() {
		return this.usrUsuario;
	}

	public void setUsrUsuario(String usrUsuario) {
		this.usrUsuario = usrUsuario;
	}

	public int getCodCentrocosto() {
		return this.codCentrocosto;
	}

	public void setCodCentrocosto(int codCentrocosto) {
		this.codCentrocosto = codCentrocosto;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NomiRelUsuariocentrocostoPK that = (NomiRelUsuariocentrocostoPK) o;
		return codCentrocosto == that.codCentrocosto &&
				Objects.equals(usrUsuario, that.usrUsuario);
	}

	@Override
	public int hashCode() {
		return Objects.hash(usrUsuario, codCentrocosto);
	}
}