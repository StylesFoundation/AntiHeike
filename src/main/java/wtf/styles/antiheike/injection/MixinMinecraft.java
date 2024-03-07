package wtf.styles.antiheike.injection;


import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.styles.antiheike.AntiHeike;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "getInstance", at = @At("HEAD"))
    private static void getInstance(CallbackInfoReturnable<Minecraft> cir){
        AntiHeike.INSTANCE.traceInvoke();
    }
}
