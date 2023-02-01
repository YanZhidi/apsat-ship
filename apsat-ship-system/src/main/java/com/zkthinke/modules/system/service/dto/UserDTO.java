package com.zkthinke.modules.system.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zkthinke.modules.apsat.ship.domain.RolePO;
import com.zkthinke.modules.apsat.ship.domain.ShipPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Data
public class UserDTO implements Serializable {

    @ApiModelProperty(hidden = true)
    private Long id;

    private String username;

    private String avatar;

    private String email;

    private String phone;

    private Boolean enabled;

    @JsonIgnore
    private String password;

    private Timestamp createTime;

    private Date lastPasswordResetTime;

    private Set<RoleSmallDTO> roles;

    private Integer updatePwdFlag;

    //用户角色的船舶信息
    private List<ShipPO> ships;

    //用户角色的船舶id信息
    private List<Long> shipIds;

    //用户角色信息
    private List<RolePO> userRoles;

}
