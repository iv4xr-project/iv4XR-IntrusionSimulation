package intrusionSimulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class BinaryReadWriteTest {

    /**
     * Sandbox test to read/write with ByteBuffer.
     */
    @Test
    public void readWriteTest() {
        System.out.println("Read all string");
        String text = "toto";
        System.out.println(text.getBytes().length);
        ByteArrayInputStream textInputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1));

        byte[] allBytes = new byte[4];
        int bytesRead = textInputStream.read(allBytes, 0, 4);

        Assertions.assertEquals(bytesRead, 4);

        String s = new String(allBytes, StandardCharsets.UTF_8);
        System.out.println(s);

        ByteBuffer bb = ByteBuffer.wrap(allBytes);

        // JVM uses BIG_ENDIAN by default, whereas Windows and Linux on x86_64 use LITTLE_ENDIAN
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // We can use the get() relative methods (that advance the read position at each usage)
        // Or the get(index i) absolute method to check one particular position, without modifying
        // the current relative read position
        // Same logic with the put methods
        System.out.println(bb.get(0) + ", " + bb.get(1));
        System.out.println(bb.getShort());
        System.out.println(bb.getInt(0));
        System.out.println(bb.getShort());
        // For unsigned integer types, we need to parse them as signed and use what's in Java to treat them
        // such as Integer.compareUnsigned(), etc.

        // Dynamic size test
        ByteBuffer newBb = ByteBuffer.allocate(10);
        for (Byte b : bb.array()) {
            newBb.put(b);
        }
        int size = newBb.position();
        System.out.println(size);
        byte[] effectiveBytes = new byte[size];
        newBb.get(0, effectiveBytes, 0, size);
        System.out.println(new String(effectiveBytes, StandardCharsets.UTF_8));
    }
}
