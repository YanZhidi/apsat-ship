package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.repository.ShipDetailRepository;
import com.zkthinke.modules.apsat.ship.repository.ShipDetailSimpleRepository;
import com.zkthinke.modules.apsat.ship.service.ShipDetailService;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.ship.service.dto.*;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipDetailMapper;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipDetailSimpleMapper;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @author weicb
* @date 2020-10-15
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ShipDetailServiceImpl implements ShipDetailService {

    @Autowired
    private ShipDetailRepository shipDetailRepository;

    @Autowired
    private ShipDetailMapper shipDetailMapper;

    @Autowired
    private ShipService shipService;

    @Autowired
    private ShipDetailSimpleRepository shipDetailSimpleRepository;

    @Autowired
    private ShipDetailSimpleMapper shipDetailSimpleMapper;

    @Override
    public Object queryAll(ShipDetailQueryCriteria criteria, Pageable pageable){
        Sort.Order order = Sort.Order.asc("collectTime");
        Sort sort = Sort.by(order);
        List<ShipDetail> list = shipDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) ->
                QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort);
        List<ShipDetailSimple2DTO> resultList = list.stream().map(ShipDetailSimple2DTO::new).collect(Collectors.toList());
        return PageUtil.toPage(resultList, list.size());
    }

    @Override
    public Object queryAll(ShipDetailQueryCriteria criteria){
        return shipDetailMapper.toDto(shipDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public ShipDetailDTO findById(Long id) {
        Optional<ShipDetail> shipDetail = shipDetailRepository.findById(id);
        ValidationUtil.isNull(shipDetail,"ShipDetail","id",id);
        return shipDetailMapper.toDto(shipDetail.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShipDetailDTO create(ShipDetail resources) {
        return shipDetailMapper.toDto(shipDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShipDetail resources) {
        Optional<ShipDetail> optionalShipDetail = shipDetailRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalShipDetail,"ShipDetail","id",resources.getId());
        ShipDetail shipDetail = optionalShipDetail.get();
        shipDetail.copy(resources);
        shipDetailRepository.save(shipDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        shipDetailRepository.deleteById(id);
    }

    @Override
    public ShipDetailDTO findLastByShipId(Long shipId) {
        ShipDTO ship = shipService.findById(shipId);
        Optional<ShipDetailDTO> shipDetail = Optional.ofNullable(ship.getLastShipDetail());
        if (!shipDetail.isPresent()) {
            return new ShipDetailDTO();
        }
        ShipDetailDTO shipDetailDTO = shipDetail.get();
        if("0".equals(shipDetailDTO.getSailingStatus())) {
            shipDetailDTO.setSailingStatus("发动机使用中");
        }else if("1".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("锚泊");
        } else if("2".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("未操纵");
        }else if("3".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("有限适航性");
        }else if("4".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("受船舶吃水限制");
        }else if("5".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("系泊");
        }else if("6".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("搁浅");
        }else if("7".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("从事捕捞");
        }else if("7".equals(shipDetailDTO.getSailingStatus())){
            shipDetailDTO.setSailingStatus("航行中");
        }
        
        return shipDetailDTO;
    }

    @Override
    public void batchInsertOrUpdate(List<ShipDetail> shipDetails) {
        shipDetailRepository.saveAll(shipDetails);
    }

    @Override
    public List<ShipDetailSimpleDTO> findSimpleByShipId(Long shipId, Optional<Long> collectTimeBegin, Optional<Long> collectTimeEnd) {
        ShipDetailQueryCriteria query = new ShipDetailQueryCriteria();
        query.setShipId(shipId);
        collectTimeBegin.ifPresent(cb -> query.setCollectTimeBegin(cb));
        collectTimeEnd.ifPresent(ce -> query.setCollectTimeEnd(ce));
        // 这里要分批进行数据查询,以最大限度保证性能
        return shipDetailSimpleMapper.toDto(shipDetailSimpleRepository.findAll(
                (root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, query, criteriaBuilder))
        );
    }
    @Override
    public List<ShipDetail> findByIdOrLike(Long id, String groundSpeed) {
        List<ShipDetail> shipDetails = shipDetailRepository.findAll(
                ((root, criteriaQuery, criteriaBuilder) ->
        {

//            Predicate predicateEq = criteriaBuilder.equal(root.get("id").as(Long.class), id);
//            Predicate predicateLike = criteriaBuilder.like(root.get("groundSpeed").as(String.class), groundSpeed + "%");
//
//            Predicate predicates = criteriaBuilder.or(predicateEq, predicateLike);

//            Predicate predicates = criteriaBuilder.or(
//                    criteriaBuilder.equal(root.get("id").as(Long.class), id),
//                    criteriaBuilder.like(root.get("groundSpeed").as(String.class), groundSpeed + "%"));

            List<Predicate> predicates = new ArrayList<>();
            Predicate predicateEq = criteriaBuilder.equal(root.get("id").as(Long.class),id);
            Predicate predicateLike = criteriaBuilder.like(root.get("groudSpeed").as(String.class),groundSpeed + "%");
            Collections.addAll(predicates,predicateEq,predicateLike);

            Predicate predicateOr = criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));

            return predicateOr;
        })
        );
        return shipDetails;
    }
}