package com.em.fep.server;

import com.em.fep.codec.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FepServerHandShakingHandler extends ChannelInboundHandlerAdapter {

    public final static String FILE_ROOT ="D:/data/fep_files";

    public final static int MAX_BLOCK_SIZE = 9999;
    public final static int MAX_SEQ_SIZE = 999;

    public final static int BYTES_PER_SEQ = 3600;

    public final static int LINE_LENGTH = 400;

    public final static byte[] LINE_SEPARATOR = System.getProperty("line.separator").getBytes();

    private boolean beginProcess = false;

    private ReceiveFile receiveFile;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof FepRequest) {

            FepRequest req = (FepRequest) msg;

            System.out.println(String.format("receive data request:[%s]", req));

            if ("0600".equals(req.getHeader().getKindCode())) {
                process0600(ctx, req);
            } else if ("0630".equals(req.getHeader().getKindCode())) {
                process0630(ctx, req);
            } else if ("0320".equals(req.getHeader().getKindCode())) {
                process0320(ctx, req);
            } else if ("0620".equals(req.getHeader().getKindCode())) {
                process0620(ctx, req);
            }


        }
    }

    private void process0620(ChannelHandlerContext ctx, FepRequest req) {

        FepMessage620 req0620 = (FepMessage620) req;

        int blockNo = req0620.getBlockNo();
        int lastSeqNo = req0620.getSequenceNo();

        int missingCnt = receiveFile.getMissingCnt(blockNo, lastSeqNo);
        char[] missingBit = receiveFile.getMissingBit(blockNo, lastSeqNo);

        PacketHeader resHeader = req0620.getHeader().clone();
        resHeader.setKindCode("0300");


        FepMessage300 res0300 = new FepMessage300();
        res0300.setHeader(resHeader);
        res0300.setBlockNo(blockNo);
        res0300.setSequenceNo(lastSeqNo);
        res0300.setMissingCnt(missingCnt);
        res0300.setMissingBit(missingBit);

        resHeader.setLength(res0300.length());

        ctx.writeAndFlush(res0300);

    }

    private void process0320(ChannelHandlerContext ctx, FepRequest req) {
        FepMessage320310 req0320 = (FepMessage320310) req;

        if (receiveFile != null && receiveFile.isOpen()) {

            try {

                int blockNo = req0320.getBlockNo();
                int sequenceNo = req0320.getSequenceNo();
                int byteLength = req0320.getByteLength();
                ByteBuf fileByte = req0320.getFileByte();

                int readedLength = 0;
                long beforeSize = receiveFile.size();

                receiveFile.setCurrBlockNo(blockNo);
                receiveFile.setCurrSeqNo(sequenceNo);

                int i=0;
                System.out.println(String.format("blockNo: %s, sequenceNo: %s", blockNo, sequenceNo));

                while(fileByte.isReadable(LINE_LENGTH)) {

                    readedLength += LINE_LENGTH;

                    System.out.println(String.format("beforeSize: %s, read count: %s, readedLength:%s", beforeSize, ++i, readedLength));

                    receiveFile.getFileChannel().write(fileByte.readBytes(LINE_LENGTH).writeBytes(LINE_SEPARATOR).nioBuffer());

                }

                System.out.println(String.format("readedLength: %s, byteLength:%s", readedLength, byteLength));

                if (readedLength != byteLength) {

                    receiveFile.getFileChannel().truncate(beforeSize);
                }

                receiveFile.getFileChannel().force(false);

                receiveFile.completedCurrSeqNo();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process0630(ChannelHandlerContext ctx, FepRequest req) {

        FepMessage630640 req0630 = (FepMessage630640) req;

        boolean failPrepareFileReceive = false;
        try {
            prepareFileReceive(req0630);
        } catch (IOException e) {
            e.printStackTrace();
            failPrepareFileReceive = true;
        }

        PacketHeader resHeader = req.getHeader().clone();
        resHeader.setKindCode("0640");

        if (failPrepareFileReceive) {
            resHeader.setResponseCode("090");
        }

        FepMessage630640 res0640 = new FepMessage630640();
        res0640.setHeader(resHeader);
        res0640.setFileName(req0630.getFileName());
        res0640.setFileSize(0);
        res0640.setByteLength(req0630.getByteLength());
        ctx.writeAndFlush(res0640);

    }

    private void prepareFileReceive(FepMessage630640 req0630) throws IOException {


        String fileName = req0630.getFileName();
        String fileCode = StringUtils.substring(fileName, 0, 4);
        String fileMM = StringUtils.substring(fileName, 4, 6);
        String fileYYYY = DateFormatUtils.format(new Date(), "yyyy");

        String saveDirPath = FILE_ROOT + "/" + fileCode + "/" + fileYYYY + "/" + fileYYYY + fileMM;
        File saveDir = new File(saveDirPath);

        if (!saveDir.exists()) {
            System.out.println(String.format("saveDir not exists:%s", saveDir));
            saveDir.mkdirs();
        }

        receiveFile = new ReceiveFile(saveDir, fileName);
    }


    private void process0600(ChannelHandlerContext ctx, FepRequest req) {

        FepMessage600610 req0600 = (FepMessage600610) req;

        if ("001".equals(req0600.getTaskManageInfo())) {
            // 업무개시 요구
            beginProcess = true;


            PacketHeader resHeader = req.getHeader().clone();
            resHeader.setKindCode("0610");

            FepMessage600610 res0610 = new FepMessage600610();
            res0610.setHeader(resHeader);
            res0610.setPacketSendDt(DateFormatUtils.format(new Date(), "MMddHHmmss"));
            res0610.setTaskManageInfo("001");
            ctx.writeAndFlush(res0610);

        } else if ("003".equals(req0600.getTaskManageInfo())) {
            //파일송신완료 지시

            try {
                receiveFile.close();
            } catch (IOException e) {
                e.printStackTrace();

            }


            PacketHeader resHeader = req.getHeader().clone();
            resHeader.setKindCode("0610");

            FepMessage600610 res0610 = new FepMessage600610();
            res0610.setHeader(resHeader);
            res0610.setPacketSendDt(DateFormatUtils.format(new Date(), "MMddHHmmss"));
            res0610.setTaskManageInfo("003");
            ctx.writeAndFlush(res0610);
        } else if ("004".equals(req0600.getTaskManageInfo())) {

            PacketHeader resHeader = req.getHeader().clone();
            resHeader.setKindCode("0610");

            FepMessage600610 res0610 = new FepMessage600610();
            res0610.setHeader(resHeader);
            res0610.setPacketSendDt(DateFormatUtils.format(new Date(), "MMddHHmmss"));
            res0610.setTaskManageInfo("004");
            ctx.writeAndFlush(res0610);


        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
