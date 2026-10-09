package com.fabribat.apiNomina.entities.rrhh;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "bkp_departamento")
public class BkpDepartamento implements Serializable {
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

    @Column(name = "cod_empresa")
    private Short codEmpresa;

    @Column(name = "cod_area")
    private Short codArea;

    @Column(name = "usr_gerentesier")
    private String usrGerentesier;

    @Column(name = "usr_gerentecost")
    private String usrGerentecost;

    @Column(name = "cod_departamento")
    private Short codDepartamento;

    @Column(name = "tip_departamento")
    private String tipDepartamento;

    @Column(name = "ide_departamento")
    private String ideDepartamento;

    @Column(name = "nom_departamento")
    private String nomDepartamento;

    @Column(name = "des_departamento")
    private String desDepartamento;

    @Column(name = "res_departamento")
    private String resDepartamento;

    @Column(name = "pri_departamento")
    private String priDepartamento;

    @Column(name = "obj_espedepartamento")
    private String objEspedepartamento;

    @Column(name = "obj_estrdepartamento")
    private String objEstrdepartamento;

    @Column(name = "est_departamento")
    private String estDepartamento;

    public BkpDepartamento() {}

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

    public Short getCodEmpresa() { return codEmpresa; }
    public void setCodEmpresa(Short codEmpresa) { this.codEmpresa = codEmpresa; }

    public Short getCodArea() { return codArea; }
    public void setCodArea(Short codArea) { this.codArea = codArea; }

    public String getUsrGerentesier() { return usrGerentesier; }
    public void setUsrGerentesier(String usrGerentesier) { this.usrGerentesier = usrGerentesier; }

    public String getUsrGerentecost() { return usrGerentecost; }
    public void setUsrGerentecost(String usrGerentecost) { this.usrGerentecost = usrGerentecost; }

    public Short getCodDepartamento() { return codDepartamento; }
    public void setCodDepartamento(Short codDepartamento) { this.codDepartamento = codDepartamento; }

    public String getTipDepartamento() { return tipDepartamento; }
    public void setTipDepartamento(String tipDepartamento) { this.tipDepartamento = tipDepartamento; }

    public String getIdeDepartamento() { return ideDepartamento; }
    public void setIdeDepartamento(String ideDepartamento) { this.ideDepartamento = ideDepartamento; }

    public String getNomDepartamento() { return nomDepartamento; }
    public void setNomDepartamento(String nomDepartamento) { this.nomDepartamento = nomDepartamento; }

    public String getDesDepartamento() { return desDepartamento; }
    public void setDesDepartamento(String desDepartamento) { this.desDepartamento = desDepartamento; }

    public String getResDepartamento() { return resDepartamento; }
    public void setResDepartamento(String resDepartamento) { this.resDepartamento = resDepartamento; }

    public String getPriDepartamento() { return priDepartamento; }
    public void setPriDepartamento(String priDepartamento) { this.priDepartamento = priDepartamento; }

    public String getObjEspedepartamento() { return objEspedepartamento; }
    public void setObjEspedepartamento(String objEspedepartamento) { this.objEspedepartamento = objEspedepartamento; }

    public String getObjEstrdepartamento() { return objEstrdepartamento; }
    public void setObjEstrdepartamento(String objEstrdepartamento) { this.objEstrdepartamento = objEstrdepartamento; }

    public String getEstDepartamento() { return estDepartamento; }
    public void setEstDepartamento(String estDepartamento) { this.estDepartamento = estDepartamento; }
}