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

package com.manerfan.translator.api.baidu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by manerfan on 2017/10/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaiduTransAutoConfiguration.class)
public class BaiduTransUtilTest {

    @Autowired
    TransUtil transUtil;

    @Test
    public void transTest() throws Exception {
//        Pattern pattern = Pattern.compile("@(?!\\s)+");
//        String q = "@andy @manerfan 哈哈 @super @刘灿";
//        Matcher matcher = pattern.matcher(q);
//        while (matcher.find()) {
//            System.out.println(matcher.group());
//        }
//        System.out.println(transUtil.trans("哈哈，这里是测试环境", "auto", "en"));
        System.out.println(Integer.toHexString(UUID.randomUUID().hashCode()));
        System.out.println(Integer.toHexString(UUID.randomUUID().hashCode()));
        System.out.println(Integer.toHexString(UUID.randomUUID().hashCode()));
        System.out.println(Integer.toHexString(UUID.randomUUID().hashCode()));
        System.out.println(Integer.toHexString(UUID.randomUUID().hashCode()));
    }

}
