package advertise.web.util.def.data;

import advertise.orm.model.*;
import advertise.service.util.DefaultInsertable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * This class is created for Demo purpose. And creates default/sample data in the database
 * so user can view.
 *
 * Will create data for {@link AdCategory}, {@link Location} and {@link Ad} entity tables.
 * Image/picture resources are taken from the img folder in the webapp root.
 */
@Component
public class DefaultDataCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataCreator.class);

    @Value("${insert.default.data:false}")
    private boolean enableInsertDefaultData;

    @Autowired
    private DefaultInsertable<String, AdCategory> adCategoryService;

    @Autowired
    private DefaultInsertable<String, Location> locationService;

    @Autowired
    private DefaultInsertable<String, Ad> adService;

    @PostConstruct
    private void init() {
        if (enableInsertDefaultData) {
            checkAndCreate();
        }
    }

    private void checkAndCreate() {
        List<AdCategory> adCategories = getAdCategories();

        for (AdCategory category: adCategories) {
            try {
                if (!adCategoryService.isExist(category.getName())) {
                    adCategoryService.insert(category);
                }
            } catch (Exception e) {
                LOGGER.error("Could not insert AdCategory : {}", category.getName(), e);
            }
        }

        List<Location> locations = getLocations();

        for (Location location : locations) {
            try {
                if (!locationService.isExist(location.getName())) {
                    locationService.insert(location);
                }
            } catch (Exception e) {
                LOGGER.error("Could not insert Location : {}", location.getName(), e);
            }
        }

        List<Ad> ads = getAds(locations, adCategories);

        for (Ad ad : ads) {
            try {
                if (!adService.isExist(ad.getTitle())) {
                    adService.insert(ad);
                }
            } catch (Exception e) {
                LOGGER.error("Could not insert Ad : {}", ad.getTitle(), e);
            }
        }
    }

    private List<AdCategory> getAdCategories() {
        List<AdCategory> data = new ArrayList<>(8);
        data.add(new AdCategory("Property", "Property, in the abstract, is what belongs to or with something, whether as an attribute or as a component of said thing. In the context of this article, it is one or more components (rather than attributes), whether physical or incorporeal, of a person's estate; or so belonging to, as in being owned by, a person or jointly a group of people or a legal entity like a corporation or even a society."));
        data.add(new AdCategory("Vehicle", "Vehicle, in the abstract, is what belongs to or with something, whether as an attribute or as a component of said thing. In the context of this article, it is one or more components (rather than attributes), whether physical or incorporeal, of a person's estate; or so belonging to, as in being owned by, a person or jointly a group of people or a legal entity like a corporation or even a society."));
        data.add(new AdCategory("Electronics", "Electronics, in the abstract, is what belongs to or with something, whether as an attribute or as a component of said thing. In the context of this article, it is one or more components (rather than attributes), whether physical or incorporeal, of a person's estate; or so belonging to, as in being owned by, a person or jointly a group of people or a legal entity like a corporation or even a society."));
        data.add(new AdCategory("Mobile phones", "Mobile phones, in the abstract, is what belongs to or with something, whether as an attribute or as a component of said thing. In the context of this article, it is one or more components (rather than attributes), whether physical or incorporeal, of a person's estate; or so belonging to, as in being owned by, a person or jointly a group of people or a legal entity like a corporation or even a society."));
        data.add(new AdCategory("Jobs", "Jobs, in the abstract, is what belongs to or with something, whether as an attribute or as a component of said thing. In the context of this article, it is one or more components (rather than attributes), whether physical or incorporeal, of a person's estate; or so belonging to, as in being owned by, a person or jointly a group of people or a legal entity like a corporation or even a society."));
        //...
        return data;
    }

    private List<Location> getLocations() {
        List<Location> data = new ArrayList<>(10);
        data.add(new Location("Kandy", new Country("Sri Lanka", "Rs")));
        data.add(new Location("Colombo", new Country("Sri Lanka", "Rs")));
        data.add(new Location("Galle", new Country("Sri Lanka", "Rs")));
        data.add(new Location("NY", new Country("US", "USD")));
        data.add(new Location("NY", new Country("US", "USD")));
        data.add(new Location("Singapore", new Country("Singapore", "SD")));
        return data;
    }

    private List<Ad> getAds(List<Location> locations, List<AdCategory> adCategories) {

        assert locations.size() >= 5;
        assert adCategories.size() >= 5;
        assert locations.get(0).getId() != null;
        assert adCategories.get(0).getId() != null;

        User user = new User("test-abc@gmail.com", "user 1", "021892121");

        List<Ad> data = new ArrayList<>(12);
        String ad1Desc = "Special scratch-resistant coating to protect your device from daily scratches, dust, scrapes, and normal signs of wear. Touch Compatible: This screen protector is super thin, can maintain the touch feature of your. This kind of screen protector is 99% High Definition Clarity, 99% light transmittance, keeps the bright and colorful image quality. Strictly following the instructions to operate and you can apply a perfect screen protector easily (Dust and Bubble Free). For Samsung S8 Plus Black x2";
        List<String> ad1imgs = Arrays.asList("images/ad-img-1-1.jpg", "images/ad-img-1-2.jpg");
        data.add(new Ad("Samsung Galaxy S8 Plus Black Screen Protector", ad1Desc, new Date(), new Date(), adCategories.get(3).getId(), adCategories.get(3).getName(), SalesArea.INTERNATIONAL,  locations.get(0).getId(), locations.get(0).getName(), user, "100", ad1imgs));

        String ad2Desc = "Never pay hugely expensive cable or satellite fees again! Get access to your local news, weather, sitcoms, kids and sports programs, educational programs etc., Receives free broadcast High Definition Over-the-Air TV signals such as ABC, CBS, NBC, PBS, Fox, Univision and others. Enjoy crystal clear HDTV shows, 720p, 1080i, 1080p, 4K | ATSC available. This television antenna is compatible with all types of TV converter boxes and digital televisions/4K Ultra High Definition TVs.";
        List<String> ad2imgs = Arrays.asList("images/ad-img-2-1.jpg", "images/ad-img-2-2.jpg");
        data.add(new Ad("Indoor Amplified Digtial HDTV Antenna", ad2Desc, new Date(), new Date(), adCategories.get(2).getId(), adCategories.get(2).getName(), SalesArea.WITHIN_CITY, locations.get(1).getId(), locations.get(1).getName(), user, "100", ad2imgs));

        String ad3Desc = "With a 5.93\" 18: 9 edge-to-edge all-screen design, high screen-to-body ratio and 2160 x 1080 FHD+ resolution, the HUAWEI FullView display on the Huawei mate SE brings you an immersive visual experience. The dual-lens 16MP + 2MP rear camera offers an all new portrait mode, letting you capture professional and artistic photos in a single shot with an ultra-fast focus time";
        List<String> ad3imgs = Arrays.asList("images/ad-img-3-1.jpg", "images/ad-img-3-2.jpg");
        data.add(new Ad("Huawei Mate SE", ad3Desc, new Date(), new Date(), adCategories.get(3).getId(), adCategories.get(3).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad3imgs));

        String ad4Desc = "Smart Functionality: With built-in Wi-Fi you can wirelessly access popular streaming apps and services including Netflix, YouTube, Hulu Plus, Amazon Instant Video & more, Full HD 1080p Playback: Supports full HD 1080p Blu-ray disc playback when connect via HDMI on a compatible TV. The upscaling chip also improves the quality of online content, and upconverts DVD's to near HD quality for an improved viewing experience";
        List<String> ad4imgs = Arrays.asList("images/ad-img-4-1.jpg", "images/ad-img-4-2.jpg");
        data.add(new Ad("Sony BDPS3700 Streaming Blu-Ray Disc Player", ad4Desc, new Date(), new Date(), adCategories.get(2).getId(), adCategories.get(2).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad4imgs));

        String ad5Desc = "Global Version. Dual Main Camera: Wide angle lens 12MP 1.25μm f / 2.2 and Telephoto lens 12MP 1.0 μm, f / 2.6, Chip: Snapdragon 625, octa-core 2.0GHz, Adreno 506 650MHz graphics processor. Display: 5.5\" FHD LTPS Display 1920 x 1080, 403 PPI, Memory: 64GB ROM, 4GB RAM, Up to 128GB of expandable storage (microSD).";
        List<String> ad5imgs = Arrays.asList("images/ad-img-5-1.jpg", "images/ad-img-5-2.jpg");
        data.add(new Ad("Xiaomi MI A1", ad5Desc, new Date(), new Date(), adCategories.get(3).getId(), adCategories.get(3).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad5imgs));

        String ad6Desc = "Apple iPhone 6s smartphone. Announced Sep 2015. Features 4.7″ LED-backlit IPS LCD display, Apple A9 chipset, 12 MP primary camera, 5 MP front camera, 1715 mAh battery, 128 GB storage, 2 GB RAM, Ion-strengthened glass.";
        List<String> ad6imgs = Arrays.asList("images/ad-img-6-1.jpg", "images/ad-img-6-2.jpg");
        data.add(new Ad("Apple iPhone 6S 64GB", ad6Desc, new Date(), new Date(), adCategories.get(3).getId(), adCategories.get(3).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad6imgs));

        String ad7Desc = "It's true what they say - you don't fix what's not broken. And that's what Samsung did for the Galaxy S9 - it didn't change what was already great, it just tweaked specs wherever possible. And it has worked out just fine for them. Futuristic is what we used to call the Galaxy S7 and S8 design, but now the iconic glass curves are just mainstream. You can see them shine on the cheapest of smartphones, even on knock-offs, all the way up to the current Galaxy S9 series. The shape might be wearing off, but Samsung has managed to keep its coolness for yet another year thanks to some stunning choice of colors.";
        List<String> ad7imgs = Arrays.asList("images/ad-img-7-1.jpg", "images/ad-img-7-2.jpg");
        data.add(new Ad("Samsung Galaxy S9", ad7Desc, new Date(), new Date(), adCategories.get(3).getId(), adCategories.get(3).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad7imgs));

        String ad8Desc = "The information you include in the actual description of the job and the profile of the ideal candidate should come very easily to you – assuming you have written a proper job description and prepared a performance profile for the role. Select the key skills, core competencies and most relevant performance or success measures and include them. This is where you will eliminate those applicants who are not actually suitable for the role.";
        List<String> ad8imgs = Arrays.asList("images/ad-img-2-1.jpg", "images/ad-img-2-2.jpg");
        data.add(new Ad("Unique videos for need-to-fill job ads", ad8Desc, new Date(), new Date(), adCategories.get(4).getId(), adCategories.get(4).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad8imgs));

        String ad9Desc = "50% BRIGHTER THAN ORDINARY LED PROJECTORS: Being 50% brighter and sharper than comparable projectors on the market, it provides you with probably the best home cinema experience out there. However, as this projector is only ideal for home entertainment, we do NOT recommend it for use in PPT or business presentations.";
        List<String> ad9imgs = Arrays.asList("images/ad-img-9-1.jpg", "imagess/ad-img-9-2.jpg");
        data.add(new Ad("DBPOWER T20 LCD Mini Movie Projector", ad9Desc, new Date(), new Date(), adCategories.get(2).getId(), adCategories.get(2).getName(), SalesArea.WITHIN_CITY, locations.get(2).getId(), locations.get(2).getName(), user, "100", ad9imgs));

        String ad10Desc = "Dimensions (W x H x D): TV without stand: 49.1\" x 28.5\" x 3\", TV with stand: 49.1\" x 30.8\" x 8.7\". Smart functionality offers access to over 4,000 streaming channels featuring more than 450,000 movies and TV episodes via Roku TV. Pairs 4K Ultra HD picture clarity with the contrast, color, and detail of High Dynamic Range (HDR) for the most lifelike picture";
        List<String> ad10imgs = Arrays.asList("images/ad-img-10-1.jpg", "images/ad-img-10-2.jpg");
        data.add(new Ad("TCL 55S405 55-Inch 4K Ultra HD Roku Smart LED TV", ad10Desc, new Date(), new Date(), adCategories.get(2).getId(), adCategories.get(2).getName(), SalesArea.INTERNATIONAL, locations.get(3).getId(), locations.get(3).getName(), user, "200", ad10imgs));

        return data;
    }
}
