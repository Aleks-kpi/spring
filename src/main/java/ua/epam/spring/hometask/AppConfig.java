package ua.epam.spring.hometask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.epam.spring.hometask.dbConnect.ManagerConnect;
import ua.epam.spring.hometask.domain.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"ua.epam.spring.hometask.service.serviceImpl", "ua.epam.spring.hometask.service.serviceDao", "ua.epam.spring.hometask.domain",
        "ua.epam.spring.hometask.dbConnect", "ua.epam.spring.hometask.service.aspectDao"})
public class AppConfig {

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("auditorium.properties"));
        configurer.setIgnoreResourceNotFound(true);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return configurer;
    }

    @Bean
    public PropertyPlaceholderConfigurer propertyDataBase() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("database.properties"));
        configurer.setIgnoreResourceNotFound(true);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return configurer;
    }

    @Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(ManagerConnect managerConnect) {
        DataSource dataSource = managerConnect.getDataSource();
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public HashSet<Auditorium> auditoriums() {
        HashSet<Auditorium> auditoriums = new HashSet<>();
        HashSet<Long> vipSeats = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            vipSeats.add((long) (i + 1));
        }

        Auditorium auditorium_1 = new Auditorium();
        auditorium_1.setName("small_hall");
        auditorium_1.setNumberOfSeats(45);
        auditorium_1.setVipSeats(vipSeats);
        auditoriums.add(auditorium_1);

        Auditorium auditorium_2 = new Auditorium();
        auditorium_2.setName("big_hall");
        auditorium_2.setNumberOfSeats(95);
        auditorium_2.setVipSeats(vipSeats);
        auditoriums.add(auditorium_2);

        return auditoriums;
    }

    @Bean
    @Autowired
    public User user(LocalDateTime dateTime) {
        User user = new User();
        user.setId(77L);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setEmail("John_Smith@mail.com");
        user.setBirthday(dateTime.plusDays(-3));
        return user;
    }

    @Bean
    @Autowired
    public User student(LocalDateTime dateTime) {
        User user = new User();
        user.setId(55L);
        user.setFirstName("Mark");
        user.setLastName("Sweetman");
        user.setEmail("Mark_Sweetman@mail.com");
        user.setBirthday(dateTime.plusDays(-5));
        return user;
    }

    @Bean
    @Autowired
    public Event event(LocalDateTime eventTime, HashSet<Auditorium> auditoriums) {
        Event event = new Event();
        event.setId(15L);
        event.setName("Movie");
        event.getAirDates().add(eventTime);
        event.setBasePrice(100.00);
        event.setRating(EventRating.HIGH);
        event.getAuditoriums().put(eventTime, auditoriums.stream().findFirst().get());
        return event;
    }

    @Bean
    @Autowired
    public Event eventAnimate(LocalDateTime eventTime, HashSet<Auditorium> auditoriums) {
        Event event = new Event();
        event.setId(17L);
        event.setName("Cartoon");
        event.getAirDates().add(eventTime);
        event.setBasePrice(50.00);
        event.setRating(EventRating.MID);
        event.getAuditoriums().put(eventTime, auditoriums.stream().findFirst().get());
        return event;
    }

    @Bean
    @Autowired
    public Set<Ticket> ticketSet(@Qualifier("student") User user, @Qualifier("eventAnimate") Event event, LocalDateTime dateTime) {
        Set<Ticket> tickets = new HashSet<>();
        for (int i = 1; i <= 7; i++) {
            Ticket ticket = new Ticket(user, event, dateTime, (long) i);
            ticket.setId((long) i * 5);
            tickets.add(ticket);
        }
        return tickets;
    }

    @Bean
    @Autowired
    public Ticket ticket(User user, Event event, LocalDateTime dateTime, @Value("50") long seat) {
        Ticket ticket = new Ticket(user, event, dateTime, seat);
        ticket.setId(70L);
        return ticket;
    }

    @Bean
    public LocalDateTime eventTime() {
        return LocalDateTime.now();
    }
}