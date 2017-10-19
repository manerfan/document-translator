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
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author manerfan
 * @date 2017/11/1
 */

@Entity(name = "File")
@Table(name = "file", indexes = {
        @Index(name = "file_name_index", columnList = "name", unique = true),
        @Index(name = "file_language_index", columnList = "language")
})
public class FileEntity extends CommonEntity {

    @Column(name = "name", unique = true, nullable = false)
    String name;

    @Column(name = "language", nullable = false)
    String language;

    public FileEntity() {
    }

    public FileEntity(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(name, language);
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
        final FileEntity other = (FileEntity) obj;
        return Objects.equal(this.name, other.name)
                && Objects.equal(this.language, other.language);
    }
}
