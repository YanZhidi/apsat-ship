package com.zkthinke.modules.security.service;

import com.zkthinke.exception.BadRequestException;
import com.zkthinke.modules.apsat.ship.domain.RolePO;
import com.zkthinke.modules.apsat.ship.domain.ShipPO;
import com.zkthinke.modules.apsat.ship.mapper.ShipMapper;
import com.zkthinke.modules.security.security.JwtUser;
import com.zkthinke.modules.system.service.UserService;
import com.zkthinke.modules.system.service.dto.RoleSmallDTO;
import com.zkthinke.modules.system.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2020-10-22
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtPermissionService permissionService;

    @Autowired
    private ShipMapper shipMapper;

    @Override
    public UserDetails loadUserByUsername(String username){
        UserDTO user = userService.findByName(username);
        //获取用户的船舶信息(根据用户id查出的角色id再查出对应的船舶信息)
        List<ShipPO> ships = shipMapper.selectShipByRoleAndUserId(user.getId());
        user.setShips(ships);
        List<Long> shipIds = new ArrayList<>();
        if(ships != null && ships.size() > 0 ) {
            for (ShipPO shipPO : ships ) {
                shipIds.add(shipPO.getId());
            }
        }
        user.setShipIds(shipIds);

        //封装用户角色
        List<RolePO> roles = new ArrayList<>();
        if(user.getRoles() != null && user.getRoles().size() > 0 ) {
            for (RoleSmallDTO roleSmallDTO : user.getRoles() ) {
                RolePO role = new RolePO();
                role.setId(roleSmallDTO.getId());
                role.setName(roleSmallDTO.getName());
                roles.add(role);
            }
        }
        user.setUserRoles(roles);

        if (user == null) {
            throw new BadRequestException("账号不存在");
        } else {
            return createJwtUser(user);
        }

    }

    public UserDetails createJwtUser(UserDTO user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAvatar(),
                user.getEmail(),
                user.getPhone(),
                user.getShips(),
                user.getShipIds(),
                user.getUserRoles(),
                permissionService.mapToGrantedAuthorities(user),
                user.getEnabled(),
                user.getCreateTime(),
                user.getLastPasswordResetTime(),
                user.getUpdatePwdFlag()
        );
    }
}
