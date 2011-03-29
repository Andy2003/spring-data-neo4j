package org.springframework.data.graph.neo4j.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.graph.neo4j.Group;
import org.springframework.data.graph.neo4j.GroupRepository;
import org.springframework.data.graph.neo4j.Person;
import org.springframework.data.graph.neo4j.PersonRepository;
import org.springframework.data.graph.neo4j.support.node.Neo4jHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.neo4j.helpers.collection.IteratorUtil.asCollection;
import static org.springframework.data.graph.neo4j.Person.persistedPerson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/springframework/data/graph/neo4j/support/Neo4jGraphPersistenceTest-context.xml"})

public class FinderTest {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private GraphDatabaseContext graphDatabaseContext;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private GroupRepository groupRepository;

    @BeforeTransaction
    public void cleanDb() {
        Neo4jHelper.cleanDb(graphDatabaseContext);
    }

    @Test
    @Transactional
    public void testFinderFindAll() {
        Person p1 = persistedPerson("Michael", 35);
        Person p2 = persistedPerson("David", 25);
        Iterable<Person> allPersons = personRepository.findAll();
        assertThat(asCollection(allPersons), hasItems(p1, p2));
    }

    @Test
    @Transactional
    public void testSaveManyPeople() {
        Person p1 = new Person("Michael", 35);
        Person p2 = new Person("David", 25);
        personRepository.save(asList(p1,p2));
        assertEquals("persisted person 1",true,p1.hasPersistentState());
        assertEquals("persisted person 2",true,p2.hasPersistentState());
        assertThat(asCollection(personRepository.findAll()), hasItems(p2, p1));
    }

    @Test
    @Transactional
    public void testSavePerson() {
        Person p1 = new Person("Michael", 35);
        personRepository.save(p1);
        assertEquals("persisted person",true,p1.hasPersistentState());
        assertThat(personRepository.findOne(p1.getId()),is(p1));
    }
    @Test
    public void testDeletePerson() {
        Person p1 = persistedPerson("Michael", 35);
        personRepository.delete(p1);
        assertEquals("people deleted", false, personRepository.findAll().iterator().hasNext());
    }
    @Test
    public void testDeletePeople() {
        Person p1 = persistedPerson("Michael", 35);
        Person p2 = persistedPerson("David", 26);
        personRepository.delete(asList(p1,p2));
        assertEquals("people deleted", false, personRepository.findAll().iterator().hasNext());
    }

    @Test
    @Transactional
    public void testFinderFindById() {
        Person p = persistedPerson("Michael", 35);
        Person pById = personRepository.findOne(p.getNodeId());
        assertEquals(p, pById);
    }

    @Test
    @Transactional
    public void testFinderFindByIdNonexistent() {
        Person p = persistedPerson("Michael", 35);
        Person p2 = personRepository.findOne(589736218L);
        Assert.assertNull(p2);
    }

    @Test
    @Transactional
    public void testFinderCount() {
        assertEquals((Long)0L, personRepository.count());
        Person p = persistedPerson("Michael", 35);
        assertEquals((Long)1L, personRepository.count());
    }

    @Test
	@Transactional
	public void testFindAllOnGroup() {
	    log.debug("FindAllOnGroup start");
        Group g = new Group().persist();
        g.setName("test");
        Group g2 = new Group().persist();
        g.setName("test");
        Collection<Group> groups = IteratorUtil.addToCollection(groupRepository.findAll().iterator(), new HashSet<Group>());
        Assert.assertEquals(2, groups.size());
	    log.debug("FindAllOnGroup done");
	}
}
