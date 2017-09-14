package com.em.fep.codec;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter@Getter
@ToString
public class PacketHeader implements Cloneable{

    public static final int HEADER_LENGTH = 22;

    private int length;
    private String taskDivCode;
    private String organCode;
    private String kindCode;
    private String transDivCode;
    private String sendRcvFlag;
    private String fileName;
    private String responseCode;

    @Override
    public PacketHeader clone()  {
        try {
            return (PacketHeader) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
