package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class BombExplosionEvent extends ExplosionEvent implements Attacker {
   public BombExplosionEvent() {
      this(0.0F, 0.0F, 40, new GameDamage(80.0F), true, 0, (Mob)null);
   }

   public BombExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, int var6, Mob var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public BombExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, boolean var6, int var7, Mob var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected GameDamage getTotalObjectDamage(float var1) {
      return super.getTotalObjectDamage(var1).modDamage(10.0F);
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.5F).pitch(1.5F));
      this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0F, 3.0F, true);
   }
}
