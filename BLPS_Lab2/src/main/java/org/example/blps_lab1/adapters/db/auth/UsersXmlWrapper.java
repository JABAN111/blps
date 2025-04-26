package org.example.blps_lab1.adapters.db.auth;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.blps_lab1.core.domain.auth.UserXml;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "users")
@Data
@AllArgsConstructor
public class UsersXmlWrapper {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "user")
    private List<UserXml> users;

    public UsersXmlWrapper() {
        this.users = new ArrayList<>();
    }
}