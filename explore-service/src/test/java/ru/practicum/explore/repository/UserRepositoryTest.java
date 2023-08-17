/*
package ru.practicum.explore.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.explore.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
//@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        for (int i = 1; i < 3; i++) {
            User userToSave = User.builder()
                    .name(i + "name")
                    .email(i + "email@email.ru")
                    .build();

            userRepository.save(userToSave);
        }
    }

    @Test
    public void findByIds_Normal() {
        List<User> expectedUsers = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            User userToSave = User.builder()
                    .id((long) i)
                    .name(i + "name")
                    .email(i + "email@email.ru")
                    .build();
            expectedUsers.add(userToSave);
        }
        final PageRequest page = PageRequest.of(0, 10);

        List<User> actualUsers = userRepository.findByIdInOrderById(List.of(1L, 2L), page).getContent();

        assertEquals(expectedUsers, actualUsers);
    }


}
*/
