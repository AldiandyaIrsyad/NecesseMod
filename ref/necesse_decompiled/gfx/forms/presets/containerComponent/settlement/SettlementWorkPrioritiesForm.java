package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import necesse.engine.ClipboardTracker;
import necesse.engine.GameTileRange;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobPriority;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconValueButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionDynamic;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementWorkPrioritiesForm<T extends SettlementContainer> extends Form implements SettlementSubForm {
   public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public int maxHeight;
   public int contentWidth;
   public int contentHeight;
   protected FormSwitcher setCurrentWhenLoaded;
   protected ArrayList<SettlementSettlerPrioritiesData> settlers;
   public FormContentBox content;
   public int prioritiesSubscription = -1;
   public ClipboardTracker<SettlementJobPrioritiesForm.PrioritiesData> listClipboard;
   public ArrayList<PasteButton> pasteButtons;
   private ArrayList<HudDrawElement> hudElements = new ArrayList();

   public SettlementWorkPrioritiesForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      super(800, 250);
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.maxHeight = 300;
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 40, this.getWidth(), this.getHeight() - 40));
      this.pasteButtons = new ArrayList();
      this.listClipboard = new ClipboardTracker<SettlementJobPrioritiesForm.PrioritiesData>() {
         public SettlementJobPrioritiesForm.PrioritiesData parse(String var1) {
            try {
               return new SettlementJobPrioritiesForm.PrioritiesData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(SettlementJobPrioritiesForm.PrioritiesData var1) {
            Iterator var2 = SettlementWorkPrioritiesForm.this.pasteButtons.iterator();

            while(var2.hasNext()) {
               PasteButton var3 = (PasteButton)var2.next();
               var3.updateActive(var1);
            }

         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((SettlementJobPrioritiesForm.PrioritiesData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementSettlersChangedEvent.class, (var1) -> {
         this.container.requestSettlerPriorities.runAndSend();
      });
      this.container.onEvent(SettlementSettlerPrioritiesEvent.class, (var1) -> {
         if (this.setCurrentWhenLoaded != null) {
            this.setCurrentWhenLoaded.makeCurrent(this);
         }

         this.setCurrentWhenLoaded = null;
         if (this.containerForm.isCurrent(this)) {
            this.settlers = var1.settlers;
            this.updateContent();
         }
      });
      this.container.onEvent(SettlementSettlerPrioritiesChangedEvent.class, (var1) -> {
         if (this.settlers != null) {
            Iterator var2 = this.settlers.iterator();

            while(var2.hasNext()) {
               SettlementSettlerPrioritiesData var3 = (SettlementSettlerPrioritiesData)var2.next();
               if (var3.mobUniqueID == var1.mobUniqueID) {
                  Iterator var4 = var1.priorities.entrySet().iterator();
                  if (var4.hasNext()) {
                     Map.Entry var5 = (Map.Entry)var4.next();
                     SettlementSettlerPrioritiesData.TypePriority var6 = (SettlementSettlerPrioritiesData.TypePriority)var3.priorities.get(var5.getKey());
                     if (var6 != null) {
                        var6.priority = ((SettlementSettlerPrioritiesData.TypePriority)var5.getValue()).priority;
                        var6.disabledByPlayer = ((SettlementSettlerPrioritiesData.TypePriority)var5.getValue()).disabledByPlayer;
                        this.listClipboard.forceUpdate();
                        this.listClipboard.onUpdate((SettlementJobPrioritiesForm.PrioritiesData)this.listClipboard.getValue());
                     } else {
                        this.container.requestSettlerPriorities.runAndSend();
                     }

                     return;
                  }
               }
            }

            this.container.requestSettlerPriorities.runAndSend();
         }
      });
   }

   public void updateContent() {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      this.clearComponents();
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 40, this.getWidth(), this.getHeight() - 40));
      this.pasteButtons.clear();
      FormFlow var1 = new FormFlow(0);
      ArrayList var2 = (ArrayList)JobTypeRegistry.streamTypes().filter((var0) -> {
         return var0.canChangePriority;
      }).sorted(Comparator.comparingInt(JobType::getID)).collect(Collectors.toCollection(ArrayList::new));
      this.contentWidth = 0;
      boolean var3 = false;
      short var4 = 150;
      boolean var5 = false;
      int var6 = 0;
      Comparator var7 = Comparator.comparing((var0) -> {
         return var0.settler.getID();
      });
      var7 = var7.thenComparing((var0) -> {
         return var0.mobUniqueID;
      });
      this.settlers.sort(var7);
      Iterator var8 = this.settlers.iterator();

      while(true) {
         SettlementSettlerPrioritiesData var9;
         final SettlerMob var10;
         label63:
         do {
            while(var8.hasNext()) {
               var9 = (SettlementSettlerPrioritiesData)var8.next();
               var10 = var9.getSettlerMob(this.client.getLevel());
               if (var10 != null) {
                  continue label63;
               }

               ++var6;
            }

            this.contentWidth += this.content.getScrollBarWidth() + 6;
            if (!var5) {
               this.content.alwaysShowVerticalScrollBar = false;
               this.contentWidth = 400;
               this.content.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlementnoworkingsettlers", new FontOptions(16), 0, this.contentWidth / 2, 0, this.contentWidth - 20), 32));
            } else {
               this.content.alwaysShowVerticalScrollBar = true;
            }

            if (var6 > 0) {
               this.content.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", new Object[]{"count", var6}), new FontOptions(16), -1, 10, 0), 5));
            }

            this.contentHeight = var1.next() + 8;
            this.updateSize();
            this.listClipboard.forceUpdate();
            this.listClipboard.onUpdate((SettlementJobPrioritiesForm.PrioritiesData)this.listClipboard.getValue());
            if (!this.settlers.isEmpty()) {
               lastScroll.load(this.content);
            }

            return;
         } while(!(var10 instanceof EntityJobWorker));

         final Mob var11 = (Mob)var10;
         final EntityJobWorker var12 = (EntityJobWorker)var10;
         var5 = true;
         FormFlow var13 = new FormFlow(5);
         int var14 = var1.next(32);
         String var15 = var10.getSettlerName();
         this.content.addComponent(new FormSettlerIcon(var13.next(35), var14, var9.settler, var11, this.containerForm));
         int var16 = var13.next(var4);
         FontOptions var17 = new FontOptions(16);
         this.content.addComponent(new FormLabel(GameUtils.maxString(var15, var17, var4), var17, -1, var16, var14, var4));
         FontOptions var18 = new FontOptions(12);
         final AtomicReference var19 = new AtomicReference();
         final AtomicBoolean var20 = new AtomicBoolean(var10.hasCommandOrders());
         FormFairTypeLabel var21 = (FormFairTypeLabel)this.content.addComponent(new FormFairTypeLabel(var9.settler.getGenericMobName(), var16, var14 + 16) {
            public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
               GameMessage var4 = var10.getCurrentActivity();
               if (!GameMessage.isSame((GameMessage)var19.get(), var4) || var20.get() != var10.hasCommandOrders()) {
                  String var5 = var4.translate();
                  this.setColor(var10.hasCommandOrders() ? Settings.UI.errorTextColor : Settings.UI.activeTextColor);
                  this.setText(var5.isEmpty() ? " " : var5);
                  var19.set(var4);
                  var20.set(var10.hasCommandOrders());
               }

               super.draw(var1, var2, var3);
               if (this.isHovering() && !this.displaysFullText()) {
                  Screen.addTooltip(new StringTooltips(var4.translate(), 300), TooltipLocation.FORM_FOCUS);
               }

            }
         });
         var21.setMax(var4, 1, true);
         var21.setFontOptions(var18);
         var21.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(var21.getFontOptions().getSize()), TypeParsers.InputIcon(var21.getFontOptions()));
         ((FormContentIconButton)this.content.addComponent(new FormContentIconButton(var13.next(24), var14, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
            HashMap var3 = new HashMap();
            Iterator var4 = var9.priorities.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry var5 = (Map.Entry)var4.next();
               SettlementSettlerPrioritiesData.TypePriority var6 = (SettlementSettlerPrioritiesData.TypePriority)var5.getValue();
               if (!var6.disabledBySettler) {
                  var3.put((JobType)var5.getKey(), var6);
               }
            }

            SettlementJobPrioritiesForm.PrioritiesData var8 = new SettlementJobPrioritiesForm.PrioritiesData(var3);
            SaveData var7 = new SaveData("jobs");
            var8.addSaveData(var7);
            Screen.putClipboard(var7.getScript());
            this.listClipboard.forceUpdate();
         });
         FormContentIconButton var22 = (FormContentIconButton)this.content.addComponent(new FormContentIconButton(var13.next(24), var14, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
         var22.onClicked((var3x) -> {
            SettlementJobPrioritiesForm.PrioritiesData var4 = (SettlementJobPrioritiesForm.PrioritiesData)this.listClipboard.getValue();
            if (var4 != null) {
               Iterator var5 = var4.priorities.entrySet().iterator();

               while(true) {
                  Map.Entry var6;
                  SettlementSettlerPrioritiesData.TypePriority var7;
                  SettlementSettlerPrioritiesData.TypePriority var8;
                  do {
                     do {
                        if (!var5.hasNext()) {
                           return;
                        }

                        var6 = (Map.Entry)var5.next();
                        var7 = (SettlementSettlerPrioritiesData.TypePriority)var9.priorities.get(var6.getKey());
                     } while(var7 == null);

                     var8 = (SettlementSettlerPrioritiesData.TypePriority)var6.getValue();
                  } while(var7.disabledByPlayer == var8.disabledByPlayer && var7.priority == var8.priority);

                  var7.disabledByPlayer = var8.disabledByPlayer;
                  var7.priority = var8.priority;
                  this.container.setSettlerPriority.runAndSend(var11.getUniqueID(), (JobType)var6.getKey(), var7.priority, var7.disabledByPlayer);
               }
            }
         });
         var22.setupDragPressOtherButtons("workPrioritiesPasteButton");
         this.pasteButtons.add(new PasteButton(var22, var9.priorities));
         int var23 = 0;
         Iterator var24 = var2.iterator();

         while(var24.hasNext()) {
            final JobType var25 = (JobType)var24.next();
            byte var26 = 50;
            int var27 = var13.next(var26);
            if (!var3) {
               String var28 = var25.displayName.translate();
               FontOptions var29 = new FontOptions(12);
               int var30 = 5 + var23 * 18;
               String var31 = GameUtils.maxString(var28, var29, var26 * 2 - 5);
               FormLabel var32 = (FormLabel)this.addComponent(new FormLabel(var31, var29, 0, var27 + var26 / 2, var30));
               Rectangle var33 = var32.getBoundingBox();
               FormMouseHover var34 = (FormMouseHover)this.addComponent(new FormMouseHover(var27 + var26 / 2 - var33.width / 2, var30, var33.width, 12) {
                  public GameTooltips getTooltips(PlayerMob var1) {
                     StringTooltips var2 = new StringTooltips();
                     var2.add(var25.displayName.translate());
                     if (var25.tooltip != null) {
                        var2.add(var25.tooltip.translate(), 400);
                     }

                     return var2;
                  }
               });
               FormBreakLine var35 = (FormBreakLine)this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, var27 + var26 / 2 - 1, var30 + 12, 45 - (var30 + 12) - 6, false));
               var32.setPosition(new FormPositionDynamic(var32.getX(), var32.getY(), () -> {
                  return -this.content.getScrollX();
               }, () -> {
                  return 0;
               }));
               var34.setPosition(new FormPositionDynamic(var34.getX(), var34.getY(), () -> {
                  return -this.content.getScrollX();
               }, () -> {
                  return 0;
               }));
               var35.setPosition(new FormPositionDynamic(var35.getX(), var35.getY(), () -> {
                  return -this.content.getScrollX();
               }, () -> {
                  return 0;
               }));
               var23 = (var23 + 1) % 2;
            }

            final SettlementSettlerPrioritiesData.TypePriority var36 = (SettlementSettlerPrioritiesData.TypePriority)var9.priorities.getOrDefault(var25, new SettlementSettlerPrioritiesData.TypePriority(true, 0, false));
            final FormContentIconValueButton var37 = (FormContentIconValueButton)this.content.addComponent(new FormContentIconValueButton<Integer>(var27 + (var26 - 24) / 2, var14, FormInputSize.SIZE_24, ButtonColor.BASE) {
               public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                  if (!var36.disabledBySettler) {
                     int var4 = var36.disabledByPlayer ? Integer.MIN_VALUE : var36.priority;
                     if ((Integer)this.getValue() != var4) {
                        this.setCurrent(var4, var36.disabledByPlayer ? Settings.UI.priority_disabled : (ButtonIcon)JobPriority.getJobPriority(var36.priority).icon.get());
                     }
                  }

                  super.draw(var1, var2, var3);
               }

               public GameTooltips getTooltips() {
                  StringTooltips var1 = new StringTooltips();
                  var1.add(var25.displayName.translate());
                  if (var25.tooltip != null) {
                     var1.add(var25.tooltip.translate(), 400);
                  }

                  if (var36.disabledBySettler) {
                     var1.add(Localization.translate("jobs", "settlerincapable"));
                  } else if (var36.disabledByPlayer) {
                     var1.add(Localization.translate("ui", "prioritydisabled"));
                  } else {
                     var1.add(JobPriority.getJobPriority(var36.priority).displayName.translate());
                  }

                  if (!var36.disabledBySettler) {
                     var1.add(Localization.translate("jobs", "jobrange", "range", (Object)((GameTileRange)var25.tileRange.apply(var11.getLevel())).maxRange));
                  }

                  return var1;
               }
            });
            var37.setActive(!var36.disabledBySettler);
            if (!var36.disabledBySettler) {
               var37.setCurrent(var36.disabledByPlayer ? Integer.MIN_VALUE : var36.priority, var36.disabledByPlayer ? Settings.UI.priority_disabled : (ButtonIcon)JobPriority.getJobPriority(var36.priority).icon.get());
            }

            var37.acceptRightClicks = true;
            var37.onClicked((var4x) -> {
               JobPriority var5;
               NavigableSet var6;
               if (var4x.event.getID() == -100 || var4x.event.isControllerEvent() && var4x.event.getControllerEvent().getState() == ControllerInput.MENU_SELECT) {
                  if (var36.disabledByPlayer) {
                     var36.disabledByPlayer = false;
                     var36.priority = ((JobPriority)JobPriority.priorities.last()).priority;
                  } else {
                     var5 = JobPriority.getJobPriority(var36.priority);
                     var6 = JobPriority.priorities.headSet(var5, false);
                     if (var6.isEmpty()) {
                        var36.disabledByPlayer = true;
                        var36.priority = ((JobPriority)JobPriority.priorities.first()).priority;
                     } else {
                        var36.priority = ((JobPriority)var6.last()).priority;
                     }
                  }

                  this.container.setSettlerPriority.runAndSend(var11.getUniqueID(), var25, var36.priority, var36.disabledByPlayer);
               } else if (var4x.event.getID() == -99 || var4x.event.isControllerEvent() && var4x.event.getControllerEvent().getState() == ControllerInput.MENU_BACK) {
                  if (var36.disabledByPlayer) {
                     var36.disabledByPlayer = false;
                     var36.priority = ((JobPriority)JobPriority.priorities.first()).priority;
                  } else {
                     var5 = JobPriority.getJobPriority(var36.priority);
                     var6 = JobPriority.priorities.tailSet(var5, false);
                     if (var6.isEmpty()) {
                        var36.disabledByPlayer = true;
                        var36.priority = ((JobPriority)JobPriority.priorities.last()).priority;
                     } else {
                        var36.priority = ((JobPriority)var6.first()).priority;
                     }
                  }

                  this.container.setSettlerPriority.runAndSend(var11.getUniqueID(), var25, var36.priority, var36.disabledByPlayer);
               }

            });
            var37.setupDragToOtherButtons("workPrioritiesToggleButton", false, (var4x) -> {
               if (var4x == Integer.MIN_VALUE) {
                  var36.disabledByPlayer = true;
                  var36.priority = ((JobPriority)JobPriority.priorities.first()).priority;
               } else {
                  var36.disabledByPlayer = false;
                  var36.priority = var4x;
               }

               this.container.setSettlerPriority.runAndSend(var11.getUniqueID(), var25, var36.priority, var36.disabledByPlayer);
               return true;
            });
            if (!var36.disabledBySettler) {
               HudDrawElement var38 = new HudDrawElement() {
                  public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
                     if (var37.isHovering()) {
                        Point var4 = var12.getJobSearchTile();
                        final SharedTextureDrawOptions var5 = ((GameTileRange)var25.tileRange.apply(var11.getLevel())).getDrawOptions(new Color(255, 255, 255, 100), new Color(255, 255, 255, 10), var4.x, var4.y, var2);
                        if (var5 != null) {
                           var1.add(new SortedDrawable() {
                              public int getPriority() {
                                 return -1000000;
                              }

                              public void draw(TickManager var1) {
                                 var5.draw();
                              }
                           });
                        }

                     }
                  }
               };
               this.client.getLevel().hudManager.addElement(var38);
               this.hudElements.add(var38);
            }
         }

         var3 = true;
         this.contentWidth = Math.max(var13.next(), this.contentWidth);
      }
   }

   public void updateSize() {
      int var1 = Screen.getHudWidth() - 200;
      short var2 = 200;
      this.setWidth(GameMath.limit(this.contentWidth, var2, Math.max(var1, var2)));
      this.setHeight(Math.min(this.maxHeight, this.content.getY() + this.contentHeight));
      this.content.setContentBox(new Rectangle(0, 0, Math.max(this.getWidth(), this.contentWidth), this.contentHeight));
      this.content.setWidth(this.getWidth());
      this.content.setHeight(this.getHeight() - this.content.getY());
      ContainerComponent.setPosInventory(this);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateSize();
   }

   public void onSetCurrent(boolean var1) {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      this.settlers = null;
      if (var1) {
         if (this.prioritiesSubscription == -1) {
            this.prioritiesSubscription = this.container.subscribePriorities.subscribe();
         }
      } else if (this.prioritiesSubscription != -1) {
         this.container.subscribePriorities.unsubscribe(this.prioritiesSubscription);
         this.prioritiesSubscription = -1;
      }

   }

   public void onMenuButtonClicked(FormSwitcher var1) {
      this.setCurrentWhenLoaded = var1;
      this.container.requestSettlerPriorities.runAndSend();
      if (this.prioritiesSubscription == -1) {
         this.prioritiesSubscription = this.container.subscribePriorities.subscribe();
      }

   }

   public void dispose() {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      lastScroll.save(this.content);
      super.dispose();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.listClipboard.update();
      super.draw(var1, var2, var3);
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementworkpriorities");
   }

   public String getTypeString() {
      return "workpriorities";
   }

   public static class PasteButton {
      public final FormContentIconButton button;
      public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> data;

      public PasteButton(FormContentIconButton var1, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var2) {
         this.button = var1;
         this.data = var2;
      }

      public void updateActive(SettlementJobPrioritiesForm.PrioritiesData var1) {
         if (var1 != null && var1.priorities != null) {
            Iterator var2 = this.data.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               SettlementSettlerPrioritiesData.TypePriority var4 = (SettlementSettlerPrioritiesData.TypePriority)var3.getValue();
               SettlementSettlerPrioritiesData.TypePriority var5 = (SettlementSettlerPrioritiesData.TypePriority)var1.priorities.get(var3.getKey());
               if (!var4.disabledBySettler && var5 != null && !var5.disabledBySettler && (var5.disabledByPlayer != var4.disabledByPlayer || var5.priority != var4.priority)) {
                  this.button.setActive(true);
                  return;
               }
            }
         }

         this.button.setActive(false);
      }
   }
}
