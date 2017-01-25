/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.sistema;

import com.sdg.administracion.AreasBean;
import com.sdg.administracion.FlujosBean;
import com.sdg.excepciones.BorrarException;
import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.GrabarException;
import com.sdg.excepciones.InsertarException;
import com.sdg.seguridad.SeguridadBean;
import com.sdg.servicios.EntidadesFacade;
import com.sdg.servicios.FlujosFacade;
import com.sdg.servicios.ProcesosFacade;
import com.sdg.servicios.RespuestasFacade;
import com.sdg.servicios.TrackingsFacade;
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "procesosCP")
@ViewScoped
public class ProcesosBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of ProcesosBean
     */
    @ManagedProperty(value = "#{seguridadBean}")
    protected SeguridadBean seguridadBean;
    @ManagedProperty(value = "#{flujosCP}")
    private FlujosBean flujosBean;
    @ManagedProperty(value = "#{areasCP}")
    private AreasBean procedimientoBean;
//    @ManagedProperty(value = "#{personas}")
//    private PersonasBean usuariosBean;
    @ManagedProperty(value = "#{imagenesCompras}")
    private ImagenesBean imagenesBean;
    private List listadoProcesos;
    private LazyDataModel<Procesos> listaProcesos;
    private List<Flujos> listaFlujo;
    private List<Entidades> listaUsuarios;
    private List<Trackings> listaTrackings;
    private Procesos proceso;
    private Procesos procesobuscar;
    private Entidades denunciante;
    private Entidades denunciado;
    private Respuestas respuesta;
    private Flujos flujo;
    private Flujos flujobuscar;
    private Areas areabuscar;
    private String codigo;
    private String area;
    private String tipoingreso;
    private String denunciantebuscar;
    private String denunciadobuscar;
    private Date fechaingreso;
    private Date fechainicio;
    private Boolean mostrar = false;
    private Formulario formulario = new Formulario();
    private Formulario formularioHistorial = new Formulario();
    private Formulario formularioRespuesta = new Formulario();
    private String codigosolicitud;
    private String descripcion;
    //private String direccion;
    private Integer anio;
    //private String subact;
    //private String producto;
    
    @EJB
    private FlujosFacade ejbFlujos;
    @EJB
    private ProcesosFacade ejbProcesos;
    @EJB
    private TrackingsFacade ejbTrackings;
    @EJB
    private RespuestasFacade ejbRespuestas;
    @EJB
    private EntidadesFacade ejbUsuarios;

    @PostConstruct
    public void idenficarDireccion() {
        Calendar act = Calendar.getInstance();
        act.setTime(new Date());
        anio = act.get(Calendar.YEAR);
    }

    public ProcesosBean() {
        listaProcesos = new LazyDataModel<Procesos>() {
            @Override
            public List<Procesos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
    }

    public List<Procesos> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

        if (seguridadBean.getEntidad().getRolsistema() == null) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }
        if (controlInsertar()) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }
        //Flujos flujoinicial = new Flujos();
        try {
            Map parametros = new HashMap();
            String where = "o.fechaingreso is not null  ";
            //Criterios de busqueda
            //CODIGO
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.codigo) like:codigo";
                parametros.put("codigo", "%" + getCodigo().toUpperCase() + "%");
            }

