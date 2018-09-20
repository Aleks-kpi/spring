package ua.epam.spring.hometask.service.serviceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Auditorium;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuditoriumServiceDao {
    @Resource(name = "auditoriums")
    private Set<Auditorium> auditoriums;
    @Autowired
    private JdbcTemplate jdbcTemplateAuditorium;

    public AuditoriumServiceDao() {
    }

    @PostConstruct
    public void createTableAuditorium() {
        jdbcTemplateAuditorium.update("CREATE TYPE db.setVipSeats EXTERNAL NAME 'java.util.Set' LANGUAGE JAVA");
        jdbcTemplateAuditorium.update("CREATE TABLE auditoriums (name VARCHAR(255), number_of_seats BIGINT, vip_seats db.setVipSeats)");
        //Save auditoriums to DB int the auditoriums tables
        for (Auditorium auditorium : auditoriums)
            insert(auditorium);
    }

    public Auditorium save(@Nonnull Auditorium auditorium) {
        final String auditoriumName = auditorium.getName();
        if (getByName(auditoriumName) != null)
            update(auditorium);
        else
            insert(auditorium);
        return getByName(auditoriumName);
    }

    private void insert(@Nonnull Auditorium auditorium) {
        Object[] rowMapEvent = convertAuditoriumToRowMap(auditorium);
        jdbcTemplateAuditorium.update("INSERT INTO auditoriums (name, number_of_seats, vip_seats) VALUES (?, ?, ?)", rowMapEvent);
    }

    private void update(@Nonnull Auditorium auditorium) {
        Object[] rowMapAuditorium = convertAuditoriumToRowMap(auditorium);
        //Change the position of the auditorium_name in the array for update to the database.
        Object[] rowMapAuditoriumForUpdate = new Object[rowMapAuditorium.length];
        rowMapAuditoriumForUpdate[rowMapAuditoriumForUpdate.length - 1] = rowMapAuditorium[0];
        System.arraycopy(rowMapAuditorium, 1, rowMapAuditoriumForUpdate, 0, rowMapAuditorium.length - 1);

        jdbcTemplateAuditorium.update("UPDATE auditoriums SET number_of_seats = ?, vip_seats = ? WHERE name = ?", rowMapAuditoriumForUpdate);
    }

    public void remove(@Nonnull Auditorium auditorium) {
        final String auditoriumName = auditorium.getName();
        jdbcTemplateAuditorium.update("DELETE FROM auditoriums WHERE name = ?", auditoriumName);
    }

    public Auditorium getByName(@Nonnull String auditoriumName) {
        RowMapper<Auditorium> auditoriumMapRow = getAuditoriumMapRow();
        Auditorium auditorium = null;
        try {
            auditorium = jdbcTemplateAuditorium.queryForObject("SELECT * FROM auditoriums WHERE name = ?", new Object[]{auditorium}, auditoriumMapRow);
        } catch (EmptyResultDataAccessException e) {
            //System.out.printf("   INFO: The auditorium does not exist with name=[%s] in the DB.\r\n", auditoriumName);
        }
        return auditorium;
    }

    public Set<Auditorium> getAll() {
        RowMapper<Auditorium> auditoriumMapRow = getAuditoriumMapRow();
        Set<Auditorium> allAuditoriums = jdbcTemplateAuditorium.query("SELECT * FROM auditoriums", auditoriumMapRow).stream().collect(Collectors.toSet());
        return allAuditoriums;
    }

    private RowMapper<Auditorium> getAuditoriumMapRow() {
        /*RowMapper<Auditorium> auditoriumRowMapper = (resultSet, numRow) -> {
            Auditorium auditorium = new Auditorium();
            auditorium.setName(resultSet.getString("name"));
            auditorium.setNumberOfSeats(resultSet.getLong("number_of_seats"));
            auditorium.setVipSeats((Set<Long>) resultSet.getObject("vip_seats"));
            return auditorium;
        };*/

        RowMapper<Auditorium> auditoriumRowMapper = new RowMapper<Auditorium>() {
            public Auditorium mapRow(ResultSet resultSet, int numRow) throws SQLException {
                Auditorium auditorium = new Auditorium();
                auditorium.setName(resultSet.getString("name"));
                auditorium.setNumberOfSeats(resultSet.getLong("number_of_seats"));
                auditorium.setVipSeats((Set<Long>) resultSet.getObject("vip_seats"));
                return auditorium;
            }
        };
        return auditoriumRowMapper;
    }

    private Object[] convertAuditoriumToRowMap(@Nonnull Auditorium auditorium) {
        String name = auditorium.getName();
        Long numberOfSeats = auditorium.getNumberOfSeats();
        Set<Long> vipSeats = auditorium.getVipSeats();

        Object[] rowMapAuditorium = new Object[]{name, numberOfSeats, vipSeats};
        return rowMapAuditorium;
    }
}