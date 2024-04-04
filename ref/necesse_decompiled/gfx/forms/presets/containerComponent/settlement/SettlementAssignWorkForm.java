package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.EventVariable;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconVarToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.inventory.Inventory;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;
import necesse.level.maps.multiTile.MultiTile;

public class SettlementAssignWorkForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   protected int workContentSubscriptionID;
   public Form work;
   public FormContentBox workContent;
   public SettlementStorageConfigForm storageConfig;
   public Point requestedStorageConfigPos = null;
   public SettlementWorkstationConfigForm workstationConfig;
   public Point requestedWorkstationConfigPos = null;
   public WorkZoneConfigComponent workZoneConfig;
   public int requestedWorkZoneConfigUniqueID;
   public int currentOpenWorkZoneConfigUniqueID;
   protected ArrayList<Point> storagePositions = new ArrayList();
   protected ArrayList<Point> workstationPositions = new ArrayList();
   protected HashMap<Integer, SettlementWorkZone> settlementWorkZones = new HashMap();
   protected List<HudDrawElement> hudElements = new ArrayList();
   protected SettlementAssignWorkToolHandler toolHandler;
   protected boolean overrideShowZones;

   public SettlementAssignWorkForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.toolHandler = new SettlementAssignWorkToolHandler(var1, var2, this);
      this.work = (Form)this.addComponent(new Form("jobs", 500, 250));
      FormFlow var4 = new FormFlow(5);
      int var5 = this.work.getHeight();
      this.workContent = (FormContentBox)this.work.addComponent(new FormContentBox(0, 0, this.work.getWidth(), var5));
      this.workContent.addComponent(new FormLocalLabel("ui", "settlementassignwork", new FontOptions(20), 0, this.work.getWidth() / 2, var4.next(30)));
      FormLocalTextButton var6 = (FormLocalTextButton)this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignstorage", 16, var4.next(40), this.work.getWidth() - 32 - 32));
      var6.onClicked((var1x) -> {
         this.startAssignStorageTool();
      });
      var6.setLocalTooltip(new LocalMessage("ui", "settlementstoragetip"));
      this.workContent.addComponent(this.getHideButton(var6.getX() + var6.getWidth(), var6.getY(), Settings.hideSettlementStorage, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
      FormLocalTextButton var7 = (FormLocalTextButton)this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignworkstation", 16, var4.next(40), this.work.getWidth() - 32 - 32));
      var7.onClicked((var1x) -> {
         this.startAssignWorkstationTool();
      });
      var7.setLocalTooltip(new LocalMessage("ui", "settlementworkstationtip"));
      this.workContent.addComponent(this.getHideButton(var7.getX() + var7.getWidth(), var7.getY(), Settings.hideSettlementWorkstations, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
      FormLocalTextButton var8 = (FormLocalTextButton)this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignforestry", 16, var4.next(40), this.work.getWidth() - 32 - 32));
      var8.onClicked((var1x) -> {
         this.startAssignForestryZoneTool();
      });
      var8.setLocalTooltip(new LocalMessage("ui", "settlementforestrytip"));
      this.workContent.addComponent(this.getHideButton(var8.getX() + var8.getWidth(), var8.getY(), Settings.hideSettlementForestryZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
      FormLocalTextButton var9 = (FormLocalTextButton)this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignhusbandry", 16, var4.next(40), this.work.getWidth() - 32 - 32));
      var9.onClicked((var1x) -> {
         this.startAssignHusbandryZoneTool();
      });
      var9.setLocalTooltip(new LocalMessage("ui", "settlementhusbandrytip"));
      this.workContent.addComponent(this.getHideButton(var9.getX() + var9.getWidth(), var9.getY(), Settings.hideSettlementHusbandryZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
      FormLocalTextButton var10 = (FormLocalTextButton)this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignfertilize", 16, var4.next(40), this.work.getWidth() - 32 - 32));
      var10.onClicked((var1x) -> {
         this.startAssignFertilizeZoneTool();
      });
      var10.setLocalTooltip(new LocalMessage("ui", "settlementfertilizetip"));
      this.workContent.addComponent(this.getHideButton(var10.getX() + var10.getWidth(), var10.getY(), Settings.hideSettlementFertilizeZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
      this.workContent.setContentBox(new Rectangle(0, 0, this.work.getWidth(), var4.next()));
      this.makeCurrent(this.work);
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementStorageEvent.class, (var1) -> {
         this.storagePositions = var1.storage;
         this.updateHudElements();
         if (this.storageConfig != null && this.isCurrent(this.storageConfig) && !this.storagePositions.contains(this.storageConfig.tile)) {
            this.makeCurrent(this.work);
         }

      });
      this.container.onEvent(SettlementSingleStorageEvent.class, (var1) -> {
         if (var1.exists) {
            if (!this.storagePositions.contains(new Point(var1.tileX, var1.tileY))) {
               this.storagePositions.add(new Point(var1.tileX, var1.tileY));
               this.updateHudElements();
            }
         } else if (this.storagePositions.removeIf((var1x) -> {
            return var1x.x == var1.tileX && var1x.y == var1.tileY;
         })) {
            this.updateHudElements();
         }

      });
      this.container.onEvent(SettlementStorageChangeAllowedEvent.class, (var1) -> {
         if (this.storageConfig != null && this.storageConfig.tile.x == var1.tileX && this.storageConfig.tile.y == var1.tileY) {
            if (var1.isItems) {
               Item[] var2 = var1.items;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Item var5 = var2[var4];
                  this.storageConfig.filter.setItemAllowed(var5, var1.allowed);
                  this.storageConfig.filterForm.updateAllButtons();
               }
            } else {
               ItemCategoriesFilter.ItemCategoryFilter var6 = this.storageConfig.filter.getItemCategory(var1.category.id);
               var6.setAllowed(var1.allowed);
               this.storageConfig.filterForm.updateButtons(var1.category);
            }
         }

      });
      this.container.onEvent(SettlementStorageLimitsEvent.class, (var1) -> {
         if (this.storageConfig != null && this.storageConfig.tile.x == var1.tileX && this.storageConfig.tile.y == var1.tileY) {
            if (var1.isItems) {
               this.storageConfig.filter.setItemAllowed(var1.item, var1.limits);
               this.storageConfig.filterForm.updateAllButtons();
            } else {
               ItemCategoriesFilter.ItemCategoryFilter var2 = this.storageConfig.filter.getItemCategory(var1.category.id);
               var2.setMaxItems(var1.maxItems);
               this.storageConfig.filterForm.updateButtons(var1.category);
            }
         }

      });
      this.container.onEvent(SettlementStoragePriorityLimitEvent.class, (var1) -> {
         if (this.storageConfig != null && this.storageConfig.tile.x == var1.tileX && this.storageConfig.tile.y == var1.tileY) {
            if (var1.isPriority) {
               this.storageConfig.updatePrioritySelect(var1.priority);
            } else {
               this.storageConfig.filter.limitMode = var1.limitMode;
               this.storageConfig.filter.maxAmount = var1.limit;
               this.storageConfig.updateLimitMode();
               this.storageConfig.updateLimitInput();
            }
         }

      });
      this.container.onEvent(SettlementStorageFullUpdateEvent.class, (var1) -> {
         if (this.storageConfig != null && this.storageConfig.tile.x == var1.tileX && this.storageConfig.tile.y == var1.tileY) {
            this.storageConfig.updatePrioritySelect(var1.priority);
            this.storageConfig.filter.readPacket(new PacketReader(var1.filterContent));
            this.storageConfig.updateLimitInput();
            this.storageConfig.filterForm.updateAllButtons();
         }

      });
      this.container.onEvent(SettlementWorkstationsEvent.class, (var1) -> {
         this.workstationPositions = var1.workstations;
         this.updateHudElements();
         if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && !this.workstationPositions.contains(this.workstationConfig.tile)) {
            this.makeCurrent(this.work);
         }

      });
      this.container.onEvent(SettlementSingleWorkstationsEvent.class, (var1) -> {
         if (var1.exists) {
            if (!this.workstationPositions.contains(new Point(var1.tileX, var1.tileY))) {
               this.workstationPositions.add(new Point(var1.tileX, var1.tileY));
               this.updateHudElements();
            }
         } else if (this.workstationPositions.removeIf((var1x) -> {
            return var1x.x == var1.tileX && var1x.y == var1.tileY;
         })) {
            this.updateHudElements();
         }

      });
      this.container.onEvent(SettlementOpenStorageConfigEvent.class, (var1) -> {
         if (this.requestedStorageConfigPos != null && this.requestedStorageConfigPos.x == var1.tileX && this.requestedStorageConfigPos.y == var1.tileY) {
            this.setupConfigStorage(var1);
         }

      });
      this.container.onEvent(SettlementOpenWorkstationEvent.class, (var1) -> {
         if (this.requestedWorkstationConfigPos != null && this.requestedWorkstationConfigPos.x == var1.tileX && this.requestedWorkstationConfigPos.y == var1.tileY) {
            this.setupConfigWorkstation(var1);
         }

      });
      this.container.onEvent(SettlementWorkstationEvent.class, (var1) -> {
         if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == var1.tileX && this.workstationConfig.tile.y == var1.tileY) {
            this.workstationConfig.setRecipes(var1.recipes);
         }

      });
      this.container.onEvent(SettlementWorkstationRecipeUpdateEvent.class, (var1) -> {
         if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == var1.tileX && this.workstationConfig.tile.y == var1.tileY) {
            this.workstationConfig.onRecipeUpdate(var1);
         }

      });
      this.container.onEvent(SettlementWorkstationRecipeRemoveEvent.class, (var1) -> {
         if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == var1.tileX && this.workstationConfig.tile.y == var1.tileY) {
            this.workstationConfig.onRecipeRemove(var1);
         }

      });
      this.container.onEvent(SettlementWorkZonesEvent.class, (var1) -> {
         this.settlementWorkZones = var1.zones;
         this.updateHudElements();
      });
      this.container.onEvent(SettlementWorkZoneRemovedEvent.class, (var1) -> {
         if (this.settlementWorkZones.remove(var1.uniqueID) != null) {
            this.updateHudElements();
         }

         if (this.workZoneConfig != null && this.isCurrent((FormComponent)this.workZoneConfig) && this.currentOpenWorkZoneConfigUniqueID == var1.uniqueID) {
            this.makeCurrent(this.work);
         }

      });
      this.container.onEvent(SettlementWorkZoneChangedEvent.class, (var1) -> {
         if (var1.zone.shouldRemove()) {
            if (this.settlementWorkZones.remove(var1.zone.getUniqueID()) != null) {
               this.updateHudElements();
            }
         } else {
            this.settlementWorkZones.put(var1.zone.getUniqueID(), var1.zone);
            this.updateHudElements();
         }

      });
      this.container.onEvent(SettlementRestrictZoneRenameEvent.class, (var1) -> {
         SettlementWorkZone var2 = (SettlementWorkZone)this.settlementWorkZones.get(var1.restrictZoneUniqueID);
         if (var2 != null) {
            var2.setName(var1.name);
         }

      });
      this.container.onEvent(SettlementOpenWorkZoneConfigEvent.class, (var1) -> {
         if (this.requestedWorkZoneConfigUniqueID == var1.uniqueID) {
            this.setupWorkZoneConfig(var1);
         }

      });
   }

   protected FormContentIconVarToggleButton getHideButton(int var1, int var2, final EventVariable<Boolean> var3, final GameMessage var4, final GameMessage var5) {
      int var10004 = var2 + 4;
      FormInputSize var10005 = FormInputSize.SIZE_32;
      ButtonColor var10006 = ButtonColor.BASE;
      Objects.requireNonNull(var3);
      FormContentIconVarToggleButton var6 = new FormContentIconVarToggleButton(var1, var10004, var10005, var10006, var3::get, Settings.UI.button_hidden_big, Settings.UI.button_shown_big, new GameMessage[0]) {
         public GameTooltips getTooltips() {
            return (Boolean)var3.get() ? new StringTooltips(var5.translate()) : new StringTooltips(var4.translate());
         }
      };
      var6.onClicked((var1x) -> {
         var3.set(!(Boolean)var3.get());
      });
      return var6;
   }

   protected void updateHudElements() {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      if (this.containerForm.isCurrent(this)) {
         HashMapArrayList var1 = new HashMapArrayList();
         Level var2 = this.client.getLevel();
         Iterator var3 = this.storagePositions.iterator();

         Point var4;
         while(var3.hasNext()) {
            var4 = (Point)var3.next();
            ObjectEntity var5 = var2.entityManager.getObjectEntity(var4.x, var4.y);
            if (var5 instanceof OEInventory) {
               var1.add(var4, new ObjectValue(Settings.UI.storage, () -> {
                  return !(Boolean)Settings.hideSettlementStorage.get();
               }));
            }
         }

         var3 = this.workstationPositions.iterator();

         while(var3.hasNext()) {
            var4 = (Point)var3.next();
            GameObject var12 = var2.getObject(var4.x, var4.y);
            if (var12 instanceof SettlementWorkstationObject) {
               var1.add(var4, new ObjectValue(Settings.UI.workstation, () -> {
                  return !(Boolean)Settings.hideSettlementWorkstations.get();
               }));
            }
         }

         var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var10 = (Map.Entry)var3.next();
            final Point var13 = (Point)var10.getKey();
            final ArrayList var6 = (ArrayList)var10.getValue();
            GameObject var7 = var2.getObject(var13.x, var13.y);
            final MultiTile var8 = var7.getMultiTile(var2, var13.x, var13.y);
            HudDrawElement var9 = new HudDrawElement() {
               public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
                  int var4 = (int)var6.stream().filter((var0) -> {
                     return ((BooleanSupplier)var0.value).getAsBoolean();
                  }).count();
                  if (var4 > 0) {
                     Point var5 = var8.getCenterLevelPos(var13.x, var13.y);
                     final DrawOptionsList var6x = new DrawOptionsList();
                     Color var7 = new Color(255, 255, 255);
                     var6x.add(HUD.tileBoundOptions(var2, var7, false, var8.getTileRectangle(var13.x, var13.y)));
                     int var8x = (int)Math.ceil(Math.sqrt((double)var4));
                     int var9 = var4 / var8x;
                     float var10 = Math.min(Math.min((float)var8.width / (float)var8x, (float)var8.height / (float)var9), 1.0F);
                     int var11 = (int)(32.0F * var10);
                     int var12 = var11 * var8x;
                     int var13x = var11 * var9;

                     for(int var14 = 0; var14 < var6.size(); ++var14) {
                        if (((BooleanSupplier)((ObjectValue)var6.get(var14)).value).getAsBoolean()) {
                           int var15 = var14 % var8x;
                           int var16 = var14 / var8x;
                           var6x.add(((GameTexture)((ObjectValue)var6.get(var14)).object).initDraw().color(var7).size(var11).posMiddle(var2.getDrawX(var5.x) - var12 / 2 + var15 * var11 + var11 / 2, var2.getDrawY(var5.y) - var13x / 2 + var16 * var11 + var11 / 2));
                        }
                     }

                     var1.add(new SortedDrawable() {
                        public int getPriority() {
                           return -1000000;
                        }

                        public void draw(TickManager var1) {
                           var6x.draw();
                        }
                     });
                  }
               }
            };
            var2.hudManager.addElement(var9);
            this.hudElements.add(var9);
         }

         var3 = this.settlementWorkZones.values().iterator();

         while(var3.hasNext()) {
            SettlementWorkZone var11 = (SettlementWorkZone)var3.next();
            HudDrawElement var14 = var11.getHudDrawElement(-999500, () -> {
               return this.overrideShowZones;
            });
            var2.hudManager.addElement(var14);
            this.hudElements.add(var14);
         }

      }
   }

   protected void startAssignStorageTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new SelectTileGameTool(this.client.getLevel(), new LocalMessage("ui", "settlementassignstorage")) {
         public DrawOptions getIconTexture(Color var1, int var2, int var3) {
            return Settings.UI.storage.initDraw().color(var1).posMiddle(var2, var3);
         }

         public boolean onSelected(InputEvent var1, TilePosition var2) {
            if (var2 == null) {
               return true;
            } else {
               if (var1.state) {
                  LevelObject var3 = (LevelObject)var2.object().getMultiTile().getMasterLevelObject(this.level, var2.tileX, var2.tileY).orElse((Object)null);
                  if (var3 != null) {
                     SettlementAssignWorkForm.this.container.assignStorage.runAndSend(var3.tileX, var3.tileY);
                     return true;
                  }
               }

               return false;
            }
         }

         public GameMessage isValidTile(TilePosition var1) {
            this.lastHoverBounds = null;
            LevelObject var2 = (LevelObject)var1.object().getMultiTile().getMasterLevelObject(this.level, var1.tileX, var1.tileY).orElse((Object)null);
            if (var2 != null) {
               this.lastHoverBounds = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
               if (SettlementAssignWorkForm.this.storagePositions.contains(new Point(var2.tileX, var2.tileY))) {
                  return new LocalMessage("ui", "settlementalreadyinventory");
               }

               ObjectEntity var3 = this.level.entityManager.getObjectEntity(var2.tileX, var2.tileY);
               if (var3 instanceof OEInventory) {
                  if (((OEInventory)var3).getSettlementStorage() != null) {
                     return null;
                  }

                  return new LocalMessage("ui", "settlementcannotinventory");
               }
            }

            return new LocalMessage("ui", "settlementnotinventory");
         }
      }, this);
   }

   public void openStorageConfig(int var1, int var2) {
      this.requestedStorageConfigPos = new Point(var1, var2);
      this.container.openStorage.runAndSend(var1, var2);
   }

   public void setupConfigStorage(final SettlementOpenStorageConfigEvent var1) {
      Inventory var3 = null;
      GameMessage var4 = this.client.getLevel().getObjectName(var1.tileX, var1.tileY);
      ObjectEntity var5 = this.client.getLevel().entityManager.getObjectEntity(var1.tileX, var1.tileY);
      ItemCategoriesFilter var2;
      if (var5 instanceof OEInventory) {
         var2 = var1.getFilter((OEInventory)var5);
         var3 = ((OEInventory)var5).getInventory();
         var4 = ((OEInventory)var5).getInventoryName();
      } else {
         var2 = new ItemCategoriesFilter(false);
      }

      int var6 = this.container.subscribeStorage.subscribe(new Point(var1.tileX, var1.tileY));
      SettlementStorageConfigForm var7 = (SettlementStorageConfigForm)this.addComponent(new SettlementStorageConfigForm("storageConfig", 500, 350, new Point(var1.tileX, var1.tileY), this.client, var3, var4, var2, var1.priority) {
         public void onItemsChanged(Item[] var1x, boolean var2) {
            SettlementAssignWorkForm.this.container.changeAllowedStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onItemLimitsChanged(Item var1x, ItemCategoriesFilter.ItemLimits var2) {
            SettlementAssignWorkForm.this.container.changeLimitsStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, boolean var2) {
            SettlementAssignWorkForm.this.container.changeAllowedStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, int var2) {
            SettlementAssignWorkForm.this.container.changeLimitsStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onFullChange(ItemCategoriesFilter var1x, int var2) {
            SettlementAssignWorkForm.this.container.fullUpdateSettlementStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onPriorityChange(int var1x) {
            SettlementAssignWorkForm.this.container.priorityLimitStorage.runAndSendPriority(var1.tileX, var1.tileY, var1x);
         }

         public void onLimitChange(ItemCategoriesFilter.ItemLimitMode var1x, int var2) {
            SettlementAssignWorkForm.this.container.priorityLimitStorage.runAndSendLimit(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onRemove() {
            SettlementAssignWorkForm.this.container.removeStorage.runAndSend(var1.tileX, var1.tileY);
            SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
         }

         public void onBack() {
            SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
         }
      }, (var2x, var3x) -> {
         if (!var3x) {
            this.removeComponent(var2x);
            this.storageConfig = null;
            this.container.subscribeStorage.unsubscribe(var6);
         }

      });
      this.makeCurrent(var7);
      this.storageConfig = var7;
      this.storageConfig.setPosInventory();
      this.requestedStorageConfigPos = null;
   }

   protected void startAssignWorkstationTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new SelectTileGameTool(this.client.getLevel(), new LocalMessage("ui", "settlementassignworkstation")) {
         public DrawOptions getIconTexture(Color var1, int var2, int var3) {
            return Settings.UI.workstation.initDraw().color(var1).posMiddle(var2, var3);
         }

         public boolean onSelected(InputEvent var1, TilePosition var2) {
            if (var2 == null) {
               return true;
            } else {
               if (var1.state) {
                  LevelObject var3 = (LevelObject)var2.object().getMultiTile().getMasterLevelObject(this.level, var2.tileX, var2.tileY).orElse((Object)null);
                  if (var3 != null) {
                     SettlementAssignWorkForm.this.container.assignWorkstation.runAndSend(var3.tileX, var3.tileY);
                     return true;
                  }
               }

               return false;
            }
         }

         public GameMessage isValidTile(TilePosition var1) {
            this.lastHoverBounds = null;
            LevelObject var2 = (LevelObject)var1.object().getMultiTile().getMasterLevelObject(this.level, var1.tileX, var1.tileY).orElse((Object)null);
            if (var2 != null) {
               this.lastHoverBounds = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
               if (SettlementAssignWorkForm.this.workstationPositions.contains(new Point(var2.tileX, var2.tileY))) {
                  return new LocalMessage("ui", "settlementalreadyworkstation");
               } else {
                  return var2.object instanceof SettlementWorkstationObject ? null : new LocalMessage("ui", "settlementcannotworkstation");
               }
            } else {
               return new LocalMessage("ui", "settlementnotworkstation");
            }
         }
      }, this);
   }

   public void openWorkstationConfig(int var1, int var2) {
      this.requestedWorkstationConfigPos = new Point(var1, var2);
      this.container.openWorkstation.runAndSend(var1, var2);
   }

   public void setupConfigWorkstation(final SettlementOpenWorkstationEvent var1) {
      SettlementWorkstationLevelObject var2 = null;
      GameObject var3 = this.client.getLevel().getObject(var1.tileX, var1.tileY);
      String var4 = var3.getDisplayName();
      if (var3 instanceof SettlementWorkstationObject) {
         var2 = new SettlementWorkstationLevelObject(this.client.getLevel(), var1.tileX, var1.tileY);
      }

      if (var2 != null) {
         int var5 = this.container.subscribeWorkstation.subscribe(new Point(var1.tileX, var1.tileY));
         SettlementWorkstationConfigForm var6 = (SettlementWorkstationConfigForm)this.addComponent(new SettlementWorkstationConfigForm("workstationConfig", 400, 240, new Point(var1.tileX, var1.tileY), this.client, new StaticMessage(var4), this.container.client.playerMob, var2, var1.recipes) {
            public void onSubmitRemove(int var1x) {
               SettlementAssignWorkForm.this.container.removeWorkstationRecipe.runAndSend(var1.tileX, var1.tileY, var1x);
            }

            public void onSubmitUpdate(int var1x, SettlementWorkstationRecipe var2) {
               SettlementAssignWorkForm.this.container.updateWorkstationRecipe.runAndSend(var1.tileX, var1.tileY, var1x, var2);
            }

            public void onRemove() {
               SettlementAssignWorkForm.this.container.removeWorkstation.runAndSend(var1.tileX, var1.tileY);
               SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
            }

            public void onBack() {
               SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
            }
         }, (var2x, var3x) -> {
            if (!var3x) {
               this.container.subscribeWorkstation.unsubscribe(var5);
               this.removeComponent(var2x);
               this.workstationConfig = null;
            }

         });
         this.makeCurrent(var6);
         this.workstationConfig = var6;
         this.workstationConfig.setPosInventory();
         this.requestedWorkstationConfigPos = null;
      }

   }

   public void openWorkZoneConfig(SettlementWorkZone var1) {
      this.requestedWorkZoneConfigUniqueID = var1.getUniqueID();
      this.container.openWorkZoneConfig.runAndSend(var1.getUniqueID());
   }

   public void setupWorkZoneConfig(SettlementOpenWorkZoneConfigEvent var1) {
      SettlementWorkZone var2 = (SettlementWorkZone)this.settlementWorkZones.get(var1.uniqueID);
      if (var2 != null) {
         WorkZoneConfigComponent var3 = var2.getSettingsForm(this, () -> {
            this.makeCurrent(this.work);
         }, new PacketReader(var1.configPacket));
         if (var3 != null) {
            int var4 = this.container.subscribeWorkZoneConfig.subscribe(var2);
            var2.subscribeConfigEvents(this.container, () -> {
               return this.container.subscribeWorkZoneConfig.isActive(var4);
            });
            FormComponent var5 = this.addComponent((FormComponent)var3, (var2x, var3x) -> {
               if (!var3x) {
                  this.container.subscribeWorkZoneConfig.unsubscribe(var4);
                  this.removeComponent(var2x);
                  this.workZoneConfig = null;
                  this.currentOpenWorkZoneConfigUniqueID = 0;
               }

            });
            this.makeCurrent(var5);
            this.workZoneConfig = var3;
            this.currentOpenWorkZoneConfigUniqueID = var1.uniqueID;
         }

         this.requestedWorkZoneConfigUniqueID = 0;
      }

   }

   public void startAssignForestryZoneTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()) {
         public Stream<SettlementWorkZone> streamEditZones() {
            return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter((var0) -> {
               return var0.getID() == SettlementWorkZoneRegistry.FORESTRY_ID;
            });
         }

         public void onCreatedNewZone(Rectangle var1, Point var2) {
            SettlementWorkZone var3 = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.FORESTRY_ID);
            var3.expandZone(this.level, var1, var2, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (!var3.shouldRemove()) {
               var3.generateUniqueID((var1x) -> {
                  return SettlementAssignWorkForm.this.settlementWorkZones.containsKey(var1x);
               });
               SettlementAssignWorkForm.this.settlementWorkZones.put(var3.getUniqueID(), var3);
               SettlementAssignWorkForm.this.container.createWorkZone.runAndSend(var3.getID(), var3.getUniqueID(), var1, var2);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var3.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onRemovedZone(SettlementWorkZone var1, Rectangle var2) {
            if (var1.shrinkZone(this.level, var2)) {
               SettlementAssignWorkForm.this.container.shrinkWorkZone.runAndSend(var1.getUniqueID(), var2);
               if (var1.shouldRemove()) {
                  SettlementAssignWorkForm.this.settlementWorkZones.remove(var1.getUniqueID());
               }

               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onExpandedZone(SettlementWorkZone var1, Rectangle var2, Point var3) {
            boolean var4 = var1.expandZone(this.level, var2, var3, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (var4) {
               SettlementAssignWorkForm.this.container.expandWorkZone.runAndSend(var1.getUniqueID(), var2, var3);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var1.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void isCancelled() {
            super.isCancelled();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }

         public void isCleared() {
            super.isCleared();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }
      }, this);
      this.overrideShowZones = true;
   }

   public void startAssignHusbandryZoneTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()) {
         public Stream<SettlementWorkZone> streamEditZones() {
            return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter((var0) -> {
               return var0.getID() == SettlementWorkZoneRegistry.HUSBANDRY_ID;
            });
         }

         public void onCreatedNewZone(Rectangle var1, Point var2) {
            SettlementWorkZone var3 = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.HUSBANDRY_ID);
            var3.expandZone(this.level, var1, var2, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (!var3.shouldRemove()) {
               var3.generateUniqueID((var1x) -> {
                  return SettlementAssignWorkForm.this.settlementWorkZones.containsKey(var1x);
               });
               SettlementAssignWorkForm.this.settlementWorkZones.put(var3.getUniqueID(), var3);
               SettlementAssignWorkForm.this.container.createWorkZone.runAndSend(var3.getID(), var3.getUniqueID(), var1, var2);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var3.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onRemovedZone(SettlementWorkZone var1, Rectangle var2) {
            if (var1.shrinkZone(this.level, var2)) {
               SettlementAssignWorkForm.this.container.shrinkWorkZone.runAndSend(var1.getUniqueID(), var2);
               if (var1.shouldRemove()) {
                  SettlementAssignWorkForm.this.settlementWorkZones.remove(var1.getUniqueID());
               }

               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onExpandedZone(SettlementWorkZone var1, Rectangle var2, Point var3) {
            boolean var4 = var1.expandZone(this.level, var2, var3, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (var4) {
               SettlementAssignWorkForm.this.container.expandWorkZone.runAndSend(var1.getUniqueID(), var2, var3);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var1.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void isCancelled() {
            super.isCancelled();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }

         public void isCleared() {
            super.isCleared();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }
      }, this);
      this.overrideShowZones = true;
   }

   public void startAssignFertilizeZoneTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()) {
         public Stream<SettlementWorkZone> streamEditZones() {
            return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter((var0) -> {
               return var0.getID() == SettlementWorkZoneRegistry.FERTILIZE_ID;
            });
         }

         public void onCreatedNewZone(Rectangle var1, Point var2) {
            SettlementWorkZone var3 = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.FERTILIZE_ID);
            var3.expandZone(this.level, var1, var2, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (!var3.shouldRemove()) {
               var3.generateUniqueID((var1x) -> {
                  return SettlementAssignWorkForm.this.settlementWorkZones.containsKey(var1x);
               });
               SettlementAssignWorkForm.this.settlementWorkZones.put(var3.getUniqueID(), var3);
               SettlementAssignWorkForm.this.container.createWorkZone.runAndSend(var3.getID(), var3.getUniqueID(), var1, var2);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var3.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onRemovedZone(SettlementWorkZone var1, Rectangle var2) {
            if (var1.shrinkZone(this.level, var2)) {
               SettlementAssignWorkForm.this.container.shrinkWorkZone.runAndSend(var1.getUniqueID(), var2);
               if (var1.shouldRemove()) {
                  SettlementAssignWorkForm.this.settlementWorkZones.remove(var1.getUniqueID());
               }

               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void onExpandedZone(SettlementWorkZone var1, Rectangle var2, Point var3) {
            boolean var4 = var1.expandZone(this.level, var2, var3, (var1x, var2x) -> {
               return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (var4) {
               SettlementAssignWorkForm.this.container.expandWorkZone.runAndSend(var1.getUniqueID(), var2, var3);
               SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = var1.getUniqueID();
               SettlementAssignWorkForm.this.updateHudElements();
            }

         }

         public void isCancelled() {
            super.isCancelled();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }

         public void isCleared() {
            super.isCleared();
            SettlementAssignWorkForm.this.overrideShowZones = false;
         }
      }, this);
      this.overrideShowZones = true;
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this.work);
      if (this.storageConfig != null) {
         this.storageConfig.setPosInventory();
      }

      if (this.workstationConfig != null) {
         this.workstationConfig.setPosInventory();
      }

   }

   public void onSetCurrent(boolean var1) {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      if (var1) {
         this.makeCurrent(this.work);
         this.workContentSubscriptionID = this.container.subscribeWorkContent.subscribe();
      } else {
         this.container.subscribeWorkContent.unsubscribe(this.workContentSubscriptionID);
         Screen.clearGameTools(this);
      }

   }

   public void dispose() {
      Screen.clearGameTools(this);
      this.hudElements.forEach(HudDrawElement::remove);
      super.dispose();
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementassignwork");
   }

   public String getTypeString() {
      return "assignwork";
   }

   public SettlementToolHandler getToolHandler() {
      return this.toolHandler;
   }
}
