package advertise.service.impl;

import advertise.lucene.LuceneSearchProcessor;
import advertise.lucene.LuceneIndexableEntityService;
import advertise.orm.dao.AdDao;
import advertise.orm.model.Ad;
import advertise.orm.model.AdCategory;
import advertise.orm.model.Location;
import advertise.service.*;
import advertise.service.util.CollectionUtills;
import advertise.service.util.DefaultInsertable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service("adService")
public class AdServiceImpl implements AdService, LuceneIndexableEntityService<UUID, Ad>, DefaultInsertable<String, Ad> {

    @Autowired
    private AdDao adDao;

    @Autowired
    private AdCategoryService adCategoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LuceneSearchProcessor<UUID, Ad> legacySearchProcessor;

    public Result create(Ad ad) {
        Result validationResult = validateAd(ad);
        verifyAndSetLocation(ad, validationResult);
        verifyAndSetAdCategory(ad, validationResult);
        if (validationResult.isSuccess()){
            LocalDateTime now  = LocalDateTime.now();
            ad.setPostedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            ad.setExpireDate(Date.from(now.plusDays(90).atZone(ZoneId.systemDefault()).toInstant()));
            ad.setId(UUID.randomUUID());

            Ad savedAd = adDao.save(ad);

            if (savedAd != null) {
                legacySearchProcessor.addIndex(ad.getID(), ad.getTitle());
            }
        }
        return validationResult;
    }

    @Override
    public Ad getAdById(UUID id) {
        return adDao.findById(id).orElse(null);
    }

    public Slice<Ad> getAds(Pageable pageRequest) {
        return adDao.findAll(pageRequest);
    }

    public Slice<Ad> getSearchResult(Pageable pageRequest, UUID adCategoryId, UUID locationId, String title) {
        if (adCategoryId != null && locationId == null) {
            return adDao.findByAdCategoryId(pageRequest, adCategoryId);
        } else if (adCategoryId == null && locationId != null) {
            return adDao.findByLocationId(pageRequest, locationId);
        } else if (adCategoryId != null && locationId != null) {
            return adDao.findByAdCategoryIdAndLocationId(pageRequest, adCategoryId, locationId);
        } else if (title != null && !title.isEmpty()) {
            List<UUID> ids = legacySearchProcessor.searchForIds(title);
            return adDao.findAllByIdIn(pageRequest, ids);
        }
        return getAds(pageRequest);
    }

    @Override
    public List<Ad> getIndexableEntities() {
        return CollectionUtills.iterableToList(adDao.findAll());
    }

    @Override
    public List<Ad> getAll() {
        return CollectionUtills.iterableToList(adDao.findAll());
    }

    @Override
    public void reIndex() {
        legacySearchProcessor.reIndexAsync();
    }

    @Override
    public List<Ad> getEntitiesByIds(List<UUID> ids) throws NotImplementedException {
        return CollectionUtills.iterableToList(adDao.findAllById(ids));
    }

    @Override
    public Optional<Ad> getEntityById(UUID id) {
        return adDao.findById(id);
    }

    @Override
    public boolean isExist(String fieldValue) {
        return adDao.existsByTitle(fieldValue).isPresent();
    }

    @Override
    public void insert(Ad e) {
        create(e);
    }

    private Result validateAd(Ad ad) {
        Result result = Result.newResultInstance();
        if (ad.getTitle() == null || ad.getTitle().isEmpty()) {
            result.addError(ErrorCode.AD_ENTITY_INVALID_TITLE_NOT_FOUND);
        }
        if (ad.getBody() == null || ad.getBody().isEmpty()) {
            result.addError(ErrorCode.AD_ENTITY_INVALID_BODY_NOT_FOUND);
        }
        if (ad.getAdCategoryId() == null) {
            result.addError(ErrorCode.AD_ENTITY_INVALID_CATEGORY_NOT_FOUND);
        }
        if (ad.getLocationId() == null) {
            result.addError(ErrorCode.AD_ENTITY_INVALID_LOCATION_NOT_FOUND);
        }
        return result;
    }

    private void verifyAndSetLocation(Ad ad, Result result) {
        Location location = locationService.getById(ad.getLocationId());
        if (location != null)
            ad.setLocation(location.getName());
        else
            result.addError(ErrorCode.LOCATION_ID_INVALID_NOT_FOUND);
    }

    private void verifyAndSetAdCategory(Ad ad, Result result) {
        AdCategory adCategory = adCategoryService.getById(ad.getAdCategoryId());
        if (adCategory != null)
            ad.setAdCategory(adCategory.getName());
        else
            result.addError(ErrorCode.AD_CATEGORY_ID_INVALID_NOT_FOUND);
    }
}
