package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.GameDamage;
import necesse.gfx.GameResources;

public class TNTExplosionEvent extends ExplosionEvent {
   public TNTExplosionEvent() {
      this(0.0F, 0.0F);
   }

   public TNTExplosionEvent(float var1, float var2) {
      super(var1 * 32.0F + 16.0F, var2 * 32.0F + 16.0F, 256, new GameDamage(400.0F, 1000.0F), true, 10);
      this.sendCustomData = false;
   }

   public void serverTick() {
      this.level.sendObjectChangePacket(this.level.getServer(), this.tileX, this.tileY, 0);
      super.serverTick();
   }

   public GameMessage getAttackerName() {
      return new LocalMessage("deaths", "tntname");
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(3.5F));
      this.level.getClient().startCameraShake(this.x, this.y, 500, 40, 5.0F, 5.0F, true);
   }
}
