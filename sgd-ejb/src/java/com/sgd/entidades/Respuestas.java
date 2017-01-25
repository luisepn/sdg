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
@Table(name = "respuestas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Respuestas.findAll", query = "SELECT r FROM Respuestas r"),
    @NamedQuery(name = "Respuestas.findById", query = "SELECT r FROM Respuestas r WHERE r.id = :id"),
    @NamedQuery(name = "Respuestas.findByNombreoficio", query = "SELECT r FROM Respuestas r WHERE r.nombreoficio = :nombreoficio"),
    @NamedQuery(name = "Respuestas.findByNrooficio", query = "SELECT r FROM Respuestas r WHERE r.nrooficio = :nrooficio")})
public class Respuestas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombreoficio")
    private String nombreoficio;
    @Size(max = 2147483647)
    @Column(name = "nrooficio")
    private String nrooficio;
    @JoinColumn(name = "proceso", referencedColumnName = "id")
    @ManyToOne
    private Procesos proceso;
    @OneToMany(mappedBy = "respuesta")
    private List<Documentos> documentosList;

    public Respuestas() {
    }

    public Respuestas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreoficio() {
        return nombreoficio;
    }

    public void setNombreoficio(String nombreoficio) {
        this.nombreoficio = nombreoficio;
    }

    public String getNrooficio() {
        return nrooficio;
    }

    public void setNrooficio(String nrooficio) {
        this.nrooficio = nrooficio;
    }

    public Procesos getProceso() {
        return proceso;
    }

    public void setProceso(Procesos proceso) {
        this.proceso = proceso;
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
        if (!(object instanceof Respuestas)) {
            return false;
        }
        Respuestas other = (Respuestas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sgd.entidades.Respuestas[ id=" + id + " ]";
    }
    
}
