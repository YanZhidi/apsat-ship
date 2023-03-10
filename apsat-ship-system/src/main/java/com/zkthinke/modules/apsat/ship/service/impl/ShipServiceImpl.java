package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.mapper.NavigationInformationMapper;
import com.zkthinke.modules.apsat.ship.repository.ShipRepository;
import com.zkthinke.modules.apsat.ship.service.ShipAttentionService;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipDetailMapper;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipMapper;
import com.zkthinke.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author weicb
* @date 2020-10-15
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ShipMapper shipMapper;

    @Autowired
    private ShipDetailMapper shipDetailMapper;

    @Autowired
    private ShipAttentionService shipAttentionService;

    @Autowired
    private NavigationInformationMapper navigationInformationMapper;

    @Override
    public Object queryAll(ShipQueryCriteria criteria, Pageable pageable) {
        Page<Ship> page = shipRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            CriteriaQuery<?> where = criteriaQuery.where(predicate);

            //??????and??????
            List<Predicate> listAnd = new ArrayList<>();
            CriteriaBuilder.In<Long> flightPlanSerial = criteriaBuilder.in(root.get("id"));
            if(null != criteria.getShipIds() && criteria.getShipIds().size() > 0 ) {
                for(Long id:criteria.getShipIds()) {
                    flightPlanSerial.value(id);
                }
            }

            listAnd.add(flightPlanSerial);
            List<Predicate> listOr = new ArrayList<>(); // ??????or??????
            if(!StringUtils.isEmpty(criteria.getName())) {
                //???????????? ???OR??????
                listOr.add(criteriaBuilder.like(root.get("name"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.get("mmsiNumber"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.get("imoNumber"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.get("callSign"), "%" + criteria.getName() + "%"));
                // ??????????????????
                String pinyin = PinYinUtils.toChinesePinyin(criteria.getName());
                listOr.add(criteriaBuilder.like(root.get("namePinyin"), "%" + pinyin + "%"));
            }
            Predicate predicateOR = criteriaBuilder.or(listOr.toArray(new Predicate[listOr.size()])); //OR????????????????????????

            if( !StringUtils.isEmpty(criteria.getName()) && !SecurityUtils.getAdminRole() ) {
                listAnd.add(predicateOR);
            }
            Predicate preAnd = criteriaBuilder.and(listAnd.toArray(new Predicate[listAnd.size()])); //AND????????????????????????

            //?????????????????????
            if(!StringUtils.isEmpty(criteria.getName())) {
                where.where(predicateOR);
            }
            //????????????????????? && ???????????????
            if (!SecurityUtils.getAdminRole()){
                where.where(preAnd);
            }
            return where.getRestriction();

        }, pageable);
//        Page<Ship> ships = page.map(shipMapper::toDto);
        List<Ship> ships = page.getContent();
        List<Long> ids = ships.stream().map(Ship::getId).collect(Collectors.toList());
        // ??????????????????????????????????????????
        List<ShipAttentionDTO> attentions = new ArrayList<>();
        if(ids != null && ids.size() > 0) {
            attentions = shipAttentionService.findAttentions(ids, criteria.getUserId());
        }
        /*
          ??????id??????????????????????????????Duplicate key xxx, ??????????????????????????????value
          Map<String, Student> map = list.stream().collect(Collectors.toMap(Student::getId,Function.identity(),(oldValue,newValue) -> newValue))
          ????????????id???????????????vaue??????????????????????????????id???
          Map<String, String> map = list.stream().collect(Collectors.toMap(Student::getId,Student::getName,(e1,e2)->e1+","+e2));
          */
        Map<Long, ShipAttentionDTO> attentionMap = attentions.stream().filter(a -> a.getShip() != null)
                .collect(Collectors.toMap(a -> a.getShip().getId(), a -> a, (p, q) -> q));

        List<ShipDTO> list = ships.stream().map(s -> {
            ShipDTO ship = shipMapper.toDto(s);
            ship.setAttention(attentionMap.get(s.getId()) == null ? 0 : 1);
            if (s.getLastShipDetail()!=null){
                // ????????????
                ShipDetailDTO shipDetail = shipDetailMapper.toDto(s.getLastShipDetail());
                this.navigationPercent(shipDetail);
                ship.setLastShipDetail(shipDetail);
    //            ShipDetailDTO lastShipDetail = shipDetail.getLastShipDetail();
                if("0".equals(shipDetail.getSailingStatus())) {
                    shipDetail.setSailingStatus("??????????????????");
                }else if("1".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("??????");
                } else if("2".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("?????????");
                }else if("3".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("???????????????");
                }else if("4".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("?????????????????????");
                }else if("5".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("??????");
                }else if("6".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("??????");
                }else if("7".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("????????????");
                }else if("8".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("?????????");
                }else if("9".equals(shipDetail.getSailingStatus())){
                    shipDetail.setSailingStatus("??????????????????");
                }
            }
            return ship;
        }).collect(Collectors.toList());
        return PageUtil.toPage(list, page.getTotalElements());
    }

    /**
     * ?????????????????????
     */
    private void navigationPercent(ShipDetailDTO detail) {
        if (detail == null) {
            return;
        }
        if (detail.getDepartureTime() != null && detail.getEta() != null) {
            if (System.currentTimeMillis() >= detail.getEta()) {
                detail.setPercent(new BigDecimal(1));
                return;
            }
            BigDecimal percent = new BigDecimal((System.currentTimeMillis() - detail.getDepartureTime()))
                    .divide(new BigDecimal(detail.getEta() - detail.getDepartureTime()),
                            2, BigDecimal.ROUND_HALF_UP);
            detail.setPercent(percent);
        }
    }

    @Override
    public Object queryAll(ShipQueryCriteria criteria){
        return shipMapper.toDto(shipRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public ShipDTO findById(Long id) {
        Optional<Ship> ship = shipRepository.findById(id);
        ValidationUtil.isNull(ship,"Ship","id",id);
        Ship s = ship.get();
        ShipDTO shipDTO = shipMapper.toDto(s);

        //????????????

        // ????????????
        ShipDetailDTO shipDetail = shipDetailMapper.toDto(s.getLastShipDetail());
        this.navigationPercent(shipDetail);
        shipDTO.setLastShipDetail(shipDetail);
        ShipDetailDTO lastShipDetail = shipDTO.getLastShipDetail();
        if("0".equals(lastShipDetail.getSailingStatus())) {
            lastShipDetail.setSailingStatus("??????????????????");
        }else if("1".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("??????");
        } else if("2".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("?????????");
        }else if("3".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("???????????????");
        }else if("4".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("?????????????????????");
        }else if("5".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("??????");
        }else if("6".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("??????");
        }else if("7".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("????????????");
        }else if("8".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("?????????");
        }else if("9".equals(lastShipDetail.getSailingStatus())){
            lastShipDetail.setSailingStatus("??????????????????");
        }
        return shipDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShipDTO create(Ship resources) {
        resources.setNamePinyin(PinYinUtils.toChinesePinyin(resources.getName()));
        return shipMapper.toDto(shipRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Ship resources) {
        resources.setNamePinyin(PinYinUtils.toChinesePinyin(resources.getName()));
        Optional<Ship> optionalShip = shipRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalShip,"Ship","id",resources.getId());
        Ship ship = optionalShip.get();
        resources.setType(ship.getType());
        resources.setLastDetailStime(ship.getLastDetailStime());
        resources.setLastDeviceStime(ship.getLastDeviceStime());
        resources.setLastShipDetail(ship.getLastShipDetail());
        resources.setUpdateTime(System.currentTimeMillis());
        ship.copy(resources);
        shipRepository.save(ship);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSync(Ship resources) {
        Optional<Ship> optionalShip = shipRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalShip,"Ship","id",resources.getId());
        Ship ship = optionalShip.get();
        resources.setUpdateTime(System.currentTimeMillis());
        ship.copy(resources);
        shipRepository.save(ship);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public Optional<Ship> findOne(ShipQueryCriteria criteria) {
        return shipRepository.findOne((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<Ship> findAll() {
        return shipRepository.findAll();
    }

    @Override
    public int updateLastDeviceStime(Long shipId, String requestTime) {
        return shipRepository.updateLastDeviceStime(shipId, requestTime);
    }

    @Override
    public String getZdaTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy???M???d??? HH:mm:ss");
        String zdaTimeInDb = navigationInformationMapper.getZdaTime();
        Date date;
        try {
            date = sdf.parse(zdaTimeInDb);
        } catch (ParseException e) {
            date = new Date();
        }
        return  sdf2.format(date);

    }

    @Override
    public Object getMenuTree() {
        List<Map<String,Object>> list = new LinkedList<>();
        List<Ship> ships = shipRepository.findAll();
        if(ships != null && ships.size() > 0 ) {
            for (Ship ship : ships ) {
                Map<String,Object> map = new HashMap<>();
                map.put("id",ship.getId());
                map.put("label",ship.getName());
                list.add(map);
            }
        }
        return list;
    }

}
