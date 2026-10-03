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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenMirror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MirrorCracks;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorDouble;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

//the Mirror Shade's unique artifact, spends cracks for a variety of effects
public class CrackedMirror extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_MIRROR;

		exp = 0;
		levelCap = 10;

		defaultAction = AC_REFLECT;

		unique = true;
		bones = false;
	}

	public static final String AC_REFLECT   = "REFLECT";
	public static final String AC_DOUBLE    = "DOUBLE";
	public static final String AC_SWAP      = "SWAP";
	public static final String AC_SHATTER   = "SHATTER";

	public static final int SHATTER_CRACKS  = 8;
	public static final float SHATTER_HP    = 0.25f;

	public int reflectCost(){
		//3 cracks, -1 every 3 levels, to a min of 1
		return Math.max(1, 3 - level()/3);
	}

	public int doubleCost(){
		//5 cracks, -1 every 5 levels
		return Math.max(3, 5 - level()/5);
	}

	public static int swapCost( Hero hero ){
		return Math.max(0, 3 - hero.pointsInTalent(Talent.SPECULAR_LINK));
	}

	private boolean usable( Hero hero ){
		return isEquipped(hero) && !cursed && hero.buff(MagicImmune.class) == null;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (usable(hero)) {
			actions.add(AC_REFLECT);
			if (hero.subClass == HeroSubClass.TWIN) {
				actions.add(AC_DOUBLE);
				if (hero.hasTalent(Talent.SPECULAR_LINK)) {
					actions.add(AC_SWAP);
				}
			}
			actions.add(AC_SHATTER);
		}
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_REFLECT)){
			return Messages.get(this, "ac_reflect", reflectCost());
		} else if (action.equals(AC_DOUBLE)){
			return Messages.get(this, "ac_double", doubleCost());
		} else if (action.equals(AC_SWAP)){
			return Messages.get(this, "ac_swap", swapCost(hero));
		}
		return super.actionName(action, hero);
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (!action.equals(AC_REFLECT) && !action.equals(AC_DOUBLE)
				&& !action.equals(AC_SWAP) && !action.equals(AC_SHATTER)){
			return;
		}

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			return;
		} else if (cursed) {
			GLog.i(Messages.get(this, "cursed"));
			return;
		}

		MirrorCracks cracks = Buff.affect(hero, MirrorCracks.class);

		switch (action) {
			case AC_REFLECT:
				if (hero.buff(Reflect.class) != null) {
					GLog.w(Messages.get(this, "already_reflecting"));
				} else if (spendCracks(hero, cracks, reflectCost())) {
					Buff.affect(hero, Reflect.class, Reflect.DURATION);
					Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
					hero.sprite.operate(hero.pos);
					hero.spendAndNext(1f);
				}
				break;

			case AC_DOUBLE:
				if (spendCracks(hero, cracks, doubleCost())) {
					if (MirrorDouble.spawn(hero, MirrorDouble.baseDuration(hero)) == null) {
						//refund if there was no room
						cracks.add(doubleCost());
						GLog.w(Messages.get(this, "no_space"));
					} else {
						Sample.INSTANCE.play(Assets.Sounds.READ);
						hero.sprite.operate(hero.pos);
						hero.spendAndNext(1f);
					}
				}
				break;

			case AC_SWAP:
				MirrorDouble twin = MirrorDouble.active();
				if (twin == null || !Dungeon.level.heroFOV[twin.pos]) {
					GLog.w(Messages.get(this, "no_double"));
				} else if (hero.rooted || hero.buff(SwapCooldown.class) != null) {
					GLog.w(Messages.get(this, "cant_swap"));
				} else if (spendCracks(hero, cracks, swapCost(hero))) {
					int oldPos = twin.pos;
					int newPos = hero.pos;
					twin.pos = newPos;
					hero.pos = oldPos;
					ScrollOfTeleportation.appear(twin, newPos);
					ScrollOfTeleportation.appear(hero, oldPos);
					Dungeon.observe();
					GameScene.updateFog();
					if (swapCost(hero) == 0) {
						Buff.affect(hero, SwapCooldown.class, 20f);
					}
					hero.spendAndNext(1f);
				}
				break;

			case AC_SHATTER:
				if (!cracks.canShatter(hero, SHATTER_CRACKS, SHATTER_HP)) {
					GLog.w(Messages.get(this, "cant_shatter", SHATTER_CRACKS, Math.round(SHATTER_HP*100)));
				} else {
					gainExp(cracks.count());
					BrokenMirror.trigger(hero, cracks);
					hero.spendAndNext(1f);
				}
				break;
		}
	}

	private boolean spendCracks( Hero hero, MirrorCracks cracks, int amount ){
		if (!cracks.use(amount)) {
			GLog.w(Messages.get(this, "not_enough", amount));
			return false;
		}
		gainExp(amount);
		return true;
	}

	private void gainExp( int cracksSpent ){
		exp += 10 * cracksSpent;
		while (level() < levelCap && exp >= 20 + 10*level()){
			exp -= 20 + 10*level();
			upgrade();
			Catalog.countUse(CrackedMirror.class);
			GLog.p(Messages.get(this, "levelup"));
		}
		updateQuickslot();
	}

	@Override
	public String status() {
		if (!isIdentified() || cursed || Dungeon.hero == null){
			return null;
		}
		return Integer.toString(MirrorCracks.count(Dungeon.hero));
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)){
			desc += "\n\n" + Messages.get(this, "desc_costs", reflectCost(), doubleCost(), SHATTER_CRACKS);
		}
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new MirrorBond();
	}

	public class MirrorBond extends ArtifactBuff {
	}

	//the next ranged attack against the hero is sent back to the attacker
	public static class Reflect extends FlavourBuff {

		public static final float DURATION = 20f;

		{
			type = buffType.POSITIVE;
		}

		private Char pending = null;

		public void arm( Char attacker ){
			pending = attacker;
		}

		public boolean isArmed(){
			return pending != null;
		}

		public void disarm(){
			pending = null;
		}

		public String reflect( Hero hero ){
			Char attacker = pending;
			pending = null;
			detach();
			if (attacker != null && attacker.isAlive()) {
				MirrorCracks.reflect(hero, attacker, attacker.damageRoll(), 0);
			}
			Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f, 1.3f);
			return Messages.get(CrackedMirror.class, "reflected");
		}

		@Override
		public int icon() {
			return BuffIndicator.LIGHT_SHIELD;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.7f, 0.5f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(visualcooldown()));
		}
	}

	public static class SwapCooldown extends FlavourBuff {
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.5f, 0.5f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, visualcooldown() / 20f);
		}
	}
}
