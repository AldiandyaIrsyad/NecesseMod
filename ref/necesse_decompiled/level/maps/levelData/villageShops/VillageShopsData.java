package necesse.level.maps.levelData.villageShops;

import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;

public class VillageShopsData extends LevelData {
   private boolean generatedShopItems = false;
   private HashMap<String, ShopItemStock> shopItems = new HashMap();

   public static VillageShopsData getShopData(Level var0) {
      LevelData var1 = var0.getLevelData("villageshopdata");
      if (var1 instanceof VillageShopsData) {
         return (VillageShopsData)var1;
      } else {
         VillageShopsData var2 = new VillageShopsData(var0);
         var0.addLevelData("villageshopdata", var2);
         return var2;
      }
   }

   public VillageShopsData() {
   }

   public VillageShopsData(Level var1) {
      this.generateShopItems(var1);
   }

   public void setLevel(Level var1) {
      super.setLevel(var1);
      this.generateShopItems(var1);
   }

   public void generateShopItems(Level var1) {
      if (!this.generatedShopItems) {
         this.generatedShopItems = true;
         this.shopItems = new HashMap();
         this.shopItems.put("firemone", new ShopItemStock("firemone", 100));
         this.shopItems.put("sunflower", new ShopItemStock("sunflower", 100));
         this.shopItems.put("iceblossom", new ShopItemStock("iceblossom", 100));
         this.shopItems.put("mushroom", new ShopItemStock("mushroom", 100));
         this.shopItems.put("caveglow", new ShopItemStock("caveglow", 100));
         this.shopItems.put("wheat", new ShopItemStock("wheat", 100));
         this.shopItems.put("wheatseed", new ShopItemStock("wheatseed", 100));
         this.shopItems.put("cornseed", new ShopItemStock("cornseed", 100));
         this.shopItems.put("tomatoseed", new ShopItemStock("tomatoseed", 100));
         this.shopItems.put("cabbageseed", new ShopItemStock("cabbageseed", 100));
         this.shopItems.put("chilipepperseed", new ShopItemStock("chilipepperseed", 100));
         this.shopItems.put("sugarbeetseed", new ShopItemStock("sugarbeetseed", 100));
         this.shopItems.put("eggplantseed", new ShopItemStock("eggplantseed", 100));
         this.shopItems.put("potatoseed", new ShopItemStock("potatoseed", 100));
         this.shopItems.put("riceseed", new ShopItemStock("riceseed", 100));
         this.shopItems.put("carrotseed", new ShopItemStock("carrotseed", 100));
         this.shopItems.put("onionseed", new ShopItemStock("onionseed", 100));
         this.shopItems.put("pumpkinseed", new ShopItemStock("pumpkinseed", 100));
         this.shopItems.put("strawberryseed", new ShopItemStock("strawberryseed", 100));
         this.shopItems.put("coffeebeans", new ShopItemStock("coffeebeans", 100));
         this.shopItems.put("blueberrysapling", new ShopItemStock("blueberrysapling", 100));
         this.shopItems.put("blackberrysapling", new ShopItemStock("blackberrysapling", 100));
         this.shopItems.put("applesapling", new ShopItemStock("applesapling", 100));
         this.shopItems.put("coconutsapling", new ShopItemStock("coconutsapling", 100));
         this.shopItems.put("lemonsapling", new ShopItemStock("lemonsapling", 100));
         this.shopItems.put("bananasapling", new ShopItemStock("bananasapling", 100));
         this.shopItems.put("fertilizer", new ShopItemStock("fertilizer", 500));
         this.shopItems.put("healthpotion", new ShopItemStock("healthpotion", 30));
         this.shopItems.put("speedpotion", new ShopItemStock("speedpotion", 5));
         this.shopItems.put("healthregenpotion", new ShopItemStock("healthregenpotion", 5));
         this.shopItems.put("manaregenpotion", new ShopItemStock("manaregenpotion", 5));
         this.shopItems.put("attackspeedpotion", new ShopItemStock("attackspeedpotion", 5));
         this.shopItems.put("invisibilitypotion", new ShopItemStock("invisibilitypotion", 5));
         this.shopItems.put("fireresistancepotion", new ShopItemStock("fireresistancepotion", 5));
         this.shopItems.put("fishingpotion", new ShopItemStock("fishingpotion", 5));
         this.shopItems.put("battlepotion", new ShopItemStock("battlepotion", 5));
         this.shopItems.put("resistancepotion", new ShopItemStock("resistancepotion", 5));
         this.shopItems.put("thornspotion", new ShopItemStock("thornspotion", 5));
         this.shopItems.put("accuracypotion", new ShopItemStock("accuracypotion", 5));
         this.shopItems.put("minionpotion", new ShopItemStock("minionpotion", 5));
         this.shopItems.put("knockbackpotion", new ShopItemStock("knockbackpotion", 5));
         this.shopItems.put("rapidpotion", new ShopItemStock("rapidpotion", 5));
         this.shopItems.put("spelunkerpotion", new ShopItemStock("spelunkerpotion", 5));
         this.shopItems.put("treasurepotion", new ShopItemStock("treasurepotion", 5));
         this.shopItems.put("passivepotion", new ShopItemStock("passivepotion", 5));
         this.shopItems.put("wormbait", new ShopItemStock("wormbait", 1000));
         this.shopItems.put("anglersbait", new ShopItemStock("anglersbait", 1000));
         this.shopItems.put("gobfish", new ShopItemStock("gobfish", 50));
         this.shopItems.put("halffish", new ShopItemStock("halffish", 20));
         this.shopItems.put("furfish", new ShopItemStock("furfish", 20));
         this.shopItems.put("icefish", new ShopItemStock("icefish", 20));
         this.shopItems.put("swampfish", new ShopItemStock("swampfish", 20));
         this.shopItems.put("rockfish", new ShopItemStock("rockfish", 20));
         this.shopItems.put("terrorfish", new ShopItemStock("terrorfish", 10));
         this.shopItems.put("ironbar", new ShopItemStock("ironbar", 25));
         this.shopItems.put("copperbar", new ShopItemStock("copperbar", 25));
         this.shopItems.put("goldbar", new ShopItemStock("goldbar", 25));
         this.shopItems.put("demonicbar", new ShopItemStock("demonicbar", 25));
         this.shopItems.put("quartz", new ShopItemStock("quartz", 25));
         this.shopItems.put("obsidian", new ShopItemStock("obsidian", 50));
         this.shopItems.put("lifequartz", new ShopItemStock("lifequartz", 10));
         this.shopItems.put("tungstenbar", new ShopItemStock("tungstenbar", 25));
         this.shopItems.put("glacialbar", new ShopItemStock("glacialbar", 25));
         this.shopItems.put("myceliumbar", new ShopItemStock("myceliumbar", 25));
         this.shopItems.put("ancientfossilbar", new ShopItemStock("ancientfossilbar", 25));
         this.shopItems.put("brokenirontool", new ShopItemStock("brokenirontool", 10));
         this.shopItems.put("brokencoppertool", new ShopItemStock("brokencoppertool", 10));
         this.shopItems.put("wool", new ShopItemStock("wool", 30));
         this.shopItems.put("leather", new ShopItemStock("leather", 30));
         this.shopItems.put("batwing", new ShopItemStock("batwing", 30));
         this.shopItems.put("book", new ShopItemStock("book", 25));
         this.shopItems.put("voidshard", new ShopItemStock("voidshard", 25));
         this.shopItems.put("ectoplasm", new ShopItemStock("ectoplasm", 25));
         this.shopItems.put("recallscroll", new ShopItemStock("recallscroll", 50));
         this.shopItems.put("travelscroll", new ShopItemStock("travelscroll", 25));
         this.shopItems.put("ironbomb", new ShopItemStock("ironbomb", 50));
         this.shopItems.put("dynamitestick", new ShopItemStock("dynamitestick", 25));
      }
   }

   public ShopItemStock getShopItem(String var1) {
      return (ShopItemStock)this.shopItems.get(var1);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      SaveData var2 = new SaveData("shopItems");
      this.shopItems.forEach((var1x, var2x) -> {
         SaveData var3 = new SaveData(var1x);
         var2x.addSaveData(var3);
         var2.addSaveData(var3);
      });
      var1.addSaveData(var2);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.generateShopItems(this.getLevel());
      LoadData var2 = var1.getFirstLoadDataByName("shopItems");
      if (var2 != null) {
         Iterator var3 = var2.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();

            try {
               ShopItemStock var5 = (ShopItemStock)this.shopItems.get(var4.getName());
               if (var5 != null) {
                  var5.applyLoadData(var4);
               } else {
                  GameLog.warn.println("Could not find village shop item data for " + var4.getName());
               }
            } catch (Exception var6) {
               GameLog.warn.println("Could not load shop item with stringID " + var4.getName());
            }
         }
      }

   }
}
