package advertise.service.impl;

import advertise.orm.dao.AdCategoryDao;
import advertise.orm.model.AdCategory;
import advertise.service.AdCategoryService;
import advertise.service.ErrorCode;
import advertise.service.Result;
import advertise.service.util.CollectionUtills;
import advertise.service.util.DefaultInsertable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component("adCategoryService")
public class AdCategoryServiceImpl implements AdCategoryService, DefaultInsertable<String, AdCategory> {

    @Autowired
    private AdCategoryDao adCategoryDao;

    public Result create(AdCategory adCategory) {
        Result validationResult = validate(adCategory);
        if (validationResult.isSuccess()) {
            adCategory.setId(UUID.randomUUID());
            adCategoryDao.save(adCategory);
        }

        return validationResult;
    }

    public AdCategory getById(UUID id) {
        return adCategoryDao.findById(id).orElse(null);
    }

    @Override
    public List<AdCategory> getAll() {
        return CollectionUtills.iterableToList(adCategoryDao.findAll());
    }

    @Override
    public boolean isExist(String fieldValue) {
        return adCategoryDao.findByName(fieldValue) != null;
    }

    @Override
    public void insert(AdCategory e) {
        create(e);
    }

    private Result validate(AdCategory adCategory) {
        Result result = Result.newResultInstance();
        if (Objects.isNull(adCategory.getName()) || adCategory.getName().isEmpty()) {
            result.addError(ErrorCode.AD_CATEGORY_ENTITY_INVAID_NAME_NOT_FOUND);
        }
        if (Objects.isNull(adCategory.getDescription()) || adCategory.getDescription().isEmpty()) {
            result.addError(ErrorCode.AD_CATEGORY_ENTITY_INVAID_DESCRIPTION_NOT_FOUND);
        }
        return result;
    }
}
