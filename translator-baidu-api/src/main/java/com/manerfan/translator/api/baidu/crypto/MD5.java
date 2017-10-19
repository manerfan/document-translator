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

package com.manerfan.translator.api.baidu.crypto;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author manerfan
 * @date 2017/10/12
 */

@Component
public class MD5 {
    GenericObjectPool<MessageDigest> md5DigestPool;

    public String encrypt(String str) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = md5DigestPool.borrowObject();
            md5.reset();
            md5.update(str.getBytes());
            byte[] encryptBytes = md5.digest();
            BigInteger encryptBitInt = new BigInteger(1, encryptBytes);
            return String.format("%32s", encryptBitInt.toString(16)).replaceAll("\\s", "0");
        } finally {
            if (null != md5) {
                md5DigestPool.returnObject(md5);
            }
        }
    }

    BasePooledObjectFactory<MessageDigest> md5DigestPoolFactory() {
        return new BasePooledObjectFactory<MessageDigest>() {
            @Override
            public MessageDigest create() throws Exception {
                return MessageDigest.getInstance("MD5");
            }

            @Override
            public PooledObject<MessageDigest> wrap(MessageDigest messageDigest) {
                return new DefaultPooledObject<>(messageDigest);
            }
        };
    }

    @PostConstruct
    void initMessageDigestPool() throws Exception {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        /* 设置池中最大idle，若idle数量大于此值，则会清理多余idle */
        poolConfig.setMaxIdle(100);
        /* 设置多久没有borrow则设置为idle */
        poolConfig.setMinEvictableIdleTimeMillis(5 * 60 * 1000);

        /* 设置池中最小idle，若idle数量小于此值，则在Evictor定时器中会自动创建idle */
        poolConfig.setMinIdle(50);
        /* 设置Evictor定时器周期并启动定时器 */
        poolConfig.setTimeBetweenEvictionRunsMillis(30 * 1000);

        /* 设置池中最大数量，若达到上限时borrow，则阻塞 */
        poolConfig.setMaxTotal(500);

        /* 调用者最大阻塞的时间 */
        poolConfig.setMaxWaitMillis(5 * 1000);

        md5DigestPool = new GenericObjectPool<>(md5DigestPoolFactory(), poolConfig);
        // 初始化池中idle个数到minIdle，提前create，免得在使用时再创建
        // BaseGenericObjectPool.Evictor定时器会定时执行ensureMinIdle，确保idle keypare个数可以达到minIdle
        md5DigestPool.preparePool();
    }
}
