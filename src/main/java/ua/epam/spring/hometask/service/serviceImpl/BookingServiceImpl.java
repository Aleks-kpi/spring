package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.*;
import ua.epam.spring.hometask.service.BookingService;
import ua.epam.spring.hometask.service.DiscountService;
import ua.epam.spring.hometask.service.TicketService;
import ua.epam.spring.hometask.service.UserService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Component("bookingServiceImpl")
public class BookingServiceImpl implements BookingService {
    @Resource(name = "userServiceImpl")
    private UserService userService;
    @Resource(name = "discountServiceImpl")
    private DiscountService discountService;
    @Resource(name = "ticketServiceImpl")
    private TicketService ticketService;

    public BookingServiceImpl() {
    }

    @Override
    public double getTicketsPrice(@Nonnull Event event, @Nonnull LocalDateTime dateTime, @Nullable User user, @Nonnull Set<Long> seats) {
        double userPrice = 0;

        final Auditorium auditorium = event.getAuditoriums().get(dateTime);
        final Set<Long> vipSeats = auditorium.getVipSeats();
        for (Long seat : seats) {
            if (vipSeats.contains(seat))
                userPrice += 2 * event.getBasePrice();
            else
                userPrice += event.getBasePrice();
        }

        if (event.getRating().equals(EventRating.HIGH))
            userPrice = 1.2 * userPrice;

        byte userDiscount = discountService.getDiscount(user, event, dateTime, seats.size());
        if (userDiscount != 0)
            userPrice -= userPrice * userDiscount / 100;
        return userPrice;
    }

    @Override
    public void bookTickets(@Nonnull Set<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            if (!isNull(userService.getById(ticket.getUser().getId())))
                userService.getById(ticket.getUser().getId()).getTickets().add(ticket);
            else
                userService.save(ticket.getUser()).getTickets().add(ticket);

            ticketService.save(ticket);
        }
    }

    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        Set<Ticket> purchasedTickets = new HashSet<>();
        for (Ticket ticket : ticketService.getTicketsByEvent(event)) {
            if (ticket.getDateTime().isEqual(dateTime))
                purchasedTickets.add(ticket);
        }
        return purchasedTickets;
    }
}
