package com.zkthinke.modules.system.service.dto;

import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Data
public class RoleDTO implements Serializable {

    private Long id;

    private String name;

    private String dataScope;

    private Integer level;

    private String remark;

    private Set<PermissionDTO> permissions;

    private Set<MenuDTO> menus;

    private Set<ShipDTO> ships;

    private Timestamp createTime;
}
