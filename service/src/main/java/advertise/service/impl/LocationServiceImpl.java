package advertise.service.impl;

import advertise.orm.dao.LocationDao;
import advertise.orm.model.Location;
import advertise.service.ErrorCode;
import advertise.service.LocationService;
import advertise.service.Result;
import advertise.service.util.CollectionUtills;
import advertise.service.util.DefaultInsertable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service("locationService")
public class LocationServiceImpl implements LocationService, DefaultInsertable<String, Location> {

    @Autowired
    private LocationDao locationDao;

    @Override
    public Result create(Location location) {
        Result validationResult = validate(location);
        if (validationResult.isSuccess()) {
            location.setId(UUID.randomUUID());
            locationDao.save(location);
        }
        return validationResult;
    }

    public Location getById(UUID id) {
        return locationDao.findById(id).orElse(null);
    }

    @Override
    public List<Location> getAll() {
        return CollectionUtills.iterableToList(locationDao.findAll());
    }

    @Override
    public boolean isExist(String fieldValue) {
        return locationDao.existsByName(fieldValue).isPresent();
    }

    @Override
    public void insert(Location e) {
        create(e);
    }

    private Result validate(Location location) {
        Result result = Result.newResultInstance();
        if (Objects.isNull(location.getName()) || location.getName().isEmpty()) {
            result.addError(ErrorCode.LOCATION_ENTITY_INVAID_NAME_NOT_FOUND);
        }
        if (Objects.isNull(location.getCountry()) || Objects.isNull(location.getCountry().getName())) {
            result.addError(ErrorCode.LOCATION_ENTITY_INVAID_COUNTRY_NOT_FOUND);
        }
        return result;
    }
}
