package com.javaspring.petclinic.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "petClinicSeqGen")
    @SequenceGenerator(name = "petClinicSeqGen",sequenceName = "petclinic_sequence")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createDate;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Date updatedDate;

}
