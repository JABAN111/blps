package org.example.blps_lab1.adapters;


import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.example.blps_lab1.core.domain.auth.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML parser for User objects. Supports saving single or multiple users and reading them back.
 */
@Service
public class XmlUserParser {
    @Value("${app.files.users}")
    private String filePath;
    private File xmlFile;
    private JAXBContext jaxbContext;


    @PostConstruct
    public void xmlConstruct() throws JAXBException {
        this.xmlFile = new File(filePath);
        this.jaxbContext = JAXBContext.newInstance(UsersWrapper.class, User.class);
    }

    public void save(User user) throws JAXBException {
        var prevData = parse();
        for(var d : prevData){
            if (d.getId().equals(user.getId())){

            }
        }
        prevData.add(user);
        save(prevData);
    }

    public void save(List<User> users) throws JAXBException {
        UsersWrapper wrapper = new UsersWrapper();
        wrapper.setUsers(users);

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(wrapper, xmlFile);
    }

    public List<User> parse() throws JAXBException {
        if (!xmlFile.exists()) {
            return new ArrayList<>();
        }
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        UsersWrapper wrapper = (UsersWrapper) unmarshaller.unmarshal(xmlFile);
        return wrapper.getUsers();
    }

    /**
     * Wrapper class for JAXB to handle list of User elements.
     */
    @XmlRootElement(name = "users")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UsersWrapper {
        @XmlElement(name = "user")
        private List<User> users = new ArrayList<>();

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }
}
