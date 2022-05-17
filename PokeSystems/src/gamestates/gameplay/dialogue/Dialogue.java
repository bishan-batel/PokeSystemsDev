/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.dialogue;

import engine.Window;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public abstract class Dialogue
{
    public boolean dispose = false;
    public abstract void render(Graphics g);
    public abstract void keyPressed(KeyEvent e);
}
