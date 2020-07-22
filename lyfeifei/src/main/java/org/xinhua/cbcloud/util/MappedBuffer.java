package org.xinhua.cbcloud.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MappedBuffer {

   /* public static void main(String[] args) {
        try {
            mapped();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private static final int start = 0;

    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("D:/mapper.txt", "rw");
        FileChannel fileChannel = raf.getChannel();

        String a = "我要写入文件";
        MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE, start, a.getBytes().length+1);
        mbb.put(a.getBytes());

        mbb.flip();

        byte[] bb = new byte[mbb.capacity()];
        while (mbb.hasRemaining()){
            byte b = mbb.get();
            bb[mbb.position()]=b;
        }
        System.out.println(new String(bb));
        raf.close();
    }

    public static void mapped() throws Exception {
        long start = System.currentTimeMillis();

        FileChannel inChannel = FileChannel.open(Paths.get("D:/123.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:/234.mp4"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();

        long end = System.currentTimeMillis();
        System.out.println("耗费时间为：" + (end - start));
    }
}
