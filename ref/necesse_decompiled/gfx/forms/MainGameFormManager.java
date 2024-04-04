package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameLaunch;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketOpenPartyConfig;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketOpenQuests;
import necesse.engine.network.packet.PacketPlayerAutoOpenDoors;
import necesse.engine.network.packet.PacketPlayerHotbarLocked;
import necesse.engine.network.packet.PacketPlayerRespawnRequest;
import necesse.engine.network.packet.PacketRequestTravel;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormBuffHud;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormContentIconVarToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerMountSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerPlayerArmorSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerToolbarSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerTrashSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerTrinketSlot;
import necesse.gfx.forms.components.lists.FormContainerCraftingList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.ButtonToolbarForm;
import necesse.gfx.forms.presets.ChatBoxForm;
import necesse.gfx.forms.presets.CraftFilterForm;
import necesse.gfx.forms.presets.CurrentModifiersForm;
import necesse.gfx.forms.presets.MapForm;
import necesse.gfx.forms.presets.PauseMenuForm;
import necesse.gfx.forms.presets.ScoreboardForm;
import necesse.gfx.forms.presets.TestCrashReportForm;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.sidebar.SidebarComponent;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.recipe.Tech;

public class MainGameFormManager extends FormManager {
   private static final FontOptions totalArmorFontOptions;
   private MainGame mainGame;
   private Client client;
   private boolean lastShowToolbar;
   private boolean lastInventoryExtended;
   private boolean lastIsDead;
   private boolean lastShowMap;
   private boolean lastIsRunning;
   private TravelDir lastTravelDir;
   public Form inventory;
   public Form toolbar;
   public Form equipment;
   public Form leftQuickbar;
   public Form crafting;
   public Form travel;
   public Form death;
   public ButtonToolbarForm rightQuickbar;
   public ButtonToolbarForm alwaysPartyConfigButtonForm;
   public ChatBoxForm chat;
   public ContainerComponent<?> focus;
   public CraftFilterForm filters;
   public FormComponentList buffsContainer;
   public FormBuffHud importantBuffs;
   public FormBuffHud unimportantBuffs;
   public PauseMenuForm pauseMenu;
   public ScoreboardForm scoreboard;
   private FormContentBox sidebarBox;
   private LinkedList<SidebarComponent> sidebar;
   public MapForm minimap;
   public MapForm fullMap;
   private TutorialSidebarForm tutorialSidebar;
   private CurrentModifiersForm modifiersForm;
   private static Point lastModifiersFormPos;
   public DebugForm debugForm;
   private FormLocalTextButton travelButton;
   private FormLocalTextButton respawnButton;
   private long respawnButtonCD;
   private FormContentIconToggleButton craftFiltersButton;
   private InventoryItem lastCheckedItemSidebar;

   public MainGameFormManager(MainGame var1, Client var2) {
      this.mainGame = var1;
      this.client = var2;
      this.sidebar = new LinkedList();
   }

