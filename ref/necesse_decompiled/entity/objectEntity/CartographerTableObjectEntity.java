package necesse.entity.objectEntity;

import java.util.HashSet;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketMapData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.DiscoveredMap;
import necesse.level.maps.DiscoveredMapManager;
import necesse.level.maps.Level;

public class CartographerTableObjectEntity extends ObjectEntity {
   private DiscoveredMapManager discoveredMapManager = new DiscoveredMapManager();
   private HashSet<String> knownIslands = new HashSet();

   public CartographerTableObjectEntity(Level var1, String var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.isServer()) {
         this.discoveredMapManager.clearRemovedLevelIdentifiers(this.getLevel().getServer().world);
      }

      SaveData var2 = new SaveData("CARTOGRAPHYMAPS");
      this.discoveredMapManager.addSaveData(var2);
      var1.addSaveData(var2);
      var1.addStringHashSet("knownIslands", this.knownIslands);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      LoadData var2 = var1.getFirstLoadDataByName("CARTOGRAPHYMAPS");
      if (var2 != null) {
         this.discoveredMapManager.applySaveData(var2);
      }

      this.knownIslands = var1.getStringHashSet("knownIslands", new HashSet());
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      StringTooltips var3 = new StringTooltips();
      var3.add(this.getObject().getDisplayName());
      Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
   }

   public void interact(PlayerMob var1) {
      if (var1.isServerClient()) {
         ServerClient var2 = var1.getServerClient();
         var2.mapManager.clearRemovedLevelIdentifiers(var2.getServer().world);
         boolean var3 = false;
         if (this.discoveredMapManager.combine(var2.mapManager) | var2.combineToDiscovered(this.knownIslands)) {
            var3 = true;
            var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "cartographeradded")));
         }

         if (var2.mapManager.combine(this.discoveredMapManager) | var2.combineFromDiscovered(this.knownIslands)) {
            var3 = true;
            var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "cartographerdiscovered")));
            DiscoveredMap var4 = var2.mapManager.getDiscovery(var2.getLevelIdentifier());
            if (var4 != null) {
               var2.sendPacket(new PacketMapData(var4.getKnownMap()));
            }
         }

         if (!var3) {
            var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "cartographernothing")));
         }
      }

   }
}
