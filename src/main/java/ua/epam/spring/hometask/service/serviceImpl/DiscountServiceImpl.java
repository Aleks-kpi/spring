package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.DiscountService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Component("discountServiceImpl")
public class DiscountServiceImpl implements DiscountService {
    @Override
    public byte getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, long numberOfTickets) {
        //Check for the existence of an event on the specified date
        if (!event.getAirDates().contains(airDateTime))
            return 0;

        byte discountBirthday = 0;
        if (checkBirthdayWithin5DaysOfAirDate(user, airDateTime))
            discountBirthday = 5;

        byte discount10ThTicket = 0;
        if (getCountTickets(user) >= 10 || numberOfTickets >= 10)
            discount10ThTicket = 50;

        byte discount = discount10ThTicket > discountBirthday ? discount10ThTicket : discountBirthday;
        return discount;
    }

    public boolean checkBirthdayWithin5DaysOfAirDate(@Nonnull User user, @Nonnull LocalDateTime airDateTime) {
        final LocalDateTime userBirthday = user.getBirthday();
        if (airDateTime.isAfter(userBirthday.plusDays(-5)))
            return true;
        return false;
    }

    public long getCountTickets(@Nonnull User user) {
        long countTickets = 0;
        for (Ticket ticket : user.getTickets())
            countTickets += 1;
        return countTickets;
    }

}
