package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import com.zkthinke.modules.apsat.ship.repository.ShipDeviceRepository;
import com.zkthinke.modules.apsat.ship.repository.ShipDeviceSimpleRepository;
import com.zkthinke.modules.apsat.ship.service.ShipDeviceService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceSimpleDTO;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipDeviceMapper;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipDeviceSimpleMapper;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
* @author weicb
* @date 2020-10-28
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ShipDeviceServiceImpl implements ShipDeviceService {

    @Autowired
    private ShipDeviceRepository shipDeviceRepository;

    @Autowired
    private ShipDeviceMapper shipDeviceMapper;

    @Autowired
    private ShipDeviceSimpleRepository shipDeviceSimpleRepository;

    @Autowired
    private ShipDeviceSimpleMapper shipDeviceSimpleMapper;

    @Override
    public Object queryAll(ShipDeviceQueryCriteria criteria, Pageable pageable){
        Page<ShipDevice> page = shipDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(shipDeviceMapper::toDto));
    }

    @Override
    public Object queryAll(ShipDeviceQueryCriteria criteria){
        return shipDeviceMapper.toDto(shipDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public ShipDeviceDTO findById(Long id) {
        Optional<ShipDevice> shipDevice = shipDeviceRepository.findById(id);
        ValidationUtil.isNull(shipDevice,"ShipDevice","id",id);
        return shipDeviceMapper.toDto(shipDevice.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShipDeviceDTO create(ShipDevice resources) {
        return shipDeviceMapper.toDto(shipDeviceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShipDevice resources) {
        Optional<ShipDevice> optionalShipDevice = shipDeviceRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalShipDevice,"ShipDevice","id",resources.getId());
        ShipDevice shipDevice = optionalShipDevice.get();
        shipDevice.copy(resources);
        shipDeviceRepository.save(shipDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        shipDeviceRepository.deleteById(id);
    }

    @Override
    public void batchInsertOrUpdate(List<ShipDevice> shipDevices) {
        shipDeviceRepository.saveAll(shipDevices);
    }


    @Override
    public ShipDeviceDTO findLastByShipId(Long shipId) {
        Optional<ShipDevice> shipDevice = shipDeviceRepository.findLastByShipId(shipId);
        ValidationUtil.isNull(shipDevice,"ShipDevice","shipId",shipId);
        return shipDeviceMapper.toDto(shipDevice.get());
    }

    @Override
    public List<ShipDeviceSimpleDTO> findSimpleByShipId(Long shipId, Optional<Long> collectTimeBegin, Optional<Long> collectTimeEnd) {
        ShipDeviceQueryCriteria query = new ShipDeviceQueryCriteria();
        query.setShipId(shipId);
        collectTimeBegin.ifPresent(cb -> query.setCollectTimeBegin(cb));
        collectTimeEnd.ifPresent(ce -> query.setCollectTimeEnd(ce));
        // 这里要分批进行数据查询,以最大限度保证性能
        return shipDeviceSimpleMapper.toDto(shipDeviceSimpleRepository.findAll(
                (root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, query, criteriaBuilder))
        );
    }
}