package jpabook.jpashop.web;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.print.attribute.HashPrintJobAttributeSet;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        return "members/join-form";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<String, String>();
            result.getFieldErrors().stream().forEach(fieldError -> {
                String filedName = fieldError.getField();
                String errorMsg = fieldError.getDefaultMessage();
                errorMap.put(filedName, errorMsg);
            });
            model.addAttribute("error", errorMap);
            model.addAttribute("memberForm", form);
            return "/members/join-form";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/member-list";
    }
}
