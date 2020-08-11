package com.newuser.registration.resource;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newuser.registration.domain.ApiResponse;
import com.newuser.registration.domain.User;
import com.newuser.registration.repository.UserRepository;
import com.newuser.registration.service.FileStorageService;
import com.newuser.registration.service.UserService;
import com.newuser.registration.util.ApiConstants;
import io.github.jhipster.web.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserResource {

    @Autowired
    UserService userService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = ApiConstants.USER_URI, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse createUser(@RequestParam(value = ApiConstants.USER_JSON_PARAM) String userJson,
                                  @RequestParam(value = ApiConstants.USER_FILE_PARAM) MultipartFile file)
            throws JsonParseException, JsonMappingException, IOException {
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(ApiConstants.DOWNLOAD_PATH)
                .path(fileName).toUriString();
        User user = objectMapper.readValue(userJson, User.class);
        user.setPhotoPath(fileDownloadUri);
        userService.createUser(user);

        return new ApiResponse(ApiConstants.SUCCESS_CODE, ApiConstants.SUCCESS_MSG);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUsersById(@PathVariable Long id) {
        Optional<User> user = userService.findOne(id);
        return ResponseUtil.wrapOrNotFound(user);
    }

    @PutMapping("/users/editProfile")
    public ResponseEntity<User> updateUser(@RequestParam(value = ApiConstants.USER_JSON_PARAM) String userJson)
            throws Exception {
        User user = objectMapper.readValue(userJson,User.class);
        if (user.getId() == null) {
            throw new Exception("Invalid ID");
        }
        User currentUser = userRepository.findByUserDetails(user.getId());
        String photoPath = currentUser.getPhotoPath();
        user.setPhotoPath(photoPath);
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PutMapping("/users/editPhoto/{id}")
    public ResponseEntity<User> updateProPic(@RequestParam(value = ApiConstants.USER_FILE_PARAM) MultipartFile file,
                                             @PathVariable Long id) throws IOException {
        User userDetails = userRepository.findByUserDetails(id);
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(ApiConstants.DOWNLOAD_PATH)
                .path(fileName).toUriString();
        userDetails.setPhotoPath(fileDownloadUri);
        userDetails = userService.createUser(userDetails);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}