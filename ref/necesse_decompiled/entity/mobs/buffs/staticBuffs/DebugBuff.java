package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DebugBuff extends Buff {
   public DebugBuff() {
   }

   public void clientTick(ActiveBuff var1) {
      Mob var2 = var1.owner;
      if ((var2.dx != 0.0F || var2.dy != 0.0F) && var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(214, 127, 255)).height(16.0F);
      }

   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_SUMMONS, 50);
      var1.setModifier(BuffModifiers.DASH_STACKS, 100);
      var1.setModifier(BuffModifiers.DASH_COOLDOWN, -10.0F);
      var1.setModifier(BuffModifiers.STAMINA_CAPACITY, 10.0F);
      var1.setModifier(BuffModifiers.STAMINA_REGEN, 10.0F);
      var1.setModifier(BuffModifiers.TRAVEL_DISTANCE, 10);
      var1.setModifier(BuffModifiers.BIOME_VIEW_DISTANCE, 10);
   }

   public int getStackSize() {
      return 1;
   }

   public boolean overridesStackDuration() {
      return false;
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add("Used for testing purposes");
      return var3;
   }

   public void updateLocalDisplayName() {
      this.displayName = new StaticMessage("Debug buff");
   }
}
