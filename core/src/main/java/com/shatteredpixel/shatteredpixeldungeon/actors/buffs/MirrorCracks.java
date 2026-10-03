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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorDouble;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

//the Mirror Shade's core resource. Cracks are gained by taking hits and dodging,
// and are spent by the cracked mirror and by the broken mirror rage.
public class MirrorCracks extends Buff {

	public static final int MAX = 10;

	{
		type = buffType.POSITIVE;
		revivePersists = true;
	}

	private int cracks = 0;
	private int idleTurns = 0;
	private float lastDodge = -1;
	private boolean lastShardUsed = false;

	public int count(){
		return cracks;
	}

	public static void gain( Char ch, int amount ){
		MirrorCracks buff = ch.buff(MirrorCracks.class);
		if (buff != null) buff.add(amount);
	}

	public static int count( Char ch ){
		MirrorCracks buff = ch.buff(MirrorCracks.class);
		return buff == null ? 0 : buff.count();
	}

	public void add( int amount ){
		if (amount <= 0) return;
		cracks = Math.min(MAX, cracks + amount);
		idleTurns = 0;
		BuffIndicator.refreshHero();
		Item.updateQuickslot();
	}

	public boolean use( int amount ){
		if (amount < 0 || cracks < amount) return false;
		cracks -= amount;
		BuffIndicator.refreshHero();
		Item.updateQuickslot();
		return true;
	}

	//only one crack can be gained from dodging per turn
	public void onDodge(){
		if (lastDodge != Actor.now()){
			lastDodge = Actor.now();
			add(1);
		}
	}

	@Override
	public boolean act() {
		Hero hero = (Hero) target;
		if (hero.visibleEnemies() > 0 || hero.buff(BrokenMirror.class) != null){
			idleTurns = 0;
		} else {
			idleTurns++;
			if (cracks > 0) {
				if (hero.hasTalent(Talent.ABSORPTION) && hero.HP < hero.HT && idleTurns % 3 == 0) {
					//converts 1 crack into 1/2 HP every 3 turns outside of combat
					int heal = Math.min(hero.pointsInTalent(Talent.ABSORPTION), hero.HT - hero.HP);
					hero.HP += heal;
					hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
					use(1);
				} else if (idleTurns % 10 == 0) {
					use(1);
				}
			}
		}
		spend( TICK );
		return true;
	}

	//*** Broken Mirror ***

	public static float shatterThreshold( Hero hero ){
		//15% base, +5% per point in fracture point
		return 0.15f + 0.05f*hero.pointsInTalent(Talent.FRACTURE_POINT);
	}

	public boolean canShatter( Hero hero, int minCracks, float hpThreshold ){
		return hero.isAlive()
				&& hero.HP < hero.HT * hpThreshold
				&& cracks >= minCracks
				&& hero.buff(BrokenMirror.class) == null
				&& hero.buff(BrokenMirror.Cooldown.class) == null;
	}

	public void checkShatter( Hero hero ){
		if (canShatter(hero, 5, shatterThreshold(hero))){
			BrokenMirror.trigger(hero, this);
		}
	}

	//*** Reflected damage, used by the reflector and broken mirror ***

	public static class Reflection {}

	public static void reflect( Hero hero, Char enemy, int amount, int bleed ){
		if (amount <= 0 || enemy == null) return;
		//deferred so that we don't kill the attacker in the middle of its own attack
		Actor.add(new Actor() {
			{
				actPriority = VFX_PRIO;
			}

			@Override
			protected boolean act() {
				Actor.remove(this);
				if (enemy.isAlive()) {
					enemy.damage(amount, new Reflection());
					if (enemy.sprite != null) enemy.sprite.burst(0xFFB79CFF, 4);
					if (bleed > 0 && enemy.isAlive()) {
						Buff.affect(enemy, Bleeding.class).set(bleed);
					}
				}
				return true;
			}
		});
	}

	//handles both the reflector subclass and broken mirror reflection, returns nothing as
	// incoming damage is not reduced, it is mirrored back
	public static void onMeleeHitTaken( Hero hero, Char enemy, int damage ){
		if (damage <= 0 || enemy == null || enemy == hero
				|| !Dungeon.level.adjacent(hero.pos, enemy.pos)) return;

		int reflected = 0;
		int bleed = 0;
		if (hero.subClass == HeroSubClass.REFLECTOR){
			int polish = hero.pointsInTalent(Talent.POLISHED_SURFACE);
			//25% of damage, up to 10. +5% and +5 cap per point in polished surface
			reflected += Math.min(10 + 5*polish, Math.round(damage * (0.25f + 0.05f*polish)));
			if (reflected > 0 && hero.hasTalent(Talent.SHARP_SHARDS)){
				bleed = 1 + hero.pointsInTalent(Talent.SHARP_SHARDS);
			}
		}
		if (hero.buff(BrokenMirror.class) != null){
			reflected += Math.round(damage * BrokenMirror.REFLECT);
		}
		reflect(hero, enemy, reflected, bleed);
	}

	//*** Last Shard, the Mirror Shade's endgame survival passive ***

	public boolean tryLastShard( Hero hero ){
		if (lastShardUsed || hero.belongings.getItem(Amulet.class) == null){
			return false;
		}
		lastShardUsed = true;

		hero.HP = Math.max(1, Math.round(hero.HT * 0.3f));
		cracks = 0;
		Buff.detach(hero, BrokenMirror.class);

		MirrorDouble.spawn(hero, 5);

		hero.sprite.burst(0xFFB79CFF, 20);
		GameScene.flash(0x806040FF);
		Sample.INSTANCE.play(Assets.Sounds.SHATTER);
		GLog.w(Messages.get(this, "last_shard"));
		BuffIndicator.refreshHero();
		Item.updateQuickslot();
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.SHADOWS;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.7f, 0.5f, 1f);
	}

	@Override
	public float iconFadePercent() {
		return 1f - cracks / (float)MAX;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(cracks);
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc", cracks, MAX);
		if (!lastShardUsed && target instanceof Hero && ((Hero) target).belongings.getItem(Amulet.class) != null){
			desc += "\n\n" + Messages.get(this, "last_shard_ready");
		}
		return desc;
	}

	private static final String CRACKS      = "cracks";
	private static final String IDLE_TURNS  = "idle_turns";
	private static final String LAST_SHARD  = "last_shard";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CRACKS, cracks);
		bundle.put(IDLE_TURNS, idleTurns);
		bundle.put(LAST_SHARD, lastShardUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cracks = bundle.getInt(CRACKS);
		idleTurns = bundle.getInt(IDLE_TURNS);
		lastShardUsed = bundle.getBoolean(LAST_SHARD);
	}
}
