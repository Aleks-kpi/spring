package ua.epam.spring.hometask.service.serviceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.NavigableSet;

@Component("userServiceDao")
public class UserServiceDao {
    @Autowired
    private JdbcTemplate jdbcTemplateUser;

    public UserServiceDao() {
    }

    @PostConstruct
    public void createTableUser() {
        jdbcTemplateUser.update("CREATE TYPE db.navigableSet EXTERNAL NAME 'java.util.NavigableSet' LANGUAGE JAVA");
        jdbcTemplateUser.update("CREATE TABLE users (id BIGINT, first_name VARCHAR(255), last_name VARCHAR(255), email VARCHAR(255), tickets db.navigableSet, birthday TIMESTAMP, number_of_discounts BIGINT, lucky BOOLEAN)");
    }

    public User save(@Nonnull User user) {
        final Long userId = user.getId();
        if (getById(userId) != null)
            update(user);
        else
            insert(user);
        return getById(userId);
    }

    private void insert(@Nonnull User user) {
        Object[] rowMapUser = convertUserToRowMap(user);
        jdbcTemplateUser.update("INSERT INTO users (id, first_name, last_name, email, tickets, birthday, number_of_discounts, lucky) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", rowMapUser);
    }

    private void update(@Nonnull User user) {
        Object[] rowMapUser = convertUserToRowMap(user);
        //Change the position of the userId in the array for update to the database.
        Object[] rowMapUserForUpdate = new Object[rowMapUser.length];
        rowMapUserForUpdate[rowMapUserForUpdate.length - 1] = rowMapUser[0];
        System.arraycopy(rowMapUser, 1, rowMapUserForUpdate, 0, rowMapUser.length - 1);

        jdbcTemplateUser.update("UPDATE users SET first_name = ?, last_name = ?, email = ?, tickets = ?, birthday = ?, " +
                "number_of_discounts = ?, lucky = ? WHERE id = ?", rowMapUserForUpdate);
    }

    public void remove(@Nonnull User user) {
        final Long userId = user.getId();
        jdbcTemplateUser.update("DELETE FROM users WHERE id = ?", userId);
    }

    public User getById(@Nonnull Long userId) {
        RowMapper<User> userMapRow = getUserMapRow();
        User user = null;
        try {
            user = jdbcTemplateUser.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{userId}, userMapRow);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The user does not exist with id=[%d] in the DB.\r\n", userId);
        }
        return user;
    }

    public Collection<User> getAll() {
        RowMapper<User> userMapRow = getUserMapRow();
        Collection<User> allUsers = jdbcTemplateUser.query("SELECT * FROM users", userMapRow);
        return allUsers;
    }

    private RowMapper<User> getUserMapRow() {
        RowMapper<User> userRowMapper = new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int numRow) throws SQLException {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setEmail(resultSet.getString("email"));
                user.setTickets((NavigableSet<Ticket>) resultSet.getObject("tickets"));
                user.setBirthday(resultSet.getTimestamp("birthday").toLocalDateTime());
                user.setNumberOfDiscounts(resultSet.getLong("number_of_discounts"));
                user.setLucky(resultSet.getBoolean("lucky"));
                return user;
            }
        };
        return userRowMapper;
    }

    private Object[] convertUserToRowMap(@Nonnull User user) {
        Long id = user.getId();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        NavigableSet<Ticket> tickets = user.getTickets();
        Timestamp birthday = Timestamp.valueOf(user.getBirthday());
        Long numberOfDiscount = user.getNumberOfDiscounts();
        Boolean lucky = user.isLucky();

        Object[] rowMapUser = new Object[]{id, firstName, lastName, email, tickets, birthday, numberOfDiscount, lucky};
        return rowMapUser;
    }
}