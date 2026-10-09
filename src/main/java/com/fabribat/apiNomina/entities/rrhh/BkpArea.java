package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "bkp_area")
public class BkpArea implements Serializable {
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

    @Column(name = "cod_departamento")
    private Short codDepartamento;

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

    @Column(name = "cod_empresa")
    private Short codEmpresa;

    @Column(name = "usr_gerente")
    private String usrGerente;

    @Column(name = "cod_area")
    private Short codArea;

    @Column(name = "ide_area")
    private String ideArea;

    @Column(name = "nom_area")
    private String nomArea;

    @Column(name = "tip_area")
    private String tipArea;

    @Column(name = "est_area")
    private String estArea;

    public BkpArea() {}

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

    public Short getCodDepartamento() { return codDepartamento; }
    public void setCodDepartamento(Short codDepartamento) { this.codDepartamento = codDepartamento; }

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

    public Short getCodEmpresa() { return codEmpresa; }
    public void setCodEmpresa(Short codEmpresa) { this.codEmpresa = codEmpresa; }

    public String getUsrGerente() { return usrGerente; }
    public void setUsrGerente(String usrGerente) { this.usrGerente = usrGerente; }

    public Short getCodArea() { return codArea; }
    public void setCodArea(Short codArea) { this.codArea = codArea; }

    public String getIdeArea() { return ideArea; }
    public void setIdeArea(String ideArea) { this.ideArea = ideArea; }

    public String getNomArea() { return nomArea; }
    public void setNomArea(String nomArea) { this.nomArea = nomArea; }

    public String getTipArea() { return tipArea; }
    public void setTipArea(String tipArea) { this.tipArea = tipArea; }

    public String getEstArea() { return estArea; }
    public void setEstArea(String estArea) { this.estArea = estArea; }
}