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
package l1j.server.server.datatables;

import static l1j.server.server.ActionCodes.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class SprTable {

	private static Logger _log = Logger.getLogger(SprTable.class.getName());

	private static class Spr {
		private final HashMap<Integer, Integer> moveSpeed = new HashMap<Integer, Integer>();

		private final HashMap<Integer, Integer> attackSpeed = new HashMap<Integer, Integer>();

		private final HashMap<Integer, Integer> dmgMotionSpeed = new HashMap<Integer, Integer>();

		private int nodirSpellSpeed = 1200;

		private int dirSpellSpeed = 1200;
	}

	private static final HashMap<Integer, Spr> _dataMap = new HashMap<Integer, Spr>();

	private static final SprTable _instance = new SprTable();

	private SprTable() {
		loadSprAction();
	}

	public static SprTable getInstance() {
		return _instance;
	}

	/**
	 * spr_action load
	 */
	public void loadSprAction() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spr_action");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("spr_id");
				if (!_dataMap.containsKey(key)) {
					spr = new Spr();
					_dataMap.put(key, spr);
				} else {
					spr = _dataMap.get(key);
				}

				int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("framecount");
				int frameRate = rs.getInt("framerate");
				int speed = calcMoveActionSpeed(frameCount, frameRate);
				/*
				 * if(key >= 11328 && key <= 11448){ speed =
				 * calcActionSpeed(frameCount, frameRate); }
				 */

				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
				case ACTION_CHAINSWORD_Walk: {
					speed = calcMoveActionSpeed(frameCount, frameRate);
					spr.moveSpeed.put(actid, speed);
				}
					break;
				case ACTION_SkillAttack: {
					spr.dirSpellSpeed = speed;
				}
					break;
				case ACTION_SkillBuff: {
					spr.nodirSpellSpeed = speed;
				}
					break;
				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
				case ACTION_AltAttack:
				case ACTION_CHAINSWORD:
				case 89:
				case 74:
				case 75: {
					spr.attackSpeed.put(actid, speed);
				}
					break;
				case 2:
					spr.dmgMotionSpeed.put(actid, speed);
					break;
				default:
					break;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.config("SPR Data" + _dataMap.size() + "is loaded.");
	}

	/**
	 * フレーム数とフレームレートからアクションの合計時間（ms）を計算して返す。
	 */
	private int calcActionSpeed(int frameCount, int frameRate) {
		// return (int) (frameCount * 40 * (24D / frameRate));
		return (int) (frameCount * 49 * (24D / frameRate));
	}

	private int calcMoveActionSpeed(int frameCount, int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	/**
	 * 指定されたsprの攻撃速度を返す。 sprで指定されたweapon_typeのデータが設定されていない場合は、1。
	 * attackのデータを返す。
	 *
	 * @param sprid
	 * - 調査するsprのID
	 * @param actid
	 * - 武器の種類を表す値。 L1Item.getType1() の変換値 +1 と一致する
	 * @return 指定された spr の攻撃速度 (ms)
	 */
	public int getAttackSpeed(int sprid, int actid) {
		try {
			boolean ww = false;
			if (actid == 89) {
				actid = 12;
				ww = true;
			}

			if (_dataMap.containsKey(sprid)) {
				if (_dataMap.get(sprid).attackSpeed.containsKey(actid)) {
					if (ww) {
						return (int) (_dataMap.get(sprid).attackSpeed.get(actid) * 0.828);
					}
					return _dataMap.get(sprid).attackSpeed.get(actid);
				} else if (actid == ACTION_Attack) {
					return 0;
				} else {
					if (_dataMap.get(sprid).attackSpeed.get(ACTION_Attack) != null) {
						return _dataMap.get(sprid).attackSpeed.get(ACTION_Attack);
					} else {
						return 0;
					}
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	public int getMoveSpeed(int sprid, int actid) {
		try {
			if (actid == 88) {
				actid = 11;
			}
			if (_dataMap.containsKey(sprid)) {
				if (_dataMap.get(sprid).moveSpeed.containsKey(actid)) {
					return _dataMap.get(sprid).moveSpeed.get(actid);
				} else if (actid == ACTION_Walk) {
					return 0;
				} else {
					return _dataMap.get(sprid).moveSpeed.get(ACTION_Walk);
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	public int getDirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).dirSpellSpeed;
		}
		return 0;
	}

	public int getNodirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).nodirSpellSpeed;
		}
		return 0;
	}

	public int getDmgMotionSpeed(int sprid) {
		try {
			if (_dataMap.containsKey(sprid)) {
				if (_dataMap.get(sprid).dmgMotionSpeed.containsKey(2))
					return _dataMap.get(sprid).dmgMotionSpeed.get(2);
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;

	}
}
