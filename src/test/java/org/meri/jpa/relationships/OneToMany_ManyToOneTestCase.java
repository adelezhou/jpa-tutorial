package org.meri.jpa.relationships;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.junit.Test;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CollectionInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CollectionOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CustomColumnCollectionInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CustomColumnCollectionOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CustomColumnMapInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.CustomColumnMapOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.LazyOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.MapInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.MapOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.OrderedInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.OrderedOwner;
import org.meri.jpa.relationships.entities.onetomany_manytoone.OrphanInverse;
import org.meri.jpa.relationships.entities.onetomany_manytoone.OrphanOwner;

public class OneToMany_ManyToOneTestCase extends AbstractRelationshipTestCase {

  protected static final String CHANGELOG_LOCATION = RelationshipsConstants.ONE_TO_MANY_MANY_TO_ONE_CHANGELOG_PATH;

  @Override
  protected String getInitialChangeLog() {
    return CHANGELOG_LOCATION;
  }

  @Test
  public void basicCollection() {
    EntityManager em = factory.createEntityManager();
    
    CollectionOwner owner = em.find(CollectionOwner.class, 1);
    
    CollectionInverse inverse = owner.getInverse();
    assertEquals(5, inverse.getId());
    assertEquals(1, inverse.getOwners().size());

    em.close();
  }

  @Test
  public void eagerLoadingCollection() {
    EntityManager em = factory.createEntityManager();
    
    CollectionOwner owner = em.find(CollectionOwner.class, 1);
    em.close();

    //this side is eagerly loaded by default
    CollectionInverse inverse = owner.getInverse();
    assertEquals(5, inverse.getId());
  }

  @Test
  public void lazyLoadingCollection() {
    EntityManager em = factory.createEntityManager();
    
    LazyOwner owner = em.find(LazyOwner.class, 1);
    em.close();

    // it is possible to configure it to be lazy
    assertNull(owner.getInverse());
  }

  @Test
  public void basicMap() {
    EntityManager em = factory.createEntityManager();
    
    MapOwner owner = em.find(MapOwner.class, 1);
    
    MapInverse inverse = owner.getInverse();
    assertEquals(5, inverse.getId());
    // mapKey property is a key to the map
    Map<String, MapOwner> owners = inverse.getOwners();
    assertEquals(owner, owners.get(owner.getMapKey()));
    
    em.close();
  }
  
  @Test
  public void orphanRemoval() {
    EntityManager em = factory.createEntityManager();
    em.getTransaction().begin();
    // load the inverse
    OrphanInverse inverse = em.find(OrphanInverse.class, 5);
    // check whether the owner exists
    assertEquals(1, inverse.getOwners().get("first").getId());
    // remove the owner
    inverse.getOwners().remove("first");
    // commit the transaction and close manager
    em.getTransaction().commit();
    em.close();
  
    // owner without inverse has been removed
    assertEntityNOTExists(OrphanOwner.class, 1);
  }

  @Test
  public void customColumnCollection() {
    EntityManager em = factory.createEntityManager();
    
    CustomColumnCollectionOwner owner = em.find(CustomColumnCollectionOwner.class, 1);
    
    CustomColumnCollectionInverse inverse = owner.getInverse();
    assertEquals(5, inverse.getId());
    assertEquals(1, inverse.getOwners().size());

    em.close();
  }

  @Test
  public void customColumnMap() {
    EntityManager em = factory.createEntityManager();
    
    CustomColumnMapOwner owner = em.find(CustomColumnMapOwner.class, 1);
    
    CustomColumnMapInverse inverse = owner.getInverse();
    assertEquals(5, inverse.getId());
    // mapKey property is a key to the map
    assertEquals(owner, inverse.getOwners().get(owner.getMapKey()));
    
    em.close();
  }
  
