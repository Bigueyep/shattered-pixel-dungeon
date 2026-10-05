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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Loadout;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

//lets the player turn the arsenal mode on and pick its +10 weapon and its relic
public class WndLoadout extends Window {

	private static final int WIDTH		= 120;
	private static final int HEIGHT		= 150;
	private static final int BTN_HEIGHT	= 16;

	private final Callback onClose;

	private CheckBox enable;
	private ScrollingListPane list;

	private final ArrayList<ScrollingListPane.ListItem> weaponItems = new ArrayList<>();
	private final ArrayList<Class<?>> weaponClasses = new ArrayList<>();
	private final ArrayList<ScrollingListPane.ListItem> relicItems = new ArrayList<>();
	private final ArrayList<Class<?>> relicClasses = new ArrayList<>();

	public WndLoadout( Callback onClose ){
		super();
		this.onClose = onClose;

		resize(WIDTH, HEIGHT);

		enable = new CheckBox(Messages.get(HeroSelectScene.class, "loadout_enable")){
			@Override
			public void checked( boolean value ) {
				super.checked(value);
				SPDSettings.loadout(value);
			}
		};
		enable.checked(SPDSettings.loadout());
		enable.setRect(0, 0, WIDTH - BTN_HEIGHT, BTN_HEIGHT);
		add(enable);

		IconButton info = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().addToFront(new WndTitledMessage(
						Icons.get(Icons.INFO),
						Messages.get(HeroSelectScene.class, "loadout_title"),
						Messages.get(HeroSelectScene.class, "loadout_desc")));
			}
		};
		info.setRect(enable.right(), 0, BTN_HEIGHT, BTN_HEIGHT);
		add(info);

		list = new ScrollingListPane();
		add(list);

		list.addTitle(Messages.get(HeroSelectScene.class, "loadout_weapons", Loadout.WEAPON_LEVEL));
		for (Class<?> cls : Loadout.WEAPONS){
			weaponClasses.add(cls);
			weaponItems.add(addEntry(cls, true));
		}

		list.addTitle(Messages.get(HeroSelectScene.class, "loadout_relics"));
		for (Class<?>[] group : Loadout.relicLists()){
			for (Class<?> cls : group){
				relicClasses.add(cls);
				relicItems.add(addEntry(cls, false));
			}
		}

		updateSelection();
		list.setRect(0, BTN_HEIGHT + 2, WIDTH, HEIGHT - BTN_HEIGHT - 2);
	}

	private ScrollingListPane.ListItem addEntry( final Class<?> cls, final boolean weapon ){
		Item item = null;
		Image icon = null;
		String text = Messages.titleCase(Messages.get(cls, "name"));
		try {
			item = (Item) Reflection.newInstance(cls);
			icon = new ItemSprite(item);
		} catch (Exception e){
			//the entry just has no icon
		}

		if (item instanceof MeleeWeapon){
			text = Messages.get(HeroSelectScene.class, "loadout_weapon_entry",
					text, ((MeleeWeapon) item).STRReq(Loadout.WEAPON_LEVEL));
		} else if (item instanceof Ring || item instanceof Wand){
			text = Messages.get(HeroSelectScene.class, "loadout_relic_entry", text, Loadout.RELIC_LEVEL);
		}

		ScrollingListPane.ListItem entry = new ScrollingListPane.ListItem(icon, null, text){
			@Override
			public boolean onClick( float x, float y ){
				if (inside(x, y)){
					String name = cls.getName();
					if (weapon) SPDSettings.loadoutWeapon(name);
					else SPDSettings.loadoutRelic(name);
					//picking an item is a clear sign that the mode is wanted
					enable.checked(true);
					updateSelection();
					return true;
				}
				return false;
			}
		};
		list.addItem(entry);
		return entry;
	}

	//the chosen entries are highlighted
	private void updateSelection(){
		Class<?> weapon = Loadout.weapon();
		for (int i = 0; i < weaponItems.size(); i++){
			weaponItems.get(i).hardlight(weaponClasses.get(i) == weapon ? TITLE_COLOR : WHITE);
		}
		Class<?> relic = Loadout.relic();
		for (int i = 0; i < relicItems.size(); i++){
			relicItems.get(i).hardlight(relicClasses.get(i) == relic ? TITLE_COLOR : WHITE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (onClose != null) onClose.call();
	}
}
