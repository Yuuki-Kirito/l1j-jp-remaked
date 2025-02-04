/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;

public class S_NoSell extends ServerBasePacket {
	private static final String _S__25_NoSell = "[S] _S__25_NoSell";

	private byte[] _byte = null;

	public S_NoSell(L1NpcInstance npc) {
		buildPacket(npc);
	}

	private void buildPacket(L1NpcInstance npc) {
		writeC(Opcodes.S_HYPERTEXT);
		writeD(npc.getId());
		writeS("nosell");
		if (npc.getNpcTemplate().get_nameid().startsWith("$")) {
			writeC(0x00);
			writeH(0x01);
			writeS(npc.getNpcTemplate().get_nameid());
		} else if (npc.getNpcId() == 100437) {// ����
			writeC(0x00);
			writeH(0x01);
			writeS("$7543");
		} else if (npc.getNpcId() == 70027) {// ���
			writeC(0x00);
			writeH(0x01);
			writeS("$859");
		} else {
			writeC(0x00);
			writeC(1);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return _S__25_NoSell;
	}
}
