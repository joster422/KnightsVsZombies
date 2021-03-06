import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

public class Block extends Rectangle {

	public int groundId;
	public int airId;
	public int randomId;
	
	public boolean hasTower = false;
	public Tower tower;
	
	public int targetWave = -1;
	public int targetEnemy = -1;
	public boolean shoting = false;
	public int shotFrame = 0, fire = 1250;
	
	public int animationId = 0;					// The index of the next sprite to get in an animation
	public int animationUpdatesPerFrame = 60;	// The number of screen updates it takes to update a the frame of an
												// 	animation
	public int animationUpdateIndex = 1;		// Keeps track of how many screen updates have passed since the last animation frame was drawn
	
	public static final int animationBase = 0;	// The index of the base animation frame
	public int animationIdleStart;				// The index of the first frame of the idle animation
	public int animationIdleEnd;				// The index of the last frame of the idle animation
	public int animationAttackStart;
	public int animationAttackEnd;
	
	public Ellipse2D.Float towerHitBox = new Ellipse2D.Float();
	
	public Block(int x, int y, int width, int height, int groundID, int airID) {
		setBounds(x, y, width, height);
		
		this.groundId = groundID;
		this.airId = airID;
		this.randomId = (int)(Math.random()*100);
	}
	
	public void addTower(int towerId) {
		this.hasTower = true;
		this.airId = towerId;
		this.tower = new Tower(towerId);
		towerHitBox = new Ellipse2D.Float(x - (width * tower.towerRange), y - (width * tower.towerRange), width + (width * (2 * tower.towerRange)), width + (width * (2 * tower.towerRange)));
	
		if (this.airId == 1){
			this.animationIdleStart = 1;
			this.animationIdleEnd = 4;
			this.animationAttackStart = 5;
			this.animationAttackEnd = 9;
		}
		else if (this.airId == 2){
			this.animationIdleStart = 1;
			this.animationIdleEnd = 4;
			this.animationAttackStart = 5;
			this.animationAttackEnd = 8;
		}
		else{
			this.animationIdleStart = 0;
			this.animationIdleEnd = 0;
			this.animationAttackStart = 0;
			this.animationAttackEnd = 0;
		}

	}
	
	public void physic() {
		if(airId > Value.airCastle) {
			outerLoop:
			for (int i = 0; i < Screen.levelEnemyList.size(); i++) {
				for (int j = 0; j < Screen.levelEnemyList.get(i).length; j++) {
					if(Screen.levelEnemyList.get(i)[j].inGame) {
						if(towerHitBox.intersects(Screen.levelEnemyList.get(i)[j])) {
							this.targetWave = i;
							this.targetEnemy = j;
							this.shoting = true;
							break outerLoop;
						}
						else {
							shoting  = false;
						}
					}
				}
			}
			if(shoting) {
				if(shotFrame >= (fire / tower.towerRate)) {
					Screen.levelEnemyList.get(targetWave)[targetEnemy].looseEnemyHealth(tower.towerDamage);
					shoting = false;
					shotFrame = 0;
				}
				else {
					shotFrame += 1;
				}
			} 
		}
	}
	
