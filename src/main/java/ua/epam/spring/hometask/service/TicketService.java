package ua.epam.spring.hometask.service;

import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface TicketService extends AbstractDomainObjectService<Ticket> {

    /**
     * Finding tickets by Event
     *
     * @param event Event of the Ticket
     * @return found Collection of tickets or <code>null</code>
     */
    public @Nullable Collection<Ticket> getTicketsByEvent(@Nonnull Event event);

    /**
     * Finding tickets by User
     *
     * @param user User of the Ticket
     * @return found Collection of tickets or <code>null</code>
     */
    public @Nullable Collection<Ticket> getTicketsByUser(@Nonnull User user);
}
