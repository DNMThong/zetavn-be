package com.zetavn.api.service.impl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.TokenStatusEnum;
import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.ComfirmationTokenEntity;
import com.zetavn.api.model.entity.RefreshTokenEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.*;
import com.zetavn.api.service.AuthService;
import com.zetavn.api.service.MailerService;
import com.zetavn.api.service.RefreshTokenService;
import com.zetavn.api.utils.CookieHelper;
import com.zetavn.api.utils.JwtHelper;
import com.zetavn.api.utils.UUIDGenerator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service @Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;

    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ComfirmationTokenRepository comfirmationTokenRepository;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private Configuration configFreemarker;
    @Autowired
    MailerService mailerService;
    @Value("${zetavn.domain}")
    String domain;


    @Override
    public ApiResponse<?> register(SignUpRequest signUpRequest) {

        log.info("Try to create User in database");
        if (userRepository.findUserEntityByEmail(signUpRequest.getEmail()) != null) {
            log.error("User exist in DB: {}", signUpRequest.getEmail());
            return ApiResponse.error(HttpStatus.CONFLICT, "Email have been taken");
        } else {
            String id = UUIDGenerator.generateRandomUUID();
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(id);
            userEntity.setUsername(userEntity.getUserId());
            userEntity.setEmail(signUpRequest.getEmail());
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setIsAuthorized(false);
            userEntity.setIsDeleted(false);
            userEntity.setStatus(UserStatusEnum.ACTIVE);
            userEntity.setRole(RoleEnum.USER);
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);

            if (_user != null) {
                UserInfoEntity userInfo = new UserInfoEntity();
                userInfo.setBirthday(signUpRequest.getBirthday());
                userInfo.setGenderEnum(signUpRequest.getGender());
                userInfo.setUserEntity(_user);
                userInfo.setCreatedAt(LocalDateTime.now());
                userInfo.setUpdatedAt(LocalDateTime.now());
                log.info("try to save userinfo: birthday: {} - gender: {}", userInfo.getBirthday(), userInfo.getGenderEnum());
                userInfoRepository.save(userInfo);
            } else {
                log.error("Error Logging: Cannot find user in database: {}", userEntity.getEmail());
                return ApiResponse.error(HttpStatus.NOT_FOUND, "Cannot save user info in database");
            }


            log.info("Register user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }

    }

    @Override
    public ApiResponse<?> login(SignInRequest signInRequest, HttpServletResponse res) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());

        } catch (LockedException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Account has been locked");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // Fetch the user
        UserEntity user = userRepository.findUserEntityByEmail(signInRequest.getUsername());
        if (user != null) {
            // Generate a JWT token, Refresh token
            Map<String, String> tokens = jwtHelper.generateTokens(user);

            // Store refresh token to db
            log.info("Store refresh - Token: {}", tokens.get("refresh_token"));
            refreshTokenService.create(tokens.get("refresh_token"), user.getUserId(), "asdasd");

            // Add refresh token to Coookie
            Cookie c = CookieHelper.addCookie(res, "refresh_token2", tokens.get("refresh_token"), -1, true);


            // Build the response containing JWT token and refresh token
            SignInResponse response = new SignInResponse();
//            JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
            UserResponse userResponse = UserMapper.userInfoToUserResponse(user.getUserInfo());
            userResponse.getInformation().setTotalFriends(friendshipRepository.countFriends(user.getUserId()));
            userResponse.getInformation().setTotalPosts(postRepository.countPostEntityByUserEntityUserId(user.getUserId()));
            userResponse.getInformation().setCountLikesOfPosts(postRepository.getTotalLikesByUserId(user.getUserId()));
            response.setUserInfo(userResponse);
            response.setAccess_token(tokens.get("access_token"));

            return ApiResponse.success(HttpStatus.OK, "Login success", response);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Your username does not exist!");
        }

    }

    @Override
    public ApiResponse<?> reLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = null;
        token = request.getHeader(AUTHORIZATION).substring(7);
        if (!token.equals("")) {
            try {
                DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
                if (decodedJWT.getExpiresAt().before(new Date())) {
                    log.error("Expires At: {}", decodedJWT.getExpiresAt().toInstant());
                    throw new TokenExpiredException("The token has expired", decodedJWT.getExpiresAt().toInstant());
                }
                String username = decodedJWT.getSubject();
                UserEntity user = userRepository.findUserEntityByEmail(username);

                // Generate new access token
                String access_token = jwtHelper.generateToken(user);

                // response data for client
                SignInResponse _res = new SignInResponse();

                UserResponse userResponse = UserMapper.userInfoToUserResponse(user.getUserInfo());
                userResponse.getInformation().setTotalFriends(friendshipRepository.countFriends(user.getUserId()));
                userResponse.getInformation().setTotalPosts(postRepository.countPostEntityByUserEntityUserId(user.getUserId()));
                userResponse.getInformation().setCountLikesOfPosts(postRepository.getTotalLikesByUserId(user.getUserId()));

                _res.setUserInfo(userResponse);
                _res.setAccess_token(access_token);
                return ApiResponse.success(HttpStatus.OK, "Re-login success", _res);
            } catch (TokenExpiredException e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("ERROR", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("ERROR", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        return ApiResponse.error(FORBIDDEN, "Re-login failed");
    }

    @Override
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refresh_token = null;
        refresh_token = CookieHelper.getCookie(request.getCookies(), "refresh_token2").getValue();
        if (refresh_token != null && !refresh_token.equals("")) {
            try {
                DecodedJWT decodedJWT = jwtHelper.decodedJWTRef(refresh_token);
                if (decodedJWT.getExpiresAt().before(new Date())) {
                    log.error("Expires At: {}", decodedJWT.getExpiresAt().toInstant());
                    throw new TokenExpiredException("The token has expired", decodedJWT.getExpiresAt().toInstant());
                }
                RefreshTokenEntity user = refreshTokenService.getRefreshTokenByToken(refresh_token);
                String access_token = jwtHelper.generateToken(user.getUserEntity());

                Map<String, String> tokens = new HashMap<>();

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
                return ApiResponse.success(HttpStatus.OK, "Refresh token Success", jwtResponse);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("ERROR", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
//        else {
//            // Do delete refresh token in db by ip and user id
//            refreshTokenService.removeRefreshTokenByIpAddressAndUserId();
//        }
        return ApiResponse.error(FORBIDDEN, "Refresh token is invalid");
    }

    @Override
    public ApiResponse<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie c = CookieHelper.getCookie(request.getCookies(), "refresh_token2");
        String token = c.getValue();
        if (token.equals("")) {
            log.error("Refresh token does not exist");
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Bad Request");
        } else {
            log.info("Delete token in database: {}", token);
            refreshTokenService.removeRefreshTokenByToken(token);
            log.info("Delete token in cookie: {}", token);
            CookieHelper.removeCookie(request, response, c.getName());
            return ApiResponse.success(HttpStatus.OK, "Logout Success", null);
        }
    }

    @Override
    public ApiResponse<?> forgotPassword(String email) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email);
        if(userEntity!=null) {
            String token = UUIDGenerator.generateRandomUUID();
            ComfirmationTokenEntity comfirmationToken = new ComfirmationTokenEntity();
            comfirmationToken.setToken(token);
            comfirmationToken.setCreatedAt(LocalDateTime.now());
            comfirmationToken.setExpiredAt(LocalDateTime.now().plusMinutes(15));
            comfirmationToken.setStatus(TokenStatusEnum.RESET_PASSWORD);
            comfirmationToken.setUserEntity(userEntity);
            comfirmationToken.setConfirmedAt(null);

            comfirmationTokenRepository.save(comfirmationToken);

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("display",userEntity.getFirstName()+" "+userEntity.getLastName());
                model.put("link_reset_password",domain+"/reset-password?t="+token);

                Template t = configFreemarker.getTemplate("email-template.ftl");
                String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
                mailerService.send(email,"Lấy lại mật khẩu",html);

                return ApiResponse.success(HttpStatus.OK, "Success", null);
            }catch (Exception e) {
                log.error(e.getMessage());
                return ApiResponse.success(HttpStatus.BAD_REQUEST, "Bad Request", null);
            }
        }
        return ApiResponse.success(HttpStatus.FAILED_DEPENDENCY, "Email not found", null);
    }

    @Override
    public ApiResponse<?> resetPassword(String token,String password) {
        ComfirmationTokenEntity comfirmationToken = comfirmationTokenRepository.getComfirmationToken(token,TokenStatusEnum.RESET_PASSWORD);

        if(comfirmationToken!=null) {
            if(comfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())&&comfirmationToken.getConfirmedAt()==null) {
                comfirmationToken.setConfirmedAt(LocalDateTime.now());
                comfirmationTokenRepository.save(comfirmationToken);

                UserEntity userEntity = comfirmationToken.getUserEntity();
                userEntity.setPassword(passwordEncoder.encode(password));
                userRepository.save(userEntity);

                return ApiResponse.success(HttpStatus.OK, "Reset password success", null);
            }
        }
        return ApiResponse.success(HttpStatus.FAILED_DEPENDENCY, "Invalid token", null);
    }



    @Override
    public ApiResponse<?> sendEmailConfirmation(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);

        if(userEntity!=null) {
            String token = UUIDGenerator.generateRandomUUID();
            ComfirmationTokenEntity comfirmationToken = new ComfirmationTokenEntity();
            comfirmationToken.setToken(token);
            comfirmationToken.setCreatedAt(LocalDateTime.now());
            comfirmationToken.setExpiredAt(LocalDateTime.now().plusMinutes(15));
            comfirmationToken.setStatus(TokenStatusEnum.VERIFICATION_EMAIL);
            comfirmationToken.setUserEntity(userEntity);
            comfirmationToken.setConfirmedAt(null);

            comfirmationTokenRepository.save(comfirmationToken);

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("display",userEntity.getFirstName()+" "+userEntity.getLastName());
                model.put("email",userEntity.getEmail());
                model.put("link_confirm_email",domain+"/confirmation-email?t="+token);

                Template t = configFreemarker.getTemplate("confirmation-email-template.ftl");
                String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

                mailerService.send(userEntity.getEmail(),"Xác thực tài khoản",html);

                return ApiResponse.success(HttpStatus.OK, "Success", null);
            }catch (Exception e) {
                log.error(e.getMessage());
                return ApiResponse.success(HttpStatus.BAD_REQUEST, "Bad Request", null);
            }
        }

        return ApiResponse.success(HttpStatus.FAILED_DEPENDENCY, "User not found", null);
    }

    @Override
    public ApiResponse<?> confirmationEmail(String token) {
        ComfirmationTokenEntity comfirmationToken = comfirmationTokenRepository.getComfirmationToken(token,TokenStatusEnum.VERIFICATION_EMAIL);

        if(comfirmationToken!=null) {
            if(comfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())&&comfirmationToken.getConfirmedAt()==null) {
                comfirmationToken.setConfirmedAt(LocalDateTime.now());
                comfirmationTokenRepository.save(comfirmationToken);
                UserEntity userEntity = comfirmationToken.getUserEntity();
                userEntity.setIsAuthorized(true);
                userRepository.save(userEntity);
                return ApiResponse.success(HttpStatus.OK, "Confirm email success", null);
            }
        }
        return ApiResponse.success(HttpStatus.FAILED_DEPENDENCY, "Invalid token", null);
    }

    @Override
    public ApiResponse<SignInResponse> loginForAdmin(SignInRequest signInRequest, HttpServletResponse res) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());


        try {
            authenticationManager.authenticate(authenticationToken);
        }  catch (LockedException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Account has been locked");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!!");
        } catch (NotFoundException e) {
            throw new NotFoundException("Not found username");
        }
