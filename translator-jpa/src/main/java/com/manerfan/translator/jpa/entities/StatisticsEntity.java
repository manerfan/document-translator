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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author manerfan
 * @date 2017/11/2
 */

@Entity(name = "Statistics")
@Table(name = "statistics")
public class StatisticsEntity extends CommonEntity {

    @Column(name = "key", nullable = false, unique = true)
    String key = "trans-statistics";

    /**
     * 文字翻译次数
     */
    @Column(name = "text_trans_num", nullable = false, columnDefinition = "bigint default 0")
    long textTransNum;

    /**
     * 文档翻译次数
     */
    @Column(name = "doc_trans_num", nullable = false, columnDefinition = "bigint default 0")
    long docTransNum;

    /**
     * 文档翻译次数
     */
    @Column(name = "sbd_num", nullable = false, columnDefinition = "bigint default 0")
    long sbdNum;

    /**
     * 翻译字节数(KB)
     */
    @Column(name = "byte_trans_num", nullable = false, columnDefinition = "bigint default 0")
    long byteTransNum;

    public long getSbdNum() {
        return sbdNum;
    }

    public void setSbdNum(long sbdNum) {
        this.sbdNum = sbdNum;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(key, textTransNum, docTransNum, sbdNum, byteTransNum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StatisticsEntity other = (StatisticsEntity) obj;
        return Objects.equal(this.key, other.key)
                && Objects.equal(this.textTransNum, other.textTransNum)
                && Objects.equal(this.docTransNum, other.docTransNum)
                && Objects.equal(this.sbdNum, other.sbdNum)
                && Objects.equal(this.byteTransNum, other.byteTransNum);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTextTransNum() {
        return textTransNum;
    }

    public void setTextTransNum(long textTransNum) {
        this.textTransNum = textTransNum;
    }

    public long getDocTransNum() {
        return docTransNum;
    }

    public void setDocTransNum(long docTransNum) {
        this.docTransNum = docTransNum;
    }

    public long getByteTransNum() {
        return byteTransNum;
    }

    public void setByteTransNum(long byteTransNum) {
        this.byteTransNum = byteTransNum;
    }

}
