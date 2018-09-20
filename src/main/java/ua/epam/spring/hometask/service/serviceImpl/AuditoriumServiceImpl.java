package ua.epam.spring.hometask.service.serviceImpl;

import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Auditorium;
import ua.epam.spring.hometask.service.AuditoriumService;
import ua.epam.spring.hometask.service.serviceDao.AuditoriumServiceDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.Set;

@Component("auditoriumServiceImpl")
public class AuditoriumServiceImpl implements AuditoriumService {
    @Resource(name = "auditoriumServiceDao")
    private AuditoriumServiceDao auditoriumServiceDao;

    public AuditoriumServiceImpl() {
    }

    public AuditoriumServiceImpl(AuditoriumServiceDao auditoriumServiceDao) {
        this.auditoriumServiceDao = auditoriumServiceDao;
    }

    @Nonnull
    @Override
    public Set<Auditorium> getAll() {
        return auditoriumServiceDao.getAll();
    }

    @Nullable
    @Override
    public Auditorium getByName(@Nonnull String auditoriumName) {
        return auditoriumServiceDao.getByName(auditoriumName);
    }
}
