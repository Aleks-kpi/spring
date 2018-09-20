package ua.epam.spring.hometask.service.aspectDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("counterAspectDao")
public class CounterAspectDao {
    @Autowired
    private JdbcTemplate jdbcTemplateCounter;

    @PostConstruct
    public void createTableCounter() {
        jdbcTemplateCounter.update("CREATE TABLE event_type_count (event_type VARCHAR(255), event_name VARCHAR(255), count_event_by_name INTEGER)");
    }

    //Implementing CRUD operations using the jdbc template for all counts in aspect: eventName, eventPrice and frequencyBookTicket and to save in the event_name_count table
    public String findEventNameByEventType(@Nonnull String eventType, @Nonnull String eventName) {
        String findEvent = null;
        try {
            findEvent = jdbcTemplateCounter.queryForObject("SELECT event_name FROM event_type_count WHERE event_type = ? AND event_name = ?", new Object[]{eventType, eventName}, String.class);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The event %s type %s does not exist with in the {event_name_count} table.\r\n", eventName, eventType);
        }
        return findEvent;
    }

    public void insertEventByEventType(@Nonnull String eventType, @Nonnull String eventName, @Nonnull Integer eventCount) {
        jdbcTemplateCounter.update("INSERT INTO event_type_count (event_type, event_name, count_event_by_name) VALUES (?, ?, ?)", new Object[]{eventType, eventName, eventCount});
    }

    public void updateEventByEventType(@Nonnull String eventType, @Nonnull String eventName, @Nonnull Integer eventCount) {
        jdbcTemplateCounter.update("UPDATE event_type_count SET count_event_by_name = ? WHERE event_type = ? AND event_name = ?", new Object[]{eventCount, eventType, eventName});
    }

    public Integer getCountEventByEventTypeAndName(@Nonnull String eventType, @Nonnull String eventName) {
        Integer countEvent = null;
        try {
            countEvent = jdbcTemplateCounter.queryForObject("SELECT count_event_by_name FROM event_type_count WHERE event_type = ? AND event_name = ?", new Object[]{eventType, eventName}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The event %s does not exist with in the {event_name_count} table.\r\n", eventName);
        }
        return countEvent;
    }

    public Integer getLastTimeTicketBooked(@Nonnull String eventType) {
        Integer lastNumberTicketBooked = null;
        try {
            lastNumberTicketBooked = jdbcTemplateCounter.queryForObject("SELECT MAX(CAST(event_name AS INT)) FROM event_type_count WHERE event_type = ?", new Object[]{eventType}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            System.out.printf("INFO: The tickets not booked yet.\r\n");
        }
        return lastNumberTicketBooked;
    }

    public Collection<CounterAspectResponse> getAllEventCountByType(String eventType) {
        RowMapper<CounterAspectResponse> luckyWinnerUserMapRow = getEventNameMapRow();
        Collection<CounterAspectResponse> allEventNameCounts = jdbcTemplateCounter.query("SELECT * FROM event_type_count WHERE event_type = ?", new Object[]{eventType}, luckyWinnerUserMapRow);
        return allEventNameCounts;
    }

    private RowMapper<CounterAspectResponse> getEventNameMapRow() {
        RowMapper counterRowMapper = new RowMapper<CounterAspectResponse>() {
            public CounterAspectResponse mapRow(ResultSet resultSet, int numRow) throws SQLException {
                String eventType = resultSet.getString("event_type");
                String eventName = resultSet.getString("event_name");
                Integer countEvent = resultSet.getInt("count_event_by_name");
                CounterAspectResponse counterAspectResponse = new CounterAspectResponse(eventType, eventName, countEvent);
                return counterAspectResponse;
            }
        };
        return counterRowMapper;
    }

    public class CounterAspectResponse {
        private String eventType;
        private String eventName;
        private Integer count;

        public CounterAspectResponse(String eventType, String eventName, Integer count) {
            this.eventType = eventType;
            this.eventName = eventName;
            this.count = count;
        }

        public String getEventType() {
            return eventType;
        }

        public String getEventName() {
            return eventName;
        }

        public Integer getCount() {
            return count;
        }
    }
}