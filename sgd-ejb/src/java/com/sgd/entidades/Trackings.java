/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sgd.entidades;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edison
 */
@Entity
@Table(name = "trackings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trackings.findAll", query = "SELECT t FROM Trackings t"),
    @NamedQuery(name = "Trackings.findById", query = "SELECT t FROM Trackings t WHERE t.id = :id"),
    @NamedQuery(name = "Trackings.findByFecha", query = "SELECT t FROM Trackings t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Trackings.findByFlujo", query = "SELECT t FROM Trackings t WHERE t.flujo = :flujo"),
    @NamedQuery(name = "Trackings.findByFechaplanificacion", query = "SELECT t FROM Trackings t WHERE t.fechaplanificacion = :fechaplanificacion"),
    @NamedQuery(name = "Trackings.findByFecharealizacion", query = "SELECT t FROM Trackings t WHERE t.fecharealizacion = :fecharealizacion"),
    @NamedQuery(name = "Trackings.findByObservaciones", query = "SELECT t FROM Trackings t WHERE t.observaciones = :observaciones"),
    @NamedQuery(name = "Trackings.findByUserid", query = "SELECT t FROM Trackings t WHERE t.userid = :userid"),
    @NamedQuery(name = "Trackings.findByResponsable", query = "SELECT t FROM Trackings t WHERE t.responsable = :responsable")})
public class Trackings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "flujo")
    private String flujo;
    @Column(name = "fechaplanificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaplanificacion;
    @Column(name = "fecharealizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharealizacion;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 2147483647)
    @Column(name = "userid")
    private String userid;
    @Size(max = 2147483647)
    @Column(name = "responsable")
    private String responsable;
    @JoinColumn(name = "proceso", referencedColumnName = "id")
    @ManyToOne
    private Procesos proceso;

    public Trackings() {
    }

    public Trackings(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFlujo() {
        return flujo;
    }

    public void setFlujo(String flujo) {
        this.flujo = flujo;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Procesos getProceso() {
        return proceso;
    }

    public void setProceso(Procesos proceso) {
        this.proceso = proceso;
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
        if (!(object instanceof Trackings)) {
            return false;
        }
        Trackings other = (Trackings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sgd.entidades.Trackings[ id=" + id + " ]";
    }
    
}
