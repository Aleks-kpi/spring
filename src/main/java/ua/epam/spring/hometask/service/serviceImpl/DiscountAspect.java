package ua.epam.spring.hometask.service.serviceImpl;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.aspectDao.DiscountAspectDao;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("discountAspect")
@Aspect
public class DiscountAspect {
    public static final byte discountBirthday = 5;
    public static final byte discount10ThTicket = 50;
    public static final String birthday = "discountBirthday";
    public static final String ticket10th = "discount10ThTicket";
    @Resource(name = "discountAspectDao")
    private DiscountAspectDao discountAspectDao;

    public Collection<DiscountAspectDao.DiscountUserMap> getTotalDiscountMap() {
        return discountAspectDao.getAllDiscount();
    }

    @Pointcut("execution(* get*iscount(..))")
    public void discount() {
    }

    @AfterReturning(pointcut = "discount() && args(usr, event, airDateTime, numberOfTickets)", returning = "discount")
    public void eachDiscountForUser(Object discount, Object usr, Object event, Object airDateTime, Object numberOfTickets) {
        Byte discountAfterReturning = (Byte) discount;
        User user = (User) usr;
        if (discountAspectDao.findUserId(user) == null)
            discountAspectDao.insert(user, getTypeDiscount());

        Map<String, Integer> userDiscountMap = discountAspectDao.getDiscountForUser(user);
        if (discountAfterReturning == discountBirthday) {
            userDiscountMap.put(birthday, userDiscountMap.get(birthday) + 1);
            discountAspectDao.update(user, userDiscountMap);
        }
        if (discountAfterReturning == discount10ThTicket) {
            userDiscountMap.put(ticket10th, userDiscountMap.get(ticket10th) + 1);
            discountAspectDao.update(user, userDiscountMap);
        }
    }

    private static Map<String, Integer> getTypeDiscount() {
        Map<String, Integer> discountMap = new HashMap<>();
        discountMap.put(birthday, 0);
        discountMap.put(ticket10th, 0);
        return discountMap;
    }
}