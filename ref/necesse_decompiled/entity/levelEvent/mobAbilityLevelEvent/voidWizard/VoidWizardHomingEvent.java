package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.VoidWizardHomingProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardHomingEvent extends MobAbilityLevelEvent {
   private int tickCounter;
   private int targetID;
   private VoidWizard wizard;
   private Mob target;
   private GameDamage damage;
   private boolean startInstantly;
   private boolean addParticle;

   public VoidWizardHomingEvent() {
   }

   public VoidWizardHomingEvent(Mob var1, Mob var2, boolean var3, boolean var4) {
      super(var1, GameRandom.globalRandom);
      this.target = var2;
      if (var2 != null) {
         this.targetID = var2.getUniqueID();
      } else {
         this.targetID = -1;
      }

      this.startInstantly = var3;
      this.addParticle = var4;
   }

   public void init() {
      super.init();
      if (this.owner != null) {
         this.tickCounter = this.startInstantly ? 20 : 0;
         this.damage = VoidWizard.homingExplosion.modDamage(0.8F);
         this.target = GameUtils.getLevelMob(this.targetID, this.level);
         if (this.target == null) {
            GameLog.warn.println("Could not find target for dungeon wizard attack homing event, server level: " + this.isServer());
            this.over();
         } else {
            if (this.isClient() && this.addParticle) {
               this.level.entityManager.particles.add(new EventParticle(this.level, this.owner, this.target, 3000L));
            }

            if (this.owner instanceof VoidWizard) {
               this.wizard = (VoidWizard)this.owner;
               this.wizard.swingAttack = false;
               this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
            }

         }
      }
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.targetID);
      var1.putNextBoolean(this.startInstantly);
      var1.putNextBoolean(this.addParticle);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.targetID = var1.getNextInt();
      this.startInstantly = var1.getNextBoolean();
      this.addParticle = var1.getNextBoolean();
   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.owner != null && !this.owner.removed() && this.tickCounter <= 60) {
         if (this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
         }

      } else {
         this.over();
      }
   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.owner != null && !this.owner.removed() && this.target.isSamePlace(this.owner) && this.tickCounter <= 60) {
         if (this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
         }

         if (this.tickCounter > 20 && this.tickCounter % 5 == 0) {
            VoidWizardHomingProjectile var1 = new VoidWizardHomingProjectile(this.level, this.owner, this.target, this.damage);
            this.level.entityManager.projectiles.add(var1);
            if (this.wizard != null) {
               this.wizard.playBoltSoundAbility.runAndSend(1.0F, 1.1F);
            }
         }

      } else {
         this.over();
      }
   }

   public void over() {
      super.over();
      if (this.wizard != null) {
         this.wizard.isAttacking = false;
      }

   }

   public class EventParticle extends Particle {
      private Mob owner;
      private Mob target;

      public EventParticle(Level var2, Mob var3, Mob var4, long var5) {
         super(var2, var3.x, var3.y, var5);
         this.owner = var3;
         this.target = var4;
      }

      public void tickMovement(float var1) {
         this.x = this.owner.x;
         this.y = this.owner.y;
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         if (!this.removed()) {
            GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
            int var10 = var7.getDrawX(this.x);
            int var11 = var7.getDrawY(this.y) - 16;
            float var12 = this.getRotation();
            TextureDrawOptionsEnd var13 = MobRegistry.Textures.voidWizard.body.initDraw().sprite(0, 5, 64).light(var9.minLevelCopy(Math.min(var9.getLevel() + 100.0F, 150.0F))).rotate(var12, 0, 0).pos(var10, var11);
            var3.add((var1x) -> {
               var13.draw();
            });
         }
      }

      public float getRotation() {
         float var1 = this.owner.x - this.target.x;
         float var2 = this.owner.y - this.target.y;
         float var3 = (float)(var1 == 0.0F ? (double)(var2 < 0.0F ? 90 : -90) : Math.toDegrees(Math.atan((double)(var2 / var1))));
         if (var1 > 0.0F) {
            var3 += 180.0F;
         }

         return var3 - 45.0F;
      }
   }
}