//            //DIRECCION
//            if (direccion != null) {
//                where += " and o.direccion=:direccion";
//                parametros.put("direccion", direccion);
//            }
            //FECHA INICIO
            if (fechainicio != null) {
                where += " and o.fechainicio=:fechainicio";
                parametros.put("fechainicio", getFechainicio());
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

            //Procedimiento
            if (areabuscar != null) {
                where += " and o.area=:area";
                parametros.put("area", areabuscar);
            }

            //FLUJO
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
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listaProcesos.setRowCount(total);
            return ejbProcesos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (areabuscar == null) {
            MensajesErrores.advertencia("Seleccione un procedimiento");
            return null;
        }
        setListaProcesos(new LazyDataModel<Procesos>() {
            @Override
            public List<Procesos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        });
        return null;
    }

    public String nuevo() {
        if (seguridadBean.getEntidad().getRolsistema() == null) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }
        if (anio == null) {
            MensajesErrores.advertencia("Indique el año del proceso");
            return null;
        }
        if (areabuscar == null) {
            MensajesErrores.advertencia("Seleccione un procedimiento");
            return null;
        }
        if (controlInsertar()) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }
        proceso = new Procesos();
        proceso.setCodigo(nombreProceso());
        proceso.setFechaingreso(new Date());
        proceso.setIndicadorna(Boolean.TRUE);
        proceso.setArea(areabuscar);
        //proceso.setDireccion(direccion);
        proceso.setAnio(anio);
        imagenesBean.setListaDocumentos(new LinkedList<Documentos>());
        getFormulario().insertar();
        return null;
    }

    public String modificar() {
        proceso = (Procesos) listaProcesos.getRowData();
        //producto = proceso.getNombresubact();
        imagenesBean.setProceso(proceso);
        imagenesBean.buscarDocumentos();
        formulario.editar();
        return null;
    }

    public String eliminar() {
        proceso = (Procesos) listaProcesos.getRowData();
        formulario.eliminar();
        return null;
    }

    public boolean validar() {

        //DATOS PROCESO
        if (proceso.getTipoingreso() == null || proceso.getTipoingreso().isEmpty()) {
            MensajesErrores.advertencia("Seleccione Tipo de Ingreso");
            return true;
        }
        if (proceso.getArea().getNombre() == null || proceso.getArea().getNombre().isEmpty()) {
            MensajesErrores.advertencia("Seleccione Procedimiento");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
//            parametros.put(";where", " o.anio=:anio");
//            parametros.put("anio", anio);
////            parametros.put("producto", producto);
//            int a = ejbProcesos.contar(parametros);
//            if (a > 0) {
//                MensajesErrores.advertencia("Ya existe un proceso para el Producto seleccionado");
//                return null;
//            }
            //proceso.setNombresubact(producto);
            proceso.setFlujo(flujo);
            proceso.setFechainicio(new Date());
            proceso.setFecharealizacion(new Date());
            Calendar fecha1 = Calendar.getInstance();
            fecha1.add(Calendar.DAY_OF_YEAR, proceso.getFlujo().getDiasrecordatorio());
            proceso.setFechaplanificacion(fecha1.getTime());
            proceso.setResponsable(seguridadBean.getEntidad());
            ejbProcesos.create(proceso, seguridadBean.getUsrLoggeado());
            imagenesBean.setProceso(proceso);
            imagenesBean.insertarDocumentos();
            Trackings tracking = new Trackings();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy'/'HH:mm:ss");
            tracking.setFechaplanificacion(fecha1.getTime());
            tracking.setFecha(new Date());
            tracking.setProceso(proceso);
            tracking.setFlujo(proceso.getFlujo().getNombre());
            tracking.setObservaciones("Proceso " + proceso.getCodigo() + " ingresada el : " + format1.format(proceso.getFechainicio()));
            tracking.setUserid(seguridadBean.getUsrLoggeado());
            tracking.setResponsable(seguridadBean.getUsrLoggeado());
            ejbTrackings.create(tracking, getSeguridadBean().getUsrLoggeado());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        areabuscar = proceso.getArea();
        buscar();
        return null;
    }

    public String grabar() {
        try {
//            if (!producto.equals(proceso.getNombresubact())) {
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.nombresubact=:producto and o.anio=:anio");
//                parametros.put("anio", anio);
//                parametros.put("producto", producto);
//                int a = ejbProcesos.contar(parametros);
//                if (a > 0) {
//                    MensajesErrores.advertencia("Ya existe un proceso para esa Subactividad");
//                    return null;
//                }
//            }
//            proceso.setNombresubact(producto);
            ejbProcesos.edit(proceso, seguridadBean.getUsrLoggeado());
            imagenesBean.setProceso(proceso);
            imagenesBean.insertarDocumentos();
            Trackings tracking = new Trackings();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy'/'HH:mm:ss");
            tracking.setFecharealizacion(new Date());
            Calendar fecha1 = Calendar.getInstance();
            fecha1.add(Calendar.DAY_OF_YEAR, proceso.getFlujo().getDiasrecordatorio());
            tracking.setFechaplanificacion(fecha1.getTime());
            tracking.setFecha(new Date());
            tracking.setProceso(proceso);
            tracking.setFlujo(proceso.getFlujo().getNombre());
            tracking.setObservaciones("Proceso " + proceso.getCodigo() + " modificada el : " + format1.format(tracking.getFecha()));
            tracking.setUserid(getSeguridadBean().getUsr());
            tracking.setResponsable(seguridadBean.getUsrLoggeado());
            ejbTrackings.create(tracking, getSeguridadBean().getUsrLoggeado());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        areabuscar = proceso.getArea();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbProcesos.remove(proceso, seguridadBean.getUsrLoggeado());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public Boolean controlVerDocumento(Procesos pro) {
        Map parametro = new HashMap();
        parametro.put(";where", "o.proceso=:proceso");
        parametro.put("proceso", pro);
        List<Respuestas> listaRespuestas;
        try {
            listaRespuestas = ejbRespuestas.encontarParametros(parametro);
            return !listaRespuestas.isEmpty();
        } catch (ConsultarException ex) {
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String traerRespuesta(Procesos pro) {

        try {
            Map parametro = new HashMap();
            parametro.put(";where", "o.proceso=:proceso");
            parametro.put("proceso", pro);
            List<Respuestas> listaRespuestas = ejbRespuestas.encontarParametros(parametro);
            if (listaRespuestas.isEmpty()) {
                MensajesErrores.advertencia("No se registra ninguna respuesta");
                return null;
            } else {
                respuesta = listaRespuestas.get(0);
                imagenesBean.setRespuesta(respuesta);
                imagenesBean.buscarDocumentosR();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRespuesta.editar();
        return null;
    }

    public Boolean controlInsertar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.area=:area and o.anterior is null");
            parametros.put("area", areabuscar);
            List<Flujos> aux = ejbFlujos.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                flujo = aux.get(0);
                return !flujo.getRol().equals(seguridadBean.getEntidad().getRolsistema());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Procesos traer(Integer id) throws ConsultarException {
        return ejbProcesos.find(id);
    }

    public String nombreProceso() {
        Integer nro = 0;
        codigosolicitud = new String();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id is not null");
            parametros.put(";orden", "o.id desc");
            List<Procesos> aux = ejbProcesos.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                nro = aux.get(0).getId();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ProcesosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        codigosolicitud = "PROCESO-" + (nro + 1);
        return codigosolicitud;
    }

    
        
    //SALTO DE LINEA EN TEXTO
    public List<String> getListaTextoImp(String tex) {
        List<String> textos = new LinkedList<>();
        String aux[] = tex.split("\n");
        textos.addAll(Arrays.asList(aux));
        return textos;
    }
    //RESPUESTAS
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
    public String getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(String area) {
        this.area = area;
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
     * @return the fechainicio
     */
    public Date getFechainicio() {
        return fechainicio;
    }

    /**
     * @param fechainicio the fechainicio to set
     */
    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
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

    /**
     * @return the mostrar
     */
    public Boolean getMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
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
     * @return the codigosolicitud
     */
    public String getCodigosolicitud() {
        return codigosolicitud;
    }

    /**
     * @param codigosolicitud the codigosolicitud to set
     */
    public void setCodigosolicitud(String codigosolicitud) {
        this.codigosolicitud = codigosolicitud;
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
     * @return the listadoProcesos
     */
    public List getListadoProcesos() {
        return listadoProcesos;
    }

    /**
     * @param listadoProcesos the listadoProcesos to set
     */
    public void setListadoProcesos(List listadoProcesos) {
        this.listadoProcesos = listadoProcesos;
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
     * @return the listaFlujo
     */
    public List<Flujos> getListaFlujo() {
        return listaFlujo;
    }

    /**
     * @param listaFlujo the listaFlujo to set
     */
    public void setListaFlujo(List<Flujos> listaFlujo) {
        this.listaFlujo = listaFlujo;
    }

    /**
     * @return the listaUsuarios
     */
    public List<Entidades> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List<Entidades> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
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
     * @return the procesobuscar
     */
    public Procesos getProcesobuscar() {
        return procesobuscar;
    }

    /**
     * @param procesobuscar the procesobuscar to set
     */
    public void setProcesobuscar(Procesos procesobuscar) {
        this.procesobuscar = procesobuscar;
    }

    /**
     * @return the denunciante
     */
    public Entidades getDenunciante() {
        return denunciante;
    }

    /**
     * @param denunciante the denunciante to set
     */
    public void setDenunciante(Entidades denunciante) {
        this.denunciante = denunciante;
    }

    /**
     * @return the denunciado
     */
    public Entidades getDenunciado() {
        return denunciado;
    }

    /**
     * @param denunciado the denunciado to set
     */
    public void setDenunciado(Entidades denunciado) {
        this.denunciado = denunciado;
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
     * @return the procedimientoBean
     */
    public AreasBean getProcedimientoBean() {
        return procedimientoBean;
    }

    /**
     * @param procedimientoBean the procedimientoBean to set
     */
    public void setProcedimientoBean(AreasBean procedimientoBean) {
        this.procedimientoBean = procedimientoBean;
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

//    /**
//     * @return the direccion
//     */
//    public String getDireccion() {
//        return direccion;
//    }
//
//    /**
//     * @param direccion the direccion to set
//     */
//    public void setDireccion(String direccion) {
//        this.direccion = direccion;
//    }

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

//    /**
//     * @return the subact
//     */
//    public String getSubact() {
//        return subact;
//    }
//
//    /**
//     * @param subact the subact to set
//     */
//    public void setSubact(String subact) {
//        this.subact = subact;
//    }

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

//    /**
//     * @return the producto
//     */
//    public String getProducto() {
//        return producto;
//    }
//
//    /**
//     * @param producto the producto to set
//     */
//    public void setProducto(String producto) {
//        this.producto = producto;
//    }

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
}