  @Test
  public void orderedCollection() {
    EntityManager em = factory.createEntityManager();
    
    OrderedOwner owner = em.find(OrderedOwner.class, 1);
    
    OrderedInverse inverse = owner.getInverse();
    assertOrdered(inverse.getOwners());

    em.close();
  }

  private void assertOrdered(List<OrderedOwner> owners) {
    for (int i = 1; i < owners.size(); i++) {
      OrderedOwner current = owners.get(i);
      OrderedOwner previous = owners.get(i-1);
      
      if (current.getOrdering() == previous.getOrdering()) {
        assertTrue(current.getId() < previous.getId());
      } else {
        assertTrue(current.getOrdering() > previous.getOrdering());
      }
    }
  }

  @Test
  public void relationshipSaveOnlyOwner() {
    EntityManager em = getFactory().createEntityManager();
    // load two entities
    CollectionOwner owner = em.find(CollectionOwner.class, 6);
    CollectionInverse inverse = em.find(CollectionInverse.class, 6);
    //check initial data - there is no relationship
    assertTrue(inverse.getOwners().isEmpty());
    
    // create a relationship between entities
    owner.setInverse(inverse);
    inverse.getOwners().add(owner);
    // detached inverse will be ignored by commit
    em.detach(inverse);
    em.getTransaction().begin();
    // persist only the owner - it is the only loaded entity
    em.getTransaction().commit();
    em.close();

    // relationship was saved
    EntityManager em1 = getFactory().createEntityManager();
    CollectionInverse inverseCheck = em1.find(CollectionInverse.class, 6);
    assertFalse(inverseCheck.getOwners().isEmpty());
    em1.close();
  }

  @Test
  public void relationshipSaveOnlyInverse() {
    EntityManager em = getFactory().createEntityManager();
    // load two entities
    CollectionOwner owner = em.find(CollectionOwner.class, 7);
    CollectionInverse inverse = em.find(CollectionInverse.class, 7);
    //check initial data - there is no relationship
    assertTrue(inverse.getOwners().isEmpty());

    // create a relationship between entities
    owner.setInverse(inverse);
    inverse.getOwners().add(owner);
    // detached owner will be ignored by commit
    em.detach(owner);
    em.getTransaction().begin();
    // persist only the inverse - it is the only loaded entity
    em.getTransaction().commit();
    em.close();

    // relationship was not saved
    EntityManager em1 = getFactory().createEntityManager();
    CollectionOwner ownerCheck = em1.find(CollectionOwner.class, 7);
    assertNull(ownerCheck.getInverse());
    em1.close();
  }

  @Test
  public void relationshipDeleteOnlyInverse() {
    EntityManager em = getFactory().createEntityManager();
    // check initial data
    CollectionOwner owner = em.find(CollectionOwner.class, 4);
    CollectionInverse inverse = em.find(CollectionInverse.class, 4);
    assertTrue(inverse.getOwners().contains(owner));
    // detached owner will be ignored by commit
    em.detach(owner);
    // persist only the inverse
    em.getTransaction().begin();
    try {
      em.remove(inverse);
      em.getTransaction().commit();
    } catch (PersistenceException ex) {
      em.close();
      return;
    }
    
    fail("The delete was supposed to throw an exception.");

  }

  @Test
  public void relationshipDeleteOnlyOwner() {
    EntityManager em = getFactory().createEntityManager();
    // check initial data
    CollectionOwner owner = em.find(CollectionOwner.class, 3);
    CollectionInverse inverse = em.find(CollectionInverse.class, 3);
    assertTrue(inverse.getOwners().contains(owner));
    // detached owner will be ignored by commit
    em.detach(inverse);
    // persist only the inverse
    em.getTransaction().begin();
    em.remove(owner);
    em.getTransaction().commit();
    em.close();
    
    // relationship was deleted
    EntityManager em1 = getFactory().createEntityManager();
    CollectionInverse inverseCheck = em1.find(CollectionInverse.class, 3);
    assertTrue(inverseCheck.getOwners().isEmpty());
    em1.close();
  }

}
