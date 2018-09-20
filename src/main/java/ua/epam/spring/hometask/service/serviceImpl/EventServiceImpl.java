package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.service.EventService;
import ua.epam.spring.hometask.service.serviceDao.EventServiceDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;

@Component("eventServiceImpl")
public class EventServiceImpl implements EventService {
    @Resource(name = "eventServiceDao")
    private EventServiceDao eventServiceDao;

    public EventServiceImpl() {
    }

    public EventServiceImpl(EventServiceDao eventServiceDao) {
        this.eventServiceDao = eventServiceDao;
    }

    @Nullable
    @Override
    public Event getByName(@Nonnull String eventName) {
        final Collection<Event> events = getAll();
        for (Event event : events) {
            if (event.getName().equals(eventName))
                return event;
        }
        return null;
    }

    @Override
    public Event save(@Nonnull Event event) {
        return eventServiceDao.save(event);
    }

    @Override
    public void remove(@Nonnull Event event) {
        eventServiceDao.remove(event);
    }

    @Override
    public Event getById(@Nonnull Long eventId) {
        return eventServiceDao.getById(eventId);
    }

    @Nonnull
    @Override
    public Collection<Event> getAll() {
        return eventServiceDao.getAll();
    }

    @Nullable
    public Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to) {
        final Collection<Event> allEvents = getAll();
        Collection<Event> eventsDateRange = new TreeSet<>();
        NavigableSet<LocalDateTime> airDate = new TreeSet<>();
        for (Event event : allEvents) {
            for (LocalDateTime dateTime : event.getAirDates()) {
                if (dateTime.isAfter(from) && dateTime.isBefore(to))
                    eventsDateRange.add(event);
            }
        }
        return eventsDateRange;
    }

    public Collection<Event> getNextEvents(LocalDateTime to) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        final Collection<Event> eventsDateRange = getForDateRange(nowDateTime, to);
        return eventsDateRange;
    }
}
