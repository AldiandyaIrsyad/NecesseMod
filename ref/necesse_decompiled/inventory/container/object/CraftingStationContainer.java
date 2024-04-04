package necesse.inventory.container.object;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.CraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.multiTile.MultiTile;

public class CraftingStationContainer extends SettlementDependantContainer {
   public static int nearbyCraftTileRange = 9;
   public final GameMessage header;
   public final Tech[] techs;
   public final int objectX;
   public final int objectY;
   public final CraftingStationObject craftingStationObject;
   public final GameTileRange range;
   private LinkedHashSet<Inventory> nearbyInventories = new LinkedHashSet();
   public SettlementContainerObjectStatusManager settlementObjectManager;

   public CraftingStationContainer(NetworkClient var1, int var2, LevelObject var3, PacketReader var4) {
      super(var1, var2);
      this.objectX = var3.tileX;
      this.objectY = var3.tileY;
      this.craftingStationObject = (CraftingStationObject)var3.object;
      this.header = this.craftingStationObject.getCraftingHeader();
      this.techs = this.craftingStationObject.getCraftingTechs();
      this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, var3.level, var3.tileX, var3.tileY, var4);
      Recipes.streamRecipes().filter((var1x) -> {
         Stream var10000 = Arrays.stream(this.techs);
         Objects.requireNonNull(var1x);
         return var10000.anyMatch(var1x::matchTech);
      }).forEach(this::addRecipe);
      MultiTile var5 = var3.getMultiTile();
      Rectangle var6 = var5.getTileRectangle(0, 0);
      this.range = new GameTileRange(nearbyCraftTileRange, var6);
      this.nearbyInventories.addAll(this.craftInventories);
      Iterator var7 = this.getNearbyInventories(var3.level, this.objectX, this.objectY, this.range, OEInventory::canUseForNearbyCrafting).iterator();

      while(var7.hasNext()) {
         InventoryRange var8 = (InventoryRange)var7.next();
         this.nearbyInventories.add(var8.inventory);
      }

   }

   protected Level getLevel() {
      return this.client.isServer() ? this.client.getServerClient().getLevel() : this.client.playerMob.getLevel();
   }

   public Collection<Inventory> getCraftInventories() {
      return (Collection)(this.useNearbyInventories() ? this.nearbyInventories : super.getCraftInventories());
   }

   private boolean useNearbyInventories() {
      return this.client.isServer() ? this.client.craftingUsesNearbyInventories : (Boolean)Settings.craftingUseNearby.get();
   }

   public void init() {
      super.init();
      if (this.client.isClient()) {
         GlobalData.updateRecipes();
      }

   }

   public void tick() {
      super.tick();
      if (this.client.isClient() && (Boolean)Settings.craftingUseNearby.get()) {
         boolean var1 = false;

         Inventory var3;
         for(Iterator var2 = this.nearbyInventories.iterator(); var2.hasNext(); var3.clean()) {
            var3 = (Inventory)var2.next();
            if (var3.isDirty()) {
               var1 = true;
            }
         }

         if (var1) {
            GlobalData.updateCraftable();
         }
      }

   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         Level var2 = var1.getLevel();
         return var2.getObjectID(this.objectX, this.objectY) == this.craftingStationObject.getID() && var2.getObject(this.objectX, this.objectY).inInteractRange(var2, this.objectX, this.objectY, var1.playerMob);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4, Packet var5) {
      if (!var2.isServer()) {
         throw new IllegalStateException("Level must be a server level");
      } else {
         Packet var6 = new Packet();
         PacketWriter var7 = new PacketWriter(var6);
         SettlementContainerObjectStatusManager.writeContent(var1, var2, var3, var4, var7);
         if (var5 != null) {
            var7.putNextContentPacket(var5);
         }

         PacketOpenContainer var8 = PacketOpenContainer.LevelObject(var0, var3, var4, var6);
         ContainerRegistry.openAndSendContainer(var1, var8);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4) {
      openAndSendContainer(var0, var1, var2, var3, var4, (Packet)null);
   }
}
