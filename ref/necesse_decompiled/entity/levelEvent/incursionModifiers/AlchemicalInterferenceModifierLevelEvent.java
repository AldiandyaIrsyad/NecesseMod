package necesse.entity.levelEvent.incursionModifiers;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class AlchemicalInterferenceModifierLevelEvent extends LevelEvent implements MobBuffsEntityComponent {
   public AlchemicalInterferenceModifierLevelEvent() {
      super(true);
      this.shouldSave = true;
   }

   public void serverTick() {
      super.serverTick();
      if (this.level.isServer()) {
         this.level.getServer().streamClients().filter((var1) -> {
            return !var1.isSamePlace(this.level) && var1.hasSpawned();
         }).forEach((var0) -> {
            if (var0.playerMob.buffManager.hasBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE)) {
               var0.playerMob.buffManager.removeBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE, true);
            }

         });
         this.level.getServer().streamClients().filter((var1) -> {
            return var1.isSamePlace(this.level) && var1.hasSpawned();
         }).forEach((var0) -> {
            if (!var0.playerMob.buffManager.hasBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE)) {
               var0.playerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE, var0.playerMob, 99999, (Attacker)null), true);
            }

         });
      }

   }

   public boolean isNetworkImportant() {
      return true;
   }

   public Stream<ModifierValue<?>> getLevelModifiers(Mob var1) {
      return var1.isPlayer ? Stream.of(new ModifierValue(BuffModifiers.POTION_DURATION, 0.5F)) : Stream.empty();
   }

   public void over() {
      super.over();
      if (this.level.isServer()) {
         this.level.getServer().streamClients().filter((var1) -> {
            return var1.isSamePlace(this.level) && var1.hasSpawned();
         }).forEach((var0) -> {
            if (var0.playerMob.buffManager.hasBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE)) {
               var0.playerMob.buffManager.removeBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE, true);
            }

         });
      }

   }
}
