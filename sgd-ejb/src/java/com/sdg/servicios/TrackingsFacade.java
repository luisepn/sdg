/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.servicios;

import com.sgd.entidades.Trackings;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class TrackingsFacade extends AbstractFacade<Trackings> {
    @PersistenceContext(unitName = "sgd-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackingsFacade() {
        super(Trackings.class);
    }
    
}
