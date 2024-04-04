package necesse.gfx.forms.components.chat;

import com.codedisaster.steamworks.SteamControllerHandle;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormFairTypeEdit;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormCursorMoveEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.FormShader;

public class FormChatInput extends FormFairTypeEdit implements FormPositionContainer {
   private final Client client;
   private FormPosition position;
   private int width;
   private int scroll;
   public int logScroll = -1;
   private String autocompleteQuery = null;
   private ArrayList<FairTypeDrawOptions> autoCompleteList = null;
   private int selectedAutocomplete = -1;
   private FairTypeDrawOptions currentCommandUsage = null;
   private FormEventsHandler<FormInputEvent<FormChatInput>> submitEvents = new FormEventsHandler();

   public FormChatInput(int var1, int var2, Client var3, int var4) {
      super(ChatMessage.fontOptions, FairType.TextAlign.LEFT, Color.WHITE, -1, 1, 200);
      this.client = var3;
      this.width = var4;
      this.position = new FormFixedPosition(var1, var2);
      this.allowCaretSetTyping = false;
      this.allowCaretRemoveTyping = false;
      this.allowItemAppend = true;
      this.parsers = ChatMessage.getParsers(this.fontOptions);
      this.onChange((var1x) -> {
         this.updateAutocomplete();
         this.updateCommandUsage();
      });
      this.onCaretMove((var1x) -> {
         if (!var1x.causedByMouse) {
            this.fitScroll();
         }

         this.updateAutocomplete();
         this.updateCommandUsage();
      });
   }

   public FormChatInput onSubmit(FormEventListener<FormInputEvent<FormChatInput>> var1) {
      this.submitEvents.addListener(var1);
      return this;
   }

   public boolean submitControllerEnter() {
      InputEvent var1 = InputEvent.ControllerButtonEvent(ControllerEvent.customEvent((SteamControllerHandle)null, ControllerInput.MENU_SELECT), (TickManager)null);
      FormInputEvent var2 = new FormInputEvent(this, var1);
      this.submitEvents.onEvent(var2);
      if (!var2.hasPreventedDefault()) {
         var1.use();
         return true;
      } else {
         return false;
      }
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isKeyboardEvent() && !FormTypingComponent.isCurrentlyTyping()) {
         if (var1.state && (var1.getID() == 257 || var1.getID() == 335)) {
            this.setTyping(true);
            var1.use();
         }
      } else if (this.isTyping()) {
         if (!var1.state || var1.getID() != 257 && var1.getID() != 335) {
            if (var1.state && var1.getID() == 265) {
               if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                  ++this.selectedAutocomplete;
                  this.selectAutocomplete();
                  this.updateCommandUsage();
               } else if (this.logScroll < this.client.chatSubmits.size() - 1) {
                  ++this.logScroll;
                  this.setText((String)this.client.chatSubmits.get(this.logScroll), false);
               }

               var1.use();
            } else if (var1.state && var1.getID() == 264) {
               if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                  if (this.selectedAutocomplete != -1) {
                     --this.selectedAutocomplete;
                  }

                  this.selectAutocomplete();
                  this.updateCommandUsage();
               } else if (this.logScroll >= 0) {
                  --this.logScroll;
                  if (this.logScroll == -1) {
                     this.setText("", false);
                  } else {
                     this.setText((String)this.client.chatSubmits.get(this.logScroll), false);
                  }
               }

               var1.use();
            } else if (var1.state && var1.getID() == 258) {
               if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                  ++this.selectedAutocomplete;
                  this.selectAutocomplete();
                  this.updateCommandUsage();
               }

