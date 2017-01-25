/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.sistema;

import com.icesoft.faces.context.Resource;
import com.sdg.administracion.FlujosBean;
import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.GrabarException;
import com.sdg.excepciones.InsertarException;
import com.sdg.seguridad.SeguridadBean;
import com.sdg.servicios.CorreoFacade;
import com.sdg.servicios.EntidadesFacade;
import com.sdg.servicios.FlujosFacade;
import com.sdg.servicios.ProcesosFacade;
import com.sdg.servicios.RespuestasFacade;
import com.sdg.servicios.TrackingsFacade;
import com.sdg.utilitarios.Combos;
import com.sdg.utilitarios.Formulario;
import com.sdg.utilitarios.ImagenesBean;
import com.sdg.utilitarios.MensajesErrores;
import com.sgd.entidades.Areas;
import com.sgd.entidades.Documentos;
import com.sgd.entidades.Entidades;
import com.sgd.entidades.Flujos;
import com.sgd.entidades.Procesos;
import com.sgd.entidades.Respuestas;
import com.sgd.entidades.Trackings;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "trackingsCP")
@ViewScoped
public class TrackingsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of TrackingsBean
     */
    @ManagedProperty(value = "#{seguridadBean}")
    protected SeguridadBean seguridadBean;
    @ManagedProperty(value = "#{procesosCP}")
    private ProcesosBean procesosBean;
    @ManagedProperty(value = "#{flujosCP}")
    private FlujosBean flujosBean;
