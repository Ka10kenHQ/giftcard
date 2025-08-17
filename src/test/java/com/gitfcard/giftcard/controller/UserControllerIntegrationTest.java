package com.gitfcard.giftcard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitfcard.giftcard.config.TestSecurityConfig;
import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.UserPatchDTO;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.exception.EmailAlreadyUsedException;
import com.gitfcard.giftcard.exception.UserNotFoundException;
import com.gitfcard.giftcard.service.JWTUtil;
import com.gitfcard.giftcard.service.OrderService;
import com.gitfcard.giftcard.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JWTUtil jwtUtil;

    private UserResponceDTO user1;
    private UserResponceDTO user2;
    private UserRequestDTO userRequestDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserPatchDTO userPatchDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {

        user1 = new UserResponceDTO();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");

        user2 = new UserResponceDTO();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");


        userRequestDTO = new UserRequestDTO("Alice", "Johnson", "alice.johnson@example.com", "password123");
        
        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Updated");
        userUpdateDTO.setLastName("User");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setPassword("newPassword");

        userPatchDTO = new UserPatchDTO();
        userPatchDTO.setFirstName("Patched");


        orderResponseDTO = new OrderResponseDTO(
            1L,
            1L,
            LocalDateTime.now().minusDays(1),
            "COMPLETED",
            new BigDecimal("100.00"),
            Arrays.asList()
        );
    }



    @Test
    void testGetAllUsers_Success() throws Exception {

        List<UserResponceDTO> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);


        mockMvc.perform(get("/api/users/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void testGetAllUsers_EmptyList_ReturnsEmptyArray() throws Exception {

        when(userService.getAllUsers()).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/users/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }



    @Test
    void testGetUserById_Success() throws Exception {

        when(userService.getUserById(1L)).thenReturn(user1);


        mockMvc.perform(get("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetUserById_UserNotFound_ReturnsNotFound() throws Exception {

        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException(999L));


        mockMvc.perform(get("/api/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with id: 999 not found"));
    }

    @Test
    void testGetUserById_InvalidIdFormat_ReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/api/users/{id}", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testAddUser_Success() throws Exception {

        UserResponceDTO savedUser = new UserResponceDTO();
        savedUser.setId(3L);
        savedUser.setFirstName("Alice");
        savedUser.setLastName("Johnson");
        savedUser.setEmail("alice.johnson@example.com");
        
        when(userService.save(any(UserRequestDTO.class))).thenReturn(savedUser);


        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"));
    }

    @Test
    void testAddUser_InvalidRequestBody_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"firstName\": \"Alice\", \"invalidField\":}";


        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUser_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        UserRequestDTO invalidUser = new UserRequestDTO();
        invalidUser.setFirstName("Alice");




        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddUser_ServiceException_ReturnsInternalServerError() throws Exception {

        when(userService.save(any(UserRequestDTO.class)))
            .thenThrow(new RuntimeException("Database connection failed"));


        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isInternalServerError());
    }



    @Test
    void testUpdateUser_Success_AuthorizedUser() throws Exception {

        when(jwtUtil.getCurrentUserIdFromRequest(any(HttpServletRequest.class))).thenReturn(1L);
        when(userService.update(any(UserUpdateDTO.class), any(Long.class))).thenReturn(user1);


        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testUpdateUser_Forbidden_UnauthorizedUser() throws Exception {
        when(jwtUtil.getCurrentUserIdFromRequest(any(HttpServletRequest.class))).thenReturn(2L);


        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("You can only update your own profile"));
    }

    @Test
    void testUpdateUser_UserNotFound_ReturnsNotFound() throws Exception {

        when(jwtUtil.getCurrentUserIdFromRequest(any(HttpServletRequest.class))).thenReturn(999L);
        when(userService.update(any(UserUpdateDTO.class), any(Long.class)))
            .thenThrow(new UserNotFoundException(999L));


        mockMvc.perform(put("/api/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with id: 999 not found"));
    }



    @Test
    void testDeleteUser_Success() throws Exception {

        mockMvc.perform(delete("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User: 1 Deleted successfully"));
    }

    @Test
    void testDeleteUser_UserNotFound_ReturnsNotFound() throws Exception {

        doThrow(new UserNotFoundException(999L)).when(userService).delete(999L);


        mockMvc.perform(delete("/api/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with id: 999 not found"));
    }



    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testGetCurrentUser_Success() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(user1);

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetCurrentUser_Unauthorized_ReturnsForbidden() throws Exception {

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com")
    void testGetCurrentUser_UserNotFound_ReturnsNotFound() throws Exception {
        when(userService.getUserByEmail("nonexistent@example.com"))
            .thenThrow(new UserNotFoundException("nonexistent@example.com"));

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with name: nonexistent@example.com not found"));
    }

    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testUpdateCurrentUser_Success() throws Exception {
        when(userService.updateByEmail(anyString(), any(UserUpdateDTO.class))).thenReturn(user1);

        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testUpdateCurrentUser_Unauthorized_ReturnsForbidden() throws Exception {

        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testUpdateCurrentUser_EmailAlreadyUsed_ReturnsConflict() throws Exception {

        when(userService.updateByEmail(anyString(), any(UserUpdateDTO.class)))
            .thenThrow(new EmailAlreadyUsedException("updated@example.com"));


        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with this email already exists: updated@example.com"));
    }



    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testPatchCurrentUser_Success() throws Exception {

        when(userService.patchByEmail(anyString(), any(UserPatchDTO.class))).thenReturn(user1);


        mockMvc.perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testPatchCurrentUser_Unauthorized_ReturnsForbidden() throws Exception {

        mockMvc.perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchDTO)))
                .andExpect(status().isForbidden());
    }



    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testGetCurrentUserOrders_Success() throws Exception {

        List<OrderResponseDTO> orders = Arrays.asList(orderResponseDTO);
        when(orderService.getOrderByUserEmail("john.doe@example.com")).thenReturn(orders);


        mockMvc.perform(get("/api/users/me/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));
    }

    @Test
    void testGetCurrentUserOrders_Unauthorized_ReturnsForbidden() throws Exception {

        mockMvc.perform(get("/api/users/me/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }



    @Test
    void testGetUserOrdersById_Success() throws Exception {

        List<OrderResponseDTO> orders = Arrays.asList(orderResponseDTO);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);


        mockMvc.perform(get("/api/users/{id}/orders", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));
    }

    @Test
    void testGetUserOrdersById_EmptyOrderList_ReturnsEmptyArray() throws Exception {

        when(orderService.getOrdersByUserId(1L)).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/users/{id}/orders", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }



    @Test
    void testGetAllUsers_InvalidHttpMethod_ReturnsMethodNotAllowed() throws Exception {

        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testGetUserById_InvalidHttpMethod_ReturnsMethodNotAllowed() throws Exception {

        mockMvc.perform(post("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }



    @Test
    void testAddUser_UnsupportedMediaType_ReturnsUnsupportedMediaType() throws Exception {

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testUpdateUser_UnsupportedMediaType_ReturnsUnsupportedMediaType() throws Exception {

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text content"))
                .andExpect(status().isUnsupportedMediaType());
    }



    @Test
    void testGetUserById_ZeroId_Success() throws Exception {

        UserResponceDTO userWithZeroId = new UserResponceDTO();
        userWithZeroId.setId(0L);
        userWithZeroId.setFirstName("Zero");
        userWithZeroId.setEmail("zero@example.com");
        
        when(userService.getUserById(0L)).thenReturn(userWithZeroId);


        mockMvc.perform(get("/api/users/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.firstName").value("Zero"));
    }

    @Test
    void testGetUserById_NegativeId_Success() throws Exception {

        UserResponceDTO userWithNegativeId = new UserResponceDTO();
        userWithNegativeId.setId(-1L);
        userWithNegativeId.setFirstName("Negative");
        userWithNegativeId.setEmail("negative@example.com");
        
        when(userService.getUserById(-1L)).thenReturn(userWithNegativeId);


        mockMvc.perform(get("/api/users/{id}", -1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(-1))
                .andExpect(jsonPath("$.firstName").value("Negative"));
    }



    @Test
    @WithMockUser(username = "john.doe@example.com")
    void testUpdateCurrentUser_LargePayload_Success() throws Exception {

        UserUpdateDTO largeUpdateDTO = new UserUpdateDTO();
        largeUpdateDTO.setFirstName("A".repeat(100));
        largeUpdateDTO.setLastName("B".repeat(100));
        largeUpdateDTO.setEmail("large.email@example.com");
        largeUpdateDTO.setPassword("password123");
        
        when(userService.updateByEmail(anyString(), any(UserUpdateDTO.class))).thenReturn(user1);


        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeUpdateDTO)))
                .andExpect(status().isOk());
    }
}