               var1.use();
            }
         } else {
            FormInputEvent var4 = new FormInputEvent(this, var1);
            this.submitEvents.onEvent(var4);
            if (!var4.hasPreventedDefault()) {
               var1.use();
            }
         }

         if (var1.isUsed()) {
            return;
         }

         super.handleInputEvent(InputEvent.OffsetHudEvent(Screen.input(), var1, -this.getX(), -this.getY()), var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (!this.client.isPaused()) {
         State var7 = GlobalData.getCurrentState();
         FormManager var8 = var7.getFormManager();
         if (!(var8 instanceof MainGameFormManager) || !((MainGameFormManager)var8).inventory.isHidden()) {
            Rectangle var9 = this.getDrawOptions().getBoundingBox(this.getX(), this.getY());
            var9.width = Math.max(var9.width, 100);
            var9.height = Math.max(var9.height, this.fontOptions.getSize());
            ControllerFocus.add(var1, var5, this, var9, var2, var3, this.controllerInitialFocusPriority, var4);
         }
      }
   }

   private void selectAutocomplete() {
      String var1 = this.getTextCharString();
      int var2 = var1.indexOf(" ", this.getCaretIndex() + 1);
      String var3 = var1.substring(var2 == -1 ? var1.length() : var2);
      if (this.selectedAutocomplete < 0) {
         this.selectedAutocomplete = this.autoCompleteList.size() + this.selectedAutocomplete % this.autoCompleteList.size();
      }

      String var4 = ((FairTypeDrawOptions)this.autoCompleteList.get(this.selectedAutocomplete % this.autoCompleteList.size())).getType().getParseString();
      int var5 = var4.length() - 1;
      var4 = var4 + var3;
      this.setText(var4, false);
      this.getCaret().typeIndex = var5;
      this.getCaret().updateProps((FormFairTypeEdit.CaretPosition)null, true, (FormCursorMoveEvent)null);
      this.fitScroll();
   }

   public String clearAndAddToLog() {
      String var1 = this.getText();
      String var2 = this.client.chatSubmits.isEmpty() ? null : (String)this.client.chatSubmits.get(0);
      if (!var1.equals(var2)) {
         this.client.chatSubmits.add(0, var1);
      }

      this.logScroll = -1;
      this.autoCompleteList = null;
      this.selectedAutocomplete = -1;
      this.autocompleteQuery = null;
      this.currentCommandUsage = null;
      this.setText("");
      return var1;
   }

   public void setText(String var1, boolean var2) {
      super.setText(var1, var2);
      this.fitScroll();
   }

   private void updateAutocomplete() {
      this.autoCompleteList = null;
      this.selectedAutocomplete = -1;
      this.autocompleteQuery = null;
      String var1 = this.getTextCharString();
      if (this.getCaretIndex() >= 0 && var1.startsWith("/")) {
         int var2 = var1.indexOf(" ", this.getCaretIndex() + 1);
         String var3 = var1.substring(1, var2 == -1 ? var1.length() : var2);
         this.autocompleteQuery = var3;
         this.generateCommandsFromList(this.client.commandsManager.autocomplete(new ParsedCommand(var3), (ServerClient)null), var3);
      }

   }

   private void updateCommandUsage() {
      this.currentCommandUsage = null;
      String var1 = this.getTextCharString();
      if (this.getCaretIndex() >= 0 && var1.startsWith("/")) {
         int var2 = var1.indexOf(" ", this.getCaretIndex() + 1);
         String var3 = var1.substring(1, var2 == -1 ? var1.length() : var2);
         String var4 = this.client.commandsManager.getCurrentUsage(new ParsedCommand(var3), (ServerClient)null);
         if (var4 != null) {
            FairType var5 = (new FairType()).append(new FontOptions(16), var4).applyParsers(TypeParsers.GAME_COLOR);
            this.currentCommandUsage = var5.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
         } else {
            this.currentCommandUsage = null;
         }
      }

   }

   public void submitEscapeEvent(InputEvent var1) {
      if (this.isTyping()) {
         if (this.selectedAutocomplete >= 0 && this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
            this.selectedAutocomplete = -1;
            this.setText("/" + this.autocompleteQuery);
         } else {
            this.setTyping(false);
         }

         var1.use();
      }

   }

   private void generateCommandsFromList(List<AutoComplete> var1, String var2) {
      this.autoCompleteList = (ArrayList)var1.stream().map((var1x) -> {
         FairType var2x = (new FairType()).append(new FontOptions(16), "/" + var1x.getFullCommand(var2));
         return var2x.getDrawOptions(FairType.TextAlign.LEFT);
      }).collect(Collectors.toCollection(ArrayList::new));
   }

   public void onAutocompletePacket(List<AutoComplete> var1) {
      String var2 = this.getTextCharString();
      if (var2.startsWith("/") && var2.substring(1).equals(this.autocompleteQuery)) {
         this.generateCommandsFromList(var1, this.autocompleteQuery);
      }

   }

   public void fitScroll() {
      Rectangle var1 = this.getCaretBoundingBox();
      var1.x -= this.getTextX();
      if (this.getDrawOptions().getBoundingBox().width < this.width) {
         this.scroll = 0;
      } else {
         int var2 = var1.x;
         int var3 = var1.x + var1.width;
         if (var3 > this.width - 32) {
            this.scroll = Math.max(this.scroll, var3 - (this.width - 32));
         }

         if (var2 < this.scroll) {
            this.scroll = Math.max(0, Math.min(this.scroll, var2 - 16));
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.fontOptions.getSize()));
   }

   protected Rectangle getTextBoundingBox() {
      return new Rectangle(0, 0, this.width, this.fontOptions.getSize());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isTyping() && this.getTextLength() != 0 && this.autoCompleteList != null) {
         LinkedList var4 = new LinkedList();
         Rectangle var5 = new Rectangle();
         int var6 = this.getY();
         int var8;
         if (this.currentCommandUsage != null) {
            Rectangle var7 = new Rectangle(this.currentCommandUsage.getBoundingBox());
            var8 = var6 - var7.height;
            var6 = var8;
            var4.add(() -> {
               this.currentCommandUsage.draw(this.getX() - var7.x, var8 - var7.y, (Color)GameColor.GRAY.color.get());
            });
            var5.height += var7.height;
            var5.width = Math.max(var5.width, var7.width);
         }

         int var20 = 0;
         var8 = this.selectedAutocomplete >= 0 ? this.selectedAutocomplete % this.autoCompleteList.size() : -1;
         if (var8 > 5) {
            var20 = Math.max(0, Math.min(this.autoCompleteList.size() - 10, var8 - 5));
         }

         int var9 = Math.min(var20 + 10, this.autoCompleteList.size());

         for(int var10 = var20; var10 < var9; ++var10) {
            FairTypeDrawOptions var11 = (FairTypeDrawOptions)this.autoCompleteList.get(var10);
            Rectangle var12 = new Rectangle(var11.getBoundingBox());
            int var13 = var6 - var12.height;
            var6 = var13;
            boolean var14 = var8 >= 0 && var8 == var10;
            var4.add(() -> {
               var11.draw(this.getX() - var12.x, var13 - var12.y, var14 ? (Color)GameColor.YELLOW.color.get() : (Color)GameColor.GRAY.color.get());
            });
            var5.height += var12.height;
            var5.width = Math.max(var5.width, var12.width);
         }

         Screen.initQuadDraw(var5.width, var5.height).color(0.0F, 0.0F, 0.0F, 0.9F).draw(this.getX(), var6);
         var4.forEach(DrawOptions::draw);
      }

      Rectangle var18 = new Rectangle(-this.getX(), -this.getY(), Screen.getHudWidth(), Screen.getHudHeight());
      FormShader.FormShaderState var19 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), var18);

      try {
         super.draw(var1, var2, var3);
      } finally {
         var19.end();
      }

   }

   protected int getTextX() {
      return -this.scroll;
   }

   protected int getTextY() {
      return 0;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
