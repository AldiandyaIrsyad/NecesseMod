package necesse.engine.registries;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SalvageStationObjectEntity;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.entity.objectEntity.UpgradeStationObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.PartyConfigContainerForm;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.containerComponent.QuestsContainerForm;
import necesse.gfx.forms.presets.containerComponent.SettlementNameContainerForm;
import necesse.gfx.forms.presets.containerComponent.SleepContainerForm;
import necesse.gfx.forms.presets.containerComponent.TravelContainerComponent;
import necesse.gfx.forms.presets.containerComponent.item.CloudItemContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.CraftingGuideContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.EnchantingScrollContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.PortableMusicPlayerContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.RecipeBookContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.RenameItemContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.WrappingPaperContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.BufferLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CountdownLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CountdownRelayLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CounterLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.DelayLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SRLatchLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SensorLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SimpleLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SoundLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.TFlipFlopLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.TimerLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.AlchemistContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.ElderContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.ExplorerContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.MageContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.MinerContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.PawnbrokerContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.StylistContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ArmorStandContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.CraftingStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.DresserContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledCraftingStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledIncineratorInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledOEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledProcessingInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledRefrigeratorInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.HomestoneContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.MusicPlayerContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.SalvageStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.SignContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.UpgradeStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.inventory.container.BedContainer;
import necesse.inventory.container.Container;
import necesse.inventory.container.PartyConfigContainer;
import necesse.inventory.container.SettlementNameContainer;
import necesse.inventory.container.item.CloudItemContainer;
import necesse.inventory.container.item.CraftingGuideContainer;
import necesse.inventory.container.item.EnchantingScrollContainer;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.container.item.RenameItemContainer;
import necesse.inventory.container.item.WrappingPaperContainer;
import necesse.inventory.container.logicGate.BufferLogicGateContainer;
import necesse.inventory.container.logicGate.CountdownLogicGateContainer;
import necesse.inventory.container.logicGate.CountdownRelayLogicGateContainer;
import necesse.inventory.container.logicGate.CounterLogicGateContainer;
import necesse.inventory.container.logicGate.DelayLogicGateContainer;
import necesse.inventory.container.logicGate.SRLatchLogicGateContainer;
import necesse.inventory.container.logicGate.SensorLogicGateContainer;
import necesse.inventory.container.logicGate.SimpleLogicGateContainer;
import necesse.inventory.container.logicGate.SoundLogicGateContainer;
import necesse.inventory.container.logicGate.TFlipFlopLogicGateContainer;
import necesse.inventory.container.logicGate.TimerLogicGateContainer;
import necesse.inventory.container.mob.AlchemistContainer;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.container.mob.ExplorerContainer;
import necesse.inventory.container.mob.MageContainer;
import necesse.inventory.container.mob.MinerContainer;
import necesse.inventory.container.mob.PawnbrokerContainer;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.mob.StylistContainer;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.object.FueledCraftingStationContainer;
import necesse.inventory.container.object.FueledIncineratorInventoryContainer;
import necesse.inventory.container.object.FueledOEInventoryContainer;
import necesse.inventory.container.object.FueledProcessingOEInventoryContainer;
import necesse.inventory.container.object.FueledRefrigeratorInventoryContainer;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.container.object.MusicPlayerContainer;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.object.SalvageStationContainer;
import necesse.inventory.container.object.SignContainer;
import necesse.inventory.container.object.UpgradeStationContainer;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.teams.PvPTeamsContainer;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelScrollContainer;
import necesse.inventory.container.travel.TravelStoneContainer;
import necesse.level.gameLogicGate.entities.BufferLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownRelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.CounterLogicGateEntity;
import necesse.level.gameLogicGate.entities.DelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameLogicGate.entities.SRLatchLogicGateEntity;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;
import necesse.level.gameLogicGate.entities.SimpleLogicGateEntity;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;
import necesse.level.gameLogicGate.entities.TFlipFlopLogicGateEntity;
import necesse.level.gameLogicGate.entities.TimerLogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class ContainerRegistry extends GameRegistry<ContainerRegistryElement> {
   public static int OE_INVENTORY_CONTAINER;
   public static int ARMOR_STAND_CONTAINER;
   public static int DRESSER_CONTAINER;
   public static int PROCESSING_INVENTORY_CONTAINER;
   public static int MUSIC_PLAYER_CONTAINER;
   public static int CRAFTING_STATION_CONTAINER;
   public static int FUELED_CRAFTING_STATION_CONTAINER;
   public static int FUELED_OE_INVENTORY_CONTAINER;
   public static int FUELED_PROCESSING_STATION_CONTAINER;
   public static int FUELED_REFRIGERATOR_INVENTORY_CONTAINER;
   public static int INCINERATOR_INVENTORY_CONTAINER;
   public static int SIMPLE_LOGIC_GATE_CONTAINER;
   public static int BUFFER_LOGIC_GATE_CONTAINER;
   public static int DELAY_LOGIC_GATE_CONTAINER;
   public static int SRLATCH_LOGIC_GATE_CONTAINER;
   public static int COUNTER_LOGIC_GATE_CONTAINER;
   public static int SENSOR_LOGIC_GATE_CONTAINER;
   public static int SOUND_LOGIC_GATE_CONTAINER;
   public static int TFLIPFLOP_LOGIC_GATE_CONTAINER;
   public static int TIMER_LOGIC_GATE_CONTAINER;
   public static int COUNTDOWN_LOGIC_GATE_CONTAINER;
   public static int COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER;
   public static int ELDER_CONTAINER;
   public static int SHOP_CONTAINER;
   public static int MAGE_CONTAINER;
   public static int STYLIST_CONTAINER;
   public static int PAWNBROKER_CONTAINER;
   public static int EXPLORER_CONTAINER;
   public static int MINER_CONTAINER;
   public static int ALCHEMIST_CONTAINER;
   public static int SIGN_CONTAINER;
   public static int SETTLEMENT_NAME_CONTAINER;
   public static int SETTLEMENT_CONTAINER;
   public static int TRAVEL_CONTAINER;
   public static int TRAVEL_SCROLL_CONTAINER;
   public static int TRAVEL_STONE_CONTAINER;
   public static int CRAFTING_GUIDE_CONTAINER;
   public static int RECIPE_BOOK_CONTAINER;
   public static int ENCHANTING_SCROLL_CONTAINER;
   public static int PARTY_CONFIG_CONTAINER;
   public static int PVP_TEAMS_CONTAINER;
   public static int QUESTS_CONTAINER;
   public static int WRAPPING_PAPER_CONTAINER;
   public static int BED_CONTAINER;
   public static int CLOUD_INVENTORY_CONTAINER;
   public static int HOMESTONE_CONTAINER;
   public static int ITEM_INVENTORY_CONTAINER;
   public static int ITEM_MUSIC_PLAYER_CONTAINER;
   public static int RENAME_ITEM_CONTAINER;
   public static int FALLEN_ALTAR_CONTAINER;
   public static int UPGRADE_STATION_CONTAINER;
   public static int SALVAGE_STATION_CONTAINER;
   public static final ContainerRegistry instance = new ContainerRegistry();

   private ContainerRegistry() {
      super("Container", 65535, false);
   }

   public void registerCore() {
      OE_INVENTORY_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new OEInventoryContainerForm(var0, new OEInventoryContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new OEInventoryContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      ARMOR_STAND_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new ArmorStandContainerForm(var0, new OEInventoryContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new OEInventoryContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      DRESSER_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new DresserContainerForm(var0, new OEInventoryContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new OEInventoryContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      PROCESSING_INVENTORY_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new ProcessingInventoryContainerForm(var0, new OEInventoryContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new OEInventoryContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      MUSIC_PLAYER_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new MusicPlayerContainerForm(var0, new MusicPlayerContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new MusicPlayerContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      CRAFTING_STATION_CONTAINER = registerLOContainer((var0, var1, var2, var3) -> {
         return new CraftingStationContainerForm(var0, new CraftingStationContainer(var0.getClient(), var1, var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new CraftingStationContainer(var0, var1, var2, new PacketReader(var3));
      });
      FUELED_CRAFTING_STATION_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FueledCraftingStationContainerForm(var0, new FueledCraftingStationContainer(var0.getClient(), var1, (FueledInventoryObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new FueledCraftingStationContainer(var0, var1, (FueledInventoryObjectEntity)var2, new PacketReader(var3));
      });
      FUELED_OE_INVENTORY_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FueledOEInventoryContainerForm(var0, new FueledOEInventoryContainer(var0.getClient(), var1, (OEInventory)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new FueledOEInventoryContainer(var0, var1, (OEInventory)var2, new PacketReader(var3));
      });
      FUELED_PROCESSING_STATION_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FueledProcessingInventoryContainerForm(var0, new FueledProcessingOEInventoryContainer(var0.getClient(), var1, (FueledProcessingInventoryObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new FueledProcessingOEInventoryContainer(var0, var1, (FueledProcessingInventoryObjectEntity)var2, new PacketReader(var3));
      });
      FUELED_REFRIGERATOR_INVENTORY_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FueledRefrigeratorInventoryContainerForm(var0, new FueledRefrigeratorInventoryContainer(var0.getClient(), var1, (FueledRefrigeratorObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new FueledRefrigeratorInventoryContainer(var0, var1, (FueledRefrigeratorObjectEntity)var2, new PacketReader(var3));
      });
      INCINERATOR_INVENTORY_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FueledIncineratorInventoryContainerForm(var0, new FueledIncineratorInventoryContainer(var0.getClient(), var1, (FueledIncineratorObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new FueledIncineratorInventoryContainer(var0, var1, (FueledIncineratorObjectEntity)var2, new PacketReader(var3));
      });
      SIMPLE_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new SimpleLogicGateContainerForm(var0, new SimpleLogicGateContainer(var0.getClient(), var1, (SimpleLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new SimpleLogicGateContainer(var0, var1, (SimpleLogicGateEntity)var2);
      });
      BUFFER_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new BufferLogicGateContainerForm(var0, new BufferLogicGateContainer(var0.getClient(), var1, (BufferLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new BufferLogicGateContainer(var0, var1, (BufferLogicGateEntity)var2);
      });
      DELAY_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new DelayLogicGateContainerForm(var0, new DelayLogicGateContainer(var0.getClient(), var1, (DelayLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new DelayLogicGateContainer(var0, var1, (DelayLogicGateEntity)var2);
      });
      SRLATCH_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new SRLatchLogicGateContainerForm(var0, new SRLatchLogicGateContainer(var0.getClient(), var1, (SRLatchLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new SRLatchLogicGateContainer(var0, var1, (SRLatchLogicGateEntity)var2);
      });
      COUNTER_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new CounterLogicGateContainerForm(var0, new CounterLogicGateContainer(var0.getClient(), var1, (CounterLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new CounterLogicGateContainer(var0, var1, (CounterLogicGateEntity)var2);
      });
      SENSOR_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new SensorLogicGateContainerForm(var0, new SensorLogicGateContainer(var0.getClient(), var1, (SensorLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new SensorLogicGateContainer(var0, var1, (SensorLogicGateEntity)var2);
      });
      SOUND_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new SoundLogicGateContainerForm(var0, new SoundLogicGateContainer(var0.getClient(), var1, (SoundLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new SoundLogicGateContainer(var0, var1, (SoundLogicGateEntity)var2);
      });
      TFLIPFLOP_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new TFlipFlopLogicGateContainerForm(var0, new TFlipFlopLogicGateContainer(var0.getClient(), var1, (TFlipFlopLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new TFlipFlopLogicGateContainer(var0, var1, (TFlipFlopLogicGateEntity)var2);
      });
      TIMER_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new TimerLogicGateContainerForm(var0, new TimerLogicGateContainer(var0.getClient(), var1, (TimerLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new TimerLogicGateContainer(var0, var1, (TimerLogicGateEntity)var2);
      });
      COUNTDOWN_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new CountdownLogicGateContainerForm(var0, new CountdownLogicGateContainer(var0.getClient(), var1, (CountdownLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new CountdownLogicGateContainer(var0, var1, (CountdownLogicGateEntity)var2);
      });
      COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER = registerLogicGateContainer((var0, var1, var2, var3) -> {
         return new CountdownRelayLogicGateContainerForm(var0, new CountdownRelayLogicGateContainer(var0.getClient(), var1, (CountdownRelayLogicGateEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new CountdownRelayLogicGateContainer(var0, var1, (CountdownRelayLogicGateEntity)var2);
      });
      ELDER_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new ElderContainerForm(var0, new ElderContainer(var0.getClient(), var1, (ElderHumanMob)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new ElderContainer(var0, var1, (ElderHumanMob)var2, var3);
      });
      SHOP_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new ShopContainerForm(var0, new ShopContainer(var0.getClient(), var1, (HumanShop)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new ShopContainer(var0, var1, (HumanShop)var2, var3);
      });
      MAGE_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new MageContainerForm(var0, new MageContainer(var0.getClient(), var1, (MageHumanMob)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new MageContainer(var0, var1, (MageHumanMob)var2, new PacketReader(var3));
      });
      STYLIST_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new StylistContainerForm(var0, new StylistContainer(var0.getClient(), var1, (StylistHumanMob)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new StylistContainer(var0, var1, (StylistHumanMob)var2, var3);
      });
      PAWNBROKER_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new PawnbrokerContainerForm(var0, new PawnbrokerContainer(var0.getClient(), var1, (HumanShop)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new PawnbrokerContainer(var0, var1, (HumanShop)var2, new PacketReader(var3));
      });
      EXPLORER_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new ExplorerContainerForm(var0, new ExplorerContainer(var0.getClient(), var1, (ExplorerHumanMob)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new ExplorerContainer(var0, var1, (ExplorerHumanMob)var2, new PacketReader(var3));
      });
      MINER_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new MinerContainerForm(var0, new MinerContainer(var0.getClient(), var1, (MinerHumanMob)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new MinerContainer(var0, var1, (MinerHumanMob)var2, new PacketReader(var3));
      });
      ALCHEMIST_CONTAINER = registerMobContainer((var0, var1, var2, var3) -> {
         return new AlchemistContainerForm(var0, new AlchemistContainer(var0.getClient(), var1, (AlchemistHumanMob)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new AlchemistContainer(var0, var1, (AlchemistHumanMob)var2, var3);
      });
      SIGN_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new SignContainerForm(var0, new SignContainer(var0.getClient(), var1, (SignObjectEntity)var2));
      }, (var0, var1, var2, var3, var4) -> {
         return new SignContainer(var0, var1, (SignObjectEntity)var2);
      });
      SETTLEMENT_NAME_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new SettlementNameContainerForm(var0, new SettlementNameContainer(var0.getClient(), var1, var0.getLevel()));
      }, (var0, var1, var2, var3) -> {
         return new SettlementNameContainer(var0, var1, var0.getLevel());
      });
      SETTLEMENT_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new SettlementContainerForm(var0, new SettlementContainer(var0.getClient(), var1, (SettlementFlagObjectEntity)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new SettlementContainer(var0, var1, (SettlementFlagObjectEntity)var2, var3);
      });
      TRAVEL_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new TravelContainerComponent(var0, new TravelContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new TravelContainer(var0, var1, var2);
      });
      TRAVEL_SCROLL_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new TravelContainerComponent(var0, new TravelScrollContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new TravelScrollContainer(var0, var1, var2);
      });
      TRAVEL_STONE_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new TravelContainerComponent(var0, new TravelStoneContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new TravelStoneContainer(var0, var1, var2);
      });
      CRAFTING_GUIDE_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new CraftingGuideContainerForm(var0, new CraftingGuideContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new CraftingGuideContainer(var0, var1, var2);
      });
      RECIPE_BOOK_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new RecipeBookContainerForm(var0, new RecipeBookContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new RecipeBookContainer(var0, var1, var2);
      });
      ENCHANTING_SCROLL_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new EnchantingScrollContainerForm(var0, new EnchantingScrollContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new EnchantingScrollContainer(var0, var1, var2);
      });
      PARTY_CONFIG_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new PartyConfigContainerForm(var0, new PartyConfigContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new PartyConfigContainer(var0, var1, var2);
      });
      PVP_TEAMS_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new PvPTeamsContainerForm(var0, new PvPTeamsContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new PvPTeamsContainer(var0, var1, var2);
      });
      QUESTS_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new QuestsContainerForm(var0, new Container(var0.getClient(), var1));
      }, (var0, var1, var2, var3) -> {
         return new Container(var0, var1);
      });
      WRAPPING_PAPER_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new WrappingPaperContainerForm(var0, new WrappingPaperContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new WrappingPaperContainer(var0, var1, var2);
      });
      BED_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new SleepContainerForm(var0, new BedContainer(var0.getClient(), var1, var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new BedContainer(var0, var1, var2, var3);
      });
      CLOUD_INVENTORY_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new CloudItemContainerForm(var0, new CloudItemContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new CloudItemContainer(var0, var1, var2);
      });
      HOMESTONE_CONTAINER = registerLOContainer((var0, var1, var2, var3) -> {
         return new HomestoneContainerForm(var0, new HomestoneContainer(var0.getClient(), var1, var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new HomestoneContainer(var0, var1, var2, var3);
      });
      ITEM_INVENTORY_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new ItemInventoryContainerForm(var0, new ItemInventoryContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new ItemInventoryContainer(var0, var1, var2);
      });
      ITEM_MUSIC_PLAYER_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new PortableMusicPlayerContainerForm(var0, new ItemInventoryContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new ItemInventoryContainer(var0, var1, var2);
      });
      RENAME_ITEM_CONTAINER = registerContainer((var0, var1, var2) -> {
         return new RenameItemContainerForm(var0, new RenameItemContainer(var0.getClient(), var1, var2));
      }, (var0, var1, var2, var3) -> {
         return new RenameItemContainer(var0, var1, var2);
      });
      FALLEN_ALTAR_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new FallenAltarContainerForm(var0, new FallenAltarContainer(var0.getClient(), var1, (FallenAltarObjectEntity)var2, var3));
      }, (var0, var1, var2, var3, var4) -> {
         return new FallenAltarContainer(var0, var1, (FallenAltarObjectEntity)var2, var3);
      });
      UPGRADE_STATION_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new UpgradeStationContainerForm(var0, new UpgradeStationContainer(var0.getClient(), var1, (UpgradeStationObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new UpgradeStationContainer(var0, var1, (UpgradeStationObjectEntity)var2, new PacketReader(var3));
      });
      SALVAGE_STATION_CONTAINER = registerOEContainer((var0, var1, var2, var3) -> {
         return new SalvageStationContainerForm(var0, new SalvageStationContainer(var0.getClient(), var1, (SalvageStationObjectEntity)var2, new PacketReader(var3)));
      }, (var0, var1, var2, var3, var4) -> {
         return new SalvageStationContainer(var0, var1, (SalvageStationObjectEntity)var2, new PacketReader(var3));
      });
   }

   protected void onRegister(ContainerRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
   }

   public static int registerLogicGateContainer(ClientExtraContainerHandler<LogicGateEntity> var0, ServerExtraContainerHandler<LogicGateEntity> var1) {
      return registerLevelContainer((var1x, var2, var3, var4) -> {
         PacketReader var5 = new PacketReader(var4);
         LogicGateEntity var6 = var3.logicLayer.getEntity(var5.getNextShortUnsigned(), var5.getNextShortUnsigned());
         return var0.handle(var1x, var2, var6, var5.getNextContentPacket());
      }, (var1x, var2, var3, var4, var5) -> {
         PacketReader var6 = new PacketReader(var4);
         LogicGateEntity var7 = var3.logicLayer.getEntity(var6.getNextShortUnsigned(), var6.getNextShortUnsigned());
         return var1.handle(var1x, var2, var7, var6.getNextContentPacket(), var5);
      });
   }

   public static int registerOEContainer(ClientExtraContainerHandler<ObjectEntity> var0, ServerExtraContainerHandler<ObjectEntity> var1) {
      return registerLevelContainer((var1x, var2, var3, var4) -> {
         PacketReader var5 = new PacketReader(var4);
         ObjectEntity var6 = var3.entityManager.getObjectEntity(var5.getNextShortUnsigned(), var5.getNextShortUnsigned());
         return var0.handle(var1x, var2, var6, var5.getNextContentPacket());
      }, (var1x, var2, var3, var4, var5) -> {
         PacketReader var6 = new PacketReader(var4);
         ObjectEntity var7 = var3.entityManager.getObjectEntity(var6.getNextShortUnsigned(), var6.getNextShortUnsigned());
         return var1.handle(var1x, var2, var7, var6.getNextContentPacket(), var5);
      });
   }

   public static int registerLOContainer(ClientExtraContainerHandler<LevelObject> var0, ServerExtraContainerHandler<LevelObject> var1) {
      return registerLevelContainer((var1x, var2, var3, var4) -> {
         PacketReader var5 = new PacketReader(var4);
         LevelObject var6 = var3.getLevelObject(var5.getNextShortUnsigned(), var5.getNextShortUnsigned());
         return var0.handle(var1x, var2, var6, var5.getNextContentPacket());
      }, (var1x, var2, var3, var4, var5) -> {
         PacketReader var6 = new PacketReader(var4);
         LevelObject var7 = var3.getLevelObject(var6.getNextShortUnsigned(), var6.getNextShortUnsigned());
         return var1.handle(var1x, var2, var7, var6.getNextContentPacket(), var5);
      });
   }

   public static int registerMobContainer(ClientExtraContainerHandler<Mob> var0, ServerExtraContainerHandler<Mob> var1) {
      return registerLevelContainer((var1x, var2, var3, var4) -> {
         PacketReader var5 = new PacketReader(var4);
         Mob var6 = (Mob)var3.entityManager.mobs.get(var5.getNextInt(), false);
         return var0.handle(var1x, var2, var6, var5.getNextContentPacket());
      }, (var1x, var2, var3, var4, var5) -> {
         PacketReader var6 = new PacketReader(var4);
         Mob var7 = (Mob)var3.entityManager.mobs.get(var6.getNextInt(), false);
         return var1.handle(var1x, var2, var7, var6.getNextContentPacket(), var5);
      });
   }

   public static int registerLevelContainer(ClientExtraContainerHandler<Level> var0, ServerExtraContainerHandler<Level> var1) {
      return registerContainer((var1x, var2, var3) -> {
         Level var4 = var1x.getLevel();
         return var0.handle(var1x, var2, var4, var3);
      }, (var1x, var2, var3, var4) -> {
         Level var5 = var1x.getServer().world.getLevel(var1x);
         return var1.handle(var1x, var2, var5, var3, var4);
      });
   }

   public static int registerContainer(ClientContainerHandler var0, ServerContainerHandler var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register containers");
      } else {
         return instance.register("container", new ContainerRegistryElement(var0, var1));
      }
   }

   public static void openContainer(int var0, Client var1, int var2, Packet var3) {
      try {
         ContainerRegistryElement var4 = (ContainerRegistryElement)instance.getElement(var0);
         if (var4 != null) {
            var1.openContainerForm(var4.clientHandler.handle(var1, var2, var3));
         }
      } catch (Exception var5) {
         System.err.println("Error trying to open " + var0 + " client side:");
         var5.printStackTrace();
      }

   }

   public static void openContainer(int var0, ServerClient var1, int var2, Packet var3, Object var4) {
      try {
         ContainerRegistryElement var5 = (ContainerRegistryElement)instance.getElement(var0);
         if (var5 != null) {
            var1.openContainer(var5.serverHandler.handle(var1, var2, var3, var4));
         }
      } catch (Exception var6) {
         System.err.println("Error trying to open " + var0 + " server side:");
         var6.printStackTrace();
      }

   }

   public static void openAndSendContainer(ServerClient var0, PacketOpenContainer var1) {
      var0.getServer().network.sendPacket(var1, (ServerClient)var0);
      openContainer(var1.containerID, var0, var1.uniqueSeed, var1.content, var1.serverObject);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((ContainerRegistryElement)var1, var2, var3, var4);
   }

   @FunctionalInterface
   public interface ClientExtraContainerHandler<T> {
      ContainerComponent<? extends Container> handle(Client var1, int var2, T var3, Packet var4);
   }

   @FunctionalInterface
   public interface ServerExtraContainerHandler<T> {
      Container handle(ServerClient var1, int var2, T var3, Packet var4, Object var5);
   }

   @FunctionalInterface
   public interface ClientContainerHandler {
      ContainerComponent<? extends Container> handle(Client var1, int var2, Packet var3);
   }

   @FunctionalInterface
   public interface ServerContainerHandler {
      Container handle(ServerClient var1, int var2, Packet var3, Object var4);
   }

   protected static class ContainerRegistryElement implements IDDataContainer {
      public final IDData data = new IDData();
      public final ClientContainerHandler clientHandler;
      public final ServerContainerHandler serverHandler;

      public ContainerRegistryElement(ClientContainerHandler var1, ServerContainerHandler var2) {
         this.clientHandler = var1;
         this.serverHandler = var2;
      }

      public IDData getIDData() {
         return this.data;
      }
   }
}
