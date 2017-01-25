/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.administracion;


import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.GrabarException;
import com.sdg.excepciones.InsertarException;
import com.sdg.seguridad.SeguridadBean;
import com.sdg.servicios.FlujosFacade;
import com.sdg.utilitarios.Combos;
import com.sdg.utilitarios.Formulario;
import com.sdg.utilitarios.MensajesErrores;
import com.sgd.entidades.Areas;
import com.sgd.entidades.Flujos;
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
@ManagedBean(name = "flujosCP")
@ViewScoped
public class FlujosBean implements Serializable {

    private static long serialVersionUID = 1L;

    /**
     * Creates a new instance of EstadosBean
     */
    @ManagedProperty(value = "#{seguridadBean}")
    private SeguridadBean seguridadBean;
    private List<Flujos> listaFlujos;
    private Formulario formulario = new Formulario();
    private Flujos flujo;
    private Areas area;

    @EJB
    private FlujosFacade ejbFlujos;
//    @EJB
//    private SeguridadComprasPublicasFacade ejbLogin;

    public FlujosBean() {
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = "o.nombre is not null and o.activo=true and o.area.activo=true";
        if (area != null) {
            where += " and o.area=:area";
            parametros.put("area", area);
        }
        //parametros.put(";where", "o.nombre is not null and o.activo=true");
        parametros.put(";where", where);
        //parametros.put(";orden", "o.area , o.nombre");
        parametros.put(";orden", "o.area.id asc, o.nro asc");
        try {
            listaFlujos = ejbFlujos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(FlujosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (area == null) {
            MensajesErrores.advertencia("Indique el Procedimiento para el flujo");
            return null;
        }
        flujo = new Flujos();
        flujo.setArea(area);
        flujo.setFechamod(Boolean.FALSE);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        flujo = listaFlujos.get(formulario.getFila().getRowIndex());
        area = flujo.getArea();
        formulario.editar();
        buscar();
        return null;
    }

    public String eliminar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        flujo = listaFlujos.get(formulario.getFila().getRowIndex());
        area = flujo.getArea();
        formulario.eliminar();
        return null;
    }

    public boolean validar() {

        if ((flujo.getNombre() == null) || (flujo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Ingresar el nombre del la etapa del flujo");
            return true;
        }
        if (flujo.getDiasrecordatorio() == null) {
            MensajesErrores.advertencia("Ingresar el numero de dias previo al recordario");
            return true;
        }
        if ((flujo.getRol().getNombre() == null) || (flujo.getRol().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Ingresar el rol del ejecutor de la etapa del flujo");
            return true;
        }
        if ((flujo.getAnterior()) != null) {
            if (flujo.getAnterior().equals(flujo.getSiguiente())) {
                MensajesErrores.advertencia("Seleccionar una etapa siguiente, distinta a la etapa anterior");
                return true;
            }
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.nombre=:nombre and o.activo=true");
            parametros.put("nombre", flujo.getNombre());
            List<Flujos> aux = ejbFlujos.encontarParametros(parametros);
            for (Flujos e : aux) {
                if (e.getNombre().contentEquals(flujo.getNombre())) {
                    MensajesErrores.advertencia("Etapa ya registrada");
                    return null;
                }
            }
            flujo.setActivo(Boolean.TRUE);
            ejbFlujos.create(flujo, seguridadBean.getUsr());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(FlujosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        //area = null;
        return null;
    }

    public String grabar() {
        try {
            ejbFlujos.edit(flujo, seguridadBean.getUsr());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(FlujosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        //area = null;
        return null;
    }

    public String borrar() {
        if (validar()) {
            return null;
        }
        try {
            flujo.setActivo(Boolean.FALSE);
            ejbFlujos.edit(flujo,  seguridadBean.getUsr());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(FlujosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        //area = null;
        return null;
    }

    public Flujos traer(Integer id) throws ConsultarException {
        return ejbFlujos.find(id);
    }

    public SelectItem[] getComboflujos() {
        buscar();
        return Combos.SelectItems(listaFlujos, true);
    }

    public SelectItem[] getComboflujosArea() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.area=:area and o.activo=true");
        parametros.put("area", area);
        //parametros.put(";orden", "o.area, o.nombre");
        parametros.put(";orden", "o.nombre");
        try {
            return Combos.SelectItems(ejbFlujos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(FlujosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    /**
     * @return the listaFlujos
     */
    public List<Flujos> getListaFlujos() {
        return listaFlujos;
    }

    /**
     * @param listaFlujos the listaFlujos to set
     */
    public void setListaFlujos(List<Flujos> listaFlujos) {
        this.listaFlujos = listaFlujos;
    }

    /**
     * @return the flujo
     */
    public Flujos getFlujo() {
        return flujo;
    }

    /**
     * @param flujo the flujo to set
     */
    public void setFlujo(Flujos flujo) {
        this.flujo = flujo;
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
}
