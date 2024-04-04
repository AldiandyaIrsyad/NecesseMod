package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.gameTool.GameTool;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.lists.FormSettlerHelpList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.SelectSettlementContinueForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.level.gameObject.furniture.BedObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlersForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public int maxHeight;
   public int contentHeight;
   protected Form settlers;
   protected Form settlersHelp;
   protected ConfirmationForm banishConfirm;
   protected FormLocalLabel settlersHeader;
   protected FormContentBox settlersContent;
   protected FormLocalTextButton lockNoSettlersButton;
   protected List<HudDrawElement> hudElements = new ArrayList();
   protected int mobMoveUniqueID = -1;

   public SettlementSettlersForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.maxHeight = 300;
      this.banishConfirm = (ConfirmationForm)this.addComponent(new ConfirmationForm("banish", 300, 200));
      this.settlers = (Form)this.addComponent(new Form("settlers", 500, 300));
      this.settlersHeader = (FormLocalLabel)this.settlers.addComponent(new FormLocalLabel("ui", "settlers", new FontOptions(20), 0, this.settlers.getWidth() / 2, 5));
      ((FormContentIconButton)this.settlers.addComponent(new FormContentIconButton(this.settlers.getWidth() - 25, 5, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[]{new LocalMessage("ui", "settlershelpbutton")}))).onClicked((var1x) -> {
         this.makeCurrent(this.settlersHelp);
      });
      this.settlersContent = (FormContentBox)this.settlers.addComponent(new FormContentBox(0, 30, this.settlers.getWidth(), this.settlers.getHeight() - 30 - 30));
      this.lockNoSettlersButton = (FormLocalTextButton)this.settlers.addComponent(new FormLocalTextButton("ui", "settlementlockbed", 4, this.settlers.getHeight() - 28, this.settlers.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.lockNoSettlersButton.onClicked((var3x) -> {
         startLockBedTool(var2, this, var1.getLevel());
      });
      this.settlersHelp = (Form)this.addComponent(new Form(400, 200));
      this.settlersHelp.addComponent(new FormLocalLabel("ui", "settlers", new FontOptions(20), -1, 10, 5));
      this.settlersHelp.addComponent(new FormSettlerHelpList(0, 40, this.settlersHelp.getWidth(), this.settlersHelp.getHeight() - 40));
      ((FormLocalTextButton)this.settlersHelp.addComponent(new FormLocalTextButton("ui", "backbutton", this.settlersHelp.getWidth() - 120, 0, 120))).onClicked((var1x) -> {
         this.makeCurrent(this.settlers);
      });
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementSettlerBasicsEvent.class, (var1) -> {
         if (this.containerForm.isCurrent(this)) {
            this.updateSettlers();
         }
      });
      this.container.onEvent(SettlementOpenSettlementListEvent.class, this::openSettlementList);
      this.container.onEvent(SettlementMoveErrorEvent.class, (var1) -> {
         NoticeForm var2 = new NoticeForm("moveError", 300, 400);
         var2.setupNotice(var1.error);
         this.addComponent(var2);
         var2.onContinue(() -> {
            this.removeComponent(var2);
            this.makeCurrent(this.settlers);
         });
         this.makeCurrent(var2);
      });
   }

   public void updateSettlers() {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      this.settlersContent.clearComponents();
      FormFlow var1 = new FormFlow();
      Comparator var2 = Comparator.comparing((var0) -> {
         return var0.settler.getID();
      });
      var2 = var2.thenComparing((var0) -> {
         return var0.mobUniqueID;
      });
      this.containerForm.settlers.sort(var2);
      if (!this.containerForm.settlers.isEmpty()) {
         this.addSettlers(var1, this.containerForm.settlers);
      }

      GameMessageBuilder var3 = (new GameMessageBuilder()).append("ui", "settlers").append(" (" + this.containerForm.settlers.size() + ")");
      this.settlersHeader.setLocalization(var3);
      this.addLockedRooms(this.containerForm.lockedBeds);
      if (this.containerForm.settlers.isEmpty()) {
         this.settlersContent.alwaysShowVerticalScrollBar = false;
         var1.next(16);
         this.settlersContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.settlers.getWidth() / 2, 0, this.settlers.getWidth() - 20), 16));
      } else {
         this.settlersContent.alwaysShowVerticalScrollBar = true;
      }

      this.contentHeight = Math.max(var1.next(), 70);
      this.updateSize();
      if (!this.containerForm.settlers.isEmpty()) {
         lastScroll.load(this.settlersContent);
      }

   }

   public void updateSize() {
      this.settlers.setHeight(Math.min(this.maxHeight, this.settlersContent.getY() + this.contentHeight + 30));
      this.settlersContent.setContentBox(new Rectangle(0, 0, this.settlersContent.getWidth(), this.contentHeight));
      this.settlersContent.setWidth(this.settlers.getWidth());
      this.settlersContent.setHeight(this.settlers.getHeight() - this.settlersContent.getY() - 30);
      this.lockNoSettlersButton.setPosition(4, this.settlers.getHeight() - 28);
      ContainerComponent.setPosInventory(this.settlers);
   }

   private static GameTool startLockBedTool(final SettlementContainer var0, Object var1, Level var2) {
      Screen.clearGameTools(var1);
      SelectTileGameTool var3 = new SelectTileGameTool(var2, new LocalMessage("ui", "settlementlockbed")) {
         public DrawOptions getIconTexture(Color var1, int var2, int var3) {
            return null;
         }

         public boolean onSelected(InputEvent var1, TilePosition var2) {
            if (var2 == null) {
               return true;
            } else {
               LevelObject var3 = (LevelObject)var2.object().getMultiTile().getMasterLevelObject(this.level, var2.tileX, var2.tileY).orElse((Object)null);
               if (var3 != null && var3.object instanceof BedObject) {
                  var0.lockNoSettlerRoom.runAndSend(var3.tileX, var3.tileY);
                  return true;
               } else {
                  return false;
               }
            }
         }

         public GameMessage isValidTile(TilePosition var1) {
            this.lastHoverBounds = null;
            LevelObject var2 = (LevelObject)var1.object().getMultiTile().getMasterLevelObject(this.level, var1.tileX, var1.tileY).orElse((Object)null);
            if (var2 != null && var2.object instanceof BedObject) {
               this.lastHoverBounds = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
               return null;
            } else {
               return new LocalMessage("ui", "settlmentnotbed");
            }
         }

         public Screen.CURSOR getCursor() {
            return Screen.CURSOR.LOCK;
         }
      };
      Screen.setGameTool(var3, var1);
      return var3;
   }

   public static GameTool startAssignSettlerBedTool(final SettlementContainer var0, final Mob var1, Object var2) {
      Screen.clearGameTools(var2);
      SelectTileGameTool var3 = new SelectTileGameTool(var1.getLevel(), new LocalMessage("misc", "movesettler", "settler", var1.getDisplayName())) {
         public DrawOptions getIconTexture(Color var1x, int var2, int var3) {
            return null;
         }

         public boolean onSelected(InputEvent var1x, TilePosition var2) {
            if (var2 == null) {
               return true;
            } else {
               LevelObject var3 = (LevelObject)var2.object().getMultiTile().getMasterLevelObject(this.level, var2.tileX, var2.tileY).orElse((Object)null);
               if (var3 != null && var3.object instanceof BedObject) {
                  var0.moveSettlerRoom.runAndSend(var3.tileX, var3.tileY, var1.getUniqueID());
                  return true;
               } else {
                  return false;
               }
            }
         }

         public GameMessage isValidTile(TilePosition var1x) {
            this.lastHoverBounds = null;
            LevelObject var2 = (LevelObject)var1x.object().getMultiTile().getMasterLevelObject(this.level, var1x.tileX, var1x.tileY).orElse((Object)null);
            if (var2 != null && var2.object instanceof BedObject) {
               this.lastHoverBounds = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
               return null;
            } else {
               return new LocalMessage("ui", "settlmentnotbed");
            }
         }

         public Screen.CURSOR getCursor() {
            return Screen.CURSOR.LOCK;
         }
      };
      Screen.setGameTool(var3, var2);
      return var3;
   }

   private void addSettlers(FormFlow var1, List<SettlementSettlerBasicData> var2) {
      int var3 = 0;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         final SettlementSettlerBasicData var5 = (SettlementSettlerBasicData)var4.next();
         final SettlerMob var6 = var5.getSettlerMob(this.client.getLevel());
         if (var6 != null) {
            byte var7 = 32;
            final FormMouseHover var8 = (FormMouseHover)this.settlersContent.addComponent(new FormMouseHover(0, var1.next(), this.settlersContent.getWidth(), var7), Integer.MAX_VALUE);
            HudDrawElement var9 = new HudDrawElement() {
               public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
                  if (var8.isHovering()) {
                     SettlerMob var4 = var5.getSettlerMob(SettlementSettlersForm.this.client.getLevel());
                     if (var4 != null) {
                        final DrawOptions var5x = SettlementSettlersForm.this.getSelectedMobDrawOptions(var4.getMob(), var2);
                        var1.add(new SortedDrawable() {
                           public int getPriority() {
                              return 2147482647;
                           }

                           public void draw(TickManager var1) {
                              var5x.draw();
                           }
                        });
                     }
                  }

               }
            };
            this.hudElements.add(var9);
            this.client.getLevel().hudManager.addElement(var9);
            this.settlersContent.addComponent(new FormSettlerIcon(4, var1.next(), var5.settler, var6.getMob(), this.containerForm));
            FontOptions var10 = new FontOptions(16);
            FormLabelEdit var11 = (FormLabelEdit)this.settlersContent.addComponent(new FormLabelEdit(var6.getSettlerName(), var10, Settings.UI.activeTextColor, 40, var1.next(), 100, 30), -1000);
            int var12 = this.settlersContent.getWidth() - 24 - this.settlersContent.getScrollBarWidth() - 2;
            FormContentIconButton var13 = (FormContentIconButton)this.settlersContent.addComponent(new FormContentIconButton(var12, var1.next() + 4, FormInputSize.SIZE_24, ButtonColor.YELLOW, Settings.UI.settler_banish, new GameMessage[]{new LocalMessage("ui", "settlerbanish")}));
            var13.onClicked((var2x) -> {
               SettlerMob var3 = var5.getSettlerMob(this.client.getLevel());
               String var4 = var3 == null ? var5.settler.getGenericMobName() : var3.getMob().getDisplayName();
               this.banishConfirm.setupConfirmation((GameMessage)(new LocalMessage("ui", "settlerbanishconfirm", "settler", var4)), () -> {
                  this.container.banishSettler.runAndSend(var5.mobUniqueID);
                  this.makeCurrent(this.settlers);
               }, () -> {
                  this.makeCurrent(this.settlers);
               });
               this.makeCurrent(this.banishConfirm);
            });
            var13.setActive(var5.canBanish);
            if (!var13.isActive()) {
               var13.setTooltips();
            }

            var12 -= 24;
            FormContentIconButton var14 = (FormContentIconButton)this.settlersContent.addComponent(new FormContentIconButton(var12, var1.next() + 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.settler_move_settlement, new GameMessage[]{new LocalMessage("ui", "settlermovesettlement")}));
            var14.onClicked((var2x) -> {
               this.mobMoveUniqueID = var5.mobUniqueID;
               NoticeForm var3 = new NoticeForm("loadingsettlements", 300, 400);
               var3.setButtonCooldown(-2);
               var3.setupNotice((GameMessage)(new LocalMessage("ui", "loadingdotdot")));
               this.addComponent(var3, (var1, var2) -> {
                  if (!var2) {
                     this.removeComponent(var1);
                  }

               });
               this.makeCurrent(var3);
               this.container.requestMoveSettlerList.runAndSend(this.mobMoveUniqueID);
            });
            var14.setActive(var5.canMoveOut);
            if (!var14.isActive()) {
               var14.setTooltips();
            }

            var12 -= 24;
            FormContentIconButton var15 = (FormContentIconButton)this.settlersContent.addComponent(new FormContentIconButton(var12, var1.next() + 4, FormInputSize.SIZE_24, var5.bedPosition == null ? ButtonColor.RED : ButtonColor.BASE, Settings.UI.settler_assign_bed, new GameMessage[0]) {
               public GameTooltips getTooltips(PlayerMob var1) {
                  ListGameTooltips var2 = new ListGameTooltips();
                  var2.add(Localization.translate("ui", "settlerassignbed"));
                  if (var5.bedPosition == null) {
                     var2.add(Localization.translate("ui", "settlerhasnobed"));
                  }

                  return var2;
               }
            });
            var15.onClicked((var2x) -> {
               startAssignSettlerBedTool(this.container, var6.getMob(), this);
            });
            var12 -= 24;
            FormContentButton var16 = (FormContentButton)this.settlersContent.addComponent(new FormContentButton(var12, var1.next() + 4, 24, FormInputSize.SIZE_24, ButtonColor.BASE) {
               protected void drawContent(int var1, int var2, int var3, int var4) {
                  float var5 = GameMath.limit((float)var6.getSettlerHappiness() / 100.0F, 0.0F, 1.0F);
                  Color var6x = GameUtils.getStatusColorLerp(var5, 0.15F, 0.85F);
                  Screen.initQuadDraw(var3, var4).color(var6x).draw(var1, var2);
               }

               public boolean isActive() {
                  return false;
               }

               public Color getDrawColor() {
                  return Color.WHITE;
               }

               protected void addTooltips(PlayerMob var1) {
                  super.addTooltips(var1);
                  StringTooltips var2 = new StringTooltips();
                  int var3 = GameMath.limit(var6.getSettlerHappiness(), 0, 100);
                  String var4 = Settler.getMood(var3).displayName.translate() + " (";
                  if (var3 >= 0) {
                     var4 = var4 + "+";
                  }

                  var4 = var4 + var3 + ")";
                  var2.add(var4);
                  var2.add(Localization.translate("settlement", "moodtalkto"), GameColor.LIGHT_GRAY);
                  Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
               }
            });
            var16.handleClicksIfNoEventHandlers = true;
            var12 -= 24;
            FormContentIconButton var17 = (FormContentIconButton)this.settlersContent.addComponent(new FormContentIconButton(var12, var1.next() + 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[0]));
            AtomicBoolean var18 = new AtomicBoolean(false);
            var11.onMouseChangedTyping((var5x) -> {
               var18.set(var11.isTyping());
               this.runRenameUpdate(var5.getSettlerMob(this.client.getLevel()), var11, var17);
            });
            var11.onSubmit((var5x) -> {
               var18.set(var11.isTyping());
               this.runRenameUpdate(var5.getSettlerMob(this.client.getLevel()), var11, var17);
            });
            var17.onClicked((var5x) -> {
               var18.set(!var11.isTyping());
               var11.setTyping(!var11.isTyping());
               this.runRenameUpdate(var5.getSettlerMob(this.client.getLevel()), var11, var17);
            });
            this.runRenameUpdate(var6, var11, var17);
            var11.setWidth(var12 - 40);
            FontOptions var19 = new FontOptions(12);
            int var20 = var12 - 40;
            final AtomicReference var21 = new AtomicReference();
            final AtomicBoolean var22 = new AtomicBoolean(var6.hasCommandOrders());
            FormFairTypeLabel var23 = (FormFairTypeLabel)this.settlersContent.addComponent(new FormFairTypeLabel(var5.settler.getGenericMobName(), 40, var1.next() + 16) {
               public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                  GameMessage var4 = var6.getCurrentActivity();
                  if (!GameMessage.isSame((GameMessage)var21.get(), var4) || var22.get() != var6.hasCommandOrders()) {
                     String var5 = var4.translate();
                     this.setColor(var6.hasCommandOrders() ? Settings.UI.errorTextColor : Settings.UI.activeTextColor);
                     this.setText(var5.isEmpty() ? " " : var5);
                     var21.set(var4);
                     var22.set(var6.hasCommandOrders());
                  }

                  super.draw(var1, var2, var3);
                  if (this.isHovering() && !this.displaysFullText()) {
                     Screen.addTooltip(new StringTooltips(var4.translate(), 300), TooltipLocation.FORM_FOCUS);
                  }

               }
            });
            var23.setMax(var20, 1, true);
            var23.setFontOptions(var19);
            var23.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(var23.getFontOptions().getSize()), TypeParsers.InputIcon(var23.getFontOptions()));
            var1.next(var7);
            if (var5.bedPosition != null) {
               HudDrawElement var24 = new HudDrawElement() {
                  public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
                     final SettlerMob var4 = var5.getSettlerMob(SettlementSettlersForm.this.client.getLevel());
                     if (var4 != null) {
                        final boolean var5x = var4.getMob() != null && !SettlementSettlersForm.this.getManager().isMouseOver() && var5.settler.isMouseOverSettlerFlag(var5.bedPosition.x, var5.bedPosition.y, var2);
                        final DrawOptions var6 = var5.settler.getSettlerFlagDrawOptionsTile(var5.bedPosition.x, var5.bedPosition.y, var2, var4.getMob());
                        final DrawOptions var7 = var5x ? SettlementSettlersForm.this.getSelectedMobDrawOptions(var4.getMob(), var2) : null;
                        var1.add(new SortedDrawable() {
                           public int getPriority() {
                              return Integer.MAX_VALUE;
                           }

                           public void draw(TickManager var1) {
                              if (var5x) {
                                 var7.draw();
                                 Screen.addTooltip(new StringTooltips(var4.getMob().getDisplayName()), TooltipLocation.FORM_FOCUS);
                              }

                              var6.draw();
                           }
                        });
                     }

                  }
               };
               this.hudElements.add(var24);
               this.client.getLevel().hudManager.addElement(var24);
            }
         } else {
            ++var3;
         }
      }

      if (var3 > 0) {
         this.settlersContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", new Object[]{"count", var3}), new FontOptions(16), -1, 10, 0), 5));
      }

   }

   private void addLockedRooms(List<SettlementLockedBedData> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         final SettlementLockedBedData var3 = (SettlementLockedBedData)var2.next();
         HudDrawElement var4 = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3x) {
               final DrawOptions var4 = SettlerRegistry.SETTLER_LOCKED.getSettlerFlagDrawOptionsTile(var3.tileX, var3.tileY, var2, (Mob)null);
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MAX_VALUE;
                  }

                  public void draw(TickManager var1) {
                     var4.draw();
                  }
               });
            }
         };
         this.hudElements.add(var4);
         this.client.getLevel().hudManager.addElement(var4);
      }

   }

   public void openSettlementList(SettlementOpenSettlementListEvent var1) {
      if (this.mobMoveUniqueID != var1.mobUniqueID) {
         GameLog.warn.println("Received wrong settlement list for mob id " + var1.mobUniqueID);
      } else {
         if (this.client.getLevel().isIslandPosition()) {
            var1.options.sort(Comparator.comparing((var1x) -> {
               return (new Point(var1x.islandX, var1x.islandY)).distance((double)this.client.getLevel().getIslandX(), (double)this.client.getLevel().getIslandY());
            }));
         }

         SelectSettlementContinueForm.Option[] var2 = (SelectSettlementContinueForm.Option[])var1.options.stream().map((var2x) -> {
            return new SelectSettlementContinueForm.Option(this.client.getLevel().isIslandPosition() && (var2x.islandX != this.client.getLevel().getIslandX() || var2x.islandY != this.client.getLevel().getIslandY()), var2x.islandX, var2x.islandY, var2x.name) {
               public void onSelected(SelectSettlementContinueForm var1x) {
                  SettlementSettlersForm.this.container.moveSettlerSettlement.runAndSend(var1.mobUniqueID, this.islandX, this.islandY);
                  SettlementSettlersForm.this.makeCurrent(SettlementSettlersForm.this.settlers);
               }
            };
         }).toArray((var0) -> {
            return new SelectSettlementContinueForm.Option[var0];
         });
         SelectSettlementContinueForm var3 = new SelectSettlementContinueForm("movetosettlement", 300, 400, new LocalMessage("ui", "settlementselect"), var2) {
            public void onCancel() {
               SettlementSettlersForm.this.makeCurrent(SettlementSettlersForm.this.settlers);
               this.removeComponent(this);
            }
         };
         this.addComponent(var3);
         this.makeCurrent(var3);
      }
   }

   protected DrawOptions getSelectedMobDrawOptions(Mob var1, GameCamera var2) {
      Object var3 = HUD.getDirectionIndicator(this.client.getPlayer().x, this.client.getPlayer().y, var1.x, var1.y, var1.getDisplayName(), (new FontOptions(16)).outline().color(200, 200, 200), var2);
      if (var3 == null) {
         var3 = HUD.levelBoundOptions(var2, var1.getSelectBox());
      }

      return (DrawOptions)var3;
   }

   private void runRenameUpdate(SettlerMob var1, FormLabelEdit var2, FormContentIconButton var3) {
      if (var2.isTyping()) {
         var3.setIcon(Settings.UI.container_rename_save);
         var3.setTooltips(new LocalMessage("ui", "settlersavename"));
      } else {
         if (var1 != null && !var2.getText().equals(var1.getSettlerName())) {
            if (var2.getText().isEmpty()) {
               var2.setText(var1.getSettlerName());
            } else {
               this.container.renameSettler.runAndSend(var1.getMob().getUniqueID(), var2.getText());
            }
         }

         var3.setIcon(Settings.UI.container_rename);
         var3.setTooltips(new LocalMessage("ui", "settlerchangename"));
      }

   }

   public void onSetCurrent(boolean var1) {
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      this.settlersContent.clearComponents();
      if (var1) {
         this.updateSettlers();
         this.makeCurrent(this.settlers);
      } else {
         Screen.clearGameTools(this);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateSize();
      ContainerComponent.setPosInventory(this.settlersHelp);
   }

   public void dispose() {
      Screen.clearGameTools(this);
      this.hudElements.forEach(HudDrawElement::remove);
      this.hudElements.clear();
      lastScroll.save(this.settlersContent);
      super.dispose();
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementsettlers");
   }

   public String getTypeString() {
      return "settlers";
   }
}
