package necesse.engine.network.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketRemoveDeathLocation;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelDeathLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.MapDrawElement;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class ClientLevelManager {
   public final Client client;
   private Level previousLevel;
   private Level currentLevel;
   private ClientLevelLoading loading;
   private LevelMap map;
   private LinkedList<ClientDeathLocation> deathLocations = new LinkedList();

   public ClientLevelManager(Client var1) {
      this.client = var1;
   }

   public Level getLevel() {
      return this.currentLevel;
   }

   public Level getDrawnLevel() {
      return this.previousLevel != null ? this.previousLevel : this.currentLevel;
   }

   public boolean updateLevel(PacketLevelData var1) {
      ClientClient var2 = this.client.getClient();
      if (var2 != null && !var2.isSamePlace(var1.levelIdentifier)) {
         return false;
      } else {
         if (this.currentLevel == null || !var1.isSameLevel(this.currentLevel)) {
            this.setLevel(LevelRegistry.getNewLevel(var1.levelID, var1.levelIdentifier, var1.width, var1.height, this.client.worldEntity));
         }

         this.currentLevel.readLevelDataPacket(new PacketReader(var1.levelContent));
         if (this.client.getPlayer() != null) {
            this.client.getPlayer().setLevel(this.currentLevel);
         }

         Iterator var3 = var1.deathLocations.iterator();

         while(var3.hasNext()) {
            LevelDeathLocation var4 = (LevelDeathLocation)var3.next();
            this.addDeathLocation(var4);
         }

         return true;
      }
   }

   public void setLevel(Level var1) {
      if (this.currentLevel != var1) {
         if (this.currentLevel != null) {
            if (this.previousLevel != null) {
               this.previousLevel.dispose();
            }

            this.previousLevel = this.currentLevel;
         }

         this.currentLevel = var1;
         if (var1 != null) {
            var1.setWorldEntity(this.client.worldEntity);
            var1.makeClientLevel(this.client);
            this.loading = new ClientLevelLoading(var1);
            this.map = new LevelMap(var1, true);
         } else {
            this.map.dispose();
            this.map = null;
            this.loading = null;
         }

         this.deathLocations.clear();

         for(int var2 = 0; var2 < this.client.getSlots(); ++var2) {
            ClientClient var3 = this.client.getClient(var2);
            if (var3 != null && var3.playerMob != null && var3.loadedPlayer) {
               if (var1 == null) {
                  var3.playerMob.setLevel((Level)null);
               } else if (var3.isSamePlace(var1)) {
                  var3.playerMob.setLevel(var1);
               } else {
                  var3.playerMob.setLevel((Level)null);
               }
            }
         }

      }
   }

   public void tick() {
      if (this.loading != null) {
         this.loading.tickLoading(this.client.getPlayer());
      }

      if (this.previousLevel != null && this.currentLevel != null && (this.loading == null || this.loading.level != this.previousLevel && this.loading.isPreloadingDone())) {
         this.previousLevel.dispose();
         this.previousLevel = null;
         GlobalData.getCurrentState().onClientDrawnLevelChanged();
      }

   }

   public void addDeathLocation(LevelDeathLocation var1) {
      if (this.deathLocations.stream().noneMatch((var1x) -> {
         return var1x.location.x == var1.x && var1x.location.y == var1.y;
      })) {
         this.deathLocations.add(new ClientDeathLocation(this.client, var1));
      }

   }

   public boolean removeDeathLocationsIf(Predicate<LevelDeathLocation> var1) {
      if (this.currentLevel == null) {
         return false;
      } else {
         ListIterator var2 = this.deathLocations.listIterator();
         boolean var3 = false;

         while(var2.hasNext()) {
            ClientDeathLocation var4 = (ClientDeathLocation)var2.next();
            if (var1.test(var4.location)) {
               if (this.client != null) {
                  this.client.network.sendPacket(new PacketRemoveDeathLocation(this.currentLevel.getIdentifier(), var4.location.x, var4.location.y));
               }

               var4.drawElement.remove();
               var2.remove();
               var3 = true;
            }
         }

         return var3;
      }
   }

   public void clearDeathLocations() {
      Iterator var1 = this.deathLocations.iterator();

      while(var1.hasNext()) {
         ClientDeathLocation var2 = (ClientDeathLocation)var1.next();
         var2.drawElement.remove();
      }

      this.deathLocations.clear();
   }

   public ClientLevelLoading loading() {
      return this.loading;
   }

   public boolean isLevelLoaded(int var1) {
      return this.currentLevel != null && this.currentLevel.getIdentifierHashCode() == var1;
   }

   public boolean isRegionLoaded(int var1, int var2) {
      return this.loading != null && this.loading.isRegionLoaded(var1, var2);
   }

   public boolean isRegionLoadedAtTile(int var1, int var2) {
      int var3 = this.currentLevel.regionManager.getRegionXByTile(var1);
      int var4 = this.currentLevel.regionManager.getRegionYByTile(var2);
      return this.loading != null && this.loading.isRegionLoaded(var3, var4);
   }

   public boolean checkIfLoadedRegionAtTile(int var1, int var2, int var3, boolean var4) {
      if (this.loading != null && this.isLevelLoaded(var1)) {
         int var5 = this.currentLevel.regionManager.getRegionXByTile(var2);
         int var6 = this.currentLevel.regionManager.getRegionYByTile(var3);
         if (this.loading.isRegionLoaded(var5, var6)) {
            return true;
         }

         if (var4 && !this.loading.isRegionInQueue(var5, var6)) {
            this.client.network.sendPacket(new PacketUnloadRegion(this.currentLevel, var5, var6));
         }
      }

      return false;
   }

   public boolean checkIfLoadedRegionAtTile(int var1, RegionPositionGetter var2, boolean var3) {
      if (var2 == null) {
         return false;
      } else {
         if (this.loading != null && this.isLevelLoaded(var1)) {
            Collection var4 = var2.getRegionPositions();
            if (var4.isEmpty() || var4.stream().anyMatch((var1x) -> {
               return this.loading.isRegionLoaded(var1x.regionX, var1x.regionY);
            })) {
               return true;
            }

            if (var3) {
               HashSet var5 = (HashSet)var4.stream().filter((var1x) -> {
                  return !this.loading.isRegionLoaded(var1x.regionX, var1x.regionY) && !this.loading.isRegionInQueue(var1x.regionX, var1x.regionY);
               }).map((var0) -> {
                  return new Point(var0.regionX, var0.regionY);
               }).collect(Collectors.toCollection(HashSet::new));
               if (!var5.isEmpty()) {
                  this.client.network.sendPacket(new PacketUnloadRegions(this.currentLevel, var5));
               }
            }
         }

         return false;
      }
   }

   public LevelMap map() {
      return this.map;
   }

   public void finishUp() {
      this.currentLevel.onLoadingComplete();
      this.map.generateMapTextures();
      this.map.tickDiscovery(this.client.getPlayer());
   }

   public void clientTick() {
      this.currentLevel.clientTick();
      this.map.tickMapTexture();
   }

   public void updateMap(int var1, int var2) {
      this.map.updateMapTexture(var1, var2);
   }

   public void dispose() {
      if (this.previousLevel != null) {
         this.previousLevel.dispose();
      }

      if (this.currentLevel != null) {
         this.currentLevel.dispose();
      }

      if (this.map != null) {
         this.map.dispose();
      }

   }

   public class ClientDeathLocation {
      public final LevelDeathLocation location;
      public final MapDrawElement drawElement;

      public ClientDeathLocation(final Client var2, final LevelDeathLocation var3) {
         this.location = var3;
         final long var4 = var2.worldEntity.getLocalTime() - (long)(var3.secondsSince * 1000);
         this.drawElement = new MapDrawElement(var3.x, var3.y, new Rectangle(-12, -12, 24, 24)) {
            public void draw(int var1, int var2x, PlayerMob var3x) {
               Settings.UI.deathmarker.initDraw().posMiddle(var1, var2x).draw();
            }

            public boolean shouldRemove() {
               long var1 = (var2.worldEntity.getLocalTime() - var4) / 1000L;
               return var1 > 7200L;
            }

            public GameTooltips getTooltips(int var1, int var2x, PlayerMob var3x) {
               ListGameTooltips var4x = new ListGameTooltips();
               var4x.add(Localization.translate("misc", "recentdeath"));
               long var5 = (var2.worldEntity.getLocalTime() - var4) / 1000L;
               var4x.add(GameUtils.formatSeconds(var5));
               return var4x;
            }

            public String getMapInteractTooltip() {
               return Localization.translate("controls", "cleartip");
            }

            public void onMapInteract(InputEvent var1, PlayerMob var2x) {
               if (ClientLevelManager.this.currentLevel != null) {
                  var2.network.sendPacket(new PacketRemoveDeathLocation(ClientLevelManager.this.currentLevel.getIdentifier(), var3.x, var3.y));
               }

               this.remove();
               var1.use();
            }
         };
         ClientLevelManager.this.map.addDrawElement(this.drawElement);
      }
   }
}
