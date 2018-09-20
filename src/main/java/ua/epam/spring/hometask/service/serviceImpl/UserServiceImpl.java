package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.UserService;
import ua.epam.spring.hometask.service.serviceDao.UserServiceDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.Collection;

@Component("userServiceImpl")
public class UserServiceImpl implements UserService {
    @Resource(name = "userServiceDao")
    private UserServiceDao userServiceDao;

    public UserServiceImpl() {
    }

    public UserServiceImpl(UserServiceDao userServiceDao) {
        this.userServiceDao = userServiceDao;
    }

    @Nullable
    @Override
    public User getUserByEmail(@Nonnull String email) {
        final Collection<User> allUsers = getAll();
        for (User user : allUsers) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    @Override
    public User save(@Nonnull User user) {
        return userServiceDao.save(user);
    }

    @Override
    public void remove(@Nonnull User user) {
        userServiceDao.remove(user);
    }

    @Override
    public User getById(@Nonnull Long userId) {
        return userServiceDao.getById(userId);
    }

    @Nonnull
    @Override
    public Collection<User> getAll() {
        return userServiceDao.getAll();
    }
}
