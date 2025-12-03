package com.mercemay.shortlink.admin.common.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PhoneDesensitizationSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String string, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String phoneDesensitization = DesensitizedUtil.mobilePhone(string); // 手机号脱敏
        jsonGenerator.writeString(phoneDesensitization);
    }
}
