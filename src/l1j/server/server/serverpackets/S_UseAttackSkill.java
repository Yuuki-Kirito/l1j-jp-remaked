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

import java.util.concurrent.atomic.AtomicInteger;

import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_UseAttackSkill extends ServerBasePacket {

	private static final String S_USE_ATTACK_SKILL = "[S] S_UseAttackSkill";
	private static AtomicInteger _sequentialNumber = new AtomicInteger(0);

	private byte[] _byte = null;

	// public S_UseAttackSkill(L1Character caster, L1Character target,
	// int spellgfx, boolean motion) {
	// Point pt = target.getLocation();
	// buildPacket(caster, target.getId(), spellgfx, pt.getX(), pt.getY(),
	// ActionCodes.ACTION_SkillAttack, 6, motion);
	// }

	// public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
	// int x, int y) {
	// buildPacket(cha, targetobj, spellgfx, x, y,
	// ActionCodes.ACTION_SkillAttack, 6, true);
	// }

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, true);
	}

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId, boolean motion) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, motion);
	}

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId, int isHit) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, isHit, true);
	}

	public S_UseAttackSkill(L1PcInstance pc, L1Character cha) {// 토마호크
		buildPacket(pc, cha);
	}

	private void buildPacket(L1PcInstance pc, L1Character cha) {
		int autonum = _sequentialNumber.incrementAndGet();
		writeC(Opcodes.S_ATTACK);
		writeC(12);
		writeD(pc.getId());
		writeD(cha.getId());
		writeC(20);
		writeC(0);
		writeC(0);
		writeD(autonum); // 번호가 겹치지 않게 보낸다
		writeH(12524);
		writeC(0); // 뱀파-0 , 에볼- 6 타켓지종:6, 범위&타켓지종:8, 범위:0
		writeH(pc.getX());
		writeH(pc.getY());
		writeH(cha.getX());
		writeH(cha.getY());
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(0);
	}

	private void buildPacket(L1Character cha, int targetobj, int spellgfx, int x, int y, int actionId, int isHit, boolean withCastMotion) {
		if (cha instanceof L1PcInstance) {
			// 그림자계 변신중에 공격 마법을 사용하면(자) 클라이언트가 떨어지기 (위해)때문에 잠정 대응
			if (cha.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.SHAPE_CHANGE)
					&& actionId == ActionCodes.ACTION_SkillAttack) {
				int tempchargfx = cha.getGfxId().getTempCharGfx();
				if (tempchargfx == 5727 || tempchargfx == 5730) {
					actionId = ActionCodes.ACTION_SkillBuff;
				} else if (tempchargfx == 5733 || tempchargfx == 5736) {
					// 보조 마법 모션으로 하면(자) 공격 마법의 그래픽과
					// 대상에의 데미지 모션이 발생하지 않게 되기 (위해)때문에
					// 공격 모션으로 대용
					actionId = ActionCodes.ACTION_Attack;
				}
			}
		}
		// 화령의 주인이 디폴트라면 공격 마법의 그래픽이 발생하지 않기 때문에 강제 치환
		// 어딘가 별개로 관리하는 것이 좋아?
		if (cha.getGfxId().getTempCharGfx() == 4013) {
			actionId = ActionCodes.ACTION_Attack;
		}
		int autonum = _sequentialNumber.incrementAndGet();

		int newheading = calcheading(cha.getX(), cha.getY(), x, y);
		cha.getMoveState().setHeading(newheading);
		writeC(Opcodes.S_ATTACK);
		writeC(actionId);
		writeD(withCastMotion ? cha.getId() : 0);
		writeD(targetobj);
		writeC(isHit);
		writeC(0x00);
		writeC(newheading);
		writeD(autonum); // 번호가 겹치지 않게 보낸다
		writeH(spellgfx);
		writeC(6); // 뱀파-0 , 에볼- 6 타켓지종:6, 범위&타켓지종:8, 범위:0
		writeH(cha.getX());
		writeH(cha.getY());
		writeH(x);
		writeH(y);
		writeD(0);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		} else {
			int seq = _sequentialNumber.incrementAndGet();
			_byte[13] = (byte) (seq & 0xff);
			_byte[14] = (byte) (seq >> 8 & 0xff);
			_byte[15] = (byte) (seq >> 16 & 0xff);
			_byte[16] = (byte) (seq >> 24 & 0xff);
		}

		return _byte;
	}

	private static int calcheading(int myx, int myy, int tx, int ty) {
		int newheading = 0;
		if (tx > myx && ty > myy) {
			newheading = 3;
		}
		if (tx < myx && ty < myy) {
			newheading = 7;
		}
		if (tx > myx && ty == myy) {
			newheading = 2;
		}
		if (tx < myx && ty == myy) {
			newheading = 6;
		}
		if (tx == myx && ty < myy) {
			newheading = 0;
		}
		if (tx == myx && ty > myy) {
			newheading = 4;
		}
		if (tx < myx && ty > myy) {
			newheading = 5;
		}
		if (tx > myx && ty < myy) {
			newheading = 1;
		}
		return newheading;
	}

	@Override
	public String getType() {
		return S_USE_ATTACK_SKILL;
	}

}