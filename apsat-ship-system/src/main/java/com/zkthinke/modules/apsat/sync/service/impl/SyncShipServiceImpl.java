package com.zkthinke.modules.apsat.sync.service.impl;

import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;
import com.zkthinke.modules.apsat.sync.repository.SyncShipRepository;
import com.zkthinke.modules.apsat.sync.service.SyncShipService;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipQueryCriteria;
import com.zkthinke.modules.apsat.sync.service.mapper.SyncShipMapper;
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
* @date 2020-10-19
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SyncShipServiceImpl implements SyncShipService {

    @Autowired
    private SyncShipRepository syncShipRepository;

    @Autowired
    private SyncShipMapper syncShipMapper;

    @Override
    public Object queryAll(SyncShipQueryCriteria criteria, Pageable pageable){
        Page<SyncShip> page = syncShipRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(syncShipMapper::toDto));
    }

    @Override
    public List<SyncShipDTO> queryAll(SyncShipQueryCriteria criteria){
        return syncShipMapper.toDto(syncShipRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public SyncShipDTO findById(Long id) {
        Optional<SyncShip> syncShip = syncShipRepository.findById(id);
        ValidationUtil.isNull(syncShip,"SyncShip","id",id);
        return syncShipMapper.toDto(syncShip.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncShipDTO create(SyncShip resources) {
        return syncShipMapper.toDto(syncShipRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SyncShip resources) {
        Optional<SyncShip> optionalSyncShip = syncShipRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalSyncShip,"SyncShip","id",resources.getId());
        SyncShip syncShip = optionalSyncShip.get();
        syncShip.copy(resources);
        syncShipRepository.save(syncShip);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        syncShipRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOrUpdate(List<SyncShip> ships) {
        syncShipRepository.saveAll(ships);
    }
}