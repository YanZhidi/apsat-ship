package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.domain.ShipAttention;
import com.zkthinke.modules.apsat.ship.repository.ShipAttentionRepository;
import com.zkthinke.modules.apsat.ship.service.ShipAttentionService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipAttentionMapper;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipMapper;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.PinYinUtils;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @author weicb
* @date 2020-11-01
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ShipAttentionServiceImpl implements ShipAttentionService {

    @Autowired
    private ShipAttentionRepository shipAttentionRepository;

    @Autowired
    private ShipAttentionMapper shipAttentionMapper;

    @Autowired
    private ShipMapper shipMapper;

    @Override
    public Object queryAll(ShipAttentionQueryCriteria criteria, Pageable pageable){
        Page<ShipAttention> page = shipAttentionRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            List<Predicate> listOr = new ArrayList<>(); // 组装or语句
            if(!StringUtils.isEmpty(criteria.getName())) {
                //模糊匹配 用OR链接
                listOr.add(criteriaBuilder.like(root.join("ship", JoinType.LEFT).get("name"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.join("ship", JoinType.LEFT).get("mmsiNumber"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.join("ship", JoinType.LEFT).get("imoNumber"), "%" + criteria.getName() + "%"));
                listOr.add(criteriaBuilder.like(root.join("ship", JoinType.LEFT).get("callSign"), "%" + criteria.getName() + "%"));
                // 名称拼音匹配
                String pinyin = PinYinUtils.toChinesePinyin(criteria.getName());
                listOr.add(criteriaBuilder.like(root.join("ship", JoinType.LEFT).get("namePinyin"), "%" + pinyin + "%"));
                Predicate predicateOR = criteriaBuilder.or(listOr.toArray(new Predicate[listOr.size()])); //OR查询加入查询条件
                return criteriaQuery.where(predicate, predicateOR).getRestriction();
            }
            return  predicate;
        }, pageable);

        Page<ShipDTO> list = page.map(a -> shipMapper.toDto(a.getShip()));
        list.forEach(s -> s.setAttention(1));
        return PageUtil.toPage(list);
    }

    @Override
    public Object queryAll(ShipAttentionQueryCriteria criteria){
        return shipAttentionMapper.toDto(shipAttentionRepository.findAll((root, criteriaQuery, criteriaBuilder) ->
                QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public ShipAttentionDTO findById(Long id) {
        Optional<ShipAttention> shipAttention = shipAttentionRepository.findById(id);
        ValidationUtil.isNull(shipAttention,"ShipAttention","id",id);
        return shipAttentionMapper.toDto(shipAttention.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShipAttentionDTO create(ShipAttention resources) {
        ShipAttentionQueryCriteria criteria = new ShipAttentionQueryCriteria();
        criteria.setUserId(resources.getUserId());
        criteria.setShipId(resources.getShip().getId());
        Optional<ShipAttention> shipAttention = shipAttentionRepository.findOne((root, criteriaQuery, criteriaBuilder) ->
                QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        resources.setCreateTime(System.currentTimeMillis());
        ShipAttention attention = shipAttention.orElse(resources);
        attention.setUpdateTime(System.currentTimeMillis());
        attention.setAttention(1);
        attention.setAttentionTime(System.currentTimeMillis());
        return shipAttentionMapper.toDto(shipAttentionRepository.save(attention));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShipAttention resources) {
        Optional<ShipAttention> optionalShipAttention = shipAttentionRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalShipAttention,"ShipAttention","id",resources.getId());
        ShipAttention shipAttention = optionalShipAttention.get();
        shipAttention.copy(resources);
        shipAttentionRepository.save(shipAttention);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        shipAttentionRepository.deleteById(id);
    }

    @Override
    public void deleteByShipId(Long shipId, Long userId) {
        ShipAttentionQueryCriteria criteria = new ShipAttentionQueryCriteria();
        criteria.setShipId(shipId);
        criteria.setUserId(userId);
        Optional<ShipAttention> shipAttention = shipAttentionRepository.findOne((root, criteriaQuery, criteriaBuilder) ->
                QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        shipAttention.ifPresent(a -> shipAttentionRepository.deleteById(a.getId()));
    }

    @Override
    public List<ShipAttentionDTO> findAttentions(List<Long> ids, Long userId) {
        return shipAttentionMapper.toDto(shipAttentionRepository.findAttentions(ids, userId));
    }
}