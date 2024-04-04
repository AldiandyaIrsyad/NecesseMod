package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class CannonBallExplosionEvent extends ExplosionEvent implements Attacker {
   public CannonBallExplosionEvent() {
      this(0.0F, 0.0F, new GameDamage(100.0F), (Mob)null);
   }

   public CannonBallExplosionEvent(float var1, float var2, GameDamage var3, Mob var4) {
      super(var1, var2, 80, var3, false, 0, var4);
      this.sendCustomData = false;
      this.sendOwnerData = true;
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.0F).pitch(1.3F));
      this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0F, 3.0F, true);
   }
}
