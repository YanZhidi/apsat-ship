package com.zkthinke.modules.system.service.impl;

import com.zkthinke.exception.EntityExistException;
import com.zkthinke.exception.EntityNotFoundException;
import com.zkthinke.modules.system.domain.User;
import com.zkthinke.modules.system.repository.UserRepository;
import com.zkthinke.modules.system.service.UserService;
import com.zkthinke.modules.system.service.dto.UserDTO;
import com.zkthinke.modules.system.service.dto.UserQueryCriteria;
import com.zkthinke.modules.system.service.mapper.UserMapper;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Value("${apsat.init-password}")
    private String initPassword;


    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    @Override
    public UserDTO findById(long id) {
        Optional<User> user = userRepository.findById(id);
        ValidationUtil.isNull(user,"User","id",id);
        return userMapper.toDto(user.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(User resources) {
        if(userRepository.findByUsername(resources.getUsername())!=null){
            throw new EntityExistException(User.class,"username",resources.getUsername());
        }

        if(userRepository.findByEmail(resources.getEmail())!=null){
            throw new EntityExistException(User.class,"email",resources.getEmail());
        }
        resources.setUpdatePwdFlag(0);
        // 默认密码 Apsat123456. 此密码是加密后的字符
        resources.setPassword(initPassword);
        return userMapper.toDto(userRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User resources) {
        Optional<User> userOptional = userRepository.findById(resources.getId());
        ValidationUtil.isNull(userOptional,"User","id",resources.getId());
        User user = userOptional.get();
        if("admin".equals(user.getUsername())){
            resources.setUsername("admin");
        }
        User user1 = userRepository.findByUsername(user.getUsername());
        User user2 = userRepository.findByEmail(user.getEmail());

        if(user1 !=null&&!user.getId().equals(user1.getId())){
            throw new EntityExistException(User.class,"username",resources.getUsername());
        }

        if(user2!=null&&!user.getId().equals(user2.getId())){
            throw new EntityExistException(User.class,"email",resources.getEmail());
        }
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setPhone(resources.getPhone());
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        ValidationUtil.isNull(userOptional,"User","id", id);
        User user = userOptional.get();
        if("admin".equals(user.getUsername())){
            ValidationUtil.isNull(Optional.empty(),"User","id", null);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO findByName(String userName) {
        User user = null;
        if(ValidationUtil.isEmail(userName)){
            user = userRepository.findByEmail(userName);
        } else {
            user = userRepository.findByUsername(userName);
        }
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            UserDTO userDTO = userMapper.toDto(user);
            return userDTO;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String pass) {
        User user = userRepository.findByUsername(username);
        user.setLastPasswordResetTime(new Date());
        user.setPassword(pass);
        user.setUpdatePwdFlag(1);
        userRepository.updatePass(username, pass, new Date(), 1);
//        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String username, String url) {
        userRepository.updateAvatar(username,url);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username,email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO insert(UserDTO userDTO, Long loginUserId){
        User resources = userMapper.toEntity(userDTO);
        UserDTO userDTODb = create(resources);
        return userDTODb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO userDTO, Long loginUserId){
        User resources = userMapper.toEntity(userDTO);
        update(resources);
    }
}
