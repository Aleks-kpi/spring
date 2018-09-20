package ua.epam.spring.hometask.dbConnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.sql.SQLException;

@Component
public class ManagerConnect {
    private DriverManagerDataSource dataSource;

    @Autowired
    public ManagerConnect(@Value("${jdbc.driverClassName}") String driverName, @Value("${jdbc.url}") String url,
                          @Value("${jdbc.username}") String username, @Value("${jdbc.password}") String password) {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    public DriverManagerDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DriverManagerDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PreDestroy
    public void disconnect() throws SQLException {
        dataSource.getConnection().close();
    }
}
