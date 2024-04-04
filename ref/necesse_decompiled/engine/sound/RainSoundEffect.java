package necesse.engine.sound;

import java.util.function.Supplier;
import org.lwjgl.openal.AL10;

public class RainSoundEffect extends GlobalSoundEffect {
   private float dx;
   private float dy;
   private float distance;
   private float intensity;

   public RainSoundEffect(Supplier<Float> var1, float var2, float var3, float var4, float var5) {
      super(var1);
      this.dx = var2;
      this.dy = var3;
      this.distance = var4;
      this.intensity = var5;
   }

   public void updateSound(SoundPlayer var1, int var2, float var3, SoundEmitter var4) {
      AL10.alSourcef(var2, 4099, this.pitch);
      float var5 = (Float)this.settingVolumeMod.get() * this.volume * this.intensity * 0.5F * var3;
      float var6 = PositionSoundEffect.getGain(0.02F, this.distance, 20, 200);
      AL10.alSourcef(var2, 4106, var5 * var6);
   }
}
