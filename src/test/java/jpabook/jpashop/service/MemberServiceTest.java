package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//Test는 커밋하지 않고 롤백한다.
class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        Member member = new Member();
        member.setName("Kim");
        Long savedId = memberService.join(member);
        Assertions.assertThat(member).isEqualTo(memberService.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        Member member1 = new Member();
        member1.setName("Kim");
        Member member2 = new Member();
        member2.setName("Kim");
        memberService.join(member1);

        // junit
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->
//            memberService.join(member2)
//        );
//        String message = exception.getMessage();
//        System.out.println("message = " + message);

        // AssertJ
        Assertions.assertThatThrownBy(
                () -> {
                    memberService.join(member2);
                }
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("이미 존재하는 회원 입니다.");


    }
}