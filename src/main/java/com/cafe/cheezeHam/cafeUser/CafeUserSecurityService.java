package com.cafe.cheezeHam.cafeUser;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CafeUserSecurityService implements UserDetailsService {

    private final CafeUserRepository cafeUserRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<CafeUser> _cafeUser = this.cafeUserRepository.findByid(id);

        if (_cafeUser.isEmpty()) {
            throw new UsernameNotFoundException("일치하는 아이디를 찾을수 없습니다.");
        }
        CafeUser cafeUser = _cafeUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(cafeUser.getROLE().equals("ADMIN")) {
            authorities.add(new SimpleGrantedAuthority(CafeUserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(CafeUserRole.USER.getValue()));
        }

        return new User(cafeUser.getId(), cafeUser.getPassword(), authorities);
    }
}
