package necesse.entity.mobs;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.DPSTracker;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.TrainingDummyObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class TrainingDummyMob extends Mob {
   public DPSTracker trainingDummyDPSTracker = new DPSTracker();
   private int aliveTimer;

   public TrainingDummyMob() {
      super(10000);
      this.setArmor(0);
      this.setSpeed(0.0F);
      this.setFriction(1000.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-18, -15, 36, 30);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.shouldSave = false;
      this.aliveTimer = 20;
      this.isStatic = true;
   }

   public void clientTick() {
      super.clientTick();
      this.tickAlive();
   }

   public void serverTick() {
      super.serverTick();
      long var1 = this.getWorldEntity().getTime();
      this.trainingDummyDPSTracker.tick(var1);
      if (this.getLevel().tickManager().isFirstGameTickInSecond() && this.trainingDummyDPSTracker.isLastHitBeforeReset(var1)) {
         float var3 = (float)this.trainingDummyDPSTracker.getDPS(var1);
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsAt(new PacketShowDPS(this.getUniqueID(), var3), (Level)this.getLevel());
         }
      }

      this.tickAlive();
   }

   public boolean canBeTargetedFromAdjacentTiles() {
      return true;
   }

   private void tickAlive() {
      this.setHealthHidden(this.getMaxHealth());
      --this.aliveTimer;
      if (this.aliveTimer <= 0) {
         this.remove();
      }

   }

   public void keepAlive(TrainingDummyObjectEntity var1) {
      this.aliveTimer = 20;
      this.setPos((float)(var1.getX() * 32 + 16), (float)(var1.getY() * 32 + 16), true);
   }

   protected void playHitSound() {
      Screen.playSound(GameResources.blunthit, SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   public boolean canTakeDamage() {
      return true;
   }

   public boolean countDamageDealt() {
      return false;
   }

   public boolean canPushMob(Mob var1) {
      return false;
   }

   public MobWasHitEvent isHit(MobWasHitEvent var1, Attacker var2) {
      if (this.getLevel() != null) {
         this.getLevel().forceGrassWeave(this.getX() / 32, this.getY() / 32, 200);
      }

      return super.isHit(var1, var2);
   }

   public boolean canGiveResilience(Attacker var1) {
      if (var1 != null) {
         Mob var2 = var1.getAttackOwner();
         if (var2 != null) {
            return !var2.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY);
         }
      }

      return super.canGiveResilience(var1);
   }

   public void setHealthHidden(int var1, float var2, float var3, Attacker var4, boolean var5) {
      int var6 = this.getHealth();
      super.setHealthHidden(var1, var2, var3, var4, var5);
      if (this.getLevel() != null) {
         int var7 = this.getHealth();
         if (var7 < var6) {
            int var8 = var6 - var7;
            this.trainingDummyDPSTracker.addHit(this.getWorldEntity().getTime(), (float)var8);
            if (this.isServer() && var4 != null) {
               Mob var9 = var4.getAttackOwner();
               if (var9 != null && var9.isPlayer) {
                  ServerClient var10 = ((PlayerMob)var9).getServerClient();
                  var10.trainingDummyDPSTracker.addHit(this.getWorldEntity().getTime(), (float)var8);
               }
            }
         }
      }

   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      return !var3 ? false : super.onMouseHover(var1, var2, var3);
   }

   public float getArmorAfterPen(float var1) {
      return this.getArmor() - var1;
   }
}
