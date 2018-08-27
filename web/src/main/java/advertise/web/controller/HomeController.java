/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2018 Yasitha Thilakaratne
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package advertise.web.controller;

import advertise.orm.model.*;
import advertise.service.AdCategoryService;
import advertise.service.AdService;
import advertise.service.LocationService;
import advertise.service.Result;
import advertise.web.dto.CountLessPagedResponseDTO;
import advertise.web.dto.ResponseEntityDTO;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * The {@link HomeController} class is the main MVC controller which is responsible to
 * handle all incoming requests in this DEMO application.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private static final String ALL_OPTION = "all";
    private static final String IMAGE_KEY = "imgs";

    @Autowired
    private AdService adService;

    @Autowired
    private AdCategoryService adCategoryService;

    @Autowired
    private LocationService locationService;

    @Value("${upload.file.system.path}")
    private String uploadDir;

    /**
     * The shared jsonMapper instance.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * The controller method to handle and direct to main ad view page.
     *
     * @return the view all ads.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        try {
            return "view";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    /**
     * The controller method to return all ads.
     * Configured to handle AJAX requests.
     * Expects Content-Type: application/json entity and produces application/json response.
     * Expects to contain 'resetPage' boolean node.
     *
     * @param httpEntity {@link HttpEntity} of {@link String}
     * @param request    {@link HttpServletRequest} instance
     * @return All ads list, Will be converted into json by interceptor.
     * @throws IOException when thrown from jsonMapper
     */
    @RequestMapping(value = "all/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CountLessPagedResponseDTO<Ad> getAds(HttpEntity<String> httpEntity, HttpServletRequest request) throws IOException {
        JsonNode root = mapper.readTree(httpEntity.getBody());
        boolean resetPage = root.get("resetPage").asBoolean(false);

        Pageable pageRequest = getPageRequest(resetPage, request.getSession());

        Slice<Ad> ads = adService.getAds(pageRequest);

        request.getSession().setAttribute("next", ads.nextPageable());

        return CountLessPagedResponseDTO.buildDTO(ads.getContent(), ads.hasNext(), ads.hasPrevious());
    }

    /**
     * The controller method to return ad view.
     *
     * @param modelMap {@link ModelMap}
     * @param id       id of the ad to view
     * @return the view ad.
     */
    @RequestMapping(value = "ad/{i}", method = RequestMethod.GET)
    public String viewAd(ModelMap modelMap, @PathVariable("i") UUID id) {
        modelMap.put("i", id);
        return "ad";
    }

    /**
     * The controller method to return ad.
     * Configured to handle AJAX requests.
     * Produces application/json response.
     *
     * @param id {@link UUID} identifier of the entity
     * @return Ad entity, Will be converted into json by interceptor.
     */
    @RequestMapping(value = "get/{i}", method = RequestMethod.GET)
    @ResponseBody
    public Ad getAd(@PathVariable("i") UUID id) {
        return adService.getAdById(id);
    }

    /**
     * This controller method has been designed to perform searches of ads. It allows
     * searching using {@link Ad#locationId}, {@link Ad#adCategoryId}, and {@link Ad#body}
     * full text search. Full text search capability depends on the search library
     * configurations. If the required parameters are not configured in the request
     * (eg: locationId: All, adCategoryId: All, text: '') will return all ads.
     *
     * Configured to handle AJAX requests.
     * Expects Content-Type: application/json entity and produces application/json response.
     * Expects to contain 'resetPage' boolean node.
     *
     * @param httpEntity {@link HttpEntity} of {@link String}
     * @param request    {@link HttpServletRequest} instance
     * @return list of ads that matches search criteria, Will be converted into json by interceptor.
     * @throws IOException when thrown from jsonMapper
     */
    @RequestMapping(value = "search/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CountLessPagedResponseDTO<Ad> searchAds(HttpEntity<String> httpEntity, HttpServletRequest request) throws IOException {
        JsonNode root = mapper.readTree(httpEntity.getBody());

        boolean resetPage = root.get("resetPage").asBoolean(false);

        String location = root.get("location").asText();
        UUID locationId = null;
        if (!location.isEmpty() && !location.equals(ALL_OPTION)) {
            locationId = UUID.fromString(location);
        }

        String adCategory = root.get("category").asText();
        UUID adCategoryId = null;
        if (!adCategory.isEmpty() && !adCategory.equals(ALL_OPTION)) {
            adCategoryId = UUID.fromString(adCategory);
        }

        String search = root.get("phrase").getTextValue();

        Pageable pageRequest = getPageRequest(resetPage, request.getSession());

        Slice<Ad> ads = adService.getSearchResult(pageRequest, adCategoryId, locationId, search);

        request.getSession().setAttribute("next", ads.getPageable());

        return CountLessPagedResponseDTO.buildDTO(ads.getContent(), ads.hasNext(), false);
    }

    /**
     * The controller method to get all {@link AdCategory}s.
     *
     * @return list of {@link AdCategory}, Will be converted into json by interceptor.
     */
    @RequestMapping(value = "ad-categories", method = RequestMethod.GET)
    @ResponseBody
    public List<AdCategory> getAdCategories() {
        return adCategoryService.getAll();
    }

    /**
     * The controller method to get all {@link Location}s.
     *
     * @return list of {@link Location}, Will be converted into json by interceptor.
     */
    @RequestMapping(value = "locations", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getLocations() {
        return locationService.getAll();
    }

    /**
     * The controller method to get all {@link SalesArea}s.
     *
     * @return list of {@link SalesArea}, Will be converted into json by interceptor.
     */
    @RequestMapping(value = "sales-areas", method = RequestMethod.GET)
    @ResponseBody
    public List<SalesArea> getSalesAreas() {
        return Arrays.asList(SalesArea.values());
    }

    /**
     * The controller method to handle and direct to main ad create page.
     *
     * @return the view create.
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String createPage() {
        return "create";
    }

    /**
     * The controller method to create ad with received json request.
     *
     * Expects json to be a serialized {@link Ad} entity. entity will be validated before persisting.
     *
     * @param ad      {@link Ad} entity to save.
     * @param session {@link HttpSession}
     * @return ResponseEntityDTO denoting state and message.
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntityDTO createAd(@RequestBody Ad ad, HttpSession session) {
        try {
            Object img;
            if ((img = session.getAttribute(IMAGE_KEY)) != null) {
                ad.setImgs((List<String>) img);
            }

            Result result = adService.create(ad);

            return result.onResultCallAndGet((message, extras) -> ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.SUCCESS, "Ad Saved"),
                    (message, errorCodes, extras) -> ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, null));
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred. ", e);
            return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, "failed: internal error");
        }
    }

    /**
     * The controller method to upload an image. The image must be encoded as a multipart file. The image
     * will be saved in the given location of the file system and the relative path will be added to the
     * {@link HttpSession} of the user.
     *
     * @param file    {@link MultipartFile} containing an image
     * @param session {@link HttpSession}
     * @return ResponseEntityDTO denoting state and message.
     */
    @RequestMapping(value = "add-image", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntityDTO addImage(@RequestParam(value = "file") MultipartFile file, HttpSession session) {
        String fileName;
        try {
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();

                if (Objects.isNull(file.getContentType()) || !(file.getContentType()).startsWith("image"))
                    return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, "failed: invalid file type");

                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    if (!dir.mkdirs())
                        return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, "failed: could not create the directory");
                }

                String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'), file.getOriginalFilename().length());
                fileName = "ad_img_" + System.currentTimeMillis() + '_' + UUID.randomUUID() + extension;
                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                LOGGER.info("Server File Location=" + serverFile.getAbsolutePath());

            } else {
                return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, "failed: empty image");
            }

            List<String> images = (List<String>) Optional.ofNullable(session.getAttribute(IMAGE_KEY)).orElse(new ArrayList<String>());

            images.add("uploads" + File.separator + fileName);
            session.setAttribute(IMAGE_KEY, images);
            return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.SUCCESS, "success", fileName);
        } catch (Exception e) {
            return ResponseEntityDTO.getResponse(ResponseEntityDTO.Status.ERROR, "failed: internal error");
        }
    }

    /**
     * REST API method to re-index ads when required.
     * Will be executed asynchronously and immediately will be returned.
     *
     * @return status {@link String}
     */
    @RequestMapping("/re-index")
    @ResponseBody
    public String reCreateIndex() {
        try {
            adService.reIndex();
            return "PROCESSING IN BACKGROUND";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    private Pageable getPageRequest(boolean resetPage, HttpSession userSession) {
        Pageable pageRequest;
        if (resetPage)
            pageRequest = CassandraPageRequest.of(0, 10);
        else
            pageRequest = (Pageable) userSession.getAttribute("next");
        return pageRequest;
    }
}
