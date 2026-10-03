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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mirrorshade;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

//traps an enemy inside a mirror: it can neither act nor be harmed
public class GlassPrison extends ArmorAbility {

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return dst;
	}

	private static boolean canImprison( Char ch ){
		return ch != null
				&& ch.alignment == Char.Alignment.ENEMY
				&& !Char.hasProp(ch, Char.Property.BOSS)
				&& !Char.hasProp(ch, Char.Property.MINIBOSS)
				&& ch.buff(Imprisoned.class) == null;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);

		if (ch == null || !Dungeon.level.heroFOV[target]){
			GLog.w(Messages.get(this, "no_target"));
			return;
		} else if (ch.alignment != Char.Alignment.ENEMY){
			GLog.w(Messages.get(this, "ally_target"));
			return;
		} else if (!canImprison(ch)){
			GLog.w(Messages.get(this, "boss_target"));
			return;
		}

		float duration = 6 + 2*hero.pointsInTalent(Talent.PRISON_LENGTH);
		imprison(ch, duration);

		//cellmates: up to 1/2/3/4 enemies adjacent to the target, for half duration
		int extra = hero.pointsInTalent(Talent.PRISON_CELLMATES);
		for (int i : PathFinder.NEIGHBOURS8){
			if (extra <= 0) break;
			Char other = Actor.findChar(ch.pos + i);
			if (other != hero && canImprison(other)){
				imprison(other, duration/2f);
				extra--;
			}
		}

		Sample.INSTANCE.play(Assets.Sounds.SHATTER, 1f, 0.7f);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		hero.sprite.zap(target);
		hero.spendAndNext(1f);
	}

	private static void imprison( Char ch, float duration ){
		Buff.affect(ch, Imprisoned.class, duration).frailty = Dungeon.hero.pointsInTalent(Talent.PRISON_FRAILTY);
		if (ch.sprite != null) ch.sprite.burst(0xFFB79CFF, 6);
	}

	@Override
	public int icon() {
		return HeroIcon.GLASS_PRISON;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.PRISON_LENGTH, Talent.PRISON_FRAILTY, Talent.PRISON_CELLMATES, Talent.HEROIC_ENERGY};
	}

	public static class Imprisoned extends FlavourBuff {

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		public int frailty = 0;

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)){
				target.paralysed++;
				return true;
			}
			return false;
		}

		@Override
		public void detach() {
			super.detach();
			if (target.paralysed > 0) target.paralysed--;
			//3/6/9/12 turns of vulnerability once released
			if (frailty > 0 && target.isAlive()){
				Buff.prolong(target, Vulnerable.class, 3*frailty);
			}
		}

		@Override
		public void fx(boolean on) {
			if (on)                         target.sprite.add(CharSprite.State.PARALYSED);
			else if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
		}

		@Override
		public int icon() {
			return BuffIndicator.PARALYSIS;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.7f, 0.5f, 1f);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(visualcooldown()));
		}

		private static final String FRAILTY = "frailty";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(FRAILTY, frailty);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			frailty = bundle.getInt(FRAILTY);
		}
	}
}
