/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sgd.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edison
 */
@Entity
@Table(name = "procesos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Procesos.findAll", query = "SELECT p FROM Procesos p"),
    @NamedQuery(name = "Procesos.findById", query = "SELECT p FROM Procesos p WHERE p.id = :id"),
    @NamedQuery(name = "Procesos.findByCodigo", query = "SELECT p FROM Procesos p WHERE p.codigo = :codigo"),
    @NamedQuery(name = "Procesos.findByFechaingreso", query = "SELECT p FROM Procesos p WHERE p.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Procesos.findByFechainicio", query = "SELECT p FROM Procesos p WHERE p.fechainicio = :fechainicio"),
    @NamedQuery(name = "Procesos.findByFechafin", query = "SELECT p FROM Procesos p WHERE p.fechafin = :fechafin"),
    @NamedQuery(name = "Procesos.findByTipoingreso", query = "SELECT p FROM Procesos p WHERE p.tipoingreso = :tipoingreso"),
    @NamedQuery(name = "Procesos.findByDescripcion", query = "SELECT p FROM Procesos p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Procesos.findByFechaplanificacion", query = "SELECT p FROM Procesos p WHERE p.fechaplanificacion = :fechaplanificacion"),
    @NamedQuery(name = "Procesos.findByFecharealizacion", query = "SELECT p FROM Procesos p WHERE p.fecharealizacion = :fecharealizacion"),
    @NamedQuery(name = "Procesos.findByFechaasignacion", query = "SELECT p FROM Procesos p WHERE p.fechaasignacion = :fechaasignacion"),
    @NamedQuery(name = "Procesos.findByIndicadorna", query = "SELECT p FROM Procesos p WHERE p.indicadorna = :indicadorna"),
    @NamedQuery(name = "Procesos.findByCodigosubact", query = "SELECT p FROM Procesos p WHERE p.codigosubact = :codigosubact"),
    @NamedQuery(name = "Procesos.findByNombresubact", query = "SELECT p FROM Procesos p WHERE p.nombresubact = :nombresubact"),
    @NamedQuery(name = "Procesos.findByDireccion", query = "SELECT p FROM Procesos p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Procesos.findByAnio", query = "SELECT p FROM Procesos p WHERE p.anio = :anio")})
public class Procesos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaingreso;
    @Column(name = "fechainicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechainicio;
    @Column(name = "fechafin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechafin;
    @Size(max = 2147483647)
    @Column(name = "tipoingreso")
    private String tipoingreso;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fechaplanificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaplanificacion;
    @Column(name = "fecharealizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharealizacion;
    @Column(name = "fechaasignacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaasignacion;
    @Column(name = "indicadorna")
    private Boolean indicadorna;
    @Size(max = 2147483647)
    @Column(name = "codigosubact")
    private String codigosubact;
    @Size(max = 2147483647)
    @Column(name = "nombresubact")
    private String nombresubact;
    @Size(max = 2147483647)
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "anio")
    private Integer anio;
    @OneToMany(mappedBy = "proceso")
    private List<Respuestas> respuestasList;
    @OneToMany(mappedBy = "proceso")
    private List<Trackings> trackingsList;
    @JoinColumn(name = "flujo", referencedColumnName = "id")
    @ManyToOne
    private Flujos flujo;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne
    private Entidades responsable;
    @JoinColumn(name = "denunciante", referencedColumnName = "id")
    @ManyToOne
    private Entidades denunciante;
    @JoinColumn(name = "denunciado", referencedColumnName = "id")
    @ManyToOne
    private Entidades denunciado;
    @JoinColumn(name = "area", referencedColumnName = "id")
    @ManyToOne
    private Areas area;
    @OneToMany(mappedBy = "proceso")
    private List<Documentos> documentosList;

    public Procesos() {
    }

    public Procesos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public String getTipoingreso() {
        return tipoingreso;
    }

    public void setTipoingreso(String tipoingreso) {
        this.tipoingreso = tipoingreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaplanificacion() {
        return fechaplanificacion;
    }

    public void setFechaplanificacion(Date fechaplanificacion) {
        this.fechaplanificacion = fechaplanificacion;
    }

    public Date getFecharealizacion() {
        return fecharealizacion;
    }

    public void setFecharealizacion(Date fecharealizacion) {
        this.fecharealizacion = fecharealizacion;
    }

    public Date getFechaasignacion() {
        return fechaasignacion;
    }

    public void setFechaasignacion(Date fechaasignacion) {
        this.fechaasignacion = fechaasignacion;
    }

    public Boolean getIndicadorna() {
        return indicadorna;
    }

    public void setIndicadorna(Boolean indicadorna) {
        this.indicadorna = indicadorna;
    }

    public String getCodigosubact() {
        return codigosubact;
    }

    public void setCodigosubact(String codigosubact) {
        this.codigosubact = codigosubact;
    }

    public String getNombresubact() {
        return nombresubact;
    }

    public void setNombresubact(String nombresubact) {
        this.nombresubact = nombresubact;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    @XmlTransient
    public List<Respuestas> getRespuestasList() {
        return respuestasList;
    }

    public void setRespuestasList(List<Respuestas> respuestasList) {
        this.respuestasList = respuestasList;
    }

    @XmlTransient
    public List<Trackings> getTrackingsList() {
        return trackingsList;
    }

    public void setTrackingsList(List<Trackings> trackingsList) {
        this.trackingsList = trackingsList;
    }

    public Flujos getFlujo() {
        return flujo;
    }

    public void setFlujo(Flujos flujo) {
        this.flujo = flujo;
    }

    public Entidades getResponsable() {
        return responsable;
    }

    public void setResponsable(Entidades responsable) {
        this.responsable = responsable;
    }

    public Entidades getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(Entidades denunciante) {
        this.denunciante = denunciante;
    }

    public Entidades getDenunciado() {
        return denunciado;
    }

    public void setDenunciado(Entidades denunciado) {
        this.denunciado = denunciado;
    }

    public Areas getArea() {
        return area;
    }

    public void setArea(Areas area) {
        this.area = area;
    }

    @XmlTransient
    public List<Documentos> getDocumentosList() {
        return documentosList;
    }

    public void setDocumentosList(List<Documentos> documentosList) {
        this.documentosList = documentosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Procesos)) {
            return false;
        }
        Procesos other = (Procesos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo;
    }
    
}
