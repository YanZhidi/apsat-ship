package com.zkthinke.service.impl;

import com.zkthinke.domain.StorageContent;
import com.zkthinke.repository.StorageContentMapper;
import com.zkthinke.repository.StorageContentRepository;
import com.zkthinke.service.StorageContentService;
import com.zkthinke.service.dto.StorageContentDTO;
import com.zkthinke.service.dto.StorageContentQueryCriteria;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.QueryHelp;
import com.zkthinke.utils.ValidationUtil;

import java.io.File;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Created by cjj on 2019-08-31.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StorageContentServiceImpl implements StorageContentService {

  private static final String HTTP = "http";

  @Autowired
  private StorageContentRepository storageContentRepository;

  @Autowired
  private StorageContentMapper storageContentMapper;

  @Value("${front.upload.url}")
  private String frontUploadUrl;

  @Override
  public Object queryAll(StorageContentQueryCriteria criteria, Pageable pageable) {
    Page<StorageContent> page = storageContentRepository
        .findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(storageContentMapper::toDto));
  }

  @Override
  public Object queryAll(StorageContentQueryCriteria criteria) {
    return storageContentMapper.toDto(storageContentRepository.findAll(
        (root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder)));
  }

  @Override
  public StorageContentDTO findById(Long id) {
    Optional<StorageContent> storageContent = storageContentRepository.findById(id);
    ValidationUtil.isNull(storageContent, "StorageContent", "id", id);
    return storageContentMapper.toDto(storageContent.get());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public StorageContentDTO create(StorageContent resources) {
      String url = resources.getUrl();
      if(!StringUtils.isEmpty(url) && !url.startsWith(HTTP)){
          resources.setUrl(frontUploadUrl + url);
      }
      return storageContentMapper.toDto(storageContentRepository.save(resources));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(StorageContent resources) {
      String url = resources.getUrl();
      if(!StringUtils.isEmpty(url) && !url.startsWith(HTTP)){
          resources.setUrl(frontUploadUrl + "/" + url);
      }
      Optional<StorageContent> optionalStorageContent = storageContentRepository
            .findById(Long.valueOf(resources.getId()));
      ValidationUtil.isNull(optionalStorageContent, "StorageContent", "id", resources.getId());
      StorageContent storageContent = optionalStorageContent.get();
      storageContent.copy(resources);
      storageContentRepository.save(storageContent);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    storageContentRepository.deleteById(id);
  }

  @Override
  public Optional<StorageContent> findByFileKey(String key) {
    return storageContentRepository.findByFileKey(key);
  }
}
