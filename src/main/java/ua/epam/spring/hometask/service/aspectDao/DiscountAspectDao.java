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
import java.util.Map;

@Component("discountAspectDao")
public class DiscountAspectDao {
    @Autowired
    private JdbcTemplate jdbcTemplateDiscount;

    public DiscountAspectDao() {
    }

    @PostConstruct
    public void createTableDiscount() {
        jdbcTemplateDiscount.update("CREATE TYPE db.hashMap EXTERNAL NAME 'java.util.HashMap' LANGUAGE JAVA");
        jdbcTemplateDiscount.update("CREATE TABLE total_discount (id BIGINT, discount_map db.hashMap)");
    }

    public Long findUserId(@Nonnull User user) {
        Long userId = null;
        try {
            userId = jdbcTemplateDiscount.queryForObject("SELECT id FROM total_discount WHERE id = ?", new Object[]{user.getId()}, Long.class);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The user %s %s does not exist with in the {total_discount} table.\r\n", user.getFirstName(), user.getLastName());
        }
        return userId;
    }

    public void insert(@Nonnull User user, Map<String, Integer> discounts) {
        jdbcTemplateDiscount.update("INSERT INTO total_discount (id, discount_map) VALUES (?, ?)", new Object[]{user.getId(), discounts});
    }

    public void update(@Nonnull User user, Map<String, Integer> discounts) {
        jdbcTemplateDiscount.update("UPDATE total_discount SET discount_map = ? WHERE id = ?", new Object[]{discounts, user.getId()});
    }

    public Map<String, Integer> getDiscountForUser(@Nonnull User user) {
        Map<String, Integer> discount = null;
        try {
            discount = jdbcTemplateDiscount.queryForObject("SELECT discount_map FROM total_discount WHERE id = ?", new Object[]{user.getId()}, getDiscountMapRow());
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The user %s %s does not exist with in the {total_discount} table.\r\n", user.getFirstName(), user.getLastName());
        }
        return discount;
    }

    public Collection<DiscountUserMap> getAllDiscount() {
        RowMapper<DiscountUserMap> discountUserMapRow = getDiscountUserMapRow();
        Collection<DiscountUserMap> allDiscounts = jdbcTemplateDiscount.query("SELECT * FROM total_discount", discountUserMapRow);
        return allDiscounts;
    }

    private RowMapper<Map<String, Integer>> getDiscountMapRow() {
        RowMapper discountRowMapper = new RowMapper<Map<String, Integer>>() {
            public Map<String, Integer> mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Map<String, Integer> discountForUser = (Map<String, Integer>) resultSet.getObject("discount_map");
                return discountForUser;
            }
        };
        return discountRowMapper;
    }

    private RowMapper<DiscountUserMap> getDiscountUserMapRow() {
        RowMapper discountRowMapper = new RowMapper<DiscountUserMap>() {
            public DiscountUserMap mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Long userId = resultSet.getLong("id");
                Map<String, Integer> discountForUser = (Map<String, Integer>) resultSet.getObject("discount_map");
                DiscountUserMap discountUserMap = new DiscountUserMap(userId, discountForUser);
                return discountUserMap;
            }
        };
        return discountRowMapper;
    }

    public class DiscountUserMap {
        private Long userId;
        private Map<String, Integer> discount;

        public DiscountUserMap(Long userId, Map<String, Integer> discount) {
            this.userId = userId;
            this.discount = discount;
        }

        public Long getUserId() {
            return userId;
        }

        public Map<String, Integer> getDiscount() {
            return discount;
        }
    }
}