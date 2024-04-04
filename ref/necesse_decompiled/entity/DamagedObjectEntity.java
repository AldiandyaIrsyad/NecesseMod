package necesse.entity;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.GameEvents;
import necesse.engine.events.players.DamageTileEvent;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.packet.PacketTileDestroyed;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public class DamagedObjectEntity extends Entity {
   public int tileDamage;
   public int objectDamage;
   public long lastDamageTime;

   public DamagedObjectEntity(Level var1, int var2, int var3) {
      this.setLevel(var1);
      this.x = (float)var2;
      this.y = (float)var3;
      this.tileDamage = 0;
      this.objectDamage = 0;
      this.lastDamageTime = this.getWorldEntity().getTime();
   }

   public int getTileX() {
      return this.getX();
   }

   public int getTileY() {
      return this.getY();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void clientTick() {
   }

   public void serverTick() {
      if (!this.removed()) {
         if (this.getWorldEntity().getTime() - this.lastDamageTime > 10000L) {
            this.remove();
         }

         this.checkTileDamage((ServerClient)null, (ArrayList)null);
         this.checkObjectDamage((ServerClient)null, (ArrayList)null);
      }
   }

   public boolean hasDamage() {
      return this.tileDamage != 0 || this.objectDamage != 0;
   }

   public boolean checkObjectDamage(ServerClient var1, ArrayList<ItemPickupEntity> var2) {
      int var3 = this.getTileX();
      int var4 = this.getTileY();
      GameObject var5 = this.getLevel().getObject(var3, var4);
      if (this.objectDamage >= var5.objectHealth) {
         var5.onDestroyed(this.getLevel(), var3, var4, var1, var2 == null ? new ArrayList() : var2);
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithTile(new PacketTileDestroyed(this.getLevel(), var3, var4, var5.getID(), false), this.getLevel(), var3, var4);
         }

         this.objectDamage = 0;
         this.getLevel().getLevelTile(var3, var4).checkAround();
         this.getLevel().getLevelObject(var3, var4).checkAround();
         if (this.tileDamage == 0 && this.objectDamage == 0) {
            this.remove();
         }

         if (this.isServer()) {
            this.getLevel().onObjectDestroyed(var5, var3, var4, var1, var2);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean checkTileDamage(ServerClient var1, ArrayList<ItemPickupEntity> var2) {
      int var3 = this.getTileX();
      int var4 = this.getTileY();
      GameTile var5 = this.getLevel().getTile(var3, var4);
      if (this.tileDamage >= var5.tileHealth) {
         var5.onDestroyed(this.getLevel(), var3, var4, var1, var2 == null ? new ArrayList() : var2);
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithTile(new PacketTileDestroyed(this.getLevel(), var3, var4, var5.getID(), true), this.getLevel(), var3, var4);
         }

         this.tileDamage = 0;
         this.getLevel().getLevelTile(var3, var4).checkAround();
         this.getLevel().getLevelObject(var3, var4).checkAround();
         if (this.tileDamage == 0 && this.objectDamage == 0) {
            this.remove();
         }

         return true;
      } else {
         return false;
      }
   }

   public TileDamageResult doDamageOverride(int var1, TileDamageType var2) {
      int var3 = this.getTileX();
      int var4 = this.getTileY();
      ArrayList var5;
      boolean var6;
      LevelTile var7;
      LevelObject var8;
      if (var2 == TileDamageType.Tile) {
         this.tileDamage += var1;
         var5 = new ArrayList();
         var6 = false;
         var7 = this.getLevel().getLevelTile(var3, var4);
         var8 = this.getLevel().getLevelObject(var3, var4);
         if (!this.isClient()) {
            var6 = this.checkTileDamage((ServerClient)null, var5);
         }

         return new TileDamageResult(this, var7, var8, var2, var1, var6, var5, false, 0, 0);
      } else if (var2 == TileDamageType.Object) {
         this.objectDamage += var1;
         var5 = new ArrayList();
         var6 = false;
         var7 = this.getLevel().getLevelTile(var3, var4);
         var8 = this.getLevel().getLevelObject(var3, var4);
         if (!this.isClient()) {
            var6 = this.checkObjectDamage((ServerClient)null, var5);
         }

         return new TileDamageResult(this, var7, var8, var2, var1, var6, var5, false, 0, 0);
      } else {
         return null;
      }
   }

   public TileDamageResult doDamage(int var1, TileDamageType var2, int var3, ServerClient var4) {
      return this.doDamage(var1, (TileDamageType)var2, var3, var4, false, 0, 0);
   }

   public TileDamageResult doDamage(int var1, TileDamageType var2, int var3, ServerClient var4, boolean var5, int var6, int var7) {
      int var8 = this.getTileX();
      int var9 = this.getTileY();
      DamageTileEvent var10 = new DamageTileEvent(this.getLevel(), var8, var9, var1, var2, var3, var4);
      GameEvents.triggerEvent(var10);
      if (!var10.isPrevented()) {
         this.lastDamageTime = this.getWorldEntity().getTime();
         boolean var12;
         if (var2 == TileDamageType.Tile) {
            GameTile var11 = this.getLevel().getTile(var8, var9);
            if (var11.getID() != 0 && var11.canBeMined) {
               if (this.getLevel().isProtected(var8, var9) || var3 != -1 && var11.toolTier > var3) {
                  var1 = 0;
               }

               var12 = var11.onDamaged(this.getLevel(), var8, var9, var1, var4, var5, var6, var7);
               if (!var12) {
                  var1 = 0;
               }

               this.tileDamage += var1;
               ArrayList var13 = new ArrayList();
               boolean var14 = false;
               LevelTile var15 = this.getLevel().getLevelTile(var8, var9);
               LevelObject var16 = this.getLevel().getLevelObject(var8, var9);
               if (this.isServer()) {
                  if (var4 != null) {
                     this.getLevel().getServer().network.sendToClientsWithTileExcept(new PacketTileDamage(this.getLevel(), var2, var8, var9, var1, var5, var6, var7), this.getLevel(), var8, var9, var4);
                  } else {
                     this.getLevel().getServer().network.sendToClientsWithTile(new PacketTileDamage(this.getLevel(), var2, var8, var9, var1, var5, var6, var7), this.getLevel(), var8, var9);
                  }
               }

               if (!this.isClient()) {
                  var14 = this.checkTileDamage(var4, var13);
               }

               return new TileDamageResult(this, var15, var16, var2, var1, var14, var13, var5, var6, var7);
            }
         } else if (var2 == TileDamageType.Object) {
            GameObject var19 = this.getLevel().getObject(var8, var9);
            if (var19.getID() != 0 && var19.toolType != ToolType.UNBREAKABLE) {
               if (!var19.canPlaceOnProtectedLevels && this.getLevel().isProtected(var8, var9) || var3 != -1 && var19.toolTier > var3) {
                  var1 = 0;
               }

               if (var19.getMultiTile(this.getLevel(), var8, var9).streamOtherObjects(var8, var9).anyMatch((var1x) -> {
                  return !((GameObject)var1x.value).canPlaceOnProtectedLevels && this.getLevel().isProtected(var1x.tileX, var1x.tileY);
               })) {
                  var1 = 0;
               }

               var12 = var19.onDamaged(this.getLevel(), var8, var9, var1, var4, var5, var6, var7);
               if (!var12) {
                  var1 = 0;
               }

               this.objectDamage += var1;
               LevelObject var20 = var19.isMultiTileMaster() ? null : (LevelObject)var19.getMultiTile(this.getLevel(), var8, var9).getMasterLevelObject(this.getLevel(), var8, var9).orElse((Object)null);
               ArrayList var21 = new ArrayList();
               boolean var22 = false;
               LevelTile var23 = this.getLevel().getLevelTile(var8, var9);
               LevelObject var17 = this.getLevel().getLevelObject(var8, var9);
               if (this.isServer()) {
                  if (var4 != null) {
                     this.getLevel().getServer().network.sendToClientsWithTileExcept(new PacketTileDamage(this.getLevel(), var2, var8, var9, var1, var5, var6, var7), this.getLevel(), var8, var9, var4);
                  } else {
                     this.getLevel().getServer().network.sendToClientsWithTile(new PacketTileDamage(this.getLevel(), var2, var8, var9, var1, var5, var6, var7), this.getLevel(), var8, var9);
                  }
               }

               if (var20 != null) {
                  TileDamageResult var18 = this.getLevel().entityManager.doDamage(var20.tileX, var20.tileY, var1, var2, var3, var4, false, var6, var7);
                  var22 = var18.destroyed;
                  var21.addAll(var18.itemsDropped);
               }

               if (!this.isClient()) {
                  var22 = this.checkObjectDamage(var4, var21) || var22;
               }

               return new TileDamageResult(this, var23, var17, var2, var1, var22, var21, var5, var6, var7);
            }
         }
      }

      return null;
   }

   public TileDamageResult doDamage(int var1, ToolType var2, int var3, ServerClient var4, boolean var5, int var6, int var7) {
      int var8 = this.getTileX();
      int var9 = this.getTileY();
      if (var2 != ToolType.AXE && var2 != ToolType.PICKAXE && (var2 != ToolType.ALL || this.getLevel().getObjectID(var8, var9) == 0)) {
         if (var2 == ToolType.SHOVEL || var2 == ToolType.ALL) {
            GameTile var11 = this.getLevel().getTile(var8, var9);
            if (var11.getID() != 0 && var11.canBeMined) {
               return this.doDamage(var1, TileDamageType.Tile, var3, var4, var5, var6, var7);
            }
         }
      } else {
         GameObject var10 = this.getLevel().getObject(var8, var9);
         if (var10.getID() != 0 && var10.toolType != ToolType.UNBREAKABLE) {
            if (!var2.canDealDamageTo(var10.toolType)) {
               var1 = 0;
            }

            return this.doDamage(var1, TileDamageType.Object, var3, var4, var5, var6, var7);
         }
      }

      return null;
   }
}
