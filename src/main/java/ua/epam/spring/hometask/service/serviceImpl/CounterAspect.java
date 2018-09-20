package ua.epam.spring.hometask.service.serviceImpl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.service.aspectDao.CounterAspectDao;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Component("counterAspect")
@Aspect
public class CounterAspect {
    private static int numberTimes = 0;
    private static final String eventNameType = "eventName";
    private static final String eventPriceType = "eventPrice";
    private static final String frequencyBookTicketType = "frequencyBookTicket";
    @Resource(name = "counterAspectDao")
    private CounterAspectDao counterAspectDao;

    public Collection<CounterAspectDao.CounterAspectResponse> getEventNameMap() {
        return counterAspectDao.getAllEventCountByType(eventNameType);
    }

    public Collection<CounterAspectDao.CounterAspectResponse> getEventPriceMap() {
        return counterAspectDao.getAllEventCountByType(eventPriceType);
    }

    public Collection<CounterAspectDao.CounterAspectResponse> getFrequencyBookTicket() {
        return counterAspectDao.getAllEventCountByType(frequencyBookTicketType);
    }

    @Pointcut("execution(* EventServiceImpl.getByName(..))")
    public void accessByName() {
    }

    @Before("accessByName() && args(evt)")
    public void countEventByName(JoinPoint jp, Object evt) {
        int count = 0;
        String nameEvent = (String) evt;
        if (counterAspectDao.findEventNameByEventType(eventNameType, nameEvent) == null)
            counterAspectDao.insertEventByEventType(eventNameType, nameEvent, count);
        counterAspectDao.updateEventByEventType(eventNameType, nameEvent, counterAspectDao.getCountEventByEventTypeAndName(eventNameType, nameEvent) + 1);
    }

    @Pointcut("execution(* BookingServiceImpl.getTicketsPrice(..))")
    public void getTicketPrice() {
    }

    @Before("getTicketPrice() && args(evt, dateTime, user, sets)")
    public void countTicketPriceQuery(JoinPoint jp, Object evt, Object dateTime, Object user, Object sets) {
        int count = 0;
        Event event = (Event) evt;
        if (counterAspectDao.findEventNameByEventType(eventPriceType, event.getId().toString()) == null)
            counterAspectDao.insertEventByEventType(eventPriceType, event.getId().toString(), count);
        counterAspectDao.updateEventByEventType(eventPriceType, event.getId().toString(), counterAspectDao.getCountEventByEventTypeAndName(eventPriceType, event.getId().toString()) + 1);
    }

    @Pointcut("execution(* BookingServiceImpl.bookTickets(..))")
    public void getBookTickets() {
    }

    @Before("getBookTickets() && args(ticket)")
    public void countTicketBooked(JoinPoint jp, Object ticket) {
        Set<Ticket> tickets = (Set<Ticket>) ticket;
        int countTicketsByOneRequest = tickets.size();
        if (counterAspectDao.getLastTimeTicketBooked(frequencyBookTicketType) != null)
            numberTimes = counterAspectDao.getLastTimeTicketBooked(frequencyBookTicketType);
        counterAspectDao.insertEventByEventType(frequencyBookTicketType, String.valueOf(++numberTimes), countTicketsByOneRequest);
    }
}