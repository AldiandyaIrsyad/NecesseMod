package necesse.engine.quest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.ItemSave;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.item.Item;
import necesse.inventory.item.ObtainTip;
import necesse.inventory.item.questItem.QuestItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public abstract class DeliverItemsQuest extends Quest {
   protected ArrayList<ItemObjective> objectives;

   public DeliverItemsQuest() {
      this.objectives = new ArrayList();
   }

   public DeliverItemsQuest(ItemObjective var1, ItemObjective... var2) {
      this.objectives = new ArrayList();
      this.objectives = new ArrayList();
      if (var1 != null) {
         this.objectives.add(var1);
      }

      ItemObjective[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ItemObjective var6 = var3[var5];
         if (var6 != null) {
            ItemObjective var7 = (ItemObjective)this.objectives.stream().filter((var1x) -> {
               return var1x.item == var6.item;
            }).findFirst().orElse((Object)null);
            if (var7 != null) {
               var7.itemsAmount += var6.itemsAmount;
            } else {
               this.objectives.add(var6);
            }
         }
      }

      if (this.objectives.isEmpty()) {
         throw new IllegalArgumentException("Must have at least one objective");
      }
   }

   public DeliverItemsQuest(String var1, int var2) {
      this(new ItemObjective(var1, var2));
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         ItemObjective var3 = (ItemObjective)var2.next();
         SaveData var4 = new SaveData("objective");
         var4.addUnsafeString("itemStringID", var3.item.getStringID());
         var4.addInt("itemsAmount", var3.itemsAmount);
         var1.addSaveData(var4);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.objectives.clear();
      String var2 = var1.getUnsafeString("itemStringID", (String)null, false);
      if (var2 != null) {
         Item var3 = ItemSave.loadItem(var2);
         if (var3 == null) {
            throw new IllegalArgumentException("Could not find item with id " + var2);
         }

         int var4 = var1.getInt("itemsAmount", 1);
         this.objectives.add(new ItemObjective(var2, var4));
      }

      Iterator var7 = var1.getLoadDataByName("objective").iterator();

      while(var7.hasNext()) {
         LoadData var8 = (LoadData)var7.next();
         String var9 = var8.getUnsafeString("itemStringID", (String)null, false);
         if (var9 != null) {
            Item var5 = ItemSave.loadItem(var9);
            if (var5 == null) {
               throw new IllegalArgumentException("Could not find item with id " + var9);
            }

            int var6 = var8.getInt("itemsAmount", 1);
            this.objectives.add(new ItemObjective(var9, var6));
         }
      }

      if (this.objectives.isEmpty()) {
         throw new IllegalStateException("Could not find any objectives");
      }
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.objectives.size());
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         ItemObjective var3 = (ItemObjective)var2.next();
         var1.putNextShortUnsigned(var3.item.getID());
         var1.putNextInt(var3.itemsAmount);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.objectives.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         int var5 = var1.getNextInt();
         this.objectives.add(new ItemObjective(var4, var5));
      }

   }

   public void tick(ServerClient var1) {
   }

   public boolean canComplete(NetworkClient var1) {
      if (var1.playerMob != null) {
         Iterator var2 = this.objectives.iterator();

         ItemObjective var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (ItemObjective)var2.next();
         } while(var3.itemsAmount <= var1.playerMob.getInv().main.getAmount(var1.playerMob.getLevel(), var1.playerMob, var3.item, "deliverquest"));

         return false;
      } else {
         return false;
      }
   }

   public void complete(ServerClient var1) {
      super.complete(var1);
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         ItemObjective var3 = (ItemObjective)var2.next();
         var1.playerMob.getInv().main.removeItems(var1.playerMob.getLevel(), var1.playerMob, var3.item, var3.itemsAmount, "deliverquest");
      }

   }

   public GameMessage getDescription() {
      return null;
   }

   public void drawProgress(PlayerMob var1, int var2, int var3, int var4, Color var5, boolean var6) {
      for(Iterator var7 = this.objectives.iterator(); var7.hasNext(); var3 += 20) {
         ItemObjective var8 = (ItemObjective)var7.next();
         int var9 = 0;
         if (var1 != null) {
            var9 = Math.min(var8.itemsAmount, var1.getInv().main.getAmount(var1.getLevel(), var1, var8.item, "deliverquest"));
         }

         float var10 = var8.itemsAmount == 0 ? 1.0F : (float)var9 / (float)var8.itemsAmount;
         String var11 = Localization.translate("quests", "deliveritem", "item", ItemRegistry.getDisplayName(var8.item.getID()));
         FontOptions var12 = (new FontOptions(16)).outline(var6);
         if (var5 != null) {
            var12.color(var5);
         }

         FontManager.bit.drawString((float)var2, (float)var3, var11, var12);
         var3 += 16;
         FontOptions var14;
         if (var8.item instanceof ObtainTip) {
            GameMessage var13 = ((ObtainTip)var8.item).getObtainTip();
            if (var13 != null) {
               var14 = (new FontOptions(12)).outline(var6);
               if (var5 != null) {
                  var14.color(var5);
               }

               String var15 = var13.translate();
               ArrayList var16 = GameUtils.breakString(var15, var14, var4);

               for(Iterator var17 = var16.iterator(); var17.hasNext(); var3 += 12) {
                  String var18 = (String)var17.next();
                  FontManager.bit.drawString((float)var2, (float)(var3 + 2), var18, var14);
               }

               var3 += 2;
            }
         }

         Color var19 = var10 == 1.0F ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
         var14 = (new FontOptions(16)).outline(var6).color(var19);
         Achievement.drawProgressbarText(var2, var3, var4, 5, var10, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, var9 + "/" + var8.itemsAmount, var14);
      }

   }

   public int getDrawProgressHeight(int var1, boolean var2) {
      int var3 = 0;
      Iterator var4 = this.objectives.iterator();

      while(var4.hasNext()) {
         ItemObjective var5 = (ItemObjective)var4.next();
         var3 += 36;
         if (var5.item instanceof ObtainTip) {
            GameMessage var6 = ((ObtainTip)var5.item).getObtainTip();
            if (var6 != null) {
               String var7 = var6.translate();
               FontOptions var8 = (new FontOptions(12)).outline(var2);
               ArrayList var9 = GameUtils.breakString(var7, var8, var1);
               var3 += var9.size() * 12 + 2;
            }
         }
      }

      return var3;
   }

   public MobSpawnTable getExtraCritterSpawnTable(ServerClient var1, Level var2) {
      Iterator var3 = this.objectives.iterator();

      ItemObjective var4;
      do {
         if (!var3.hasNext()) {
            return super.getExtraCritterSpawnTable(var1, var2);
         }

         var4 = (ItemObjective)var3.next();
      } while(!(var4.item instanceof QuestItem));

      return ((QuestItem)var4.item).getExtraCritterSpawnTable(var1, var2);
   }

   public MobSpawnTable getExtraMobSpawnTable(ServerClient var1, Level var2) {
      Iterator var3 = this.objectives.iterator();

      ItemObjective var4;
      do {
         if (!var3.hasNext()) {
            return super.getExtraMobSpawnTable(var1, var2);
         }

         var4 = (ItemObjective)var3.next();
      } while(!(var4.item instanceof QuestItem));

      return ((QuestItem)var4.item).getExtraMobSpawnTable(var1, var2);
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      Iterator var3 = this.objectives.iterator();

      ItemObjective var4;
      do {
         if (!var3.hasNext()) {
            return super.getExtraFishingLoot(var1, var2);
         }

         var4 = (ItemObjective)var3.next();
      } while(!(var4.item instanceof QuestItem));

      return ((QuestItem)var4.item).getExtraFishingLoot(var1, var2);
   }

   public LootTable getExtraMobDrops(ServerClient var1, Mob var2) {
      Iterator var3 = this.objectives.iterator();

      ItemObjective var4;
      do {
         if (!var3.hasNext()) {
            return super.getExtraMobDrops(var1, var2);
         }

         var4 = (ItemObjective)var3.next();
      } while(!(var4.item instanceof QuestItem));

      return ((QuestItem)var4.item).getExtraMobDrops(var1, var2);
   }

   public static class ItemObjective {
      protected Item item;
      protected int itemsAmount;

      public ItemObjective(int var1, int var2) {
         this.item = ItemRegistry.getItem(var1);
         if (this.item == null) {
            throw new IllegalArgumentException("Could not find item with id " + var1);
         } else {
            this.itemsAmount = var2;
         }
      }

      public ItemObjective(String var1, int var2) {
         this.item = ItemRegistry.getItem(var1);
         if (this.item == null) {
            throw new IllegalArgumentException("Could not find item with id " + var1);
         } else {
            this.itemsAmount = var2;
         }
      }
   }
}
