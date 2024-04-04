package necesse.engine.sound;

import java.util.function.Supplier;
import org.lwjgl.openal.AL10;

public class GlobalSoundEffect extends SoundEffect {
   protected Supplier<Float> settingVolumeMod;
   protected float pitch = 1.0F;

   GlobalSoundEffect(Supplier<Float> var1) {
      this.settingVolumeMod = var1;
   }

   public GlobalSoundEffect pitch(float var1) {
      this.pitch = var1;
      return this;
   }

   public GlobalSoundEffect volume(float var1) {
      this.volume = var1;
      return this;
   }

   public void updateSound(SoundPlayer var1, int var2, float var3, SoundEmitter var4) {
      float var5 = (Float)this.settingVolumeMod.get() * this.volume * var3;
      AL10.alSourcef(var2, 4099, this.pitch);
      AL10.alSourcef(var2, 4106, var5);
      AL10.alSourcefv(var2, 4100, getSoundPosition(new float[]{0.0F, 0.0F, 0.0F}));
   }

   public PrimitiveSoundEmitter getEmitter() {
      return null;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public SoundEffect volume(float var1) {
      return this.volume(var1);
   }
}
