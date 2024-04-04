package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalToggle;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTeamInvite;
import necesse.gfx.forms.components.FormTeamJoinRequest;
import necesse.gfx.forms.components.FormTeamMember;
import necesse.gfx.forms.components.FormTeamPlayerInvite;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPAllTeamsUpdateEvent;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PvPTeamsContainerForm extends ContainerFormSwitcher<PvPTeamsContainer> {
   public static boolean pauseGameOnClose = false;
   private boolean isOwner;
   public Form main;
   public Form invites;
   public Form changeName;
   public Form joinTeam;
   public ConfirmationForm confirmForm;
   public FormLocalLabel ownerLabel;
   public FormContentBox membersContent;
   public FormContentBox invitesContent;
   public FormContentBox joinTeamContent;
   public FormTextInput changeNameInput;
   public FormContentIconButton isPublicButton;

   public PvPTeamsContainerForm(Client var1, PvPTeamsContainer var2) {
      super(var1, var2);
      this.setupMainForm();
      FormFlow var3 = new FormFlow(5);
      this.joinTeam = (Form)this.addComponent(new Form("jointeam", 300, 400));
      this.joinTeam.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "teamjoinateam", new FontOptions(20), 0, this.joinTeam.getWidth() / 2, 0, this.joinTeam.getWidth() - 20), 10));
      this.joinTeamContent = (FormContentBox)this.joinTeam.addComponent(new FormContentBox(0, var3.next(350), this.joinTeam.getWidth(), 350));
      this.joinTeamContent.alwaysShowVerticalScrollBar = true;
      ((FormLocalTextButton)this.joinTeam.addComponent(new FormLocalTextButton("ui", "backbutton", 4, var3.next(40), this.joinTeam.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE))).onClicked((var1x) -> {
         this.makeCurrent(this.main);
      });
      this.joinTeam.setHeight(var3.next());
      this.confirmForm = (ConfirmationForm)this.addComponent(new ConfirmationForm("teamconfirm"));
      this.changeName = (Form)this.addComponent(new Form(300, 110));
      this.changeName.addComponent(new FormLocalLabel("ui", "teamchangename", new FontOptions(20), 0, this.changeName.getWidth() / 2, 5));
      this.changeNameInput = (FormTextInput)this.changeName.addComponent(new FormTextInput(4, 30, FormInputSize.SIZE_32_TO_40, this.changeName.getWidth() - 8, 20));
      ((FormLocalTextButton)this.changeName.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6))).onClicked((var2x) -> {
         var2.changeTeamNameAction.runAndSend(this.changeNameInput.getText());
         this.makeCurrent(this.main);
      });
      ((FormLocalTextButton)this.changeName.addComponent(new FormLocalTextButton("ui", "backbutton", this.changeName.getWidth() / 2 + 2, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.main);
      });
      var2.onEvent(PvPCurrentTeamUpdateEvent.class, (var1x) -> {
         this.onFullUpdate();
      });
      var2.onEvent(PvPOwnerUpdateEvent.class, (var1x) -> {
         this.onOwnerUpdate();
      });
      var2.onEvent(PvPPublicUpdateEvent.class, (var1x) -> {
         this.onPublicUpdate();
      });
      var2.onEvent(PvPMemberUpdateEvent.class, (var1x) -> {
         this.updateMembersContent();
      });
      var2.onEvent(PvPJoinRequestUpdateEvent.class, (var1x) -> {
         this.updateMembersContent();
      });
      var2.onEvent(PvPAllTeamsUpdateEvent.class, (var1x) -> {
         this.updateJoinTeamsContent(var1x);
      });
      this.makeCurrent(this.main);
   }

   public void setupMainForm() {
      if (this.main != null) {
         this.removeComponent(this.main);
      }

      this.main = (Form)this.addComponent(new Form(300, 400));
      this.ownerLabel = null;
      this.membersContent = null;
      this.isOwner = false;
      FormFlow var1 = new FormFlow(10);
      this.main.addComponent(new FormLocalLabel("ui", "pvplabel", new FontOptions(16), 0, this.main.getWidth() / 2, var1.next(20)));
      FormHorizontalToggle var2 = (FormHorizontalToggle)this.main.addComponent(new FormHorizontalToggle(this.main.getWidth() / 2 - 16, var1.next(30)));
      var2.setToggled(this.client.pvpEnabled());
      var2.onToggled((var1x) -> {
         this.client.setPvP(((FormButtonToggle)var1x.from).isToggled());
      });
      var2.setCooldown(5000);
      long var3 = System.currentTimeMillis() - this.client.pvpChangeTime;
      if (var3 < 5000L) {
         var2.startCooldown((int)(5000L - var3));
      }

      var2.setActive(!this.client.worldSettings.forcedPvP);
      PvPTeamsContainer.TeamData var5 = ((PvPTeamsContainer)this.container).data.currentTeam;
      FormLocalLabel var6;
      if (var5 == null) {
         this.main.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "teamnocurrent", new FontOptions(16), 0, this.main.getWidth() / 2, 0, this.main.getWidth() - 10), 10));
         ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "teamcreate", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var1x) -> {
            ((PvPTeamsContainer)this.container).createTeamButton.runAndSend();
            ((FormButton)var1x.from).startCooldown(5000);
         });
         ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "teamjoinateam", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var1x) -> {
            this.joinTeamContent.clearComponents();
            this.joinTeamContent.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 10, this.joinTeamContent.getWidth() - 20));
            this.joinTeamContent.setContentBox(new Rectangle(this.joinTeamContent.getWidth(), this.joinTeamContent.getHeight()));
            this.makeCurrent(this.joinTeam);
            ((PvPTeamsContainer)this.container).askForExistingTeams.runAndSend();
         });
         var1.next(5);
         var6 = (FormLocalLabel)this.main.addComponent(new FormLocalLabel("ui", "teaminvites", new FontOptions(20), 0, this.main.getWidth() / 2, var1.next(), this.main.getWidth() - 10));
         var1.next(var6.getHeight() + 10);
         this.invitesContent = (FormContentBox)this.main.addComponent(new FormContentBox(0, var1.next(120), this.main.getWidth(), 120));
         this.updateInvitesContent();
      } else {
         var6 = (FormLocalLabel)this.main.addComponent(new FormLocalLabel(new StaticMessage(var5.name), new FontOptions(20), 0, this.main.getWidth() / 2, var1.next(), this.main.getWidth() - 10));
         var1.next(var6.getHeight() + 5);
         long var7 = var5.owner;
         PvPTeamsContainer.MemberData var9 = (PvPTeamsContainer.MemberData)((PvPTeamsContainer)this.container).data.members.stream().filter((var2x) -> {
            return var2x.auth == var7;
         }).findFirst().orElse((Object)null);
         String var10 = var9 == null ? "Unknown" : var9.name;
         this.ownerLabel = (FormLocalLabel)this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "teamowner", "owner", var10), new FontOptions(12), 0, this.main.getWidth() / 2, var1.next(), this.main.getWidth() - 10));
         var1.next(this.ownerLabel.getHeight() + 5);
         this.isOwner = var7 == GameAuth.getAuthentication();
         FormLocalLabel var11;
         if (this.isOwner) {
            var1.next(5);
            var11 = (FormLocalLabel)this.main.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "teamcanselfjoin", new FontOptions(16), -1, 28, 0, this.main.getWidth() - 32), 10));
            this.isPublicButton = (FormContentIconButton)this.main.addComponent(new FormContentIconButton(4, var11.getY() + var11.getHeight() / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_checked_20, new GameMessage[0]));
            this.isPublicButton.onClicked((var1x) -> {
               ((PvPTeamsContainer)this.container).setPublicAction.runAndSend(!((PvPTeamsContainer)this.container).data.currentTeam.isPublic);
            });
            this.onPublicUpdate();
            ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "teamchangename", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var2x) -> {
               this.changeNameInput.setText(var5.name);
               this.makeCurrent(this.changeName);
            });
         }

         ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "teaminvite", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var1x) -> {
            this.openInvites();
         });
         var1.next(5);
         var11 = (FormLocalLabel)this.main.addComponent(new FormLocalLabel("ui", "teammembers", new FontOptions(20), 0, this.main.getWidth() / 2, var1.next(), this.main.getWidth() - 10));
         var1.next(var11.getHeight() + 10);
         this.membersContent = (FormContentBox)this.main.addComponent(new FormContentBox(0, var1.next(120), this.main.getWidth(), 120));
         this.updateMembersContent();
         ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "teamleave", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var1x) -> {
            GameMessageBuilder var2 = (new GameMessageBuilder()).append("ui", "teamleaveconf");
            if (((PvPTeamsContainer)this.container).data.currentTeam != null && !((PvPTeamsContainer)this.container).data.currentTeam.isPublic) {
               var2.append("\n\n").append("ui", "teamleaveneedinvite");
            }

            this.confirmForm.setupConfirmation((GameMessage)var2, () -> {
               ((PvPTeamsContainer)this.container).leaveTeamButton.runAndSend();
               this.setupMainForm();
               this.makeCurrent(this.main);
            }, () -> {
               this.makeCurrent(this.main);
            });
            this.makeCurrent(this.confirmForm);
         });
      }

      ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "closebutton", 4, var1.next(40), this.main.getWidth() - 8))).onClicked((var1x) -> {
         this.client.closeContainer(true);
      });
      this.main.setHeight(var1.next());
      this.main.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void updateInvitesContent() {
      if (this.invitesContent != null) {
         this.invitesContent.clearComponents();
         FormFlow var1 = new FormFlow();
         if (((PvPTeamsContainer)this.container).data.invites.isEmpty()) {
            var1.next(10);
            this.invitesContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "teamnoinvites", new FontOptions(16), 0, this.invitesContent.getWidth() / 2, 10, this.invitesContent.getWidth() - 20), 10));
         } else {
            int var2 = 0;

            for(Iterator var3 = ((PvPTeamsContainer)this.container).data.invites.iterator(); var3.hasNext(); ++var2) {
               PvPTeamsContainer.InviteData var4 = (PvPTeamsContainer.InviteData)var3.next();
               Color var5 = var2 % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
               this.invitesContent.addComponent(new FormTeamInvite(4, var1.next(20), this.main.getWidth() - 8, 20, var4, var5) {
                  public void onAccept(PvPTeamsContainer.InviteData var1) {
                     PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamInviteReply(var1.teamID, true));
                  }

                  public void onDecline(PvPTeamsContainer.InviteData var1) {
                     ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).data.invites.removeIf((var1x) -> {
                        return var1x.teamID == var1.teamID;
                     });
                     PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamInviteReply(var1.teamID, false));
                     PvPTeamsContainerForm.this.updateInvitesContent();
                  }
               });
            }
         }

         this.invitesContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), var1.next()));
      }

   }

   public void updateJoinTeamsContent(PvPAllTeamsUpdateEvent var1) {
      this.joinTeamContent.clearComponents();
      FormFlow var2 = new FormFlow();
      if (var1.teams.isEmpty()) {
         var2.next(10);
         this.joinTeamContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "teamnoteams", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 0, this.joinTeamContent.getWidth() - 20), 10));
      } else {
         Iterator var3 = var1.teams.iterator();

         while(var3.hasNext()) {
            PvPTeamsContainer.TeamData var4 = (PvPTeamsContainer.TeamData)var3.next();
            this.joinTeamContent.addComponent((FormLabel)var2.nextY(new FormLabel(var4.name, new FontOptions(20), -1, 5, 0), 2));
            this.joinTeamContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel(new LocalMessage("ui", "teammembercount", new Object[]{"count", var4.memberCount}), new FontOptions(16), -1, 5, 0), 2));
            if (var4.isPublic) {
               this.joinTeamContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "teampublic", new FontOptions(16), -1, 5, 0), 2));
               ((FormLocalTextButton)this.joinTeamContent.addComponent((FormLocalTextButton)var2.nextY(new FormLocalTextButton("ui", "teamjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE)))).onClicked((var2x) -> {
                  ((PvPTeamsContainer)this.container).requestToJoinTeam.runAndSend(var4.teamID);
                  ((FormButton)var2x.from).startCooldown(5000);
               });
            } else {
               this.joinTeamContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "teamprivate", new FontOptions(16), -1, 5, 0), 2));
               ((FormLocalTextButton)this.joinTeamContent.addComponent((FormLocalTextButton)var2.nextY(new FormLocalTextButton("ui", "teamrequestjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE)))).onClicked((var2x) -> {
                  ((PvPTeamsContainer)this.container).requestToJoinTeam.runAndSend(var4.teamID);
                  ((FormButton)var2x.from).startCooldown(5000);
               });
            }

            var2.next(8);
            this.joinTeamContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, var2.next(), this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), true));
            var2.next(8);
         }
      }

      this.joinTeamContent.setContentBox(new Rectangle(0, 0, this.joinTeamContent.getWidth(), var2.next()));
   }

   public void updateMembersContent() {
      if (this.membersContent != null) {
         this.membersContent.clearComponents();
         FormFlow var1 = new FormFlow();
         int var2 = 0;

         Iterator var3;
         PvPTeamsContainer.MemberData var4;
         Color var5;
         for(var3 = ((PvPTeamsContainer)this.container).data.members.iterator(); var3.hasNext(); ++var2) {
            var4 = (PvPTeamsContainer.MemberData)var3.next();
            var5 = var2 % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
            this.membersContent.addComponent(new FormTeamMember(4, var1.next(20), this.main.getWidth() - 8, 20, var4, this.isOwner, var5) {
               public void onKickMember(PvPTeamsContainer.MemberData var1) {
                  ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).kickMemberAction.runAndSend(var1.auth);
               }

               public void onPassOwnership(PvPTeamsContainer.MemberData var1) {
                  ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).passOwnershipAction.runAndSend(var1.auth);
               }
            });
         }

         if (!((PvPTeamsContainer)this.container).data.joinRequests.isEmpty()) {
            var1.next(10);
            this.membersContent.addComponent(new FormLocalLabel("ui", "teamjoinrequests", new FontOptions(16), -1, 5, var1.next(20) + 2));

            for(var3 = ((PvPTeamsContainer)this.container).data.joinRequests.iterator(); var3.hasNext(); ++var2) {
               var4 = (PvPTeamsContainer.MemberData)var3.next();
               var5 = var2 % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
               this.membersContent.addComponent(new FormTeamJoinRequest(4, var1.next(20), this.main.getWidth() - 8, 20, var4, var5) {
                  public void onAccept(PvPTeamsContainer.MemberData var1) {
                     PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamRequestReply(var1.auth, true));
                  }

                  public void onDecline(PvPTeamsContainer.MemberData var1) {
                     PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamRequestReply(var1.auth, false));
                     ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).data.joinRequests.removeIf((var1x) -> {
                        return var1x.auth == var1.auth;
                     });
                     PvPTeamsContainerForm.this.updateMembersContent();
                  }
               });
            }
         }

         this.membersContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), var1.next()));
      }

   }

   public void openInvites() {
      if (this.invites != null) {
         this.removeComponent(this.invites);
      }

      this.invites = (Form)this.addComponent(new Form(300, 300));
      this.invites.addComponent(new FormLocalLabel("ui", "teaminvite", new FontOptions(20), 0, this.invites.getWidth() / 2, 5));
      FormContentBox var1 = (FormContentBox)this.invites.addComponent(new FormContentBox(0, 30, this.invites.getWidth(), this.invites.getHeight() - 110));
      List var2 = (List)this.client.streamClients().filter((var1x) -> {
         return var1x != null && var1x.slot != this.client.getSlot();
      }).map((var1x) -> {
         return new FormTeamPlayerInvite(0, 0, var1x, this.invites.getWidth(), (Color)null);
      }).collect(Collectors.toList());
      FormFlow var3 = new FormFlow();
      int var4 = 0;

      for(Iterator var5 = var2.iterator(); var5.hasNext(); ++var4) {
         FormTeamPlayerInvite var6 = (FormTeamPlayerInvite)var5.next();
         var6.backgroundColor = var4 % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
         var6.setPosition(0, var3.next(20));
         var1.addComponent(var6);
      }

      var1.setContentBox(new Rectangle(0, 0, var1.getWidth(), var3.next()));
      ((FormLocalTextButton)this.invites.addComponent(new FormLocalTextButton("ui", "teaminviteselected", 4, this.invites.getHeight() - 80, this.invites.getWidth() - 8))).onClicked((var2x) -> {
         ClientClient[] var3 = (ClientClient[])var2.stream().filter((var1) -> {
            return var1.selected && this.client.getClient(var1.client.slot) == var1.client;
         }).map((var0) -> {
            return var0.client;
         }).toArray((var0) -> {
            return new ClientClient[var0];
         });
         if (var3.length != 0) {
            ((PvPTeamsContainer)this.container).inviteMembersAction.runAndSend(var3);
         }

         this.makeCurrent(this.main);
         this.removeComponent(this.invites);
         this.invites = null;
      });
      ((FormLocalTextButton)this.invites.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.invites.getHeight() - 40, this.invites.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.main);
         this.removeComponent(this.invites);
         this.invites = null;
      });
      this.invites.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.makeCurrent(this.invites);
   }

   public void onFullUpdate() {
      boolean var1 = this.isCurrent(this.main);
      this.setupMainForm();
      if (var1) {
         this.makeCurrent(this.main);
      } else if (((PvPTeamsContainer)this.container).data.currentTeam != null) {
         if (this.isCurrent(this.joinTeam)) {
            this.makeCurrent(this.main);
         }

         if (((PvPTeamsContainer)this.container).data.currentTeam.owner != GameAuth.getAuthentication() && this.isCurrent(this.changeName)) {
            this.makeCurrent(this.main);
         }
      } else if (this.invites != null && this.isCurrent(this.invites) || this.isCurrent(this.changeName)) {
         this.makeCurrent(this.main);
      }

   }

   public void onOwnerUpdate() {
      if (!this.isOwner && ((PvPTeamsContainer)this.container).data.currentTeam.owner != GameAuth.getAuthentication()) {
         if (this.ownerLabel != null) {
            long var1 = ((PvPTeamsContainer)this.container).data.currentTeam.owner;
            PvPTeamsContainer.MemberData var3 = (PvPTeamsContainer.MemberData)((PvPTeamsContainer)this.container).data.members.stream().filter((var2) -> {
               return var2.auth == var1;
            }).findFirst().orElse((Object)null);
            String var4 = var3 == null ? "Unknown" : var3.name;
            this.ownerLabel.setLocalization(new LocalMessage("ui", "teamowner", "owner", var4));
         }
      } else {
         this.onFullUpdate();
      }

   }

   public void onPublicUpdate() {
      if (this.isPublicButton != null && ((PvPTeamsContainer)this.container).data.currentTeam != null) {
         this.isPublicButton.setIcon(((PvPTeamsContainer)this.container).data.currentTeam.isPublic ? Settings.UI.button_checked_20 : Settings.UI.button_escaped_20);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.main.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      if (this.invites != null) {
         this.invites.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      }

      this.changeName.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.joinTeam.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }

   public void onContainerClosed() {
      super.onContainerClosed();
      if (pauseGameOnClose) {
         State var1 = GlobalData.getCurrentState();
         if (var1 instanceof MainGame) {
            var1.setRunning(false);
         }

         pauseGameOnClose = false;
      }

   }
}
