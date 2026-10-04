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

package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.files.FileHandle;
import com.watabou.utils.FileUtils;

import java.io.IOException;

//optional checkpoint mode: a full copy of the save is taken when arriving on the floor after each boss,
// and is restored over the save when the hero dies, so the run continues from there
public class Checkpoint {

	private static final String FOLDER = "checkpoint";

	//set when a checkpoint was just taken, so the game scene can tell the player
	public static boolean justSaved = false;

	public static String folder( int slot ){
		return GamesInProgress.gameFolder(slot) + "/" + FOLDER;
	}

	//the floor directly after a boss floor: 6, 11, 16, 21 and 26
	public static boolean isCheckpointFloor( int depth, int branch ){
		return branch == 0 && depth > 1 && (depth - 1) % 5 == 0;
	}

	public static boolean enabled(){
		return Dungeon.checkpoints && !Dungeon.daily;
	}

	public static boolean exists( int slot ){
		return FileUtils.fileLength(folder(slot) + "/" + gameFileName(slot)) > 1;
	}

	//called after the hero arrives on a level from the interlevel scene's loading thread
	public static void onArrival() throws IOException {
		if (!enabled()
				|| !isCheckpointFloor(Dungeon.depth, Dungeon.branch)
				|| Dungeon.depth <= Dungeon.checkpointDepth){
			return;
		}
		Dungeon.checkpointDepth = Dungeon.depth;
		Dungeon.saveAll();
		save(GamesInProgress.curSlot);
		justSaved = true;
	}

	private static String gameFileName( int slot ){
		return FileUtils.getFileHandle(GamesInProgress.gameFile(slot)).name();
	}

	public static void save( int slot ){
		clear(slot);
		String src = GamesInProgress.gameFolder(slot);
		FileHandle dest = FileUtils.getFileHandle(folder(slot));
		dest.mkdirs();
		for (String name : FileUtils.filesInDir(src)){
			FileHandle file = FileUtils.getFileHandle(src + "/" + name);
			if (!file.isDirectory() && name.endsWith(".dat")){
				file.copyTo(FileUtils.getFileHandle(folder(slot) + "/" + name));
			}
		}
	}

	//replaces the slot's save with the checkpoint, returns false if there is no checkpoint
	public static boolean restore( int slot ){
		if (!exists(slot)) return false;

		String dir = GamesInProgress.gameFolder(slot);
		for (String name : FileUtils.filesInDir(dir)){
			FileHandle file = FileUtils.getFileHandle(dir + "/" + name);
			if (!file.isDirectory()){
				file.delete();
			}
		}
		for (String name : FileUtils.filesInDir(folder(slot))){
			FileHandle file = FileUtils.getFileHandle(folder(slot) + "/" + name);
			if (!file.isDirectory()){
				file.copyTo(FileUtils.getFileHandle(dir + "/" + name));
			}
		}
		GamesInProgress.setUnknown(slot);
		return true;
	}

	public static void clear( int slot ){
		FileUtils.deleteDir(folder(slot));
	}
}
