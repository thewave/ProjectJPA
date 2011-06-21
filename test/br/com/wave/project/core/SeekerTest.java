package br.com.wave.project.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import br.com.wave.repository.core.Seeker;
import br.com.wave.repository.propositions.Between;
import br.com.wave.repository.propositions.Equals;
import br.com.wave.repository.propositions.GreaterEquals;
import br.com.wave.repository.propositions.GreaterThan;
import br.com.wave.repository.propositions.IsNotNull;
import br.com.wave.repository.propositions.IsNull;
import br.com.wave.repository.propositions.LesserEquals;
import br.com.wave.repository.propositions.LesserThan;
import br.com.wave.repository.propositions.NotEquals;

public class SeekerTest {

	private Seeker seeker;

	private EntityManager manager;

	private EntityTransaction transaction;
	
	private Calendar calendar0;

	private Calendar calendar1;
	
	private Calendar calendar2;
	
	@Before
	public void setUp() {
		WeldContainer container = new Weld().initialize();
		this.seeker = container.instance().select(Seeker.class).get();

		this.manager = container.instance().select(EntityManager.class).get();
		this.transaction = this.manager.getTransaction();
		this.transaction.begin();

		this.calendar0 = Calendar.getInstance();
		this.calendar1 = Calendar.getInstance();
		this.calendar2 = Calendar.getInstance();
		
		EntidadeBasic entidade0 = new EntidadeBasic();
		entidade0.setIntegerField(0);
		entidade0.setBigDecimalField(BigDecimal.ZERO);
		entidade0.setLongField(0L);
		entidade0.setCalendarField(calendar0);
		this.manager.persist(entidade0);
		
		calendar1.add(Calendar.YEAR, 2);
		
		EntidadeBasic entidade1 = new EntidadeBasic();
		entidade1.setIntegerField(1);
		entidade1.setBigDecimalField(BigDecimal.ONE);
		entidade1.setLongField(1L);
		entidade1.setCalendarField(calendar1);
		this.manager.persist(entidade1);
		
		calendar2.add(Calendar.YEAR, 4);
		
		EntidadeBasic entidade2 = new EntidadeBasic();
		entidade2.setCalendarField(calendar2);
		this.manager.persist(entidade2);
		
	}
	
	
	@Test
	public void deveRetornarTodasAsInstancias() {
		
		this.seeker.giveme(EntidadeBasic.class);
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(3,lista.size());
	}

	@Test
	public void naoDeveRetornarAInstanciaDeMenorValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new LesserThan("integerField",0));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeMenorValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new LesserThan("integerField",1));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void naoDeveRetornarAInstanciaDeMaiorValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new GreaterThan("bigDecimalField",BigDecimal.ONE));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeMaiorValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new GreaterThan("bigDecimalField",BigDecimal.ZERO));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(BigDecimal.ONE,lista.get(0).getBigDecimalField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeMenorOuIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new LesserEquals("longField",-1L));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeMenorOuIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new LesserEquals("longField",0L));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(Long.valueOf(0),lista.get(0).getLongField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeMenorOuIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new LesserEquals("longField",1L));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(2,lista.size());
		assertEquals(Long.valueOf(0),lista.get(0).getLongField());		
		assertEquals(Long.valueOf(1),lista.get(1).getLongField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeMaiorOuIgualValor() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 5);
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new GreaterEquals("calendarField",calendar));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeMaiorOuIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new GreaterEquals("calendarField",this.calendar2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(this.calendar2,lista.get(0).getCalendarField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeMaiorOuIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new GreaterEquals("calendarField",this.calendar1));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(2,lista.size());
		assertEquals(this.calendar1,lista.get(0).getCalendarField());		
		assertEquals(this.calendar2,lista.get(1).getCalendarField());		
	}

	@Test
	public void naoDeveRetornarAInstanciaDeIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Equals("integerField",2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarAInstanciaDeIgualValor() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Equals("integerField",0));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void naoDeveRetornarUmaInstanciaDeValorDiferente() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new NotEquals("integerField",0));
		this.seeker.whose( new NotEquals("integerField",1));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarUmaInstanciaDeValorDiferente() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new NotEquals("integerField",0));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertEquals(Integer.valueOf(1),lista.get(0).getIntegerField());		
	}
	
	@Test
	public void deveRetornarDuasInstanciasDeValorDiferente() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new NotEquals("integerField",2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(2,lista.size());
		assertEquals(Integer.valueOf(0),lista.get(0).getIntegerField());		
		assertEquals(Integer.valueOf(1),lista.get(1).getIntegerField());		
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeValorNulo() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new IsNull("calendarField"));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarTresInstanciasDeValorNulo() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new IsNull("byteField"));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(3,lista.size());
		assertNull(lista.get(0).getByteField());		
		assertNull(lista.get(1).getByteField());		
		assertNull(lista.get(2).getByteField());		
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeValorNaoNulo() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new IsNotNull("byteField"));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveRetornarTresInstanciasDeValorNaoNulo() {
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new IsNotNull("calendarField"));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(3,lista.size());
		assertNull(lista.get(0).getByteField());		
		assertNull(lista.get(1).getByteField());		
		assertNull(lista.get(2).getByteField());		
	}

	@Test
	public void naoDeveRetornarInstanciasEntreDoisValores() {
		
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -2);
		
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -1);
				
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Between("calendarField",c1,c2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaEntreDoisValores() {
		
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -1);
		
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 1);
						
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Between("calendarField",c1,c2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(1,lista.size());
		assertNotNull(lista.get(0).getCalendarField());		
	}

	@Test
	public void deveRetornarDuasInstanciaEntreDoisValores() {
		
		Calendar c1 = Calendar.getInstance();
		
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 2);
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Between("calendarField",c1,c2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(2,lista.size());
		assertNotNull(lista.get(0).getCalendarField());		
		assertNotNull(lista.get(1).getCalendarField());		
	}

	@Test
	public void deveRetornarTresInstanciaEntreDoisValores() {
		
		Calendar c1 = Calendar.getInstance();
		
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 4);
		
		this.seeker.giveme(EntidadeBasic.class);
		this.seeker.whose( new Between("calendarField",c1,c2));
		List<EntidadeBasic> lista = this.seeker.go();
		
		assertEquals(3,lista.size());
		assertNotNull(lista.get(0).getCalendarField());		
		assertNotNull(lista.get(1).getCalendarField());		
		assertNotNull(lista.get(2).getCalendarField());		
	}

	@After
	public void tearDown() {
		this.transaction.rollback();
		this.transaction = null;
	}
	
}
