package necesse.inventory.item.miscItem;

import java.util.function.Consumer;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameState;
import necesse.engine.MusicList;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.Item;

public class PortableMusicPlayerItem extends PouchItem {
   public PortableMusicPlayerItem() {
      this.rarity = Item.Rarity.RARE;
      this.combinePurposes.clear();
      this.insertPurposes.clear();
      this.drawStoredItems = false;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "musicplayertip"));
      VinylItem var5 = this.getActiveVinyl(var1);
      if (var5 != null) {
         var4.add(Localization.translate("ui", "musicplaying", "name", var5.music.trackName.translate() + (var5.music.fromName == null ? "" : " (" + var5.music.fromName.translate() + ")")));
      }

      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   protected void openContainer(ServerClient var1, PlayerInventorySlot var2) {
      PacketOpenContainer var3 = new PacketOpenContainer(ContainerRegistry.ITEM_MUSIC_PLAYER_CONTAINER, ItemInventoryContainer.getContainerContent(this, var2));
      ContainerRegistry.openAndSendContainer(var1, var3);
   }

   public void tick(Inventory var1, int var2, InventoryItem var3, GameClock var4, GameState var5, Entity var6, WorldSettings var7, Consumer<InventoryItem> var8) {
      super.tick(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var6 != null) {
         GNDItemMap var9 = var3.getGndData();
         int var10 = var9.getInt("lastVinyl");
         long var11 = var9.getLong("lastTime");
         VinylItem var13 = this.getActiveVinyl(var3);
         if (var13 != null) {
            if (var10 != var13.getID() || var11 == 0L) {
               var9.setInt("lastVinyl", var13.getID());
               var11 = var6.getWorldEntity().getTime();
               var9.setLong("lastTime", var11);
               var1.markDirty(var2);
            }

            if (var6 instanceof PlayerMob) {
               if (var6.isClient()) {
                  if (var6.getLevel().getClient().getPlayer() == var6) {
                     MusicList var14 = (new MusicList(var6.getWorldEntity().getTime() - var11)).addMusic(var13.music, 1.5F);
                     Screen.setMusic((AbstractMusicList)var14, (ComparableSequence)Screen.MusicPriority.PORTABLE_MUSIC_PLAYER.thenBy(var2));
                  }
               } else if (var6.isServer()) {
                  ServerClient var15 = ((PlayerMob)var6).getServerClient();
                  if (var15.achievementsLoaded()) {
                     var15.achievements().MY_JAM.markCompleted(var15);
                  }
               }
            }
         } else if (var10 != 0) {
            var9.setItem("lastVinyl", (GNDItem)null);
            var9.setItem("lastTime", (GNDItem)null);
            var1.markDirty(var2);
         }
      }

   }

   public VinylItem getActiveVinyl(InventoryItem var1) {
      Inventory var2 = this.getInternalInventory(var1);
      InventoryItem var3 = var2.getItem(0);
      return var3 != null && var3.item instanceof VinylItem ? (VinylItem)var3.item : null;
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestItem(var1.item);
   }

   public boolean isValidRequestItem(Item var1) {
      return var1 instanceof VinylItem;
   }

   public boolean isValidRequestType(Item.Type var1) {
      return false;
   }

   public int getInternalInventorySize() {
      return 1;
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canChangePouchName() {
      return false;
   }
}
