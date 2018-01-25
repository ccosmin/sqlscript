package com.snowlinesoftware.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SQLScriptOnJPATest {
    private EntityManager entityManager;

    @Before
    public void setup() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sqlscript-test");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @After
    public void teardown() {
        if ( entityManager != null ) {
            entityManager.close();
        }
    }

    @Test
    public void can_execute_script_on_entity_manager() {

    }
}
