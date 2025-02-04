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

package l1j.server.server.model.item.function;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class Elixir extends L1ItemInstance {

	public Elixir(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		try {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
				int itemId = useItem.getItemId();
				int item_minlvl = ((L1EtcItem) useItem.getItem()).getMinLevel();
				int item_maxlvl = ((L1EtcItem) useItem.getItem()).getMaxLevel();
				int ElixirLev = ((pc.getLevel() - 45) / 5) - pc.getAbility().getElixirCount();

				if (item_minlvl != 0 && item_minlvl > pc.getLevel() && !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(318, String.valueOf(item_minlvl)), true);
					// 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
					return;
				} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(673, String.valueOf(item_maxlvl)), true);
					// 이 아이템은%d레벨 이상만 사용할 수 있습니다.
					return;
				}
				switch (itemId) {
				case 40033:
					if (pc.getAbility().getStr() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {

						pc.getAbility().addStr((byte) 1);
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				case 40034:
					if (pc.getAbility().getCon() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {
						pc.getAbility().addCon((byte) 1);
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				case 40035:
					if (pc.getAbility().getDex() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {
						pc.getAbility().addDex((byte) 1);
						pc.resetBaseAc();
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.sendPackets(new S_PacketBox(S_PacketBox.char_ER, pc.get_PlusEr()), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				case 40036:
					if (pc.getAbility().getInt() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {
						pc.getAbility().addInt((byte) 1);
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				case 40037:
					if (pc.getAbility().getWis() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {
						pc.getAbility().addWis((byte) 1);
						pc.resetBaseMr();
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				case 40038:
					if (pc.getAbility().getCha() < 45 && pc.getAbility().getElixirCount() < 10 && ElixirLev >= 1) {
						pc.getAbility().addCha((byte) 1);
						pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc), true);
						pc.save();
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.Elixir, pc.getAbility().getElixirCount()));
					} else {
						pc.sendPackets(new S_ServerMessage(4473), true);
						pc.sendPackets(new S_SystemMessage("\\aH알림: 엘릭서는 50lv부터 5lv단위로 섭취가 가능합니다 ."), true);
					}
					break;
				}
			}
		} catch (Exception e) {
		}
	}
}
