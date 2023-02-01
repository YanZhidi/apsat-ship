
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_ship
-- ----------------------------
DROP TABLE IF EXISTS `t_ship`;
CREATE TABLE `t_ship` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '名称',
  `mmsi_number` varchar(255) DEFAULT NULL COMMENT 'MMSI编号',
  `call_sign` varchar(255) DEFAULT NULL COMMENT '呼号',
  `imo_number` varchar(255) DEFAULT NULL COMMENT 'IMO,唯一编号',
  `type` varchar(255) DEFAULT NULL COMMENT '类型',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `last_detail_id` bigint(20) DEFAULT NULL COMMENT '最新航行信息 id',
  `name_pinyin` varchar(1024) DEFAULT NULL,
  `last_device_id` bigint(20) DEFAULT NULL COMMENT '最新设备信息 id',
  `last_device_stime` varchar(64) DEFAULT NULL COMMENT '最新设备同步时间',
  `last_detail_stime` varchar(64) DEFAULT NULL COMMENT '最新详情数据同步时间',
  PRIMARY KEY (`id`),
  KEY `idx_ship_last_detail_id` (`last_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='船舶信息表';

-- ----------------------------
-- Records of t_ship
-- ----------------------------
BEGIN;
INSERT INTO `t_ship` VALUES (2, 'ZHONG WAI YUN BO HAI', '414247000', 'BICH7', '0', '货轮', NULL, 1607413200154, 6414, 'ZHONG WAI YUN BO HAI', NULL, '2020-12-08 15:29:28', '2020-12-08 15:34:29');
INSERT INTO `t_ship` VALUES (8, '复兴号', '414247099', 'BICH8', '12', '货轮', 0, 1607352550195, NULL, 'FUXINGHAO', NULL, '2020-11-29 11:54:29', '2020-11-29 11:54:29');
COMMIT;

-- ----------------------------
-- Table structure for t_ship_attention
-- ----------------------------
DROP TABLE IF EXISTS `t_ship_attention`;
CREATE TABLE `t_ship_attention` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `ship_id` bigint(11) NOT NULL COMMENT '船舶 id',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户 id',
  `attention` tinyint(4) DEFAULT NULL COMMENT '是否关注 0 否 1 是',
  `attention_time` bigint(20) DEFAULT NULL COMMENT '关注时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='船舶关注表';


-- ----------------------------
-- Table structure for t_ship_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_ship_detail`;
CREATE TABLE `t_ship_detail` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `ship_id` bigint(11) NOT NULL COMMENT '船舶 id',
  `sailing_status` varchar(255) DEFAULT NULL COMMENT '航行状态',
  `reset_voyage` varchar(255) DEFAULT NULL COMMENT '复位对地航程(当前航程)',
  `total_voyage` varchar(255) DEFAULT NULL COMMENT '累计对地航程(累计航程)',
  `steering_speed` varchar(255) DEFAULT NULL COMMENT '转向速度',
  `ground_speed` varchar(255) DEFAULT NULL COMMENT '对地航速',
  `longitude` varchar(255) DEFAULT NULL COMMENT '当前经度',
  `latitude` varchar(255) DEFAULT NULL COMMENT '当前纬度',
  `cog` varchar(255) DEFAULT NULL COMMENT '对地航向',
  `ship_head` varchar(255) DEFAULT NULL COMMENT '船首向',
  `departure_time` bigint(20) DEFAULT NULL COMMENT '出发时间',
  `eta` bigint(20) DEFAULT NULL COMMENT '估计到达时间',
  `destination` varchar(255) DEFAULT NULL COMMENT '目的地',
  `max_static_draft` varchar(255) DEFAULT NULL COMMENT '最大静态吃水',
  `wind_speed` varchar(255) DEFAULT NULL COMMENT '风速',
  `sensor_depth` varchar(255) DEFAULT NULL COMMENT '富裕水深',
  `relative_wind` varchar(255) DEFAULT NULL COMMENT '相对风向',
  `device_name` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `collect_time` bigint(20) DEFAULT NULL COMMENT '数据采集时间',
  `departure` varchar(255) DEFAULT NULL COMMENT '出发地',
  `source_id` bigint(20) DEFAULT NULL COMMENT '同步数据 id',
  PRIMARY KEY (`id`),
  KEY `idx_ship_detail_ship_id` (`ship_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6415 DEFAULT CHARSET=utf8 COMMENT='船舶航行信息表';



-- ----------------------------
-- Table structure for t_ship_device
-- ----------------------------
DROP TABLE IF EXISTS `t_ship_device`;
CREATE TABLE `t_ship_device` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `ship_id` bigint(11) NOT NULL COMMENT '船舶 id',
  `revolution_speed` varchar(255) DEFAULT NULL COMMENT '主机转速',
  `host_load` varchar(255) DEFAULT NULL COMMENT '主机负荷',
  `running_hours` varchar(255) DEFAULT NULL COMMENT '主机运行小时',
  `supercharger_speed` varchar(255) DEFAULT NULL COMMENT '增压器转速',
  `starting_air_pressure` varchar(255) DEFAULT NULL COMMENT '起动空气压力',
  `tbbt` varchar(255) DEFAULT NULL COMMENT '推力块轴承温度',
  `meav` varchar(255) DEFAULT NULL COMMENT '主机轴向振动',
  `meloip` varchar(255) DEFAULT NULL COMMENT '主机滑油进口压力',
  `mefc` varchar(255) DEFAULT NULL COMMENT '主机燃油刻度',
  `meloot` varchar(255) DEFAULT NULL COMMENT '主机滑油出口温度',
  `meloit` varchar(255) DEFAULT NULL COMMENT '主机滑油进口温度',
  `sloip` varchar(255) DEFAULT NULL COMMENT '增压器滑油进口压力',
  `sloot` varchar(255) DEFAULT NULL COMMENT '增压器滑油出口温度',
  `fip` varchar(255) DEFAULT NULL COMMENT '主机燃油进口压力',
  `fit` varchar(255) DEFAULT NULL COMMENT '主机燃油进口温度',
  `pcoip` varchar(255) DEFAULT NULL COMMENT '活塞冷却油进口压力',
  `cloit` varchar(255) DEFAULT NULL COMMENT '汽缸滑油进口温度',
  `mejip` varchar(255) DEFAULT NULL COMMENT '主机缸套冷却水进口压力',
  `mejit` varchar(255) DEFAULT NULL COMMENT '主机缸套冷却水进口温度',
  `siep` varchar(255) DEFAULT NULL COMMENT '暂时弃用',
  `siet` varchar(255) DEFAULT NULL COMMENT '增压器进口排气温度',
  `soet` varchar(255) DEFAULT NULL COMMENT '增压器出口排气温度',
  `control_air_pressure` varchar(255) DEFAULT NULL COMMENT '控制空气压力',
  `smp` varchar(255) DEFAULT NULL COMMENT '扫气集管压力',
  `smt` varchar(255) DEFAULT NULL COMMENT '扫气集管温度',
  `runstatus1` varchar(255) DEFAULT NULL COMMENT '一号机运行状态',
  `sae1` varchar(255) DEFAULT NULL COMMENT '1号辅机转速',
  `pog1` varchar(255) DEFAULT NULL COMMENT '1号发电机功率',
  `ats1` varchar(255) DEFAULT NULL COMMENT '1号辅机增压器转速',
  `alop1` varchar(255) DEFAULT NULL COMMENT '1号辅机滑油压力',
  `alot1` varchar(255) DEFAULT NULL COMMENT '1号辅机滑油温度',
  `afp1` varchar(255) DEFAULT NULL COMMENT '1号辅机燃油压力',
  `aft1` varchar(255) DEFAULT NULL COMMENT '1号辅机燃油温度',
  `runstatus2` varchar(255) DEFAULT NULL COMMENT '二号机运行状态',
  `sae2` varchar(255) DEFAULT NULL COMMENT '2号辅机转速',
  `pog2` varchar(255) DEFAULT NULL COMMENT '2号发电机功率',
  `ats2` varchar(255) DEFAULT NULL COMMENT '2号辅机增压器转速',
  `alop2` varchar(255) DEFAULT NULL COMMENT '2号辅机滑油压力',
  `alot2` varchar(255) DEFAULT NULL COMMENT '2号辅机滑油温度',
  `afp2` varchar(255) DEFAULT NULL COMMENT '2号辅机燃油压力',
  `aft2` varchar(255) DEFAULT NULL COMMENT '2号辅机燃油温度',
  `runstatus3` varchar(255) DEFAULT NULL COMMENT '三号机运行状态',
  `sae3` varchar(255) DEFAULT NULL COMMENT '3号辅机转速',
  `pog3` varchar(255) DEFAULT NULL COMMENT '3号发电机功率',
  `ats3` varchar(255) DEFAULT NULL COMMENT '3号辅机增压器转速',
  `alop3` varchar(255) DEFAULT NULL COMMENT '3号辅机滑油压力',
  `alot3` varchar(255) DEFAULT NULL COMMENT '3号辅机滑油温度',
  `afp3` varchar(255) DEFAULT NULL COMMENT '3号辅机燃油压力',
  `aft3` varchar(255) DEFAULT NULL COMMENT '3号辅机燃油温度',
  `boiler_steam_pressure` varchar(255) DEFAULT NULL COMMENT '锅炉蒸汽压力',
  `boiler_water_level` varchar(255) DEFAULT NULL COMMENT '锅炉水位',
  `bot` varchar(255) DEFAULT NULL COMMENT '锅炉燃油温度',
  `stern_draught` varchar(255) DEFAULT NULL COMMENT '艉部吃水',
  `stem_draft` varchar(255) DEFAULT NULL COMMENT '艏部吃水',
  `starboard_draft` varchar(255) DEFAULT NULL COMMENT '右舷吃水',
  `port_draft` varchar(255) DEFAULT NULL COMMENT '左舷吃水',
  `trim` varchar(255) DEFAULT NULL COMMENT '纵倾',
  `heel` varchar(255) DEFAULT NULL COMMENT '横倾',
  `mefit` varchar(255) DEFAULT NULL COMMENT '暂时弃用',
  `mefif` varchar(255) DEFAULT NULL COMMENT '主机燃油进口流量',
  `mefof` varchar(255) DEFAULT NULL COMMENT '主机燃油出口流量',
  `gfif` varchar(255) DEFAULT NULL COMMENT '辅机燃油进口流量',
  `gfof` varchar(255) DEFAULT NULL COMMENT '辅机燃油出口流量',
  `bfoif` varchar(255) DEFAULT NULL COMMENT '锅炉燃油进口流量',
  `bfoof` varchar(255) DEFAULT NULL COMMENT '锅炉燃油出口流量',
  `sefif` varchar(255) DEFAULT NULL COMMENT '辅机燃油进口流量',
  `sefof` varchar(255) DEFAULT NULL COMMENT '辅机燃油出口流量',
  `device_name` varchar(255) DEFAULT NULL COMMENT '设备名',
  `source_id` bigint(20) DEFAULT NULL COMMENT '对应同步数据 id',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `collect_time` bigint(20) DEFAULT NULL COMMENT '数据采集时间',
  PRIMARY KEY (`id`),
  KEY `idx_ship_device_ship_id` (`ship_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6430 DEFAULT CHARSET=utf8 COMMENT='船舶设备信息(能效)表';


-- ----------------------------
-- Table structure for t_sync_ship_device
-- ----------------------------
DROP TABLE IF EXISTS `t_sync_ship_device`;
CREATE TABLE `t_sync_ship_device` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `imo_number` varchar(255) NOT NULL COMMENT '船舶 IMO 唯一编号',
  `name` varchar(255) DEFAULT NULL COMMENT '船名',
  `revolution_speed` varchar(255) DEFAULT NULL COMMENT '主机转速',
  `host_load` varchar(255) DEFAULT NULL COMMENT '主机负荷',
  `running_hours` varchar(255) DEFAULT NULL COMMENT '主机运行小时',
  `supercharger_speed` varchar(255) DEFAULT NULL COMMENT '增压器转速',
  `starting_air_pressure` varchar(255) DEFAULT NULL COMMENT '起动空气压力',
  `tbbt` varchar(255) DEFAULT NULL COMMENT '推力块轴承温度',
  `meav` varchar(255) DEFAULT NULL COMMENT '主机轴向振动',
  `meloip` varchar(255) DEFAULT NULL COMMENT '主机滑油进口压力',
  `mefc` varchar(255) DEFAULT NULL COMMENT '主机燃油刻度',
  `meloot` varchar(255) DEFAULT NULL COMMENT '主机滑油出口温度',
  `meloit` varchar(255) DEFAULT NULL COMMENT '主机滑油进口温度',
  `sloip` varchar(255) DEFAULT NULL COMMENT '增压器滑油进口压力',
  `sloot` varchar(255) DEFAULT NULL COMMENT '增压器滑油出口温度',
  `fip` varchar(255) DEFAULT NULL COMMENT '主机燃油进口压力',
  `fit` varchar(255) DEFAULT NULL COMMENT '主机燃油进口温度',
  `pcoip` varchar(255) DEFAULT NULL COMMENT '活塞冷却油进口压力',
  `cloit` varchar(255) DEFAULT NULL COMMENT '汽缸滑油进口温度',
  `mejip` varchar(255) DEFAULT NULL COMMENT '主机缸套冷却水进口压力',
  `mejit` varchar(255) DEFAULT NULL COMMENT '主机缸套冷却水进口温度',
  `siep` varchar(255) DEFAULT NULL COMMENT '暂时弃用',
  `siet` varchar(255) DEFAULT NULL COMMENT '增压器进口排气温度',
  `soet` varchar(255) DEFAULT NULL COMMENT '增压器出口排气温度',
  `control_air_pressure` varchar(255) DEFAULT NULL COMMENT '控制空气压力',
  `smp` varchar(255) DEFAULT NULL COMMENT '扫气集管压力',
  `smt` varchar(255) DEFAULT NULL COMMENT '扫气集管温度',
  `runstatus1` varchar(255) DEFAULT NULL COMMENT '一号机运行状态',
  `sae1` varchar(255) DEFAULT NULL COMMENT '1号辅机转速',
  `pog1` varchar(255) DEFAULT NULL COMMENT '1号发电机功率',
  `ats1` varchar(255) DEFAULT NULL COMMENT '1号辅机增压器转速',
  `alop1` varchar(255) DEFAULT NULL COMMENT '1号辅机滑油压力',
  `alot1` varchar(255) DEFAULT NULL COMMENT '1号辅机滑油温度',
  `afp1` varchar(255) DEFAULT NULL COMMENT '1号辅机燃油压力',
  `aft1` varchar(255) DEFAULT NULL COMMENT '1号辅机燃油温度',
  `runstatus2` varchar(255) DEFAULT NULL COMMENT '二号机运行状态',
  `sae2` varchar(255) DEFAULT NULL COMMENT '2号辅机转速',
  `pog2` varchar(255) DEFAULT NULL COMMENT '2号发电机功率',
  `ats2` varchar(255) DEFAULT NULL COMMENT '2号辅机增压器转速',
  `alop2` varchar(255) DEFAULT NULL COMMENT '2号辅机滑油压力',
  `alot2` varchar(255) DEFAULT NULL COMMENT '2号辅机滑油温度',
  `afp2` varchar(255) DEFAULT NULL COMMENT '2号辅机燃油压力',
  `aft2` varchar(255) DEFAULT NULL COMMENT '2号辅机燃油温度',
  `runstatus3` varchar(255) DEFAULT NULL COMMENT '三号机运行状态',
  `sae3` varchar(255) DEFAULT NULL COMMENT '3号辅机转速',
  `pog3` varchar(255) DEFAULT NULL COMMENT '3号发电机功率',
  `ats3` varchar(255) DEFAULT NULL COMMENT '3号辅机增压器转速',
  `alop3` varchar(255) DEFAULT NULL COMMENT '3号辅机滑油压力',
  `alot3` varchar(255) DEFAULT NULL COMMENT '3号辅机滑油温度',
  `afp3` varchar(255) DEFAULT NULL COMMENT '3号辅机燃油压力',
  `aft3` varchar(255) DEFAULT NULL COMMENT '3号辅机燃油温度',
  `boiler_steam_pressure` varchar(255) DEFAULT NULL COMMENT '锅炉蒸汽压力',
  `boiler_water_level` varchar(255) DEFAULT NULL COMMENT '锅炉水位',
  `bot` varchar(255) DEFAULT NULL COMMENT '锅炉燃油温度',
  `stern_draught` varchar(255) DEFAULT NULL COMMENT '艉部吃水',
  `stem_draft` varchar(255) DEFAULT NULL COMMENT '艏部吃水',
  `starboard_draft` varchar(255) DEFAULT NULL COMMENT '右舷吃水',
  `port_draft` varchar(255) DEFAULT NULL COMMENT '左舷吃水',
  `trim` varchar(255) DEFAULT NULL COMMENT '纵倾',
  `heel` varchar(255) DEFAULT NULL COMMENT '横倾',
  `mefit` varchar(255) DEFAULT NULL COMMENT '暂时弃用',
  `mefif` varchar(255) DEFAULT NULL COMMENT '主机燃油进口流量',
  `mefof` varchar(255) DEFAULT NULL COMMENT '主机燃油出口流量',
  `gfif` varchar(255) DEFAULT NULL COMMENT '辅机燃油进口流量',
  `gfof` varchar(255) DEFAULT NULL COMMENT '辅机燃油出口流量',
  `bfoif` varchar(255) DEFAULT NULL COMMENT '锅炉燃油进口流量',
  `bfoof` varchar(255) DEFAULT NULL COMMENT '锅炉燃油出口流量',
  `sefif` varchar(255) DEFAULT NULL COMMENT '辅机燃油进口流量',
  `sefof` varchar(255) DEFAULT NULL COMMENT '辅机燃油出口流量',
  `device_name` varchar(255) DEFAULT NULL COMMENT '设备名',
  `sync_time` bigint(20) DEFAULT NULL COMMENT '同步时间',
  `source_id` varchar(255) DEFAULT NULL COMMENT '源 id',
  `data_sync_time` varchar(255) DEFAULT NULL COMMENT '接口方数据采集时间',
  PRIMARY KEY (`id`),
  KEY `idx_ship_device_source_id` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6430 DEFAULT CHARSET=utf8 COMMENT='船舶设备信息(能效)同步数据表';


-- ----------------------------
-- Table structure for t_sync_ship
-- ----------------------------
DROP TABLE IF EXISTS `t_sync_ship`;
CREATE TABLE `t_sync_ship` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `imo_number` varchar(255) NOT NULL COMMENT '船舶 IMO 唯一编号',
  `name` varchar(255) DEFAULT NULL COMMENT '船名',
  `device_name` varchar(255) DEFAULT NULL COMMENT '设备名',
  `rerative_wind` varchar(255) DEFAULT NULL COMMENT '相对风向',
  `sensor_depth` varchar(255) DEFAULT NULL COMMENT '富裕水深',
  `swsd` varchar(255) DEFAULT NULL COMMENT '传感器水面距离',
  `sind_speed` varchar(255) DEFAULT NULL COMMENT '风速',
  `total_cumulative_ground_distance` varchar(255) DEFAULT NULL COMMENT '累计对地航程',
  `ground_distance_since_reset` varchar(255) DEFAULT NULL COMMENT '复位对地航程',
  `mmsi_number` varchar(255) DEFAULT NULL COMMENT 'MMSI编号',
  `navigational_status` varchar(255) DEFAULT NULL COMMENT '航行状态',
  `departure_time` varchar(255) DEFAULT NULL COMMENT '出发时间',
  `steering_speed` varchar(255) DEFAULT NULL COMMENT '转向速度',
  `ground_speed` varchar(255) DEFAULT NULL COMMENT '对地航速',
  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(255) DEFAULT NULL COMMENT '纬度',
  `cog` varchar(255) DEFAULT NULL COMMENT '对地航向',
  `ship_head` varchar(255) DEFAULT NULL COMMENT '船首向',
  `call_sign` varchar(255) DEFAULT NULL COMMENT '呼号',
  `type` varchar(255) DEFAULT NULL COMMENT '船舶和货物类型',
  `eta` varchar(255) DEFAULT NULL COMMENT '估计到达时间',
  `maximum_static_draft` varchar(255) DEFAULT NULL COMMENT '最大静态吃水',
  `destination` varchar(255) DEFAULT NULL COMMENT '目的地',
  `sync_time` bigint(20) DEFAULT NULL COMMENT '同步时间',
  `source_id` varchar(255) DEFAULT NULL COMMENT '数据源 id',
  `departure` varchar(255) DEFAULT NULL COMMENT '出发地',
  `data_sync_time` varchar(255) DEFAULT NULL COMMENT '接口方数据采集时间',
  PRIMARY KEY (`id`),
  KEY `idx_ship_detail_source_id` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6416 DEFAULT CHARSET=utf8 COMMENT='船舶航行信息同步数据表';

SET FOREIGN_KEY_CHECKS = 1;