package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.light.GameLight;

public class ReturnPortalMob extends Mob {
   public ReturnPortalMob() {
      super(100);
      this.setFriction(1.0F);
      this.isSummoned = true;
      this.shouldSave = true;
      this.isStatic = true;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-18, -45, 36, 48);
      this.setKnockbackModifier(0.0F);
   }

   public void init() {
      super.init();
      this.dir = 0;
   }

   public boolean canPushMob(Mob var1) {
      return false;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 20.0F, 0.7F);

      int var1;
      int var2;
      float var4;
      float var5;
      for(var1 = 0; var1 < 2; ++var1) {
         var2 = GameRandom.globalRandom.nextInt(360);
         Point2D.Float var3 = GameMath.getAngleDir((float)var2);
         var4 = GameRandom.globalRandom.getFloatBetween(25.0F, 40.0F);
         var5 = this.x + var3.x * var4;
         float var6 = this.y + 4.0F;
         float var7 = 29.0F;
         float var8 = var7 + var3.y * var4;
         int var9 = GameRandom.globalRandom.getIntBetween(200, 500);
         float var10 = var3.x * var4 * 250.0F / (float)var9;
         Color var11 = new Color(150, 54, 13);
         Color var12 = new Color(255, 91, 3);
         Color var13 = new Color(255, 121, 3);
         Color var14 = (Color)GameRandom.globalRandom.getOneOf((Object[])(var11, var12, var13));
         this.getLevel().entityManager.addParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(var14).fadesAlphaTime(100, 50).lifeTime(var9);
      }

      for(var1 = 0; var1 < 2; ++var1) {
         var2 = GameRandom.globalRandom.getIntBetween(500, 1000);
         float var15 = (float)var2 / 1000.0F;
         var4 = (float)(5 + GameRandom.globalRandom.nextInt(5));
         var5 = var4 + (float)GameRandom.globalRandom.getIntBetween(20, 50) * var15;
         Color var16 = new Color(150, 54, 13);
         Color var17 = new Color(255, 91, 3);
         Color var18 = new Color(255, 121, 3);
         Color var19 = (Color)GameRandom.globalRandom.getOneOf((Object[])(var16, var17, var18));
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), this.y + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(6, 12, 100, var2 - 100).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), 0.7F).heightMoves(var4, var5).color(var19).lifeTime(var2);
      }

   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (var1 != null && var1.isServerClient() && !var1.buffManager.hasBuff("teleportsickness")) {
         ServerClient var2 = var1.getServerClient();
         Level var3 = this.getLevel();
         if (var3 instanceof IncursionLevel) {
            ((IncursionLevel)var3).returnToAltar(var2);
         } else {
            this.remove();
         }
      }

   }

   public boolean canInteract(Mob var1) {
      return true;
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return Localization.translate("controls", "usetip");
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 1;
      int var12 = var8.getDrawY(var6) - 25;
      int var13 = (int)(this.getWorldEntity().getTime() % 1600L) / 200;
      if (var13 > 4) {
         var13 = 4 - var13 % 4;
      }

      Color var14 = new Color(150, 54, 13);
      Color var15 = new Color(255, 91, 3);
      Color var16 = new Color(255, 121, 3);
      Color var17 = new Color(244, 184, 152);
      Color var18 = new Color(253, 243, 236);
      byte var19 = 50;
      final SharedTextureDrawOptions var20 = new SharedTextureDrawOptions(MobRegistry.Textures.portalSphere);
      this.addSphere(var11, var12 + var13, 0L, 1000, (float)var19 * 0.68F, (float)var19, var14, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 900, (float)var19 * 0.68F, (float)var19, var14, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 800, (float)var19 * 0.64F, (float)var19 * 0.88F, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 760, (float)var19 * 0.64F, (float)var19 * 0.88F, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 700, (float)var19 * 0.64F, (float)var19 * 0.88F, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 740, (float)var19 * 0.48F, (float)var19 * 0.68F, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 680, (float)var19 * 0.48F, (float)var19 * 0.68F, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 650, (float)var19 * 0.48F, (float)var19 * 0.68F, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 630, (float)var19 * 0.28F, (float)var19 * 0.52F, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 580, (float)var19 * 0.28F, (float)var19 * 0.52F, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 550, (float)var19 * 0.28F, (float)var19 * 0.52F, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 540, (float)var19 * 0.16F, (float)var19 * 0.36F, var18, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 520, (float)var19 * 0.16F, (float)var19 * 0.36F, var18, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 490, (float)var19 * 0.16F, (float)var19 * 0.36F, var18, var10, var20);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var20.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected void addSphere(int var1, int var2, long var3, int var5, float var6, float var7, Color var8, GameLight var9, SharedTextureDrawOptions var10) {
      float var11 = GameUtils.getAnimFloat(this.getWorldEntity().getLocalTime() + var3, var5);
      float var12 = var7 - var6;
      int var13 = (int)(var6 + var12 * var11);
      var10.addFull().color(var8).alpha(0.5F).light(var9).size(var13).posMiddle(var1, var2, true);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public boolean isVisibleOnMap(Client var1, LevelMap var2) {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      GameLight var4 = this.getLevel().lightManager.newLight(150.0F);
      Color var5 = new Color(150, 54, 13);
      Color var6 = new Color(255, 91, 3);
      Color var7 = new Color(255, 121, 3);
      Color var8 = new Color(244, 184, 152);
      Color var9 = new Color(253, 243, 236);
      int var11 = var3 - 10;
      byte var12 = 32;
      SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(MobRegistry.Textures.portalSphere);
      this.addSphere(var2, var11, 0L, 1000, (float)var12 * 0.68F, (float)var12, var5, var4, var13);
      this.addSphere(var2, var11, 0L, 900, (float)var12 * 0.68F, (float)var12, var5, var4, var13);
      this.addSphere(var2, var11, 0L, 800, (float)var12 * 0.64F, (float)var12 * 0.88F, var6, var4, var13);
      this.addSphere(var2, var11, 0L, 760, (float)var12 * 0.64F, (float)var12 * 0.88F, var6, var4, var13);
      this.addSphere(var2, var11, 0L, 700, (float)var12 * 0.64F, (float)var12 * 0.88F, var6, var4, var13);
      this.addSphere(var2, var11, 0L, 740, (float)var12 * 0.48F, (float)var12 * 0.68F, var7, var4, var13);
      this.addSphere(var2, var11, 0L, 680, (float)var12 * 0.48F, (float)var12 * 0.68F, var7, var4, var13);
      this.addSphere(var2, var11, 0L, 650, (float)var12 * 0.48F, (float)var12 * 0.68F, var7, var4, var13);
      this.addSphere(var2, var11, 0L, 630, (float)var12 * 0.28F, (float)var12 * 0.52F, var8, var4, var13);
      this.addSphere(var2, var11, 0L, 580, (float)var12 * 0.28F, (float)var12 * 0.52F, var8, var4, var13);
      this.addSphere(var2, var11, 0L, 550, (float)var12 * 0.28F, (float)var12 * 0.52F, var8, var4, var13);
      this.addSphere(var2, var11, 0L, 540, (float)var12 * 0.16F, (float)var12 * 0.36F, var9, var4, var13);
      this.addSphere(var2, var11, 0L, 520, (float)var12 * 0.16F, (float)var12 * 0.36F, var9, var4, var13);
      this.addSphere(var2, var11, 0L, 490, (float)var12 * 0.16F, (float)var12 * 0.36F, var9, var4, var13);
      var13.draw();
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-12, -24, 24, 28);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName());
   }
}
