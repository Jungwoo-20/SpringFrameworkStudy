package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            System.out.println("================merge before============");
            Item findItem = em.merge(item); // merge이후에 작업을 해야하는경우 findItem으로 작업해야함.(item은 영속성 컨텍스트에서 관리되지 않음)
            System.out.println("findItem.getPrice() = " + findItem.getPrice());
            System.out.println("item.getPrice() = " + item.getPrice());
            System.out.println("em.contains(item)" + em.contains(item));
            System.out.println("em.contains(findItem)" + em.contains(findItem)); // 영속성 컨텍스트에서 관리됨
            em.detach(findItem);

            System.out.println("================merge after============");
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
