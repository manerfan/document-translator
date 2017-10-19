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

package com.manerfan.translator.web;

import com.manerfan.translator.jpa.entities.FileEntity;
import com.manerfan.translator.jpa.repositories.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author manerfan
 * @date 2017/11/2
 */

@Component
@ConditionalOnProperty(value = "server.data.clean.enabled", matchIfMissing = true)
public class TempDocCleanerConfiguration {
    Logger logger = LoggerFactory.getLogger(TempDocCleanerConfiguration.class);

    @Autowired
    FileRepository fileRepository;

    @Value("${server.data.tmpdir}")
    File tmpDir;

    @Async
    @Scheduled(cron = "0 0 2 * * ?")
    public void clean() {
        Date limit = Date.from(LocalDateTime.now().plusDays(-1).atZone(ZoneId.systemDefault()).toInstant());

        try {
            List<FileEntity> files = fileRepository.findBefore(limit);
            files.stream().map((file) -> new File(tmpDir, file.getName())).forEach((file) -> {
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            });
            fileRepository.deleteBefore(limit);
        } catch (Exception e) {
            logger.error("TempDocCleaner Schedule Error!", e);
        }
    }
}
