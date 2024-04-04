package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class DoubleStreetlamp2Object extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;
   protected int counterID;

   public DoubleStreetlamp2Object(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.displayMapTooltip = true;
      this.lightLevel = 200;
      this.drawDamage = false;
      this.objectHealth = 50;
      this.canPlaceOnShore = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(1, 0, 2, 1, var1, false, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      byte var5;
      byte var6;
      if (var4 == 0) {
         var5 = 12;
         var6 = 16;
         return new Rectangle(var2 * 32, var3 * 32 + (32 - var6) / 2, var5, var6);
      } else if (var4 == 1) {
         var5 = 16;
         var6 = 12;
         return new Rectangle(var2 * 32 + (32 - var5) / 2, var3 * 32, var5, var6);
      } else if (var4 == 2) {
         var5 = 12;
         var6 = 16;
         return new Rectangle(var2 * 32 + 32 - var5, var3 * 32 + (32 - var6) / 2, var5, var6);
      } else {
         var5 = 16;
         var6 = 12;
         return new Rectangle(var2 * 32 + (32 - var5) / 2, var3 * 32 + 32 - var6, var5, var6);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "doublestreetlampSetup", (Runnable)(() -> {
         GameLight var6 = var3.getLightLevel(var4, var5);
         int var7x = var7.getTileDrawX(var4);
         int var8 = var7.getTileDrawY(var5);
         boolean var9 = this.isActive(var3, var4, var5);
         int var10 = var3.getObjectRotation(var4, var5) % 4;
         int var11 = this.texture.getHeight() / 2;
         final DrawOptionsList var12 = new DrawOptionsList();
         if (var10 == 0) {
            var12.add(this.texture.initDraw().sprite(1, var9 ? 0 : 1, 32, var11).light(var6).pos(var7x, var8 - var11 + 32));
         } else {
            int var13;
            if (var10 == 1) {
               var13 = var11 / 32;
               var12.add(this.texture.initDraw().sprite(2, var9 ? var13 - 1 : var13 * 2 - 1, 32, 32).light(var6).pos(var7x, var8));
            } else if (var10 == 2) {
               var12.add(this.texture.initDraw().sprite(0, var9 ? 0 : 1, 32, var11).light(var6).pos(var7x, var8 - var11 + 32));
            } else {
               var13 = var11 / 32;

               for(int var14 = 0; var14 < var13; ++var14) {
                  var12.add(this.texture.initDraw().sprite(2, var9 ? var14 : var14 + var13, 32, 32).light(var6).pos(var7x, var8 - var11 + 64 + var14 * 32));
               }
            }
         }

         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 16;
            }

            public void draw(TickManager var1) {
               DrawOptionsList var10002 = var12;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "doublestreetlampDraw", (Runnable)(var10002::draw));
            }
         });
      }));
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isActive(var1, var2, var3) ? this.lightLevel : 0;
   }

   public boolean isActive(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      return this.getMultiTile(var4).streamIDs(var2, var3).noneMatch((var1x) -> {
         return var1.wireManager.isWireActiveAny(var1x.tileX, var1x.tileY);
      });
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      byte var6 = var1.getObjectRotation(var2, var3);
      Rectangle var7 = this.getMultiTile(var6).getTileRectangle(var2, var3);
      var1.lightManager.updateStaticLight(var7.x, var7.y, var7.x + var7.width - 1, var7.y + var7.height - 1, true);
   }
}
