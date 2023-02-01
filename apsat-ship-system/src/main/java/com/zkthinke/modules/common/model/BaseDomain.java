package com.zkthinke.modules.common.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Created by kellen on 2019/9/5.
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseDomain {

  @Column(name = "date_created")
  @CreatedDate
  private Timestamp dateCreated;

  @Column(name = "date_updated")
  @LastModifiedDate
  private Timestamp dateUpdated;

  @Column(name = "created_by")
  @CreatedBy
  private String createdBy;

  @Column(name = "updated_by")
  @LastModifiedBy
  private String updatedBy;

}
