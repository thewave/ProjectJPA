package br.com.wave.project.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.wave.project.core.entities.EntidadeBasic;
import br.com.wave.repository.core.Deliverer;
import br.com.wave.repository.propositions.Equals;
import br.com.wave.repository.propositions.GreaterEquals;
import br.com.wave.repository.propositions.GreaterThan;
import br.com.wave.repository.propositions.LesserEquals;
import br.com.wave.repository.propositions.LesserThan;
import br.com.wave.repository.propositions.NotEquals;

public class DelivererTest {

	private Deliverer deliverer;

	private EntityManager manager;

	private EntityTransaction transaction;
	
	private Calendar calendar;

	private Calendar calendar2;
	
	@Before
	public void setUp() {
		WeldContainer container = new Weld().initialize();
		this.deliverer = container.instance().select(Deliverer.class).get();

		this.manager = container.instance().select(EntityManager.class).get();
		this.transaction = this.manager.getTransaction();
		this.transaction.begin();

		this.calendar = Calendar.getInstance();
		this.calendar2 = Calendar.getInstance();
		
		EntidadeBasic entidade0 = new EntidadeBasic();
		entidade0.setIntegerField(0);
		entidade0.setBigDecimalField(BigDecimal.ZERO);
		entidade0.setLongField(0L);
		entidade0.setCalendarField(calendar);
		this.manager.persist(entidade0);
		
		calendar2.add(Calendar.YEAR, 1);
		
		EntidadeBasic entidade1 = new EntidadeBasic();
		entidade1.setIntegerField(1);
		entidade1.setBigDecimalField(BigDecimal.ONE);
		entidade1.setLongField(1L);
		entidade1.setCalendarField(calendar2);
		this.manager.persist(entidade1);
		
//		EntidadeOneToOne entidadeOneToOne0 = new EntidadeOneToOne();
//		entidadeOneToOne0.setEntidadeBasic(entidade0);
//		this.manager.persist(entidadeOneToOne0);
		
	}
	
	
	@Test
	public void deveRetornarTodasAsInstancias() {
		
		this.deliverer.select(EntidadeBasic.class);
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(2,lista.size());
	}

	@Test
	public void naoDeveRetornarAInstanciaDeMenorValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new LesserThan("integerField",0));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeMenorValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new LesserThan("integerField",1));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void naoDeveRetornarAInstanciaDeMaiorValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new GreaterThan("bigDecimalField",BigDecimal.ONE));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeMaiorValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new GreaterThan("bigDecimalField",BigDecimal.ZERO));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(BigDecimal.ONE,lista.get(0).getBigDecimalField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeMenorOuIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new LesserEquals("longField",-1L));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeMenorOuIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new LesserEquals("longField",0L));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(Long.valueOf(0),lista.get(0).getLongField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeMenorOuIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new LesserEquals("longField",1L));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(2,lista.size());
		assertEquals(Long.valueOf(0),lista.get(0).getLongField());		
		assertEquals(Long.valueOf(1),lista.get(1).getLongField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeMaiorOuIgualValor() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 2);
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new GreaterEquals("calendarField",calendar));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeMaiorOuIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new GreaterEquals("calendarField",this.calendar2));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(this.calendar2,lista.get(0).getCalendarField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeMaiorOuIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new GreaterEquals("calendarField",this.calendar));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(2,lista.size());
		assertEquals(this.calendar,lista.get(0).getCalendarField());		
		assertEquals(this.calendar2,lista.get(1).getCalendarField());		
	}

	@Test
	public void naoDeveRetornarAInstanciaDeIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new Equals("integerField",2));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeIgualValor() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new Equals("integerField",0));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeValorDiferente() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new NotEquals("integerField",0));
		this.deliverer.whose( new NotEquals("integerField",1));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeValorDiferente() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new NotEquals("integerField",0));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(1),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeValorDiferente() {
		
		this.deliverer.select(EntidadeBasic.class);
		this.deliverer.whose( new NotEquals("integerField",2));
		List<EntidadeBasic> lista = this.deliverer.fetchAll();
		
		assertEquals(2,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
		assertEquals(Integer.valueOf(1),lista.get(1).getIntegerField());		
	}

//	@Test
//	public void naoDeveRetornarUmaInstanciaDeValorNulo() {
//		
//		this.deliverer.select(EntidadeBasic.class);
//		this.deliverer.whose( new IsNull("entidadeBasic"));
//		List<EntidadeBasic> lista = this.deliverer.fetchAll();
//		
//		assertTrue(lista.isEmpty());
//	}

	@After
	public void tearDown() {
		this.transaction.rollback();
		this.transaction = null;
	}
	
}
