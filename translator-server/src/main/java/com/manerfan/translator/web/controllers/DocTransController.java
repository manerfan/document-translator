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

package com.manerfan.translator.web.controllers;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.manerfan.translator.api.baidu.crypto.MD5;
import com.manerfan.translator.api.document.DocumentTransManager;
import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import com.manerfan.translator.jpa.entities.FileEntity;
import com.manerfan.translator.jpa.repositories.FileRepository;
import com.manerfan.translator.web.controllers.body.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author manerfan
 * @date 2017/11/1
 */

@RestController
public class DocTransController extends ApiControllerBase {
    static final String TMP_PREFIX = "doctrans_";

    @Autowired
    MD5 md5;

    @Autowired
    DocumentTransManager documentTransManager;

    @Autowired
    FileRepository fileRepository;

    @Value("${server.data.tmpdir}")
    File tmpDir;

    @PostMapping("/doc")
    public ResponseBody trans(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "from", required = false, defaultValue = "auto") String from,
            @RequestParam String to) throws Exception {
        String srcFilename = file.getOriginalFilename();

        String key = md5.encrypt(UUID.randomUUID().toString());
        File srcDoc = new File(tmpDir, new StringBuilder()
                .append(TMP_PREFIX).append(key).append(".")
                .append(Files.getFileExtension(srcFilename))
                .toString());
        file.transferTo(srcDoc);
        fileRepository.save(new FileEntity(srcDoc.getName(), from));

        String destFile = documentTransManager.translate(srcDoc.getAbsolutePath(), from, to);
        FileEntity dstFile = fileRepository.save(new FileEntity(StringUtils.getFilename(destFile), to));

        Map<String, Object> data = Maps.newHashMap();
        data.put("from", from);
        data.put("to", to);
        data.put("key", dstFile.getUuid());
        data.put("filename", new StringBuilder()
                .append(Files.getNameWithoutExtension(srcFilename))
                .append("_").append(to).append(".")
                .append(Files.getFileExtension(srcFilename))
                .toString());

        return ResponseBody.newBody(data);
    }

    @GetMapping("/doc/{key}")
    public Object download(@PathVariable String key, @RequestParam String filename) throws IOException {
        FileEntity entity = fileRepository.findOne(key);
        Optional<FileEntity> fileEntity = Optional.ofNullable(entity);
        File file = fileEntity.map(FileEntity::getName).map((name) -> new File(tmpDir, name)).orElse(null);

        if (null == file || !file.exists()) {
            throw new BusinessException(ErrorCode.RESOURCE_MISSING, "Resource Not Found! " + key);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(filename, StandardCharsets.UTF_8.name()));
        return new ResponseEntity(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }
}
