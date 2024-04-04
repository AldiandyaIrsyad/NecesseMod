package necesse.gfx.ui.debug;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldGenerator;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.Level;

public class DebugInfo extends Debug {
   public DebugInfo() {
   }

   protected void drawDebug(Client var1) {
      PlayerMob var2 = var1.getPlayer();
      ServerClient var3 = null;
      Level var4 = var1.getLevel();
      if (var4 != null) {
         Level var5 = null;
         if (var1.getLocalServer() != null) {
            var5 = var1.getLocalServer().world.getLevel(var4.getIdentifier());
            var3 = var1.getLocalServer().getLocalServerClient();
            if (Settings.serverPerspective) {
               var4 = var5;
            }
         }

         this.drawString("Player tile pos/speed: " + var2.getX() / 32 + ", " + var2.getY() / 32 + " / " + var2.getCurrentSpeed());
         this.drawString("Player hp/armor: " + var2.getHealth() + " / " + var2.getArmor() + " (" + GameDamage.getDamageReduction(var2.getArmor()) + ")");
         this.drawString("World Time, time: " + (int)((float)var4.getWorldEntity().getWorldTime() / 1000.0F) + " (day " + var4.getWorldEntity().getDay() + ", " + var4.getWorldEntity().getDayTimeReadable() + ") " + var4.getWorldEntity().getDayTimeInt() + "/" + var4.getWorldEntity().getDayTimeMax() + ", " + var4.getWorldEntity().getTime());
         this.drawString("AmbientLight: " + (Settings.serverPerspective && var5 != null ? var5.lightManager.getAmbientLight() : var4.lightManager.getAmbientLight()));
         this.drawString("Entities/projectiles: " + var4.entityManager.getSize() + " / " + var4.entityManager.projectiles.count() + " (" + var4.entityManager.projectiles.countCache() + ")");
         this.drawString("Mobs/pickups: " + var4.entityManager.mobs.count() + " (" + var4.entityManager.mobs.countCache() + ") / " + var4.entityManager.pickups.count() + " (" + var4.entityManager.pickups.countCache() + ")");
         this.drawString("Particles/trails: " + var4.entityManager.particles.count() + ", " + var4.entityManager.particleOptions.count() + " / " + var4.entityManager.trails.size());
         this.drawString("Chains/pillar: " + var4.entityManager.chains.size() + " / " + var4.entityManager.pillarHandlers.size());
         this.drawString("DamagedObjects: " + var4.entityManager.damagedObjects.count());
         this.drawString("ObjectEntities/LevelEvents: " + var4.entityManager.objectEntities.count() + " / " + var4.entityManager.getLevelEvents().size());
         this.drawString("Recipes: " + Recipes.getTotalRecipes() + " (Hash: " + Integer.toHexString(Recipes.getHash()) + ")");
         this.drawString("Biome: " + var4.biome.getDisplayName() + (var4.isIslandPosition() ? " (Size: " + GameMath.toDecimals(WorldGenerator.getIslandSize(var4.getIslandX(), var4.getIslandY()), 2) + ")" : ""));
         this.drawString("Level: " + var4.getClass().getSimpleName() + ", identifier: " + var4.getIdentifier() + ", size: " + var4.width + "x" + var4.height);
         this.skipY(10);
         if (var2.getDraggingItem() != null) {
            this.drawString("Dragging item: " + var2.getDraggingItem().getItemDisplayName());
            this.drawString("Dragging amount: " + var2.getDraggingItem().getAmount());
            this.skipY(10);
         }

         this.drawString("Server perspective: " + Settings.serverPerspective);
         this.skipY(10);
         this.drawString("Client in: " + var1.packetManager.getAverageIn() + "/s (" + var1.packetManager.getAverageInPackets() + "), Total: " + var1.packetManager.getTotalIn() + " (" + var1.packetManager.getTotalInPackets() + ")");
         this.drawString("Client out: " + var1.packetManager.getAverageOut() + "/s (" + var1.packetManager.getAverageOutPackets() + "), Total: " + var1.packetManager.getTotalOut() + " (" + var1.packetManager.getTotalOutPackets() + ")");
         this.drawString("Lost packets: In: " + var1.packetManager.getLostInPackets() + ", Out: " + var1.packetManager.getLostOutPackets());
         this.drawString("Slot: " + (var1.getSlot() + 1) + "/" + var1.getSlots());
         this.drawString("Raining: " + var4.rainingLayer.isRaining());
         if (var5 != null) {
            this.drawString("Rain timer: " + GameUtils.formatSeconds(var5.rainingLayer.getRemainingRainTime() / 1000L));
         }

         this.drawString("Playing sounds: " + Screen.getPlayingSoundsCount());
         this.drawString("Localization listeners: " + Localization.getListenersSize());
         this.drawString("Generated textures: " + GameTexture.getGeneratedTextureCount());
         this.drawString("Performance history: " + var1.tickManager().getPerformanceHistorySize());
         this.drawString("Memory max: " + GameUtils.getByteString(Runtime.getRuntime().maxMemory()));
         long var6 = Runtime.getRuntime().totalMemory();
         this.drawString("Memory total: " + GameUtils.getByteString(var6));
         long var8 = var6 - Runtime.getRuntime().freeMemory();
         double var10 = (double)var8 / (double)var6;
         String var12 = GameUtils.getByteString(var8) + " (" + GameMath.toDecimals(var10 * 100.0, 2) + "%)";
         this.drawString("Memory used: " + var12);
         ClientLevelLoading var13 = var1.levelManager.loading();
         if (var13 != null) {
            this.skipY(10);
            int var14 = var13.getRegionsLoadedCount();
            int var15 = var13.getRegionsLoadQueueCount();
            int var16 = var13.getRegionsRequestedCount();
            this.drawString("Loaded regions: " + var14 + ", Requested: " + var16 + ", Queued: " + var15);
            if (var3 != null) {
               this.drawString("Server client regions: " + var3.getLoadedRegionsCount(var4.getIdentifier()));
            }
         }

         this.skipY(10);
         AdventureParty var20 = Settings.serverPerspective && var3 != null ? var3.adventureParty : var1.adventureParty;
         String var21 = var20.getDebugString();
         if (var21 != null) {
            this.drawString("Adventure party: " + var21);
         }

         this.skipY(10);
         GameMessage var22 = var4.settlementLayer.getSettlementName();
         this.drawString("Settlement active: " + var4.settlementLayer.isActive());
         this.drawString("Settlement name: " + (var22 == null ? null : var22.translate()));
         this.drawString("Settlement owner: " + var4.settlementLayer.getOwnerAuth() + ", " + var4.settlementLayer.getOwnerName());
         this.drawString("Settlement team: " + var4.settlementLayer.getTeamID());
         if (var4.settlementLayer.isRaidActive()) {
            this.drawString("Settlement raid: Active");
         } else if (var4.settlementLayer.isRaidApproaching()) {
            this.drawString("Settlement raid: Approaching");
         } else {
            this.drawString("Settlement raid: None");
         }

         if (Screen.isKeyDown(340)) {
            LinkedList var17 = var4.buffManager.getModifierTooltips();
            this.skipY(10);
            if (var17.isEmpty()) {
               this.drawString("No level modifiers");
            } else {
               this.drawString("Level modifiers:");
               Iterator var18 = var17.iterator();

               while(var18.hasNext()) {
                  ModifierTooltip var19 = (ModifierTooltip)var18.next();
                  this.drawString(var19.tip.toMessage((Color)null, (Color)null, (Color)null, false).translate());
               }
            }
         }

      }
   }
}
