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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

//a sturdier, temporary mirror image created by the Mirror Shade's twin subclass
public class MirrorDouble extends MirrorImage {

	{
		defenseSkill = 1;
	}

	public static MirrorDouble active(){
		for (Char ch : Actor.chars()){
			if (ch instanceof MirrorDouble && ch.isAlive()){
				return (MirrorDouble) ch;
			}
		}
		return null;
	}

	//returns the spawned double, or null if there was no room
	public static MirrorDouble spawn( Hero hero, int duration ){
		ArrayList<Integer> cells = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8) {
			int p = hero.pos + i;
			if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
				cells.add(p);
			}
		}
		if (cells.isEmpty()) return null;

		MirrorDouble old = active();
		if (old != null){
			old.die(null);
		}

		MirrorDouble twin = new MirrorDouble();
		twin.duplicate(hero);
		Buff.detach(twin, MirrorInvis.class);
		twin.HP = twin.HT = Math.max(1, Math.round(hero.HT * (0.3f + 0.1f*hero.pointsInTalent(Talent.TWIN_RESILIENCE))));
		GameScene.add(twin);
		//lifetime is tracked in game time, so fast attacks don't make the double expire sooner
		Buff.affect(twin, Lifetime.class, duration);
		ScrollOfTeleportation.appear(twin, Random.element(cells));
		return twin;
	}

	public static int baseDuration( Hero hero ){
		return 15 + 5*hero.pointsInTalent(Talent.TWIN_RESILIENCE);
	}

	@Override
	public int damageRoll() {
		Hero hero = Dungeon.hero;
		int damage;
		if (hero.belongings.weapon() != null){
			damage = hero.belongings.weapon().damageRoll(this);
		} else {
			damage = hero.damageRoll();
		}
		//50% of hero damage, +10% per point in shared echo
		return Math.round(damage * (0.5f + 0.1f*hero.pointsInTalent(Talent.SHARED_ECHO)));
	}

	@Override
	public int defenseSkill(Char enemy) {
		//the double doesn't fade on hit, so it uses the hero's full evasion
		return Dungeon.hero != null ? 4 + Dungeon.hero.lvl : 0;
	}

	@Override
	public String description() {
		Lifetime lifetime = buff(Lifetime.class);
		return Messages.get(this, "desc", lifetime != null ? (int)Math.ceil(lifetime.visualcooldown()) : 0);
	}

	public static class Lifetime extends FlavourBuff {

		@Override
		public void detach() {
			super.detach();
			if (target != null && target.isAlive()){
				target.die(null);
			}
		}
	}
}