//        catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }



        // Fetch the user
        UserEntity user = userRepository.findUserEntityByEmail(signInRequest.getUsername());
        if (user != null) {

            if (!user.getRole().equals(RoleEnum.ADMIN)) throw new AccessDeniedException("Access denied");


            // Generate a JWT token, Refresh token
            String token = jwtHelper. generateToken(user);


            // Build the response containing JWT token and refresh token
            SignInResponse response = new SignInResponse();
//            JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
            UserResponse userResponse = UserMapper.userInfoToUserResponse(user.getUserInfo());
            response.setUserInfo(userResponse);
            response.setAccess_token(token);

            return ApiResponse.success(HttpStatus.OK, "Login success", response);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Your username does not exist!");
        }

    }

    @Override
    public ApiResponse<?> createForAdmin(SignUpRequest signUpRequest) {
        log.info("Try to create User in database");
        if (userRepository.findUserEntityByEmail(signUpRequest.getEmail()) != null) {
            log.error("User exist in DB: {}", signUpRequest.getEmail());
            return ApiResponse.error(HttpStatus.CONFLICT, "Email have been taken");
        } else {
            String id = UUIDGenerator.generateRandomUUID();
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(id);
            userEntity.setUsername(userEntity.getUserId());
            userEntity.setEmail(signUpRequest.getEmail());
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setIsAuthorized(false);
            userEntity.setIsDeleted(false);
            userEntity.setStatus(UserStatusEnum.ACTIVE);
            userEntity.setRole(RoleEnum.ADMIN);
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);

            if (_user != null) {
                UserInfoEntity userInfo = new UserInfoEntity();
                userInfo.setBirthday(signUpRequest.getBirthday());
                userInfo.setGenderEnum(signUpRequest.getGender());
                userInfo.setUserEntity(_user);
                userInfo.setCreatedAt(LocalDateTime.now());
                userInfo.setUpdatedAt(LocalDateTime.now());
                log.info("try to save userinfo: birthday: {} - gender: {}", userInfo.getBirthday(), userInfo.getGenderEnum());
                userInfoRepository.save(userInfo);
            } else {
                log.error("Error Logging: Cannot find user in database: {}", userEntity.getEmail());
                return ApiResponse.error(HttpStatus.NOT_FOUND, "Cannot save user info in database");
            }


            log.info("Register user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }
    }
}
