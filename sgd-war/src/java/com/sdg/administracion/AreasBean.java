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
import com.sdg.servicios.AreasFacade;
import com.sdg.utilitarios.Combos;
import com.sdg.utilitarios.Formulario;
import com.sdg.utilitarios.MensajesErrores;
import com.sgd.entidades.Areas;
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
@ManagedBean(name = "areasCP")
@ViewScoped
public class AreasBean implements Serializable {

    private static long serialVersionUID = 1L;

    /**
     * Creates a new instance of AreasBean
     */
    public AreasBean() {
    }

    @ManagedProperty(value = "#{seguridadBean}")
    private SeguridadBean seguridadBean;
    private List<Areas> listaAreas;
    private Areas area;
    private Formulario formulario = new Formulario();

    @EJB
    private AreasFacade ejbAreas;

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.nombre is not null and o.activo=true");
        parametros.put(";orden", "o.nombre");
        try {
            setListaAreas(ejbAreas.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AreasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        setArea(new Areas());
        area.setCambiofecha(Boolean.FALSE);
        getFormulario().insertar();
        return null;
    }

    public String modificar(Areas area) {
        this.setArea(area);
        getFormulario().editar();
        buscar();
        return null;
    }

    public String borra(Areas area) {
        this.setArea(area);
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {
        if ((getArea().getNombre() == null) || (getArea().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Ingresar el nombre del Procedimiento");
            return true;
        }
        if ((getArea().getDiasalerta() == null)) {
            MensajesErrores.advertencia("Ingresar el número de días para emitir recordatorio");
            return true;
        }
        if ((getArea().getDescripcion() == null) || (getArea().getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Ingresar descripción del Procedimiento");
            return true;
        }
        return false;
    }

    public String borrar() {
        if (validar()) {
            return null;
        }
        try {
            area.setActivo(Boolean.FALSE);
            ejbAreas.edit(getArea(), getSeguridadBean().getUsrLoggeado());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AreasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.nombre=:nombre and o.activo=true");
            parametros.put("nombre", area.getNombre());
            List<Areas> listaareas = ejbAreas.encontarParametros(parametros);
            for (Areas a : listaareas) {
                if (a.getNombre().contentEquals(area.getNombre())) {
                    MensajesErrores.advertencia("Procedimiento ya registrado");
                    return null;
                }
            }
            area.setActivo(Boolean.TRUE);
            ejbAreas.create(getArea(), getSeguridadBean().getUsrLoggeado());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(AreasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            ejbAreas.edit(area, getSeguridadBean().getUsr());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(AreasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Areas traer(Integer id) throws ConsultarException {
        return ejbAreas.find(id);
    }

    public SelectItem[] getComboareas() {
        buscar();
        return Combos.SelectItems(listaAreas, true);
    }

    /**
     * @return the listaAreas
     */
    public List<Areas> getListaAreas() {
        return listaAreas;
    }

    /**
     * @param listaAreas the listaAreas to set
     */
    public void setListaAreas(List<Areas> listaAreas) {
        this.listaAreas = listaAreas;
    }

    /**
     * @return the area
     */
    public Areas getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(Areas area) {
        this.area = area;
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
