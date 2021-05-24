package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemService itemService;

    public Long order(Long memberId, Long itemId, int quantity) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemService.findOne(itemId);
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), quantity);
        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order);
        return order.getId();
    }
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancel();

    }

    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }
}
