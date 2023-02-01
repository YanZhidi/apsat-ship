package com.zkthinke.modules.apsat.ship.rest;


import com.zkthinke.modules.apsat.ship.domain.NavigationWarningMSIbyAreaDTO;
import com.zkthinke.modules.apsat.ship.service.NavigationWarningService;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "航行警告")
@RestController
@RequestMapping("api/navigationWarning")
public class NavigationWarningController {

    @Autowired
    private NavigationWarningService navigationWarningService;

    /**
     * 3.3.	按区域获取航行警告/航行通告信息
     *
     * @param navigationWarningMSIbyAreaDTO 航行警告 3.3.	按区域获取航行警告/航行通告信息数据传输对象
     * @return 统一返回数据对象
     */
    @ApiOperation(value = "按区域获取航行警告/航行通告信息")
    @PostMapping("/getMSIbyArea")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getMSIbyArea(@RequestBody NavigationWarningMSIbyAreaDTO navigationWarningMSIbyAreaDTO) {
        if (navigationWarningMSIbyAreaDTO == null || navigationWarningMSIbyAreaDTO.getType() == null
                || StringUtils.isBlank(navigationWarningMSIbyAreaDTO.getArea())
                || StringUtils.isBlank(navigationWarningMSIbyAreaDTO.getLanguage())) {
            return ResponseResult.fail("必填参数不允许为空");
        }
        return navigationWarningService.getNoticeByArea(navigationWarningMSIbyAreaDTO);
    }

}
