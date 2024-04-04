package necesse.inventory.item.armorItem;

import java.util.List;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.GameHair;
import necesse.gfx.HumanLook;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;

public class WigArmorItem extends HelmetArmorItem {
   public WigArmorItem(int var1) {
      super(var1, (DamageType)null, 0, (String)null);
   }

   public void loadItemTextures() {
   }

   public GameTexture getArmorTexture(InventoryItem var1, PlayerMob var2) {
      return var1 != null ? GameHair.getHair(getHair(var1.getGndData())).getHairTexture(getHairCol(var1.getGndData())) : super.getArmorTexture(var1, var2);
   }

   public GameTexture getHeadArmorBackTexture(InventoryItem var1, PlayerMob var2) {
      return var1 != null ? GameHair.getHair(getHair(var1.getGndData())).getBackHairTexture(getHairCol(var1.getGndData())) : super.getHeadArmorBackTexture(var1, var2);
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      return var1 != null ? new GameSprite(GameHair.getHair(getHair(var1.getGndData())).getWigTexture(getHairCol(var1.getGndData())), 32) : super.getItemSprite(var1, var2);
   }

   public void addDefaultItems(List<InventoryItem> var1, PlayerMob var2) {
      for(int var3 = 0; var3 < GameHair.getTotalHair(); ++var3) {
         HumanLook var4 = var2 == null ? new HumanLook() : new HumanLook(var2.look);
         var4.setHair(var3);
         var1.add(addWigData(new InventoryItem(this), var4));
      }

      super.addDefaultItems(var1, var2);
   }

   public InventoryItem getDefaultItem(PlayerMob var1, int var2) {
      return addWigData(super.getDefaultItem(var1, var2), var1 == null ? new HumanLook() : var1.look);
   }

   public static int getHair(GNDItemMap var0) {
      return var0.getByte("hair") & 255;
   }

   public static int getHairCol(GNDItemMap var0) {
      return var0.getByte("haircol") & 255;
   }

   public static GNDItemMap getWigData(HumanLook var0) {
      return addWigData((GNDItemMap)null, var0);
   }

   public static GNDItemMap addWigData(GNDItemMap var0, HumanLook var1) {
      if (var0 == null) {
         var0 = new GNDItemMap();
      }

      var0.setByte("hair", (byte)var1.getHair());
      var0.setByte("haircol", (byte)var1.getHairColor());
      return var0;
   }

   public static InventoryItem addWigData(InventoryItem var0, HumanLook var1) {
      addWigData(var0.getGndData(), var1);
      return var0;
   }
}
