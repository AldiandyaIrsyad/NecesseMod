package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FeedingTroughObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class FeedingTroughObject extends GameObject {
   public GameTexture texture;
   protected int counterID;

   public FeedingTroughObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(150, 119, 70);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/feedingtrough");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 5, var3 * 32, 22, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 22);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 5, var3 * 32 + 6, 22, 26) : new Rectangle(var2 * 32, var3 * 32 + 6, 26, 22);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = false;
      ObjectEntity var14 = var3.entityManager.getObjectEntity(var4, var5);
      if (var14 instanceof FeedingTroughObjectEntity) {
         var13 = ((FeedingTroughObjectEntity)var14).hasFeed();
      }

      final DrawOptionsList var15 = new DrawOptionsList();
      if (var12 == 0) {
         var15.add(this.texture.initDraw().sprite(2, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var15.add(this.texture.initDraw().sprite(3, 2, 32).light(var9).pos(var10, var11));
         }
      } else if (var12 == 1) {
         var15.add(this.texture.initDraw().sprite(0, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var15.add(this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11 - 32));
            var15.add(this.texture.initDraw().sprite(0, 1, 32).light(var9).pos(var10, var11));
         }
      } else if (var12 == 2) {
         var15.add(this.texture.initDraw().sprite(2, 1, 32).light(var9).pos(var10, var11));
         if (var13) {
            var15.add(this.texture.initDraw().sprite(3, 0, 32).light(var9).pos(var10, var11 - 32));
            var15.add(this.texture.initDraw().sprite(3, 1, 32).light(var9).pos(var10, var11));
         }
      } else {
         var15.add(this.texture.initDraw().sprite(1, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var15.add(this.texture.initDraw().sprite(1, 0, 32).light(var9).pos(var10, var11 - 32));
            var15.add(this.texture.initDraw().sprite(1, 1, 32).light(var9).pos(var10, var11));
         }
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      if (var4 == 0) {
         this.texture.initDraw().sprite(2, 2, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(2, 1, 32).alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 1) {
         this.texture.initDraw().sprite(0, 2, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(1, 2, 32).alpha(var5).draw(var8 + 32, var9);
      } else if (var4 == 2) {
         this.texture.initDraw().sprite(2, 1, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(2, 2, 32).alpha(var5).draw(var8, var9 + 32);
      } else {
         this.texture.initDraw().sprite(1, 2, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(0, 2, 32).alpha(var5).draw(var8 - 32, var9);
      }

   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FeedingTroughObjectEntity(var1, var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "feedingtroughtip"));
      return var3;
   }

   public static int[] registerFeedingTrough() {
      FeedingTroughObject var0;
      int var2 = ObjectRegistry.registerObject("feedingtrough", var0 = new FeedingTroughObject(), 20.0F, true);
      FeedingTrough2Object var1;
      int var3 = ObjectRegistry.registerObject("feedingtrough2", var1 = new FeedingTrough2Object(), 0.0F, false);
      var0.counterID = var3;
      var1.counterID = var2;
      return new int[]{var2, var3};
   }
}
