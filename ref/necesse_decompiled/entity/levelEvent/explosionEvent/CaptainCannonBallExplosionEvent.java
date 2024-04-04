package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class CaptainCannonBallExplosionEvent extends ExplosionEvent implements Attacker {
   public CaptainCannonBallExplosionEvent() {
      this(0.0F, 0.0F, new GameDamage(10.0F), (Mob)null);
   }

   public CaptainCannonBallExplosionEvent(float var1, float var2, GameDamage var3, Mob var4) {
      super(var1, var2, 100, var3, false, 0, var4);
      this.hitsOwner = false;
      this.sendCustomData = false;
      this.sendOwnerData = true;
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(1.5F).pitch(1.2F));
      this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0F, 3.0F, true);
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("piratecap", 4);
   }
}
