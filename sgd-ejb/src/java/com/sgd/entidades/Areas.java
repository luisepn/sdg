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
@Table(name = "areas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Areas.findAll", query = "SELECT a FROM Areas a"),
    @NamedQuery(name = "Areas.findById", query = "SELECT a FROM Areas a WHERE a.id = :id"),
    @NamedQuery(name = "Areas.findByNombre", query = "SELECT a FROM Areas a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Areas.findByCambiofecha", query = "SELECT a FROM Areas a WHERE a.cambiofecha = :cambiofecha"),
    @NamedQuery(name = "Areas.findByDiasalerta", query = "SELECT a FROM Areas a WHERE a.diasalerta = :diasalerta"),
    @NamedQuery(name = "Areas.findByDescripcion", query = "SELECT a FROM Areas a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Areas.findByActivo", query = "SELECT a FROM Areas a WHERE a.activo = :activo")})
public class Areas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "cambiofecha")
    private Boolean cambiofecha;
    @Column(name = "diasalerta")
    private Integer diasalerta;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @OneToMany(mappedBy = "area")
    private List<Procesos> procesosList;
    @OneToMany(mappedBy = "area")
    private List<Flujos> flujosList;

    public Areas() {
    }

    public Areas(Integer id) {
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

    public Boolean getCambiofecha() {
        return cambiofecha;
    }

    public void setCambiofecha(Boolean cambiofecha) {
        this.cambiofecha = cambiofecha;
    }

    public Integer getDiasalerta() {
        return diasalerta;
    }

    public void setDiasalerta(Integer diasalerta) {
        this.diasalerta = diasalerta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @XmlTransient
    public List<Procesos> getProcesosList() {
        return procesosList;
    }

    public void setProcesosList(List<Procesos> procesosList) {
        this.procesosList = procesosList;
    }

    @XmlTransient
    public List<Flujos> getFlujosList() {
        return flujosList;
    }

    public void setFlujosList(List<Flujos> flujosList) {
        this.flujosList = flujosList;
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
        if (!(object instanceof Areas)) {
            return false;
        }
        Areas other = (Areas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
