package com.optimis.custom;

import org.apache.commons.codec.digest.DigestUtils;

public class OptimisPasswordEncoder {

    public String encode(String source, int numberOfIterations) {
            for (int i = 0; i < numberOfIterations; i++) {
                source = DigestUtils.sha512Hex(source);
            }
        return source;
    }
}