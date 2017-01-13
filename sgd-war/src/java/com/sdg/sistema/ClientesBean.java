/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.sistema;

import com.sdg.administracion.PersonasBean;
import com.sdg.excepciones.ConsultarException;
import com.sdg.utilitarios.Combos;
import com.sdg.utilitarios.Formulario;
import com.sdg.utilitarios.MensajesErrores;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "clientes")
@ViewScoped
public class ClientesBean extends PersonasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Formulario formularioClave = new Formulario();

    public ClientesBean() {
        super.setRol("#C"); //Clientes
    }

    public SelectItem[] getComboUsuarios() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo=true and o.rol like '%#C%'");
        parametros.put(";orden", " o.apellidos");

        try {
            return Combos.SelectItems(ejbEntidad.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PersonasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the formularioClave
     */
    public Formulario getFormularioClave() {
        return formularioClave;
    }

    /**
     * @param formularioClave the formularioClave to set
     */
    public void setFormularioClave(Formulario formularioClave) {
        this.formularioClave = formularioClave;
    }

}