	public void draw(Graphics g) {
		if(groundId == Value.groundOpen) {
			if(this.randomId < 40) {
				if(Frame.gameScreen.level <= 5)
				g.drawImage(ScreenPanel.sprites.getSprite("block", (Frame.gameScreen.level - 1), 1), x, y, width, height, null);
				else
				g.drawImage(ScreenPanel.sprites.getSprite("block", 3, 1), x, y, width, height, null);
			}
			else if(this.randomId < 70) {
				if(Frame.gameScreen.level <= 5)
				g.drawImage(ScreenPanel.sprites.getSprite("block", (Frame.gameScreen.level - 1), 2), x, y, width, height, null);
				else
				g.drawImage(ScreenPanel.sprites.getSprite("block", 3, 2), x, y, width, height, null);
			}
			else if(this.randomId < 90) {
				if(Frame.gameScreen.level <= 5)
				g.drawImage(ScreenPanel.sprites.getSprite("block", (Frame.gameScreen.level - 1), 3), x, y, width, height, null);
				else
				g.drawImage(ScreenPanel.sprites.getSprite("block", 3, 3), x, y, width, height, null);
			}
			else if(this.randomId < 100) {
				if(Frame.gameScreen.level <= 5)
				g.drawImage(ScreenPanel.sprites.getSprite("block", (Frame.gameScreen.level - 1), 4), x, y, width, height, null);
				else
				g.drawImage(ScreenPanel.sprites.getSprite("block", 3, 4), x, y, width, height, null);
			}
		}
		else if(groundId == Value.pathOpen) {
			if(Frame.gameScreen.level <= 5)
			g.drawImage(ScreenPanel.sprites.getSprite("block", (Frame.gameScreen.level - 1), 0), x, y, width, height, null);
			else
			g.drawImage(ScreenPanel.sprites.getSprite("block", 3, 0), x, y, width, height, null);
		}
		
		if(airId == Value.airCastle) {
			g.drawImage(new ImageIcon("res/Graphics/towr_base.png").getImage(),x-32, y-64, width+64, height+64, null);
			//g.setColor(Color.CYAN);
			//g.fillRect(x, y, width, height);
		}
		else if(airId > Value.airCastle) {
			
			// If a tower is shooting at an enemy to its right,
			if (shoting && Screen.levelEnemyList.get(targetWave)[targetEnemy].x > (x))
				g.drawImage(ScreenPanel.sprites.getSprite("tower", (airId - 1), this.animationId), x, y, x+64, y+64, 64, 0, 0, 64, null);
				
			else
				g.drawImage(ScreenPanel.sprites.getSprite("tower", (airId - 1), this.animationId) ,x, y, width, height, null);
		}
		
		if(this.animationUpdateIndex == this.animationUpdatesPerFrame){
			this.animationId = nextAnimationFrame();
			this.animationUpdateIndex = 1;
		}
		
		this.animationUpdateIndex++;
	}
	
	public void fight(Graphics g) {
		g.setColor(Color.WHITE);
		if(this.contains(Opening.mse) && this.hasTower) {	
			//g.drawRect(towerHitBox.x, towerHitBox.y, towerHitBox.width, towerHitBox.height);
			g.drawOval((int)towerHitBox.x, (int)towerHitBox.y, (int)towerHitBox.width, (int)towerHitBox.height);
		}

		else if(this.contains(Opening.mse) && ScreenPanel.holdItem){
			Ellipse2D.Float tempRange = new Ellipse2D.Float(x - (width * Value.getTowerStats("range", ScreenPanel.holdItemId)), y - (width * Value.getTowerStats("range", ScreenPanel.holdItemId)), width + (width * (2 * Value.getTowerStats("range", ScreenPanel.holdItemId))), width + (width * (2 * Value.getTowerStats("range", ScreenPanel.holdItemId))));
			g.drawOval((int)tempRange.x, (int)tempRange.y, (int)tempRange.width, (int)tempRange.height);
		}
		
		if(shoting) {
			//g.drawLine(x + (width / 2), y + (height / 2), Screen.levelEnemyList.get(targetWave)[targetEnemy].x + (width / 2), Screen.levelEnemyList.get(targetWave)[targetEnemy].y + (height / 2) );
			
			// If shooting and not already in the attack animation, start the attack animation
			if(!(this.animationAttackStart <= this.animationId) && (this.animationId < this.animationAttackEnd))
				this.animationId = this.animationAttackStart;
		}
	}
	
	public int nextAnimationFrame() {
		
		// If idle, start the idle animation
		if (this.animationId == animationBase)
			return this.animationIdleStart;
		
		// Go through each frame of the idle animation
		else if(this.animationIdleStart <= this.animationId && this.animationId < this.animationIdleEnd)  
			return (this.animationId + 1);
	
		// Repeat the idle animation
		else if (this.animationId == this.animationIdleEnd)
			return this.animationIdleStart;
		
		// Go through each frame of the attack animation
		else if(this.animationAttackStart <= this.animationId && this.animationId < this.animationAttackEnd){ 
			if(this.animationAttackStart == this.animationId)
				this.animationUpdatesPerFrame = 30;
			return (this.animationId + 1);
		}
	
		// If finished attacking, return to the idle animation
		else if (this.animationId == this.animationAttackEnd){
			this.animationUpdatesPerFrame = 60;
			return this.animationIdleStart;
		}
		
		// If all else fails, return the base sprite
		return 0;
	}
}
