package pw.kaboom.papermixins.mixin.execute_vanilla_only;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.util.BrigadierConstants;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    // This is a bit fragile as we depend on Paper utilizing ArrayList<>() only here, but it's fine for now.
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Ljava/util/ArrayList;<init>(Ljava/util/Collection;)V", unsafe = true))
    private void init$arrayList(final Commands.CommandSelection selection, final CommandBuildContext context,
                                final CallbackInfo ci) {
        for (final CommandNode<CommandSourceStack> node : this.dispatcher.getRoot().getChildren()) {
            BrigadierConstants.VANILLA_DISPATCHER.getRoot().addChild(node);
        }
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE",
            target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V"))
    private void init$setConsumer(final CommandDispatcher<CommandSourceStack> instance,
                                  final ResultConsumer<CommandSourceStack> consumer, final Operation<Void> original) {
        // Use same consumer for our vanilla dispatcher
        BrigadierConstants.VANILLA_DISPATCHER.setConsumer(consumer);

        original.call(instance, consumer);
    }
}
