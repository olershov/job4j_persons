package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;
import java.util.Optional;

import static java.util.Collections.emptyList;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private PersonRepository persons;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Person> user = persons.findByLogin(login);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(login);
        }
        return new User(user.get().getLogin(), user.get().getPassword(), emptyList());
    }
}