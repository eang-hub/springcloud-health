package com.springhealth.user.services;

import com.springhealth.user.domain.User;
import com.springhealth.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUserName(String userName) {

        return userRepository.findUserByUserName(userName);
    }
}
