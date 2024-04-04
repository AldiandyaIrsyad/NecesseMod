package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class FallenAltarObject extends StaticMultiObject {
   public GameTexture emptyAltarTexture;
   public GameTexture portalTexture;

   private FallenAltarObject(int var1, int var2, int var3, int var4, int[] var5, Rectangle var6) {
      super(var1, var2, var3, var4, var5, var6, "fallenaltar");
      this.stackSize = 1;
      this.rarity = Item.Rarity.LEGENDARY;
      this.mapColor = new Color(75, 63, 81);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.emptyAltarTexture = GameTexture.fromFile("objects/" + this.texturePath + "empty");
      this.portalTexture = GameTexture.fromFile("objects/" + this.texturePath + "portal");
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "interacttip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer() && var4.isServerClient()) {
         LevelObject var5 = (LevelObject)this.getMultiTile(var1, var2, var3).getMasterLevelObject(var1, var2, var3).orElse((Object)null);
         if (var5 != null) {
            ObjectEntity var6 = var1.entityManager.getObjectEntity(var5.tileX, var5.tileY);
            if (var6 instanceof FallenAltarObjectEntity) {
               FallenAltarObjectEntity var7 = (FallenAltarObjectEntity)var6;
               PacketOpenContainer var8 = PacketOpenContainer.ObjectEntity(ContainerRegistry.FALLEN_ALTAR_CONTAINER, var6, FallenAltarContainer.getContainerContent(var1.getServer(), var7));
               ContainerRegistry.openAndSendContainer(var4.getServerClient(), var8);
            }
         }
      }

      super.interact(var1, var2, var3, var4);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameTexture var9 = this.texture;
      MultiTile var10 = this.getMultiTile(var3, var4, var5);
      LevelObject var11 = (LevelObject)var10.getMasterLevelObject(var3, var4, var5).orElse((Object)null);
      if (var11 != null) {
         ObjectEntity var12 = var3.entityManager.getObjectEntity(var11.tileX, var11.tileY);
         if (var12 instanceof FallenAltarObjectEntity) {
            FallenAltarObjectEntity var13 = (FallenAltarObjectEntity)var12;
            OpenIncursion var14 = var13.getOpenIncursion();
            int var16;
            int var17;
            GameLight var18;
            if (var14 != null) {
               var9 = this.emptyAltarTexture;
               if (this.isMultiTileMaster()) {
                  Point var15 = var10.getCenterLevelPos(var4, var5);
                  var16 = var7.getDrawX(var15.x);
                  var17 = var7.getDrawY(var15.y) - 8;
                  var18 = var3.getLightLevel(var15.x / 32, var15.y / 32).minLevelCopy(75.0F);
                  Color var19 = new Color(140, 82, 50);
                  Color var20 = new Color(150, 54, 13);
                  Color var21 = new Color(255, 91, 3);
                  Color var22 = new Color(255, 121, 3);
                  Color var23 = new Color(244, 184, 152);
                  Color var24 = new Color(253, 243, 236);
                  ArrayList var25 = var14.incursionData.getIncursionBiome().getFallenAltarGatewayColorsForBiome();
                  if (var25 != null) {
                     var19 = (Color)var25.get(0);
                     var20 = (Color)var25.get(1);
                     var21 = (Color)var25.get(2);
                     var22 = (Color)var25.get(3);
                     var23 = (Color)var25.get(4);
                     var24 = (Color)var25.get(5);
                  }

                  int var26 = Math.max(this.portalTexture.getWidth(), this.portalTexture.getHeight());
                  float var27 = 2.0F;
                  SharedTextureDrawOptions var28 = new SharedTextureDrawOptions(this.portalTexture);
                  this.addSphere(var3, var16, var17, 0L, (int)(1000.0F * var27), var26, var26, var19, 1.0F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(1000.0F * var27), var26 - (int)((float)var26 / 5.0F), var26, var20, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(900.0F * var27), var26 - (int)((float)var26 / 5.0F), var26, var20, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(800.0F * var27), var26 - (int)((float)var26 / 3.5F), var26 - (int)((float)var26 / 5.0F), var21, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(760.0F * var27), var26 - (int)((float)var26 / 3.5F), var26 - (int)((float)var26 / 5.0F), var21, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(700.0F * var27), var26 - (int)((float)var26 / 3.5F), var26 - (int)((float)var26 / 5.0F), var21, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(740.0F * var27), var26 - (int)((float)var26 / 2.5F), var26 - (int)((float)var26 / 4.0F), var22, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(680.0F * var27), var26 - (int)((float)var26 / 2.5F), var26 - (int)((float)var26 / 4.0F), var22, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(650.0F * var27), var26 - (int)((float)var26 / 2.5F), var26 - (int)((float)var26 / 4.0F), var22, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(630.0F * var27), var26 - (int)((float)var26 / 1.2F), var26 - (int)((float)var26 / 2.8F), var23, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(580.0F * var27), var26 - (int)((float)var26 / 1.2F), var26 - (int)((float)var26 / 2.8F), var23, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(550.0F * var27), var26 - (int)((float)var26 / 1.2F), var26 - (int)((float)var26 / 2.8F), var23, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(540.0F * var27), var26 - (int)((float)var26 / 1.05F), var26 - (int)((float)var26 / 1.5F), var24, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(520.0F * var27), var26 - (int)((float)var26 / 1.05F), var26 - (int)((float)var26 / 1.5F), var24, 0.5F, var18, var28);
                  this.addSphere(var3, var16, var17, 0L, (int)(490.0F * var27), var26 - (int)((float)var26 / 1.05F), var26 - (int)((float)var26 / 1.5F), var24, 0.5F, var18, var28);
                  var2.add((var1x) -> {
                     var28.draw();
                  });
               }
            }

            if (this.isMultiTileMaster()) {
               InventoryItem var30 = var13.inventory.getItem(0);
               if (var30 != null || var14 != null) {
                  if (var14 != null) {
                     var30 = new InventoryItem("gatewaytablet");
                     GatewayTabletItem.setIncursionData(var30, var14.incursionData);
                  }

                  var16 = var7.getTileDrawX(var4);
                  var17 = var7.getTileDrawY(var5);
                  var18 = var3.getLightLevel(var4, var5);
                  final DrawOptions var31 = var30.getWorldDrawOptions(var8, var16 + 16 + 32, var17 + 32 + 16, var18, 0.3F, 32);
                  var1.add(new LevelSortedDrawable(this, var4, var5) {
                     public int getSortY() {
                        return 64;
                     }

                     public void draw(TickManager var1) {
                        var31.draw();
                     }
                  });
               }
            }
         }
      }

      final DrawOptions var29 = this.getMultiTextureDrawOptions(var9, var3, var4, var5, var7);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var29.draw();
         }
      });
   }

   protected void addSphere(Level var1, int var2, int var3, long var4, int var6, int var7, int var8, Color var9, float var10, GameLight var11, SharedTextureDrawOptions var12) {
      float var13 = GameUtils.getAnimFloat(var1.getWorldEntity().getLocalTime() + var4, var6);
      int var14 = var8 - var7;
      int var15 = var7 + (int)((float)var14 * var13);
      var12.addFull().color(var9).alpha(var10).light(var11).size(var15).posMiddle(var2, var3, true);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return (ObjectEntity)(this.isMultiTileMaster() ? new FallenAltarObjectEntity(var1, var2, var3) : super.getNewObjectEntity(var1, var2, var3));
   }

   public static int[] registerFallenAltar() {
      int[] var0 = new int[6];
      Rectangle var1 = new Rectangle(12, 4, 72, 56);
      var0[0] = ObjectRegistry.registerObject("fallenaltar", new FallenAltarObject(0, 0, 3, 2, var0, var1), 0.0F, true);
      var0[1] = ObjectRegistry.registerObject("fallenaltar2", new FallenAltarObject(1, 0, 3, 2, var0, var1), 0.0F, false);
      var0[2] = ObjectRegistry.registerObject("fallenaltar3", new FallenAltarObject(2, 0, 3, 2, var0, var1), 0.0F, false);
      var0[3] = ObjectRegistry.registerObject("fallenaltar4", new FallenAltarObject(0, 1, 3, 2, var0, var1), 0.0F, false);
      var0[4] = ObjectRegistry.registerObject("fallenaltar5", new FallenAltarObject(1, 1, 3, 2, var0, var1), 0.0F, false);
      var0[5] = ObjectRegistry.registerObject("fallenaltar6", new FallenAltarObject(2, 1, 3, 2, var0, var1), 0.0F, false);
      return var0;
   }
}
