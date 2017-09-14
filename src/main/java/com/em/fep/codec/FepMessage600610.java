package com.em.fep.codec;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

@Getter
@Setter
@ToString
public class FepMessage600610 implements FepRequest , FepResponse {

    private PacketHeader header;
    private String packetSendDt;
    private String taskManageInfo;
    private String userName;
    private String password;

    @Override
    public void parseBody(ByteBuf body) {

        String bodyStr = body.toString(Charset.defaultCharset());

        packetSendDt = bodyStr.substring(0, 10);
        taskManageInfo = bodyStr.substring(10, 13);
        userName = bodyStr.substring(13, 33);
        password = bodyStr.substring(33, 49);
    }

    public void setHeader(PacketHeader header) {
        this.header = header;
    }

    @Override
    public byte[] toByteArray() {

        StringBuffer s = new StringBuffer();
        s.append(StringUtils.leftPad(String.valueOf(header.getLength()), 4, '0'))
                .append(StringUtils.rightPad(header.getTaskDivCode(), 3))
                .append(StringUtils.rightPad(header.getOrganCode(), 2))
                .append(StringUtils.rightPad(header.getKindCode(), 4))
                .append(StringUtils.rightPad(header.getTransDivCode(), 1))
                .append(StringUtils.rightPad(header.getSendRcvFlag(), 1))
                .append(StringUtils.rightPad(header.getFileName(), 8))
                .append(StringUtils.rightPad(header.getResponseCode(), 3))
                .append(StringUtils.rightPad(packetSendDt, 10))
                .append(StringUtils.rightPad(taskManageInfo, 3))
                .append(StringUtils.rightPad(StringUtils.defaultString(userName), 20))
                .append(StringUtils.rightPad(StringUtils.defaultString(password), 16));


        System.out.println("response String:" + s.toString());
        return s.toString().getBytes();
    }
}
