package br.com.brasilti.project.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.brasilti.project.entities.EntidadeBasic;
import br.com.brasilti.repository.core.Seeker;
import br.com.brasilti.repository.enums.ErrorEnum;
import br.com.brasilti.repository.enums.FieldEnum;
import br.com.brasilti.repository.enums.LikeEnum;
import br.com.brasilti.repository.exceptions.RepositoryException;
import br.com.brasilti.repository.propositions.And;
import br.com.brasilti.repository.propositions.Between;
import br.com.brasilti.repository.propositions.Equals;
import br.com.brasilti.repository.propositions.Greater;
import br.com.brasilti.repository.propositions.GreaterEquals;
import br.com.brasilti.repository.propositions.In;
import br.com.brasilti.repository.propositions.IsNotNull;
import br.com.brasilti.repository.propositions.IsNull;
import br.com.brasilti.repository.propositions.Lesser;
import br.com.brasilti.repository.propositions.LesserEquals;
import br.com.brasilti.repository.propositions.Like;
import br.com.brasilti.repository.propositions.Not;
import br.com.brasilti.repository.propositions.NotEquals;
import br.com.brasilti.repository.propositions.NotIn;
import br.com.brasilti.repository.propositions.Or;
import br.com.brasilti.repository.propositions.Proposition;
import br.com.brasilti.utils.reflection.ReflectionUtil;

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

		Field active = ReflectionUtil.getField(FieldEnum.ACTIVE.getValue(), EntidadeBasic.class);

		EntidadeBasic entidade0 = new EntidadeBasic();
		ReflectionUtil.set(Boolean.TRUE, active, entidade0);
		entidade0.setStringField("EntidadeBasicZero");
		entidade0.setIntegerField(0);
		entidade0.setLongField(0L);
		entidade0.setBigDecimalField(BigDecimal.ZERO);
		entidade0.setBooleanField(Boolean.TRUE);
		entidade0.setCalendarField(this.calendar0);
		this.manager.persist(entidade0);

		this.calendar1.add(Calendar.YEAR, 2);

		EntidadeBasic entidade1 = new EntidadeBasic();
		ReflectionUtil.set(Boolean.TRUE, active, entidade1);
		entidade1.setStringField("UmEntidadeBasic");
		entidade1.setIntegerField(1);
		entidade1.setLongField(1L);
		entidade1.setBigDecimalField(BigDecimal.ONE);
		entidade1.setBooleanField(Boolean.FALSE);
		entidade1.setCalendarField(this.calendar1);
		this.manager.persist(entidade1);

		this.calendar2.add(Calendar.YEAR, 4);

		EntidadeBasic entidade2 = new EntidadeBasic();
		ReflectionUtil.set(Boolean.TRUE, active, entidade2);
		entidade2.setStringField("BasicDoisEntidade");
		entidade2.setLongField(2L);
		entidade2.setCalendarField(this.calendar2);
		this.manager.persist(entidade2);

		EntidadeBasic entidade3 = new EntidadeBasic();
		ReflectionUtil.set(Boolean.FALSE, active, entidade3);
		this.manager.persist(entidade3);
	}

	@Test(expected = RepositoryException.class)
	public void deveLancarExcecaoQuandoAClasseForNulaException() throws RepositoryException {
		this.seeker.seekAll(null);
	}

	@Test
	public void deveLancarExcecaoQuandoAClasseForNula() {
		try {
			this.seeker.seekAll(null);
		} catch (RepositoryException e) {
			assertEquals(ErrorEnum.NULL_CLASS.getMessage(), e.getMessage());
		}
	}

	@Test
	public void deveRetornarTodasAsInstanciasAtivas() throws RepositoryException {
		System.out.println("At[e aqui");
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class);

		assertEquals(3, lista.size());
	}

	@Test
	public void naoDeveRetornarAInstanciaDeMenorValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Lesser("integerField", 0));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarAInstanciaDeMenorValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Lesser("integerField", 1));

		assertEquals(1, lista.size());
		assertEquals(Integer.valueOf(0), lista.get(0).getIntegerField());
	}

	@Test
	public void naoDeveRetornarAInstanciaDeMaiorValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Greater("bigDecimalField", BigDecimal.ONE));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarAInstanciaDeMaiorValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Greater("bigDecimalField", BigDecimal.ZERO));

		assertEquals(1, lista.size());
		assertEquals(BigDecimal.ONE, lista.get(0).getBigDecimalField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeMenorOuIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new LesserEquals("longField", -1L));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaDeMenorOuIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new LesserEquals("longField", 0L));

		assertEquals(1, lista.size());
		assertEquals(Long.valueOf(0), lista.get(0).getLongField());
	}

	@Test
	public void deveRetornarDuasInstanciasDeMenorOuIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new LesserEquals("longField", 1L));

		assertEquals(2, lista.size());
		assertEquals(Long.valueOf(0), lista.get(0).getLongField());
		assertEquals(Long.valueOf(1), lista.get(1).getLongField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeMaiorOuIgualValor() throws RepositoryException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 5);

		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new GreaterEquals("calendarField", calendar));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaDeMaiorOuIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new GreaterEquals("calendarField", this.calendar2));

		assertEquals(1, lista.size());
		assertEquals(this.calendar2, lista.get(0).getCalendarField());
	}

	@Test
	public void deveRetornarDuasInstanciasDeMaiorOuIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new GreaterEquals("calendarField", this.calendar1));

		assertEquals(2, lista.size());
		assertEquals(this.calendar1, lista.get(0).getCalendarField());
		assertEquals(this.calendar2, lista.get(1).getCalendarField());
	}

	@Test
	public void naoDeveRetornarAInstanciaDeIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Equals("integerField", 2));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarAInstanciaDeIgualValor() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Equals("integerField", 0));

		assertEquals(1, lista.size());
		assertEquals(Integer.valueOf(0), lista.get(0).getIntegerField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeValorDiferente() throws RepositoryException {
		List<Proposition> propositions = new ArrayList<Proposition>();
		propositions.add(new NotEquals("integerField", 0));
		propositions.add(new NotEquals("integerField", 1));
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, propositions);

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaDeValorDiferente() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotEquals("integerField", 0));

		assertEquals(1, lista.size());
		assertEquals(Integer.valueOf(1), lista.get(0).getIntegerField());
	}

	@Test
	public void deveRetornarDuasInstanciasDeValorDiferente() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotEquals("integerField", 2));

		assertEquals(2, lista.size());
		assertEquals(Integer.valueOf(0), lista.get(0).getIntegerField());
		assertEquals(Integer.valueOf(1), lista.get(1).getIntegerField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeValorNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNull("calendarField"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaDeValorNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNull("booleanField"));

		assertEquals(1, lista.size());
		assertNull(lista.get(0).getByteField());
	}

	@Test
	public void deveRetornarTresInstanciasDeValorNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNull("byteField"));

		assertEquals(3, lista.size());
		assertNull(lista.get(0).getByteField());
		assertNull(lista.get(1).getByteField());
		assertNull(lista.get(2).getByteField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaDeValorNaoNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNotNull("byteField"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarDuasInstanciasDeValorNaoNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNotNull("booleanField"));

		assertEquals(2, lista.size());
		assertNotNull(lista.get(0).getBooleanField());
		assertNotNull(lista.get(1).getBooleanField());
	}

	@Test
	public void deveRetornarTresInstanciasDeValorNaoNulo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNotNull("calendarField"));

		assertEquals(3, lista.size());
		assertNotNull(lista.get(0).getCalendarField());
		assertNotNull(lista.get(1).getCalendarField());
		assertNotNull(lista.get(2).getCalendarField());
	}

	@Test
	public void naoDeveRetornarInstanciasEntreDoisCalendarios() throws RepositoryException {
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -2);

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -1);

		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("calendarField", c1, c2));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaEntreDoisCalendarios() throws RepositoryException {
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -1);

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 1);

		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("calendarField", c1, c2));

		assertEquals(1, lista.size());
		assertEquals(this.calendar0.get(Calendar.YEAR), lista.get(0).getCalendarField().get(Calendar.YEAR));
	}

	@Test
	public void deveRetornarDuasInstanciasEntreDoisCalendarios() throws RepositoryException {
		Calendar c1 = Calendar.getInstance();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 2);

		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("calendarField", c1, c2));

		assertEquals(2, lista.size());
		assertEquals(this.calendar0.get(Calendar.YEAR), lista.get(0).getCalendarField().get(Calendar.YEAR));
		assertEquals(this.calendar1.get(Calendar.YEAR), lista.get(1).getCalendarField().get(Calendar.YEAR));
	}

	@Test
	public void deveRetornarTresInstanciasEntreDoisCalendarios() throws RepositoryException {
		Calendar c1 = Calendar.getInstance();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, 4);

		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("calendarField", c1, c2));

		assertEquals(3, lista.size());
		assertEquals(this.calendar0.get(Calendar.YEAR), lista.get(0).getCalendarField().get(Calendar.YEAR));
		assertEquals(this.calendar1.get(Calendar.YEAR), lista.get(1).getCalendarField().get(Calendar.YEAR));
		assertEquals(this.calendar2.get(Calendar.YEAR), lista.get(2).getCalendarField().get(Calendar.YEAR));
	}

	@Test
	public void naoDeveRetornarInstanciasEntreDoisValores() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("longField", Long.valueOf(-2), Long.valueOf(-1)));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaEntreDoisValores() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("longField", Long.valueOf(-1), Long.valueOf(0)));

		assertEquals(1, lista.size());
		assertEquals(Long.valueOf(0), lista.get(0).getLongField());
	}

	@Test
	public void deveRetornarDuasInstanciasEntreDoisValores() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("longField", Long.valueOf(0), Long.valueOf(1)));

		assertEquals(2, lista.size());
		assertEquals(Long.valueOf(0), lista.get(0).getLongField());
		assertEquals(Long.valueOf(1), lista.get(1).getLongField());
	}

	@Test
	public void deveRetornarTresInstanciasEntreDoisValores() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Between("longField", Long.valueOf(0), Long.valueOf(3)));

		assertEquals(3, lista.size());
		assertEquals(Long.valueOf(0), lista.get(0).getLongField());
		assertEquals(Long.valueOf(1), lista.get(1).getLongField());
		assertEquals(Long.valueOf(2), lista.get(2).getLongField());
	}

	@Test
	public void naoDeveRetornarInstanciasComPrefixo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Classe", LikeEnum.START));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComPrefixo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Entidade", LikeEnum.START));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasComSufixo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Interface", LikeEnum.END));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComSufixo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Basic", LikeEnum.END));

		assertEquals(1, lista.size());
		assertEquals("UmEntidadeBasic", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasComRadical() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Enum"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComRadical() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Dois"));

		assertEquals(1, lista.size());
		assertEquals("BasicDoisEntidade", lista.get(0).getStringField());
	}

	@Test
	public void deveRetornarTresInstanciaComRadical() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "Entidade"));

		assertEquals(3, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
		assertEquals("UmEntidadeBasic", lista.get(1).getStringField());
		assertEquals("BasicDoisEntidade", lista.get(2).getStringField());
	}

	@Test
	public void deveRetornarUmaInstanciaComPrefixoMaiusculo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "ENTIDADE", LikeEnum.START));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void deveRetornarUmaInstanciaComSufixoMinusculo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "basic", LikeEnum.END));

		assertEquals(1, lista.size());
		assertEquals("UmEntidadeBasic", lista.get(0).getStringField());
	}

	@Test
	public void deveRetornarUmaInstanciaComRadicalMaiusculoEMinusculo() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Like("stringField", "DoIs", LikeEnum.MIDDLE));

		assertEquals(1, lista.size());
		assertEquals("BasicDoisEntidade", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasForaDeUmaLista() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new In("stringField", Arrays.asList("Classe", "Interface", "Enum")));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaPresenteEmUmaLista() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new In("stringField", Arrays.asList("Classe", "EntidadeBasicZero", "Enum")));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasForaDeUmArray() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new In("stringField", "Classe", "Interface", "Enum"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaPresenteEmUmArray() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new In("stringField", "Classe", "Interface", "UmEntidadeBasic"));

		assertEquals(1, lista.size());
		assertEquals("UmEntidadeBasic", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasPresentesEmUmaLista() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotIn("stringField", Arrays.asList("EntidadeBasicZero", "UmEntidadeBasic", "BasicDoisEntidade")));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaForaDeUmaLista() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotIn("stringField", Arrays.asList("EntidadeBasicZero", "Interface", "BasicDoisEntidade")));

		assertEquals(1, lista.size());
		assertEquals("UmEntidadeBasic", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarInstanciasPresentesEmUmArray() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotIn("stringField", "EntidadeBasicZero", "UmEntidadeBasic", "BasicDoisEntidade"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaForaDeUmArray() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotIn("stringField", "EntidadeBasicZero", "UmEntidadeBasic", "Enum"));

		assertEquals(1, lista.size());
		assertEquals("BasicDoisEntidade", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComAnd() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new And(new NotEquals("integerField", 0), new NotEquals("integerField", 1)));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComAnd() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new And(new Equals("integerField", 0), new Equals("longField", 0L)));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComOr() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", -1), new Equals("integerField", 2)));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComOr() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 2)));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void deveRetornarDuasInstanciasComOr() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 1)));

		assertEquals(2, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
		assertEquals("UmEntidadeBasic", lista.get(1).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComNot() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Not(new Like("stringField", "Entidade")));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComNot() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Not(new IsNotNull("booleanField")));

		assertEquals(1, lista.size());
		assertEquals("BasicDoisEntidade", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComAndSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotEquals("integerField", 0), new NotEquals("longField", 1L), new IsNotNull("bigDecimalField"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComAndSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Equals("integerField", 0), new NotEquals("longField", 1L), new IsNotNull("bigDecimalField"));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComOrEAndEncadeados() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 1)), new IsNotNull("byteField"));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComOrEAndEncadeados() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 1)), new NotEquals("booleanField", Boolean.FALSE));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComAndEOrEncadeados() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new IsNotNull("byteField"), new Or(new Equals("integerField", 0), new Equals("integerField", 1)));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComAndEOrEncadeados() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new NotEquals("booleanField", Boolean.FALSE), new Or(new Equals("integerField", 0), new Equals("integerField", 1)));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", -1), new Equals("integerField", 2), new Equals("integerField", 3)));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 2), new Equals("integerField", 3)));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@Test
	public void deveRetornarDuasInstanciasComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 1), new Equals("integerField", 2)));

		assertEquals(2, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
		assertEquals("UmEntidadeBasic", lista.get(1).getStringField());
	}

	@Test
	public void deveRetornarTresInstanciasComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new Equals("integerField", 0), new Equals("integerField", 1), new IsNull("integerField")));

		assertEquals(3, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
		assertEquals("UmEntidadeBasic", lista.get(1).getStringField());
		assertEquals("BasicDoisEntidade", lista.get(2).getStringField());
	}

	@Test
	public void naoDeveRetornarUmaInstanciaComAndEncadeadoComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new And(new NotEquals("integerField", 0), new NotEquals("longField", 1L)), new IsNotNull("byteField")));

		assertTrue(lista.isEmpty());
	}

	@Test
	public void deveRetornarUmaInstanciaComAndEncadeadoComOrSequencial() throws RepositoryException {
		List<EntidadeBasic> lista = this.seeker.seekAll(EntidadeBasic.class, new Or(new And(new NotEquals("integerField", 0), new NotEquals("longField", 1L)), new Equals("integerField", 0)));

		assertEquals(1, lista.size());
		assertEquals("EntidadeBasicZero", lista.get(0).getStringField());
	}

	@After
	public void tearDown() {
		this.transaction.rollback();
		this.transaction = null;
	}

}
