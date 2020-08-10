package com.thinkerwolf.gamer.grizzly;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.HeapMemoryManager;
import org.junit.Test;

public class BufferTests {
    @Test
    public void commonTests() {
        HeapMemoryManager manager = new HeapMemoryManager();
        Buffer buffer = manager.allocate(12);
        buffer.putInt(10);
        buffer.putInt(20);
        buffer.putInt(30);

        System.out.println("Remaining -> " + buffer.remaining());
        buffer.flip();
        System.out.println("Remaining -> " + buffer.remaining());

        System.out.println("Get -> " + buffer.getInt(4));
        System.out.println("Get -> " + buffer.getInt());
        System.out.println("Remaining -> " + buffer.remaining());

    }

}
