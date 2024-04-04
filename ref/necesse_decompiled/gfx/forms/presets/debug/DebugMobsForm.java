package necesse.gfx.forms.presets.debug;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import necesse.engine.AreaFinder;
import necesse.engine.Screen;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.settlementRaidEvent.HumanSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.RaiderMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PirateHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormMobList;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.forms.presets.debug.tools.MoveMobDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobChance;

public class DebugMobsForm extends Form {
   public static ArrayList<FormMobList.MobConstructor> extraMobs = new ArrayList();
   public FormMobList mobList;
   public FormTextInput mobFilter;

   public DebugMobsForm(String var1, final DebugForm var2) {
      super((String)var1, 240, 440);
      this.addComponent(new FormLabel("Mobs", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.mobList = (FormMobList)this.addComponent(new FormMobList(0, 40, this.getWidth(), this.getHeight() - 180) {
         public void addElements(Consumer<FormMobList.MobConstructor> var1) {
            MobRegistry.getMobs().forEach((var2xx) -> {
               var1.accept(new FormMobList.MobConstructor(var2xx.getIDData().getStringID()) {
                  public Mob construct(Level var1, int var2xx, int var3) {
                     return MobRegistry.getMob(var2x.getIDData().getID(), var1);
                  }
               });
            });
            Iterator var2x = DebugMobsForm.extraMobs.iterator();

            while(var2x.hasNext()) {
               FormMobList.MobConstructor var3 = (FormMobList.MobConstructor)var2x.next();
               var1.accept(var3);
            }

         }

         public void onClicked(final FormMobList.MobConstructor var1) {
            MouseDebugGameTool var2x = new MouseDebugGameTool(var2, (String)null) {
               public void init() {
                  this.onLeftClick((var2x) -> {
                     var1.spawn(this.parent, this.getLevel(), this.getMouseX(), this.getMouseY());
                     return true;
                  }, "Spawn " + var1.displayName);
                  this.onRightClick((var1x) -> {
                     int var2x = this.getMouseX();
                     int var3 = this.getMouseY();
                     Iterator var4 = this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(var2x / 32, var3 / 32, 1).iterator();

                     while(var4.hasNext()) {
                        Mob var5 = (Mob)var4.next();
                        if (var5.getSelectBox().contains(var2x, var3)) {
                           this.parent.client.network.sendPacket(new PacketRemoveMob(var5.getUniqueID()));
                           break;
                        }
                     }

                     return true;
                  }, "Remove mob");
               }
            };
            Screen.clearGameTools(var2);
            Screen.setGameTool(var2x, var2);
         }
      });
      this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, this.getHeight() - 138));
      this.mobFilter = (FormTextInput)this.addComponent(new FormTextInput(0, this.getHeight() - 120, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
      this.mobFilter.placeHolder = new StaticMessage("Search filter");
      this.mobFilter.rightClickToClear = true;
      this.mobFilter.onChange((var1x) -> {
         this.mobList.setFilter(((FormTextInput)var1x.from).getText());
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Move mob tool", 0, this.getHeight() - 80, this.getWidth()))).onClicked((var1x) -> {
         MoveMobDebugGameTool var2x = new MoveMobDebugGameTool(var2);
         Screen.clearGameTools(var2);
         Screen.setGameTool(var2x, var2);
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()))).onClicked((var2x) -> {
         this.mobFilter.setTyping(false);
         var2.makeCurrent(var2.mainMenu);
      });
   }

   static {
      extraMobs.add(new FormMobList.MobConstructor("calf") {
         public Mob construct(Level var1, int var2, int var3) {
            HusbandryMob var4 = (HusbandryMob)MobRegistry.getMob("cow", var1);
            if (var1 != null) {
               var4.startBaby();
            }

            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("lamb") {
         public Mob construct(Level var1, int var2, int var3) {
            HusbandryMob var4 = (HusbandryMob)MobRegistry.getMob("sheep", var1);
            if (var1 != null) {
               var4.startBaby();
            }

            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("piglet") {
         public Mob construct(Level var1, int var2, int var3) {
            HusbandryMob var4 = (HusbandryMob)MobRegistry.getMob("pig", var1);
            if (var1 != null) {
               var4.startBaby();
            }

            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("losthunter") {
         public Mob construct(Level var1, int var2, int var3) {
            HunterHumanMob var4 = (HunterHumanMob)MobRegistry.getMob("hunterhuman", var1);
            var4.setLost(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("lostminer") {
         public Mob construct(Level var1, int var2, int var3) {
            MinerHumanMob var4 = (MinerHumanMob)MobRegistry.getMob("minerhuman", var1);
            var4.setLost(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("lostexplorer") {
         public Mob construct(Level var1, int var2, int var3) {
            ExplorerHumanMob var4 = (ExplorerHumanMob)MobRegistry.getMob("explorerhuman", var1);
            var4.setLost(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("trappedpirate") {
         public Mob construct(Level var1, int var2, int var3) {
            PirateHumanMob var4 = (PirateHumanMob)MobRegistry.getMob("piratehuman", var1);
            var4.setTrapped(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("trappedstylist") {
         public Mob construct(Level var1, int var2, int var3) {
            StylistHumanMob var4 = (StylistHumanMob)MobRegistry.getMob("stylisthuman", var1);
            var4.setTrapped(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("trappedmage") {
         public Mob construct(Level var1, int var2, int var3) {
            MageHumanMob var4 = (MageHumanMob)MobRegistry.getMob("magehuman", var1);
            var4.setTrapped(true);
            return var4;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("humanraideractive") {
         public Mob construct(Level var1, int var2, int var3) {
            if (var1 != null && var1.isClient()) {
               Server var4 = var1.getClient().getLocalServer();
               if (var4 != null) {
                  Level var5 = var4.world.getLevel(var1.getIdentifier());
                  Point var6 = new Point(var2 / 32, var3 / 32);
                  MobChance var7 = HumanSettlementRaidLevelEvent.spawnTable.getRandomMob(var5, (ServerClient)null, var6, GameRandom.globalRandom);
                  if (var7 != null) {
                     Mob var8 = var7.getMob(var5, (ServerClient)null, var6);
                     if (var8 instanceof RaiderMob) {
                        ((RaiderMob)var8).makeRaider((SettlementRaidLevelEvent)null, var6, var6, 0L, 1337, 1.0F);
                     }

                     return var8;
                  }
               }
            }

            HumanRaiderMob var9 = (HumanRaiderMob)MobRegistry.getMob("humanraider", var1);
            var9.makeRaider((SettlementRaidLevelEvent)null, new Point(var2 / 32, var3 / 32), new Point(var2 / 32, var3 / 32), 0L, 1337, 1.0F);
            return var9;
         }
      });
      extraMobs.add(new FormMobList.MobConstructor("queenbeeswarm") {
         public Mob construct(Level var1, int var2, int var3) {
            return MobRegistry.getMob("queenbee", var1);
         }

         public void spawn(DebugForm var1, final Level var2, int var3, int var4) {
            QueenBeeMob var5 = (QueenBeeMob)this.construct(var2, var3, var4);
            var5.resetUniqueID();
            var5.onSpawned(var3, var4);
            AreaFinder var6 = new AreaFinder(var5, 30) {
               public boolean checkPoint(int var1, int var2x) {
                  AbstractBeeHiveObjectEntity var3 = (AbstractBeeHiveObjectEntity)var2.entityManager.getObjectEntity(var1, var2x, AbstractBeeHiveObjectEntity.class);
                  return var3 != null && var3.canTakeMigratingQueen();
               }
            };
            var6.runFinder();
            Point var7 = var6.getFirstFind();
            if (var7 != null) {
               var5.setMigrationApiary(var7.x, var7.y);
            }

            var1.client.network.sendPacket(new PacketSpawnMob(var5));

            for(int var8 = 0; var8 < 10; ++var8) {
               HoneyBeeMob var9 = (HoneyBeeMob)MobRegistry.getMob("honeybee", var2);
               var9.followingQueen.uniqueID = var5.getUniqueID();
               var9.resetUniqueID();
               var9.onSpawned(var3 + GameRandom.globalRandom.getIntOffset(0, 10), var4 + GameRandom.globalRandom.getIntOffset(0, 10));
               var1.client.network.sendPacket(new PacketSpawnMob(var9));
            }

         }
      });
   }
}
