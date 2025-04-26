package org.example.blps_lab1.core.ports.course;

import java.io.File;

public interface CertificateGenerator {
    File generateCertificate(final String courseName, final String userName, String signaturePath) throws Exception;
}
