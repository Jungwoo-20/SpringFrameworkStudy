package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.NewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor //constructor 생성
public class MemberService {
    private final NewMemberRepository memberRepository;

    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 회원 입니다.");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //    public Member findOne(Long MemberId) {
//        return memberRepository.findOne(MemberId);
//    }
    public Member findOne(Long MemberId) {
        return memberRepository.findById(MemberId).get();
    }

}
