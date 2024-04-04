package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWorldPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HomePortalMob extends Mob {
   public long ownerAuth = -1L;
   public MobWorldPosition target;
   public boolean checkOwner;

   public HomePortalMob() {
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

   public void setupPortal(ServerClient var1, Mob var2) {
      this.setFollowing(var1, false);
      this.ownerAuth = var1.authentication;
      this.target = new MobWorldPosition(var2);
   }

   public void init() {
      super.init();
      this.dir = 0;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("ownerAuth", this.ownerAuth);
      if (this.target != null) {
         var1.addSaveData(this.target.getSaveData("target"));
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.ownerAuth = var1.getLong("ownerAuth", this.ownerAuth, false);
      LoadData var2 = var1.getFirstLoadDataByName("target");
      if (var2 != null) {
         this.target = new MobWorldPosition(var2);
      }

      this.checkOwner = true;
   }

   public boolean canPushMob(Mob var1) {
      return false;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 280.0F, 0.7F);

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
         Color var11 = new Color(61, 13, 150);
         Color var12 = new Color(93, 3, 255);
         Color var13 = new Color(113, 71, 255);
         Color var14 = (Color)GameRandom.globalRandom.getOneOf((Object[])(var11, var12, var13));
         this.getLevel().entityManager.addParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(var14).fadesAlphaTime(100, 50).lifeTime(var9);
      }

      for(var1 = 0; var1 < 2; ++var1) {
         var2 = GameRandom.globalRandom.getIntBetween(500, 1000);
         float var15 = (float)var2 / 1000.0F;
         var4 = (float)(5 + GameRandom.globalRandom.nextInt(5));
         var5 = var4 + (float)GameRandom.globalRandom.getIntBetween(20, 50) * var15;
         Color var16 = new Color(61, 13, 150);
         Color var17 = new Color(93, 3, 255);
         Color var18 = new Color(113, 71, 255);
         Color var19 = (Color)GameRandom.globalRandom.getOneOf((Object[])(var16, var17, var18));
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), this.y + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(6, 12, 100, var2 - 100).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), 0.7F).heightMoves(var4, var5).color(var19).lifeTime(var2);
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.checkOwner) {
         Server var1 = this.getLevel().getServer();
         ServerClient var2 = var1.getClientByAuth(this.ownerAuth);
         this.setFollowing(var2, false);
         if (var2 != null && var2.homePortals.stream().noneMatch((var1x) -> {
            return var1x.isLevel(this.getLevel()) && var1x.mobUniqueID == this.getUniqueID();
         })) {
            this.target = null;
         }

         this.checkOwner = false;
      }

      if (!this.isFollowing() || this.target == null || this.ownerAuth == -1L) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (var1 != null && var1.isServerClient() && !var1.buffManager.hasBuff("teleportsickness")) {
         ServerClient var2 = var1.getServerClient();
         TeleportEvent var3 = new TeleportEvent(var2, 0, this.target.levelIdentifier, 3.0F, (Function)null, (var3x) -> {
            Mob var4 = (Mob)var3x.entityManager.mobs.get(this.target.mobUniqueID, false);
            if (var4 != null) {
               Point var5 = new Point((int)var4.x, (int)var4.y + 4);
               if (var3x.collides((Shape)var1.getCollision(var5.x, var5.y), (CollisionFilter)var1.getLevelCollisionFilter())) {
                  var5.y -= 4;
               }

               return new TeleportResult(true, this.target.levelIdentifier, var5.x, var5.y);
            } else {
               var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "homeportalinvalid")));
               this.remove();
               return new TeleportResult(false, (Point)null);
            }
         });
         this.getLevel().entityManager.addLevelEventHidden(var3);
      }

   }

   public boolean canInteract(Mob var1) {
      return true;
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return Localization.translate("controls", "usetip");
   }

   public void onFollowingAnotherLevel(PlayerMob var1) {
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

      Color var14 = new Color(61, 13, 150);
      Color var15 = new Color(93, 3, 255);
      Color var16 = new Color(113, 71, 255);
      Color var17 = new Color(198, 152, 244);
      Color var18 = new Color(244, 236, 253);
      byte var19 = 50;
      final SharedTextureDrawOptions var20 = new SharedTextureDrawOptions(MobRegistry.Textures.portalSphere);
      this.addSphere(var11, var12 + var13, 0L, 1000, var19 - 16, var19, var14, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 900, var19 - 16, var19, var14, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 800, var19 - 18, var19 - 6, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 760, var19 - 18, var19 - 6, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 700, var19 - 18, var19 - 6, var15, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 740, var19 - 26, var19 - 16, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 680, var19 - 26, var19 - 16, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 650, var19 - 26, var19 - 16, var16, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 630, var19 - 36, var19 - 24, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 580, var19 - 36, var19 - 24, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 550, var19 - 36, var19 - 24, var17, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 540, var19 - 42, var19 - 32, var18, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 520, var19 - 42, var19 - 32, var18, var10, var20);
      this.addSphere(var11, var12 + var13, 0L, 490, var19 - 42, var19 - 32, var18, var10, var20);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var20.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected void addSphere(int var1, int var2, long var3, int var5, int var6, int var7, Color var8, GameLight var9, SharedTextureDrawOptions var10) {
      float var11 = GameUtils.getAnimFloat(this.getWorldEntity().getLocalTime() + var3, var5);
      int var12 = var7 - var6;
      int var13 = var6 + (int)((float)var12 * var11);
      var10.addFull().color(var8).alpha(0.5F).light(var9).size(var13).posMiddle(var1, var2, true);
   }

   protected GameMessage getSummonLocalization() {
      return super.getLocalization();
   }

   public GameMessage getLocalization() {
      if (this.getLevel() == null) {
         return super.getLocalization();
      } else {
         PlayerMob var1 = null;
         if (this.isServer()) {
            var1 = this.getFollowingServerPlayer();
         } else if (this.isClient()) {
            var1 = this.getFollowingClientPlayer();
         }

         return (GameMessage)(var1 != null ? new LocalMessage("mob", "spawnedname", new Object[]{"player", var1.getDisplayName(), "mob", this.getSummonLocalization()}) : super.getLocalization());
      }
   }
}
