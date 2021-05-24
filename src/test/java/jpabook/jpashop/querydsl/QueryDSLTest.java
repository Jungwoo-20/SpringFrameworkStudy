package jpabook.jpashop.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.QItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void 그() throws Exception {
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
}
