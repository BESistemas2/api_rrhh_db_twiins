package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "bkp_cargo")
public class BkpCargo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "camb_codigo")
    private Long cambCodigo;

    @Column(name = "camb_usuario")
    private String cambUsuario;

    @Column(name = "camb_fecha")
    private LocalDateTime cambFecha;

    @Column(name = "camb_ip")
    private String cambIp;

    @Column(name = "camb_accion")
    private String cambAccion;

    @Column(name = "amb_usuario")
    private String ambUsuario;

    @Column(name = "cod_departamentousua")
    private Short codDepartamentousua;

    @Column(name = "cod_cargentiexte")
    private Short codCargentiexte;

    @Column(name = "cod_referencia")
    private Short codReferencia;

    @Column(name = "ced_usuario")
    private String cedUsuario;

    @Column(name = "nom_usuario")
    private String nomUsuario;

    @Column(name = "ape_usuario")
    private String apeUsuario;

    @Column(name = "nom_compusuario")
    private String nomCompusuario;

    @Column(name = "ema_usuario")
    private String emaUsuario;

    @Column(name = "cod_departamento")
    private Short codDepartamento;

    @Column(name = "cod_rol")
    private Short codRol;

    @Column(name = "cod_nivel")
    private Short codNivel;

    @Column(name = "cod_sectorial")
    private Short codSectorial;

    @Column(name = "cod_riescargo")
    private Short codRiescargo;

    @Column(name = "cod_cargo")
    private Short codCargo;

    @Column(name = "tip_cargo")
    private String tipCargo;

    @Column(name = "cod_emprcargo")
    private String codEmprcargo;

    @Column(name = "nom_cargo")
    private String nomCargo;

    @Column(name = "cri_cargo")
    private String criCargo;

    @Column(name = "ins_cargo")
    private String insCargo;

    @Column(name = "est_cargo")
    private String estCargo;

    @Column(name = "val_cargo")
    private Double valCargo;

    @Column(name = "tip_embacargo")
    private String tipEmbacargo;

    public BkpCargo() {}

    // Getters y Setters
    public Long getCambCodigo() { return cambCodigo; }
    public void setCambCodigo(Long cambCodigo) { this.cambCodigo = cambCodigo; }

    public String getCambUsuario() { return cambUsuario; }
    public void setCambUsuario(String cambUsuario) { this.cambUsuario = cambUsuario; }

    public LocalDateTime getCambFecha() { return cambFecha; }
    public void setCambFecha(LocalDateTime cambFecha) { this.cambFecha = cambFecha; }

    public String getCambIp() { return cambIp; }
    public void setCambIp(String cambIp) { this.cambIp = cambIp; }

    public String getCambAccion() { return cambAccion; }
    public void setCambAccion(String cambAccion) { this.cambAccion = cambAccion; }

    public String getAmbUsuario() { return ambUsuario; }
    public void setAmbUsuario(String ambUsuario) { this.ambUsuario = ambUsuario; }

    public Short getCodDepartamentousua() { return codDepartamentousua; }
    public void setCodDepartamentousua(Short codDepartamentousua) { this.codDepartamentousua = codDepartamentousua; }

    public Short getCodCargentiexte() { return codCargentiexte; }
    public void setCodCargentiexte(Short codCargentiexte) { this.codCargentiexte = codCargentiexte; }

    public Short getCodReferencia() { return codReferencia; }
    public void setCodReferencia(Short codReferencia) { this.codReferencia = codReferencia; }

    public String getCedUsuario() { return cedUsuario; }
    public void setCedUsuario(String cedUsuario) { this.cedUsuario = cedUsuario; }

    public String getNomUsuario() { return nomUsuario; }
    public void setNomUsuario(String nomUsuario) { this.nomUsuario = nomUsuario; }

    public String getApeUsuario() { return apeUsuario; }
    public void setApeUsuario(String apeUsuario) { this.apeUsuario = apeUsuario; }

    public String getNomCompusuario() { return nomCompusuario; }
    public void setNomCompusuario(String nomCompusuario) { this.nomCompusuario = nomCompusuario; }

    public String getEmaUsuario() { return emaUsuario; }
    public void setEmaUsuario(String emaUsuario) { this.emaUsuario = emaUsuario; }

    public Short getCodDepartamento() { return codDepartamento; }
    public void setCodDepartamento(Short codDepartamento) { this.codDepartamento = codDepartamento; }

    public Short getCodRol() { return codRol; }
    public void setCodRol(Short codRol) { this.codRol = codRol; }

    public Short getCodNivel() { return codNivel; }
    public void setCodNivel(Short codNivel) { this.codNivel = codNivel; }

    public Short getCodSectorial() { return codSectorial; }
    public void setCodSectorial(Short codSectorial) { this.codSectorial = codSectorial; }

    public Short getCodRiescargo() { return codRiescargo; }
    public void setCodRiescargo(Short codRiescargo) { this.codRiescargo = codRiescargo; }

    public Short getCodCargo() { return codCargo; }
    public void setCodCargo(Short codCargo) { this.codCargo = codCargo; }

    public String getTipCargo() { return tipCargo; }
    public void setTipCargo(String tipCargo) { this.tipCargo = tipCargo; }

    public String getCodEmprcargo() { return codEmprcargo; }
    public void setCodEmprcargo(String codEmprcargo) { this.codEmprcargo = codEmprcargo; }

    public String getNomCargo() { return nomCargo; }
    public void setNomCargo(String nomCargo) { this.nomCargo = nomCargo; }

    public String getCriCargo() { return criCargo; }
    public void setCriCargo(String criCargo) { this.criCargo = criCargo; }

    public String getInsCargo() { return insCargo; }
    public void setInsCargo(String insCargo) { this.insCargo = insCargo; }

    public String getEstCargo() { return estCargo; }
    public void setEstCargo(String estCargo) { this.estCargo = estCargo; }

    public Double getValCargo() { return valCargo; }
    public void setValCargo(Double valCargo) { this.valCargo = valCargo; }

    public String getTipEmbacargo() { return tipEmbacargo; }
    public void setTipEmbacargo(String tipEmbacargo) { this.tipEmbacargo = tipEmbacargo; }
}