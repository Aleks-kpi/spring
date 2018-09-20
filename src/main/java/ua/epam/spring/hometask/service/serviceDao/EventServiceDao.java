package ua.epam.spring.hometask.service.serviceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Auditorium;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.EventRating;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.NavigableSet;

@Component
public class EventServiceDao {
    @Autowired
    private JdbcTemplate jdbcTemplateEvent;

    public EventServiceDao() {
    }

    @PostConstruct
    public void createTableEvent() {
        jdbcTemplateEvent.update("CREATE TYPE db.eventRating EXTERNAL NAME 'ua.epam.spring.hometask.domain.EventRating' LANGUAGE JAVA");
        jdbcTemplateEvent.update("CREATE TYPE db.navigableMap EXTERNAL NAME 'java.util.NavigableMap' LANGUAGE JAVA");
        jdbcTemplateEvent.update("CREATE TABLE events (id BIGINT, name VARCHAR(255), air_dates db.navigableSet, base_price DOUBLE, rating db.eventRating, auditoriums db.navigableMap)");
    }

    public Event save(@Nonnull Event event) {
        final Long eventId = event.getId();
        if (getById(eventId) != null)
            update(event);
        else
            insert(event);
        return getById(eventId);
    }

    private void insert(@Nonnull Event event) {
        Object[] rowMapEvent = convertEventToRowMap(event);
        jdbcTemplateEvent.update("INSERT INTO events (id, name, air_dates, base_price, rating, auditoriums) " +
                "VALUES (?, ?, ?, ?, ?, ?)", rowMapEvent);
    }

    private void update(@Nonnull Event event) {
        Object[] rowMapEvent = convertEventToRowMap(event);
        //Change the position of the eventId in the array for update to the database.
        Object[] rowMapEventForUpdate = new Object[rowMapEvent.length];
        rowMapEventForUpdate[rowMapEventForUpdate.length - 1] = rowMapEvent[0];
        System.arraycopy(rowMapEvent, 1, rowMapEventForUpdate, 0, rowMapEvent.length - 1);

        jdbcTemplateEvent.update("UPDATE events SET name = ?, air_dates = ?, base_price = ?, rating = ?, auditoriums = ? " +
                "WHERE id = ?", rowMapEventForUpdate);
    }

    public void remove(@Nonnull Event event) {
        final Long eventId = event.getId();
        jdbcTemplateEvent.update("DELETE FROM events WHERE id = ?", eventId);
    }

    public Event getById(@Nonnull Long eventId) {
        RowMapper<Event> eventMapRow = getEventMapRow();
        Event event = null;
        try {
            event = jdbcTemplateEvent.queryForObject("SELECT * FROM events WHERE id = ?", new Object[]{eventId}, eventMapRow);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The event does not exist with id=[%d] in the DB.\r\n", eventId);
        }
        return event;
    }

    public Collection<Event> getAll() {
        RowMapper<Event> eventMapRow = getEventMapRow();
        Collection<Event> allEvents = jdbcTemplateEvent.query("SELECT * FROM events", eventMapRow);
        return allEvents;
    }

    private RowMapper<Event> getEventMapRow() {
        RowMapper<Event> eventRowMapper = new RowMapper<Event>() {
            public Event mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Event event = new Event();
                event.setId(resultSet.getLong("id"));
                event.setName(resultSet.getString("name"));
                event.setAirDates((NavigableSet<LocalDateTime>) resultSet.getObject("air_dates"));
                event.setBasePrice(resultSet.getDouble("base_price"));
                event.setRating((EventRating) resultSet.getObject("rating"));
                event.setAuditoriums((NavigableMap<LocalDateTime, Auditorium>) resultSet.getObject("auditoriums"));
                return event;
            }
        };
        return eventRowMapper;
    }

    private Object[] convertEventToRowMap(@Nonnull Event event) {
        Long id = event.getId();
        String name = event.getName();
        NavigableSet<LocalDateTime> airDates = event.getAirDates();
        Double basePrice = event.getBasePrice();
        EventRating rating = event.getRating();
        NavigableMap<LocalDateTime, Auditorium> auditoriums = event.getAuditoriums();

        Object[] rowMapEvent = new Object[]{id, name, airDates, basePrice, rating, auditoriums};
        return rowMapEvent;
    }
}
