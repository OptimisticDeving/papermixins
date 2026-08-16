package pw.kaboom.papermixins.pluginmixin.mixins.fix.via_kicks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.exception.CancelException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

@PluginMixin("ViaVersion")
@Mixin(UserConnectionImpl.class)
public abstract class UserConnectionImplMixin {
    @WrapOperation(
        method = "transform",
        at = @At(value = "INVOKE",
            target = "Lcom/viaversion/viaversion/api/protocol/ProtocolPipeline;transform" +
                "(Lcom/viaversion/viaversion/api/protocol/packet/Direction;Lcom/viaversion/viaversion/api/protocol/packet/State;" +
                "Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;)V"))
    private void transform$transform(final ProtocolPipeline instance,
                                     final Direction direction,
                                     final State state,
                                     final PacketWrapper packetWrapper,
                                     final Operation<Void> original) throws Exception {
        try {
            original.call(instance, direction, state, packetWrapper);
        } catch (Exception e) {
            throw e instanceof CancelException && direction == Direction.CLIENTBOUND
                ? e
                : new CancelException(e);
        }
    }
}
