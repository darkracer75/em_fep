package com.em.fep.server;

import com.em.fep.exception.FepFileException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReceiveFile {

    public static final int MAX_BLOCK_SIZE = 9999;
    public static final int MAX_SEQ_SIZE = 999;

    private int seqsPerBlock;

    private int currBlockNo;
    private int currSeqNo;

    private File saveDir;
    private String fileName;

    private File tempFile;
    private FileChannel fileChannel;

    private Map<Integer, char[]> receiveBlock = new HashMap<>();


    public ReceiveFile(File saveDir, String fileName) throws IOException {

        this.fileName = fileName;
        this.saveDir = saveDir;

        tempFile = File.createTempFile(fileName + "_", null, saveDir);

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        fileChannel = outputStream.getChannel();
    }

    public boolean isOpen() {
        return fileChannel != null && fileChannel.isOpen();
    }

    public long size() {
        long size = 0L;
        try {
            if (fileChannel != null) {
                size = fileChannel.size();
            } else if (tempFile != null){
                size = tempFile.length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;

    }

    public FileChannel getFileChannel() {
        return fileChannel;
    }

    public void setCurrBlockNo(int blockNo) {

        if (blockNo < 1) {
            throw new FepFileException("�ּ� ��� ��ȣ �̸�!");
        }

        if (blockNo > MAX_BLOCK_SIZE) {
            throw new FepFileException("�ִ� ��� ��ȣ �ʰ�!");
        }

        if (this.currBlockNo > blockNo) {
            throw new FepFileException("���� ��� ��ȣ ���� ������!");
        }

        if (this.currBlockNo != blockNo) {
            this.currBlockNo = blockNo;
            // ������ ��ϴ� ������ ������ ���� ������ ���� �ƴϸ� �ִ� ������ ������ ���
            char[] seqs = new char[seqsPerBlock > 0 ? seqsPerBlock : MAX_SEQ_SIZE];
            Arrays.fill(seqs, '0');
            receiveBlock.put(blockNo, seqs);
        }


    }

    public void setCurrSeqNo(int seqNo) {

        if (seqNo < 1) {
            throw new FepFileException("�ּ� ������ ��ȣ �̸�!");
        }

        if (seqNo > MAX_SEQ_SIZE) {
            throw new FepFileException("�ִ� ������ ��ȣ �ʰ�!");
        }

        if (seqsPerBlock > 0 && seqNo > seqsPerBlock) {
            throw new FepFileException("���� �ִ� ������ ��ȣ �ʰ�!");
        }

        if (this.currSeqNo > seqNo) {
            throw new FepFileException("���� ������ ��ȣ ���� ������!");
        }

        if (this.currSeqNo == seqNo) {
            throw new FepFileException("������ ������ �ߺ�!!");
        }

        this.currSeqNo = seqNo;
    }

    public void completedCurrSeqNo() {
        receiveBlock.get(this.currBlockNo)[this.currSeqNo-1] = '1';
    }

    public int getMissingCnt(int blockNo, int lastSeqNo) {

        int missingCnt = 0;

        char[] seqs = receiveBlock.get(blockNo);

        if (seqs != null) {

            for (int i=0; i < lastSeqNo; i++) {

                if (seqs[i] == '0') {
                    missingCnt++;
                }
            }

        }

        return missingCnt;
    }

    public char[] getMissingBit(int blockNo, int lastSeqNo) {

        char[] seqs = receiveBlock.get(blockNo);

        if (seqs != null) {

            return Arrays.copyOfRange(seqs, 0, lastSeqNo);
        }

        return new char[0];

    }

    public void close() throws IOException {

        if (isOpen()) {

            fileChannel.close();
            tempFile.renameTo(new File(saveDir.getAbsolutePath(), fileName));

        }
    }
}
