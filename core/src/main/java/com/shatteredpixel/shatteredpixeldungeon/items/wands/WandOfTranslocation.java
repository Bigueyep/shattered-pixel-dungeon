/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

//a staff that teleports the user to a visible spot inside the room they are standing in
public class WandOfTranslocation extends Wand {

	{
		image = ItemSpriteSheet.WAND_TRANSLOCATION;

		//the bolt flies over characters, it only stops at walls or at the chosen cell
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	//outside of regular rooms (boss arenas, caves, etc.) the range is limited instead
	public static int range( int lvl ){
		return 6 + 2*lvl;
	}

	//returns the closest valid cell to the target along the line of fire, or -1 if there is none
	public static int destination( Char user, int target, int lvl ){
		Ballistica path = new Ballistica(user.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);

		Room room = null;
		if (Dungeon.level instanceof RegularLevel){
			room = ((RegularLevel) Dungeon.level).room(user.pos);
		}

		int best = -1;
		for (int i = 1; i <= path.dist && i < path.path.size(); i++){
			int cell = path.path.get(i);
			if (room != null) {
				if (!room.inside(Dungeon.level.cellToPoint(cell))) break;
			} else if (Dungeon.level.distance(user.pos, cell) > range(lvl)) {
				break;
			}
			if (Dungeon.level.passable[cell]
					&& !Dungeon.level.avoid[cell]
					&& Dungeon.level.heroFOV[cell]
					&& Actor.findChar(cell) == null){
				best = cell;
			}
		}
		return best;
	}

	@Override
	public boolean tryToZap( Hero owner, int target ){
		//no charge is used if there is nowhere to land
		if (!cursed && destination(owner, target, buffedLvl()) == -1){
			GLog.w( Messages.get(this, "no_destination") );
			return false;
		}
		return super.tryToZap(owner, target);
	}

	@Override
	public void onZap(Ballistica bolt) {
		int dest = destination(curUser, bolt.collisionPos, buffedLvl());
		if (dest == -1){
			GLog.w( Messages.get(this, "no_destination") );
			return;
		}

		ScrollOfTeleportation.appear(curUser, dest);
		Dungeon.level.occupyCell(curUser);
		Dungeon.observe();
		GameScene.updateFog();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//the battlemage's footing becomes slippery: 1 turn of haste, +1 turn per 3 levels
		int level = Math.max( 0, staff.buffedLvl() );
		if (Random.Int(3) == 0) {
			Buff.prolong(attacker, Haste.class, 1f + level/3);
		}
	}

	@Override
	public String statsDesc() {
		if (levelKnown){
			return Messages.get(this, "stats_desc", range(buffedLvl()));
		} else {
			return Messages.get(this, "stats_desc", range(0));
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x9B6BFF );
		particle.am = 0.6f;
		particle.setLifespan(0.8f);
		particle.speed.polar( Random.Float(PointF.PI2), 3f );
		particle.setSize( 0.5f, 2f );
		particle.shuffleXY(1f);
	}
}
