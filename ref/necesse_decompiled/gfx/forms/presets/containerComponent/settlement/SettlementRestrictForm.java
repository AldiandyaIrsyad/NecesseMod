package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorHueSelectorFloatMenu;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementRestrictZoneData;
import necesse.inventory.container.settlement.data.SettlementSettlerRestrictZoneData;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementRestrictForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public static SavedFormContentBoxScroll lastSettlersScroll = new SavedFormContentBoxScroll();
   public static SavedFormContentBoxScroll lastZonesScroll = new SavedFormContentBoxScroll();
   public static boolean manageLastOpen;
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public int maxHeight;
   public int settlersContentHeight;
   public int zonesContentHeight;
   protected FormSwitcher setCurrentWhenLoaded;
   protected ArrayList<SettlementSettlerRestrictZoneData> settlers;
   protected HashMap<Integer, RestrictZone> zones;
   protected int newSettlerRestrictZoneUniqueID;
   protected Zoning defendZone;
   public int restrictSubscription = -1;
   protected Form settlersForm;
   protected Form zonesForm;
   protected FormContentBox settlersContent;
   protected FormContentBox zonesContent;
   protected ConfirmationForm deleteConfirm;
   protected FormDropdownButton allSettlersSelectButton;
   protected FormDropdownSelectionButton<Integer> newSettlerSelectButton;
   protected FormLocalTextButton manageZonesButton;
   protected FormLocalTextButton createNewZoneButton;
   protected FormLocalTextButton zonesBackButton;
   protected ArrayList<SelectButton> settlerSelectButtons = new ArrayList();
   protected List<HudDrawElement> hudElements = new ArrayList();
   protected int currentEditZoneUniqueID;
   protected int currentColorEditZoneUniqueID;

   public SettlementRestrictForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.maxHeight = 300;
      this.deleteConfirm = (ConfirmationForm)this.addComponent(new ConfirmationForm("delete", 300, 200));
      this.settlersForm = (Form)this.addComponent(new Form("settlers", 500, 300));
      this.settlersForm.addComponent(new FormLocalLabel("ui", "settlementrestrict", new FontOptions(20), 0, this.settlersForm.getWidth() / 2, 5));
      this.settlersForm.addComponent(new FormContentIconButton(this.settlersForm.getWidth() - 25, 5, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[0]) {
         public GameTooltips getTooltips(PlayerMob var1) {
            return new StringTooltips(Localization.translate("ui", "settlementrestricthelp"), 400);
         }
      });
      short var4 = 250;
      int var5 = this.settlersForm.getWidth() - var4 - Settings.UI.scrollbar.active.getHeight() - 2;
      int var6 = 30;
      this.allSettlersSelectButton = (FormDropdownButton)this.settlersForm.addComponent(new FormDropdownButton(var5, var6 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, var4));
      this.settlersForm.addComponent(new FormLocalLabel("ui", "settlementallsettlers", new FontOptions(20), -1, 5, var6 + 5));
      var6 += 30;
      this.newSettlerSelectButton = (FormDropdownSelectionButton)this.settlersForm.addComponent(new FormDropdownSelectionButton(var5, var6 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, var4));
      this.newSettlerSelectButton.onSelected((var1x) -> {
         var2.setNewSettlerRestrictZone.runAndSend((Integer)var1x.value);
      });
      this.newSettlerSelectButton.setupDragToOtherButtons("restrictSelectionButton", (var0) -> {
         return true;
      });
      this.settlersForm.addComponent(new FormLocalLabel("ui", "settlementnewsettlers", new FontOptions(20), -1, 5, var6 + 5));
      var6 += 30;
      this.settlersForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, var6, this.settlersForm.getWidth(), true));
      var6 += 2;
      this.settlersContent = (FormContentBox)this.settlersForm.addComponent(new FormContentBox(0, var6, this.settlersForm.getWidth(), this.settlersForm.getHeight() - var6 - 30));
      this.manageZonesButton = (FormLocalTextButton)this.settlersForm.addComponent(new FormLocalTextButton("ui", "settlementmanageareas", 4, this.settlersForm.getHeight() - 28, this.settlersForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.manageZonesButton.onClicked((var1x) -> {
         this.makeCurrent(this.zonesForm);
         manageLastOpen = true;
      });
      this.zonesForm = (Form)this.addComponent(new Form("zones", 500, 300));
      this.zonesContent = (FormContentBox)this.zonesForm.addComponent(new FormContentBox(0, 0, this.zonesForm.getWidth(), this.zonesForm.getHeight() - 30));
      this.zonesContent.addComponent(new FormLocalLabel("ui", "settlementrestrict", new FontOptions(20), 0, this.zonesForm.getWidth() / 2, 5));
      short var7 = 150;
      this.createNewZoneButton = (FormLocalTextButton)this.zonesForm.addComponent(new FormLocalTextButton("ui", "settlementareanew", 4, this.zonesForm.getHeight() - 28, this.zonesForm.getWidth() - var7 - 6, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.createNewZoneButton.setCooldown(1000);
      this.createNewZoneButton.onClicked((var1x) -> {
         var2.createNewRestrictZone.runAndSend();
      });
      this.zonesBackButton = (FormLocalTextButton)this.zonesForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.zonesForm.getWidth() - var7 + 2, this.zonesForm.getHeight() - 28, var7 - 6, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.zonesBackButton.onClicked((var1x) -> {
         this.makeCurrent(this.settlersForm);
         manageLastOpen = false;
         Screen.clearGameTools(this);
      });
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementSettlersChangedEvent.class, (var1) -> {
         this.container.requestFullRestricts.runAndSend();
      });
      this.container.onEvent(SettlementRestrictZonesFullEvent.class, (var1) -> {
         if (this.setCurrentWhenLoaded != null) {
            this.setCurrentWhenLoaded.makeCurrent(this);
         }

         this.setCurrentWhenLoaded = null;
         if (this.containerForm.isCurrent(this)) {
            this.settlers = var1.settlers;
            this.zones = new HashMap();
            Iterator var2 = var1.zones.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               this.zones.put((Integer)var3.getKey(), new RestrictZone((SettlementRestrictZoneData)var3.getValue(), this.client.getLevel()));
            }

            this.newSettlerRestrictZoneUniqueID = var1.newSettlerRestrictZoneUniqueID;
            this.hudElements.forEach(HudDrawElement::remove);
            this.hudElements.clear();
            this.updateSettlersContent();
            this.updateZonesContent();
            this.updateSize();
            this.checkValidEditTool();
            if (!var1.settlers.isEmpty()) {
               lastSettlersScroll.load(this.settlersContent);
            }

            if (!var1.zones.isEmpty()) {
               lastZonesScroll.load(this.zonesContent);
            }

         }
      });
      this.container.onEvent(SettlementNewSettlerRestrictZoneChangedEvent.class, (var1) -> {
         if (this.settlers != null) {
            this.newSettlerRestrictZoneUniqueID = var1.restrictZoneUniqueID;
            this.updateSelectButton(this.newSettlerSelectButton, this.newSettlerRestrictZoneUniqueID);
         }
      });
      this.container.onEvent(SettlementSettlerRestrictZoneChangedEvent.class, (var1) -> {
         if (this.settlers != null) {
            Iterator var2 = this.settlers.iterator();

            SettlementSettlerRestrictZoneData var3;
            do {
               if (!var2.hasNext()) {
                  this.container.requestFullRestricts.runAndSend();
                  return;
               }

               var3 = (SettlementSettlerRestrictZoneData)var2.next();
            } while(var3.mobUniqueID != var1.mobUniqueID);

            var3.restrictZoneUniqueID = var1.restrictZoneUniqueID;
            this.updateSelectButtons();
         }
      });
      this.container.onEvent(SettlementRestrictZoneChangedEvent.class, (var1) -> {
         if (this.zones != null) {
            RestrictZone var2 = (RestrictZone)this.zones.get(var1.restrictZoneUniqueID);
            if (var2 != null) {
               synchronized(var2.zoning) {
                  var1.change.applyTo(var2.zoning);
               }
            } else {
               this.container.requestFullRestricts.runAndSend();
            }

         }
      });
      this.container.onEvent(SettlementRestrictZoneRenameEvent.class, (var1) -> {
         if (this.zones != null) {
            RestrictZone var2 = (RestrictZone)this.zones.get(var1.restrictZoneUniqueID);
            if (var2 != null) {
               var2.name = var1.name;
               if (var2.labelEdit != null && !var2.labelEdit.isTyping()) {
                  var2.labelEdit.setText(var2.name.translate());
               }

               this.updateSelectButtons();
            } else {
               this.container.requestFullRestricts.runAndSend();
            }

         }
      });
      this.container.onEvent(SettlementRestrictZoneRecolorEvent.class, (var1) -> {
         if (this.zones != null) {
            RestrictZone var2 = (RestrictZone)this.zones.get(var1.restrictZoneUniqueID);
            if (var2 != null) {
               var2.colorHue = var1.hue;
            } else {
               this.container.requestFullRestricts.runAndSend();
            }

         }
      });
      this.container.onEvent(SettlementDefendZoneChangedEvent.class, (var1) -> {
         if (this.defendZone == null) {
            this.defendZone = new Zoning(new Rectangle(this.client.getLevel().width, this.client.getLevel().height));
         }

         synchronized(this) {
            var1.change.applyTo(this.defendZone);
         }
      });
   }

   public void updateSettlersContent() {
      this.settlerSelectButtons.clear();
      this.settlersContent.clearComponents();
      FormFlow var1 = new FormFlow(0);
      boolean var2 = false;
      int var3 = 0;
      Comparator var4 = Comparator.comparing((var0) -> {
         return var0.settler.getID();
      });
      var4 = var4.thenComparing((var0) -> {
         return var0.mobUniqueID;
      });
      this.settlers.sort(var4);
      Iterator var5 = this.settlers.iterator();

      while(var5.hasNext()) {
         final SettlementSettlerRestrictZoneData var6 = (SettlementSettlerRestrictZoneData)var5.next();
         SettlerMob var7 = var6.getSettlerMob(this.client.getLevel());
         if (var7 != null) {
            Mob var8 = var7.getMob();
            if (var8 != null) {
               var2 = true;
               byte var9 = 32;
               final FormMouseHover var10 = (FormMouseHover)this.settlersContent.addComponent(new FormMouseHover(0, var1.next(), this.settlersContent.getWidth(), var9), Integer.MAX_VALUE);
               short var11 = 250;
               int var12 = this.settlersForm.getWidth() - var11 - this.settlersContent.getScrollBarWidth() - 2;
               final FormDropdownSelectionButton var13 = (FormDropdownSelectionButton)this.settlersContent.addComponent(new FormDropdownSelectionButton(var12, var1.next() + 3, FormInputSize.SIZE_24, ButtonColor.BASE, var11));
               this.settlerSelectButtons.add(new SelectButton(var6, var13));
               var13.onSelected((var3x) -> {
                  this.container.setSettlerRestrictZone.runAndSend(var8.getUniqueID(), (Integer)var3x.value);
                  var6.restrictZoneUniqueID = (Integer)var3x.value;
               });
               var13.controllerFocusHashcode = "settlerrestrictselection" + var8.getUniqueID();
               var13.setupDragToOtherButtons("restrictSelectionButton", (var0) -> {
                  return true;
               });
               HudDrawElement var14 = new HudDrawElement() {
                  public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
                     if (var10.isHovering() || SettlementRestrictForm.this.isControllerFocus(var13)) {
                        RestrictZone var4 = (RestrictZone)SettlementRestrictForm.this.zones.get(var6.restrictZoneUniqueID);
                        if (var4 != null) {
                           Color var5 = Color.getHSBColor((float)var4.colorHue / 360.0F, 0.8F, 0.6F);
                           Color var6x = Color.getHSBColor((float)var4.colorHue / 360.0F, 0.8F, 0.8F);
                           synchronized(var4.zoning) {
                              final SharedTextureDrawOptions var8 = var4.zoning.getDrawOptions(var5, new Color(var6x.getRed(), var6x.getGreen(), var6x.getBlue(), 75), var2);
                              if (var8 != null) {
                                 var1.add(new SortedDrawable() {
                                    public int getPriority() {
                                       return 2147482647;
                                    }

                                    public void draw(TickManager var1) {
                                       var8.draw();
                                    }
                                 });
                              }
                           }
                        } else if (SettlementRestrictForm.this.defendZone != null && var6.restrictZoneUniqueID == 1) {
                           synchronized(this) {
                              final SharedTextureDrawOptions var13x = SettlementRestrictForm.this.defendZone.getDrawOptions(new Color(50, 50, 125), new Color(50, 50, 255, 75), var2);
                              if (var13x != null) {
                                 var1.add(new SortedDrawable() {
                                    public int getPriority() {
                                       return -100000;
                                    }

                                    public void draw(TickManager var1) {
                                       var13x.draw();
                                    }
                                 });
                              }
                           }
                        }
                     }

                  }
               };
               this.hudElements.add(var14);
               this.client.getLevel().hudManager.addElement(var14);
               String var15 = var7.getSettlerName();
               this.settlersContent.addComponent(new FormSettlerIcon(5, var1.next(), var6.settler, var8, this.containerForm));
               byte var16 = 37;
               int var17 = var12 - var16;
               FontOptions var18 = new FontOptions(16);
               this.settlersContent.addComponent(new FormLabel(GameUtils.maxString(var15, var18, var17), var18, -1, var16, var1.next(), var17));
               FontOptions var19 = new FontOptions(12);
               this.settlersContent.addComponent(new FormLabel(GameUtils.maxString(var6.settler.getGenericMobName(), var19, var17), var19, -1, var16, var1.next() + 16));
               var1.next(var9);
            }
         } else {
            ++var3;
         }
      }

      if (!var2) {
         this.settlersContent.alwaysShowVerticalScrollBar = false;
         var1.next(16);
         this.settlersContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.settlersForm.getWidth() / 2, 0, this.settlersForm.getWidth() - 20), 16));
      } else {
         this.settlersContent.alwaysShowVerticalScrollBar = true;
      }

      if (var3 > 0) {
         this.settlersContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", new Object[]{"count", var3}), new FontOptions(16), -1, 10, 0), 5));
      }

      this.settlersContentHeight = Math.max(var1.next(), 100);
      this.updateSelectButtons();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public void updateZonesContent() {
      this.zonesContent.clearComponents();
      FormFlow var1 = new FormFlow(5);
      this.zonesContent.addComponent(new FormLocalLabel("ui", "settlementmanageareas", new FontOptions(20), 0, this.zonesForm.getWidth() / 2, var1.next(25)));
      ArrayList var2 = new ArrayList(this.zones.values());
      var2.sort(Comparator.comparingInt((var0) -> {
         return var0.index;
      }));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         final RestrictZone var4 = (RestrictZone)var3.next();
         byte var5 = 24;
         final FormMouseHover var6 = (FormMouseHover)this.zonesContent.addComponent(new FormMouseHover(0, var1.next(), this.zonesContent.getWidth(), var5), Integer.MAX_VALUE);
         FontOptions var7 = new FontOptions(16);
         var4.labelEdit = (FormLabelEdit)this.zonesContent.addComponent(new FormLabelEdit(var4.name.translate(), var7, Settings.UI.activeTextColor, 5, var1.next() + 4, 100, 30), -1000);
         int var8 = this.zonesContent.getWidth() - 24 - this.zonesContent.getScrollBarWidth() - 2;
         final FormContentIconButton var9 = (FormContentIconButton)this.zonesContent.addComponent(new FormContentIconButton(var8, var1.next(), FormInputSize.SIZE_24, ButtonColor.RED, Settings.UI.container_storage_remove, new GameMessage[]{new LocalMessage("ui", "deletebutton")}));
         var9.onClicked((var2x) -> {
            this.deleteConfirm.setupConfirmation((GameMessage)(new LocalMessage("ui", "settlementareadeleteconfirm", "zone", var4.name.translate())), () -> {
               this.container.deleteRestrictZone.runAndSend(var4.uniqueID);
               this.makeCurrent(this.zonesForm);
            }, () -> {
               this.makeCurrent(this.zonesForm);
            });
            this.makeCurrent(this.deleteConfirm);
         });
         var9.controllerFocusHashcode = "zonedelete" + var4.uniqueID;
         var8 -= 24;
         final FormContentIconButton var10 = (FormContentIconButton)this.zonesContent.addComponent(new FormContentIconButton(var8, var1.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "clonebutton")}));
         var10.onClicked((var2x) -> {
            this.container.cloneRestrictZone.runAndSend(var4.uniqueID);
         });
         var10.setActive(this.zones.size() < SettlementLevelData.MAX_RESTRICT_ZONES);
         var10.controllerFocusHashcode = "zoneclone" + var4.uniqueID;
         var8 -= 24;
         final FormContentIconButton var11 = (FormContentIconButton)this.zonesContent.addComponent(new FormContentIconButton(var8, var1.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_sort, new GameMessage[]{new LocalMessage("ui", "settlementareainvert")}));
         var11.onClicked((var2x) -> {
            synchronized(var4.zoning) {
               var4.zoning.invert();
            }

            this.container.changeRestrictZone.runAndSend(var4.uniqueID, ZoningChange.fullInvert());
         });
         var11.controllerFocusHashcode = "zoneinvert" + var4.uniqueID;
         var8 -= 24;
         final FormContentIconButton var12 = (FormContentIconButton)this.zonesContent.addComponent(new FormContentIconButton(var8, var1.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_config, new GameMessage[]{new LocalMessage("ui", "configurebutton")}));
         var12.onClicked((var2x) -> {
            this.startEditZoneTool(var4);
         });
         var8 -= 24;
         final FormContentButton var13 = (FormContentButton)this.zonesContent.addComponent(new FormContentButton(var8, var1.next(), 24, FormInputSize.SIZE_24, ButtonColor.BASE) {
            protected void drawContent(int var1, int var2, int var3, int var4x) {
               Color var5 = this.getDrawColor();
               float[] var6 = Color.RGBtoHSB(var5.getRed(), var5.getGreen(), var5.getBlue(), (float[])null);
               Color var7 = Color.getHSBColor((float)var4.colorHue / 360.0F, 0.8F, var6[2]);
               Screen.initQuadDraw(var3, var4x).color(var7).draw(var1, var2);
            }

            protected void addTooltips(PlayerMob var1) {
               super.addTooltips(var1);
               Screen.addTooltip(new StringTooltips(Localization.translate("ui", "changecolorbutton")), TooltipLocation.FORM_FOCUS);
            }
         });
         var13.onClicked((var3x) -> {
            this.currentColorEditZoneUniqueID = var4.uniqueID;
            final int var4x = var4.colorHue;
            ((FormButton)var3x.from).getManager().openFloatMenu(new ColorHueSelectorFloatMenu(var3x.from, 150, 24, (float)var4.colorHue / 360.0F) {
               public void onChanged(float var1x) {
                  var1.colorHue = (int)(var1x * 360.0F);
               }

               public void dispose() {
                  super.dispose();
                  int var1x = (int)(this.picker.getSelectedHue() * 360.0F);
                  if (var4 != var1x) {
                     SettlementRestrictForm.this.container.recolorRestrictZone.runAndSend(var1.uniqueID, var1x);
                     var1.colorHue = var1x;
                  }

                  SettlementRestrictForm.this.currentColorEditZoneUniqueID = 0;
               }
            }, var13, var3x.event, 0, 0);
         });
         var13.controllerFocusHashcode = "zonecolor" + var4.uniqueID;
         var8 -= 24;
         final FormContentIconButton var14 = (FormContentIconButton)this.zonesContent.addComponent(new FormContentIconButton(var8, var1.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{new LocalMessage("ui", "renamebutton")}));
         AtomicBoolean var15 = new AtomicBoolean(false);
         var4.labelEdit.onMouseChangedTyping((var4x) -> {
            var15.set(var4.labelEdit.isTyping());
            this.runRenameUpdate(var4, var4.labelEdit, var14);
         });
         var4.labelEdit.onSubmit((var4x) -> {
            var15.set(var4.labelEdit.isTyping());
            this.runRenameUpdate(var4, var4.labelEdit, var14);
         });
         var14.onClicked((var4x) -> {
            var15.set(!var4.labelEdit.isTyping());
            var4.labelEdit.setTyping(!var4.labelEdit.isTyping());
            this.runRenameUpdate(var4, var4.labelEdit, var14);
         });
         this.runRenameUpdate(var4, var4.labelEdit, var14);
         var14.controllerFocusHashcode = "zonerename" + var4.uniqueID;
         HudDrawElement var16 = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
               if (var6.isHovering() || SettlementRestrictForm.this.isControllerFocus(var9) || SettlementRestrictForm.this.isControllerFocus(var10) || SettlementRestrictForm.this.isControllerFocus(var11) || SettlementRestrictForm.this.isControllerFocus(var12) || SettlementRestrictForm.this.isControllerFocus(var13) || SettlementRestrictForm.this.isControllerFocus(var14) || SettlementRestrictForm.this.currentEditZoneUniqueID == var4.uniqueID || SettlementRestrictForm.this.currentColorEditZoneUniqueID == var4.uniqueID) {
                  Color var4x = Color.getHSBColor((float)var4.colorHue / 360.0F, 0.8F, 0.6F);
                  Color var5 = Color.getHSBColor((float)var4.colorHue / 360.0F, 0.8F, 0.8F);
                  synchronized(var4.zoning) {
                     final SharedTextureDrawOptions var7 = var4.zoning.getDrawOptions(var4x, new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), 75), var2);
                     if (var7 != null) {
                        var1.add(new SortedDrawable() {
                           public int getPriority() {
                              return 2147482647;
                           }

                           public void draw(TickManager var1) {
                              var7.draw();
                           }
                        });
                     }
                  }
               }

            }
         };
         this.hudElements.add(var16);
         this.client.getLevel().hudManager.addElement(var16);
         var4.labelEdit.setWidth(var8);
         var1.next(var5);
      }

      this.zonesContentHeight = Math.max(var1.next(), 100);
      this.zonesContent.alwaysShowVerticalScrollBar = true;
      this.createNewZoneButton.setActive(this.zones.size() < SettlementLevelData.MAX_RESTRICT_ZONES);
      ControllerInput.submitNextRefreshFocusEvent();
   }

   private void runRenameUpdate(RestrictZone var1, FormLabelEdit var2, FormContentIconButton var3) {
      if (var2.isTyping()) {
         var3.setIcon(Settings.UI.container_rename_save);
         var3.setTooltips(new LocalMessage("ui", "savebutton"));
      } else {
         if (!var2.getText().equals(var1.name.translate())) {
            if (var2.getText().isEmpty()) {
               var2.setText(var1.name.translate());
            } else {
               var1.name = new StaticMessage(var2.getText());
               this.updateSelectButtons();
               this.container.renameRestrictZone.runAndSend(var1.uniqueID, var2.getText());
            }
         }

         var3.setIcon(Settings.UI.container_rename);
         var3.setTooltips(new LocalMessage("ui", "renamebutton"));
      }

   }

   private void checkValidEditTool() {
      if (this.currentEditZoneUniqueID != 0 && this.zones.get(this.currentEditZoneUniqueID) == null) {
         Screen.clearGameTools(this);
      }

   }

   private void startEditZoneTool(final RestrictZone var1) {
      this.currentEditZoneUniqueID = var1.uniqueID;
      Screen.clearGameTools(this);
      Screen.setGameTool(new CreateOrExpandGlobalZoneGameTool(this.client.getLevel()) {
         public void onExpandedZone(Rectangle var1x) {
            synchronized(var1.zoning) {
               var1.zoning.addRectangle(var1x);
            }

            SettlementRestrictForm.this.container.changeRestrictZone.runAndSend(var1.uniqueID, ZoningChange.expand(var1x));
         }

         public void onShrankZone(Rectangle var1x) {
            synchronized(var1.zoning) {
               var1.zoning.removeRectangle(var1x);
            }

            SettlementRestrictForm.this.container.changeRestrictZone.runAndSend(var1.uniqueID, ZoningChange.shrink(var1x));
         }

         public void isCancelled() {
            super.isCancelled();
            if (SettlementRestrictForm.this.currentEditZoneUniqueID == var1.uniqueID) {
               SettlementRestrictForm.this.currentEditZoneUniqueID = 0;
            }

         }

         public void isCleared() {
            super.isCleared();
            if (SettlementRestrictForm.this.currentEditZoneUniqueID == var1.uniqueID) {
               SettlementRestrictForm.this.currentEditZoneUniqueID = 0;
            }

         }
      }, this);
   }

   private void updateSelectButtons() {
      this.updateSelectButton(this.allSettlersSelectButton, (var1x) -> {
         if (this.settlers != null) {
            SettlementSettlerRestrictZoneData var3;
            for(Iterator var2 = this.settlers.iterator(); var2.hasNext(); var3.restrictZoneUniqueID = var1x) {
               var3 = (SettlementSettlerRestrictZoneData)var2.next();
               this.container.setSettlerRestrictZone.runAndSend(var3.mobUniqueID, var1x);
            }

            this.updateSelectButtons();
         }
      });
      this.updateSelectButton(this.newSettlerSelectButton, this.newSettlerRestrictZoneUniqueID);
      Iterator var1 = this.settlerSelectButtons.iterator();

      while(var1.hasNext()) {
         SelectButton var2 = (SelectButton)var1.next();
         this.updateSelectButton(var2.button, var2.data.restrictZoneUniqueID);
      }

   }

   private void updateSelectButton(FormDropdownButton var1, Consumer<Integer> var2) {
      var1.options.clear();
      var1.options.add(new LocalMessage("ui", "settlementunrestricted"), () -> {
         var2.accept(0);
      });
      var1.options.add(new LocalMessage("ui", "settlementdefendzone"), () -> {
         var2.accept(1);
      });
      this.zones.values().stream().sorted(Comparator.comparingInt((var0) -> {
         return var0.index;
      })).forEach((var2x) -> {
         var1.options.add(var2x.name, () -> {
            var2.accept(var2x.uniqueID);
         });
      });
   }

   private void updateSelectButton(FormDropdownSelectionButton<Integer> var1, int var2) {
      var1.options.clear();
      var1.options.add(0, new LocalMessage("ui", "settlementunrestricted"));
      var1.options.add(1, new LocalMessage("ui", "settlementdefendzone"));
      this.zones.values().stream().sorted(Comparator.comparingInt((var0) -> {
         return var0.index;
      })).forEach((var1x) -> {
         var1.options.add(var1x.uniqueID, var1x.name);
      });
      if (var2 == 0) {
         var1.setSelected(0, new LocalMessage("ui", "settlementunrestricted"));
      } else if (var2 == 1) {
         var1.setSelected(1, new LocalMessage("ui", "settlementdefendzone"));
      } else {
         RestrictZone var3 = (RestrictZone)this.zones.get(var2);
         if (var3 != null) {
            var1.setSelected(var2, var3.name);
         } else {
            var1.setSelected(var2, new LocalMessage("ui", "settlementunknownarea"));
         }
      }

   }

   public void updateSize() {
      this.settlersForm.setHeight(Math.min(this.maxHeight, this.settlersContent.getY() + this.settlersContentHeight + 30));
      this.settlersContent.setContentBox(new Rectangle(0, 0, this.settlersContent.getWidth(), this.settlersContentHeight));
      this.settlersContent.setWidth(this.settlersForm.getWidth());
      this.settlersContent.setHeight(this.settlersForm.getHeight() - this.settlersContent.getY() - 30);
      this.manageZonesButton.setY(this.settlersForm.getHeight() - 27);
      ContainerComponent.setPosInventory(this.settlersForm);
      this.zonesForm.setHeight(Math.min(this.maxHeight, this.zonesContent.getY() + this.zonesContentHeight + 30));
      this.zonesContent.setContentBox(new Rectangle(0, 0, this.zonesContent.getWidth(), this.zonesContentHeight));
      this.zonesContent.setWidth(this.zonesForm.getWidth());
      this.zonesContent.setHeight(this.zonesForm.getHeight() - this.zonesContent.getY() - 30);
      this.createNewZoneButton.setY(this.zonesForm.getHeight() - 27);
      this.zonesBackButton.setY(this.zonesForm.getHeight() - 27);
      ContainerComponent.setPosInventory(this.zonesForm);
   }

   public void onSetCurrent(boolean var1) {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      this.settlersContent.clearComponents();
      this.zonesContent.clearComponents();
      this.settlers = null;
      this.zones = null;
      if (var1) {
         if (this.restrictSubscription == -1) {
            this.restrictSubscription = this.container.subscribeRestrict.subscribe();
         }

         this.makeCurrent(manageLastOpen ? this.zonesForm : this.settlersForm);
      } else {
         this.defendZone = null;
         if (this.restrictSubscription != -1) {
            this.container.subscribeRestrict.unsubscribe(this.restrictSubscription);
            this.restrictSubscription = -1;
         }
      }

   }

   public void onMenuButtonClicked(FormSwitcher var1) {
      this.setCurrentWhenLoaded = var1;
      this.container.requestFullRestricts.runAndSend();
      if (this.restrictSubscription == -1) {
         this.restrictSubscription = this.container.subscribeRestrict.subscribe();
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateSize();
   }

   public void dispose() {
      Screen.clearGameTools(this);
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      lastSettlersScroll.save(this.settlersContent);
      lastZonesScroll.save(this.zonesContent);
      super.dispose();
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementrestrict");
   }

   public String getTypeString() {
      return "restrict";
   }

   private static class SelectButton {
      public final SettlementSettlerRestrictZoneData data;
      public final FormDropdownSelectionButton<Integer> button;

      public SelectButton(SettlementSettlerRestrictZoneData var1, FormDropdownSelectionButton<Integer> var2) {
         this.data = var1;
         this.button = var2;
      }
   }

   private static class RestrictZone {
      public final int uniqueID;
      public int index;
      public int colorHue;
      public GameMessage name;
      public final Zoning zoning;
      public FormLabelEdit labelEdit;

      public RestrictZone(SettlementRestrictZoneData var1, Level var2) {
         this.uniqueID = var1.uniqueID;
         this.index = var1.index;
         this.colorHue = var1.colorHue;
         this.name = var1.name;
         this.zoning = var1.getZoning(var2);
      }
   }
}
