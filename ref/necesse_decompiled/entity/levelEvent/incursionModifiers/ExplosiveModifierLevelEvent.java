package necesse.entity.levelEvent.incursionModifiers;

import java.util.HashSet;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierChargeUpLevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierExplosionLevelEvent;
import necesse.entity.manager.MobDeathListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class ExplosiveModifierLevelEvent extends LevelEvent implements MobDeathListenerEntityComponent {
   public ExplosiveModifierLevelEvent() {
      super(true);
      this.shouldSave = true;
   }

   public void onLevelMobDied(final Mob var1, Attacker var2, HashSet<Attacker> var3) {
      if (!var1.isPlayer && var1.isHostile && !var1.isBoss() && var1.shouldSendSpawnPacket()) {
         ExplosiveModifierChargeUpLevelEvent var4 = new ExplosiveModifierChargeUpLevelEvent(var1.getX(), var1.getY(), 1500.0F);
         this.getLevel().entityManager.addLevelEvent(var4);
         Screen.playSound(GameResources.fireworkFuse, SoundEffect.effect(var1).volume(1.0F));
         this.level.entityManager.addLevelEventHidden(new WaitForSecondsEvent(1.5F) {
            public void onWaitOver() {
               GameDamage var1x = new GameDamage(100.0F * (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE));
               ExplosiveModifierExplosionLevelEvent var2 = new ExplosiveModifierExplosionLevelEvent(var1.x, var1.y, 75, var1x, false, 0, var1);
               this.getLevel().entityManager.addLevelEvent(var2);
            }
         });
      }

   }
}
