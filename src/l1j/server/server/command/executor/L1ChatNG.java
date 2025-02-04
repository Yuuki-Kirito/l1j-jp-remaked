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

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ChatNG implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1ChatNG.class.getName());

	private L1ChatNG() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChatNG();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			int time = Integer.parseInt(st.nextToken());

			L1PcInstance tg = L1World.getInstance().getPlayer(name);

			if (tg != null) {
				/*
				 * getSkillEffectTimerSet().setSkillEffect(L1SkillId.
				 * STATUS_CHAT_PROHIBITED, 120 * 1000); S_SkillIconGFX si = new
				 * S_SkillIconGFX(36, 120); sendPackets(si, true);
				 * S_ServerMessage sm = new S_ServerMessage(153);
				 * sendPackets(sm, true); _chatCount = 0; _oldChatTimeInMillis =
				 * 0;
				 */

				tg.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_CHAT_PROHIBITED, time * 60 * 1000);
				tg.sendPackets(new S_SkillIconGFX(36, time * 60));
				tg.sendPackets(new S_ServerMessage(286, String.valueOf(time))); // \f3게임에
																				// 적합하지
																				// 않는
																				// 행동이기
																				// (위해)때문에,
																				// 향후%0분간
																				// 채팅을
																				// 금지합니다.
				pc.sendPackets(new S_SystemMessage(name
						+ "님은 게임에 적합하지 않은 행동으로 인해 " + String.valueOf(time)
						+ "분 간 채팅을 금지합니다."));
				// L1World.getInstance().broadcastServerMessage(name+"님은 게임에 적합하지 않은 행동으로 인해 "+String.valueOf(time)+"분 간 채팅을 금지합니다.");
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [캐릭터명] [시간(분)] 이라고 입력해 주세요. "));

		}
	}
}
