package com.zkthinke.modules.system.service;

import com.zkthinke.modules.system.service.dto.UserDTO;
import com.zkthinke.modules.system.service.dto.UserQueryCriteria;
import com.zkthinke.modules.system.domain.User;
import org.springframework.data.domain.Pageable;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
public interface UserService {

    /**
     * get
     * @param id
     * @return
     */
    UserDTO findById(long id);

    /**
     * create
     * @param resources
     * @return
     */
    UserDTO create(User resources);

    /**
     * update
     * @param resources
     */
    void update(User resources);

    /**
     * delete
     * @param id
     */
    void delete(Long id);

    /**
     * findByName
     * @param userName
     * @return
     */
    UserDTO findByName(String userName);

    /**
     * 修改密码
     * @param username
     * @param encryptPassword
     */
    void updatePass(String username, String encryptPassword);

    /**
     * 修改头像
     * @param username
     * @param url
     */
    void updateAvatar(String username, String url);

    /**
     * 修改邮箱
     * @param username
     * @param email
     */
    void updateEmail(String username, String email);

    Object queryAll(UserQueryCriteria criteria, Pageable pageable);

    UserDTO insert(UserDTO userDTO, Long loginUserId);

    void update(UserDTO userDTO, Long loginUserId);
}
