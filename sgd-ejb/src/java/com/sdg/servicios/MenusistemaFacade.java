/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.servicios;

import com.sgd.entidades.Menusistema;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author limon
 */
@Stateless
public class MenusistemaFacade extends AbstractFacade<Menusistema> {
    @PersistenceContext(unitName = "sinvent-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MenusistemaFacade() {
        super(Menusistema.class);
    }
    
}
