package com.optimis.custom;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by nex on 10.1.16..
 */
public class UUIDUtil {

    public static void fixUuidAttributes(List<Map<String, Object>> result, Set<String> uuidAttributes) {
        for (Map<String, Object> resultMap : result) {
            fixUuidAttributes(resultMap, uuidAttributes);
        }
    }

    public static void fixUuidAttributes(Map<String, Object> resultMap, Set<String> uuidAttributes) {
        for (String key : resultMap.keySet()) {
            if(uuidAttributes.contains(key)){
                String withoutDashes = (String) resultMap.get(key);
                BigInteger bi1 = new BigInteger(withoutDashes.substring(0, 16), 16);
                BigInteger bi2 = new BigInteger(withoutDashes.substring(16, 32), 16);
                UUID uuid = new UUID(bi1.longValue(), bi2.longValue());
                String withDashes = uuid.toString();
                resultMap.put(key, withDashes);
            }
        }
    }
}
