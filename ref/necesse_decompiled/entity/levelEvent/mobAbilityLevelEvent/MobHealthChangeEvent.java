package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.hudManager.floatText.DamageText;

public class MobHealthChangeEvent extends MobAbilityLevelEvent {
   private int finalHealth;
   private int change;

   public MobHealthChangeEvent() {
   }

   public MobHealthChangeEvent(Mob var1, int var2, int var3) {
      super(var1, GameRandom.globalRandom);
      if (var3 == 0) {
         throw new IllegalArgumentException("Cannot send a health change event with 0 change.");
      } else {
         this.finalHealth = var2;
         this.change = var3;
      }
   }

   public MobHealthChangeEvent(Mob var1, int var2) {
      this(var1, Math.max(0, var1.getHealth() + var2), var2);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.finalHealth = var1.getNextInt();
      this.change = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.finalHealth);
      var1.putNextInt(this.change);
   }

   public void init() {
      super.init();
      if (this.owner != null) {
         this.owner.setHealthHidden(this.finalHealth);
         if (this.isClient()) {
            this.level.hudManager.addElement(new DamageText(this.owner, this.change, this.change > 0 ? Color.GREEN : Color.RED, GameRandom.globalRandom.getIntBetween(30, 40)));
         }
      }

   }
}
