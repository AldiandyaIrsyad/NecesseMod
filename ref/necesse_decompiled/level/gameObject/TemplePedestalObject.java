package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.entity.mobs.hostile.bosses.GritHead;
import necesse.entity.mobs.hostile.bosses.SageAndGritStartMob;
import necesse.entity.mobs.hostile.bosses.SageHead;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TemplePedestalObject extends GameObject {
   protected GameTexture texture;
   protected boolean hasRotation;
   protected int yOffset = -3;

   public TemplePedestalObject() {
      super(new Rectangle(2, 5, 28, 22));
      this.toolType = ToolType.UNBREAKABLE;
      this.mapColor = new Color(130, 105, 52);
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/templepedestal");
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      int var4 = (int)((double)var1.getWorldEntity().getTime() / 1.5 % 360.0);
      Rectangle var5 = GameUtils.rangeTileBounds(var2 * 32 + 16, var3 * 32 + 16, 50);
      if (var1.entityManager.mobs.streamInRegionsShape(var5, 0).anyMatch((var0) -> {
         return var0 instanceof GritHead;
      })) {
         this.spawnParticles(var1, var2, var3, (float)var4, FlyingSpiritsHead.Variant.GRIT.particleHue);
      }

      if (var1.entityManager.mobs.streamInRegionsShape(var5, 0).anyMatch((var0) -> {
         return var0 instanceof SageHead;
      })) {
         this.spawnParticles(var1, var2, var3, (float)(var4 + 180), FlyingSpiritsHead.Variant.SAGE.particleHue);
      }

   }

   protected void spawnParticles(Level var1, int var2, int var3, float var4, float var5) {
      var1.lightManager.refreshParticleLightFloat((float)(var2 * 32 + 16), (float)(var3 * 32 + 16), var5, 0.8F);
      Point2D.Float var6 = GameMath.getAngleDir(var4);

      for(int var7 = 0; var7 < 2; ++var7) {
         int var8 = GameRandom.globalRandom.getIntBetween(10, 20);
         int var9 = (int)(36.0F + var6.y * (float)var8 * 0.2F);
         var1.entityManager.addParticle((float)(var2 * 32 + 16) + var6.x * (float)var8, (float)(var3 * 32 + 16), var7 == 0 ? Particle.GType.CRITICAL : Particle.GType.IMPORTANT_COSMETIC).heightMoves((float)var9, (float)(var9 + 16)).color(ParticleOption.randomizeColor(var5, 0.8F, 0.6F, 0.0F, 0.0F, 0.1F)).sizeFades(8, 14).lifeTime(1500).onProgress(0.8F, (var3x) -> {
            for(int var4 = 0; var4 < GameRandom.globalRandom.getIntBetween(1, 2); ++var4) {
               var1.entityManager.addParticle(var3x.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), var3x.y, Particle.GType.COSMETIC).smokeColor(var5).sizeFades(6, 10).heightMoves((float)(var9 + 14), (float)(var9 + 36));
            }

         });
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - this.texture.getHeight() + 32;
      final TextureDrawOptionsEnd var12;
      int var13;
      if (this.hasRotation) {
         var13 = var3.getObjectRotation(var4, var5);
         int var14 = this.texture.getWidth() / 4;
         int var15 = (var14 - 32) / 2;
         var12 = this.texture.initDraw().sprite(var13 % 4, 0, var14, this.texture.getHeight()).light(var9).pos(var10 - var15, var11 + this.yOffset);
      } else {
         var13 = (this.texture.getWidth() - 32) / 2;
         var12 = this.texture.initDraw().light(var9).pos(var10 - var13, var11 + this.yOffset);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - this.texture.getHeight() + 32;
      int var10;
      if (this.hasRotation) {
         var10 = this.texture.getWidth() / 4;
         int var11 = (var10 - 32) / 2;
         this.texture.initDraw().sprite(var4 % 4, 0, var10, this.texture.getHeight()).alpha(var5).draw(var8 - var11, var9 + this.yOffset);
      } else {
         var10 = (this.texture.getWidth() - 32) / 2;
         this.texture.initDraw().alpha(var5).draw(var8 - var10, var9 + this.yOffset);
      }

   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "activatetip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      super.interact(var1, var2, var3, var4);
      Item var5 = ItemRegistry.getItem("dragonsouls");
      if (!var4.isItemOnCooldown(var5)) {
         GameMessage var6 = null;
         if (var1 instanceof IncursionLevel) {
            var6 = ((IncursionLevel)var1).canSummonBoss("sageandgrit");
            if (var6 != null && var4.isServerClient()) {
               var4.getServerClient().sendChatMessage(var6);
            }
         }

         if (var6 == null && var4.getInv().removeItems(var5, 1, false, false, false, "use") > 0) {
            var4.startItemCooldown(var5, 2000);
            if (var1.isServer()) {
               System.out.println("Flying Spirits has been summoned at " + var1.getIdentifier() + ".");
               SageAndGritStartMob var7 = (SageAndGritStartMob)MobRegistry.getMob("sageandgrit", var1);
               var7.pedestalPosition = new Point(var2, var3);
               var1.entityManager.addMob(var7, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16));
               var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("grit"))), (Level)var1);
               var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("sage"))), (Level)var1);
               if (var1 instanceof IncursionLevel) {
                  ((IncursionLevel)var1).onBossSummoned(var7);
               }
            }
         } else if (var6 == null && var1.isServer() && var4.isServerClient()) {
            var4.getServerClient().sendChatMessage((GameMessage)(new LocalMessage("misc", "bossmissingitem")));
         }
      }

   }
}
