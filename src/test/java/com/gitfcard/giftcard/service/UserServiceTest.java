package com.gitfcard.giftcard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gitfcard.giftcard.dto.UserLoginDTO;
import com.gitfcard.giftcard.dto.UserPatchDTO;
import com.gitfcard.giftcard.dto.UserRegisterDTO;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.entity.Role;
import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.exception.EmailAlreadyUsedException;
import com.gitfcard.giftcard.exception.UserNotFoundException;
import com.gitfcard.giftcard.repository.RoleRepository;
import com.gitfcard.giftcard.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;
    private UserRequestDTO userRequestDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserPatchDTO userPatchDTO;
    private UserRegisterDTO userRegisterDTO;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setName("ROLE_USER");
        
        adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(Set.of(userRole));

        userRequestDTO = new UserRequestDTO("Jane", "Smith", "jane.smith@example.com", "password123");
        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Updated");
        userUpdateDTO.setLastName("User");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setPassword("newPassword");

        userPatchDTO = new UserPatchDTO();
        userPatchDTO.setFirstName("Patched");

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("New");
        userRegisterDTO.setLastName("User");
        userRegisterDTO.setEmail("new.user@example.com");
        userRegisterDTO.setPassword("newPassword");

        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("john.doe@example.com");
        userLoginDTO.setPassword("password123");
    }


    @Test
    void testGetAllUsers_Success() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("hashedPassword2");
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponceDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("jane.smith@example.com", result.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<UserResponceDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testGetUserById_Success() throws UserNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponceDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.getUserById(999L));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findById(999L);
    }


    @Test
    void testGetUserByEmail_Success() throws UserNotFoundException {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        UserResponceDTO result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void testGetUserByEmail_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.getUserByEmail("nonexistent@example.com"));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }


    @Test
    void testSave_UserRequestDTO_Success() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(userRole));
        
        User savedUser = new User("Jane", "Smith", "jane.smith@example.com", "hashedPassword123");
        savedUser.setId(2L);
        savedUser.setRoles(Set.of(userRole));
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponceDTO result = userService.save(userRequestDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByRoleName("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSave_UserRequestDTO_RoleNotFound_ThrowsRuntimeException() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.save(userRequestDTO));
        
        assertEquals("Default role not found.", exception.getMessage());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByRoleName("ROLE_USER");
        verify(userRepository, never()).save(any());
    }


    @Test
    void testSave_UserRegisterDTO_Success() {
        when(userRepository.findByEmail("new.user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(userRole));
        
        User savedUser = new User("New", "User", "new.user@example.com", "hashedNewPassword");
        savedUser.setId(3L);
        savedUser.setRoles(Set.of(userRole));
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponceDTO result = userService.save(userRegisterDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("new.user@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findByEmail("new.user@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(roleRepository, times(1)).findByRoleName("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSave_UserRegisterDTO_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        when(userRepository.findByEmail("new.user@example.com")).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.save(userRegisterDTO));
        
        assertEquals("User already exists with email: new.user@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("new.user@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }


    @Test
    void testUpdate_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("hashedNewPassword");
        
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponceDTO result = userService.update(userUpdateDTO, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.update(userUpdateDTO, 999L));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }


    @Test
    void testUpdateByEmail_Success() throws Exception {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("hashedNewPassword");
        
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponceDTO result = userService.updateByEmail("john.doe@example.com", userUpdateDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated", result.getFirstName());
        assertEquals("updated@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).existsByEmail("updated@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateByEmail_EmailAlreadyExists_ThrowsEmailAlreadyUsedException() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(true);

        EmailAlreadyUsedException exception = assertThrows(EmailAlreadyUsedException.class,
            () -> userService.updateByEmail("john.doe@example.com", userUpdateDTO));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).existsByEmail("updated@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateByEmail_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.updateByEmail("nonexistent@example.com", userUpdateDTO));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any());
    }


    @Test
    void testPatchByEmail_Success() throws Exception {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        
        User patchedUser = new User();
        patchedUser.setId(1L);
        patchedUser.setFirstName("Patched");
        patchedUser.setLastName("Doe");
        patchedUser.setEmail("john.doe@example.com");
        patchedUser.setPassword("hashedPassword");
        
        when(userRepository.save(any(User.class))).thenReturn(patchedUser);

        UserResponceDTO result = userService.patchByEmail("john.doe@example.com", userPatchDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Patched", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testPatchByEmail_WithEmailChange_Success() throws Exception {
        userPatchDTO.setEmail("new.email@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new.email@example.com")).thenReturn(false);
        
        User patchedUser = new User();
        patchedUser.setId(1L);
        patchedUser.setFirstName("Patched");
        patchedUser.setLastName("Doe");
        patchedUser.setEmail("new.email@example.com");
        patchedUser.setPassword("hashedPassword");
        
        when(userRepository.save(any(User.class))).thenReturn(patchedUser);

        UserResponceDTO result = userService.patchByEmail("john.doe@example.com", userPatchDTO);

        assertNotNull(result);
        assertEquals("new.email@example.com", result.getEmail());
        
        verify(userRepository, times(1)).existsByEmail("new.email@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void testDelete_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDelete_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.delete(999L));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any());
    }


    @Test
    void testVerifyUser_Success() throws Exception {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtService.generateToken(anyString(), any(Long.class), any(List.class))).thenReturn("jwt.token.here");

        String result = userService.verifyUser(userLoginDTO);

        assertNotNull(result);
        assertEquals("jwt.token.here", result);
        
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(anyString(), any(Long.class), any(List.class));
    }

    @Test
    void testVerifyUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        userLoginDTO.setEmail("nonexistent@example.com");

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> userService.verifyUser(userLoginDTO));
        
        assertNotNull(exception);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(authManager, never()).authenticate(any());
    }

    @Test
    void testVerifyUser_AuthenticationFails_ThrowsBadCredentialsException() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(false);
        
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
            () -> userService.verifyUser(userLoginDTO));
        
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString(), any(Long.class), any(List.class));
    }


    @Test
    void testUpdateByEmail_NullFieldsInDTO_KeepsOriginalValues() throws Exception {
        UserUpdateDTO partialUpdate = new UserUpdateDTO();
        partialUpdate.setFirstName("OnlyFirstName");
        
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponceDTO result = userService.updateByEmail("john.doe@example.com", partialUpdate);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void testPatchByEmail_WithPassword_EncodesPassword() throws Exception {
        userPatchDTO.setPassword("newPassword123");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponceDTO result = userService.patchByEmail("john.doe@example.com", userPatchDTO);

        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testPatchByEmail_WithBlankPassword_DoesNotEncodePassword() throws Exception {
        userPatchDTO.setPassword("   ");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponceDTO result = userService.patchByEmail("john.doe@example.com", userPatchDTO);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
