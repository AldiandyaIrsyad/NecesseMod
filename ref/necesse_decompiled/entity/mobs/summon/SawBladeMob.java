package necesse.entity.mobs.summon;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SawBladeMob extends SummonedMob {
   public static GameDamage damage = new GameDamage(25.0F, 20.0F, 0.0F, 2.0F, 1.0F);
   public float sawSpeed;
   public float topSpeed = 100.0F;
   private float particleBuffer;
   public int sawDir;
   long startTime = 0L;
   long startupTime = 250L;
   long moveStartTime = 0L;
   long lifeTime = 10000L;
   private SoundPlayer movingSound;
   private SoundPlayer collisionSound;
   private SoundPlayer deathSound;
   boolean isAtEndOfTrack = false;

   public SawBladeMob() {
      super(1);
      this.isSummoned = true;
      this.setSpeed(250.0F);
      this.setFriction(0.0F);
      this.accelerationMod = 0.2F;
      this.setKnockbackModifier(0.1F);
      this.collision = new Rectangle(-10, -10, 20, 14);
      this.hitBox = new Rectangle(-14, -15, 28, 24);
      this.selectBox = new Rectangle(-14, -20, 28, 30);
      this.overrideMountedWaterWalking = true;
      this.staySmoothSnapped = true;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("sawDir", this.sawDir);
      var1.addFloat("sawSpeed", this.sawSpeed);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.sawDir = var1.getInt("sawDir", this.sawDir);
      this.sawSpeed = var1.getFloat("sawSpeed", this.sawSpeed);
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.sawSpeed);
      var1.putNextMaxValue(this.sawDir, 3);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.sawSpeed = var1.getNextFloat();
      this.sawDir = var1.getNextMaxValue(3);
   }

   public void tickCurrentMovement(float var1) {
      super.tickCurrentMovement(var1);
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
   }

   protected void tickCollisionMovement(float var1, Mob var2) {
      int var3 = this.getTileX();
      int var4 = this.getTileY();
      GameObject var5 = this.getLevel().getObject(var3, var4);
      if (var5 instanceof TrapTrackObject) {
         TrapTrackObject var6 = (TrapTrackObject)var5;
         float var7 = this.colDx / 20.0F;
         float var8 = this.colDy / 20.0F;
         float var9 = this.moveX;
         float var10 = this.moveY;
         MinecartLines var11 = var6.getMinecartLines(this.getLevel(), var3, var4, var9, var10, false);
         MinecartLinePos var12 = var11.getMinecartPos(this.x, this.y, this.sawDir);
         if (var12 != null) {
            float var13 = 0.0F;
            switch (this.sawDir) {
               case 0:
                  if (var10 < 0.0F) {
                     var13 = 1.0F;
                  }

                  var13 -= var8;
                  var7 = 0.0F;
                  break;
               case 1:
                  if (var9 > 0.0F) {
                     var13 = 1.0F;
                  }

                  var13 += var7;
                  var8 = 0.0F;
                  break;
               case 2:
                  if (var10 > 0.0F) {
                     var13 = 1.0F;
                  }

                  var13 += var8;
                  var7 = 0.0F;
                  break;
               default:
                  if (var9 < 0.0F) {
                     var13 = 1.0F;
                  }

                  var13 -= var7;
                  var8 = 0.0F;
            }

            if (var7 != 0.0F || var8 != 0.0F) {
               this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
            }

            float var14 = this.getAccelerationModifier();
            float var15 = this.getSpeed();
            if (this.sawSpeed < this.topSpeed && this.moveStartTime > 0L) {
               this.sawSpeed += (-(var15 * var13) + this.sawSpeed) * var1 / 250.0F * var14;
            }

            if (this.sawSpeed < 0.0F) {
               this.sawDir = (this.sawDir + 2) % 4;
               this.sawSpeed = 0.0F;
            }

            if (var13 == 0.0F && Math.abs(this.sawSpeed) < var15 / 40.0F) {
               this.sawSpeed = 0.0F;
            }

            if (this.startTime == 0L) {
               this.startTime = this.getWorldEntity().getTime();
            }

            if (this.moveStartTime == 0L && this.getWorldEntity().getTime() - this.startTime > this.startupTime) {
               this.moveStartTime = this.getWorldEntity().getTime();
               this.sawSpeed = 30.0F;
            }

            if (this.sawSpeed > 0.0F) {
               if (this.moveStartTime > 0L) {
                  this.particleBuffer += var1;
                  this.drawTrailParticles();
               }

               MinecartLinePos var16 = var12.progressLines(this.sawDir, this.sawSpeed * var1 / 250.0F, (Consumer)null);
               this.x = var16.x;
               this.y = var16.y;
               this.sawDir = var16.dir;
               this.dir = this.sawDir;
               this.dx = this.dir != 1 && this.dir != 3 ? 0.0F : this.sawSpeed * (float)(this.dir == 1 ? 1 : -1);
               this.dy = this.dir != 0 && this.dir != 2 ? 0.0F : this.sawSpeed * (float)(this.dir == 2 ? 1 : -1);
               if (var16.distanceRemainingToTravel > 0.0F) {
                  this.isAtEndOfTrack = true;
                  this.sawSpeed = 0.0F;
               } else if (!this.isServer()) {
                  if (this.movingSound == null || this.movingSound.isDone()) {
                     this.movingSound = Screen.playSound(GameResources.train, SoundEffect.effect(this).falloffDistance(1400).volume(0.0F));
                  }

                  if (this.movingSound != null) {
                     this.movingSound.effect.volume(Math.min(this.sawSpeed / 200.0F, 1.0F) / 1.5F);
                     this.movingSound.refreshLooping(0.2F);
                  }
               }
            } else {
               this.moveStartTime = 0L;
               this.x = var12.x;
               this.y = var12.y;
               if (var12.dir != 1 && var12.dir != 3) {
                  if (this.sawDir == 1 || this.sawDir == 3) {
                     this.sawDir = var12.dir;
                  }
               } else if (this.sawDir == 0 || this.sawDir == 2) {
                  this.sawDir = var12.dir;
               }
            }
         }
      } else {
         this.sawDir = this.dir;
         this.dx = 0.0F;
         this.dy = 0.0F;
         if (this.colDx != 0.0F || this.colDy != 0.0F) {
            this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
         }

         super.tickCollisionMovement(var1, var2);
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.startTime == 0L) {
         this.startTime = this.getWorldEntity().getTime();
      } else if (this.startTime + this.lifeTime < this.getWorldEntity().getTime()) {
         this.isAtEndOfTrack = true;
      }

      if (this.isAtEndOfTrack) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void clientTick() {
      super.clientTick();
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.getLevel().isTrialRoom ? new GameDamage(DamageTypeRegistry.TRUE, (float)var1.getMaxHealth() / 4.0F) : damage;
   }

   public GameMessage getAttackerName() {
      return new LocalMessage("deaths", "sawtrapname");
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("sawtrap", 2);
   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      return false;
   }

   public boolean canBeHit(Attacker var1) {
      return false;
   }

   public boolean canTakeDamage() {
      return false;
   }

   protected void playHitSound() {
      if (this.collisionSound == null) {
         this.collisionSound = Screen.playSound(GameResources.pop, SoundEffect.effect(this).falloffDistance(1400).volume(0.0F));
      }

      if (this.collisionSound != null) {
         this.collisionSound.effect.volume(Math.min(this.sawSpeed / 200.0F, 1.0F) / 1.5F);
      }

   }

   protected void playDeathSound() {
      if (this.deathSound == null) {
         this.deathSound = Screen.playSound(GameResources.pop, SoundEffect.effect(this).falloffDistance(1400).volume(0.0F));
      }

      if (this.deathSound != null) {
         this.deathSound.effect.volume(1.0F);
      }

   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      this.playDeathSound();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.sawblade, var3, 1, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   private long getTimeSinceStartedMoving() {
      return this.getWorldEntity().getTime() - this.moveStartTime;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   private void drawTrailParticles() {
      if (this.particleBuffer > 50.0F) {
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 3.0F, this.y - 4.0F + GameRandom.globalRandom.floatGaussian() * 3.0F, Particle.GType.IMPORTANT_COSMETIC).color(new Color(235, 193, 49)).height(0.0F).lifeTime(500);
         this.particleBuffer -= 50.0F;
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 24;
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var13;
      if (this.moveStartTime > 0L) {
         byte var14 = 75;
         if (this.getTimeSinceStartedMoving() < (long)var14) {
            var13 = MobRegistry.Textures.sawblade.initDraw().sprite(0, 0, 32).light(var10).pos(var11, var12);
         } else if (this.getTimeSinceStartedMoving() < (long)(var14 * 2)) {
            var13 = MobRegistry.Textures.sawblade.initDraw().sprite(1, 0, 32).light(var10).pos(var11, var12);
         } else {
            int var15 = (int)(this.getTimeSinceStartedMoving() / (long)var14) % 2;
            var13 = MobRegistry.Textures.sawblade.initDraw().sprite(2 + var15, 0, 32).light(var10).pos(var11, var12);
         }
      } else {
         var13 = MobRegistry.Textures.sawblade.initDraw().sprite(0, 0, 32).light(var10).pos(var11, var12);
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var13.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }
}
