package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WoodBoatMountMob extends MountFollowingMob {
   protected double deltaCounter;

   public WoodBoatMountMob() {
      super(100);
      this.setSpeed(5.0F);
      this.setFriction(0.2F);
      this.setSwimSpeed(10.0F);
      this.accelerationMod = 2.0F;
      this.setKnockbackModifier(0.1F);
      this.collision = new Rectangle(-10, -10, 20, 14);
      this.hitBox = new Rectangle(-14, -15, 28, 24);
      this.selectBox = new Rectangle(-16, -26, 32, 36);
      this.overrideMountedWaterWalking = true;
   }

   public void serverTick() {
      super.serverTick();
      if (!this.isMounted()) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

   }

   public void clientTick() {
      super.clientTick();
      if (!this.isMounted()) {
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (!this.isServer() && this.inLiquid()) {
         this.deltaCounter += (double)(var1 * Math.max(0.2F, this.getCurrentSpeed() / 30.0F));
         if (this.deltaCounter >= 50.0) {
            this.deltaCounter -= 50.0;
            WoodBoatMob.addParticleEffects(this);
         }
      }

   }

   protected GameMessage getSummonLocalization() {
      return MobRegistry.getLocalization("woodboat");
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().allLandTiles();
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return this.isMounted() ? null : Localization.translate("controls", "usetip");
   }

   public void onFollowingAnotherLevel(PlayerMob var1) {
      if (this.getRider() == var1) {
         super.onFollowingAnotherLevel(var1);
      } else {
         this.remove();
      }

   }

   protected void playDeathSound() {
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 47 - 10;
      if (!this.inLiquid(var5, var6)) {
         var12 += 10;
      }

      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.woodBoat.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
         }

         public void drawBehindRider(TickManager var1) {
            var14.draw();
         }
      });
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      Point var4 = new Point(0, var3);
      return var4;
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.boat_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2 - 6;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.dir, 0, var6).light(var3).pos(var7, var8);
   }

   public int getRockSpeed() {
      return 10000;
   }

   public int getWaterRockSpeed() {
      return 100;
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
      return MobRegistry.Textures.boat_mask[GameMath.limit(this.dir, 0, MobRegistry.Textures.boat_mask.length - 1)];
   }

   public int getRiderMaskYOffset() {
      return -7;
   }

   public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.TRAVEL_DISTANCE, 1));
   }
}
