/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.administracion;

import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.GrabarException;
import com.sdg.excepciones.InsertarException;
import com.sdg.seguridad.SeguridadBean;
import com.sdg.servicios.RolesFacade;
import com.sdg.utilitarios.Combos;
import com.sdg.utilitarios.Formulario;
import com.sdg.utilitarios.MensajesErrores;
import com.sgd.entidades.Roles;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author edison
 */
@ManagedBean(name = "rolesCP")
@ViewScoped
public class RolesBean implements Serializable {

    private static long serialVersionUID = 1L;
    
    @ManagedProperty(value = "#{seguridadBean}")
    private SeguridadBean seguridadBean;
    private List<Roles> listaRoles;
    private Roles rol;
    private Formulario formulario = new Formulario();

//    @EJB
//    private SeguridadBean ejbLogin;
    @EJB
    private RolesFacade ejbRoles;

    public RolesBean() {
    }

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.nombre is not null and o.activo=true");
        parametros.put(";orden", "o.nombre");
        try {
            setListaRoles(ejbRoles.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RolesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        setRol(new Roles());
        rol.setActivo(Boolean.TRUE);
        formulario.insertar();
        return null;
    }

    public String modificar(Roles rol) {
        this.setRol(rol);
        formulario.editar();
        buscar();
        return null;
    }

    public String borra(Roles rol) {
        this.setRol(rol);
        formulario.eliminar();
        return null;
    }

    public boolean validar() {
        if ((getRol().getNombre() == null) || (getRol().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Ingresar el nombre del Área de denuncia");
            return true;
        }
        if ((getRol().getDescripcion() == null) || (getRol().getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Ingresar descripción del rol del funcionario");
            return true;
        }
        return false;
    }

    public String borrar() {
        if (validar()) {
            return null;
        }
        try {
            rol.setActivo(Boolean.FALSE);
            ejbRoles.edit(getRol(),  seguridadBean.getUsr());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RolesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.nombre=:nombre and o.activo=true");
            parametros.put("nombre", rol.getNombre());
            List<Roles> listaroles = ejbRoles.encontarParametros(parametros);
            for (Roles r : listaroles) {
                if (r.getNombre().contentEquals(rol.getNombre())) {
                    MensajesErrores.advertencia("Rol ya registrado");
                    return null;
                }
            }
            ejbRoles.create(getRol(),  seguridadBean.getUsr());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(RolesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            ejbRoles.edit(getRol(),  seguridadBean.getUsr());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(RolesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Roles traer(Integer id) throws ConsultarException {
        return ejbRoles.find(id);
    }

    public SelectItem[] getComboroles() {
        buscar();
        return Combos.SelectItems(listaRoles, true);
    }

    /**
     * @return the listaRoles
     */
    public List<Roles> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<Roles> listaRoles) {
        this.listaRoles = listaRoles;
    }

    /**
     * @return the rol
     */
    public Roles getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(Roles rol) {
        this.rol = rol;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the seguridadBean
     */
    public SeguridadBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean the seguridadBean to set
     */
    public void setSeguridadBean(SeguridadBean seguridadBean) {
        this.seguridadBean = seguridadBean;
    }
}
