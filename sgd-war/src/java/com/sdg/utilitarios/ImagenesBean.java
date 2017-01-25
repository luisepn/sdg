/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.utilitarios;

import com.sdg.excepciones.BorrarException;
import com.sdg.excepciones.ConsultarException;
import com.sdg.excepciones.GrabarException;
import com.sdg.excepciones.InsertarException;
import com.sdg.servicios.DocumentosFacade;
import com.sgd.entidades.Documentos;
import com.sgd.entidades.Procesos;
import com.sgd.entidades.Respuestas;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;

/**
 *
 * @author Luis Fernando Ordóñez Armijos
 * @fecha Quito, 19 de diciembre de 2016
 * @hora 16:53:15
 * @descripción Este Bean administrará todos los documentos o imagenes que el
 * sistema permita ingresar, almacenadolos en un directorio del sistema de
 * archivos que tenga disponible el servidor de aplicaciones. Dicho directorio
 * puede ser parametrizado Códigos: Maestro = Directorio de
 * Imágenes; Parámetros = /root/directorioSeleccionado;
 *
 */
@ManagedBean(name = "imagenesCompras")
@ViewScoped
public class ImagenesBean {

    private static String path;
    private Recurso recurso;
    private Documentos documento;
    private String nombreArchivo;
    private String tipoArchivo;
    private Formulario formulario = new Formulario();

    private Procesos proceso;
    private Respuestas respuesta;
    private byte[] archivo;

    private List<Documentos> listaDocumentos;
    private List<Documentos> listaDocumentosR;

    @EJB
    private DocumentosFacade ejbDocumentos;

    public ImagenesBean() {
    }

