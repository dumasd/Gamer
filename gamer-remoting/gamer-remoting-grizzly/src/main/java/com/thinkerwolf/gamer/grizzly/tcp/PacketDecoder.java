package com.thinkerwolf.gamer.grizzly.tcp;

import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PacketDecoder extends AbstractTransformer<Buffer, Packet> {

    @Override
    protected TransformationResult<Buffer, Packet> transformImpl(AttributeStorage storage, Buffer input) throws TransformationException {
        if (input.remaining() < 16) {
            return TransformationResult.createIncompletedResult(input);
        }
        Packet packet = new Packet();
        int commandLen = input.getInt(8);
        int contentLen = input.getInt(12);

        // 256B
        int MAX_COMMAND_LEN = 1024 / 4;
        if (commandLen < 0 || commandLen > MAX_COMMAND_LEN) { // 512字节
            // 非法请求
            return TransformationResult.createErrorResult(0, "Illegal command len " + commandLen);
        }
        // 500K
        int MAX_CONTENT_LEN = 500 * 1024;
        if (contentLen < 0 || contentLen > MAX_CONTENT_LEN) {
            return TransformationResult.createErrorResult(0, "Illegal content len " + contentLen);
        }

        if (input.remaining() < commandLen + contentLen) {
            input.flip();
            return TransformationResult.createIncompletedResult(input);
        }

        int opcode = input.getInt();
        int requestId = input.getInt();
        commandLen = input.getInt();
        contentLen = input.getInt();

        byte[] commandBytes = new byte[commandLen];
        input.get(commandBytes);
        String command = new String(commandBytes, UTF_8);

        byte[] content = new byte[contentLen];
        input.get(content);

        packet.setOpcode(opcode);
        packet.setRequestId(requestId);
        packet.setCommand(command);
        packet.setContent(content);
        return TransformationResult.createCompletedResult(packet, input);
    }

    @Override
    public String getName() {
        return "packetDecoder";
    }

    @Override
    public boolean hasInputRemaining(AttributeStorage storage, Buffer input) {
        return input != null && input.hasRemaining();
    }
}