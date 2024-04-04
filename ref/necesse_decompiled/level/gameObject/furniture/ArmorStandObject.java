package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ArmorStandObjectEntity;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ArmorStandObject extends FurnitureObject {
   public GameTexture base;
   public GameTexture head;
   public GameTexture body;
   public GameTexture leftArms;
   public GameTexture rightArms;
   public GameTexture feet;

   public ArmorStandObject() {
      super(new Rectangle(3, 10, 26, 20));
      this.mapColor = new Color(150, 119, 70);
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "armorstand";
   }

   public void loadTextures() {
      super.loadTextures();
      this.base = GameTexture.fromFile("objects/armorstand_base");
      this.head = GameTexture.fromFile("objects/armorstand_head");
      this.body = GameTexture.fromFile("objects/armorstand_body");
      this.leftArms = GameTexture.fromFile("objects/armorstand_arms_left");
      this.rightArms = GameTexture.fromFile("objects/armorstand_arms_right");
      this.feet = GameTexture.fromFile("objects/armorstand_feet");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - 16;
      int var11 = var7.getTileDrawY(var5) - 32;
      int var12 = var3.getObjectRotation(var4, var5) % 4;
      ObjectEntity var13 = var3.entityManager.getObjectEntity(var4, var5);
      HumanDrawOptions var15 = (new HumanDrawOptions(var3)).headTexture(this.head).bodyTexture(this.body).leftArmsTexture(this.leftArms).rightArmsTexture(this.rightArms).feetTexture(this.feet).sprite(0, var12).dir(var12).light(var9);
      final DrawOptions var14;
      if (var13 != null && var13.implementsOEInventory()) {
         InventoryItem var16 = ((InventoryObjectEntity)var13).inventory.getItem(0);
         InventoryItem var17 = ((InventoryObjectEntity)var13).inventory.getItem(1);
         InventoryItem var18 = ((InventoryObjectEntity)var13).inventory.getItem(2);
         var14 = var15.helmet(var16).chestplate(var17).boots(var18).pos(var10, var11);
      } else {
         var14 = var15.pos(var10, var11);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 19;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var19 = this.base.initDraw().sprite(0, var12, 64).light(var9).pos(var10, var11 + 2);
      var2.add((var1x) -> {
         var19.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2) - 16;
      int var9 = var7.getTileDrawY(var3) - 32;
      this.base.initDraw().sprite(0, var4, 64).alpha(var5).draw(var8, var9 + 2);
      (new HumanDrawOptions(var1)).headTexture(this.head).bodyTexture(this.body).leftArmsTexture(this.leftArms).rightArmsTexture(this.rightArms).feetTexture(this.feet).sprite(0, var4).dir(var4).alpha(var5).draw(var8, var9);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 4, -20, 24, 20, 19));
      return var4;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.ARMOR_STAND_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new ArmorStandObjectEntity(var1, var2, var3);
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      if (!var1.settlementLayer.isActive()) {
         super.doExplosionDamage(var1, var2, var3, var4, var5, var6);
      }

   }
}
