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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

//the Mirror Shade's temporary rage, triggered at low HP when enough cracks are stored
public class BrokenMirror extends FlavourBuff {

	public static final float DMG_BOOST     = 1.4f;
	public static final float LIFESTEAL     = 0.15f;
	public static final float REFLECT       = 0.2f;
	public static final float EVASION_MULTI = 0.5f;
	public static final float HEALING_MULTI = 0.5f;
	public static final float ATTACK_DELAY  = 0.75f;

	public static final float MAX_DURATION  = 10f;
	public static final float BASE_COOLDOWN = 150f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public static void trigger( Hero hero, MirrorCracks cracks ){
		int consumed = cracks.count();
		cracks.use(consumed);

		//6 turns, +1 per 2 cracks consumed, to a max of 10
		float duration = Math.min(MAX_DURATION, 6 + consumed/2);
		Buff.prolong(hero, BrokenMirror.class, duration);

		hero.sprite.burst(0xFFB79CFF, 12);
		GameScene.flash(0x804020C0);
		Sample.INSTANCE.play(Assets.Sounds.SHATTER);
		GLog.n(Messages.get(BrokenMirror.class, "trigger"));
	}

	public static int lifesteal( Hero hero, int damage ){
		int heal = Math.min(hero.HT - hero.HP, Math.round(damage * LIFESTEAL));
		if (heal > 0){
			hero.HP += heal;
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
		}
		return heal;
	}

	@Override
	public void detach() {
		super.detach();
		Char ch = target;
		if (ch != null && ch.isAlive()) {
			Buff.affect(ch, Weakness.class, 10f);
			Hunger hunger = ch.buff(Hunger.class);
			if (hunger != null) hunger.affectHunger(-50);
			float cooldown = BASE_COOLDOWN;
			if (ch instanceof Hero){
				cooldown -= 25*((Hero) ch).pointsInTalent(Talent.SHATTERPROOF);
			}
			Buff.affect(ch, Cooldown.class, cooldown);
		}
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.aura(0x8B5CF6, 5);
		else    target.sprite.clearAura();
	}

	@Override
	public int icon() {
		return BuffIndicator.RAGE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.7f, 0.4f, 1f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (MAX_DURATION - visualcooldown()) / MAX_DURATION);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(visualcooldown()));
	}

	public static class Cooldown extends FlavourBuff {

		{
			type = buffType.NEUTRAL;
		}

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.6f, 0.4f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, visualcooldown() / BASE_COOLDOWN);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(visualcooldown()));
		}
	}
}
