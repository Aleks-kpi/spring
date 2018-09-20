package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.TicketService;
import ua.epam.spring.hometask.service.serviceDao.TicketServiceDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;

@Component("ticketServiceImpl")
public class TicketServiceImpl implements TicketService {
    @Resource(name = "ticketServiceDao")
    private TicketServiceDao ticketServiceDao;

    public TicketServiceImpl() {
    }

    public TicketServiceImpl(TicketServiceDao ticketServiceDao) {
        this.ticketServiceDao = ticketServiceDao;
    }

    @Nullable
    @Override
    public Collection<Ticket> getTicketsByEvent(@Nonnull Event event) {
        Collection<Ticket> ticketsByEvent = new LinkedList<>();
        for (Ticket ticket : getAll()) {
            if (ticket.getEvent().equals(event))
                ticketsByEvent.add(ticket);
        }
        return ticketsByEvent;
    }

    @Nullable
    @Override
    public Collection<Ticket> getTicketsByUser(@Nonnull User user) {
        Collection<Ticket> ticketsByUser = new LinkedList<>();
        for (Ticket ticket : getAll()) {
            if (ticket.getUser().equals(user))
                ticketsByUser.add(ticket);
        }
        return ticketsByUser;
    }

    @Override
    public Ticket save(@Nonnull Ticket ticket) {
        return ticketServiceDao.save(ticket);
    }

    @Override
    public void remove(@Nonnull Ticket ticket) {
        ticketServiceDao.remove(ticket);
    }

    @Override
    public Ticket getById(@Nonnull Long ticketId) {
        return ticketServiceDao.getById(ticketId);
    }

    @Override
    public Collection<Ticket> getAll() {
        return ticketServiceDao.getAll();
    }
}
