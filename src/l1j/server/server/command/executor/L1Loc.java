/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Loc implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Loc.class.getName());

	private L1Loc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Loc();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int locx = pc.getX();
			int locy = pc.getY();
			short mapid = pc.getMapId();
			int gab = L1WorldMap.getInstance().getMap(mapid)
					.getOriginalTile(locx, locy);

			String msg = String.format(
					"��ġ : X - %d, Y - %d, MAP - %d / Tile - %d", locx, locy,
					mapid, gab);
			pc.sendPackets(new S_SystemMessage(msg), true);
			// System.out.println("����55��.add(new Robot_Location_bean("+locx+", "+locy+", 155));");
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
