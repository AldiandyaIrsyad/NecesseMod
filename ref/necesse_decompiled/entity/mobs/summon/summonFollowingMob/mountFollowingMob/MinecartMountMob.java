package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.MinecartLinePos;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MinecartMountMob extends MountFollowingMob {
   public float minecartSpeed;
   public int minecartDir;
   public float collisionMovementBuffer;
   public Point collisionMovementLastPos;
   protected SoundPlayer movingSound;
   protected SoundPlayer breakingSound;
   protected float breakParticleBuffer;
   protected boolean breakParticleAlternate;

   public MinecartMountMob() {
      super(1);
      this.setSpeed(200.0F);
      this.setFriction(3.0F);
      this.accelerationMod = 0.1F;
      this.setKnockbackModifier(0.1F);
      this.collision = new Rectangle(-10, -10, 20, 14);
      this.hitBox = new Rectangle(-14, -15, 28, 24);
      this.selectBox = new Rectangle(-14, -20, 28, 30);
      this.overrideMountedWaterWalking = true;
      this.staySmoothSnapped = true;
   }

   protected GameMessage getSummonLocalization() {
      return MobRegistry.getLocalization("minecart");
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("minecartDir", this.minecartDir);
      var1.addFloat("minecartSpeed", this.minecartSpeed);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.minecartDir = var1.getInt("minecartDir", this.minecartDir);
      this.minecartSpeed = var1.getFloat("minecartSpeed", this.minecartSpeed);
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.minecartSpeed);
      var1.putNextMaxValue(this.minecartDir, 3);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.minecartSpeed = var1.getNextFloat();
      this.minecartDir = var1.getNextMaxValue(3);
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
      if (var5 instanceof MinecartTrackObject && !(var5 instanceof TrapTrackObject)) {
         MinecartTrackObject var6 = (MinecartTrackObject)var5;
         float var7 = this.colDx / 20.0F;
         float var8 = this.colDy / 20.0F;
         float var9 = this.moveX;
         float var10 = this.moveY;
         MinecartLines var11 = var6.getMinecartLines(this.getLevel(), var3, var4, var9, var10, false);
         MinecartLinePos var12 = var11.getMinecartPos(this.x, this.y, this.minecartDir);
         if (var12 != null) {
            boolean var13 = false;
            float var14 = 0.0F;
            if (this.minecartDir == 0) {
               if (var10 < 0.0F) {
                  var14 = 1.0F;
               } else if (var10 > 0.0F) {
                  var13 = true;
                  var14 = -1.0F;
               }

               var14 -= var8;
               var7 = 0.0F;
            } else if (this.minecartDir == 1) {
               if (var9 > 0.0F) {
                  var14 = 1.0F;
               } else if (var9 < 0.0F) {
                  var13 = true;
                  var14 = -1.0F;
               }

               var14 += var7;
               var8 = 0.0F;
            } else if (this.minecartDir == 2) {
               if (var10 > 0.0F) {
                  var14 = 1.0F;
               } else if (var10 < 0.0F) {
                  var13 = true;
                  var14 = -1.0F;
               }

               var14 += var8;
               var7 = 0.0F;
            } else {
               if (var9 < 0.0F) {
                  var14 = 1.0F;
               } else if (var9 > 0.0F) {
                  var13 = true;
                  var14 = -1.0F;
               }

               var14 -= var7;
               var8 = 0.0F;
            }

            if (var7 != 0.0F || var8 != 0.0F) {
               if (this.getLevel().entityManager.players.streamArea(this.x, this.y, 100).filter(this::collidesWith).anyMatch((var0) -> {
                  return true;
               })) {
                  this.collisionMovementBuffer = (float)((double)this.collisionMovementBuffer + GameMath.diagonalMoveDistance((double)var7, (double)var8) * (double)var1 / 250.0 * 20.0);
               }

               this.collisionMovementLastPos = new Point(this.getX(), this.getY());
               this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
            }

            float var15 = var2 == null ? 2.0F : 0.0F;
            float var16 = this.getAccelerationModifier();
            float var17 = this.getSpeed();
            if (var15 != 0.0F) {
               this.minecartSpeed += (var17 * var15 * var14 - var15 * this.minecartSpeed) * var1 / 250.0F * var16;
            } else if (var14 != 0.0F) {
               this.minecartSpeed += (var17 * var14 - this.minecartSpeed) * var1 / 250.0F * var16;
            }

            if (this.minecartSpeed < 0.0F) {
               this.minecartDir = (this.minecartDir + 2) % 4;
               this.minecartSpeed = 0.0F;
            }

            if (var14 == 0.0F && Math.abs(this.minecartSpeed) < var17 / 40.0F) {
               this.minecartSpeed = 0.0F;
            }

            if (this.minecartSpeed > 0.0F) {
               MinecartLinePos var18 = var12.progressLines(this.minecartDir, this.minecartSpeed * var1 / 250.0F, (Consumer)null);
               this.x = var18.x;
               this.y = var18.y;
               this.minecartDir = var18.dir;
               this.dir = this.minecartDir;
               if (var18.distanceRemainingToTravel > 0.0F) {
                  if (!this.isServer() && this.minecartSpeed > 25.0F) {
                     Screen.playSound(GameResources.cling, SoundEffect.effect(this).volume(0.6F).pitch(0.8F));
                  }

                  this.minecartSpeed = 0.0F;
               } else if (!this.isServer()) {
                  if (var13 && var14 < 0.0F) {
                     if (this.minecartSpeed > 10.0F) {
                        this.breakParticleBuffer += var1;
                        if (this.breakParticleBuffer > 10.0F) {
                           this.breakParticleBuffer -= 10.0F;
                           float var19 = GameRandom.globalRandom.floatGaussian();
                           float var20 = GameRandom.globalRandom.floatGaussian();
                           boolean var21 = this.breakParticleAlternate;
                           if (this.minecartDir == 0) {
                              var19 += var21 ? 8.0F : -8.0F;
                              var20 += 4.0F;
                           } else if (this.minecartDir == 1) {
                              var20 += var21 ? 6.0F : -6.0F;
                              var19 -= 4.0F;
                           } else if (this.minecartDir == 2) {
                              var19 += var21 ? 8.0F : -8.0F;
                              var20 -= 4.0F;
                           } else {
                              var20 += var21 ? 6.0F : -6.0F;
                              var19 += 4.0F;
                           }

                           float var10001 = this.x + var19;
                           float var10002 = this.y + var20;
                           this.getLevel().entityManager.addParticle(var10001, var10002, Particle.GType.IMPORTANT_COSMETIC).color(new Color(210, 160, 8)).sizeFadesInAndOut(4, 8, 50, 200).movesConstant(this.dx / 10.0F, this.dy / 10.0F).lifeTime(300).height(2.0F);
                           this.breakParticleAlternate = !this.breakParticleAlternate;
                        }
                     }

                     if (this.breakingSound == null || this.breakingSound.isDone()) {
                        this.breakingSound = Screen.playSound(GameResources.trainBrake, SoundEffect.effect(this).falloffDistance(1400).volume(0.0F));
                     }

                     if (this.breakingSound != null) {
                        this.breakingSound.effect.volume(GameMath.limit((this.minecartSpeed - 10.0F) / 100.0F, 0.0F, 1.0F) * 1.5F);
                        this.breakingSound.refreshLooping(0.5F);
                     }
                  }

                  if (this.movingSound == null || this.movingSound.isDone()) {
                     this.movingSound = Screen.playSound(GameResources.train, SoundEffect.effect(this).falloffDistance(1400).volume(0.0F));
                  }

                  if (this.movingSound != null) {
                     this.movingSound.effect.volume(Math.min(this.minecartSpeed / 200.0F, 1.0F) / 1.5F);
                     this.movingSound.refreshLooping(0.2F);
                  }
               }
            } else {
               this.x = var12.x;
               this.y = var12.y;
               if (var12.dir != 1 && var12.dir != 3) {
                  if (this.minecartDir == 1 || this.minecartDir == 3) {
                     this.minecartDir = var12.dir;
                  }
               } else if (this.minecartDir == 0 || this.minecartDir == 2) {
                  this.minecartDir = var12.dir;
               }

               this.dir = this.minecartDir;
            }
         } else {
            this.minecartSpeed = 0.0F;
            this.minecartDir = var2 == null ? this.dir : var2.dir;
         }
      } else {
         this.minecartDir = var2 == null ? this.dir : var2.dir;
         this.dx = 0.0F;
         this.dy = 0.0F;
         if (this.colDx != 0.0F || this.colDy != 0.0F) {
            if (this.getLevel().entityManager.players.streamArea(this.x, this.y, 100).filter(this::collidesWith).anyMatch((var0) -> {
               return true;
            })) {
               this.collisionMovementBuffer = (float)((double)this.collisionMovementBuffer + GameMath.diagonalMoveDistance((double)this.colDx, (double)this.colDy) * (double)var1 / 250.0);
            }

            this.collisionMovementLastPos = new Point(this.getX(), this.getY());
            this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
         }

         super.tickCollisionMovement(var1, var2);
      }

      if (this.collisionMovementBuffer >= 5.0F || this.collisionMovementLastPos != null && GameMath.diagonalMoveDistance(this.collisionMovementLastPos, new Point(this.getX(), this.getY())) > 32.0) {
         this.collisionMovementBuffer = 0.0F;
         this.collisionMovementLastPos = null;
         this.sendMovementPacket(false);
      }

   }

   public void serverTick() {
      super.serverTick();
      if (!this.isMounted()) {
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

      if (this.inLiquid()) {
         this.setHealth(0);
      }

   }

   public void clientTick() {
      super.clientTick();
      if (!this.isMounted()) {
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
   }

   protected void playHitSound() {
   }

   public void interact(PlayerMob var1) {
      if (this.isServer()) {
         if (var1.getUniqueID() == this.rider) {
            var1.dismount();
            this.getLevel().getServer().network.sendToClientsAt(new PacketMobMount(var1.getUniqueID(), -1, false, var1.x, var1.y), (Level)this.getLevel());
         } else if (var1.mount(this, false)) {
            this.getLevel().getServer().network.sendToClientsAt(new PacketMobMount(var1.getUniqueID(), this.getUniqueID(), false, var1.x, var1.y), (Level)this.getLevel());
         }
      }

   }

   public boolean canInteract(Mob var1) {
      return !this.isMounted() || var1.getUniqueID() == this.rider;
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return this.isMounted() ? null : Localization.translate("controls", "usetip");
   }

   protected void playDeathSound() {
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 47;
      Point var13 = this.getAnimSprite(var5, var6, this.minecartDir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.minecart.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
         }

         public void drawBehindRider(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public static void drawPlacePreview(Level var0, int var1, int var2, int var3, GameCamera var4) {
      Mob var5 = MobRegistry.getMob("minecart", var0);
      int var6;
      int var7;
      if (var5 != null) {
         var5.setPos((float)var1, (float)var2, true);
         var6 = var5.getTileX();
         var7 = var5.getTileY();
         GameObject var8 = var0.getObject(var6, var7);
         if (var8 instanceof MinecartTrackObject) {
            MinecartTrackObject var9 = (MinecartTrackObject)var8;
            float var10 = 0.0F;
            float var11 = 0.0F;
            if (var3 == 0) {
               var11 = -1.0F;
            } else if (var3 == 1) {
               var10 = 1.0F;
            } else if (var3 == 2) {
               var11 = 1.0F;
            } else {
               var10 = -1.0F;
            }

            MinecartLines var12 = var9.getMinecartLines(var0, var6, var7, var10, var11, false);
            MinecartLinePos var13 = var12.getMinecartPos((float)var1, (float)var2, var3);
            if (var13 != null) {
               int var14 = var4.getDrawX(var13.x) - 32;
               int var15 = var4.getDrawY(var13.y) - 47;
               Point var16 = var5.getAnimSprite((int)var13.x, (int)var13.y, var13.dir);
               var15 += var5.getBobbing((int)var13.x, (int)var13.y);
               var15 += var0.getTile((int)var13.x / 32, (int)var13.y / 32).getMobSinkingAmount(var5);
               MobRegistry.Textures.minecart.initDraw().sprite(var16.x, var16.y, 64).alpha(0.5F).draw(var14, var15);
               return;
            }
         }
      }

      var6 = var4.getDrawX(var1) - 32;
      var7 = var4.getDrawY(var2) - 47;
      var7 += var0.getLevelTile(var1 / 32, var2 / 32).getLiquidBobbing();
      MobRegistry.Textures.minecart.initDraw().sprite(0, var3, 64).alpha(0.5F).draw(var6, var7);
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      Point var4 = new Point(0, var3);
      if (!this.inLiquid(var1, var2)) {
         var4.x = (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 2;
      }

      return var4;
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      return getShadowDrawOptions(this, var1, var2, 0, this.minecartDir, var3, var4);
   }

   public static TextureDrawOptions getShadowDrawOptions(Mob var0, int var1, int var2, int var3, int var4, GameLight var5, GameCamera var6) {
      GameTexture var7 = MobRegistry.Textures.minecart_shadow;
      int var8 = var6.getDrawX(var1) - 32;
      int var9 = var6.getDrawY(var2) - 47 + var3;
      var9 += var0.getBobbing(var1, var2);
      var9 += var0.getLevel().getTile(var1 / 32, var2 / 32).getMobSinkingAmount(var0);
      return var7.initDraw().sprite(0, var4, 64).light(var5).pos(var8, var9);
   }

   public int getRockSpeed() {
      return 10;
   }

   public int getWaterRockSpeed() {
      return 10000;
   }

   public Point getSpriteOffset(int var1, int var2) {
      Point var3 = new Point(0, 0);
      var3.x += this.getRiderDrawXOffset();
      var3.y += this.getRiderDrawYOffset();
      return var3;
   }

   public int getRiderDrawYOffset() {
      return -3;
   }

   public int getRiderArmSpriteX() {
      return 2;
   }

   public GameTexture getRiderMask() {
      return MobRegistry.Textures.minecart_mask[GameMath.limit(this.minecartDir, 0, MobRegistry.Textures.minecart_mask.length - 1)];
   }

   public int getRiderMaskYOffset() {
      return -7;
   }

   public boolean isWaterWalking() {
      GameObject var1 = this.getLevel().getObject(this.getTileX(), this.getTileY());
      return var1 instanceof MinecartTrackObject ? true : super.isWaterWalking();
   }

   public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.WATER_WALKING, true));
   }
}
