package intrusionSimulation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

/**
 * SE-Star binary messages for interoperability through TCP Socket.
 *
 * @author Alexandre Kazmierowski
 */
public class SeStarMessage {
    /**
     * Enumeration of messages types for SE-Star proprietary Network API
     */
    public enum MessageType {
        UNKNOWN, // 0
        SIMULATION_PAUSE, // 10
        SIMULATION_PLAY, // 11
        SIMULATION_QUIT, // 12
        SET_TIMER_PROPERTIES, // 19
        ADVANCE_STEPS, // 24
        ENTITY_GOTO, // 106
        DELETE_ALL_ENTITIES, // 120
        SMARTOBJECT_FROM_NAME_CHANGE_VAR, // 205
        NOTIFY_CREATE_SYNTHETIC_ENTITY, // 309
        NOTIFY_ADVANCE_STEPS, // 316
        DATA_SIMULATION_TIME, // 408,
        GET_SIMULATION_TIME, // 409,
        DATA_GROUND_TRUTH_LITE, // 446
        GET_GROUND_TRUTH_LITE, // 447
    }

    // Map between message types and the corresponding value sent in the binary
    // network message.
    private static final EnumMap<MessageType, Short> MessageTypeValues;
    static {
        MessageTypeValues = new EnumMap<MessageType, Short>(
                MessageType.class
        );
        MessageTypeValues.put(MessageType.UNKNOWN, (short) 0);
        MessageTypeValues.put(MessageType.SIMULATION_PAUSE, (short) 10);
        MessageTypeValues.put(MessageType.SIMULATION_PLAY, (short) 11);
        MessageTypeValues.put(MessageType.SIMULATION_QUIT, (short) 12);
        MessageTypeValues.put(MessageType.SET_TIMER_PROPERTIES, (short) 19);
        MessageTypeValues.put(MessageType.ADVANCE_STEPS, (short) 24);
        MessageTypeValues.put(MessageType.ENTITY_GOTO, (short) 106);
        MessageTypeValues.put(MessageType.DELETE_ALL_ENTITIES, (short) 120);
        MessageTypeValues.put(MessageType.SMARTOBJECT_FROM_NAME_CHANGE_VAR, (short) 205);
        MessageTypeValues.put(MessageType.NOTIFY_CREATE_SYNTHETIC_ENTITY, (short) 309);
        MessageTypeValues.put(MessageType.NOTIFY_ADVANCE_STEPS, (short) 316);
        MessageTypeValues.put(MessageType.DATA_SIMULATION_TIME, (short) 408);
        MessageTypeValues.put(MessageType.GET_SIMULATION_TIME, (short) 409);
        MessageTypeValues.put(MessageType.DATA_GROUND_TRUTH_LITE, (short) 446);
        MessageTypeValues.put(MessageType.GET_GROUND_TRUTH_LITE, (short) 447);
    }

    /**
     * Find a message type from the enum from its 'short' value received
     * through the network.
     *
     * @param value type identifier received from the network, as a short value.
     * @return associated message type.
     */
    private static MessageType getMessageTypeByValue(short value) {
        for (Map.Entry<MessageType, Short> entry: MessageTypeValues.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return MessageType.UNKNOWN;
    }

    /**
     * Type of this message.
     */
    public MessageType type;
    /**
     * ByteBuffer of this message's binary data.
     */
    public ByteBuffer body = null;

    /**
     * Private constructor.
     *
     * @param messageType type of the message.
     */
    private SeStarMessage(MessageType messageType) {
        this.type = messageType;
    }

    /**
     * Add a String to this message's body.
     *
     * @param s String to add.
     */
    void putString(String s) {
        this.body.putInt(s.length());
        this.body.put(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Read a string from this message's body.
     *
     * @return String read.
     */
    String getString() {
        int length = this.body.getInt();
        byte[] bytes = new byte[length];
        this.body.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Add a boolean to this message's body.
     * Byte is used as underlying type.
     *
     * @param b boolean to add.
     */
    void putBool(boolean b) {
        byte asByte = b ? (byte) 1 : (byte) 0;
        this.body.put(asByte);
    }

    /**
     * Read a boolean from this message's body.
     * Byte is used as underlying type.
     *
     * @return boolean read.
     */
    boolean getBool() {
        byte asByte = this.body.get();
        return asByte != 0;
    }

    /**
     * Create an outgoing message of the given type.
     *
     * As the ByteBuffer does not manage dynamic sizing, we use a capacity of
     * 1000 bytes which is enough for our usages.
     *
     * @param messageType type of the message.
     * @return the message created.
     */
    static SeStarMessage outgoingMessage(MessageType messageType) {
        SeStarMessage message = new SeStarMessage(messageType);
        message.body = ByteBuffer.allocate(1000);
        message.body.order(ByteOrder.LITTLE_ENDIAN);
        return message;
    }

    /**
     * Send this (outgoing) message through the socket channel.
     *
     * @param channel socket channel to SE-Star.
     * @return number of bytes written to the channel.
     */
    int toChannel(SocketChannel channel) throws IOException {
        int bodySize = this.body.position();
        int messageHeader = 2 + bodySize;
        // SE-Star uses unsigned int to represent the message size
        // This does not exist in core Java, that is why we check that there
        // is no signed to unsigned cast inconsistency.
        if ((long) messageHeader != Integer.toUnsignedLong(messageHeader)) {
            throw new IOException("Unhandled unsigned integer message size");
        }
        ByteBuffer outgoingBytes = ByteBuffer.allocate(4 + 2 + bodySize);
        outgoingBytes.order(ByteOrder.LITTLE_ENDIAN);
        outgoingBytes.putInt(messageHeader);
        outgoingBytes.putShort(MessageTypeValues.get(this.type));
        outgoingBytes.put(this.body.array(), 0, bodySize);
        outgoingBytes.flip();
        return channel.write(outgoingBytes);
    }

    /**
     * Create an incoming message received through the channel.
     *
     * @param channel socket channel from SE-Star.
     * @return incoming message parsed from the bytes received.
     */
    static SeStarMessage fromChannel(SocketChannel channel) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(4);
        header.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(header);
        // SE-Star uses unsigned int to represent the message size
        // This does not exist in core Java, that is why we check that there
        // is no signed to unsigned cast inconsistency.
        int bodySize = header.getInt(0);
        if ((long) bodySize != Integer.toUnsignedLong(bodySize)) {
            throw new IOException("Unhandled unsigned integer message size");
        }
        ByteBuffer fullBody = ByteBuffer.allocate(bodySize);
        fullBody.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(fullBody);
        MessageType type = getMessageTypeByValue(fullBody.getShort(0));
        SeStarMessage incomingMessage = new SeStarMessage(type);
        incomingMessage.body = fullBody.slice(2, bodySize - 2);
        incomingMessage.body.order(ByteOrder.LITTLE_ENDIAN);
        return incomingMessage;
    }



}
