package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.util.*;

@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
@Entity
@Getter
@Setter
@NamedQuery(
        name = "Member.findByName",
        query = "select m from Member m where name= : name"
)
@NamedQueries(
        {
                @NamedQuery(
                        name = "Member.findByUsername",
                        query = "select m from Member m where name = :name"),
                @NamedQuery(
                        name = "Member.findByTeamId",
                        query = "select m from Member m where m.teamId = :teamId"
                )
        }
)

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<Order>();

//    @EntityGraph("Member.all")
//    @Query("select m from Member m")
//    List<Member> findMemberEntityGraph();
}

