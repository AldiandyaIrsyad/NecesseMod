package necesse.gfx.forms.components.chat;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormChatLog extends FormContentBox implements ChatMessageListener {
   public final ChatMessageList chat;
   private boolean active;
   private LinkedList<ChatMessageComponent> log = new LinkedList();
   private int nextY = 0;

   public FormChatLog(int var1, int var2, int var3, int var4, ChatMessageList var5) {
      super(var1, var2, var3, var4);
      this.chat = var5;
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         ChatMessage var7 = (ChatMessage)var6.next();
         this.addMessage(var7);
      }

      this.drawVerticalOnLeft = true;
      this.shouldLimitDrawArea = true;
      this.hitboxFullSize = false;
   }

   protected void init() {
      super.init();
      this.chat.addListener(this);
   }

   public void onNewMessage(ChatMessage var1) {
      this.addMessage(var1);
   }

   public void onRemoveMessage(ChatMessage var1) {
      this.removeMessage(var1);
   }

   protected boolean hasScrollbarX() {
      return false;
   }

   protected boolean hasScrollbarY() {
      return this.active && super.hasScrollbarY();
   }

   public void addMessage(ChatMessage var1) {
      ChatMessageComponent var2 = (ChatMessageComponent)this.addComponent(new ChatMessageComponent(0, this.nextY, var1));
      this.log.addLast(var2);
      if (this.log.size() > 250) {
         this.removeComponent((FormComponent)this.log.removeFirst());
      }

      this.nextY += var2.getBoundingBox().height;
      Rectangle var3 = this.getContentBoxToFitComponents();
      var3.x = 0;
      var3.width = this.getWidth();
      if (var3.height < this.getHeight()) {
         var3.y = var3.height - this.getHeight();
         var3.height = this.getHeight();
      }

      this.setContentBox(var3);
      this.scrollToFitForced(var2.getBoundingBox());
   }

   public void removeMessage(ChatMessage var1) {
      ListIterator var2 = this.log.listIterator();
      int var3 = -1;

      while(var2.hasNext()) {
         ChatMessageComponent var4 = (ChatMessageComponent)var2.next();
         if (var3 != -1) {
            var4.setPosition(var4.getX(), var4.getY() - var3);
         } else if (var4.message == var1) {
            this.removeComponent(var4);
            var2.remove();
            var3 = var4.getBoundingBox().height;
            this.nextY -= var3;
         }
      }

      if (var3 != -1) {
         Rectangle var5 = this.getContentBoxToFitComponents();
         var5.x = 0;
         var5.width = this.getWidth();
         if (var5.height < this.getHeight()) {
            var5.y = var5.height - this.getHeight();
            var5.height = this.getHeight();
         }

         this.setContentBox(var5);
      }

   }

   public void refreshBoundingBoxes() {
      this.nextY = 0;

      ChatMessageComponent var2;
      for(Iterator var1 = this.log.iterator(); var1.hasNext(); this.nextY += var2.getBoundingBox().height) {
         var2 = (ChatMessageComponent)var1.next();
         var2.setPosition(0, this.nextY);
      }

      Rectangle var3 = this.getContentBoxToFitComponents();
      var3.x = 0;
      var3.width = this.getWidth();
      if (var3.height < this.getHeight()) {
         var3.y = var3.height - this.getHeight();
         var3.height = this.getHeight();
      }

      this.setContentBox(var3);
      if (!this.log.isEmpty()) {
         this.scrollToFitForced(((ChatMessageComponent)this.log.getLast()).getBoundingBox());
      } else {
         this.scrollY(0);
      }

   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public boolean isMouseOver(InputEvent var1) {
      return false;
   }

   public void dispose() {
      super.dispose();
      this.chat.removeListener(this);
   }

   protected class ChatMessageComponent extends FormComponent implements FormPositionContainer {
      private FormPosition position;
      public final ChatMessage message;

      public ChatMessageComponent(int var2, int var3, ChatMessage var4) {
         this.position = new FormFixedPosition(var2, var3);
         this.message = var4;
      }

      public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
         this.message.drawOptions.get().handleInputEvent(this.getX(), this.getY(), var1);
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      }

      public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         this.message.drawOptions.get().draw(this.getX(), this.getY());
      }

      public boolean shouldDraw() {
         return FormChatLog.this.active || this.message.shouldDraw();
      }

      public List<Rectangle> getHitboxes() {
         return singleBox(this.message.drawOptions.get().getBoundingBox(this.getX(), this.getY()));
      }

      public FormPosition getPosition() {
         return this.position;
      }

      public void setPosition(FormPosition var1) {
         this.position = var1;
      }
   }
}
