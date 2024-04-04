package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.control.Input;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.SelectSettlementContinueForm;
import necesse.gfx.forms.presets.containerComponent.PartyConfigForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.mob.ContainerExpedition;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.mob.ShopContainerPartyResponseEvent;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class ShopContainerForm<T extends ShopContainer> extends MobContainerFormSwitcher<T> {
   public int width;
   public int height;
   public GameMessage header;
   public DialogueForm workInvForm;
   public DialogueForm missionFailedForm;
   public DialogueForm dialogueForm;
   public DialogueForm moodForm;
   public DialogueForm recruitForm;
   public DialogueForm whatCanYouDoForm;
   public DialogueForm workForm;
   public DialogueForm[] expeditionsForms;
   public DialogueForm[] unavailableExpeditionsForms;
   public DialogueForm expeditionFocus;
   public Form buyForm;
   public Form sellForm;
   public EquipmentForm equipmentForm;
   public boolean waitingForPartyConfirm;
   public DialogueForm partyResponseForm;
   public PartyConfigForm partyConfigForm;
   public FormDialogueOption acceptRecruitButton;
   public int expeditionBuyCost;
   public FormDialogueOption expeditionBuyButton;

   protected ShopContainerForm(Client var1, T var2, int var3, int var4, int var5) {
      super(var1, var2);
      this.width = var3;
      this.height = var4;
      this.header = MobRegistry.getLocalization(var2.humanShop.getID());
      var2.onEvent(ShopContainerPartyUpdateEvent.class, (var1x) -> {
         this.updateDialogue();
         this.onWindowResized();
      });
      this.workInvForm = (DialogueForm)this.addComponent(new DialogueForm("workinv", var3, var4, this.header, (GameMessage)null));
      this.updateWorkInventoryForm();
      this.dialogueForm = (DialogueForm)this.addComponent(new DialogueForm("dialogue", var3, var4, this.header, var2.introMessage));
      this.moodForm = (DialogueForm)this.addComponent(new DialogueForm("mood", var3 + 40, var4, (GameMessage)null, (GameMessage)null));
      if (var2.happinessModifiers != null) {
         this.moodForm.reset(this.header, (var1x, var2x) -> {
            if (var2.humanShop.isOnStrike()) {
               LocalMessage var3 = new LocalMessage("settlement", "onstrike");
               if (var2.hungerStrike) {
                  var3 = new LocalMessage("settlement", "onstrikehungry");
               }

               DialogueForm.addText(var1x, var2x, var3);
            } else if (var2.humanShop.isOnWorkBreak()) {
               DialogueForm.addText(var1x, var2x, new LocalMessage("settlement", "onworkbreak"));
            }

            FontOptions var10 = new FontOptions(16);
            int var4 = Math.max(FontManager.bit.getWidthCeil("-", var10), FontManager.bit.getWidthCeil("+", var10));
            int var5 = FontManager.bit.getWidthCeil(Integer.toString(Math.abs(var2.settlerHappiness)), var10);

            Iterator var6;
            HappinessModifier var7;
            for(var6 = var2.happinessModifiers.iterator(); var6.hasNext(); var5 = Math.max(var5, FontManager.bit.getWidthCeil(Integer.toString(Math.abs(var7.happiness)), var10))) {
               var7 = (HappinessModifier)var6.next();
            }

            String var11 = var2.settlerHappiness == 0 ? "" : (var2.settlerHappiness > 0 ? "+" : "-");
            int var12 = 5;
            DialogueForm.addText(var1x, var2x.split(), new StaticMessage(var11), var12 + var4 / 2 - FontManager.bit.getWidthCeil(var11, var10) / 2, Integer.MAX_VALUE);
            var12 += var4;
            DialogueForm.addText(var1x, var2x.split(), new StaticMessage(Integer.toString(Math.abs(var2.settlerHappiness))), var12, Integer.MAX_VALUE);
            var12 += var5 + 10;
            DialogueForm.addText(var1x, var2x, Settler.getMood(var2.settlerHappiness).getDescription(), var12, var1x.getWidth() - var12 - 5);
            if (!var2.happinessModifiers.isEmpty()) {
               var2x.next(16);
               DialogueForm.addText(var1x, var2x, new LocalMessage("settlement", "thoughts"), var4 + var5 + 10, var1x.getWidth() - var4 - var5 - 20);
               var6 = var2.happinessModifiers.iterator();

               while(var6.hasNext()) {
                  var7 = (HappinessModifier)var6.next();
                  String var8 = var7.happiness == 0 ? "" : (var7.happiness > 0 ? "+" : "-");
                  int var9 = 5;
                  DialogueForm.addText(var1x, var2x.split(), new StaticMessage(var8), var9 + var4 / 2 - FontManager.bit.getWidthCeil(var8, var10) / 2, Integer.MAX_VALUE);
                  var9 += var4;
                  DialogueForm.addText(var1x, var2x.split(), new StaticMessage(Integer.toString(Math.abs(var7.happiness))), var9, Integer.MAX_VALUE);
                  var9 += var5 + 10;
                  DialogueForm.addText(var1x, var2x, var7.description, var9, var1x.getWidth() - var9 - 5);
               }
            }

         });
      }

      this.moodForm.addComponent(new FormContentIconButton(this.moodForm.getWidth() - 40, 4, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[]{new LocalMessage("ui", "settlershelpbutton")}) {
         public GameTooltips getTooltips(PlayerMob var1) {
            return (new StringTooltips()).add(Localization.translate("settlement", "moodhelp"), 400);
         }
      }, 100);
      this.moodForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.partyResponseForm = (DialogueForm)this.addComponent(new DialogueForm("partyresponse", var3, var4, this.header, (GameMessage)null));
      this.partyConfigForm = (PartyConfigForm)this.addComponent(new PartyConfigForm(var1, var2, var2.PARTY_SLOTS_START, var2.PARTY_SLOTS_END, var3, (Runnable)null, () -> {
         var2.setIsInPartyConfig.runAndSend(false);
         this.makeCurrent(this.dialogueForm);
      }, () -> {
         ContainerComponent.setPosFocus(this.partyConfigForm);
      }));
      var2.onEvent(ShopContainerPartyResponseEvent.class, (var2x) -> {
         if (this.waitingForPartyConfirm) {
            if (var2x.error != null) {
               this.partyResponseForm.reset(this.header, var2x.error);
               this.partyResponseForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
                  this.makeCurrent(this.dialogueForm);
               });
               this.makeCurrent(this.partyResponseForm);
            } else {
               var2.setIsInPartyConfig.runAndSend(true);
               this.makeCurrent(this.partyConfigForm);
            }

            this.waitingForPartyConfirm = false;
         }

      });
      if (var2.hasSettlerAccess) {
         this.equipmentForm = (EquipmentForm)this.addComponent(new ShopEquipmentForm(var1, var2, (var2x) -> {
            var2.setIsInEquipment.runAndSend(false);
            this.makeCurrent(this.dialogueForm);
         }));
      }

      this.workForm = (DialogueForm)this.addComponent(new DialogueForm("work", var3, var4, this.header, (GameMessage)null));
      this.buyForm = (Form)this.addComponent(new ShopTradingForm(var1, var2, var3, var4, false, new LocalMessage("ui", "backbutton"), 150, (var1x) -> {
         this.makeCurrent(this.dialogueForm);
      }));
      this.sellForm = (Form)this.addComponent(new ShopTradingForm(var1, var2, var3, var4, true, new LocalMessage("ui", "backbutton"), 150, (var1x) -> {
         this.makeCurrent(this.dialogueForm);
      }));
      this.recruitForm = (DialogueForm)this.addComponent(new DialogueForm("recruit", var3, var4, this.header, var2.getRecruitMessage()));
      if (var2.recruitItems != null) {
         this.acceptRecruitButton = this.recruitForm.addDialogueOption(new LocalMessage("ui", "acceptbutton"), () -> {
            NoticeForm var2x = new NoticeForm("loadingsettlements", 300, 400);
            var2x.setButtonCooldown(-2);
            var2x.setupNotice((GameMessage)(new LocalMessage("ui", "loadingdotdot")));
            this.addComponent(var2x, (var1, var2xx) -> {
               if (!var2xx) {
                  this.removeComponent(var1);
               }

            });
            this.makeCurrent(var2x);
            var2.acceptRecruitAction.runAndSend();
         });
      } else {
         this.acceptRecruitButton = null;
      }

      this.updateAcceptRecruitButton();
      this.recruitForm.addDialogueOption(new LocalMessage("ui", "settlerwhatcanyoudo"), () -> {
         this.makeCurrent(this.whatCanYouDoForm);
      });
      this.recruitForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.recruitForm.setHeight(Math.max(this.recruitForm.getContentHeight() + 5, var4));
      GameMessageBuilder var6 = new GameMessageBuilder();
      var2.humanShop.jobTypeHandler.getTypePriorities().stream().filter((var0) -> {
         return !var0.disabledBySettler && var0.type.displayName != null;
      }).sorted(Comparator.comparingInt((var0) -> {
         return var0.type.getID();
      })).forEachOrdered((var1x) -> {
         var6.append("\n\t- ").append(var1x.type.displayName);
      });
      if (var2.humanShop.canJoinAdventureParties) {
         var6.append("\n").append("ui", "settlericanadventure");
      }

      LocalMessage var7 = new LocalMessage("ui", "settlericando", "list", var6);
      this.whatCanYouDoForm = (DialogueForm)this.addComponent(new DialogueForm("whatcanyoudo", var3, var4, this.header, var7));
      this.whatCanYouDoForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.recruitForm);
      });
      this.whatCanYouDoForm.setHeight(Math.max(this.whatCanYouDoForm.getContentHeight() + 5, var4));
      this.missionFailedForm = (DialogueForm)this.addComponent(new DialogueForm("missionFailed", var3, var4, this.header, (GameMessage)(var2.missionFailedMessage != null ? var2.missionFailedMessage : new LocalMessage("ui", "settlermissionfailed"))));
      this.missionFailedForm.addDialogueOption(new LocalMessage("ui", "continuebutton"), () -> {
         var2.continueFailedMissionAction.runAndSend();
         this.makeCurrent(this.dialogueForm);
      });
      this.expeditionsForms = new DialogueForm[var2.expeditionLists.size()];
      this.unavailableExpeditionsForms = new DialogueForm[var2.expeditionLists.size()];

      for(int var8 = 0; var8 < var2.expeditionLists.size(); ++var8) {
         int var9 = var8;
         ExpeditionList var10 = (ExpeditionList)var2.expeditionLists.get(var8);
         DialogueForm var11 = (DialogueForm)this.addComponent(new DialogueForm("expeditions" + var8, var3, var5, this.header, var10.selectMessage));
         this.expeditionsForms[var8] = var11;
         var11.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
            this.makeCurrent(this.dialogueForm);
         });
         DialogueForm var12 = (DialogueForm)this.addComponent(new DialogueForm("expeditionsUnavailable" + var8, var3, var5, this.header, var10.selectMessage));
         this.unavailableExpeditionsForms[var8] = var12;
         var12.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
            this.makeCurrent(var11);
         });
         boolean var13 = false;

         for(int var14 = 0; var14 < var10.expeditions.size(); ++var14) {
            ContainerExpedition var16 = (ContainerExpedition)var10.expeditions.get(var14);
            if (var16.available) {
               var11.addDialogueOption(var16.expedition.getDisplayName(), () -> {
                  this.focusOnExpedition(var9, var14, var10.focusMessage, var16, var11);
               });
            } else {
               var13 = true;
               FormDialogueOption var17 = var12.addDialogueOption(var16.expedition.getDisplayName(), () -> {
               });
               var17.setActive(false);
               GameMessage var18 = var16.expedition.getUnavailableMessage();
               if (var18 != null) {
                  var17.tooltipsSupplier = () -> {
                     return new StringTooltips(var18.translate());
                  };
               }
            }
         }

         if (var13) {
            var11.addDialogueOption(var10.moreOptionsDialogue, () -> {
               this.makeCurrent(var12);
            });
         }

         var11.setHeight(Math.min(Math.max(var11.getContentHeight(), var4), var5));
         var12.setHeight(Math.min(Math.max(var12.getContentHeight(), var4), var5));
      }

      this.expeditionFocus = (DialogueForm)this.addComponent(new DialogueForm("expeditionFocus", var3, var4, (GameMessage)null, (GameMessage)null));
      this.updateDialogue();
      if (var2.humanShop.missionFailed) {
         this.makeCurrent(this.missionFailedForm);
      } else if (var2.workInvMessage != null && !var2.humanShop.workInventory.isEmpty()) {
         this.makeCurrent(this.workInvForm);
      } else if (var2.startInRecruitment) {
         this.makeCurrent(this.recruitForm);
      } else {
         this.makeCurrent(this.dialogueForm);
      }

   }

   public ShopContainerForm(Client var1, T var2) {
      this(var1, var2, 408, 170, 240);
   }

   public void updateDialogue() {
      this.updateWorkForms();
      this.dialogueForm.reset(this.header, ((ShopContainer)this.container).introMessage);
      this.setupExtraDialogueOptions();
      if (((ShopContainer)this.container).humanShop.getSettler() == null || ((ShopContainer)this.container).recruitItems == null && ((ShopContainer)this.container).recruitError == null) {
         if (this.isCurrent(this.recruitForm)) {
            this.makeCurrent(this.dialogueForm);
         }
      } else {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerjoinme"), () -> {
            this.makeCurrent(this.recruitForm);
         });
      }

      if (((ShopContainer)this.container).hasSettlerAccess) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerchangeequipment"), () -> {
            ((ShopContainer)this.container).setIsInEquipment.runAndSend(true);
            this.makeCurrent(this.equipmentForm);
         });
      } else if (this.equipmentForm != null && this.isCurrent(this.equipmentForm)) {
         this.makeCurrent(this.dialogueForm);
      }

      boolean var1 = this.updateWorkForms();
      if (var1) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerchangework"), () -> {
            this.makeCurrent(this.workForm);
         });
      } else if (this.isCurrent(this.workForm)) {
         this.makeCurrent(this.dialogueForm);
      }

      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlergoodbye"), () -> {
         this.client.closeContainer(true);
      });
      this.dialogueForm.setHeight(Math.max(this.dialogueForm.getContentHeight() + 5, this.height));
   }

   protected void setupExtraDialogueOptions() {
      if (((ShopContainer)this.container).happinessModifiers != null) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlermood"), () -> {
            this.makeCurrent(this.moodForm);
         });
      }

      if (((ShopContainer)this.container).hasSettlerAccess && ((ShopContainer)this.container).canJoinAdventureParties && !((ShopContainer)this.container).isInYourAdventureParty) {
         if (this.isCurrent(this.partyConfigForm)) {
            ((ShopContainer)this.container).setIsInPartyConfig.runAndSend(false);
            this.makeCurrent(this.dialogueForm);
         }

         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerjoinparty"), () -> {
            ((ShopContainer)this.container).joinAdventurePartyAction.runAndSend();
            this.waitingForPartyConfirm = true;
         });
         if (((ShopContainer)this.container).isSettlerOutsideSettlement) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerreturntosettlement"), () -> {
               ((ShopContainer)this.container).returnToSettlementAction.runAndSend();
            });
         }
      } else if (((ShopContainer)this.container).isInYourAdventureParty) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "confiureadventureparty"), () -> {
            ((ShopContainer)this.container).setIsInPartyConfig.runAndSend(true);
            this.makeCurrent(this.partyConfigForm);
         });
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerleaveparty"), () -> {
            ((ShopContainer)this.container).leaveAdventurePartyAction.runAndSend();
         });
      }

      if (((ShopContainer)this.container).items != null) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "traderwantbuy"), () -> {
            this.makeCurrent(this.buyForm);
         });
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "traderwantsell"), () -> {
            this.makeCurrent(this.sellForm);
         });
      }

      for(int var1 = 0; var1 < ((ShopContainer)this.container).expeditionLists.size(); ++var1) {
         ExpeditionList var3 = (ExpeditionList)((ShopContainer)this.container).expeditionLists.get(var1);
         this.dialogueForm.addDialogueOption(var3.selectDialogue, () -> {
            this.makeCurrent(this.expeditionsForms[var1]);
         });
      }

   }

   protected void updateWorkInventoryForm() {
      this.workInvForm.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), ((ShopContainer)this.container).getWorkInvMessage());
      this.workInvForm.addDialogueOption(new LocalMessage("ui", "settlerreceiveitems"), () -> {
         ((ShopContainer)this.container).workItemsAction.runAndSend(ShopContainer.WorkItemsAction.RECEIVE);
         this.makeCurrent(this.dialogueForm);
      });
      this.workInvForm.addDialogueOption(new LocalMessage("ui", "continuebutton"), () -> {
         ((ShopContainer)this.container).workItemsAction.runAndSend(ShopContainer.WorkItemsAction.CONTINUE);
         this.makeCurrent(this.dialogueForm);
      });
      this.workInvForm.setHeight(GameMath.limit(this.workInvForm.getContentHeight() + 5, 160, 280));
      if (Input.lastInputIsController) {
         this.workInvForm.content.setScrollY(100000);
      }

      if (this.isCurrent(this.workInvForm) && ((ShopContainer)this.container).humanShop.workInventory.isEmpty()) {
         this.makeCurrent(this.dialogueForm);
      }

   }

   protected boolean updateWorkForms() {
      ((ShopContainer)this.container).humanShop.workDirty = false;
      this.workForm.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), (GameMessage)(new LocalMessage("ui", "settlerchangewhat")));
      boolean var1 = this.setupExtraWorkOptions();
      this.workForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      return var1;
   }

   public boolean setupExtraWorkOptions() {
      AtomicBoolean var1 = new AtomicBoolean(false);
      ((ShopContainer)this.container).workSettings.stream().sorted(Comparator.comparingInt((var0) -> {
         return var0.setting.dialogueSort;
      })).forEach((var2) -> {
         var1.set(var2.handler.setupWorkForm(this, this.workForm) || var1.get());
      });
      return var1.get();
   }

   public void updateAcceptRecruitButton() {
      if (this.acceptRecruitButton != null) {
         this.acceptRecruitButton.setActive(((ShopContainer)this.container).canPayForRecruit());
      }
   }

   public void openSettlementList(int var1, List<SettlementOpenSettlementListEvent.SettlementOption> var2) {
      if (((ShopContainer)this.container).humanShop.getUniqueID() != var1) {
         GameLog.warn.println("Received wrong settlement list for mob id " + var1);
      } else {
         if (this.client.getLevel().isIslandPosition()) {
            var2.sort(Comparator.comparing((var1x) -> {
               return (new Point(var1x.islandX, var1x.islandY)).distance((double)this.client.getLevel().getIslandX(), (double)this.client.getLevel().getIslandY());
            }));
         }

         SelectSettlementContinueForm.Option[] var3 = (SelectSettlementContinueForm.Option[])var2.stream().map((var2x) -> {
            return new SelectSettlementContinueForm.Option(true, var2x.islandX, var2x.islandY, var2x.name) {
               public void onSelected(SelectSettlementContinueForm var1x) {
                  ShopContainerForm.this.client.network.sendPacket(PacketShopContainerUpdate.recruitSettler(var1, this.islandX, this.islandY));
                  NoticeForm var2 = new NoticeForm("loadingresponse", 300, 400);
                  var2.setButtonCooldown(-2);
                  var2.setupNotice((GameMessage)(new LocalMessage("ui", "loadingdotdot")));
                  ShopContainerForm.this.addComponent(var2, (var1xx, var2x) -> {
                     if (!var2x) {
                        ShopContainerForm.this.removeComponent(var1xx);
                     }

                  });
                  ShopContainerForm.this.makeCurrent(var2);
               }
            };
         }).toArray((var0) -> {
            return new SelectSettlementContinueForm.Option[var0];
         });
         SelectSettlementContinueForm var4 = new SelectSettlementContinueForm("movetosettlement", 300, 400, new LocalMessage("ui", "settlementselect"), var3) {
            public void onCancel() {
               ShopContainerForm.this.makeCurrent(ShopContainerForm.this.recruitForm);
               this.removeComponent(this);
            }
         };
         this.addComponent(var4);
         this.makeCurrent(var4);
      }
   }

   public void submitRecruitResponse(int var1, GameMessage var2) {
      if (((ShopContainer)this.container).humanShop.getUniqueID() != var1) {
         GameLog.warn.println("Received wrong recruit response for mob id " + var1);
      } else {
         if (var2 == null) {
            ((ShopContainer)this.container).payForRecruit();
            this.client.closeContainer(true);
         } else {
            NoticeForm var3 = new NoticeForm("loadingresponse", 300, 400);
            var3.setupNotice(var2);
            var3.setButtonCooldown(50);
            var3.onContinue(() -> {
               this.makeCurrent(this.recruitForm);
            });
            this.addComponent(var3, (var1x, var2x) -> {
               if (!var2x) {
                  this.removeComponent(var1x);
               }

            });
            this.makeCurrent(var3);
         }

      }
   }

   public void updateExpeditionBuyButton(PlayerMob var1) {
      if (this.expeditionBuyButton != null) {
         int var2 = var1.getInv().getAmount(ItemRegistry.getItem("coin"), true, false, false, "buy");
         this.expeditionBuyButton.setActive(var2 >= this.expeditionBuyCost);
      }

   }

   public void focusOnExpedition(int var1, int var2, GameMessage var3, ContainerExpedition var4, DialogueForm var5) {
      this.expeditionBuyCost = var4.price;
      String var6 = var3.translate().replace("<expedition>", var4.expedition.getDisplayName().translate()).replace("<cost>", Integer.toString(this.expeditionBuyCost)).replace("<chance>", Integer.toString((int)(var4.successChance * 100.0F)));
      this.expeditionFocus.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), (GameMessage)(new StaticMessage(var6)));
      this.expeditionBuyButton = this.expeditionFocus.addDialogueOption(new LocalMessage("ui", "buybutton"), () -> {
         ((ShopContainer)this.container).buyExpeditionButton.runAndSend(var1, var2);
      });
      this.expeditionFocus.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(var5);
         this.expeditionBuyButton = null;
      });
      this.updateExpeditionBuyButton(this.client.getPlayer());
      this.makeCurrent(this.expeditionFocus);
      ContainerComponent.setPosFocus(this.expeditionFocus);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.missionFailedForm);
      ContainerComponent.setPosFocus(this.workInvForm);
      ContainerComponent.setPosFocus(this.dialogueForm);
      ContainerComponent.setPosFocus(this.moodForm);
      ContainerComponent.setPosFocus(this.partyResponseForm);
      ContainerComponent.setPosFocus(this.partyConfigForm);
      ContainerComponent.setPosFocus(this.workForm);
      ContainerComponent.setPosFocus(this.recruitForm);
      ContainerComponent.setPosFocus(this.whatCanYouDoForm);
      DialogueForm[] var1 = this.expeditionsForms;
      int var2 = var1.length;

      int var3;
      DialogueForm var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         ContainerComponent.setPosFocus(var4);
      }

      var1 = this.unavailableExpeditionsForms;
      var2 = var1.length;

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         ContainerComponent.setPosFocus(var4);
      }

      ContainerComponent.setPosFocus(this.expeditionFocus);
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updateExpeditionBuyButton(var2);
      if (this.isCurrent(this.recruitForm)) {
         this.updateAcceptRecruitButton();
      }

      if (((ShopContainer)this.container).humanShop.workDirty) {
         this.updateWorkInventoryForm();
         this.updateWorkForms();
      }

      super.draw(var1, var2, var3);
   }
}
