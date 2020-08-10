package com.thinkerwolf.gamer.grizzly.tcp;

import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PacketEncoder extends AbstractTransformer<Packet, Buffer> {

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
