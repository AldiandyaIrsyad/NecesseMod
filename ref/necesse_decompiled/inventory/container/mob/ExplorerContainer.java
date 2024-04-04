package necesse.inventory.container.mob;

import java.awt.Point;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.FindIslandMission;
import necesse.gfx.GameResources;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.biomes.Biome;

public class ExplorerContainer extends ShopContainer {
   public final IntCustomAction buyFindIslandButton;
   public final EmptyCustomAction acceptFoundIslandButton;
   public final EmptyCustomAction findNewIslandButton;
   public final ExplorerHumanMob explorerMob;
   public Point foundIslandCoord;
   public boolean foundIsland;
   public Biome foundBiome;
   public boolean knowBiomeAlready;

   public ExplorerContainer(final NetworkClient var1, int var2, ExplorerHumanMob var3, PacketReader var4) {
      super(var1, var2, var3, var4.getNextContentPacket());
      this.explorerMob = var3;
      this.foundIsland = var4.getNextBoolean();
      if (var4.getNextBoolean()) {
         int var5 = var4.getNextInt();
         int var6 = var4.getNextInt();
         this.foundIslandCoord = new Point(var5, var6);
         this.foundBiome = BiomeRegistry.getBiome(var4.getNextShortUnsigned());
         this.knowBiomeAlready = var4.getNextBoolean();
      }

      this.buyFindIslandButton = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            Biome var2 = BiomeRegistry.getBiome(var1x);
            int var3 = ExplorerContainer.this.getFindIslandPrice(var2);
            if (var3 == 0 || var3 > 0 && var1.playerMob.getInv().getAmount(ItemRegistry.getItem("coin"), true, false, false, "buy") >= var3) {
               if (var3 > 0) {
                  var1.playerMob.getInv().removeItems(ItemRegistry.getItem("coin"), var3, true, false, false, "buy");
               }

               if (var1.isClient()) {
                  Screen.playSound(GameResources.coins, SoundEffect.globalEffect());
               } else if (var1.isServer()) {
                  System.out.println(var1.getName() + " bought find island " + var2.getDisplayName() + " for " + var3 + " coins");
                  var1.getServerClient().closeContainer(true);
                  ExplorerContainer.this.explorerMob.startMission(new FindIslandMission(var1x, var1.getServerClient().getDiscoveredIslands()));
               }
            }

         }
      });
      this.acceptFoundIslandButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (ExplorerContainer.this.foundIsland && var1.isServer()) {
               if (ExplorerContainer.this.foundIslandCoord != null) {
                  ServerClient var1x = var1.getServerClient();
                  if (!var1x.hasDiscoveredIsland(ExplorerContainer.this.foundIslandCoord.x, ExplorerContainer.this.foundIslandCoord.y)) {
                     System.out.println(var1.getName() + " accepted found island " + ExplorerContainer.this.foundBiome.getDisplayName() + " at " + ExplorerContainer.this.foundIslandCoord.x + ", " + ExplorerContainer.this.foundIslandCoord.y);
                     var1x.addDiscoveredIsland(ExplorerContainer.this.foundIslandCoord.x, ExplorerContainer.this.foundIslandCoord.y);
                     LevelIdentifier var2 = var1x.getLevelIdentifier();
                     TravelDir var3 = var2.isIslandPosition() ? null : TravelDir.getDeltaDir(var2.getIslandX(), var2.getIslandY(), ExplorerContainer.this.foundIslandCoord.x, ExplorerContainer.this.foundIslandCoord.y);
                     LocalMessage var4 = new LocalMessage("ui", "explorerdiscover");
                     if (var3 == null) {
                        var4.addReplacement("dir", "N/A");
                        var4.addReplacement("coord", "N/A");
                     } else {
                        var4.addReplacement("dir", var3.dirMessage);
                        var4.addReplacement("coord", ExplorerContainer.this.foundIslandCoord.x - var2.getIslandX() + "," + (ExplorerContainer.this.foundIslandCoord.y - var2.getIslandY()));
                     }

                     var1x.sendChatMessage((GameMessage)var4);
                  }
               }

               ExplorerContainer.this.explorerMob.foundIsland = false;
               ExplorerContainer.this.explorerMob.foundIslandCoord = null;
               ExplorerContainer.this.explorerMob.sendMovementPacket(false);
            }

            ExplorerContainer.this.foundIsland = false;
         }
      });
      this.findNewIslandButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (ExplorerContainer.this.foundBiome != null && var1.isServer()) {
               System.out.println(var1.getName() + " chose to find new " + ExplorerContainer.this.foundBiome.getDisplayName());
               var1.getServerClient().closeContainer(true);
               ExplorerContainer.this.explorerMob.startMission(new FindIslandMission(ExplorerContainer.this.foundBiome.getID(), var1.getServerClient().getDiscoveredIslands()));
               ExplorerContainer.this.explorerMob.sendMovementPacket(false);
            }

         }
      });
   }

   public int getFindIslandPrice(Biome var1) {
      int var2 = -1;
      if (this.client.isServer()) {
         var2 = var1.getFindIslandCost(this.client.getServerClient().characterStats());
      } else if (this.client.isClient()) {
         var2 = var1.getFindIslandCost(this.client.getClientClient().getClient().characterStats);
      }

      return var2 < 0 ? var2 : (int)((new GameRandom(this.priceSeed)).getFloatBetween(0.85F, 1.15F) * (float)var2);
   }

   public static Packet getExplorerContainerContent(ExplorerHumanMob var0, Server var1, ServerClient var2, Packet var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextContentPacket(var3);
      var5.putNextBoolean(var0.foundIsland);
      var5.putNextBoolean(var0.foundIslandCoord != null);
      if (var0.foundIslandCoord != null) {
         var5.putNextInt(var0.foundIslandCoord.x);
         var5.putNextInt(var0.foundIslandCoord.y);
         var5.putNextShortUnsigned(var1.levelCache.getBiomeID(var0.foundIslandCoord.x, var0.foundIslandCoord.y));
         var5.putNextBoolean(var2.hasDiscoveredIsland(var0.foundIslandCoord.x, var0.foundIslandCoord.y));
      }

      return var4;
   }
}
