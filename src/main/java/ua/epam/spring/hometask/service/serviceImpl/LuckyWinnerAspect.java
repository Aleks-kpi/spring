package ua.epam.spring.hometask.service.serviceImpl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.aspectDao.LuckyWinnerAspectDao;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

@Component("luckyWinnerAspect")
@Aspect
public class LuckyWinnerAspect {
    @Resource(name = "luckyWinnerAspectDao")
    private LuckyWinnerAspectDao luckyEventUser;

    public Collection<LuckyWinnerAspectDao.LuckyWinnerUser> getLuckyEventUser() {
        return luckyEventUser.getAllLuckyWinner();
    }

    @Pointcut("execution(* BookingServiceImpl.bookTickets(..))")
    public void allBookTickets() {
    }

    @Around("allBookTickets() && args(tickets)")
    public void aroundBookTickets(ProceedingJoinPoint jp, Object tickets) throws Throwable {
        Set<Ticket> ticketSet = (Set<Ticket>) tickets;
        User user = ticketSet.stream().findFirst().get().getUser();

        String eventName = ticketSet.stream().findFirst().get().getEvent().getName();
        String lucyMessage = "Free tickets for the " + eventName + " event";

        boolean userLucky = checkLucky(user);
        if (userLucky == true) {
            // The ticketPrice changes to zero and ticket is booked
            ticketSet.stream().forEach(s -> s.setCalcPrice(0.0));
            jp.proceed(new Object[]{ticketSet});

            //Store the information about event into the luckyEventUser and put in system message
            if (luckyEventUser.findUserId(user) == null)
                luckyEventUser.insert(user, new LinkedList<>());
            LinkedList<String> lucyMessageForUser = luckyEventUser.getLucyMessageForUser(user);
            lucyMessageForUser.add(lucyMessage);
            luckyEventUser.update(user, lucyMessageForUser);
            System.out.printf(">>> Congratulation!!! <<< The %s %s is lucky. %s.\r\n", user.getFirstName(), user.getLastName(), lucyMessage);
        } else {
            jp.proceed(new Object[]{tickets});
        }
    }

    //The checkLucky method for the user and store into the user object
    private static boolean checkLucky(User user) {
        Random random = new Random();
        user.setLucky(random.nextBoolean());
        return user.isLucky();
    }
}