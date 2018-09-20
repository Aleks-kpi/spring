package ua.epam.spring.hometask.service.aspectDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

@Component("luckyWinnerAspectDao")
public class LuckyWinnerAspectDao {
    @Autowired
    private JdbcTemplate jdbcTemplateLuckyWinner;

    public LuckyWinnerAspectDao() {
    }

    @PostConstruct
    public void createTableLuckyWinner() {
        jdbcTemplateLuckyWinner.update("CREATE TYPE db.linkedList EXTERNAL NAME 'java.util.LinkedList' LANGUAGE JAVA");
        jdbcTemplateLuckyWinner.update("CREATE TABLE lucky_winner (user_id BIGINT, lucy_message db.linkedList)");
    }

    public Long findUserId(@Nonnull User user) {
        Long userId = null;
        try {
            userId = jdbcTemplateLuckyWinner.queryForObject("SELECT user_id FROM lucky_winner WHERE user_id = ?", new Object[]{user.getId()}, Long.class);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The user %s %s does not exist with in the {lucky_winner} table.\r\n", user.getFirstName(), user.getLastName());
        }
        return userId;
    }

    public void insert(@Nonnull User user, LinkedList<String> lucyMessage) {
        jdbcTemplateLuckyWinner.update("INSERT INTO lucky_winner (user_id, lucy_message) VALUES (?, ?)", new Object[]{user.getId(), lucyMessage});
    }

    public void update(@Nonnull User user, LinkedList<String> lucyMessage) {
        jdbcTemplateLuckyWinner.update("UPDATE lucky_winner SET lucy_message = ? WHERE user_id = ?", new Object[]{lucyMessage, user.getId()});
    }

    public LinkedList<String> getLucyMessageForUser(@Nonnull User user) {
        LinkedList<String> lucyMessage = null;
        try {
            lucyMessage = jdbcTemplateLuckyWinner.queryForObject("SELECT lucy_message FROM lucky_winner WHERE user_id = ?", new Object[]{user.getId()}, getLucyMessageMapRow());
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The user %s %s does not exist with in the {lucky_winner} table.\r\n", user.getFirstName(), user.getLastName());
        }
        return lucyMessage;
    }

    public Collection<LuckyWinnerUser> getAllLuckyWinner() {
        RowMapper<LuckyWinnerUser> luckyWinnerUserMapRow = getLuckyWinnerUserMapRow();
        Collection<LuckyWinnerUser> allLuckyWinners = jdbcTemplateLuckyWinner.query("SELECT * FROM lucky_winner", luckyWinnerUserMapRow);
        return allLuckyWinners;
    }

    private RowMapper<LinkedList<String>> getLucyMessageMapRow() {
        RowMapper lucyMessageRowMapper = new RowMapper<LinkedList<String>>() {
            public LinkedList<String> mapRow(ResultSet resultSet, int numRow) throws SQLException {
                LinkedList<String> lucyMessageForUser = (LinkedList<String>) resultSet.getObject("lucy_message");
                return lucyMessageForUser;
            }
        };
        return lucyMessageRowMapper;
    }

    private RowMapper<LuckyWinnerUser> getLuckyWinnerUserMapRow() {
        RowMapper luckyWinnerRowMapper = new RowMapper<LuckyWinnerUser>() {
            public LuckyWinnerUser mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Long userId = resultSet.getLong("user_id");
                LinkedList<String> discountForUser = (LinkedList<String>) resultSet.getObject("lucy_message");
                LuckyWinnerUser luckyWinnerUser = new LuckyWinnerUser(userId, discountForUser);
                return luckyWinnerUser;
            }
        };
        return luckyWinnerRowMapper;
    }

    public class LuckyWinnerUser {
        private Long userId;
        private LinkedList<String> lucyMessage;

        public LuckyWinnerUser(Long userId, LinkedList<String> lucyMessage) {
            this.userId = userId;
            this.lucyMessage = lucyMessage;
        }

        public Long getUserId() {
            return userId;
        }

        public LinkedList<String> getLucyMessage() {
            return lucyMessage;
        }
    }
}