package com.example.task.Controller;

import com.example.task.Entity.UserCertificate;
import com.example.task.Service.UserCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-certificates")
public class UserCertificateController {
    private final UserCertificateService userCertificateService;

    @Autowired
    public UserCertificateController(UserCertificateService userCertificateService) {
        this.userCertificateService = userCertificateService;
    }


    @GetMapping
    public List<UserCertificate> getAllUserCertificates() {
        return userCertificateService.getAllUserCertificates();
    }

    @GetMapping("/{userId}")
    public List<UserCertificate> getUserCertificatesByUserId(@PathVariable String userId) {
        return userCertificateService.getUserCertificatesByUserId(userId);
    }

    @PostMapping
    public UserCertificate addUserCertificate(
            @RequestParam String userId,
            @RequestParam int certificateId
    ) {
        return userCertificateService.addUserCertificate(userId, certificateId);
    }


    @DeleteMapping("/{id}")
    public void deleteUserCertificate(@PathVariable int id) {
        userCertificateService.deleteUserCertificate(id);
    }
}
