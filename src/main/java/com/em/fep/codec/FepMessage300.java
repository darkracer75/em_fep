package com.em.fep.codec;

import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class FepMessage300 implements FepResponse {

    private PacketHeader header;
    private int blockNo;
    private int sequenceNo;
    private int missingCnt;
    private char[] missingBit;


    public void setHeader(PacketHeader header) {
        this.header = header;
    }

    @Override
    public PacketHeader getHeader() {
        return header;
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
                .append(StringUtils.leftPad(String.valueOf(blockNo), 4, '0'))
                .append(StringUtils.leftPad(String.valueOf(sequenceNo), 3, '0'))
                .append(StringUtils.leftPad(String.valueOf(missingCnt), 3, '0'))
                .append(missingBit);


        System.out.println("response String:" + s.toString());
        return s.toString().getBytes();
    }

    public void setMissingCnt(int missingCnt) {
        this.missingCnt = missingCnt;
    }

    public void setMissingBit(char[] missingBit) {
        this.missingBit = missingBit;
    }

    public void setBlockNo(int blockNo) {
        this.blockNo = blockNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public int length() {
        return PacketHeader.HEADER_LENGTH + 4 + 3 + 3 + (missingBit != null ? missingBit.length : 0);
    }
}
