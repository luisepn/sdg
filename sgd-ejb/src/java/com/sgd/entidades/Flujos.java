/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sgd.entidades;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edison
 */
@Entity
@Table(name = "flujos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Flujos.findAll", query = "SELECT f FROM Flujos f"),
    @NamedQuery(name = "Flujos.findById", query = "SELECT f FROM Flujos f WHERE f.id = :id"),
    @NamedQuery(name = "Flujos.findByNombre", query = "SELECT f FROM Flujos f WHERE f.nombre = :nombre"),
    @NamedQuery(name = "Flujos.findByFechamod", query = "SELECT f FROM Flujos f WHERE f.fechamod = :fechamod"),
    @NamedQuery(name = "Flujos.findByDiasrecordatorio", query = "SELECT f FROM Flujos f WHERE f.diasrecordatorio = :diasrecordatorio"),
    @NamedQuery(name = "Flujos.findByDetalle", query = "SELECT f FROM Flujos f WHERE f.detalle = :detalle"),
    @NamedQuery(name = "Flujos.findByAnexosmod", query = "SELECT f FROM Flujos f WHERE f.anexosmod = :anexosmod"),
    @NamedQuery(name = "Flujos.findByActivo", query = "SELECT f FROM Flujos f WHERE f.activo = :activo"),
    @NamedQuery(name = "Flujos.findByNro", query = "SELECT f FROM Flujos f WHERE f.nro = :nro")})
public class Flujos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fechamod")
    private Boolean fechamod;
    @Column(name = "diasrecordatorio")
    private Integer diasrecordatorio;
    @Size(max = 2147483647)
    @Column(name = "detalle")
    private String detalle;
    @Column(name = "anexosmod")
    private Boolean anexosmod;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "nro")
    private Integer nro;
    @OneToMany(mappedBy = "flujo")
    private List<Procesos> procesosList;
    @JoinColumn(name = "rol", referencedColumnName = "id")
    @ManyToOne
    private Roles rol;
    @OneToMany(mappedBy = "siguiente")
    private List<Flujos> flujosList;
    @JoinColumn(name = "siguiente", referencedColumnName = "id")
    @ManyToOne
    private Flujos siguiente;
    @OneToMany(mappedBy = "anterior")
    private List<Flujos> flujosList1;
    @JoinColumn(name = "anterior", referencedColumnName = "id")
    @ManyToOne
    private Flujos anterior;
    @JoinColumn(name = "area", referencedColumnName = "id")
    @ManyToOne
    private Areas area;

    public Flujos() {
    }

    public Flujos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getFechamod() {
        return fechamod;
    }

    public void setFechamod(Boolean fechamod) {
        this.fechamod = fechamod;
    }

    public Integer getDiasrecordatorio() {
        return diasrecordatorio;
    }

    public void setDiasrecordatorio(Integer diasrecordatorio) {
        this.diasrecordatorio = diasrecordatorio;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Boolean getAnexosmod() {
        return anexosmod;
    }

    public void setAnexosmod(Boolean anexosmod) {
        this.anexosmod = anexosmod;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getNro() {
        return nro;
    }

    public void setNro(Integer nro) {
        this.nro = nro;
    }

    @XmlTransient
    public List<Procesos> getProcesosList() {
        return procesosList;
    }

    public void setProcesosList(List<Procesos> procesosList) {
        this.procesosList = procesosList;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    @XmlTransient
    public List<Flujos> getFlujosList() {
        return flujosList;
    }

    public void setFlujosList(List<Flujos> flujosList) {
        this.flujosList = flujosList;
    }

    public Flujos getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Flujos siguiente) {
        this.siguiente = siguiente;
    }

    @XmlTransient
    public List<Flujos> getFlujosList1() {
        return flujosList1;
    }

    public void setFlujosList1(List<Flujos> flujosList1) {
        this.flujosList1 = flujosList1;
    }

    public Flujos getAnterior() {
        return anterior;
    }

    public void setAnterior(Flujos anterior) {
        this.anterior = anterior;
    }

    public Areas getArea() {
        return area;
    }

    public void setArea(Areas area) {
        this.area = area;
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
        if (!(object instanceof Flujos)) {
            return false;
        }
        Flujos other = (Flujos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nro+"-"+nombre;
    }
    
}
