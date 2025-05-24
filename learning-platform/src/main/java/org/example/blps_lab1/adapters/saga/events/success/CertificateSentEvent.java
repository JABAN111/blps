package org.example.blps_lab1.adapters.saga.events.success;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.blps_lab1.core.domain.auth.UserXml;

import java.io.File;

@Data
@AllArgsConstructor
public class CertificateSentEvent {
    private UserXml user;
    private File pdf;
}
