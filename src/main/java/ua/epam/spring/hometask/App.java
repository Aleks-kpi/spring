package ua.epam.spring.hometask;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.BookingService;
import ua.epam.spring.hometask.service.EventService;
import ua.epam.spring.hometask.service.UserService;
import ua.epam.spring.hometask.service.aspectDao.CounterAspectDao;
import ua.epam.spring.hometask.service.aspectDao.DiscountAspectDao;
import ua.epam.spring.hometask.service.aspectDao.LuckyWinnerAspectDao;
import ua.epam.spring.hometask.service.serviceImpl.CounterAspect;
import ua.epam.spring.hometask.service.serviceImpl.DiscountAspect;
import ua.epam.spring.hometask.service.serviceImpl.LuckyWinnerAspect;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class App {
    /*
     * Dear reviewer,
     *
     * Below you can find an example testing a data application from the context
     *
     * From the 67th line you can find to example testing a data for aspect-oriented programming
     *
     * In the ua.epam.spring.hometask.service.serviceDao package, you can find the DAO objects that use JDBCTemplate to store and retrieve data from DB
     * In the ua.epam.spring.hometask.service.aspectDao package, you can find the DAO object to store all aspect counters into the database
     *
     * Note: When you first run the application, the tables are initialized to the database using the @PostConstruct annotation.
     *       The Derby database is initialized in the root of the project package with the "database" name.
     *       For the following application runs, please to put comments the @PostConstruct annotation in the packages:
     *          1. ua.epam.spring.hometask.service.serviceDao
     *          2. ua.epam.spring.hometask.service.aspectDao
     *       For clearly reading from the console, the try{}catch() block has hidden comments in the serviceDao and aspectDao packages
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Ticket ticket = (Ticket) ctx.getBean("ticket");

        User user = (User) ctx.getBean("user");
        Event event = (Event) ctx.getBean("event");
        LocalDateTime eventTime = (LocalDateTime) ctx.getBean("eventTime");

        UserService userService = (UserService) ctx.getBean("userServiceImpl");
        userService.save(user);

        BookingService bookingService = (BookingService) ctx.getBean("bookingServiceImpl");
        bookingService.bookTickets(new HashSet<>(Arrays.asList(ticket)));

        System.out.println("All purchased tickets for event for specific date and Time");
        for (Ticket t : bookingService.getPurchasedTicketsForEvent(event, eventTime))
            System.out.println("Event {" + t.getEvent().getName() + "}, User {" + t.getUser().getLastName() + "}, " +
                    "DataTime {" + t.getDateTime() + "}, Seat{" + t.getSeat() + "}");

        double ticketsPrice = bookingService.getTicketsPrice(event, eventTime, user, new HashSet<>(Arrays.asList(7l, 17l, 37l)));
        System.out.println("Give 5% for user has birthday within 5 days of air date, price = " + ticketsPrice);

        ticketsPrice = bookingService.getTicketsPrice(event, eventTime, user, new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l)));
        System.out.println("Give 50% for every 10th ticket purchased by user, price = " + ticketsPrice);

        //Aspect-Oriented Programming show demonstration
        CounterAspect counterAspect = (CounterAspect) ctx.getBean("counterAspect");
        User student = (User) ctx.getBean("student");
        userService.save(student);
        Event eventAnimate = (Event) ctx.getBean("eventAnimate");
        Set<Ticket> tickets = (Set<Ticket>) ctx.getBean("ticketSet");

        System.out.println("Count how many times each event was accessed by name:");
        EventService eventService = (EventService) ctx.getBean("eventServiceImpl");
        eventService.save(event);
        eventService.getByName("Movie");
        eventService.getByName("Cartoon");
        eventService.getByName("Cartoon");
        eventService.getByName("Cartoon");
        Collection<CounterAspectDao.CounterAspectResponse> accessByEventName = counterAspect.getEventNameMap();
        for (CounterAspectDao.CounterAspectResponse accessByEvent : accessByEventName)
            System.out.printf("  The %d times event was accessed by %s name.\r\n", accessByEvent.getCount(), accessByEvent.getEventName());

        System.out.println("Count how many times its prices were queried:");
        ticketsPrice = bookingService.getTicketsPrice(eventAnimate, eventTime, student, new HashSet<>(Arrays.asList(5l, 7l)));
        ticketsPrice = bookingService.getTicketsPrice(eventAnimate, eventTime, user, new HashSet<>(Arrays.asList(1l, 2l)));
        ticketsPrice = bookingService.getTicketsPrice(eventAnimate, eventTime, user, new HashSet<>(Arrays.asList(8l, 9l, 10l)));
        Collection<CounterAspectDao.CounterAspectResponse> eventPriceQueried = counterAspect.getEventPriceMap();
        for (CounterAspectDao.CounterAspectResponse eventPriceQuery : eventPriceQueried)
            System.out.printf("  The %d times prices were queried for the id %s event.\r\n", eventPriceQuery.getCount(), eventPriceQuery.getEventName());

        System.out.println("Count how many times its tickets were booked:");
        bookingService.bookTickets(new HashSet<>(tickets));
        Collection<CounterAspectDao.CounterAspectResponse> frequencyBookTicketList = counterAspect.getFrequencyBookTicket();
        for (CounterAspectDao.CounterAspectResponse frequencyBookTicket : frequencyBookTicketList)
            System.out.printf("  The %s times %d tickets were booked.\r\n", frequencyBookTicket.getEventName(), frequencyBookTicket.getCount());

        System.out.println("Count how many times each discount was given total and for specific user:");
        DiscountAspect discountAspect = (DiscountAspect) ctx.getBean("discountAspect");
        Collection<DiscountAspectDao.DiscountUserMap> discountMap = discountAspect.getTotalDiscountMap();
        for (DiscountAspectDao.DiscountUserMap discountUserMap : discountMap)
            System.out.printf("  For user id={%d} was given the birthday: %d-th discounts and the ticket10th: %d-th discounts.\r\n", discountUserMap.getUserId(),
                    discountUserMap.getDiscount().get(DiscountAspect.birthday), discountUserMap.getDiscount().get(DiscountAspect.ticket10th));

        System.out.println("The bookTicket method is executed perform the checkLucky method for the user that based on some randomness will return true or false.");
        LuckyWinnerAspect luckyWinnerAspect = (LuckyWinnerAspect) ctx.getBean("luckyWinnerAspect");
        Collection<LuckyWinnerAspectDao.LuckyWinnerUser> luckyWinner = luckyWinnerAspect.getLuckyEventUser();
        if (!luckyWinner.isEmpty())
            System.out.println("Store the information about this lucky event into the user object:");
        for (LuckyWinnerAspectDao.LuckyWinnerUser luckyWinnerUser : luckyWinner)
            System.out.printf("  The user id={%d} is lucky, and received: %s.\r\n", luckyWinnerUser.getUserId(), luckyWinnerUser.getLucyMessage());
    }
}