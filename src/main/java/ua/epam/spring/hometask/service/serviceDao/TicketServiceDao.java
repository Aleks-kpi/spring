package ua.epam.spring.hometask.service.serviceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class TicketServiceDao {
    @Autowired
    private JdbcTemplate jdbcTemplateTicket;

    public TicketServiceDao() {
    }

    @PostConstruct
    public void createTableTicket() {
        jdbcTemplateTicket.update("CREATE TYPE db.userType EXTERNAL NAME 'ua.epam.spring.hometask.domain.User' LANGUAGE JAVA");
        jdbcTemplateTicket.update("CREATE TYPE db.eventType EXTERNAL NAME 'ua.epam.spring.hometask.domain.Event' LANGUAGE JAVA");
        jdbcTemplateTicket.update("CREATE TABLE tickets (id BIGINT, users db.userType, event db.eventType, date_time TIMESTAMP, seat BIGINT, calc_price DOUBLE)");
    }

    public Ticket save(@Nonnull Ticket ticket) {
        final Long ticketId = ticket.getId();
        if (getById(ticketId) != null)
            update(ticket);
        else
            insert(ticket);
        return getById(ticketId);
    }

    private void insert(@Nonnull Ticket ticket) {
        Object[] rowMapTicket = convertTicketToRowMap(ticket);
        jdbcTemplateTicket.update("INSERT INTO tickets (id, users, event, date_time, seat, calc_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)", rowMapTicket);
    }

    private void update(@Nonnull Ticket ticket) {
        Object[] rowMapTicket = convertTicketToRowMap(ticket);
        //Change the position of the ticketId in the array for update to the database.
        Object[] rowMapTicketForUpdate = new Object[rowMapTicket.length];
        rowMapTicketForUpdate[rowMapTicketForUpdate.length - 1] = rowMapTicket[0];
        System.arraycopy(rowMapTicket, 1, rowMapTicketForUpdate, 0, rowMapTicket.length - 1);

        jdbcTemplateTicket.update("UPDATE tickets SET users = ?, event = ?, date_time = ?, seat = ?, calc_price = ? " +
                "WHERE id = ?", rowMapTicketForUpdate);
    }

    public void remove(@Nonnull Ticket ticket) {
        final Long ticketId = ticket.getId();
        jdbcTemplateTicket.update("DELETE FROM tickets WHERE id = ?", ticketId);
    }

    public Ticket getById(@Nonnull Long ticketId) {
        RowMapper<Ticket> ticketMapRow = getTicketMapRow();
        Ticket ticket = null;
        try {
            ticket = jdbcTemplateTicket.queryForObject("SELECT * FROM tickets WHERE id = ?", new Object[]{ticketId}, ticketMapRow);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The ticket does not exist with id=[%d] in the DB.\r\n", ticketId);
        }
        return ticket;
    }

    public Collection<Ticket> getAll() {
        RowMapper<Ticket> ticketMapRow = getTicketMapRow();
        Collection<Ticket> allTickets = jdbcTemplateTicket.query("SELECT * FROM tickets", ticketMapRow);
        return allTickets;
    }

    private RowMapper<Ticket> getTicketMapRow() {
        RowMapper<Ticket> ticketRowMapper = new RowMapper<Ticket>() {
            public Ticket mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Long id = resultSet.getLong("id");
                User user = (User) resultSet.getObject("users");
                Event event = (Event) resultSet.getObject("event");
                LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
                Long seat = resultSet.getLong("seat");
                Double calcPrice = resultSet.getDouble("calc_price");

                Ticket ticket = new Ticket(user, event, dateTime, seat);
                ticket.setId(id);
                ticket.setCalcPrice(calcPrice);
                return ticket;
            }
        };
        return ticketRowMapper;
    }

    private Object[] convertTicketToRowMap(@Nonnull Ticket ticket) {
        Long id = ticket.getId();
        User user = ticket.getUser();
        Event event = ticket.getEvent();
        Timestamp dateTime = Timestamp.valueOf(ticket.getDateTime());
        Long seat = ticket.getSeat();
        Double calcPrice = ticket.getCalcPrice();

        Object[] rowMapTicket = new Object[]{id, user, event, dateTime, seat, calcPrice};
        return rowMapTicket;
    }
}