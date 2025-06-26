package hr.algebra.xiaohongshuiis.service;

import hr.algebra.xiaohongshuiis.model.AppUser;
import hr.algebra.xiaohongshuiis.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByName(username);

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new User(appUser.getName(), appUser.getPassword(), new ArrayList<>());
    }
}