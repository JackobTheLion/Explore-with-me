package ru.practicum.explore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.exception.ErrorHandler;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {AdminController.class, ErrorHandler.class})
@Slf4j
public class AdminControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CategoryService categoryService;

    private UserDto userToSave;
    private UserDto savedUser;
    private CategoryDto categoryToSave;
    private CategoryDto savedCategory;

    @BeforeEach
    public void init() {
        userToSave = UserDto.builder()
                .email("email@email.com")
                .name("Name")
                .build();

        savedUser = UserDto.builder()
                .email(userToSave.getEmail())
                .id(1L)
                .name(userToSave.getName())
                .build();

        categoryToSave = CategoryDto.builder().name("concert").build();

        savedCategory = CategoryDto.builder()
                .id(1L)
                .name(categoryToSave.getName())
                .build();
    }

    @SneakyThrows
    @Test
    public void adduser_Normal() {
        when(userService.save(any())).thenReturn(savedUser);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    public void adduser_EmptyEmail() {
        userToSave.setEmail("");

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void adduser_BlankEmail() {
        userToSave.setEmail("   ");

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void adduser_EmptyName() {
        userToSave.setName("");

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void adduser_BlankName() {
        userToSave.setName("   ");

        String result = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void getUsers_Normal() {
        when(userService.findAll(any(), any(), any())).thenReturn(List.of(savedUser));

        String result = mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(savedUser)), result);
    }

    @SneakyThrows
    @Test
    public void getUsers_NoParams() {
        when(userService.findAll(any(), any(), any())).thenReturn(List.of(savedUser));

        String result = mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(savedUser)), result);
    }

    @SneakyThrows
    @Test
    public void deleteUser_Normal() {
        mockMvc.perform(delete("/admin/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, atLeastOnce()).delete(1L);
    }

    @SneakyThrows
    @Test
    public void deleteUser_IdLessThanZero() {
        mockMvc.perform(delete("/admin/users/{userId}", -9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).delete(any());
    }

    @SneakyThrows
    @Test
    public void addCategory_Normal() {
        when(categoryService.save(any())).thenReturn(savedCategory);

        String result = mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(savedCategory), result);
    }

    @SneakyThrows
    @Test
    public void addCategory_EmptyName() {
        categoryToSave.setName("");

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void addCategory_BlankName() {
        categoryToSave.setName("   ");

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).save(any());
    }

    @SneakyThrows
    @Test
    public void patchCategory_Normal() {
        when(categoryService.patch(any(), any())).thenReturn(savedCategory);

        String result = mockMvc.perform(patch("/admin/categories/{catId}", savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(savedCategory), result);
    }

    @SneakyThrows
    @Test
    public void patchCategory_EmptyName() {
        categoryToSave.setName("");

        mockMvc.perform(patch("/admin/categories/{catId}", savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).patch(any(), any());
    }

    @SneakyThrows
    @Test
    public void patchCategory_BlankName() {
        categoryToSave.setName("   ");

        mockMvc.perform(patch("/admin/categories/{catId}", savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).patch(any(), any());
    }

    @SneakyThrows
    @Test
    public void patchCategory_WrongId() {
        mockMvc.perform(patch("/admin/categories/{catId}", -9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryToSave)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).patch(any(), any());
    }

    @SneakyThrows
    @Test
    public void deleteCategory_Normal() {
        mockMvc.perform(delete("/admin/categories/{catId}", 1))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(any());
    }

    @SneakyThrows
    @Test
    public void deleteCategory_WrongId() {
        mockMvc.perform(delete("/admin/categories/{catId}", -9999))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).delete(any());
    }
}
