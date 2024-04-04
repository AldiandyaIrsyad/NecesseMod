package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FeedingTroughObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class FeedingTrough2Object extends GameObject {
   public GameTexture texture;
   protected int counterID;

   public FeedingTrough2Object() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(150, 119, 70);
      this.toolType = ToolType.ALL;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 0, 1, 2, var1, false, new int[]{this.getID(), this.counterID});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/feedingtrough");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 5, var3 * 32 + 6, 22, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32 + 6, 26, 22);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 5, var3 * 32, 22, 26) : new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 22);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = false;
      LevelObject var14 = (LevelObject)this.getMultiTile(var12).getMasterLevelObject(var3, var4, var5).orElse((Object)null);
      if (var14 != null) {
         ObjectEntity var15 = var3.entityManager.getObjectEntity(var14.tileX, var14.tileY);
         if (var15 instanceof FeedingTroughObjectEntity) {
            var13 = ((FeedingTroughObjectEntity)var15).hasFeed();
         }
      }

      final DrawOptionsList var16 = new DrawOptionsList();
      if (var12 == 0) {
         var16.add(this.texture.initDraw().sprite(2, 1, 32).light(var9).pos(var10, var11));
         if (var13) {
            var16.add(this.texture.initDraw().sprite(3, 1, 32).light(var9).pos(var10, var11));
            var16.add(this.texture.initDraw().sprite(3, 0, 32).light(var9).pos(var10, var11 - 32));
         }
      } else if (var12 == 1) {
         var16.add(this.texture.initDraw().sprite(1, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var16.add(this.texture.initDraw().sprite(1, 0, 32).light(var9).pos(var10, var11 - 32));
            var16.add(this.texture.initDraw().sprite(1, 1, 32).light(var9).pos(var10, var11));
         }
      } else if (var12 == 2) {
         var16.add(this.texture.initDraw().sprite(2, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var16.add(this.texture.initDraw().sprite(3, 2, 32).light(var9).pos(var10, var11));
         }
      } else {
         var16.add(this.texture.initDraw().sprite(0, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var16.add(this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11 - 32));
            var16.add(this.texture.initDraw().sprite(0, 1, 32).light(var9).pos(var10, var11));
         }
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var16.draw();
         }
      });
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      this.getMultiTile(var1.getObjectRotation(var2, var3)).getMasterLevelObject(var1, var2, var3).ifPresent((var1x) -> {
         var1x.interact(var4);
      });
   }
}
