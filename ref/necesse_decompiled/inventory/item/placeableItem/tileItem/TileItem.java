package necesse.inventory.item.placeableItem.tileItem;

import java.util.Iterator;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
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
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class TileItem extends PlaceableItem {
   public int tileID;
   private boolean isBucket;

   public TileItem(GameTile var1) {
      super(var1.stackSize, true);
      this.tileID = var1.getID();
      this.controllerIsTileBasedPlacing = true;
      this.dropsAsMatDeathPenalty = true;
      this.setItemCategory(var1.itemCategoryTree);
      this.keyWords.add("tile");
      this.isBucket = var1.isLiquid;
      Iterator var2 = var1.itemGlobalIngredients.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.addGlobalIngredient(new String[]{var3});
      }

   }

   public void loadItemTextures() {
      this.itemTexture = this.getTile().generateItemTexture();
   }

   public GameMessage getNewLocalization() {
      return this.getTile().getLocalization();
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)this.getTile().getItemTooltips(var1, var2));
      return var4;
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      int var7 = var3 / 32;
      int var8 = var4 / 32;
      int var9 = var2.getTileID(var7, var8);
      var1.putNextShortUnsigned(var9);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var6 == null) {
         Packet var7 = new Packet();
         this.setupAttackContentPacket(new PacketWriter(var7), var1, var2, var3, var4, var5);
         var6 = new PacketReader(var7);
      }

      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var12 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var12, var8)) {
            return "protected";
         } else {
            int var9 = var6.getNextShortUnsigned();
            if (var4.isServerClient()) {
               ServerClient var10 = var4.getServerClient();
               int var11 = var1.getTileID(var12, var8);
               if (var9 != var11) {
                  var10.sendPacket(new PacketChangeTile(var1, var12, var8, var11));
               }
            }

            if (var4.getPositionPoint().distance((double)(var12 * 32 + 16), (double)(var8 * 32 + 16)) > (double)this.getPlaceRange(var5, var4)) {
               return "outofrange";
            } else {
               String var13 = this.getTile().canPlace(var1, var12, var8);
               return var13 != null && !this.canReplace(var1, var12, var8, var4, var5, var13) ? var13 : null;
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

      int var14 = var2 / 32;
      int var8 = var3 / 32;
      var6.getNextShortUnsigned();
      GameTile var9 = this.getTile();
      boolean var10 = false;
      if (var4 != null && var9.canPlace(var1, var14, var8) != null) {
         if (!this.runReplaceDamageTile(var1, var2, var3, var14, var8, var4, var5)) {
            return var5;
         }

         if (var9.canPlace(var1, var14, var8) != null) {
            return var5;
         }

         var10 = true;
      }

      ItemPlaceEvent var11 = new ItemPlaceEvent(var1, var14, var8, var4, var5);
      GameEvents.triggerEvent(var11);
      if (!var11.isPrevented()) {
         boolean var12 = true;
         if (!var1.isClient()) {
            ServerClient var13 = var4 == null ? null : var4.getServerClient();
            var12 = this.onPlaceTile(var9, var1, var14, var8, var13, var5);
            if (var12) {
               if (var1.isServer()) {
                  var1.getServer().network.sendToClientsWithTile(new PacketPlaceTile(var1, var13, this.tileID, var14, var8), var1, var14, var8);
               }

               if (var13 != null) {
                  var13.newStats.tiles_placed.increment(1);
               }

               var1.getLevelTile(var14, var8).checkAround();
               var1.getLevelObject(var14, var8).checkAround();
            } else {
               var1.entityManager.pickups.add(var5.copy(1).getPickupEntity(var1, (float)(var14 * 32 + 16), (float)(var8 * 32 + 16)));
            }
         }

         if (this.isBucket && var12) {
            InventoryItem var15 = new InventoryItem("bucket");
            if (var5.getAmount() <= 1 && this.singleUse) {
               return var15;
            }

            if (!var1.isClient()) {
               if (var4 != null) {
                  var4.getInv().addItemsDropRemaining(var15, "addback", var4, false, false);
               } else {
                  var1.entityManager.pickups.add(var15.getPickupEntity(var1, (float)(var14 * 32 + 16), (float)(var8 * 32 + 16)));
               }
            }

            if (this.singleUse) {
               var5.setAmount(var5.getAmount() - 1);
            }
         } else if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public boolean onPlaceTile(GameTile var1, Level var2, int var3, int var4, ServerClient var5, InventoryItem var6) {
      var1.placeTile(var2, var3, var4);
      return true;
   }

   public boolean canReplace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6) {
      return this.getTile().canReplace(var1, var2, var3) && this.getBestToolDamageItem(var1, var2, var3, var4) != null;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      this.getTile().attemptPlace(var1, var2 / 32, var3 / 32, var4, var7);
      return var5;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public GameTile getTile() {
      return TileRegistry.getTile(this.tileID);
   }

   public void refreshLight(Level var1, float var2, float var3, InventoryItem var4) {
      GameTile var5 = this.getTile();
      if (var5.getLightLevel() >= 100) {
         var1.lightManager.refreshParticleLightFloat(var2, var3, var5.lightHue, var5.lightSat);
      }

   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         float var8 = 0.5F;
         int var9 = var2 / 32;
         int var10 = var3 / 32;
         this.getTile().drawPreview(var1, var9, var10, var8, var5, var4);
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
            if ((var5 == null || var5.getToolDps(var6, var4) < var10.getToolDps(var9, var4)) && var10.canDamageTile(var1, var2, var3, var4, var9) && var10.getToolType().canDealDamageTo(ToolType.SHOVEL) && var10.isTileInRange(var1, var2, var3, var4, var9)) {
               var5 = var10;
               var6 = var9;
               var7 = new PlayerInventorySlot(var4.getInv().main, var8);
            }
         }
      }

      return var7;
   }

   protected boolean runReplaceDamageTile(Level var1, int var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7) {
      ServerClient var8 = var6.isServerClient() ? var6.getServerClient() : null;
      int var9 = var1.getTileID(var4, var5);
      if (var9 != TileRegistry.dirtID && var9 != TileRegistry.emptyID) {
         PlayerInventorySlot var10 = this.getBestToolDamageItem(var1, var4, var5, var6);
         if (var10 == null) {
            return false;
         } else {
            InventoryItem var11 = var10.getItem(var6.getInv());
            ToolDamageItem var12 = (ToolDamageItem)var11.item;
            float var13 = var12.getMiningSpeedModifier(var11, var6);
            int var14 = (int)((float)var12.getToolDps(var11, var6) * ((float)this.getAttackAnimTime(var7, var6) / 1000.0F) * var13);
            ToolType var15 = var12.getToolType();
            int var16 = var12.getToolTier(var11);
            TileDamageResult var17 = var1.entityManager.doDamage(var4, var5, var14, var15, var16, var8, true, var2, var3);
            return var17 != null && var17.destroyed;
         }
      } else {
         return false;
      }
   }
}
