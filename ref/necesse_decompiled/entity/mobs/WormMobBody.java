package necesse.entity.mobs;

import java.awt.Rectangle;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameLinkedList;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.CollisionFilter;

public class WormMobBody<T extends WormMobHead<B, T>, B extends WormMobBody<T, B>> extends Mob {
   public int removeTicker;
   public final LevelMob<T> master = new LevelMob();
   public GameLinkedList<WormMoveLine>.Element moveLine;
   public float moveLineExtraDist;
   public float height = 0.0F;
   public B next;
   public boolean canHit = true;
   public boolean sharesHitCooldownWithNext = false;
   public boolean relayHitToNext = false;

   public WormMobBody(int var1) {
      super(var1);
      this.isSummoned = true;
      this.dropsLoot = false;
      this.setKnockbackModifier(0.0F);
      this.setRegen(0.0F);
   }

   public boolean shouldSendSpawnPacket() {
      return false;
   }

   public Mob getSpawnPacketMaster() {
      return this.master.get(this.getLevel());
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.master.uniqueID = var1.getNextInt();
      this.canHit = var1.getNextBoolean();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.master.uniqueID);
      var1.putNextBoolean(this.canHit);
   }

   public void init() {
      super.init();
      this.countStats = false;
   }

   public void updateBodyPartPosition(T var1, float var2, float var3) {
      this.setPos(var2, var3, true);
   }

   public void clientTick() {
      super.clientTick();
      this.tickMaster();
   }

   public void serverTick() {
      super.serverTick();
      this.movementUpdateTime = this.getWorldEntity().getTime();
      this.healthUpdateTime = this.getWorldEntity().getTime();
      this.tickMaster();
   }

   public void tickMovement(float var1) {
      if (!this.removed()) {
         this.checkCollision();
      }
   }

   public void requestServerUpdate() {
   }

   public void sendMovementPacket(boolean var1) {
   }

   public void tickMaster() {
      if (!this.removed()) {
         this.master.computeIfPresent(this.getLevel(), (var1) -> {
            this.setMaxHealth(var1.getMaxHealth());
            this.setHealthHidden(var1.getHealth(), 0.0F, 0.0F, (Attacker)null);
            this.setArmor(var1.getArmorFlat());
         });
         ++this.removeTicker;
         if (this.removeTicker > 20) {
            this.remove();
         }

      }
   }

   protected void playDeathSound() {
   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public boolean canCollisionHit(Mob var1) {
      return this.isVisible() && super.canCollisionHit(var1);
   }

   public boolean isVisible() {
      return this.moveLine != null && !((WormMoveLine)this.moveLine.object).isUnderground;
   }

   public void startHitCooldown() {
      super.startHitCooldown();
      if (this.master != null) {
         this.master.computeIfPresent(this.getLevel(), Mob::startHitCooldown);
      }

   }

   public int getHitCooldownUniqueID() {
      if (this.sharesHitCooldownWithNext) {
         if (this.next != null) {
            return this.next.getHitCooldownUniqueID();
         } else {
            WormMobHead var1 = (WormMobHead)this.master.get(this.getLevel());
            return var1 != null ? var1.getHitCooldownUniqueID() : this.master.uniqueID;
         }
      } else {
         return super.getHitCooldownUniqueID();
      }
   }

   public boolean canBeHit(Attacker var1) {
      return this.isVisible() && this.canHit ? super.canBeHit(var1) : false;
   }

   public boolean shouldSave() {
      return false;
   }

   public int getHealth() {
      if (this.master != null) {
         WormMobHead var1 = (WormMobHead)this.master.get(this.getLevel());
         if (var1 != null) {
            return var1.getHealth();
         }
      }

      return super.getHealth();
   }

   public int getMaxHealth() {
      if (this.master != null) {
         WormMobHead var1 = (WormMobHead)this.master.get(this.getLevel());
         if (var1 != null) {
            return var1.getMaxHealth();
         }
      }

      return super.getMaxHealth();
   }

   public MobWasHitEvent isHit(MobWasHitEvent var1, Attacker var2) {
      if (this.relayHitToNext) {
         if (this.next != null) {
            return this.next.isHit(var1, var2);
         } else {
            WormMobHead var3 = (WormMobHead)this.master.get(this.getLevel());
            return var3 != null ? var3.isHit(var1, var2) : super.isHit(var1, var2);
         }
      } else {
         return super.isHit(var1, var2);
      }
   }

   public MobWasHitEvent isServerHit(GameDamage var1, float var2, float var3, float var4, Attacker var5) {
      if (this.relayHitToNext) {
         if (this.next != null) {
            return this.next.isServerHit(var1, var2, var3, var4, var5);
         } else {
            WormMobHead var6 = (WormMobHead)this.master.get(this.getLevel());
            return var6 != null ? var6.isServerHit(var1, var2, var3, var4, var5) : super.isServerHit(var1, var2, var3, var4, var5);
         }
      } else {
         return super.isServerHit(var1, var2, var3, var4, var5);
      }
   }

   public void setHealthHidden(int var1, float var2, float var3, Attacker var4, boolean var5) {
      if (this.master != null) {
         this.master.computeIfPresent(this.getLevel(), (var5x) -> {
            var5x.setHealthHidden(var1, var2, var3, var4, var5);
         });
      }

      super.setHealthHidden(var1, var2, var3, var4, var5);
   }

   public Rectangle getSelectBox(int var1, int var2) {
      Rectangle var3 = super.getSelectBox(var1, var2);
      var3.y = (int)((float)var3.y - this.height);
      if (this.height < 0.0F) {
         var3.height = (int)((float)var3.height + this.height);
      }

      return var3;
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      boolean var5 = this.removed();
      super.remove(var1, var2, var3, var4);
      if (!var5 && this.master != null) {
         this.master.computeIfPresent(this.getLevel(), (var3x) -> {
            var3x.remove(var1, var2, var3);
         });
      }

   }

   protected void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("Height: " + this.height);
   }

   public float getIncomingDamageModifier() {
      WormMobHead var1 = (WormMobHead)this.master.get(this.getLevel());
      return var1 == null ? super.getIncomingDamageModifier() : var1.getIncomingDamageModifier();
   }
}