   private void removeFocusForm(boolean var1) {
      if (this.focus != null) {
         this.removeComponent((FormComponent)this.focus);
         this.focus.onContainerClosed();
         if (var1) {
            this.setupInventoryForm(this.client.getContainer());
         }
      }

      this.focus = null;
      this.updateActive(true);
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public void removeFocusForm() {
      this.removeFocusForm(true);
   }

   public FormComponent getFocusForm() {
      return (FormComponent)this.focus;
   }

   public boolean hasFocusForm() {
      return this.focus != null;
   }

   public void setFocusForm(ContainerComponent<?> var1) {
      this.removeFocusForm(false);
      this.focus = var1;
      this.addComponent((FormComponent)this.focus);
      ((FormComponent)this.focus).tryPutOnTop();
      var1.onWindowResized();
      this.setupInventoryForm(var1.getContainer());
      this.updateActive(true);
   }

   public void addSidebar(SidebarComponent var1) {
      if (!this.sidebarBox.hasComponent((FormComponent)var1)) {
         this.sidebarBox.addComponent((FormComponent)var1);
         this.sidebar.add(var1);
         var1.onAdded(this.client);
         this.fixSidebar();
         ControllerInput.submitNextRefreshFocusEvent();
      }
   }

   public void removeSidebar(SidebarComponent var1) {
      if (this.sidebar.contains(var1)) {
         var1.onRemoved(this.client);
         this.sidebar.remove(var1);
         this.sidebarBox.removeComponent((FormComponent)var1);
         this.fixSidebar();
         ControllerInput.submitNextRefreshFocusEvent();
      }

   }

   public int getSidebarWidth() {
      return this.sidebarBox.getWidth() - 10;
   }

   public void frameTick(TickManager var1) {
      super.frameTick(var1);
      this.updateActive(false);
   }

   public void updateActive(boolean var1) {
      PlayerMob var2 = this.client.getPlayer();
      TravelDir var3 = this.mainGame.canTravel(this.client, var2) ? TravelContainer.getTravelDir(var2) : null;
      boolean var4 = var2.isInventoryExtended() && (this.focus == null || this.focus.shouldShowInventory());
      boolean var5 = this.focus == null || this.focus.shouldShowToolbar();
      boolean var6 = this.client.isDead;
      boolean var7 = this.mainGame.showMap();
      boolean var8 = this.mainGame.isRunning();
      ArrayList var9 = new ArrayList();
      Stream var10000 = this.sidebar.stream().filter((var1x) -> {
         return !var1x.isValid(this.client);
      });
      Objects.requireNonNull(var9);
      var10000.forEach(var9::add);
      var9.forEach(this::removeSidebar);
      InventoryItem var10 = var2.getSelectedItem();
      if (var10 != this.lastCheckedItemSidebar) {
         if (var10 != null) {
            SidebarForm var11 = var10.item.getSidebar(var10);
            if (var11 != null) {
               this.addSidebar(var11);
            }
         }

         this.lastCheckedItemSidebar = var10;
      }

      if (var1 || this.lastTravelDir != var3 || this.lastShowMap != var7) {
         this.lastTravelDir = var3;
         if (var3 != null && !this.hasFocusForm() && var8 && !var7) {
            if (this.travel.isHidden()) {
               this.setNextControllerFocus(new ControllerFocusHandler[]{this.travelButton});
               ControllerInput.submitNextRefreshFocusEvent();
            }

            this.travel.setHidden(false);
            this.travelButton.setLocalization(var3.travelMessage);
         } else {
            this.travel.setHidden(true);
         }
      }

      if (var1 || this.lastShowToolbar != var5 || this.lastInventoryExtended != var4 || this.lastIsDead != var6 || this.lastShowMap != var7 || this.lastIsRunning != var8) {
         this.lastShowToolbar = var5;
         this.lastInventoryExtended = var4;
         this.lastIsDead = var6;
         this.lastShowMap = var7;
         this.lastIsRunning = var8;
         if (this.focus != null) {
            this.focus.setHidden(var7);
         }

         this.toolbar.setHidden(!var8 || var6 || var7 || !var5);
         this.inventory.setHidden(this.toolbar.isHidden() || !var4);
         if (this.modifiersForm != null) {
            this.modifiersForm.setHidden(!var8 || var7);
         }

         this.buffsContainer.setHidden(!var8 || var7);
         this.minimap.setHidden(!var8 || var7);
         this.fullMap.setHidden(!var8 || !var7);
         this.death.setHidden(!var6);
         if (var6 && !this.mainGame.isRunning()) {
            this.mainGame.setRunning(true);
         }
      }

      boolean var13 = !this.client.adventureParty.isEmpty() || var2.getInv().hasPartyItems();
      boolean var12 = !var13 || !this.rightQuickbar.isHidden() || this.toolbar.isHidden();
      if (this.alwaysPartyConfigButtonForm.isHidden() != var12) {
         this.alwaysPartyConfigButtonForm.setHidden(var12);
      }

      if (!this.death.isHidden()) {
         if (this.respawnButtonCD >= System.currentTimeMillis()) {
            this.respawnButton.setActive(false);
         } else {
            this.respawnButton.setActive(this.client.canRespawn() || this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE);
         }

         if (this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE) {
            this.respawnButton.setLocalization("ui", "disconnectbutton");
         } else if (this.client.canRespawn()) {
            this.respawnButton.setLocalization("ui", "respawn");
         } else {
            this.respawnButton.setLocalization(new LocalMessage("ui", "respawnin", new Object[]{"seconds", this.client.getRespawnTimeLeft() / 1000L + 1L}));
         }
      }

   }

   public void fixSidebar() {
      this.sidebarBox.setWidth(Math.max(100, Screen.getHudWidth() / 3));
      int var1 = 0;

      SidebarComponent var3;
      for(Iterator var2 = this.sidebar.iterator(); var2.hasNext(); var1 += ((FormComponent)var3).getBoundingBox().height + 10) {
         var3 = (SidebarComponent)var2.next();
         var3.onSidebarUpdate(5, var1);
      }

      Rectangle var4 = this.sidebarBox.getContentBoxToFitComponents();
      this.sidebarBox.setContentBox(new Rectangle(0, -5, var4.width, var4.height + 10));
      this.sidebarBox.setHeight(Math.max(100, Math.min(this.sidebarBox.getContentBox().height, Screen.getHudHeight() / 2)));
   }

   public void setTutorialContent(GameMessage var1, GameMessage var2) {
      this.setTutorialContent(var1, var2, (FormEventListener)null);
   }

   public void setTutorialContent(GameMessage var1, GameMessage var2, FormEventListener<FormInputEvent> var3) {
      this.tutorialSidebar.setContent(var1, var2);
      this.tutorialSidebar.buttonEvent = var3;
      this.addSidebar(this.tutorialSidebar);
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public void clearTutorial() {
      if (this.tutorialSidebar != null) {
         this.removeSidebar(this.tutorialSidebar);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      Screen.submitNextMoveEvent();
      this.updateInventoryFormPositions();
      this.rightQuickbar.setPosition(this.toolbar.getX() + this.toolbar.getWidth() + Settings.UI.formSpacing, this.toolbar.getY() + this.toolbar.getHeight() - this.rightQuickbar.getHeight());
      this.alwaysPartyConfigButtonForm.setPosition(this.toolbar.getX() + this.toolbar.getWidth() + Settings.UI.formSpacing, this.toolbar.getY() + this.toolbar.getHeight() - this.alwaysPartyConfigButtonForm.getHeight());
      this.crafting.setPosition(this.inventory.getX() + this.inventory.getWidth() + Settings.UI.formSpacing, this.inventory.getY());
      this.death.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 + 100);
      this.sidebarBox.setPosition(10, 30);
      this.fixSidebar();
   }

   public void resetTravelCooldown() {
      this.travelButton.stopCooldown();
   }

   public void draw(TickManager var1, PlayerMob var2) {
      if (!this.equipment.isHidden()) {
         String var3 = Localization.translate("ui", "totalarmor", "armor", (Object)Math.round(var2.getArmor()));
         FontManager.bit.drawString((float)this.equipment.getX(), (float)(this.equipment.getY() - 16 - Settings.UI.formSpacing + Settings.UI.form.edgeMargin), var3, totalArmorFontOptions);
      }

      super.draw(var1, var2);
   }

   public void setup() {
      PlayerMob var1 = this.client.getPlayer();
      if (GameLaunch.launchOptions.containsKey("testcrash")) {
         this.addComponent(new TestCrashReportForm());
      }

      this.chat = (ChatBoxForm)this.addComponent(new ChatBoxForm(this.client, "chat"), -1);
      this.chat.onWindowResized();
      this.debugForm = new DebugForm("debug", this.client, this.mainGame);
      this.addComponent(this.debugForm);
      this.debugForm.setHidden(true);
      MainGame.debugForm = this.debugForm;
      this.pauseMenu = (PauseMenuForm)this.addComponent(new PauseMenuForm(this.mainGame, this.client));
      this.pauseMenu.setHidden(this.mainGame.isRunning());
      GameMessageBuilder var2 = (new GameMessageBuilder()).append(Control.OPEN_ADVENTURE_PARTY.text).append("\n").append((GameMessage)(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + Control.OPEN_ADVENTURE_PARTY.id + "]")));
      this.alwaysPartyConfigButtonForm = (ButtonToolbarForm)this.addComponent(new ButtonToolbarForm("partyConfigButtonForm"));
      this.alwaysPartyConfigButtonForm.addButton("quickbarParty", Settings.UI.party_inventory_icon, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.client.network.sendPacket(new PacketOpenPartyConfig());
      }, var2);
      this.rightQuickbar = (ButtonToolbarForm)this.addComponent(new ButtonToolbarForm("rightQuickbar"));
      this.rightQuickbar.addButton("quickbarParty", Settings.UI.party_inventory_icon, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.client.network.sendPacket(new PacketOpenPartyConfig());
      }, var2);
      this.rightQuickbar.addButton("quickbarTeams", Settings.UI.quickbar_teams, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.client.network.sendPacket(new PacketOpenPvPTeams());
         PvPTeamsContainerForm.pauseGameOnClose = false;
      }, new LocalMessage("ui", "pvpandteams"));
      GameMessageBuilder var3 = (new GameMessageBuilder()).append(Control.OPEN_SETTLEMENT.text).append("\n").append((GameMessage)(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + Control.OPEN_SETTLEMENT.id + "]")));
      this.rightQuickbar.addButton("quickbarSettlement", Settings.UI.quickbar_settlement, (var1x) -> {
         this.client.network.sendPacket(new PacketSettlementOpen());
         ((FormButton)var1x.from).startCooldown(500);
      }, var3);
      this.rightQuickbar.addButton("quickbarQuests", Settings.UI.quickbar_quests, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.client.network.sendPacket(new PacketOpenQuests());
      }, new LocalMessage("ui", "quests"));
      GameMessageBuilder var4 = (new GameMessageBuilder()).append(Control.SHOW_MAP.text).append("\n").append((GameMessage)(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + Control.SHOW_MAP.id + "]")));
      this.rightQuickbar.addButton("quickbarIslandmap", Settings.UI.quickbar_island_map, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.mainGame.setShowMap(true);
      }, var4);
      GameMessageBuilder var5 = (new GameMessageBuilder()).append(Control.SHOW_WORLD_MAP.text).append("\n").append((GameMessage)(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + Control.SHOW_WORLD_MAP.id + "]")));
      this.rightQuickbar.addButton("quickbarWorldmap", Settings.UI.quickbar_world_map, (var1x) -> {
         ((FormButton)var1x.from).startCooldown(500);
         this.client.network.sendPacket(new PacketRequestTravel(TravelDir.None));
      }, var5);
      GameMessageBuilder var6 = (new GameMessageBuilder()).append(Control.SMART_MINING.text).append("\n").append((GameMessage)(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + Control.SMART_MINING.id + "]")));
      FormContentIconVarToggleButton var7 = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> {
         return Settings.smartMining;
      }, Settings.UI.quickbar_mining_icon, new GameMessage[]{var6});
      var7.controllerFocusHashcode = "quickbarSmartMining";
      var7.onClicked((var0) -> {
         Settings.smartMining = !Settings.smartMining;
         Settings.saveClientSettings();
      });
      this.rightQuickbar.addButton((FormPositionContainer)var7);
      FormContentIconVarToggleButton var8 = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> {
         return this.client.getPlayer().autoOpenDoors;
      }, Settings.UI.quickbar_door_on, Settings.UI.quickbar_door_off, new GameMessage[]{new LocalMessage("ui", "toggleautodoor")});
      var8.controllerFocusHashcode = "quickbarAutodoor";
      var8.onClicked((var1x) -> {
         this.client.getPlayer().autoOpenDoors = !this.client.getPlayer().autoOpenDoors;
         this.client.network.sendPacket(new PacketPlayerAutoOpenDoors(this.client.getSlot(), this.client.getPlayer().autoOpenDoors));
      });
      this.rightQuickbar.addButton((FormPositionContainer)var8);
      FormContentIconVarToggleButton var9 = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> {
         return this.client.getPlayer().hotbarLocked;
      }, Settings.UI.hotbar_locked, Settings.UI.hotbar_unlocked, new GameMessage[0]) {
         public GameTooltips getTooltips() {
            ListGameTooltips var1 = new ListGameTooltips();
            if (this.isToggled()) {
               var1.add((GameMessage)(new LocalMessage("ui", "unlockhotbar")));
            } else {
               var1.add((GameMessage)(new LocalMessage("ui", "lockhotbar")));
            }

            var1.add((Object)(new StringTooltips(Localization.translate("ui", "hotbarlockedtip"), GameColor.LIGHT_GRAY, 350)));
            return var1;
         }
      };
      var9.controllerFocusHashcode = "quickbarHotbarlock";
      var9.onClicked((var1x) -> {
         this.client.getPlayer().hotbarLocked = !this.client.getPlayer().hotbarLocked;
         this.client.network.sendPacket(new PacketPlayerHotbarLocked(this.client.getSlot(), this.client.getPlayer().hotbarLocked));
      });
      this.rightQuickbar.addButton((FormPositionContainer)var9);
      FormContentIconVarToggleButton var10 = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> {
         return this.modifiersForm != null;
      }, Settings.UI.quickbar_stats_icon, new GameMessage[0]) {
         protected void addTooltips(PlayerMob var1) {
            super.addTooltips(var1);
            if (var1 != null && MainGameFormManager.this.modifiersForm == null) {
               ListGameTooltips var2 = CurrentModifiersForm.getTooltips(var1);
               var2.add((Object)(new InputTooltip(-100, Localization.translate("ui", "anchortip"))));
               Screen.addTooltip(var2, GameBackground.itemTooltip, TooltipLocation.FORM_FOCUS);
            }

         }
      };
      var8.controllerFocusHashcode = "quickbarStats";
      var10.onClicked((var1x) -> {
         if (this.modifiersForm != null) {
            this.removeComponent(this.modifiersForm);
            this.modifiersForm = null;
            lastModifiersFormPos = null;
         } else {
            this.modifiersForm = (CurrentModifiersForm)this.addComponent(new CurrentModifiersForm() {
               public void onRemove() {
                  MainGameFormManager.this.removeComponent(this);
                  MainGameFormManager.this.modifiersForm = null;
                  MainGameFormManager.lastModifiersFormPos = null;
               }
            });
            this.modifiersForm.update(this.client.getPlayer());
            ControllerFocus var3 = this.getCurrentFocus();
            Point var2;
            if (var3 != null) {
               var2 = new Point(var3.boundingBox.x, var3.boundingBox.y);
            } else {
               InputPosition var4 = Screen.mousePos();
               var2 = new Point(var4.hudX, var4.hudY);
            }

            GameBackground var5 = GameBackground.itemTooltip;
            this.modifiersForm.setPosition(Math.min(var2.x + var5.getContentPadding() + 1, Screen.getHudWidth() - this.modifiersForm.getWidth() - var5.getContentPadding() - 4), var2.y - this.modifiersForm.getHeight() - 11 - 20);
         }

      });
      this.rightQuickbar.addButton((FormPositionContainer)var10);
      if (lastModifiersFormPos != null) {
         this.modifiersForm = (CurrentModifiersForm)this.addComponent(new CurrentModifiersForm() {
            public void onRemove() {
               MainGameFormManager.this.removeComponent(this);
               MainGameFormManager.this.modifiersForm = null;
               MainGameFormManager.lastModifiersFormPos = null;
            }
         });
         this.modifiersForm.setPosition(lastModifiersFormPos.x, lastModifiersFormPos.y);
         this.modifiersForm.update(var1);
      }

      this.setupInventoryForm(this.client.getContainer());
      this.crafting = (Form)this.addComponent(new Form("crafting", 156, this.inventory.getHeight() + (this.toolbar.getHeight() - this.rightQuickbar.getHeight())));
      FormContainerCraftingList var11 = (FormContainerCraftingList)this.crafting.addComponent(new FormContainerCraftingList(0, 0, this.crafting.getWidth() - 40, this.crafting.getHeight(), this.client, false, true, new Tech[]{RecipeTechRegistry.NONE}));
      var11.setFilter(GlobalData.masterFilter);
      this.craftFiltersButton = (FormContentIconToggleButton)this.crafting.addComponent(new FormContentIconToggleButton(this.crafting.getWidth() - 28 - 5, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_search_24, Settings.UI.button_search_24, new GameMessage[]{new LocalMessage("ui", "craftfilters")}));
      this.craftFiltersButton.useDownTexture = false;
      this.craftFiltersButton.onToggled((var1x) -> {
         this.filters.setHidden(!((FormButtonToggle)var1x.from).isToggled());
      });
      int var12 = 12;
      Form var10000 = this.crafting;
      FontOptions var10004 = new FontOptions(20);
      int var10006 = this.crafting.getWidth() - 30;
      var12 += 22;
      var10000.addComponent(new FormLabel("C", var10004, -1, var10006, var12));
      var10000 = this.crafting;
      var10004 = new FontOptions(20);
      var10006 = this.crafting.getWidth() - 30;
      var12 += 22;
      var10000.addComponent(new FormLabel("R", var10004, -1, var10006, var12));
      var10000 = this.crafting;
      var10004 = new FontOptions(20);
      var10006 = this.crafting.getWidth() - 30;
      var12 += 22;
      var10000.addComponent(new FormLabel("A", var10004, -1, var10006, var12));
      var10000 = this.crafting;
      var10004 = new FontOptions(20);
      var10006 = this.crafting.getWidth() - 30;
      var12 += 22;
      var10000.addComponent(new FormLabel("F", var10004, -1, var10006, var12));
      var10000 = this.crafting;
      var10004 = new FontOptions(20);
      var10006 = this.crafting.getWidth() - 30;
      var12 += 22;
      var10000.addComponent(new FormLabel("T", var10004, -1, var10006, var12));
      this.filters = (CraftFilterForm)this.addComponent(new CraftFilterForm("filtersForm", 140, this.crafting.getHeight(), this.crafting.getHeight(), GlobalData.masterFilter));
      this.filters.setPosition(new FormRelativePosition(this.crafting, this.crafting.getWidth() + Settings.UI.formSpacing, 0));
      this.filters.setHidden(true);
      this.travel = (Form)this.addComponent(new Form("travelbutton", 200, 40));
      this.travelButton = (FormLocalTextButton)this.travel.addComponent(new FormLocalTextButton("ui", "travelbutton", 4, 0, this.travel.getWidth() - 8));
      this.travelButton.onClicked((var1x) -> {
         if (this.lastTravelDir != null) {
            this.client.network.sendPacket(new PacketRequestTravel(this.lastTravelDir));
            this.travelButton.setLocalization("ui", "loadingdotdot");
         }

      });
      this.travelButton.setCooldown(5000);
      this.travelButton.controllerFocusHashcode = "travelButton";
      this.death = (Form)this.addComponent(new Form("death", 200, 80));
      this.death.addComponent(new FormLocalLabel("ui", "diedlabel", new FontOptions(20), 0, this.death.getWidth() / 2, 10));
      this.respawnButton = (FormLocalTextButton)this.death.addComponent(new FormLocalTextButton("ui", "respawn", 4, 40, this.death.getWidth() - 8));
      this.respawnButton.onClicked((var1x) -> {
         if (this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE) {
            this.mainGame.disconnect("Quit");
         } else {
            this.client.network.sendPacket(new PacketPlayerRespawnRequest());
         }

         this.respawnButtonCD = System.currentTimeMillis() + 5000L;
      });
      this.respawnButton.controllerFocusHashcode = "respawnButton";
      this.buffsContainer = (FormComponentList)this.addComponent(new FormComponentList());
      this.importantBuffs = (FormBuffHud)this.buffsContainer.addComponent(new FormBuffHud(0, 0, 7, FairType.TextAlign.CENTER, var1, (var0) -> {
         return var0.buff.isImportant(var0);
      }));
      this.unimportantBuffs = (FormBuffHud)this.buffsContainer.addComponent(new FormBuffHud(0, 0, 7, FairType.TextAlign.LEFT, var1, (var0) -> {
         return !var0.buff.isImportant(var0);
      }));
      this.minimap = (MapForm)this.addComponent(MapForm.getMiniMapForm("minimap", this.client, 200));
      this.minimap.setMapHidden(Settings.minimapHidden);
      this.fullMap = (MapForm)this.addComponent(MapForm.getFullMapForm("fullmap", this.client));
      this.fullMap.addButton((GameMessage)(new LocalMessage("ui", "travelworldmap")), 150).onClicked((var1x) -> {
         if (this.fullMap.canClickButtons()) {
            this.mainGame.setShowMap(false);
            this.client.network.sendPacket(new PacketRequestTravel(TravelDir.None));
         }

      });
      this.fullMap.setHidden(!this.mainGame.showMap());
      this.scoreboard = (ScoreboardForm)this.addComponent(new ScoreboardForm("scoreboard", this.client), Integer.MAX_VALUE);
      this.scoreboard.setHidden(!this.mainGame.showScoreboard());
      this.sidebarBox = (FormContentBox)this.addComponent(new FormContentBox(10, 30, 5, 5) {
         public boolean isMouseOver(InputEvent var1) {
            if (this.hasScrollbarX() && this.isMouseOverScrollbarX(var1)) {
               return true;
            } else if (this.hasScrollbarY() && this.isMouseOverScrollbarY(var1)) {
               return true;
            } else {
               InputEvent var2 = this.getComponentList().offsetEvent(var1, false);
               return this.getComponents().stream().anyMatch((var1x) -> {
                  return var1x.isMouseOver(var2);
               });
            }
         }
      });
      this.sidebarBox.drawHorizontalOnTop = true;
      this.sidebarBox.drawVerticalOnLeft = true;
      this.sidebarBox.hitboxFullSize = false;
      this.tutorialSidebar = new TutorialSidebarForm(this);
      this.addSidebar(new TrackedSidebarForm(this));
      this.onWindowResized();
      this.updateActive(true);
   }

   private void setupInventoryForm(Container var1) {
      PlayerMob var2 = this.client.getPlayer();
      if (this.inventory != null) {
         this.removeComponent(this.inventory);
      }

      this.inventory = (Form)this.addComponent(new Form("inventory", 408, 168) {
         public void setHidden(boolean var1) {
            super.setHidden(var1);
            MainGameFormManager.this.equipment.setHidden(var1);
            MainGameFormManager.this.leftQuickbar.setHidden(var1);
            MainGameFormManager.this.updateQuickbarHidden();
            MainGameFormManager.this.crafting.setHidden(var1);
            MainGameFormManager.this.filters.setHidden(var1 || !MainGameFormManager.this.craftFiltersButton.isToggled());
         }
      });

      int var3;
      int var4;
      int var5;
      for(var3 = var1.CLIENT_INVENTORY_START; var3 <= var1.CLIENT_INVENTORY_END; ++var3) {
         var4 = (var3 - var1.CLIENT_INVENTORY_START) / (this.inventory.getWidth() / 40);
         var5 = (var3 - var1.CLIENT_INVENTORY_START) % (this.inventory.getWidth() / 40);
         FormContainerSlot var6 = (FormContainerSlot)this.inventory.addComponent(new FormContainerSlot(this.client, var1, var3, 4 + var5 * 40, 4 + var4 * 40));
         var6.controllerFocusHashcode = "playerInventorySlot" + var3;
      }

      if (this.toolbar != null) {
         this.removeComponent(this.toolbar);
      }

      this.toolbar = (Form)this.addComponent(new Form("toolbar", 408, 48) {
         public void setHidden(boolean var1) {
            super.setHidden(var1);
            MainGameFormManager.this.updateQuickbarHidden();
            MainGameFormManager.this.sidebarBox.setHidden(var1);
         }
      });

      for(var3 = var1.CLIENT_HOTBAR_START; var3 <= var1.CLIENT_HOTBAR_END; ++var3) {
         var4 = var3 - var1.CLIENT_HOTBAR_START;
         FormContainerToolbarSlot var18 = (FormContainerToolbarSlot)this.toolbar.addComponent(new FormContainerToolbarSlot(this.client, var1, var3, 4 + var4 * 40, 4, var2));
         var18.controllerDownFocus = var18;
         var18.controllerFocusHashcode = "playerToolbarSlot" + var3;
         if (var3 == var1.CLIENT_HOTBAR_START) {
            this.tryPrioritizeControllerFocus(new ControllerFocusHandler[]{var18});
         }
      }

      TreeSet var17 = Zoning.getNewZoneSet();

      for(var4 = 0; var4 < this.client.getPlayer().getInv().trinkets.getSize() + 8; ++var4) {
         var5 = -var4 / 4;
         int var21 = var4 % 4;
         var17.add(new Point(var5, var21));
      }

      if (this.equipment != null) {
         this.removeComponent(this.equipment);
      }

      this.equipment = (Form)this.addComponent(new Form("equipment", var17, 40, 4));
      FormContainerPlayerArmorSlot var19 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_HELMET_SLOT, this.equipment.getWidth() - 48, 0, ArmorItem.ArmorType.HEAD, false));
      var19.controllerFocusHashcode = "playerHeadSlot";
      FormContainerPlayerArmorSlot var20 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_CHEST_SLOT, this.equipment.getWidth() - 48, 40, ArmorItem.ArmorType.CHEST, false));
      var20.controllerFocusHashcode = "playerChestSlot";
      FormContainerPlayerArmorSlot var22 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_FEET_SLOT, this.equipment.getWidth() - 48, 80, ArmorItem.ArmorType.FEET, false));
      var22.controllerFocusHashcode = "playerFeetSlot";
      FormContainerPlayerArmorSlot var7 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_COSM_HELMET_SLOT, this.equipment.getWidth() - 88, 0, ArmorItem.ArmorType.HEAD, true));
      var7.controllerFocusHashcode = "playerCosmeticHeadSlot";
      FormContainerPlayerArmorSlot var8 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_COSM_CHEST_SLOT, this.equipment.getWidth() - 88, 40, ArmorItem.ArmorType.CHEST, true));
      var8.controllerFocusHashcode = "playerCosmeticChestSlot";
      FormContainerPlayerArmorSlot var9 = (FormContainerPlayerArmorSlot)this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, var1, var1.CLIENT_COSM_FEET_SLOT, this.equipment.getWidth() - 88, 80, ArmorItem.ArmorType.FEET, true));
      var9.controllerFocusHashcode = "playerCosmeticFeetSlot";
      FormContainerMountSlot var10 = (FormContainerMountSlot)this.equipment.addComponent(new FormContainerMountSlot(this.client, var1, var1.CLIENT_MOUNT_SLOT, this.equipment.getWidth() - 48, 120));
      var10.controllerFocusHashcode = "playerMountSlot";
      FormContainerTrinketSlot var11 = (FormContainerTrinketSlot)this.equipment.addComponent(new FormContainerTrinketSlot(this.client, var1, var1.CLIENT_TRINKET_ABILITY_SLOT, this.equipment.getWidth() - 88, 120, true));
      var11.controllerFocusHashcode = "playerTrinketAbilitySlot";
      if (this.client.getPlayer().getInv().trinkets.getSize() > 0) {
         for(int var12 = var1.CLIENT_TRINKET_START; var12 <= var1.CLIENT_TRINKET_END; ++var12) {
            int var13 = var12 - var1.CLIENT_TRINKET_START;
            int var14 = var13 % 4 * 40;
            int var15 = this.equipment.getWidth() - 120 - var13 / 4 * 40 - 8;
            FormContainerTrinketSlot var16 = (FormContainerTrinketSlot)this.equipment.addComponent(new FormContainerTrinketSlot(this.client, var1, var12, var15, var14, false));
            var16.controllerFocusHashcode = "playerTrinketSlot" + var13;
         }
      }

      if (this.leftQuickbar != null) {
         this.removeComponent(this.leftQuickbar);
      }

      this.leftQuickbar = (Form)this.addComponent(new Form("leftQuickbar", 128, 48));
      this.leftQuickbar.addComponent(new FormContainerTrashSlot(this.client, var1, var1.CLIENT_TRASH_SLOT, this.leftQuickbar.getWidth() - 44, 4));
      FormContentIconButton var23 = (FormContentIconButton)this.leftQuickbar.addComponent(new FormContentIconButton(6, 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new GameMessage[]{new LocalMessage("ui", "restocktip")}));
      var23.onClicked((var1x) -> {
         this.client.network.sendPacket(new PacketContainerAction(-4, ContainerAction.LEFT_CLICK, 1));
      });
      var23.setCooldown(500);
      var23.controllerFocusHashcode = "quickbarRestock";
      FormContentIconButton var24 = (FormContentIconButton)this.leftQuickbar.addComponent(new FormContentIconButton(32, 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_out, new GameMessage[0]) {
         public GameTooltips getTooltips(PlayerMob var1) {
            StringTooltips var2 = new StringTooltips(Localization.translate("ui", "quickstacktip"));
            if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
               var2.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
            } else {
               var2.add(Localization.translate("ui", "quickstacktipinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
            }

            return var2;
         }
      });
      var24.onClicked((var1x) -> {
         this.client.network.sendPacket(new PacketContainerAction(-3, ContainerAction.LEFT_CLICK, 1));
      });
      var24.setCooldown(500);
      var24.controllerFocusHashcode = "quickbarStack";
      FormContentIconButton var25 = (FormContentIconButton)this.leftQuickbar.addComponent(new FormContentIconButton(58, 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_sort, new GameMessage[0]) {
         public GameTooltips getTooltips(PlayerMob var1) {
            StringTooltips var2 = new StringTooltips(Localization.translate("ui", "sorttip"));
            if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
               var2.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
            } else {
               var2.add(Localization.translate("ui", "sorttipinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
            }

            return var2;
         }
      });
      var25.onClicked((var1x) -> {
         this.client.network.sendPacket(new PacketContainerAction(-2, ContainerAction.LEFT_CLICK, 1));
      });
      var25.setCooldown(500);
      var25.controllerFocusHashcode = "quickbarSort";
      this.updateInventoryFormPositions();
   }

   protected void updateInventoryFormPositions() {
      this.toolbar.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - this.toolbar.getHeight() / 2 - Settings.UI.formSpacing - 20);
      this.inventory.setPosition(this.toolbar.getX(), this.toolbar.getY() - this.inventory.getHeight() - Settings.UI.formSpacing);
      this.equipment.setPosition(this.inventory.getX() - this.equipment.getWidth() - Settings.UI.formSpacing, this.inventory.getY());
      this.leftQuickbar.setPosition(this.inventory.getX() - this.leftQuickbar.getWidth() - Settings.UI.formSpacing, this.equipment.getY() + this.equipment.getHeight() + Settings.UI.formSpacing);
   }

   public void updateInventoryForm() {
      this.setupInventoryForm(this.client.getContainer());
      this.updateActive(true);
   }

   private void updateQuickbarHidden() {
      this.rightQuickbar.setHidden(Settings.alwaysShowQuickbar ? this.toolbar.isHidden() : this.inventory.isHidden());
   }

   public boolean isInventoryHidden() {
      return this.inventory.isHidden();
   }

   public void dispose() {
      this.sidebar.forEach((var1) -> {
         var1.onRemoved(this.client);
      });
      if (this.modifiersForm != null && !this.modifiersForm.isDisposed()) {
         lastModifiersFormPos = new Point(this.modifiersForm.getX(), this.modifiersForm.getY());
      }

      super.dispose();
      if (!this.tutorialSidebar.isDisposed()) {
         this.tutorialSidebar.dispose();
      }

   }

   static {
      totalArmorFontOptions = (new FontOptions(12)).color(Color.WHITE);
   }

   private static class TutorialSidebarForm extends SidebarForm {
      private MainGameFormManager mainFormManager;
      private FormFairTypeLabel objectiveText;
      private FormLocalTextButton button;
      private GameMessage objective;
      private GameMessage buttonText;
      public FormEventListener<FormInputEvent> buttonEvent;

      public TutorialSidebarForm(MainGameFormManager var1) {
         super("tutorialsidebar", 240, 10);
         this.mainFormManager = var1;
         this.objective = null;
         this.buttonText = null;
         this.addComponent(new FormLocalLabel("ui", "tutoriallabel", new FontOptions(20), -1, 10, 8));
         ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("tutorials", "skip", this.getWidth() - 80 - 4, 4, 80, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
            var1.client.tutorial.endTutorial();
         });
         FontOptions var2 = new FontOptions(16);
         this.objectiveText = (FormFairTypeLabel)this.addComponent((new FormFairTypeLabel("", 10, 40)).setFontOptions(var2).setMaxWidth(this.getWidth() - 20).setTextAlign(FairType.TextAlign.LEFT).setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(var2), TypeParsers.ItemIcon(16)));
         this.objectiveText.onUpdated((var1x) -> {
            this.updateHeight();
         });
         this.setContent(this.objective, this.buttonText);
      }

      public void setContent(GameMessage var1, GameMessage var2) {
         if (this.objective == null || !this.objective.equals(var1) || this.buttonText == null || !this.buttonText.equals(var2)) {
            this.objective = var1;
            this.buttonText = var2;
            this.objectiveText.setText(var1);
            if (this.button != null) {
               this.removeComponent(this.button);
            }

            if (var2 != null) {
               this.button = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var2, 4, this.getHeight() - 40, this.getWidth() - 8));
               this.button.onClicked((var1x) -> {
                  if (this.buttonEvent != null) {
                     this.buttonEvent.onEvent(var1x);
                  }

               });
            }

            this.updateHeight();
         }
      }

      public void updateHeight() {
         int var1 = 60;
         var1 += this.objectiveText.getBoundingBox().height;
         if (this.buttonText != null) {
            var1 += 40;
         }

         this.setHeight(var1);
         if (this.button != null) {
            this.button.setPosition(4, this.getHeight() - 40);
         }

         this.mainFormManager.fixSidebar();
      }

      public boolean isValid(Client var1) {
         return true;
      }
   }
}
