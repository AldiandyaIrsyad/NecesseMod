package necesse.inventory.item.matItem;

import java.util.List;
import java.util.Random;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class MultiTextureMatItem extends MatItem {
   private int spriteCount;

   public static void setCurrentSprite(GNDItemMap var0, int var1) {
      var0.setByte("currentSprite", (byte)var1);
   }

   public static GNDItemMap getGNDData(int var0) {
      GNDItemMap var1 = new GNDItemMap();
      setCurrentSprite(var1, var0);
      return var1;
   }

   public static InventoryItem generateItem(String var0, int var1) {
      InventoryItem var2 = new InventoryItem(var0);
      if (var2.item instanceof MultiTextureMatItem) {
         MultiTextureMatItem var3 = (MultiTextureMatItem)var2.item;
         var3.setCurrentSprite(var2, var1);
      }

      return var2;
   }

   public static InventoryItem generateRandomItem(String var0, Random var1) {
      return generateItem(var0, var1.nextInt());
   }

   public MultiTextureMatItem(int var1, int var2, String... var3) {
      super(var2, var3);
      this.spriteCount = var1;
   }

   public MultiTextureMatItem(int var1, int var2, Item.Rarity var3, String... var4) {
      super(var2, var3, var4);
      this.spriteCount = var1;
   }

   public MultiTextureMatItem(int var1, int var2, Item.Rarity var3, String var4) {
      super(var2, var3, var4);
      this.spriteCount = var1;
   }

   public MultiTextureMatItem(int var1, int var2, Item.Rarity var3, String var4, String... var5) {
      super(var2, var3, var4, var5);
      this.spriteCount = var1;
   }

   public void setCurrentSprite(InventoryItem var1, int var2) {
      setCurrentSprite(var1.getGndData(), var2);
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      int var3 = (var1.getGndData().getByte("currentSprite") & 255) % this.spriteCount;
      return new GameSprite(this.itemTexture, 0, var3, 32);
   }

   public boolean onCombine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, int var7, int var8, boolean var9, String var10, InventoryAddConsumer var11) {
      if (super.onCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)) {
         var5.getGndData().setByte("currentSprite", var6.getGndData().getByte("currentSprite"));
         return true;
      } else {
         return false;
      }
   }

   public void addDefaultItems(List<InventoryItem> var1, PlayerMob var2) {
      for(int var3 = 0; var3 < this.spriteCount; ++var3) {
         InventoryItem var4 = this.getDefaultItem(var2, 1);
         var4.getGndData().setByte("currentSprite", (byte)var3);
         var1.add(var4);
      }

   }
}
