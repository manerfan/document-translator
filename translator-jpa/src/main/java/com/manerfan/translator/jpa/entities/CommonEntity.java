/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.manerfan.translator.jpa.entities;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author manerfan
 * @date 2017/11/1
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class CommonEntity implements Serializable {
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    @GeneratedValue(generator = "IDGenerator")
    @GenericGenerator(name = "IDGenerator", strategy = "uuid")
    String uuid;

    @Column(name = "createdAt")
    @CreatedDate
    Date createdAt;

    @Column(name = "updatedAt")
    @LastModifiedDate
    Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid, createdAt, updatedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CommonEntity other = (CommonEntity) obj;
        return Objects.equal(this.uuid, other.uuid)
                && Objects.equal(this.createdAt, other.createdAt)
                && Objects.equal(this.updatedAt, other.updatedAt);
    }
}
