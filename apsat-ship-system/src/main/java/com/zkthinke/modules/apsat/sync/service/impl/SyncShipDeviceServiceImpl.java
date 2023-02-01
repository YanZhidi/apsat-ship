package com.zkthinke.modules.apsat.sync.service.impl;

import com.zkthinke.modules.apsat.sync.domain.SyncShipDevice;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import com.zkthinke.modules.apsat.sync.repository.SyncShipDeviceRepository;
import com.zkthinke.modules.apsat.sync.service.SyncShipDeviceService;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceQueryCriteria;
import com.zkthinke.modules.apsat.sync.service.mapper.SyncShipDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
* @author weicb
* @date 2020-10-28
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SyncShipDeviceServiceImpl implements SyncShipDeviceService {

    @Autowired
    private SyncShipDeviceRepository syncShipDeviceRepository;

    @Autowired
    private SyncShipDeviceMapper syncShipDeviceMapper;

    @Override
    public Object queryAll(SyncShipDeviceQueryCriteria criteria, Pageable pageable){
        Page<SyncShipDevice> page = syncShipDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(syncShipDeviceMapper::toDto));
    }

    @Override
    public List<SyncShipDeviceDTO> queryAll(SyncShipDeviceQueryCriteria criteria){
        return syncShipDeviceMapper.toDto(syncShipDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public SyncShipDeviceDTO findById(Long id) {
        Optional<SyncShipDevice> syncShipDevice = syncShipDeviceRepository.findById(id);
        ValidationUtil.isNull(syncShipDevice,"SyncShipDevice","id",id);
        return syncShipDeviceMapper.toDto(syncShipDevice.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncShipDeviceDTO create(SyncShipDevice resources) {
        return syncShipDeviceMapper.toDto(syncShipDeviceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SyncShipDevice resources) {
        Optional<SyncShipDevice> optionalSyncShipDevice = syncShipDeviceRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalSyncShipDevice,"SyncShipDevice","id",resources.getId());
        SyncShipDevice syncShipDevice = optionalSyncShipDevice.get();
        syncShipDevice.copy(resources);
        syncShipDeviceRepository.save(syncShipDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        syncShipDeviceRepository.deleteById(id);
    }

    @Override
    public void batchInsertOrUpdate(List<SyncShipDevice> shipDevices) {
        syncShipDeviceRepository.saveAll(shipDevices);
    }

}