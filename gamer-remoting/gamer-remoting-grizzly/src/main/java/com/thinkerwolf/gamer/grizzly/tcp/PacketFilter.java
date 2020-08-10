package com.thinkerwolf.gamer.grizzly.tcp;

import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.*;
import org.glassfish.grizzly.attributes.AttributeStorage;
import org.glassfish.grizzly.filterchain.AbstractCodecFilter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PacketFilter extends AbstractCodecFilter<Buffer, Packet> {

    public PacketFilter() {
        super(new PacketDecoder(), new PacketEncoder());
    }


    static class PacketDecoder extends AbstractTransformer<Buffer, Packet> {

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

    static class PacketEncoder extends AbstractTransformer<Packet, Buffer> {

        @Override
        protected TransformationResult<Packet, Buffer> transformImpl(AttributeStorage storage, Packet input) throws TransformationException {
            byte[] commandBytes = input.getCommand().getBytes(UTF_8);
            int size = 16 + commandBytes.length + input.getContent().length;

            final Buffer output = obtainMemoryManager(storage).allocate(size);

            output.putInt(input.getOpcode());
            output.putInt(input.getRequestId());
            int contentLen = input.getContent() == null ? 0 : input.getContent().length;
            output.putInt(commandBytes.length);
            output.putInt(contentLen);
            output.put(commandBytes);
            if (input.getContent() != null) {
                output.put(input.getContent());
            }
            output.flip();
            return TransformationResult.createCompletedResult(output, input);
        }

        @Override
        public String getName() {
            return "packetEncoder";
        }

        @Override
        public boolean hasInputRemaining(AttributeStorage storage, Packet input) {
            return false;
        }
    }

}
