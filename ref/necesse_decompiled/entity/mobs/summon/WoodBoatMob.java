package necesse.entity.mobs.summon;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WoodBoatMob extends SummonedMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("woodboat")});
   protected double deltaCounter;

   public WoodBoatMob() {
      super(1);
      this.isSummoned = true;
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
            addParticleEffects(this);
         }
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
   }

   protected void playHitSound() {
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().allLandTiles();
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

   public static void drawPlacePreview(Level var0, int var1, int var2, int var3, GameCamera var4) {
      int var5 = var4.getDrawX(var1) - 32;
      int var6 = var4.getDrawY(var2) - 47;
      var6 += var0.getLevelTile(var1 / 32, var2 / 32).getLiquidBobbing();
      MobRegistry.Textures.woodBoat.initDraw().sprite(0, var3, 64).alpha(0.5F).draw(var5, var6);
   }

   public static void addParticleEffects(Mob var0) {
      addParticleEffects(var0, 0.0F, 0.0F, 13.0F);
   }

   public static void addParticleEffects(Mob var0, float var1, float var2, float var3) {
      Level var4 = var0.getLevel();
      Point2D.Float var5 = GameMath.normalize(var0.dx, var0.dy);
      Point2D.Float var6 = GameMath.getPerpendicularDir(var5.x, var5.y);
      var3 += var0.getCurrentSpeed() / 8.0F;
      if (var0.dy > 0.0F) {
         var3 -= var5.y * 3.0F;
      }

      Point2D.Float var7 = new Point2D.Float(var5.x * var3, var5.y * var3);
      int var8 = var0.getTileX();
      int var9 = var0.getTileY();
      GameTile var10 = var4.getTile(var8, var9);
      Color var11 = var10.getMapColor(var4, var8, var9);
      float var12 = (float)(-10 + var10.getLiquidBobbing(var4, var8, var9));
      float var13 = GameMath.limit(var0.getCurrentSpeed() / 40.0F, 0.2F, 1.0F);

      for(int var14 = 0; var14 < 4; ++var14) {
         int var15 = var14 % 2 == 0 ? 1 : -1;
         Point2D.Float var16 = new Point2D.Float(var6.x * (float)var15, var6.y * (float)var15);
         float var17 = GameRandom.globalRandom.nextFloat() * GameMath.limit(var0.getCurrentSpeed() / 40.0F, 0.2F, 1.0F);
         float var18 = 3.0F + var17 * 3.0F;
         float var19 = 15.0F + var17 * 10.0F;
         var4.entityManager.addParticle(var0.x + var1 + var7.x + var16.x * 3.0F + var16.x * GameRandom.globalRandom.floatGaussian() * 1.5F, var0.y + var2 + var7.y - 10.0F + var16.y * 3.0F + var16.y * GameRandom.globalRandom.floatGaussian() * 1.5F, Particle.GType.COSMETIC).movesFriction(var16.x * var19, var16.y * var19, 0.5F).color(var11.brighter()).sizeFadesInAndOut(3, 5, 0.1F).height((var2x, var3x, var4x, var5x) -> {
            double var6 = Math.sin((double)var5x * Math.PI);
            return var12 + (float)var6 * var18;
         }).lifeTime((int)((float)GameRandom.globalRandom.getIntBetween(200, 400) * var13));
      }

   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      Point var4 = new Point(0, var3);
      return var4;
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      return getShadowDrawOptions(this, var1, var2, -6, var3, var4);
   }

   public static TextureDrawOptions getShadowDrawOptions(Mob var0, int var1, int var2, int var3, GameLight var4, GameCamera var5) {
      GameTexture var6 = MobRegistry.Textures.boat_shadow;
      int var7 = var6.getHeight();
      int var8 = var5.getDrawX(var1) - var7 / 2;
      int var9 = var5.getDrawY(var2) - var7 / 2 + var3;
      var9 += var0.getBobbing(var1, var2);
      return var6.initDraw().sprite(var0.dir, 0, var7).light(var4).pos(var8, var9);
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
