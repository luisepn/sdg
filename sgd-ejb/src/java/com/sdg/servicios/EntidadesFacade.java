/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.servicios;

import com.sdg.excepciones.ConsultarException;
import com.sgd.entidades.Entidades;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class EntidadesFacade extends AbstractFacade<Entidades> {

    @PersistenceContext(unitName = "sgd-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadesFacade() {
        super(Entidades.class);
    }

    public Entidades login(String usuario, String clave)
            throws ConsultarException {

        Map parametros = new HashMap();
        parametros.put(";where", "o.userid=:usuario and o.pwd=:password");
        parametros.put("usuario", usuario);
        parametros.put("password", clave);
        List<Entidades> lista = super.encontarParametros(parametros);
        if ((lista == null) || (lista.isEmpty())) {
            return null;
        }
        return lista.get(0);
    }

}
