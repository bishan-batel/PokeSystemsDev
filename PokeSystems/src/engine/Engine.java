/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import gamestates.*;
import gamestates.gameplay.GameplayState;
import gamestates.menuscreen.MenuScreenState;

public class Engine implements Runnable {

   protected GameState[] gameStates;
   private byte gameState = 0; // Initial Game State
   protected Window window;
   protected Thread thread;
   protected boolean[] keysPressed = new boolean[256];
   protected int currentKeyPressed = 0;
   private Console console;

   public Engine() {
      final float scaleFactor = 0.7f;
      this.window = new Window((int) (1680 * scaleFactor), (int) (1050 * scaleFactor));
      this.window.setResizable(false);
      gameStates = new GameState[] { new MenuScreenState(this), new GameplayState(this), new CreditState(this) };
      this.beginThread();
   }

   // Thread
   private synchronized void beginThread() {
      this.thread = new Thread(this, "Main");
      this.thread.start();
   }

   @Override
   public synchronized void run() {
      final double ticksPerSecond = 60; // Tickrate
      final double nanoSeconds = 1E9 / ticksPerSecond;
      long prevTime = System.nanoTime();
      double delta = 0;
      long timer = System.currentTimeMillis();
      int frames = 0;

      // Initilization of Gamestates and Listener
      this.window.getCanvas().addKeyListener(new DefaultKeyListener(this));
      this.gameStates[this.gameState].setup();
      this.console = new Console(this);
      while (this.window.isVisible()) {
         long now = System.nanoTime();
         delta += (now - prevTime) / nanoSeconds;
         prevTime = now;
         while (delta >= 1) {
            this.gameStates[this.gameState].update(delta);
            delta--;
            this.gameStates[this.gameState].render();
            // POST
         }

         frames++;
         if (System.currentTimeMillis() - timer > 1000) {
            timer += 1000;
            // Displays framerate and then resets counter
            this.window.setTitle("Pokesystems | " + frames);
            frames = 0;
         }
      }
      this.stop();
   }

   public synchronized void stop() {
      try {
         this.thread.join();
      } catch (InterruptedException e) {
         System.out.println("Issue occured while joining thread");
      }
   }

   // Key Input
   public void resetKeyStates() {
      this.keysPressed = new boolean[this.keysPressed.length];
   }

   public void setKeyState(int keyCode, boolean state) {
      if (keyCode >= this.keysPressed.length) {
         return;
      }
      this.keysPressed[keyCode] = state;
   }

   // Getters and Setters
   public void setGameState(byte state) {
      try {
         this.gameStates[this.gameState].onClose();
         this.gameState = state;
         this.gameStates[this.gameState].setup();
      } catch (ArrayIndexOutOfBoundsException be) {
         System.out.println("Failed to set game state");
      }
   }

   public byte getGameState() {
      return this.gameState;
   }

   public Window getWindow() {
      return this.window;
   }

   public static void main(String[] args) {
      new Engine();
   }

   // Console
   public Console getConsole() {
      return this.console;
   }

   // Key Input
   public void setCurrentKeyPressed(int keyCode) {
      this.currentKeyPressed = keyCode;
   }

   public int getCurrentKeyPressed() {
      return this.currentKeyPressed;
   }

   public boolean isCurrentKey(int code) {
      return this.currentKeyPressed == code;
   }

   public boolean isCurrentKey(Config.KeyBind bind) {
      return this.isCurrentKey(bind.getKeyCode());
   }

   public boolean isKeyPressed(int i) {
      return this.keysPressed[i];
   }

   public boolean isKeyPressed(Config.KeyBind bind) {
      return this.isKeyPressed(bind.getKeyCode());
   }

   public boolean isCurrentKeyPressed(int code) {
      return this.isKeyPressed(code) && this.isCurrentKey(code);
   }

   public boolean isCurrentKeyPressed(Config.KeyBind bind) {
      return this.isCurrentKeyPressed(bind.getKeyCode());
   }
}