    /**
     * Busca documentos de un proyecto o una subactividad
     *
     * @return
     */
    public String buscarDocumentos() {
        try {
            Map parametros = new HashMap();
            if (proceso != null) {
                parametros.put(";where", "o.proceso=:proceso");
                parametros.put("proceso", proceso);
            }
            if (respuesta != null) {
                parametros.put(";where", "o.respuesta=:respuesta");
                parametros.put("respuesta", respuesta);
            }
            listaDocumentos = ejbDocumentos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarDocumentosR() {
        try {
            Map parametros = new HashMap();
            if (respuesta != null) {
                parametros.put(";where", "o.respuesta=:respuesta");
                parametros.put("respuesta", respuesta);
            }
            listaDocumentosR = ejbDocumentos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ficheroDocumento(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                Documentos d = new Documentos();
                d.setProceso(proceso);
                d.setRespuesta(respuesta);
                d.setNombrearchivo(i.getFileName());
                d.setTipo(i.getContentType());
                ejbDocumentos.create(d, "Documentos Procesos");
                d.setPath(crearFichero(d.getId(), Files.readAllBytes(file.toPath())));
                ejbDocumentos.edit(d, "Documentos Procesos");
                buscarDocumentos();
            } catch (IOException | InsertarException | GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String ficheroDocumentoLista(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();

        for (FileEntryResults.FileInfo i : results.getFiles()) {
            Documentos d = new Documentos();
            d.setProceso(proceso);
            d.setNombrearchivo(i.getFileName());
            d.setPath(i.getFile().getAbsolutePath());
            d.setTipo(i.getContentType());
            listaDocumentos.add(d);
        }
        return null;
    }

    public void insertarDocumentos() throws InsertarException {
        try {
            for (Documentos d : listaDocumentos) {
                if (d.getId() == null) {
                    ejbDocumentos.create(d, "Documentos Procesos");
                    d.setProceso(proceso);
                    d.setRespuesta(respuesta);
                    File file = new File(d.getPath());
                    d.setPath(crearFichero(d.getId(), Files.readAllBytes(file.toPath())));
                    ejbDocumentos.edit(d, "Documentos Procesos");
                }
            }
        } catch (IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String borrarDocumento(Documentos d) {
        if (formulario.getFila() == null) {
            documento = d;
        } else {
            documento = listaDocumentos.get(formulario.getFila().getRowIndex());
            formulario.setIndiceFila(formulario.getFila().getRowIndex());
        }
        formulario.eliminar();
        return null;
    }

    public String eliminarDocumento() {
        if (formulario.getFila() == null) {
            borrarFichero(documento.getPath());
            try {
                ejbDocumentos.remove(documento, "Documentos Proyectos");
                buscarDocumentos();
            } catch (BorrarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            listaDocumentos.remove((int) formulario.getIndiceFila());
        }
        formulario.cancelar();
        return null;
    }

    public String crearFichero(Integer id, byte[] archivo) throws IOException {
        if (path == null) {
            try {
                path = ejbDocumentos.traerCodigo("DIRIMG", "DIRIMG", null).getParametros();
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File folder = new File(path + "/compraspublicas");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File fichero = new File(folder.getAbsolutePath() + "/" + id);
        fichero.createNewFile();

        try (OutputStream out = new FileOutputStream(fichero.getCanonicalPath())) {
            out.write(archivo);
        }
        return fichero.getCanonicalPath();
    }

    private void borrarFichero(String path) {
        if (path != null) {
            File fichero = new File(path);
            fichero.delete();
        }
    }

    public String traerImagen(String path, String nombre, String tipo) {
        try {
            tipoArchivo = tipo;
            nombreArchivo = nombre;
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            recurso = new Recurso(
                    context,
                    nombreArchivo,
                    Files.readAllBytes(Paths.get(path)));
            formulario.insertar();
        } catch (IOException ex) {
            MensajesErrores.fatal("El archivo no existe");
            return null;
        }
        return null;

    }

    public List<Documentos> traerListaDocumentos(List<Procesos> procesos) throws ConsultarException {
        List<Documentos> retorno = new LinkedList<>();

        for (Procesos p : procesos) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.procesos=:procesos");
            parametros.put("procesos", p);
            List<Documentos> aux = ejbDocumentos.encontarParametros(parametros);
            for (Documentos d : aux) {
                retorno.add(d);
            }
        }
        return retorno;
    }

    public List<Documentos> traerListaDocumentosRes(List<Respuestas> respuestas) throws ConsultarException {
        List<Documentos> retorno = new LinkedList<>();

        for (Respuestas p : respuestas) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.respuesta=:respuesta");
            parametros.put("respuesta", p);
            List<Documentos> aux = ejbDocumentos.encontarParametros(parametros);
            for (Documentos d : aux) {
                retorno.add(d);
            }
        }
        return retorno;
    }

    /**
     * @return the recurso
     */
    public Recurso getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
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
     * @return the archivo
     */
    public byte[] getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    /**
     * @return the listaDocumentos
     */
    public List<Documentos> getListaDocumentos() {
        return listaDocumentos;
    }

    /**
     * @param listaDocumentos the listaDocumentos to set
     */
    public void setListaDocumentos(List<Documentos> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
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
     * @return the listaDocumentosR
     */
    public List<Documentos> getListaDocumentosR() {
        return listaDocumentosR;
    }

    /**
     * @param listaDocumentosR the listaDocumentosR to set
     */
    public void setListaDocumentosR(List<Documentos> listaDocumentosR) {
        this.listaDocumentosR = listaDocumentosR;
    }
    /**
     * Creates a new instance of ImagenesBean
     */
//    public ImagenesBean() {
//    }
//
//    private Imagenes adjunto;
//    private Archivos archivo;
//    private List<Imagenes> imagenes;
//    private Resource archivoAnexo;
//    private Integer idModulo;
//    private Formulario formularioAdjunto = new Formulario();
//    private String modulo;
//    private String nombreArchivo;
//    private String tipoArchivo;
//    @EJB
//    private ImagenesFacade ejbimagenes;
//    @EJB
//    private ArchivosFacade ejbArchivo;
//
//    /**
//     * @return the adjunto
//     */
//    public Imagenes getAdjunto() {
//        return adjunto;
//    }
//
//    /**
//     * @param adjunto the adjunto to set
//     */
//    public void setAdjunto(Imagenes adjunto) {
//        this.adjunto = adjunto;
//    }
//
//    /**
//     * @return the imagenes
//     */
//    public List<Imagenes> getImagenes() {
//        return imagenes;
//    }
//
//    /**
//     * @param imagenes the imagenes to set
//     */
//    public void setImagenes(List<Imagenes> imagenes) {
//        this.imagenes = imagenes;
//    }
//
//    /**
//     * @return the archivoAnexo
//     */
//    public Resource getArchivoAnexo() {
//        return archivoAnexo;
//    }
//
//    /**
//     * @param archivoAnexo the archivoAnexo to set
//     */
//    public void setArchivoAnexo(Resource archivoAnexo) {
//        this.archivoAnexo = archivoAnexo;
//    }
//
//    /**
//     * @return the idModulo
//     */
//    public Integer getIdModulo() {
//        return idModulo;
//    }
//
//    /**
//     * @param idModulo the idModulo to set
//     */
//    public void setIdModulo(Integer idModulo) {
//        this.idModulo = idModulo;
//    }
//
//    // primero buscar
//    public void Buscar() {
//        if (idModulo == null) {
//            MensajesErrores.advertencia("Necesario clave de módulo");
//            return;
//        }
//        if (getModulo() == null) {
//            MensajesErrores.advertencia("Necesario módulo");
//            return;
//        }
//        //adjuntos
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.modulo=:modulo and o.idmodulo=:idmodulo");
//        parametros.put("idmodulo", idModulo);
//        parametros.put("modulo", getModulo());
//        imagenes = ejbimagenes.encontarParametros(parametros);
//    }
//
//    public String imagenListener(FileEntryEvent e) {
//        if (idModulo == null) {
//            MensajesErrores.advertencia("Necesario clave de módulo");
//            return null;
//        }
//        if (getModulo() == null) {
//            MensajesErrores.advertencia("Necesario módulo");
//            return null;
//        }
//        FileEntry fe = (FileEntry) e.getComponent();
//        FileEntryResults results = fe.getResults();
//        for (FileEntryResults.FileInfo i : results.getFiles()) {
//            try {
//                File file = i.getFile();
//                Archivos archImagen = new Archivos();
//                Imagenes img = new Imagenes();
//                archImagen.setArchivo(Files.readAllBytes(file.toPath()));
//                img.setNombre(i.getFileName());
//                img.setIdmodulo(idModulo);
//                img.setModulo(getModulo());
//                img.setFechaingreso(new Date());
//                img.setTipo(i.getContentType());
//                ejbimagenes.create(img);
//                archImagen.setImagen(img);
//                ejbArchivo.create(archImagen);
//                if (imagenes == null) {
//                    imagenes = new LinkedList<>();
//                }
//                imagenes.add(img);
//            } catch (IOException ex) {
//                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
//
//    public String verImagen(Imagenes imagen) {
//        Calendar c = Calendar.getInstance();
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.imagen=:imagen");
//        parametros.put("imagen", imagen);
//        Archivos aImagen = null;
//        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
//        for (Archivos a : larch) {
//            aImagen = a;
//        }
//        tipoArchivo = imagen.getTipo();
//        nombreArchivo = c.getTimeInMillis()+""+imagen.getNombre();
//        archivoAnexo = new anexoRecurso(ec, imagen.getNombre(), aImagen.getArchivo());
//        formularioAdjunto.insertar();
//        return null;
//    }
//
//    public String borrarImagen(Imagenes imagen) {
//        adjunto = imagen;
//        formularioAdjunto.borrar();
//        return null;
//    }
//
//    public String eliminar() {
//        ejbimagenes.remove(adjunto);
//        formularioAdjunto.cancelar();
//        Buscar();
//        return null;
//    }
//
//    public String imagenListener2(FileEntryEvent e) {
//        FileEntry fe = (FileEntry) e.getComponent();
//        FileEntryResults results = fe.getResults();
//        for (FileEntryResults.FileInfo i : results.getFiles()) {
//            try {
//                File file = i.getFile();
//                archivo = new Archivos();
//                adjunto = new Imagenes();
//                archivo.setArchivo(Files.readAllBytes(file.toPath()));
//                adjunto.setNombre(i.getFileName());
//                adjunto.setFechaingreso(new Date());
//                adjunto.setTipo(i.getContentType());
//            } catch (IOException ex) {
//                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
//
//    public String subirImagen(String modulo, Integer idmodulo) {
//        if (archivo == null || archivo.getArchivo() == null) {
//            return null;
//        }
//        Imagenes imagen = obtenerImagen(modulo, idmodulo);
//        if (imagen != null) {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.imagen=:imagen");
//            parametros.put("imagen", imagen);
//            Archivos aImagen = null;
//            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
//            for (Archivos a : larch) {
//                aImagen = a;
//            }
//            imagen.setNombre(adjunto.getNombre());
//            imagen.setFechaingreso(adjunto.getFechaingreso());
//            imagen.setTipo(adjunto.getTipo());
//            ejbimagenes.edit(imagen);
//            aImagen.setArchivo(archivo.getArchivo());
//            ejbArchivo.edit(aImagen);
//        } else {
//            adjunto.setIdmodulo(idmodulo);
//            adjunto.setModulo(modulo);
//            ejbimagenes.create(adjunto);
//            archivo.setImagen(adjunto);
//            ejbArchivo.create(archivo);
//        }
//        return null;
//    }
//
//    public Imagenes obtenerImagen(String modulo, Integer idmodulo) {
//        List<Imagenes> auximg = new LinkedList<>();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.modulo=:modulo and o.idmodulo=:idmodulo");
//        parametros.put("idmodulo", idmodulo);
//        parametros.put("modulo", modulo);
//        auximg = ejbimagenes.encontarParametros(parametros);
//        if (auximg != null && !auximg.isEmpty()) {
//            return auximg.get(0);
//        }
//        return null;
//    };
//    
//    public boolean existeImagen(String modulo, Integer idmodulo) {
//        List<Imagenes> auximg = new LinkedList<>();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.modulo=:modulo and o.idmodulo=:idmodulo");
//        parametros.put("idmodulo", idmodulo);
//        parametros.put("modulo", modulo);
//        auximg = ejbimagenes.encontarParametros(parametros);
//        if (auximg != null && !auximg.isEmpty()) {
//            return true;
//        }
//        return false;
//    };
//    
//    
//    /**
//     * @return the formularioAdjunto
//     */
//    public Formulario getFormularioAdjunto() {
//        return formularioAdjunto;
//    }
//
//    /**
//     * @param formularioAdjunto the formularioAdjunto to set
//     */
//    public void setFormularioAdjunto(Formulario formularioAdjunto) {
//        this.formularioAdjunto = formularioAdjunto;
//    }
//
//    /**
//     * @return the modulo
//     */
//    public String getModulo() {
//        return modulo;
//    }
//
//    /**
//     * @param modulo the modulo to set
//     */
//    public void setModulo(String modulo) {
//        this.modulo = modulo;
//    }
//
//    /**
//     * @return the nombreArchivo
//     */
//    public String getNombreArchivo() {
//        return nombreArchivo;
//    }
//
//    /**
//     * @param nombreArchivo the nombreArchivo to set
//     */
//    public void setNombreArchivo(String nombreArchivo) {
//        this.nombreArchivo = nombreArchivo;
//    }
//
//    /**
//     * @return the tipoArchivo
//     */
//    public String getTipoArchivo() {
//        return tipoArchivo;
//    }
//
//    /**
//     * @param tipoArchivo the tipoArchivo to set
//     */
//    public void setTipoArchivo(String tipoArchivo) {
//        this.tipoArchivo = tipoArchivo;
//    }
//
//    /**
//     * @return the archivo
//     */
//    public Archivos getArchivo() {
//        return archivo;
//    }
//
//    /**
//     * @param archivo the archivo to set
//     */
//    public void setArchivo(Archivos archivo) {
//        this.archivo = archivo;
//    }
//
//    public class anexoRecurso implements com.icesoft.faces.context.Resource, Serializable {
//
//        private static final long serialVersionUID = 1L;
//        private final String resourceName;
//        private InputStream inputStream;
//        private final Date lastModified;
//        private final ExternalContext extContext;
//        private final byte[] byteArray;
//
//        public anexoRecurso(ExternalContext ec, String resourceName, byte[] byteArray) {
//            this.extContext = ec;
//            this.resourceName = resourceName;
//            this.lastModified = new Date();
//            this.byteArray = byteArray;
//        }
//
//        /**
//         * This intermediate step of reading in the files from the JAR, into a
//         * byte array, and then serving the Resource from the
//         * ByteArrayInputStream, is not strictly necessary, but serves to
//         * illustrate that the Resource content need not come from an actual
//         * file, but can come from any source, and also be dynamically
//         * generated. In most cases, applications need not provide their own
//         * concrete implementations of Resource, but can instead simply make use
//         * of com.icesoft.faces.context.ByteArrayResource,
//         * com.icesoft.faces.context.FileResource,
//         * com.icesoft.faces.context.JarResource.
//         *
//         * @throws java.io.IOException
//         * @return tion
//         */
//        @Override
//        public InputStream open() throws IOException {
//            if (inputStream == null) {
//                inputStream = new ByteArrayInputStream(byteArray);
//            }
//            return inputStream;
//        }
//
//        @Override
//        public String calculateDigest() {
//            return resourceName;
//        }
//
//        @Override
//        public Date lastModified() {
//            return lastModified;
//        }
//
//        @Override
//        public void withOptions(com.icesoft.faces.context.Resource.Options arg0) throws IOException {
//        }
//    }
}
