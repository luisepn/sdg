/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.servicios;

import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.InsertarException;
import com.sgd.entidades.Procesos;
import com.sgd.entidades.Trackings;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.mail.MessagingException;

/**
 *
 * @author edison
 */
@Singleton
@LocalBean
public class AlertasRecordatorioFacade {

    @EJB
    private TrackingsFacade ejbTracking;
    @EJB
    private CorreoFacade ejbCorreo;
    @EJB
    private ProcesosFacade ejbDenuncias;

    @Schedule(minute = "55", second = "01", dayOfMonth = "*", month = "*", year = "*", hour = "23", dayOfWeek = "Mon-Fri")

    public void procesoAlertas() {

        List<Procesos> listaDenuncias;
        Map parametros = new HashMap();
        parametros.put("fecha", new Date());
        parametros.put(";where", "o.fechafin is null and o.area.cambiofecha=true");
        parametros.put(";orden", "o.fechainicio desc");

        try {
            listaDenuncias = ejbDenuncias.encontarParametros(parametros);
            //listaTrackings = ejbTracking.encontarParametros(parametros);
            for (Procesos d : listaDenuncias) {
                int max = 0;
                Calendar inicio = Calendar.getInstance();
                max = d.getArea().getDiasalerta();
                //max = denuncia.getEstado().getTiemporecordatorio();
                for (int c = 0; c < max; c++) {
                    inicio.add(Calendar.DATE, 1);
                    if ((inicio.get(Calendar.DAY_OF_WEEK) == 1) || (inicio.get(Calendar.DAY_OF_WEEK) == 7)) {
                        max++;
                    }
                }
                Calendar plan = Calendar.getInstance();
                plan.setTime(d.getFechaplanificacion());
                plan.set(Calendar.HOUR_OF_DAY,00);
                plan.set(Calendar.MINUTE,00);
                plan.set(Calendar.SECOND,00);
                
                
                Calendar hoy = Calendar.getInstance();
                hoy.set(Calendar.HOUR_OF_DAY, 00);
                hoy.set(Calendar.MINUTE, 00);
                hoy.set(Calendar.SECOND, 00);
            
                //fecha1.setTime(d.getFechaplanificacion());
                
                hoy.add(Calendar.DAY_OF_YEAR, max);
                if (hoy.equals(plan)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date now = new Date();
                    DateFormat df1 = DateFormat.getDateInstance(DateFormat.SHORT);
                    String s = df1.format(now);
                    String correo = "<p> <Strong>El istema deGestiòne Archivs le informa que:</Strong></p>";
                    correo += "<p><Strong> El proceso de código: </Strong>" + d.getCodigo() + "</p>";
                    correo += "<p><Strong> Se encuentra en estado: </Strong> " + d.getFlujo().getNombre() + "</p>";
                    correo += "<p><Strong> Asignada a : </Strong> " + d.getResponsable().toString() + "</p>";
                    correo += "<p><Strong> Funcionario con rol : </Strong> " + d.getFlujo().getRol().getNombre() + "</p>";
                    //correo += "<p><Strong> Fue asignada con fecha: </Strong>" + d.getFechaasignacion() + "</p>";
                    correo += "<p><Strong> Con fecha máxima de realización: </Strong>" + d.getFechaplanificacion() + "</p>";
                    correo += "<p><Strong> Se solicita tener en cuenta el cumplimiento de dicho proceso con el fin de cumplir con los tiempos establecidos</Strong></p>";
                    correo += "<p><Strong> Gracias</Strong></p>";
                    correo += "<br><br><p><Strong> Atentamente </Strong></p>";
                    correo += "<br><br><p>Texto generado automáticamente por el Sistema de Gestión de Archivos</p>";
                    ejbCorreo.enviarCorreo(d.getResponsable().getEmail(), "Alerta automática para continuación de Procesos", correo);
                    Trackings tNuevo = new Trackings();
                    tNuevo.setProceso(d);
                    tNuevo.setFlujo(d.getFlujo().getNombre());
                    tNuevo.setFecha(new Date());
                    tNuevo.setFechaplanificacion(new Date());
                    tNuevo.setFecharealizacion(new Date());
                    //tNuevo.setResponsable(d.getResponsable());
                    tNuevo.setUserid("SERVIDOR CORREO");
                    tNuevo.setObservaciones("Envio de Alerta a: " + d.getResponsable().toString() + " solicitando se de trámite a dicho proceso con el fin de cumplir con los tiempos establecidos");
                    ejbTracking.create(tNuevo, "SERVIDOR CORREO");
                }
            }
        } catch (MessagingException | UnsupportedEncodingException | ConsultarException | InsertarException ex) {
            Logger.getLogger(AlertasRecordatorioFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
