package br.com.wave.project.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.wave.project.core.entities.EntidadeBasic;
import br.com.wave.repository.core.Keeper;
import br.com.wave.repository.enums.RemoveEnum;
import br.com.wave.repository.exceptions.RepositoryException;

public class KeeperTest {

	private EntityManager manager;

	private EntityTransaction transaction;

	private Keeper keeper;

	@Before
	public void setUp() {
		WeldContainer container = new Weld().initialize();
		this.keeper = container.instance().select(Keeper.class).get();

		this.manager = container.instance().select(EntityManager.class).get();
		this.transaction = this.manager.getTransaction();
		this.transaction.begin();
	}

	@Test
	public void devePersistirUmaInstanciaEmUmRepositorio() throws RepositoryException {
		EntidadeBasic instance = new EntidadeBasic();

		this.keeper.persist(instance);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));
	}

	@Test
	public void deveAlterarUmaInstanciaEmUmRepositorio() throws RepositoryException {
		EntidadeBasic instance = new EntidadeBasic();

		this.keeper.persist(instance);
		instance.setStringField("Teste");

		// this.keeper.persist(instance);
		System.out.println("Id: " + instance.getId());
		
		EntidadeBasic novaInstancia = this.manager.find(EntidadeBasic.class, instance.getId());
		assertEquals("Teste", novaInstancia.getStringField());

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));
	}

	@Test
	public void deveRemoverDeFormaLogicaUmaInstanciaEmUmRepositorio() throws RepositoryException {
		EntidadeBasic instance = new EntidadeBasic();

		this.keeper.persist(instance);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));

		this.keeper.remove(instance);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));
		assertFalse(instance.getActive());
	}

	@Test
	public void deveRemoverDeFormaLogicaComParametroUmaInstanciaEmUmRepositorio() throws RepositoryException {
		EntidadeBasic instance = new EntidadeBasic();

		this.keeper.persist(instance);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));

		this.keeper.remove(instance, RemoveEnum.LOGICAL);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));
		assertFalse(instance.getActive());
	}

	@Test
	public void deveRemoverDeFormaFisicaUmaInstanciaEmUmRepositorio() throws RepositoryException {
		EntidadeBasic instance = new EntidadeBasic();

		this.keeper.persist(instance);

		assertNotNull(instance.getId());
		assertTrue(this.manager.contains(instance));

		this.keeper.remove(instance, RemoveEnum.PHYSICAL);

		assertFalse(this.manager.contains(instance));
	}

	@After
	public void tearDown() {
		this.transaction.rollback();
		this.transaction = null;
	}

}
