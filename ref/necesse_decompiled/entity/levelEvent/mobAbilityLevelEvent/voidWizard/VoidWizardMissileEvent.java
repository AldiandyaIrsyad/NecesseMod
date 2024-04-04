package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.projectile.VoidWizardMissileProjectile;

public class VoidWizardMissileEvent extends MobAbilityLevelEvent {
   private int tickCounter;
   private int targetID;
   private VoidWizard wizard;
   private Mob target;

   public VoidWizardMissileEvent() {
   }

   public VoidWizardMissileEvent(Mob var1, Mob var2) {
      super(var1, GameRandom.globalRandom);
      this.target = var2;
      if (var2 != null) {
         this.targetID = var2.getUniqueID();
      } else {
         this.targetID = -1;
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.targetID);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.targetID = var1.getNextInt();
   }

   public void init() {
      super.init();
      if (this.owner != null) {
         this.tickCounter = 0;
         this.target = GameUtils.getLevelMob(this.targetID, this.level);
         if (this.target == null) {
            GameLog.warn.println("Could not find target for dungeon wizard attack missile event, server level: " + this.isServer());
            this.over();
         }

         if (this.owner instanceof VoidWizard) {
            this.wizard = (VoidWizard)this.owner;
            this.wizard.swingAttack = true;
         }

      }
   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.owner != null && !this.owner.removed() && this.tickCounter <= 60) {
         if (this.tickCounter % 6 == 0 && this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
         }

      } else {
         this.over();
      }
   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.owner != null && !this.owner.removed() && this.target.isSamePlace(this.owner) && this.tickCounter <= 60) {
         if (this.tickCounter % 6 == 0) {
            if (this.wizard != null) {
               this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
               this.wizard.playBoltSoundAbility.runAndSend(1.0F, 1.3F);
            }

            VoidWizardMissileProjectile var1 = new VoidWizardMissileProjectile(this.level, this.owner, this.target, VoidWizard.missile);
            this.level.entityManager.projectiles.add(var1);
         }

      } else {
         this.over();
      }
   }
}
