package org.example.blps_lab1.adapters.rest.lms;

import lombok.AllArgsConstructor;

import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.ports.course.CertificateManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/certificate")
@AllArgsConstructor
public class CertificateController {

    private AuthService authService;
    private CertificateManager certificateManagerService;


    @GetMapping("/{courseUUID}")
    public ResponseEntity<Map<String, String>> getMethodName(@PathVariable UUID courseUUID) {
        certificateManagerService.getCertificate(authService.getCurrentUser(), courseUUID);
        Map<String, String> res = new HashMap<>();
        res.put("message","курс успешно пройден, сертификат отправлен на почту");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/dolbaeb")
    public void dolbaeb(){
        certificateManagerService.getCertificate(null, null);
    }
}
