package necesse.inventory.item.placeableItem.objectItem;

import java.awt.Point;
import java.util.Iterator;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.TileDamageResult;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;

public class ObjectItem extends PlaceableItem {
   public int objectID;

   public ObjectItem(GameObject var1, boolean var2) {
      super(var1.stackSize, true);
      this.objectID = var1.getID();
      this.controllerIsTileBasedPlacing = true;
      this.rarity = var1.rarity;
      this.dropsAsMatDeathPenalty = var2;
      this.setItemCategory(var1.itemCategoryTree);
      this.keyWords.add("object");
      this.keyWords.addAll(var1.roomProperties);
      Iterator var3 = var1.itemGlobalIngredients.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.addGlobalIngredient(new String[]{var4});
      }

   }

   public ObjectItem(GameObject var1) {
      this(var1, true);
   }

   public void loadItemTextures() {
      this.itemTexture = this.getObject().generateItemTexture();
   }

   public GameMessage getNewLocalization() {
      return this.getObject().getLocalization();
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)this.getObject().getItemTooltips(var1, var2));
      return var4;
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      int var7 = var5.isAttacking ? var5.beforeAttackDir : var5.dir;
      GameObject var8 = this.getObject();
      int var9 = var8.getPlaceRotation(var2, var3, var4, var5, var7);
      Point var10 = var8.getPlaceOffset(var2, var3, var4, var5, var9);
      int var11 = (var3 + var10.x) / 32;
      int var12 = (var4 + var10.y) / 32;
      var1.putNextShortUnsigned(var11);
      var1.putNextShortUnsigned(var12);
      var1.putNextByteUnsigned(var9);
      int var13 = var2.getObjectID(var11, var12);
      var1.putNextShortUnsigned(var13);
      if (var13 != 0) {
         var1.putNextByteUnsigned(var2.getObjectRotation(var11, var12));
      }

   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      GameObject var7 = this.getObject();
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         if (var6 == null) {
            Packet var8 = new Packet();
            this.setupAttackContentPacket(new PacketWriter(var8), var1, var2, var3, var4, var5);
            var6 = new PacketReader(var8);
         }

         int var17 = var6.getNextShortUnsigned();
         int var9 = var6.getNextShortUnsigned();
         int var10 = var6.getNextByteUnsigned();
         if (!var7.canPlaceOnProtectedLevels && var1.isProtected(var17, var9)) {
            return "protected";
         } else {
            Point var11 = var7.getPlaceOffset(var1, var2, var3, var4, var10);
            int var12 = var6.getNextShortUnsigned();
            int var13 = 0;
            if (var12 != 0) {
               var13 = var6.getNextByteUnsigned();
            }

            if (var4.isServerClient()) {
               ServerClient var14 = var4.getServerClient();
               int var15 = var1.getObjectID(var17, var9);
               byte var16 = var15 == 0 ? 0 : var1.getObjectRotation(var17, var9);
               if (var12 != var15 || var13 != var16) {
                  var14.sendPacket(new PacketChangeObject(var1, var17, var9, var15, var16));
               }
            }

            MultiTile var18 = var7.getMultiTile(var10);
            if (var18.streamOtherObjects(var17, var9).anyMatch((var1x) -> {
               return var1x.tileX < 0 || var1x.tileY < 0 || var1x.tileX >= var1.width || var1x.tileY >= var1.height;
            })) {
               return "outsidelevel";
            } else if (var18.streamOtherObjects(var17, var9).anyMatch((var1x) -> {
               return !((GameObject)var1x.value).canPlaceOnProtectedLevels && var1.isProtected(var1x.tileX, var1x.tileY);
            })) {
               return "protected";
            } else if (var4.getPositionPoint().distance((double)(var17 * 32 + 16 - var11.x), (double)(var9 * 32 + 16 - var11.y)) > (double)this.getPlaceRange(var5, var4)) {
               return "outofrange";
            } else {
               String var19 = var7.canPlace(var1, var17, var9, var10);
               if (var19 != null && !this.canReplace(var1, var17, var9, var10, var4, var5, var19)) {
                  return var19;
               } else {
                  boolean var20 = false;
                  if (var1.isServer()) {
                     var20 = !var7.checkPlaceCollision(var1, var17, var9, var10, true);
                  } else if (var1.isClient()) {
                     var20 = !var7.checkPlaceCollision(var1, var17, var9, var10, true);
                  }

                  return !var20 ? "collision" : null;
               }
            }
         }
      } else {
         return "outsidelevel";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var6 == null) {
         Packet var7 = new Packet();
         this.setupAttackContentPacket(new PacketWriter(var7), var1, var2, var3, var4, var5);
         var6 = new PacketReader(var7);
      }

      int var15 = var6.getNextShortUnsigned();
      int var8 = var6.getNextShortUnsigned();
      int var9 = var6.getNextByteUnsigned();
      if (var6.getNextShortUnsigned() != 0) {
         var6.getNextByteUnsigned();
      }

      GameObject var10 = this.getObject();
      boolean var11 = false;
      if (var4 != null && var10.canPlace(var1, var15, var8, var9) != null) {
         if (!this.runReplaceDamageTile(var1, var2, var3, var15, var8, var9, var4, var5)) {
            return var5;
         }

         if (var10.canPlace(var1, var15, var8, var9) != null) {
            return var5;
         }

         var11 = true;
      }

      ItemPlaceEvent var12 = new ItemPlaceEvent(var1, var15, var8, var4, var5);
      GameEvents.triggerEvent(var12);
      if (!var12.isPrevented()) {
         if (!var1.isClient()) {
            ServerClient var13 = var4 == null ? null : var4.getServerClient();
            boolean var14 = this.onPlaceObject(var10, var1, var15, var8, var9, var13, var5);
            if (var14) {
               if (var1.isServer()) {
                  var1.getServer().network.sendToClientsWithTile(new PacketPlaceObject(var1, var13, this.objectID, var9, var15, var8), var1, var15, var8);
               }

               if (var13 != null) {
                  var13.newStats.objects_placed.increment(1);
               }

               var1.getTile(var15, var8).checkAround(var1, var15, var8);
               var1.getObject(var15, var8).checkAround(var1, var15, var8);
            } else {
               var1.entityManager.pickups.add(var5.copy(1).getPickupEntity(var1, (float)(var15 * 32 + 16), (float)(var8 * 32 + 16)));
            }
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public boolean onPlaceObject(GameObject var1, Level var2, int var3, int var4, int var5, ServerClient var6, InventoryItem var7) {
      var1.placeObject(var2, var3, var4, var5);
      return true;
   }

   public boolean canReplace(Level var1, int var2, int var3, int var4, PlayerMob var5, InventoryItem var6, String var7) {
      MultiTile var8 = this.getObject().getMultiTile(var4);
      return var8.streamObjects(var2, var3).allMatch((var1x) -> {
         return var1.getObjectID(var1x.tileX, var1x.tileY) == 0;
      }) ? false : var8.streamObjects(var2, var3).allMatch((var4x) -> {
         if (!((GameObject)var4x.value).canReplace(var1, var4x.tileX, var4x.tileY, var4)) {
            return false;
         } else if (var1.getObjectID(var4x.tileX, var4x.tileY) == 0) {
            return true;
         } else {
            return this.getBestToolDamageItem(var1, var4x.tileX, var4x.tileY, var5) != null;
         }
      });
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      int var8 = var6.getNextShortUnsigned();
      int var9 = var6.getNextShortUnsigned();
      this.getObject().attemptPlace(var1, var8, var9, var4, var7);
      return var5;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public GameObject getObject() {
      return ObjectRegistry.getObject(this.objectID);
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         GameObject var8 = this.getObject();
         int var9 = var8.getPlaceRotation(var1, var2, var3, var5, var5.isAttacking ? var5.beforeAttackDir : var5.dir);
         Point var10 = var8.getPlaceOffset(var1, var2, var3, var5, var9);
         float var11 = 0.5F;
         int var12 = (var2 + var10.x) / 32;
         int var13 = (var3 + var10.y) / 32;
         var8.drawPreview(var1, var12, var13, var9, var11, var5, var4);
      }

   }

   public boolean showWires() {
      return this.getObject().showsWire;
   }

   public void refreshLight(Level var1, float var2, float var3, InventoryItem var4) {
      GameObject var5 = this.getObject();
      if (var5.lightLevel >= 100) {
         var1.lightManager.refreshParticleLightFloat(var2, var3, var5.lightHue, var5.lightSat);
      }

   }

   protected PlayerInventorySlot getBestToolDamageItem(Level var1, int var2, int var3, PlayerMob var4) {
      ToolDamageItem var5 = null;
      InventoryItem var6 = null;
      PlayerInventorySlot var7 = null;

      for(int var8 = 0; var8 < var4.getInv().main.getSize(); ++var8) {
         InventoryItem var9 = var4.getInv().main.getItem(var8);
         if (var9 != null && var9.item instanceof ToolDamageItem) {
            ToolDamageItem var10 = (ToolDamageItem)var9.item;
            if ((var5 == null || var5.getToolDps(var6, var4) < var10.getToolDps(var9, var4)) && var10.canDamageTile(var1, var2, var3, var4, var9) && var10.getToolType() != ToolType.SHOVEL) {
               GameObject var11 = var1.getObject(var2, var3);
               if (var10.getToolType().canDealDamageTo(var11.toolType) && (var10.isTileInRange(var1, var2, var3, var4, var9) || !var11.isMultiTileMaster())) {
                  var5 = var10;
                  var6 = var9;
                  var7 = new PlayerInventorySlot(var4.getInv().main, var8);
               }
            }
         }
      }

      return var7;
   }

   protected boolean runReplaceDamageTile(Level var1, int var2, int var3, int var4, int var5, int var6, PlayerMob var7, InventoryItem var8) {
      ServerClient var9 = var7.isServerClient() ? var7.getServerClient() : null;
      MultiTile var10 = this.getObject().getMultiTile(var6);
      Iterator var11 = var10.getIDs(var4, var5).iterator();

      TileDamageResult var20;
      do {
         MultiTile.CoordinateValue var12;
         do {
            if (!var11.hasNext()) {
               return true;
            }

            var12 = (MultiTile.CoordinateValue)var11.next();
         } while(var1.getObjectID(var12.tileX, var12.tileY) == 0);

         PlayerInventorySlot var13 = this.getBestToolDamageItem(var1, var12.tileX, var12.tileY, var7);
         if (var13 == null) {
            return false;
         }

         InventoryItem var14 = var13.getItem(var7.getInv());
         ToolDamageItem var15 = (ToolDamageItem)var14.item;
         float var16 = var15.getMiningSpeedModifier(var14, var7);
         int var17 = (int)((float)var15.getToolDps(var14, var7) * ((float)this.getAttackAnimTime(var8, var7) / 1000.0F) * var16);
         ToolType var18 = var15.getToolType();
         int var19 = var15.getToolTier(var14);
         var20 = var1.entityManager.doDamage(var12.tileX, var12.tileY, var17, var18, var19, var9, true, var2, var3);
      } while(var20 != null && var20.destroyed);

      return false;
   }
}
