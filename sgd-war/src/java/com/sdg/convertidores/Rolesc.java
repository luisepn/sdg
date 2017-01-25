/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.convertidores;


import com.sdg.administracion.RolesBean;
import com.sdg.excepciones.ConsultarException;
import com.sgd.entidades.Roles;
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
@FacesConverter(forClass = Roles.class)
public class Rolesc implements Converter{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Roles rol = null; //ENTIDAD
        try {
            RolesBean bean = (RolesBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "rolesCP");
            Integer id = Integer.parseInt(value);
            rol = bean.traer(id);       
    } catch (ConsultarException ex) {
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
    }
    return rol;
}

@Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
   Roles rol = (Roles) value;//ENTIDAD 
   if (rol.getId() == null) {
            return "0";
        }
        return rol.getId().toString();
    }  
}
