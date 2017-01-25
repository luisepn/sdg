/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.convertidores;

import com.sdg.administracion.AreasBean;
import com.sdg.excepciones.ConsultarException;
import com.sgd.entidades.Areas;
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
@FacesConverter(forClass = Areas.class)
public class Areasc implements Converter{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Areas area = null; //ENTIDAD
        try {
            AreasBean bean = (AreasBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "areasCP");
            Integer id = Integer.parseInt(value);
            area = bean.traer(id);       
    } catch (ConsultarException ex) {
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
    }
    return area;
}

@Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
   Areas area = (Areas) value;//ENTIDAD 
   if (area.getId() == null) {
            return "0";
        }
        return area.getId().toString();
    }  
}