//    @ManagedProperty(value = "#{personas}")
//    private PersonasBean usuariosBean;
    @ManagedProperty(value = "#{imagenesCompras}")
    private ImagenesBean imagenesBean;

    private LazyDataModel<Procesos> listaProcesos;
    private Procesos proceso;
    private Entidades usuario;
    private Respuestas respuesta;
    private Trackings tracking;
    private List<Trackings> listaTrackings;
    private Flujos flujo;
    private Flujos flujobuscar;
    private Areas areabuscar;
    private String codigo;
    private String areadenuncia;
    private String tipoingreso;
    private String denunciantebuscar;
    private String denunciadobuscar;
    private Date fechaingreso;
    private Resource imagenrs;
    private String descripcion;
    private Integer anio;

    private File file;
    private Formulario formularioAsignacion = new Formulario();
    private Formulario formularioRealizacion = new Formulario();
    private Formulario formularioHistorial = new Formulario();
    private Formulario formularioRespuesta = new Formulario();
    private Formulario formularioAnexos = new Formulario();

    private String direccion;

    @EJB
    private TrackingsFacade ejbTracking;
    @EJB
    private EntidadesFacade ejbUsuarios;
    @EJB
    private ProcesosFacade ejbProcesos;
    @EJB
    private FlujosFacade ejbFlujos;
    @EJB
    private RespuestasFacade ejbRespuestas;
    @EJB
    private CorreoFacade ejbCorreo;

    @PostConstruct
    public void idenficarDir() {
        Calendar act = Calendar.getInstance();
        act.setTime(new Date());
        anio = act.get(Calendar.YEAR);
    }

    public TrackingsBean() {
        listaProcesos = new LazyDataModel<Procesos>() {
            @Override
            public List<Procesos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);

            }
        };
    }

    public List<Procesos> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        if (seguridadBean.getEntidad() == null) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol especifico");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.responsable=:responsable and o.fechafin is null";
        parametros.put("responsable", seguridadBean.getEntidad());
        //Criterios de busqueda
        //CODIGO
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like:codigo";
            parametros.put("codigo", "%" + getCodigo().toUpperCase() + "%");
        }
        //FECHA INGRESO
        if (fechaingreso != null) {
            where += " and o.fechaingreso=:fechaingreso";
            parametros.put("fechaingreso", getFechaingreso());
        }

        //TIPO DE INGRESO
        if (!((tipoingreso == null) || (tipoingreso.isEmpty()))) {
            where += " and upper(o.tipoingreso) like:tipoingreso";
            parametros.put("tipoingreso", "%" + getTipoingreso().toUpperCase() + "%");
        }
        //AREA
        if (areabuscar != null) {
            where += " and o.areadenuncia=:areadenuncia";
            parametros.put("areadenuncia", areabuscar);
        }

        //ESTADO
        if (flujobuscar != null) {
            where += " and o.flujo=:flujo";
            parametros.put("flujo", flujobuscar);
        }
        //DESCRIPCION
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            where += " and upper(o.descripcion) like:descripcion";
            parametros.put("descripcion", "%" + getDescripcion().toUpperCase() + "%");
        }

        parametros.put(";where", where);
        String orden = "";
        if (scs.length == 0) {
            orden = "o.fechainicio desc";
        } else {
            orden += " o." + scs[0].getPropertyName() + (scs[0].isAscending() ? " ASC " : " DESC ");
        }
        parametros.put(";orden", orden);
        int total = 0;
        try {
            total = ejbProcesos.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }
        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        getListaProcesos().setRowCount(total);
        try {
            return ejbProcesos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        setListaProcesos(new LazyDataModel<Procesos>() {
            @Override
            public List<Procesos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        });
        return null;
    }

    public String nuevo() {
        setTracking(new Trackings());
        getFormularioAsignacion().insertar();
        getFormularioRealizacion().insertar();
        getFormularioHistorial().insertar();
        getFormularioRespuesta().insertar();
        return null;
    }

    public String asignar(Procesos p) {
        this.setProceso(p);
        getFormularioAsignacion().editar();
        Map parametros = new HashMap();
        parametros.put(";where", " o.proceso=:proceso");
        parametros.put("proceso", p);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaTrackings = ejbTracking.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //imagenesBean.setListaDocumentos(new LinkedList<Documentos>());
        imagenesBean.setProceso(proceso);
        imagenesBean.buscarDocumentos();
        tracking = new Trackings();
        return null;
    }

    public String registrar(Procesos p) {
        this.proceso = p;

        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.proceso=:proceso");
            parametros.put("proceso", p);
            listaTrackings = ejbTracking.encontarParametros(parametros);
            List<Respuestas> aux = ejbRespuestas.encontarParametros(parametros);
            if (aux.isEmpty()) {
                respuesta = new Respuestas();
                respuesta.setProceso(proceso);
            } else {
                respuesta = aux.get(0);
            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.proceso=:proceso");
//            parametros.put("proceso", p);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        imagenesBean.setListaDocumentos(new LinkedList<Documentos>());
        //respuestasBean.nuevo(p);
        imagenesBean.setRespuesta(respuesta);
        imagenesBean.buscarDocumentosR();
        tracking = new Trackings();
        getFormularioRespuesta().editar();
        return null;
    }

    public boolean validar() {
        if (tracking.getObservaciones() == null || tracking.getObservaciones().isEmpty()) {
            MensajesErrores.advertencia("Ingrese la observación de la actividad");
            return true;
        }
        if (proceso.getResponsable() == null) {
            MensajesErrores.advertencia("Seleccione funcionario a quién asignar");
            return true;
        }
        return false;
    }

    public String grabarAsignacion() {
        if (validar()) {
            return null;
        }
        try {
            tracking.setFecharealizacion(new Date());
            tracking.setProceso(proceso);
            tracking.setFlujo(proceso.getFlujo().getNombre());
            tracking.setFechaplanificacion(proceso.getFechaplanificacion());
            tracking.setUserid(getSeguridadBean().getUsr());
            tracking.setFecha(new Date());
            tracking.setResponsable(seguridadBean.getEntidad().toString());
            proceso.setFecharealizacion(new Date());
            proceso.setFlujo(proceso.getFlujo().getSiguiente());
            proceso.setFechaasignacion(new Date());
            //respuestasBean.getRespuesta().setProceso(proceso);
            Calendar fecha1 = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();
            Calendar inicio = Calendar.getInstance();
            int max = 0;
//            for (Anexos a : anexosBean.getListaAnexos()) {
//                if (a.getId() == null) {
//                    a.setDenuncia(denuncia);
//                    ejbAnexos.create(a, getSeguridadBean().getUsr());
//                } else {
//                    ejbAnexos.edit(a, getSeguridadBean().getUsr());
//                }
//            }
//            for (Anexos a : anexosBean.getListaAnexosb()) {
//                if (a.getId() != null) {
//                    ejbAnexos.remove(a, getSeguridadBean().getUsr());
//                }
//            }
            if (!(proceso.getFlujo().getFechamod()) && !(proceso.getArea().getCambiofecha())) {
                if (proceso.getFlujo().getSiguiente() != null) {
                    max = proceso.getFlujo().getDiasrecordatorio();
                    for (int c = 0; c < max; c++) {
                        inicio.add(Calendar.DATE, 1);
                        if ((inicio.get(Calendar.DAY_OF_WEEK) == 1) || (inicio.get(Calendar.DAY_OF_WEEK) == 7)) {
                            max++;
                        }
                    }
                }
                fecha1.setTime(proceso.getFechaasignacion());
                fecha1.add(Calendar.DAY_OF_YEAR, max);
                proceso.setFechaplanificacion(fecha1.getTime());
            }
            ejbProcesos.edit(proceso, seguridadBean.getUsr());
            imagenesBean.setProceso(proceso);
            imagenesBean.insertarDocumentos();
            ejbTracking.create(getTracking(), seguridadBean.getUsrLoggeado());

            String correo = "<p> <Strong>El Sistema de Gestión de Archivos le informa que:</Strong></p>";
            correo += "<p><Strong> El proceso de código: </Strong>" + proceso.getCodigo() + "</p>";
            correo += "<p><Strong> Se encuentra en estado: </Strong> " + proceso.getFlujo().getNombre() + "</p>";
            correo += "<p><Strong> Asignada a : </Strong> " + proceso.getResponsable().toString() + "</p>";
            correo += "<p><Strong> Funcionario con rol : </Strong> " + proceso.getFlujo().getRol().getNombre() + "</p>";
            //correo += "<p><Strong> Fue asignada con fecha: </Strong>" + d.getFechaasignacion() + "</p>";
            correo += "<p><Strong> Con fecha máxima de realización: </Strong>" + proceso.getFechaplanificacion() + "</p>";
            correo += "<p><Strong> Se solicita tener en cuenta el cumplimiento de dicho proceso con el fin de cumplir con los tiempos establecidos</Strong></p>";
            correo += "<p><Strong> Gracias</Strong></p>";
            correo += "<br><br><p><Strong> Atentamente </Strong></p>";
            correo += "<br><br><p>Texto generado automáticamente por el Sistema de Gestión de Archivos</p>";
            ejbCorreo.enviarCorreo(proceso.getResponsable().getEmail(), "Alerta automática para continuación de Procesos", correo);
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioAsignacion().cancelar();
        //buscar();
        return null;
    }

//GRABAR Y ENVIAR RESPUESTA
    public String grabarRespuesta() {
        if (respuesta.getNombreoficio() == null || respuesta.getNombreoficio().isEmpty()) {
            MensajesErrores.advertencia("Ingrese Nombre de Oficio");
            return null;
        }

//        if (respuestasBean.validar()) {
//            return null;
//        }
        try {
            //TEXTO PARA ENVIO DE CORREO
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            Date now = new Date();
//            DateFormat df1 = DateFormat.getDateInstance(DateFormat.SHORT);
//            String s = df1.format(now);
//            String correo = "<p> <Strong>El sistema de COmpras Públicas del CBDMQ le informa que se ha dado tratamiento al proceso :</Strong></p>";
//            correo += "<p><Strong> Solicitante:</Strong> </p>";
//            correo += "<p><Strong> Nombre:</Strong> " + denuncia.getDenunciante().getApellidos() + " " + denuncia.getDenunciante().getNombres() + "</p>";
//            correo += "<p><Strong> Teléfono: </Strong>" + denuncia.getDenunciante().getTelefono() + "</p>";
//            correo += "<p><Strong> Correo: </Strong>" + denuncia.getDenunciante().getCorreo() + "</p>";
//            correo += "<p><Strong> Dirección: </Strong></p>";
//            correo += "<p><Strong> Calle Principal: </Strong>" + denuncia.getDenunciante().getCalleprimaria() + "</p>";
//            correo += "<p><Strong> Calle Secundaria: </Strong>" + denuncia.getDenunciante().getCallesecundaria() + "</p>";
//            correo += "<p><Strong> Referencia: </Strong>" + denuncia.getDenunciante().getReferencia() + "</p>";
//            correo += "<p><Strong></Strong> </p>";
//            correo += "<p><Strong> Denunciado:</Strong> </p>";
//            correo += "<p><Strong> Nombre: </Strong>" + denuncia.getDenunciado().getNombre() + "</p>";
//            correo += "<p><Strong> Nombre Local: </Strong>" + denuncia.getDenunciado().getNombrelocal() + "</p>";
//            correo += "<p><Strong> Ubicación : </Strong></p>";
//            correo += "<p><Strong> Parroquia: </Strong>" + denuncia.getDenunciado().getParroquia() + "</p>";
//            correo += "<p><Strong> Zona: </Strong>" + denuncia.getDenunciado().getZona() + "</p>";
//            correo += "<p><Strong> Barrio: </Strong>" + denuncia.getDenunciado().getBarrio() + "</p>";
//            correo += "<p><Strong> Dirección: </Strong></p>";
//            correo += "<p><Strong> Calle Principal: </Strong>" + denuncia.getDenunciado().getCalleprimaria() + "</p>";
//            correo += "<p><Strong> Calle Secundaria: </Strong>" + denuncia.getDenunciado().getCallesecundaria() + "</p>";
//            correo += "<p><Strong> Referencia: </Strong>" + denuncia.getDenunciado().getReferencia() + "</p>";
//            
//            correo += "<p><Strong> Proceso:</Strong> </p>";
//            correo += "<p><Strong> Nro. Proceso: </Strong>" + proceso.getCodigo() + "</p>";
//            correo += "<p><Strong> Tipo de ingreso: </Strong>" + proceso.getTipoingreso() + "</p>";
//            correo += "<p><Strong> Fecha de ingreso: </Strong>" + proceso.getFechaingreso() + "</p>";
//            correo += "<p><Strong> Descripción: </Strong>" + proceso.getDescripcion() + "</p>";
//            correo += "<p><Strong> Respuesta:</Strong> </p>";
//            correo += "<p><Strong> Fecha de inicio: </Strong>" + proceso.getFechainicio() + "</p>";
//            correo += "<p><Strong> Nro. Oficio: </Strong>" + respuestasBean.getRespuesta().getNombreoficio() + "</p>";
//            correo += "<br><br><p><Strong> Atentamente CBDMQ</Strong></p>";
//            correo += "<br><br><p>Texto generado automáticamente por el Sistema de Compras Públicas del CBDMQ</p>";
            /*Codigo para adjuntar archivo y enviar por correo
            
             //            for (FileEntryResults.FileInfo i : respuestasBean.getFilee()) {
             //                file = i.getFile();
             //            }
             //            ejbCorreo.enviarCorreo(denuncia.getDenunciante().getCorreo(), "FIN de Trámite de Denuncia en CBDMQ", correo, file);
             */
            //ejbCorreo.enviarCorreo(proceso.getResponsable().getCorreo(), "CBDMQ : FIN de Proceso", correo);
            //getDenuncia().setResponsable(tracking.getResponsable());
            proceso.setFechafin(new Date());
            proceso.setResponsable(seguridadBean.getEntidad());
            ejbProcesos.edit(proceso, seguridadBean.getUsrLoggeado());
            ejbRespuestas.create(respuesta, seguridadBean.getUsrLoggeado());
            //respuestasBean.getRespuesta().setProceso(proceso);
            tracking.setProceso(proceso);
            tracking.setFlujo(proceso.getFlujo().getNombre());
            Calendar fecha1 = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy' / 'HH:mm:ss");
            fecha1.add(Calendar.DAY_OF_YEAR, proceso.getFlujo().getDiasrecordatorio());
            tracking.setFechaplanificacion(fecha1.getTime());
            tracking.setUserid(getSeguridadBean().getUsr());
            tracking.setFecha(new Date());
            tracking.setFecharealizacion(new Date());
            tracking.setResponsable(seguridadBean.getEntidad().toString());
            tracking.setObservaciones("Proceso " + proceso.getCodigo() + " se responde con oficio " + respuesta.getNombreoficio() + " el : " + format1.format(tracking.getFecharealizacion()));
            ejbTracking.create(tracking, seguridadBean.getUsrLoggeado());
            imagenesBean.setRespuesta(respuesta);
            imagenesBean.insertarDocumentos();
            //     } catch (InsertarException | GrabarException | MessagingException | UnsupportedEncodingException ex) {
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioRespuesta().cancelar();
        //buscar();
        return null;
    }

    public Boolean controlAccion(Flujos f) {
        this.setFlujo(f);
        Map parametros = new HashMap();
        List<Flujos> aux = new LinkedList<>();
        try {
            aux = ejbFlujos.encontarParametros(parametros);
            return flujo != null && flujo.getSiguiente() != null;
//            if (estado != null && estado.getEstadosiguiente() != null) {
////                if (estado.getActividad() == true) {
////                    return true;
////                } else {
////                    return false;
////                }
//                return true;
//            } else {
//                return false;
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean controlEnvio(Flujos f) {
        this.setFlujo(f);
        Map parametros = new HashMap();
        //List<Estados> aux = new LinkedList<>();
        List<Respuestas> resp = new LinkedList<>();
        try {
            resp = ejbRespuestas.encontarParametros(parametros);
            return flujo.getSiguiente() == null;
//            if (estado.getEstadosiguiente() == null) {
//                return true;
//            } else {
//                return false;
//            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean controlModFecha(Procesos p) {
        this.setProceso(p);
        //        if(denuncia.getAreadenuncia().getCambiofecha() && denuncia.getEstado().getFechamod()){
//            return true;
//            
//        }
//        return false;
        return proceso.getArea().getCambiofecha() && proceso.getFlujo().getFechamod();

    }

    public String historiar(Procesos p) {
        this.setProceso(p);
        getFormularioHistorial().editar();
        Map parametros = new HashMap();
        parametros.put(";where", " o.proceso=:proceso");
        parametros.put("proceso", p);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaTrackings = ejbTracking.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //buscar(denuncia);
        return null;
    }

    public SelectItem[] getComboFlujos(Areas a) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.anterior=:flujo and o.activo=true and o.area=:area");
        parametros.put("flujo", proceso.getFlujo());
        parametros.put("area", proceso.getArea());
        parametros.put(";orden", "o.nombre");
        try {
            List<Flujos> listaFlujos = ejbFlujos.encontarParametros(parametros);
            return Combos.SelectItems(listaFlujos, false);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboUsuarios() {
        Map parametros = new HashMap();
        parametros.put(";orden", " o.apellidos");
        //parametros.put(";where", " o.rolasig=:rolasig and o.direccion=:direccion");
        parametros.put(";where", " o.rolsistema=:rolasig and o.activo=true ");
        parametros.put("rolasig", proceso.getFlujo().getSiguiente().getRol());
        //parametros.put("direccion", direccion);
        List<Entidades> aux = null;
        try {
            aux = ejbUsuarios.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TrackingsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.SelectItems(aux, true);
    }

    /**
     * @return the tracking
     */
    public Trackings getTracking() {
        return tracking;
    }

    /**
     * @param tracking the tracking to set
     */
    public void setTracking(Trackings tracking) {
        this.tracking = tracking;
    }

    /**
     * @return the seguridad
     */
    public SeguridadBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean
     */
    public void setSeguridadBean(SeguridadBean seguridadBean) {
        this.seguridadBean = seguridadBean;
    }

    /**
     * @return the listaTrackings
     */
    public List<Trackings> getListaTrackings() {
        return listaTrackings;
    }

    /**
     * @param listaTrackings the listaTrackings to set
     */
    public void setListaTrackings(List<Trackings> listaTrackings) {
        this.listaTrackings = listaTrackings;
    }

    /**
     * @return the respuesta
     */
    public Respuestas getRespuesta() {
        return respuesta;
    }

    /**
     * @param respuesta the respuesta to set
     */
    public void setRespuesta(Respuestas respuesta) {
        this.respuesta = respuesta;
    }

//    /**
//     * @return the respuestasBean
//     */
//    public RespuestasBean getRespuestasBean() {
//        return respuestasBean;
//    }
//
//    /**
//     * @param respuestasBean the respuestasBean to set
//     */
//    public void setRespuestasBean(RespuestasBean respuestasBean) {
//        this.respuestasBean = respuestasBean;
//    }
    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the area
     */
    public String getAreadenuncia() {
        return areadenuncia;
    }

    /**
     * @param area the area to set
     */
    public void setAreadenuncia(String areadenuncia) {
        this.areadenuncia = areadenuncia;
    }

    /**
     * @return the tipoingreso
     */
    public String getTipoingreso() {
        return tipoingreso;
    }

    /**
     * @param tipoingreso the tipoingreso to set
     */
    public void setTipoingreso(String tipoingreso) {
        this.tipoingreso = tipoingreso;
    }

    /**
     * @return the denunciantebuscar
     */
    public String getDenunciantebuscar() {
        return denunciantebuscar;
    }

    /**
     * @param denunciantebuscar the denunciantebuscar to set
     */
    public void setDenunciantebuscar(String denunciantebuscar) {
        this.denunciantebuscar = denunciantebuscar;
    }

    /**
     * @return the denunciadobuscar
     */
    public String getDenunciadobuscar() {
        return denunciadobuscar;
    }

    /**
     * @param denunciadobuscar the denunciadobuscar to set
     */
    public void setDenunciadobuscar(String denunciadobuscar) {
        this.denunciadobuscar = denunciadobuscar;
    }

    /**
     * @return the fechaingreso
     */
    public Date getFechaingreso() {
        return fechaingreso;
    }

    /**
     * @param fechaingreso the fechaingreso to set
     */
    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    /**
     * @return the imagenrs
     */
    public Resource getImagenrs() {
        return imagenrs;
    }

    /**
     * @param imagenrs the imagenrs to set
     */
    public void setImagenrs(Resource imagenrs) {
        this.imagenrs = imagenrs;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the areabuscar
     */
    public Areas getAreabuscar() {
        return areabuscar;
    }

    /**
     * @param areabuscar the areabuscar to set
     */
    public void setAreabuscar(Areas areabuscar) {
        this.areabuscar = areabuscar;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the listaProcesos
     */
    public LazyDataModel<Procesos> getListaProcesos() {
        return listaProcesos;
    }

    /**
     * @param listaProcesos the listaProcesos to set
     */
    public void setListaProcesos(LazyDataModel<Procesos> listaProcesos) {
        this.listaProcesos = listaProcesos;
    }

    /**
     * @return the proceso
     */
    public Procesos getProceso() {
        return proceso;
    }

    /**
     * @param proceso the proceso to set
     */
    public void setProceso(Procesos proceso) {
        this.proceso = proceso;
    }

    /**
     * @return the usuario
     */
    public Entidades getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Entidades usuario) {
        this.usuario = usuario;
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
     * @return the flujobuscar
     */
    public Flujos getFlujobuscar() {
        return flujobuscar;
    }

    /**
     * @param flujobuscar the flujobuscar to set
     */
    public void setFlujobuscar(Flujos flujobuscar) {
        this.flujobuscar = flujobuscar;
    }

    /**
     * @return the procesosBean
     */
    public ProcesosBean getProcesosBean() {
        return procesosBean;
    }

    /**
     * @param procesosBean the procesosBean to set
     */
    public void setProcesosBean(ProcesosBean procesosBean) {
        this.procesosBean = procesosBean;
    }

    /**
     * @return the flujosBean
     */
    public FlujosBean getFlujosBean() {
        return flujosBean;
    }

    /**
     * @param flujosBean the flujosBean to set
     */
    public void setFlujosBean(FlujosBean flujosBean) {
        this.flujosBean = flujosBean;
    }

//    /**
//     * @return the usuariosBean
//     */
//    public PersonasBean getUsuariosBean() {
//        return usuariosBean;
//    }
//
//    /**
//     * @param usuariosBean the usuariosBean to set
//     */
//    public void setUsuariosBean(PersonasBean usuariosBean) {
//        this.usuariosBean = usuariosBean;
//    }
    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
    }

    /**
     * @return the formularioAsignacion
     */
    public Formulario getFormularioAsignacion() {
        return formularioAsignacion;
    }

    /**
     * @param formularioAsignacion the formularioAsignacion to set
     */
    public void setFormularioAsignacion(Formulario formularioAsignacion) {
        this.formularioAsignacion = formularioAsignacion;
    }

    /**
     * @return the formularioRealizacion
     */
    public Formulario getFormularioRealizacion() {
        return formularioRealizacion;
    }

    /**
     * @param formularioRealizacion the formularioRealizacion to set
     */
    public void setFormularioRealizacion(Formulario formularioRealizacion) {
        this.formularioRealizacion = formularioRealizacion;
    }

    /**
     * @return the formularioHistorial
     */
    public Formulario getFormularioHistorial() {
        return formularioHistorial;
    }

    /**
     * @param formularioHistorial the formularioHistorial to set
     */
    public void setFormularioHistorial(Formulario formularioHistorial) {
        this.formularioHistorial = formularioHistorial;
    }

    /**
     * @return the formularioRespuesta
     */
    public Formulario getFormularioRespuesta() {
        return formularioRespuesta;
    }

    /**
     * @param formularioRespuesta the formularioRespuesta to set
     */
    public void setFormularioRespuesta(Formulario formularioRespuesta) {
        this.formularioRespuesta = formularioRespuesta;
    }

    /**
     * @return the formularioAnexos
     */
    public Formulario getFormularioAnexos() {
        return formularioAnexos;
    }

    /**
     * @param formularioAnexos the formularioAnexos to set
     */
    public void setFormularioAnexos(Formulario formularioAnexos) {
        this.formularioAnexos = formularioAnexos;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public class MiRecurso implements com.icesoft.faces.context.Resource, Serializable {

        private static final long serialVersionUID = 1L;
        private final String resourceName;
        private InputStream inputStream;
        private final Date lastModified;
        private final ExternalContext extContext;
        private final byte[] byteArray;

        public MiRecurso(ExternalContext ec, String resourceName, byte[] byteArray) {
            this.extContext = ec;
            this.resourceName = resourceName;
            this.lastModified = new Date();
            this.byteArray = byteArray;
        }

        /**
         * This intermediate step of reading in the files from the JAR, into a
         * byte array, and then serving the Resource from the
         * ByteArrayInputStream, is not strictly necessary, but serves to
         * illustrate that the Resource content need not come from an actual
         * file, but can come from any source, and also be dynamically
         * generated. In most cases, applications need not provide their own
         * concrete implementations of Resource, but can instead simply make use
         * of com.icesoft.faces.context.ByteArrayResource,
         * com.icesoft.faces.context.FileResource,
         * com.icesoft.faces.context.JarResource.
         *
         * @throws java.io.IOException
         * @return tion
         */
        @Override
        public InputStream open() throws IOException {
            if (inputStream == null) {
                inputStream = new ByteArrayInputStream(byteArray);
            }
            return inputStream;
        }

        @Override
        public String calculateDigest() {
            return resourceName;
        }

        @Override
        public Date lastModified() {
            return lastModified;
        }

        @Override
        public void withOptions(com.icesoft.faces.context.Resource.Options arg0) throws IOException {
        }
    }
}
