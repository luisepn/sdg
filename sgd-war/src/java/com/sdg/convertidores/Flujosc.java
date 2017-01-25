/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.convertidores;


import com.sdg.administracion.FlujosBean;
import com.sdg.excepciones.ConsultarException;
import com.sgd.entidades.Flujos;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Flujos.class)
public class Flujosc implements Converter{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Flujos estado = null; //ENTIDAD
        try {
            FlujosBean bean = (FlujosBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "flujosCP");
            Integer id = Integer.parseInt(value);
            estado = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
        }
        return estado;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
   Flujos estado = (Flujos) value;//ENTIDAD 
   if (estado.getId() == null) {
            return "0";
        }
        return estado.getId().toString();
    }    
}
