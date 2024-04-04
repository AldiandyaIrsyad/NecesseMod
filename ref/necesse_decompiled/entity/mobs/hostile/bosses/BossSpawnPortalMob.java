package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.light.GameLight;

public class BossSpawnPortalMob extends Mob {
   protected Color color1 = new Color(150, 13, 38);
   protected Color color2 = new Color(255, 3, 62);
   protected Color color3 = new Color(255, 3, 49);
   protected Color color4 = new Color(244, 152, 152);
   protected Color color5 = new Color(253, 236, 236);
   public int remainingAttemptsTip = 0;

   public BossSpawnPortalMob() {
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

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("remainingAttemptsTip", this.remainingAttemptsTip);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.remainingAttemptsTip = var1.getInt("remainingAttemptsTip", this.remainingAttemptsTip, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.remainingAttemptsTip);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.remainingAttemptsTip = var1.getNextInt();
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
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, 0.7F);

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
         Color var11 = (Color)GameRandom.globalRandom.getOneOf((Object[])(this.color1, this.color2, this.color3));
         this.getLevel().entityManager.addParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(var11).fadesAlphaTime(100, 50).lifeTime(var9);
      }

      for(var1 = 0; var1 < 2; ++var1) {
         var2 = GameRandom.globalRandom.getIntBetween(500, 1000);
         float var12 = (float)var2 / 1000.0F;
         var4 = (float)(5 + GameRandom.globalRandom.nextInt(5));
         var5 = var4 + (float)GameRandom.globalRandom.getIntBetween(20, 50) * var12;
         Color var13 = (Color)GameRandom.globalRandom.getOneOf((Object[])(this.color1, this.color2, this.color3));
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), this.y + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(6, 12, 100, var2 - 100).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), 0.7F).heightMoves(var4, var5).color(var13).lifeTime(var2);
      }

   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (var1 != null && var1.isServerClient()) {
         Level var2 = this.getLevel();
         if (var2 instanceof IncursionLevel) {
            IncursionLevelEvent var3 = ((IncursionLevel)var2).getIncursionLevelEvent();
            if (var3 != null) {
               var3.onBossSpawnTriggered(this);
            }
         } else {
            this.remove(0.0F, 0.0F, (Attacker)null, true);
         }
      }

   }

   public boolean canInteract(Mob var1) {
      return true;
   }

   protected void addHoverTooltips(ListGameTooltips var1, boolean var2) {
      super.addHoverTooltips(var1, var2);
      if (this.remainingAttemptsTip > 0) {
         GameColor var3 = GameColor.NO_COLOR;
         if (this.remainingAttemptsTip <= 2) {
            var3 = GameColor.RED;
         } else if (this.remainingAttemptsTip <= 4) {
            var3 = GameColor.ITEM_QUEST;
         }

         var1.add((Object)(new StringTooltips(Localization.translate("misc", "bossattempts", "number", (Object)this.remainingAttemptsTip), var3)));
      }

   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return Localization.translate("controls", "usetip");
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         int var4 = GameRandom.globalRandom.getIntBetween(500, 5000);
         float var5 = (float)var4 / 5000.0F;
         float var6 = 20.0F;
         float var7 = var6 + (float)GameRandom.globalRandom.getIntBetween(70, 150) * var5;
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), this.y + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F), Particle.GType.IMPORTANT_COSMETIC).sizeFades(8, 12).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0F, 40.0F), GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), 0.5F).heightMoves(var6, var7).colorRandom((Color)GameRandom.globalRandom.getOneOf((Object[])(this.color1, this.color2, this.color3)), 0.1F, 0.2F, 0.2F).lifeTime(var4);
      }

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

      byte var14 = 50;
      final SharedTextureDrawOptions var15 = new SharedTextureDrawOptions(MobRegistry.Textures.bossPortal);
      this.addSphere(var11, var12 + var13, 0L, 1000, (float)var14 * 0.68F, (float)var14, this.color1, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 900, (float)var14 * 0.68F, (float)var14, this.color1, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 800, (float)var14 * 0.64F, (float)var14 * 0.88F, this.color2, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 760, (float)var14 * 0.64F, (float)var14 * 0.88F, this.color2, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 700, (float)var14 * 0.64F, (float)var14 * 0.88F, this.color2, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 740, (float)var14 * 0.48F, (float)var14 * 0.68F, this.color3, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 680, (float)var14 * 0.48F, (float)var14 * 0.68F, this.color3, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 650, (float)var14 * 0.48F, (float)var14 * 0.68F, this.color3, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 630, (float)var14 * 0.28F, (float)var14 * 0.52F, this.color4, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 580, (float)var14 * 0.28F, (float)var14 * 0.52F, this.color4, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 550, (float)var14 * 0.28F, (float)var14 * 0.52F, this.color4, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 540, (float)var14 * 0.16F, (float)var14 * 0.36F, this.color5, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 520, (float)var14 * 0.16F, (float)var14 * 0.36F, this.color5, var10, var15);
      this.addSphere(var11, var12 + var13, 0L, 490, (float)var14 * 0.16F, (float)var14 * 0.36F, this.color5, var10, var15);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
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
      int var6 = var3 - 10;
      byte var7 = 32;
      SharedTextureDrawOptions var8 = new SharedTextureDrawOptions(MobRegistry.Textures.bossPortal);
      this.addSphere(var2, var6, 0L, 1000, (float)var7 * 0.68F, (float)var7, this.color1, var4, var8);
      this.addSphere(var2, var6, 0L, 900, (float)var7 * 0.68F, (float)var7, this.color1, var4, var8);
      this.addSphere(var2, var6, 0L, 800, (float)var7 * 0.64F, (float)var7 * 0.88F, this.color2, var4, var8);
      this.addSphere(var2, var6, 0L, 760, (float)var7 * 0.64F, (float)var7 * 0.88F, this.color2, var4, var8);
      this.addSphere(var2, var6, 0L, 700, (float)var7 * 0.64F, (float)var7 * 0.88F, this.color2, var4, var8);
      this.addSphere(var2, var6, 0L, 740, (float)var7 * 0.48F, (float)var7 * 0.68F, this.color3, var4, var8);
      this.addSphere(var2, var6, 0L, 680, (float)var7 * 0.48F, (float)var7 * 0.68F, this.color3, var4, var8);
      this.addSphere(var2, var6, 0L, 650, (float)var7 * 0.48F, (float)var7 * 0.68F, this.color3, var4, var8);
      this.addSphere(var2, var6, 0L, 630, (float)var7 * 0.28F, (float)var7 * 0.52F, this.color4, var4, var8);
      this.addSphere(var2, var6, 0L, 580, (float)var7 * 0.28F, (float)var7 * 0.52F, this.color4, var4, var8);
      this.addSphere(var2, var6, 0L, 550, (float)var7 * 0.28F, (float)var7 * 0.52F, this.color4, var4, var8);
      this.addSphere(var2, var6, 0L, 540, (float)var7 * 0.16F, (float)var7 * 0.36F, this.color5, var4, var8);
      this.addSphere(var2, var6, 0L, 520, (float)var7 * 0.16F, (float)var7 * 0.36F, this.color5, var4, var8);
      this.addSphere(var2, var6, 0L, 490, (float)var7 * 0.16F, (float)var7 * 0.36F, this.color5, var4, var8);
      var8.draw();
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-12, -24, 24, 28);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName());
   }
}
