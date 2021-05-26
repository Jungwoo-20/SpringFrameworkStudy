package jpabook.jpashop.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.QItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
public class QueryDSLTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void 기본() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember member = QMember.member;
        List<Member> members =
                query.select(member)
                        .from(member)
                        .where(member.name.eq("kim"))
                        .orderBy(member.name.desc())
                        .fetch();
        members.stream().forEach(a -> System.out.println(a.getName()));
    }

    @Test
    public void 조건() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QItem item = QItem.item;
        List<Item> items =
                query.select(item)
                        .from(item)
                        .where(item.name.contains("item").and(item.price.gt(3000)).or(item.stockQuantity.eq(50)))
                        .fetch();
        items.stream().forEach(a -> System.out.println("a.getName() = " + a.getName()));
    }

    @Test
    public void 정렬() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QItem item = QItem.item;
        List<Item> members =
                query.select(item)
                        .from(item)
                        .orderBy(item.price.asc(), item.stockQuantity.asc().nullsLast())
                        .fetch();
        members.stream().forEach(a -> System.out.println("a.getName() = " + a.getName()));
    }

    @Test
    public void 동적쿼리() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QItem item = QItem.item;
        SearchParam param = new SearchParam();
        param.setName("item");
        param.setPrice(5000);
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(param.getName())) {
            builder.and(item.name.contains(param.getName()));
        }
        if (param.getPrice() != null) {
            builder.and(item.price.gt(param.getPrice()));
        }
        List<Item> items =
                query.select(item)
                        .from(item)
                        .where(builder)
                        .fetch();
        items.stream().forEach(a -> System.out.println("a.getName() = " + a.getName()));
    }

    private BooleanExpression itemNameContain(SearchParam params) {
        if (!StringUtils.hasText(params.getName())) {
            return null;
        }
        return QItem.item.name.contains(params.getName());
    }

    private BooleanExpression itemPriceGt(SearchParam params) {
        if (params.getPrice() == null) {
            return null;
        }
        return QItem.item.price.gt(params.getPrice());
    }

    @Test
    public void 페이징() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QItem item = QItem.item;
        List<Item> items =
                query.selectFrom(item)
                        .orderBy(item.price.asc())
                        .offset(0)
                        .limit(2)
                        .orderBy(item.price.asc())
                        .fetch();
        items.stream().forEach(a -> System.out.println("a.getName() = " + a.getName()));
    }

    @Test
    public void 그룹() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QItem item = QItem.item;
        List<Tuple> counts = query.selectFrom(item)
                .groupBy(item.price)
                .select(item.price, item.price.count())
                .fetch();

        IntStream
                .range(0, counts.size())
                .mapToObj(index -> String.format("%d -> %s",
                        counts.get(index).toArray()[0],
                        counts.get(index).toArray()[1]))
                .forEach(System.out::println);
    }

    @Test
    public void 전용DTO() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember member = QMember.member;
        List<MemberDTO> memberDTOs = query.select(Projections.bean(MemberDTO.class,
                member.id,
                member.name.as("username")))
                .from(member)
                .fetch();
        memberDTOs.stream().forEach(dto -> System.out.println("dto.getName() = " + dto.getUsername()));
    }

    @Test
    public void 다대일조인() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QOrder order = QOrder.order;

        // N+1문제가 있음
//        List<Order> orderWithMember = query.selectFrom(order)
//                .join(order.member, member)
//                .fetch(); //Order와 Member를 조인해도 따로 불러냄 --> Lazy로 설정했기 때문
//        orderWithMember
//                .stream()
//                .forEach(o -> System.out.println("o.getMember().getName() = " + o.getMember().getName()));
        List<Order> orderWithMember = em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
        orderWithMember.stream()
                .forEach(o -> System.out.println("o.getMember().getName() = " + o.getOrderItems().get(0).getItem().getName()));
    }

    @Test
    public void 일대다조인() throws Exception {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QOrder order = QOrder.order;

//        List<Member> resultList = em.createQuery(
//                "select m from Member m" + " join fetch m.orders o", Member.class)
//                .setFirstResult(0)
//                .setMaxResults(1)
//                .getResultList();
        // 일대다관계는 패치로 조인하는 것이 아니라 대상 엔티티만 조회한다. 배치
        List<Member> resultList = em.createQuery(
                "select m from Member m", Member.class)
                .setFirstResult(0)
                .setMaxResults(2)
                .getResultList();
        resultList.stream()
                .forEach(m -> m.getOrders().stream().forEach(o -> System.out.println("o.getOrderDate() = " + o.getOrderDate())));
    }
}
