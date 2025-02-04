package l1j.server.server.model;

import java.util.TimerTask;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.utils.CalcStat;

public class MpRegeneration extends TimerTask {
	// private static Logger _log =
	// Logger.getLogger(MpRegeneration.class.getName());

	private final L1PcInstance _pc;
	private int _regenPoint = 0;
	private int _curPoint = 4;

	public MpRegeneration(L1PcInstance pc) {
		_pc = pc;
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

			if (64 <= _regenPoint) {
				_regenPoint = 0;
				regenMp();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			// _log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void regenMp() {
		int baseMpr = 1;
		int wis = _pc.getAbility().getTotalWis();
		if (wis == 15 || wis == 16) {
			baseMpr = 2;
		} else if (wis >= 17) {
			baseMpr = 3;
		}

		// ���̽� WIS ȸ�� ���ʽ�
		int baseStatMpr = CalcStat.��ȸ��ƽ(_pc.getAbility().getTotalWis());

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_BLUE_POTION) == true) {
			if (wis < 11) {
				wis = 11;
			}
			baseMpr += wis - 10;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_BLUE_POTION2) == true) {
			if (wis < 11) {
				wis = 11;
			}
			baseMpr += wis - 8;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_BLUE_POTION3) == true) {
			baseMpr += 3;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION) == true) {
			baseMpr += 5;
		}
		if (_pc.getSkillEffectTimerSet()
				.hasSkillEffect(L1SkillId.CONCENTRATION) == true) {
			baseMpr += 4;
		}
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
			baseMpr += 3;
		}
		if (isInn(_pc)) {
			baseMpr += 3;
		}
		if (L1HouseLocation.isRegenLoc(_pc, _pc.getX(), _pc.getY(),
				_pc.getMapId())) {
			baseMpr += 3;
		}

		int itemMpr = _pc.getInventory().mpRegenPerTick();
		itemMpr += _pc.getMpr();

		if (_pc.get_food() < 24 || isOverWeight(_pc)) {
			baseMpr = 0;
			baseStatMpr = 0;
			if (itemMpr > 0) {
				itemMpr = 0;
			}
		}

		int mpr = baseMpr + itemMpr + baseStatMpr;
		// System.out.println("mpr : "+mpr+" base : "+baseMpr+" item : "+itemMpr+" basestat : "+baseStatMpr);
		int newMp = _pc.getCurrentMp() + mpr;

		_pc.setCurrentMp(newMp);
	}

	private boolean isOverWeight(L1PcInstance pc) {

		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.EXOTIC_VITALIZE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.ADDITIONAL_FIRE) || (pc.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.SCALES_WATER_DRAGON))) {
			return false;
		}

		if (isInn(pc)) {
			return false;
		}

		return (50 <= pc.getInventory().calcWeightpercent()) ? true : false;
	}

	private boolean isInn(L1PcInstance pc) {
		int mapId = pc.getMapId();
		return (mapId == 16384 || mapId == 16896 || mapId == 17408
				|| mapId == 17492 || mapId == 17820 || mapId == 17920
				|| mapId == 18432 || mapId == 18944 || mapId == 19456
				|| mapId == 19968 || mapId == 20480 || mapId == 20992
				|| mapId == 621 || mapId == 21504 || mapId == 22016
				|| mapId == 22528 || mapId == 23040 || mapId == 23552
				|| mapId == 24064 || mapId == 24576 || mapId == 25088) ? true
				: false;
	}
}
