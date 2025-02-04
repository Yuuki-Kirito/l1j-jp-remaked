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
package l1j.server.server.model.Instance;

import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_EXTRA_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HEALING;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BREATH;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
//import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Astar.AStar;
import l1j.server.GameSystem.Astar.Node;
import l1j.server.GameSystem.Astar.World;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupInfo;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcChatTimer;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Demolition;
import l1j.server.server.model.L1Tomahawk;
import l1j.server.server.model.L1flame;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SabuTell;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1NpcChat;
import l1j.server.server.types.Point;
import server.LineageClient;

public class L1NpcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	// private static int moncount = 0;

	public static final int MOVE_SPEED = 0;
	public static final int ATTACK_SPEED = 1;
	public static final int MAGIC_SPEED = 2;
	public boolean _is_HILL_AGGRO = false;
	public boolean _is_IMMUNE_AGGRO = false;
	public boolean _isReoTH = false;
	public static final int HIDDEN_STATUS_NONE = 0;
	public static final int HIDDEN_STATUS_SINK = 1;
	public static final int HIDDEN_STATUS_FLY = 2;
	public boolean STATUS_Escape = false;
	public boolean Escape = false;
	private boolean _isPinkName = false;
	
	public boolean isPinkName() {
		return _isPinkName;
	}
	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}

	public int _트루타켓 = 0;
	public int get트루타켓() { return _트루타켓; }
	public void set트루타켓(int i) { _트루타켓 = i; }
	public long NpcDeleteTime = 0;
	public L1Tomahawk tomahawk_th = null;
	public L1Demolition demolition_th = null;
	public L1flame flame_th = null;
	public static final int CHAT_TIMING_APPEARANCE = 0;
	public static final int CHAT_TIMING_DEAD = 1;
	public static final int CHAT_TIMING_HIDE = 2;
	public static final int CHAT_TIMING_GAME_TIME = 3;
	public L1Character _backtarget = null;
	public int _backtargetre = 0;
	private static final long DELETE_TIME = 20000L;
	public boolean skilluse = false;
	private L1Npc _npcTemplate;
	private L1Spawn _spawn;
	public L1DoorInstance _door = null;
	private int _spawnNumber;
	private int _petcost;
	public int tt_clanid = -1;
	public int tt_partyid = -1;
	public int tt_level = 0;

	public boolean Npc_trade = false;
	public boolean isTeleport = false;

	public int particular_summon_count = 0;

	protected L1Inventory _inventory = new L1Inventory();
	private L1MobSkillUse mobSkill;
	private static Random _random = new Random(System.nanoTime());

	private boolean firstFound = true;

	private static int courceRange = 21;

	private int _drainedMana = 0;

	private boolean _rest = false;

	private boolean _제브부활 = false;

	private boolean _isResurrect;

	private int _randomMoveDistance = 0;
	// private int _randomMoveDirection = 0;

	public AStar aStar; // 길찾기 변수
	private int[][] iPath; // 길찾기 변수
	private Node tail; // 길찾기 변수
	private int iCurrentPath; // 길찾기 변수

	private boolean _aiRunning = false;
	private boolean _actived = false;
	private boolean _firstAttack = false;
	private int _sleep_time;
	protected L1HateList _hateList = new L1HateList();
	protected L1HateList _dropHateList = new L1HateList();
	public ArrayList<String> marble = new ArrayList<String>();
	public ArrayList<String> marble2 = new ArrayList<String>();
	public ArrayList<String> tro = new ArrayList<String>();
	// private int _pahp;
	protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>();
	
	/** 어택 유저 케릭터 정보 보관 */
	protected L1Character _target = null;
	
	public L1Character getTarget() {
		return _target;
	}
	
	public void setTarget(L1Character target) {
		_target = target;
	}
	
	protected L1ItemInstance _targetItem = null;

	public L1Character ActiveTarget = null;
	private boolean _isTradingInPrivateShop = false;

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	protected L1PcInstance _master = null;
	private boolean _deathProcessing = false;
	private int _paralysisTime = 0; // Paralysis RestTime
	
	private int _PhantomDTime = 0; // Paralysis RestTime
	private int _PhantomRTime = 0; // Paralysis RestTime
	
	private int 펌프킨타임 = 0; // Paralysis RestTime
	private int 템프펌프킨 = 0; // Paralysis RestTime
	private L1MobGroupInfo _mobGroupInfo = null;
	private int _mobGroupId = 0;
	private int num; /* 버경 관련 */
	private LineageClient _netConnection;

	private DeleteTimer _deleteTask;
	private ScheduledFuture<?> _future = null;

	private Map<Integer, Integer> _digestItems;
	public boolean _digestItemRunning = false;

	/** Hadin Spawn Name 관련 by.케인 **/
	private String Spawn_Location;

	public String getSpawnLocation() {
		return Spawn_Location;
	}

	public void setSpawnLocation(String st) {
		Spawn_Location = st;
	}

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	// private static Logger _log =
	// Logger.getLogger(L1NpcInstance.class.getName());

	public L1NpcInstance(L1Npc template) {
		super();
		setActionStatus(0);
		getMoveState().setMoveSpeed(0);
		setDead(false);
		setRespawn(false);
		iPath = new int[301][2];
		aStar = new AStar();
		aStar.setnpc(this);
		if (template != null) {
			setting_template(template);
			if (getNpcId() == 100109) // 해상전 배
				setActionStatus(29);

			if (getNpcId() == 101037) {
				폰인증멘트시작();
			}
		}
	}

	private int calcRandomVal(int random, int val) {
		int ran = 0;
		if (random > 0) {
			if (val > 0) {
				ran = _random.nextInt(random - val) + 1;
			} else {
				ran = _random.nextInt(random) + 1;
			}
			val += ran;
		} else {
			ran = _random.nextInt(random * (-1)) + 1;
			val -= ran;
		}
		return val;
	}

	// private double calcRandomVal(int seed, int ranval, double rate) {
	// return rate * ( ranval - seed );
	// }

	protected void setting_template(L1Npc template) {
		_npcTemplate = template;
		// double rate = 0;
		int diff = 0;

		setName(template.get_name());
		setNameId(template.get_nameid());

		int level = template.get_level();
		int randomlevel = template.get_randomlevel();
		if (randomlevel != 0) {
			level = calcRandomVal(randomlevel, level);
			diff = randomlevel - level;
			if (level <= 0)
				level = 1;
		}
		setLevel(level);

		int hp = template.get_hp();
		int randomhp = template.get_randomhp();
		if (randomhp != 0) {
			hp = calcRandomVal(randomhp, hp);
			if (hp <= 0)
				hp = 1;
		}
		setMaxHp(hp);
		setCurrentHp(hp);

		int mp = template.get_mp();
		int randommp = template.get_randommp();
		if (randommp != 0) {
			mp = calcRandomVal(randommp, mp);
			if (mp <= 0)
				mp = 0;
		}
		setMaxMp(mp);
		setCurrentMp(mp);

		int ac = template.get_ac();
		int randomac = template.get_randomac();
		if (randomac != 0) {
			ac = calcRandomVal(randomac, ac);
		}

		this.ac.setAc(ac);

		if (template.get_randomlevel() == 0) {
			ability.setStr(template.get_str());
			ability.setCon(template.get_con());
			ability.setDex(template.get_dex());
			ability.setInt(template.get_int());
			ability.setWis(template.get_wis());
			resistance.setBaseMr(template.get_mr());
		} else {
			ability.setStr((byte) Math.min(template.get_str() + diff, 127));
			ability.setCon((byte) Math.min(template.get_con() + diff, 127));
			ability.setDex((byte) Math.min(template.get_dex() + diff, 127));
			ability.setInt((byte) Math.min(template.get_int() + diff, 127));
			ability.setWis((byte) Math.min(template.get_wis() + diff, 127));
			resistance
					.setBaseMr((byte) Math.min(template.get_mr() + diff, 127));

			addHitup((template.get_randomlevel() - level) * 2);
			addDmgup((template.get_randomlevel() - level) * 2);
		}

		setPassispeed(template.get_passispeed());
		setAtkspeed(template.get_atkspeed());
		setAgro(template.is_agro());
		setAgrocoi(template.is_agrocoi());
		setAgrososc(template.is_agrososc());

		gfx.setTempCharGfx(template.get_gfxid());
		gfx.setGfxId(template.get_gfxid());

		if (template.get_randomexp() == 0) {
			setExp(template.get_exp());
		} else {
			int ran = _random.nextInt(template.get_randomexp())
					+ template.get_exp();
			if (ran >= template.get_randomexp()) {
				ran = template.get_randomexp();
			}
			setExp(ran);
		}

		int lawful = template.get_lawful();
		int randomlawful = template.get_randomlawful();

		if (randomlawful != 0) {
			lawful = calcRandomVal(randomlawful, lawful);
		}

		setLawful(lawful);
		setTempLawful(lawful);

		setPickupItem(template.is_picupitem());

		if (template.is_bravespeed()) {
			getMoveState().setBraveSpeed(1);
		} else {
			getMoveState().setBraveSpeed(0);
		}

		if (template.get_digestitem() > 0) {
			_digestItems = new HashMap<Integer, Integer>();
		}

		setKarma(template.getKarma());
		setLightSize(template.getLightSize());

		mobSkill = new L1MobSkillUse(this);
	}

	public void startAI() {
		if (this instanceof L1ArrowInstance)
			return;
		if (getNpcTemplate().get_npcId() == 5000048
				|| getNpcTemplate().get_npcId() == 81159)
			return;
		if (Config.NPCAI_IMPLTYPE == 1) {
			// new NpcAITimerImpl().start();
		} else if (Config.NPCAI_IMPLTYPE == 2) {
			new NpcAI().start();
		} else {
			new NpcAI().start();
			// new NpcAITimerImpl().start();
		}
	}
	
	public void stopAI() {
		new NpcAI().stop();
	}

	/*
	 * private static final TimerPool _timerPool = new TimerPool(4);
	 * 
	 * class NpcAITimerImpl extends TimerTask { private class DeathSyncTimer
	 * extends TimerTask { private void schedule(int delay) {
	 * _timerPool.getTimer().schedule(new DeathSyncTimer(), delay); }
	 * 
	 * @Override public void run() {
	 * 
	 * try { if (isDeathProcessing()) { schedule(getSleepTime()); return; }
	 * allTargetClear(); setAiRunning(false); } catch (Exception e) {
	 * e.printStackTrace(); } } }
	 * 
	 * 
	 * public void start() { setAiRunning(true);
	 * _timerPool.getTimer().schedule(NpcAITimerImpl.this, 0); }
	 * 
	 * private void stop() { mobSkill.resetAllSkillUseCount();
	 * _timerPool.getTimer().schedule(new DeathSyncTimer(), 0); }
	 * 
	 * private void schedule(int delay) { _timerPool.getTimer().schedule(new
	 * NpcAITimerImpl(), delay); }
	 * 
	 * @Override public void run() {
	 * 
	 * try { if (notContinued()) { stop(); return; } if (0 < 펌프킨타임) {
	 * schedule(펌프킨타임); 펌프킨타임 = 0; setAtkspeed(템프펌프킨); 템프펌프킨 = 0; return; } if
	 * (0 < _paralysisTime) { schedule(_paralysisTime); _paralysisTime = 0;
	 * setParalyzed(false); return; } else if (isParalyzed() || isSleeped() ||
	 * isTeleport) { schedule(200); return; } // System.out.println(2); if
	 * (!AIProcess()) { schedule(getSleepTime()); return; } stop(); } catch
	 * (Exception e) { System.out.println("NPC ID : "+
	 * getNpcTemplate().get_npcId()); e.printStackTrace();
	 * //_log.log(Level.WARNING, "NpcAI에 예외가 발생했습니다.", e); } }
	 * 
	 * private boolean notContinued() { return _destroyed || isDead() ||
	 * getCurrentHp() <= 0 || getHiddenStatus() != HIDDEN_STATUS_NONE; } }
	 */

	public Object synchObject = new Object();

	// type: 2
	class NpcAIThreadImpl implements Runnable {

		public void start() {
			GeneralThreadPool.getInstance().execute(this);
		}

		public void run() {
			try {
				setAiRunning(true);
				while (!_destroyed && !isDead() && getCurrentHp() > 0
						&& getHiddenStatus() == HIDDEN_STATUS_NONE) {
					if (isParalyzed() || isSleeped() || isTeleport) {
						GeneralThreadPool.getInstance().schedule(this, 100);
						return;
					}
					/*
					 * while (isParalyzed() || isSleeped() || isTeleport) { try
					 * { Thread.sleep(200); } catch (InterruptedException e) {
					 * setParalyzed(false); } }
					 */
					synchronized (synchObject) {
						if (AIProcess()) {
							break;
						}
						if (getSleepTime() == 0) {
							setSleepTime(300);
						}
						try {
							Thread.sleep(getSleepTime());
						} catch (Exception e) {
							break;
						}
					}
				}
				if (mobSkill != null)
					mobSkill.resetAllSkillUseCount();
				do {
					try {
						Thread.sleep(getSleepTime());
					} catch (Exception e) {
						break;
					}
				} while (isDeathProcessing());
				allTargetClear();
				setAiRunning(false);
			} catch (Exception e) {
				System.out.println("NPC ID : " + getNpcTemplate().get_npcId());
				e.printStackTrace();
				// _log.log(Level.WARNING, "NpcAI에 예외가 발생했습니다.", e);
			}
			// 12_09
			if (L1NpcInstance.this instanceof L1DollInstance) {
				L1DollInstance doll = (L1DollInstance) L1NpcInstance.this;
				doll.deleteDoll();
			}
		}
	}

	class DeathSyncTimer implements Runnable {
		private void schedule(int delay) {
			GeneralThreadPool.getInstance()
					.schedule(DeathSyncTimer.this, delay);
		}

		@Override
		public void run() {
			if (isDeathProcessing()) {
				schedule(getSleepTime());
				return;
			}
			allTargetClear();
			setAiRunning(false);
		}
	}

	// type: 2
	class NpcAI implements Runnable {

		private boolean ck = false;
		private boolean skillCountReset = false;

		/*
		 * public void start() { setAiRunning(true);
		 * //GeneralThreadPool.getInstance().execute(this);
		 * GeneralThreadPool.getInstance().schedule(NpcAIThreadImpl2.this, 0); }
		 */

		public void start() {
			setAiRunning(true);
			GeneralThreadPool.getInstance().schedule(NpcAI.this, 0);
		}

		private void stop() {
			if (!skillCountReset) {
				skillCountReset = true;
				if (mobSkill != null)
					mobSkill.resetAllSkillUseCount();
			}
			GeneralThreadPool.getInstance().schedule(new DeathSyncTimer(), 0);
		}

		private void schedule(int delay) {
			GeneralThreadPool.getInstance().schedule(NpcAI.this, delay);
		}

		public void run() {
			try {
				// if(!ck && !_destroyed && !isDead() && getCurrentHp() > 0
				// && getHiddenStatus() == HIDDEN_STATUS_NONE) {
				if (notContinued()) {
					stop();
					return;
				}
				if (isParalyzed() || isSleeped() || isTeleport) {
					schedule(200);
					return;
				}
				if (!AIProcess()) {
					schedule(getSleepTime());
					return;
				}

				stop();
				// if(isParalyzed() || isSleeped() || isTeleport){
				// GeneralThreadPool.getInstance().schedule(this, 100);
				// return;
				// }
				// synchronized(synchObject){
				// if (AIProcess()) {
				// ck = true;
				// GeneralThreadPool.getInstance().execute(this);
				// return;
				// }
				// if(getSleepTime() == 0){
				// setSleepTime(300);
				// }
				// GeneralThreadPool.getInstance().schedule(this,
				// getSleepTime());
				// return;
				// }
				// }
				/*
				 * if(!skillCountReset){ skillCountReset = true; if(mobSkill !=
				 * null) mobSkill.resetAllSkillUseCount();
				 * GeneralThreadPool.getInstance().schedule(this,
				 * getSleepTime()); return; } if(isDeathProcessing()){
				 * GeneralThreadPool.getInstance().schedule(this,
				 * getSleepTime()); return; } allTargetClear();
				 * setAiRunning(false);
				 */
			} catch (Exception e) {
				System.out.println("NPC ID : " + getNpcTemplate().get_npcId());
				e.printStackTrace();
				// _log.log(Level.WARNING, "NpcAI에 예외가 발생했습니다.", e);
			}
			// 12_09
			/*
			 * if(L1NpcInstance.this instanceof L1DollInstance){ L1DollInstance
			 * doll = (L1DollInstance)L1NpcInstance.this; doll.deleteDoll(); }
			 */
		}

		private boolean notContinued() {
			return ck || _destroyed || isDead() || getCurrentHp() <= 0
					|| getHiddenStatus() != HIDDEN_STATUS_NONE;
		}
	}

	private boolean AIProcess() {
		setSleepTime(300);

		if (this instanceof L1MonsterInstance) {
			if (((L1MonsterInstance) this).shellManClose) {
				return false;
			}
		}
		if (_isReoTH) {
			if (_door == null || _target == null) {
				for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
					if (getNpcId() == 100837) {// 레오1
						if (obj instanceof L1DoorInstance) {
							L1DoorInstance mon = (L1DoorInstance) obj;
							if (mon.getNpcId() == 100833) {// 100834
								_door = mon;
								_hateList.add(mon, 0);
								_target = mon;
								break;
							}
						}
					}
					if (getNpcId() == 100838) {// 레오2
						if (obj instanceof L1DoorInstance) {
							L1DoorInstance mon = (L1DoorInstance) obj;
							if (mon.getNpcId() == 100834) {//
								_door = mon;
								_hateList.add(mon, 0);
								_target = mon;
								break;
							}
						}
					}
				}
			}
		} else {

			checkTarget();
			if (_target == null && _master == null) {
				// if(getMapId() == 1937)
				// System.out.println("아 여기는오는데 씨발");
				searchTarget();
			}
			onItemUse();

		}

		if (_target == null) {
			checkTargetItem();
			if (isPickupItem() && _targetItem == null) {
				searchTargetItem();
			}

			if (_targetItem == null) {
				if (noTarget()) {
					return true;
				}
			} else {
				// onTargetItem();
				L1Inventory groundInventory = L1World.getInstance()
						.getInventory(_targetItem.getX(), _targetItem.getY(),
								_targetItem.getMapId());
				if (groundInventory.checkItem(_targetItem.getItemId())) {
					onTargetItem();
				} else {
					_targetItemList.remove(_targetItem);
					_targetItem = null;
					setSleepTime(1000);
					return false;
				}
			}
			ActiveTarget = null;
		} else {
			if (getHiddenStatus() == HIDDEN_STATUS_NONE) {
				ActiveTarget = _target;
				onTarget();
			} else {
				ActiveTarget = null;
				allTargetClear();
				return true;
			}
		}
		return false;
	}

	public void onItemUse() {
	}

	public void searchTarget() {
	}

	public void checkTarget() {
		/*
		 * if(_target instanceof L1PcInstance){ L1PcInstance pc =
		 * (L1PcInstance)_target; if(pc !=null){
		 * if(!pc.getNearObjects().knownsObject(this)){
		 * if(pc.getLocation().getLineDistance(this.getLocation()) <= 15){
		 * pc.getNearObjects().addKnownObject(this); } } } }
		 */

		if (_target == null || _target.getMapId() != getMapId()
				|| _target.isDead() || _target.getCurrentHp() <= 0
				|| (_target.isInvisble() && !getNpcTemplate().is_agrocoi() 
						&& !_hateList.containsKey(_target))) {
			if (_target != null) {
				tagertClear();
			}

			if (!_hateList.isEmpty()) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}
		}

	}

	public void checkTargetItem() {
		try {
			if (_targetItem == null
					|| _targetItem.getMapId() != getMapId()
					|| getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
				if (!_targetItemList.isEmpty()) {
					try {
						_targetItem = _targetItemList.get(0);
						_targetItemList.remove(0);
					} catch (Exception e) {
					}
					checkTargetItem();
				} else {
					_targetItem = null;
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 거리값 추출.
	 */
	public int getDistance(int x, int y, int tx, int ty) {
		long dx = tx - x;
		long dy = ty - y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * 거리안에 있다면 참
	 */
	public boolean isDistance(int x, int y, int m, int tx, int ty, int tm,
			int loc) {
		int distance = getDistance(x, y, tx, ty);
		if (loc < distance)
			return false;
		if (m != tm)
			return false;
		return true;
	}

	/**
	 * 해당하는 좌표로 방향을 전환할때 사용.
	 */
	public int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}

	public int calcheading(L1Object o, int x, int y) {
		return calcheading(o.getX(), o.getY(), x, y);
	}

	private int cnt = 0;

	public void onTarget() {
		int targetx = _target.getX();
		int targety = _target.getY();
		setActived(true);
		_targetItemList.clear();
		_targetItem = null;
		L1Character target = _target;
		if (target == null) {
			return;
		}
		if (getAtkspeed() == 0 && getPassispeed() == 0) {
			return;
		}
		if (!_isReoTH && !(getNpcId() >= 100750 && getNpcId() <= 100757)) {
			int escapeDistance = 23;
			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DARKNESS)
					|| getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.CURSE_BLIND))
				escapeDistance = 1;
			int calcx = (int) getLocation().getX()
					- target.getLocation().getX();
			int calcy = (int) getLocation().getY()
					- target.getLocation().getY();

			if (Math.abs(calcx) > escapeDistance || Math.abs(calcy) > escapeDistance 
					|| target.get_delete()) { //허공버그픽스
				tagertClear();
				return;
			}
		}
		// System.out.println(CharPosUtil.isAttackPosition(this, target.getX(),
		// target.getY(), getNpcTemplate().get_ranged())
		// +" > "+CharPosUtil.isAttackPosition(target, getX(), getY(),
		// getNpcTemplate().get_ranged()));
		if (getAtkspeed() == 0 && getPassispeed() > 0) {
			int dir = targetReverseDirection(target.getX(), target.getY());
			dir = 체크(getX(), getY(), dir);
			if (dir == -1) {
				return;
			}
			setDirectionMove(dir);
			setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
		} else {
			if (getNpcId() == 100837 && _isReoTH) {
				targetx = 32653;
				targety = 33000;
			}
			if (getNpcId() == 100838 && _isReoTH) {
				targetx = 32693;
				targety = 33052;
			}

			boolean tail = World.isThroughAttack(getX(), getY(), getMapId(),
					calcheading(this, targetx, targety));
			if (getX() == target.getX() && getY() == target.getY()
					&& getMapId() == target.getMapId())
				tail = true;

			/*
			 * if(!tail ){ broadcastPacket(new S_NpcChatPacket(this,
			 * "아시바 나못가는디")); }
			 */
			boolean door = World.door_to_door(getX(), getY(), getMapId(),
					calcheading(this, targetx, targety));
			/*
			 * if(!door ){ broadcastPacket(new S_NpcChatPacket(this,
			 * "아시바 나못가는디")); }
			 */
			/*
			 * if(!tail ){ if(getNpcId() >=100750 && getNpcId() <= 100757){ tail
			 * = true; } }
			 */
			if (door) {
				if (getNpcId() >= 100750 && getNpcId() <= 100757) door = false;
				if (getNpcId() >= 46410 && getNpcId() <= 46483) door = false;
			}
			if ((getNpcId() == 7000002 || getNpcId() == 4707001)
					&& CharPosUtil.isAttackPosition(this, target.getX(), target
							.getY(), target.getMapId(), getNpcTemplate()
							.get_ranged()) == true) {
				if (mobSkill.isSkillTrigger(target)) {
					if (_random.nextInt(2) >= 1) {
						getMoveState().setHeading(
								CharPosUtil.targetDirection(this,
										target.getX(), target.getY()));
						attackTarget(target);
					} else {
						if (mobSkill.skillUse(target, true)) {
							setSleepTime(calcSleepTime(mobSkill.getSleepTime(),
									MAGIC_SPEED));
						} else {
							getMoveState().setHeading(
									CharPosUtil.targetDirection(this,
											target.getX(), target.getY()));
							attackTarget(target);
						}
					}
				} else {
					getMoveState().setHeading(
							CharPosUtil.targetDirection(this, target.getX(),
									target.getY()));
					attackTarget(target);
				}
			} else if (CharPosUtil.isAttackPosition(this, target.getX(), target
					.getY(), target.getMapId(), getNpcTemplate().get_ranged()) == true
					&& CharPosUtil.isAttackPosition(target, getX(), getY(),
							getMapId(), getNpcTemplate().get_ranged()) == true
					&& !_isReoTH && !STATUS_Escape) {// 기본 공격범위
				if (door) {
					// System.out.println("문막");
					cnt++;
					if (cnt > 5) {
						tagertClear();
						_backtarget = target;
						cnt = 0;
					}
					return;
				}
				if (!tail) {
					// System.out.println("길막");
					cnt++;
					if (cnt > 5) {
						tagertClear();
						_backtarget = target;
						cnt = 0;
					}
					return;
				}

				if (mobSkill.isSkillTrigger(target)) {
					if (_random.nextInt(2) >= 1) {
						getMoveState().setHeading(
								CharPosUtil.targetDirection(this,
										target.getX(), target.getY()));
						attackTarget(target);
					} else {
						if (mobSkill.skillUse(target, true)) {
							setSleepTime(calcSleepTime(mobSkill.getSleepTime(),
									MAGIC_SPEED));
						} else {
							getMoveState().setHeading(
									CharPosUtil.targetDirection(this,
											target.getX(), target.getY()));
							attackTarget(target);
						}
					}
				} else {
					getMoveState().setHeading(
							CharPosUtil.targetDirection(this, target.getX(),
									target.getY()));
					attackTarget(target);
				}

			} else {
				if (mobSkill != null && mobSkill.skillUse(target, true)) {// 확률적용
					setSleepTime(calcSleepTime(mobSkill.getSleepTime(),
							MAGIC_SPEED));
					return;
				}
				if (getPassispeed() > 0) {
					int distance = getLocation().getTileDistance(
							target.getLocation());
					if (firstFound == true && getNpcTemplate().is_teleport()
							&& distance > 3 && distance < 15) {
						if (nearTeleport(target.getX(), target.getY()) == true) {
							firstFound = false;
							return;
						}
					}

					if (getNpcTemplate().is_teleport()
							&& 20 > _random.nextInt(100)
							&& getCurrentMp() >= 10 && distance > 6
							&& distance < 15) {
						if (nearTeleport(target.getX(), target.getY()) == true) {
							return;
						}
					}
					/** 머미로드 소환 **/
					if (getNpcTemplate().get_npcId() == 45653
							&& 15 > _random.nextInt(100) && distance < 15) {
						if (!target.getSkillEffectTimerSet().hasSkillEffect(
								EARTH_BIND)
								&& !target.getSkillEffectTimerSet()
										.hasSkillEffect(ICE_LANCE)
								&& !target.getSkillEffectTimerSet()
										.hasSkillEffect(FREEZING_BREATH)) {
							if (target instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) target;
								L1Location loc = getLocation().randomLocation(
										1, false);
								if (target instanceof L1RobotInstance) {
									L1RobotInstance rob = (L1RobotInstance) target;
									L1Teleport.robottel(rob, loc.getX(), loc.getY(),
											(short) loc.getMapId(), true);
								} else {
									pc.dx = loc.getX();
									pc.dy = loc.getY();
									pc.dm = (short) getLocation().getMapId();
									pc.dh = _random.nextInt(8);
									pc.setTelType(7);
									pc.sendPackets(new S_SabuTell(pc), true);
								}
							}
						}
					}

					// //////////여기야 씨발

					int dir = moveDirection(target.getMapId(), targetx, targety);

					if (dir == -1) {
						if (this.getNpcId() >= 100750
								&& this.getNpcId() <= 100757) {
							L1Map m = L1WorldMap.getInstance().getMap(
									getMapId());
							if (m.getOriginalTile(getX(), getY()) == 12) {
								getMoveState().setHeading(
										CharPosUtil.targetDirection(this,
												target.getX(), target.getY()));

								dir = getMoveState().getHeading();
								// Broadcaster.broadcastPacket(this, new
								// S_NpcChatPacket(this,
								// "타일값 "+m.getOriginalTile(getX(), getY()),
								// 0));

								if (!오브젝트체크(getX(), getY(), dir)) {
									dir = -1;
								}
							}
						}
					}

					if (STATUS_Escape) {
						dir = EscapeDirection(dir, targetx, targety);
					}
					if (dir == -1) {
						cnt++;
						if (cnt > 5) {
							int targetDir = CharPosUtil.targetDirection(this,
									target.getX(), target.getY());
							if (펫체크(target.getX(), target.getY(), targetDir)) {
							}
							_backtarget = target;
							tagertClear();
							cnt = 0;
							// Broadcaster.broadcastPacket(this, new
							// S_NpcChatPacket(this, "무브다이렉선 -1값", 0));
						}
					} else {

						boolean tail2 = World.isThroughObject(getX(), getY(),
								getMapId(), dir);

						if (this.getNpcId() == 45617
								|| this.getNpcId() == 45529) {
							tail2 = true;
						}

						if (this.getNpcId() == 100837
								|| this.getNpcId() == 100838) {
							door = false;
							// tail2 = true;
						}

						if (this.getNpcId() >= 100750
								&& this.getNpcId() <= 100757) {
							door = false;
							tail2 = true;
							// tail2 = true;
						}

						if (door || !tail2) {

							// System.out.println("못가~");
							cnt++;
							if (cnt > 5) {
								_backtarget = target;
								tagertClear();
								cnt = 0;
							}
							// Broadcaster.broadcastPacket(this, new
							// S_NpcChatPacket(this, "몰라 못가", 0));
							return;
						}
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
					}
				} else {
					if (getNpcTemplate().get_npcId() == 100003
							|| getNpcTemplate().get_npcId() == 100814) {
						if (L1World.getInstance().getVisiblePlayer(this, 1)
								.size() < 1) {
							Broadcaster.broadcastPacket(this,
									new S_NpcChatPacket(this,
											"천한 인간 주제에 보기보다 쎄구나!", 0), true);
							((L1MonsterInstance) this).shellManClose = true;
							Broadcaster.broadcastPacket(this,
									new S_DoActionGFX(getId(), 4), true);
							setActionStatus(4);
							Broadcaster.broadcastPacket(this,
									new S_CharVisualUpdate(this), true);
						} else
							return;
					}
					tagertClear();
				}
			}
		}
	}

	/** 하딘 관련 (케레니스, 바포) **/
	public long HadinBossDelay = 0;

	public void Hadin_kere_vs_bapho(L1NpcInstance target) {
		if (CharPosUtil.isAttackPosition(this, target.getX(), target.getY(),
				target.getMapId(), getNpcTemplate().get_ranged())) {// 기본 공격범위
			if (mobSkill.isSkillTrigger(target)) {
				if (_random.nextInt(2) >= 1) {
					getMoveState().setHeading(
							CharPosUtil.targetDirection(this, target.getX(),
									target.getY()));
					attackTarget(target);
					HadinBossDelay = (calcSleepTime(getAtkspeed(), ATTACK_SPEED))
							+ System.currentTimeMillis();
				} else {
					if (mobSkill.skillUse(target, true)) {
						HadinBossDelay = (calcSleepTime(
								mobSkill.getSleepTime(), MAGIC_SPEED))
								+ System.currentTimeMillis();
					} else {
						getMoveState().setHeading(
								CharPosUtil.targetDirection(this,
										target.getX(), target.getY()));
						attackTarget(target);
						HadinBossDelay = (calcSleepTime(getAtkspeed(),
								ATTACK_SPEED)) + System.currentTimeMillis();
					}
				}
			} else {
				getMoveState().setHeading(
						CharPosUtil.targetDirection(this, target.getX(),
								target.getY()));
				attackTarget(target);
				HadinBossDelay = (calcSleepTime(getAtkspeed(), ATTACK_SPEED))
						+ System.currentTimeMillis();
			}
		} else {
			if (mobSkill.skillUse(target, true)) {// 확률적용
				HadinBossDelay = calcSleepTime(mobSkill.getSleepTime(),
						MAGIC_SPEED) + System.currentTimeMillis();
				return;
			}
			if (getPassispeed() > 0) {
				int dir = moveDirection(target.getMapId(), target.getX(),
						target.getY());
				if (dir == -1) {
				} else {
					setDirectionMove(dir);
					HadinBossDelay = calcSleepTime(getPassispeed(), MOVE_SPEED)
							+ +System.currentTimeMillis();
				}
			}
		}
	}

	public void die(L1Character lastAttacker) {
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		setActionStatus(ActionCodes.ACTION_Die);
		getMap().setPassable(getLocation(), true);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Die), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		setLawful(0);
		allTargetClear();
		startDeleteTimer();
	}

	public void setHate(L1Character cha, int hate) {
		if (cha != null && cha.getId() != getId()) {
			/*
			 * if (!isFirstAttack() && hate != 0) { // hate += 20; hate +=
			 * getMaxHp() / 10; setFirstAttack(true); }
			 */
			_hateList.add(cha, hate);
			_dropHateList.add(cha, hate);
			if (!_is_HILL_AGGRO) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}
			if (!_is_IMMUNE_AGGRO) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}
		}
	}

	public void setLink(L1Character cha) {
	}

	public void serchLink(L1PcInstance targetPlayer, int family) {
		List<L1Object> targetKnownObjects = targetPlayer.getNearObjects()
				.getKnownObjects();
		L1NpcInstance npc = null;
		L1MobGroupInfo mobGroupInfo = null;
		for (Object knownObject : targetKnownObjects) {
			if (knownObject instanceof L1NpcInstance) {
				npc = (L1NpcInstance) knownObject;
				if (npc.getNpcTemplate().get_agrofamily() > 0) {
					if (npc.getNpcTemplate().get_agrofamily() == 1) {
						if (npc.getNpcTemplate().get_family() == family) {
							npc.setLink(targetPlayer);
						}
					} else {
						npc.setLink(targetPlayer);
					}
				}
				mobGroupInfo = getMobGroupInfo();
				if (mobGroupInfo != null) {
					if (getMobGroupId() != 0
							&& getMobGroupId() == npc.getMobGroupId()) {
						npc.setLink(targetPlayer);
					}
				}
			}
		}
	}

	public void attackTarget(L1Character target) {
		Random random = new Random();
		if (target instanceof L1PcInstance) {
			L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) {
				return;
			}
		} else if (target instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) target;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		} else if (target instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) target;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		}
		if (this instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) this;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		} else if (this instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) this;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) {
				allTargetClear();
				return;
			}
		}

		boolean isCounterBarrier = false;
		boolean isHALPHAS = false;
		boolean isMortalBody = false;
		boolean isinferno = false;
		boolean isLindArmor = false;
		boolean isTaitanrock = false;
		boolean isTaitanBllit = false;
		L1Attack attack = new L1Attack(this, target);
		L1PcInstance _targetpc = null;
		int TitanRatio = 41;
		int 라이징 = 5;
		if (target instanceof L1PcInstance) {
			_targetpc = (L1PcInstance) target;
		}
		if (attack.calcHit()) {
			if (_targetpc != null) {
				if (_targetpc.getWeapon() != null) {
					if (_targetpc.isTaitanR) {
						int hpRatio = 100;
						if (0 < _targetpc.getMaxHp()) {
							hpRatio = 100 * _targetpc.getCurrentHp() / _targetpc.getMaxHp();
						}
						if(_targetpc.getWeapon().getItemId() == 30083 || _targetpc.getWeapon().getItemId() == 31083
								|| _targetpc.getWeapon().getItemId() == 222208 || _targetpc.getWeapon().getItemId() == 30092){
							TitanRatio += 5;
						}
						if(_targetpc.getSecondWeapon() != null){
						if((_targetpc.isSlayer && _targetpc.getSecondWeapon().getItemId() == 30083) || (_targetpc.isSlayer && _targetpc.getSecondWeapon().getItemId() == 31083)
								|| (_targetpc.isSlayer && _targetpc.getSecondWeapon().getItemId() == 222208) || (_targetpc.isSlayer && _targetpc.getSecondWeapon().getItemId() == 30092)){
							TitanRatio += 5;
						}
						}
						if (_targetpc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.RISING)){
							if(_targetpc.getLevel() > 80){
								라이징 += _targetpc.getLevel() - 80;
							}
							if(라이징 > 10){
								라이징 = 10;
							}
							TitanRatio += 라이징;
						}
						if (hpRatio < TitanRatio) {
							int chan = random.nextInt(100) + 1;

							boolean isProbability = false;
							if (_targetpc.getInventory().checkItem(41246, 10)) {
							if (30 > chan) {
								isProbability = true;
							}
						}

						if (_targetpc.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.SHOCK_STUN)
								|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.EARTH_BIND)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.EMPIRE)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.FORCE_STUN)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.PANTERA)) {
							isProbability = false;
						}

						boolean isShortDistance = attack.isShortDistance();
						if (isProbability && isShortDistance) {
							_targetpc.getInventory().consumeItem(41246, 10);
							isTaitanrock = true;
						}
					}
				}
				if (_targetpc.isTaitanB) {
					int hpRatio = 100;
					if (0 < _targetpc.getMaxHp()) {
						hpRatio = 100 * _targetpc.getCurrentHp()
								/ _targetpc.getMaxHp();
					}
					if(_targetpc.getWeapon().getItemId() == 30083 || _targetpc.getWeapon().getItemId() == 31083
							|| _targetpc.getWeapon().getItemId() == 222208 || _targetpc.getWeapon().getItemId() == 30092){
						TitanRatio += 5;
					}
					if (hpRatio < TitanRatio) {
						int chan = random.nextInt(100) + 1;
						boolean isProbability = false;
						if (_targetpc.getInventory().checkItem(41246, 10)) {
							if (30 > chan) {
								isProbability = true;
								_targetpc.getInventory().consumeItem(41246, 10);
							}
						}
						if (_targetpc.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.SHOCK_STUN)
								|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.EARTH_BIND)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.EMPIRE)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.FORCE_STUN)
										|| _targetpc.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.PANTERA)) {
							isProbability = false;
						}
						boolean isShortDistance = attack.isShortDistance();
						if (isProbability && !isShortDistance) {
							isTaitanBllit = true;
						}
					}
				}
			}
			}
			if (target.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.COUNTER_BARRIER)) {
				int chan = random.nextInt(100) + 1;
				boolean isProbability = false;
				if (20 > chan) {
					isProbability = true;
				}
				if (target.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.SHOCK_STUN)
						|| target.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.EARTH_BIND)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.EMPIRE)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.FORCE_STUN)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.PANTERA)) {
					isProbability = false;
				}
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isCounterBarrier = true;
				}
			} else if (target.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.HALPHAS)) {
				int chan = random.nextInt(100) + 1;
				boolean isProbability = false;
				if (25 > chan) {
					isProbability = true;
				}
				if (target.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.SHOCK_STUN)
						|| target.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.EARTH_BIND)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.EMPIRE)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.FORCE_STUN)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.PANTERA)) {
					isProbability = false;
				}
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isHALPHAS = true;
				}
			} else if (target.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.MORTAL_BODY)) {
				int chan = random.nextInt(100) + 1;
				boolean isProbability = false;
				if (15 > chan) {
					isProbability = true;
				}
				if (target.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.SHOCK_STUN)
						|| target.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.EARTH_BIND)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.EMPIRE)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.FORCE_STUN)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.PANTERA)) {
					isProbability = false;
				}
				// boolean isShortDistance = attack.isShortDistance();
				if (isProbability /* && isShortDistance */) {
					isMortalBody = true;
				}
			} else if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INFERNO)) {
				int chan = random.nextInt(100) + 1;
				boolean isProbability = false;
				if (25 > chan) {
					isProbability = true;
				}
				if (target.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.SHOCK_STUN)
						|| target.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.EARTH_BIND)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.EMPIRE)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.FORCE_STUN)
						|| target.getSkillEffectTimerSet()
						.hasSkillEffect(L1SkillId.PANTERA)) {
					isProbability = false;
				}
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isinferno = true;
				}
			} else if (target instanceof L1PcInstance) {
				/*
				 * L1PcInstance tp = (L1PcInstance)target;
				 * if(tp.getInventory().checkEquipped(420108) ||
				 * tp.getInventory().checkEquipped(420109) ||
				 * tp.getInventory().checkEquipped(420110) ||
				 * tp.getInventory().checkEquipped(420111)){ int chan =
				 * random.nextInt(100); for(L1ItemInstance item :
				 * tp.getInventory().getItems()){ if(item.isEquipped() &&
				 * item.getItemId() >= 420108 && item.getItemId() <= 420111){
				 * if(item.getEnchantLevel()*2 > chan &&
				 * getNpcTemplate().getBowActId() != 0) isLindArmor = true;
				 * break; } } }
				 */
			}

			if (!isCounterBarrier && !isMortalBody && !isLindArmor && !isinferno && !isHALPHAS) {
				attack.calcDamage();
			}
		}
		if (isTaitanrock) {
			attack.actionTaitan(0);
			attack.commitTaitan(0);
		} else if (isTaitanBllit) {
			attack.actionTaitan(2);
			attack.commitTaitan(2);
		} else if (isCounterBarrier) {
			attack.actionCounterBarrier();
			attack.commitCounterBarrier();
		} else if (isinferno) {
			int TypeRandom = _random.nextInt(100), Type[] = new int[2];
			if(TypeRandom >=95) {
				Type[0] = 17561;
				Type[1] = 4;
			}else if(TypeRandom >=90) {
				Type[0] = 17563;
				Type[1] = 3;
			}else if(TypeRandom >=80) {
				Type[0] = 17565;
				Type[1] = 2;
			}else {
				Type[0] = 17567;
				Type[1] = 1;
			}
			attack.actionInferno(Type[0]);
			attack.commitisInferno(Type[1]);
		} else if (isMortalBody) {
			attack.actionMortalBody();
			attack.commit();
			attack.commitMortalBody();
		} else if (isHALPHAS) {
			attack.actionHALPHAS();
			attack.commitisHALPHAS();

			/*
			 * }else if (isLindArmor){ attack.actionLindArmor();
			 * attack.commitLindArmor();
			 */
		} else {
			attack.action();
			attack.commit();
		}
		attack = null;
		setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));

		if (target instanceof L1PcInstance) {
			L1PcInstance targetpc = (L1PcInstance) target;
			if (agrochange(getNpcId())) {
				// int rand = _random.nextInt(100)+1;

				if (targetpc._Immune_aggro != null) {
					_is_IMMUNE_AGGRO = true;
					_hateList.add(targetpc._Immune_aggro, 0);
					_target = targetpc._Immune_aggro;
				}

				if (targetpc._healagro != null) {
					_is_HILL_AGGRO = true;
					_hateList.add(targetpc._healagro, 0);
					_target = targetpc._healagro;
				}

				targetpc._healagro = null;
				targetpc._Immune_aggro = null;
			}
		}
	}

	private static final int agro_no_npcid[] = { 100719, 145684, 45606, 45618, 45650,
			45652, 45653, 45513, 45547, 45654, 45672, 100002, 100006, 4036016,
			4036017, 400016, 400017, 4038000, 4200010, 4200011, 4039000,
			4039006, 4039007, 100340, 100341, 100014, 100013, 100012, 100420,
			100338, 100422, 100717, 100718, 45752, 45753, 100570, 45685, 45649,
			7280193, 45000167, 45601, 45600, 100064, 100063, 45573, 81082,
			45675, 45674, 45625, 45955, 45956, 45957, 45958, 45959, 45960,
			45961, 45962 };

	private boolean agrochange(int id) {
		boolean result = false;
		for (int i = 0; i < agro_no_npcid.length; i++) {
			if (id == agro_no_npcid[i]) {
				return result;
			}
		}
		return true;
	}

	public void searchTargetItem() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
				if (item.getItemOwner() != null) {
					if (_master != null) {
						if (item.getItemOwner().getId() != _master.getId()) {
							continue;
						}
					}
				}
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}
		}
	}

	public void searchItemFromAir() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getItem().getType() == 6 // potion
					|| item.getItem().getType() == 7) { // food
				if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
						setHiddenStatus(HIDDEN_STATUS_NONE);
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_Movedown), true);
						setActionStatus(0);
						Broadcaster.broadcastPacket(this, new S_NPCPack(this),
								true);
						onNpcAI();
						startChat(CHAT_TIMING_HIDE);
						_targetItem = item;
						_targetItemList.add(_targetItem);
					}
				}
			}
		}
	}

	public void searchItemFromGround() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getItemId() == 60253) { // 스콜피온의 어쩌구
				if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
						setHiddenStatus(HIDDEN_STATUS_NONE);
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_SwordWalk), true);
						setActionStatus(0);
						Broadcaster.broadcastPacket(this, new S_NPCPack(this),
								true);
						onNpcAI();
						startChat(CHAT_TIMING_HIDE);
						// _targetItem = item;
						// _targetItemList.add(_targetItem);
					}
				}
			}
		}
	}

	int cnt2 = 0;

	public void onTargetItem() {
		if (_targetItem == null) {
			return;
		}
		if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) {
			pickupTargetItem(_targetItem);
		} else {
			int dir = moveDirection(_targetItem.getMapId(), _targetItem.getX(),
					_targetItem.getY());
			if (dir == -1) {
				cnt2++;
				if (cnt2 > 60) {
					_targetItemList.remove(_targetItem);
					_targetItem = null;
					cnt2 = 0;
				}
			} else {
				boolean tail = World.isThroughObject(getX(), getY(),
						getMapId(), dir);
				// int tmpx =HEADING_TABLE_X[dir];
				// int tmpy =HEADING_TABLE_Y[dir];
				int tmpx = aStar.getXY(dir, true) + getX();
				int tmpy = aStar.getXY(dir, false) + getY();
				boolean obj = World.isMapdynamic(tmpx, tmpy, getMapId());
				boolean door = World.door_to_door(getX(), getY(), getMapId(), dir);
				if (this instanceof L1DollInstance) {
					obj = false;
				}
				if (tail && !obj && !door) {
					setDirectionMove(dir);
				}
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			}
		}
	}

	public void pickupTargetItem(L1ItemInstance targetItem) {
		L1Inventory groundInventory = L1World.getInstance().getInventory(
				targetItem.getX(), targetItem.getY(), targetItem.getMapId());
		L1ItemInstance item = groundInventory.tradeItem(targetItem,
				targetItem.getCount(), getInventory());
		light.turnOnOffLight();
		onGetItem(item);
		_targetItemList.remove(_targetItem);
		_targetItem = null;
		setSleepTime(1000);
	}

	private int random(int lbound, int ubound) {
		if (ubound < 0)
			return (int) ((Math.random() * (ubound - lbound - 1)) + lbound);
		else
			return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}

	private short cnt3 = 0;
	private short cnt4 = 0;

	public boolean noTarget() {
		if (_master != null && _master.getMapId() == getMapId()
				&& getLocation().getTileLineDistance(_master.getLocation()) > 1) {
			int dir = moveDirection(_master.getMapId(), _master.getX(),
					_master.getY());
			if (dir != -1) {
				boolean tail = World.isThroughObject(getX(), getY(),
						getMapId(), dir);
				// int tmpx =HEADING_TABLE_X[dir];
				// int tmpy =HEADING_TABLE_Y[dir];
				int tmpx = aStar.getXY(dir, true) + getX();
				int tmpy = aStar.getXY(dir, false) + getY();
				boolean obj = World.isMapdynamic(tmpx, tmpy, getMapId());
				boolean door = World.door_to_door(getX(), getY(), getMapId(), dir);
				if (this instanceof L1DollInstance) {
					obj = false;
				}
				if (tail && !obj && !door) {
					setDirectionMove(dir);
				}
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			} else {
				return true;
			}
		} else {
			if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
				return true;
			}

			if (aStar != null && _master == null && getPassispeed() > 0
					&& !isRest()) {

				// randomWalk();
				L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
				if (mobGroupInfo == null || mobGroupInfo != null
						&& mobGroupInfo.isLeader(this)) {
					if (_randomMoveDistance > 0) {
						_randomMoveDistance -= 1;
					} else {
						int heading = 0;
						switch (random(0, 5)) {
						case 0:
							_randomMoveDistance = random(5, 10);
							break;
						case 1:
						case 2:
							heading = getMoveState().getHeading() + 1;
							break;
						case 3:
						case 4:
							heading = getMoveState().getHeading() - 1;
							break;
						default:
							heading = random(0, 7);
							break;
						}
						int x = aStar.getXY(heading, true) + getX();
						int y = aStar.getXY(heading, false) + getY();
						int dis = 20;
						if (this instanceof L1MerchantInstance) {
							if (getNpcId() == 70027)// 디오
								dis = 2;
							else
								dis = 5;
						}

						if (!isDistance(x, y, getMapId(), getHomeX(),
								getHomeY(), getMapId(), dis)) {
							heading = calcheading(this, getHomeX(), getHomeY());
							x = aStar.getXY(heading, true) + getX();
							y = aStar.getXY(heading, false) + getY();
						}
						if (getNpcId() == 70848) {
							cnt3++;
							if (cnt3 > 5) {
								heading = random(0, 7);
								x = aStar.getXY(heading, true) + getX();
								y = aStar.getXY(heading, false) + getY();
								cnt4++;
							}
							if (cnt4 > 10) {
								S_SkillSound ss = new S_SkillSound(getId(), 169);
								S_RemoveObject ro = new S_RemoveObject(this);
								for (L1PcInstance pc : L1World.getInstance()
										.getRecognizePlayer(this)) {
									pc.sendPackets(ss);
									pc.sendPackets(ro);
									pc.getNearObjects().removeKnownObject(this);
								}
								teleport(getHomeX(), getHomeY(), getMoveState()
										.getHeading());
								cnt4 = 0;
							}
						}
						boolean tail = World.isThroughObject(getX(), getY(),
								getMapId(), heading);
						boolean obj = World.isMapdynamic(x, y, getMapId());
						boolean door = World.door_to_door(getX(), getY(), getMapId(),
								heading);
						if (this instanceof L1DollInstance) {
							obj = false;
						}
						if (tail && !obj && !door) {
							setDirectionMove(heading);
							if (cnt3 > 0)
								cnt3 = 0;
						}

					}
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));

				} else {
					L1NpcInstance leader = mobGroupInfo.getLeader();
					if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
						int dir = moveDirection(leader.getMapId(),
								leader.getX(), leader.getY());
						if (dir == -1) {
							return true;
						} else {
							boolean tail = World.isThroughObject(getX(),
									getY(), getMapId(), dir);
							// int tmpx =HEADING_TABLE_X[dir];
							// int tmpy =HEADING_TABLE_Y[dir];
							int tmpx = aStar.getXY(dir, true) + getX();
							int tmpy = aStar.getXY(dir, false) + getY();
							boolean obj = World.isMapdynamic(tmpx, tmpy,
									getMapId());
							boolean door = World.door_to_door(getX(), getY(),
									getMapId(), dir);
							if (this instanceof L1DollInstance) {
								obj = false;
							}
							if (tail && !obj && !door)
								setDirectionMove(dir);
							setSleepTime(calcSleepTime(getPassispeed(),
									MOVE_SPEED));
						}
					}
				}
			}
		}
		return false;
	}

	public void onFinalAction(L1PcInstance pc, String s) {
	}

	public void tagertClear() {
		if (_target == null) { 
			return;
		}
		_hateList.remove(_target);
		_target = null;
		// healagro = false;
	}

	public void targetRemove(L1Character target) {
		_hateList.remove(target);
		if (_target != null && _target.equals(target)) {
			_target = null;
		}
	}

	public void allTargetClear() {
		_hateList.clear();
		_dropHateList.clear();
		_target = null;
		_targetItemList.clear();
		_targetItem = null;
		_is_HILL_AGGRO = false;
		_is_IMMUNE_AGGRO = false;
	}

	public void setMaster(L1PcInstance cha) {
		_master = cha;
	}

	public L1Character getMaster() {
		return _master;
	}

	public void onNpcAI() {
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() == 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
			attack = null;
		}
	}

	@Override
	public void onAction(L1PcInstance pc, int adddmg) {
		if (getCurrentHp() == 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage(adddmg);
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
			attack = null;
		}
	}

	// NPC타입 HTML 출력
	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());

		String htmlid = null;
		if (talking == null && getNpcTemplate().get_npcId() != 5000043
				&& getNpcTemplate().get_npcId() != 5000048
				&& !(getNpcId() >= 100425 && getNpcId() <= 100428)) {
			player.sendPackets(new S_SystemMessage("현제 준비 중입니다."), true);
			return;
		}
		/** 하딘 인던 관련. 유리에 by.케인 **/
		if (getNpcTemplate().get_npcId() == 5000038) {
			if (player.getInventory().checkItem(500016))
				htmlid = "j_html00";
		} else if (getNpcTemplate().get_npcId() == 100001) {
			if (player.getLevel() > 65)
				htmlid = "lowlvno";
		} else if (getNpcTemplate().get_npcId() == 5000040) {
			if (getMapId() == 9202)
				htmlid = "id1";
		} else if (getNpcTemplate().get_npcId() == 45000) { // 초보자 도우미
			if (player.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.초보자도우미클릭ディレイ)) {
				// player.sendPackets(new
				// S_SystemMessage(player.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.초보자도우미클릭ディレイ)+"초 뒤에 사용 가능합니다."),
				// true);
				return;
			}
			if (player.getLevel() < 2)
				player.setExp(ExpTable.getExpByLevel(2));
			/*
			 * if(!player.getInventory().checkItem(20126) // 상아탑가죽갑옷 &&
			 * !player.getInventory().checkItem(20028)//상아탑가죽투구 &&
			 * !player.getInventory().checkItem(20173)//상아탑가죽장갑 &&
			 * !player.getInventory().checkItem(20206)//상아탑가죽샌달 &&
			 * !player.getInventory().checkItem(21098) //상아탑망토 //&&
			 * !player.getInventory().checkItem(40029)//상아탑물약 &&
			 * !player.getInventory().checkItem(40098)){//상아탑확인
			 * if(player.isCrown() || player.isKnight() ||
			 * player.isDragonknight()){ player.sendPackets(new
			 * S_ServerMessage(143, getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(35, 1).getName()), true);//한손검
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(48,
			 * 1).getName()), true);//양손검 if(player.isDragonknight())
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(147,
			 * 1).getName()), true);//도끼 }else if(player.isWizard()){
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(120,
			 * 1).getName()), true);//지팡이 }else if(player.isDarkelf()){
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(73,
			 * 1).getName()), true);//이도류 player.sendPackets(new
			 * S_ServerMessage(143, getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(156, 1).getName()), true);//크로우
			 * }else if(player.isElf()){ player.sendPackets(new
			 * S_ServerMessage(143, getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(174, 1).getName()), true);//석궁
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(175,
			 * 1).getName()), true);//활 }else if(player.isIllusionist()){
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(), player.getInventory().storeItem(147,
			 * 1).getName()), true);//도끼 player.sendPackets(new
			 * S_ServerMessage(143, getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(120, 1).getName()), true);//지팡이 }
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(20126, 1).getName()), true);
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(20028, 1).getName()), true);
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(20173, 1).getName()), true);
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(20206, 1).getName()), true);
			 * player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(21098, 1).getName()), true);
			 * //player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(40029, 1).getName()+" (50)"),
			 * true); player.sendPackets(new S_ServerMessage(143,
			 * getNpcTemplate().get_name(),
			 * player.getInventory().storeItem(40098, 10).getName()+" (10)"),
			 * true);
			 */
			htmlid = "newtutor1";
			/*
			 * }else{ if(player.getLevel() >= 20) htmlid = "tutorrw1"; else
			 * if(player.getLevel() >= 15) htmlid = "tutorrw2"; else
			 * if(player.getLevel() >= 5) htmlid = "tutorrw3"; else htmlid =
			 * "tutorrw4"; }
			 */
			if (player.getHasteItemEquipped() > 0) {
			} else {
				if (player.getSkillEffectTimerSet().hasSkillEffect(HASTE)) {
					player.getSkillEffectTimerSet().killSkillEffectTimer(HASTE);
					player.sendPackets(new S_SkillHaste(player.getId(), 0, 0),
							true);
					Broadcaster.broadcastPacket(player,
							new S_SkillHaste(player.getId(), 0, 0), true);
					player.getMoveState().setMoveSpeed(0);
				} else if (player.getSkillEffectTimerSet().hasSkillEffect(
						GREATER_HASTE)) {
					player.getSkillEffectTimerSet().killSkillEffectTimer(
							GREATER_HASTE);
					player.sendPackets(new S_SkillHaste(player.getId(), 0, 0),
							true);
					Broadcaster.broadcastPacket(player,
							new S_SkillHaste(player.getId(), 0, 0), true);
					player.getMoveState().setMoveSpeed(0);
				} else if (player.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_HASTE)) {
					player.getSkillEffectTimerSet().killSkillEffectTimer(
							STATUS_HASTE);
					player.sendPackets(new S_SkillHaste(player.getId(), 0, 0),
							true);
					Broadcaster.broadcastPacket(player,
							new S_SkillHaste(player.getId(), 0, 0), true);
					player.getMoveState().setMoveSpeed(0);
				}
				player.sendPackets(new S_SkillSound(player.getId(), 755), true);
				Broadcaster.broadcastPacket(player,
						new S_SkillSound(player.getId(), 755), true);
				player.sendPackets(new S_SkillHaste(player.getId(), 1, 1800),
						true);
				Broadcaster.broadcastPacket(player,
						new S_SkillHaste(player.getId(), 1, 0), true);
				player.getMoveState().setMoveSpeed(1);
				player.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_HASTE, 1800 * 1000);
			}
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.sendPackets(new S_SkillSound(player.getId(), 830), true);
			player.sendPackets(
					new S_HPUpdate(player.getCurrentHp(), player.getMaxHp()),
					true);
			player.sendPackets(
					new S_MPUpdate(player.getCurrentMp(), player.getMaxMp()),
					true);
			player.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.초보자도우미클릭ディレイ, 1000);
		}

		// html 표시 패킷 송신
		if (htmlid != null) { // htmlid가 지정되고 있는 경우
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid), true);
		} else {
			if (player.getLawful() < -1000) { // 플레이어가 카오틱
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 2), true);
			} else {
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 1), true);
			}
		}
	}

	public void refineItem() {
		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		if (_npcTemplate.get_npcId() == 45032) {
			if (getExp() != 0 && !_inventory.checkItem(20)) {
				materials = new int[] { 40508, 40521, 40045 };
				counts = new int[] { 150, 3, 3 };
				createitem = new int[] { 20 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			if (getExp() != 0 && !_inventory.checkItem(19)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 150, 3 };
				createitem = new int[] { 19 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			if (getExp() != 0 && !_inventory.checkItem(3)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 50, 1 };
				createitem = new int[] { 3 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			if (getExp() != 0 && !_inventory.checkItem(100)) {
				materials = new int[] { 88, 40508, 40045 };
				counts = new int[] { 4, 80, 3 };
				createitem = new int[] { 100 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			if (getExp() != 0 && !_inventory.checkItem(89)) {
				materials = new int[] { 88, 40494 };
				counts = new int[] { 2, 80 };
				createitem = new int[] { 89 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					L1ItemInstance item = null;
					for (int j = 0; j < createitem.length; j++) {
						item = _inventory.storeItem(createitem[j],
								createcount[j]);
						if (getNpcTemplate().get_digestitem() > 0) {
							setDigestItem(item);
						}
					}
				}
			}
		} else if (_npcTemplate.get_npcId() == 81069) {
			if (getExp() != 0 && !_inventory.checkItem(40542)) {
				materials = new int[] { 40032 };
				counts = new int[] { 1 };
				createitem = new int[] { 40542 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		}
		materials = null;
		counts = null;
		createitem = null;
		createcount = null;
	}

	public void setParalysisTime(int ptime) {
		_paralysisTime = ptime;
	}

	public void set펌프(int ptime) {
		펌프킨타임 = ptime;
	}

	public void set템프(int ptime) {
		템프펌프킨 = ptime;
	}

	public L1HateList getHateList() {
		return _hateList;
	}

	public int getParalysisTime() {
		return _paralysisTime;
	}
	/**팬텀R*/
	public void setPhantomRTime(int PhantomRTime) {
		_PhantomRTime = PhantomRTime;
	}
	
	public int getPhantomRTime() {
		return _PhantomRTime;
	}
	/**팬텀R*/
	
	/**팬텀D*/
	public void setPhantomDTime(int PhantomDTime) {
		_PhantomDTime = PhantomDTime;
	}
	
	public int getPhantomDTime() {
		return _PhantomDTime;
	}
	/**팬텀D*/

	public final void startHpRegeneration() {
		int hprInterval = getNpcTemplate().get_hprinterval();
		int hpr = getNpcTemplate().get_hpr();
		if (!_hprRunning && hprInterval > 0 && hpr > 0) {
			_hprRunning = true;
			try {
				_hprTimer = new HprTimer(hpr);
				// L1NpcRegenerationTimer.getInstance().(_hprTimer, hprInterval,
				// hprInterval);
				GeneralThreadPool.getInstance()
						.schedule(_hprTimer, hprInterval);
			} catch (Exception e) {
				_hprRunning = false;
			}
		}
	}

	public final void stopHpRegeneration() {
		if (_hprRunning) {
			_hprRunning = false;
			_hprTimer = null;
		}
	}

	public final void startMpRegeneration() {
		int mprInterval = getNpcTemplate().get_mprinterval();
		int mpr = getNpcTemplate().get_mpr();
		if (!_mprRunning && mprInterval > 0 && mpr > 0) {
			_mprRunning = true;
			try {
				_mprTimer = new MprTimer(mpr);
				GeneralThreadPool.getInstance()
						.schedule(_mprTimer, mprInterval);
				// L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_mprTimer,
				// mprInterval, mprInterval);
			} catch (Exception e) {
			}
		}
	}

	public final void stopMpRegeneration() {
		if (_mprRunning) {
			// _mprTimer.cancel();
			_mprRunning = false;
			_mprTimer = null;
		}
	}

	private boolean _hprRunning = false;

	private HprTimer _hprTimer;

	class HprTimer implements Runnable {// extends TimerTask {
		private final int _point;

		public HprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		@Override
		public void run() {
			try {
				if (!_hprRunning)
					return;

				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentHp() < getMaxHp())) {
					setCurrentHp(getCurrentHp() + _point);
					GeneralThreadPool.getInstance().schedule(this,
							getNpcTemplate().get_hprinterval());
				} else {
					_hprRunning = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				_hprRunning = false;
			}
		}
	}

	private boolean _mprRunning = false;

	private MprTimer _mprTimer;

	class MprTimer implements Runnable {// extends TimerTask {
		@Override
		public void run() {
			try {
				if (!_mprRunning)
					return;

				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentMp() < getMaxMp())) {
					setCurrentMp(getCurrentMp() + _point);
					GeneralThreadPool.getInstance().schedule(this,
							getNpcTemplate().get_mprinterval());
				} else {
					_mprRunning = false;
					// cancel();
				}
			} catch (Exception e) {
				// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				e.printStackTrace();
			}
		}

		public MprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		private final int _point;
	}

	class DigestItemTimer implements Runnable {

		public void run() {
			_digestItemRunning = true;
			Object[] keys = null;
			L1ItemInstance digestItem = null;
			while (!_destroyed && _digestItems.size() > 0) {
				try {
					Thread.sleep(1000);

					keys = _digestItems.keySet().toArray();
					Integer key = null;
					Integer digestCounter = null;
					for (int i = 0; i < keys.length; i++) {
						key = (Integer) keys[i];
						digestCounter = _digestItems.get(key);
						digestCounter -= 1;
						if (digestCounter <= 0) {
							_digestItems.remove(key);
							digestItem = getInventory().getItem(key);
							if (digestItem != null) {
								getInventory().removeItem(digestItem,
										digestItem.getCount());
							}
						} else {
							_digestItems.put(key, digestCounter);
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
					break;
				}
			}

			_digestItemRunning = false;
			keys = null;
		}
	}

	private int _passispeed;
	private int _atkspeed;
	private boolean _pickupItem;

	public int getPassispeed() {
		return _passispeed;
	}

	public void setPassispeed(int i) {
		_passispeed = i;
	}

	public int getAtkspeed() {
		return _atkspeed;
	}

	public void setAtkspeed(int i) {
		_atkspeed = i;
	}

	public boolean isPickupItem() {
		return _pickupItem;
	}

	public void setPickupItem(boolean flag) {
		_pickupItem = flag;
	}

	@Override
	public L1Inventory getInventory() {
		return _inventory;
	}

	public void setInventory(L1Inventory inventory) {
		_inventory = inventory;
	}

	public L1Npc getNpcTemplate() {
		return _npcTemplate;
	}

	public int getNpcId() {
		return _npcTemplate.get_npcId();
	}

	public void setPetcost(int i) {
		_petcost = i;
	}

	public int getPetcost() {
		return _petcost;
	}

	public void setSpawn(L1Spawn spawn) {
		_spawn = spawn;
	}

	public L1Spawn getSpawn() {
		return _spawn;
	}

	public void setSpawnNumber(int number) {
		_spawnNumber = number;
	}

	public int getSpawnNumber() {
		return _spawnNumber;
	}

	public void onDecay(boolean isReuseId) {
		int id = 0;
		if (isReuseId) {
			id = getId();
		} else {
			id = 0;
		}
		_spawn.executeSpawnTask(_spawnNumber, id);
	}

	public void NPCchat(String msg, int type) {
		Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, msg, type),
				true);
	}

	private void BMSG(L1PcInstance pc, int id, String msg, int x, int y) {
		pc.sendPackets(new S_NpcChatPacket(id, msg, x, y), true);
	}

	private boolean fdcheck(L1NpcInstance npc, L1PcInstance pc) {
		if (pc == null)
			return false;
		if (pc.getNetConnection() == null)
			return false;
		if (pc.getNetConnection().isClosed())
			return false;
		if (pc.getMapId() != npc.getMapId()) {
			return false;
		}
		return true;
	}

	public void Bchat_start(L1PcInstance pc) {
		GeneralThreadPool.getInstance().execute(new Start_bchat(this, pc));
	}

	class Start_bchat implements Runnable {
		L1NpcInstance _npc = null;
		L1PcInstance _pc = null;

		public Start_bchat(L1NpcInstance npc, L1PcInstance pc) {
			_npc = npc;
			_pc = pc;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18861", _npc.getX(), _npc.getY());
				Thread.sleep(3000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _pc.getId(), "$18862", _pc.getX(), _pc.getY());
				Thread.sleep(3000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18863", _npc.getX(), _npc.getY());
				Thread.sleep(3000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _pc.getId(), "$18864", _pc.getX(), _pc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18865", _npc.getX(), _npc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _pc.getId(), "$18866", _pc.getX(), _pc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18867", _npc.getX(), _npc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18868", _npc.getX(), _npc.getY());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void Wchat_start(L1PcInstance pc) {
		GeneralThreadPool.getInstance().execute(new Start_wchat(this, pc));
	}

	class Start_wchat implements Runnable {
		L1NpcInstance _npc = null;
		L1PcInstance _pc = null;

		public Start_wchat(L1NpcInstance npc, L1PcInstance pc) {
			_npc = npc;
			_pc = pc;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18645", _npc.getX(), _npc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18646", _npc.getX(), _npc.getY());
				Thread.sleep(4000);
				if (!fdcheck(_npc, _pc))
					return;
				BMSG(_pc, _npc.getId(), "$18647", _npc.getX(), _npc.getY());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private Start_vchat _vc = null;

	public void vchat_start(L1PcInstance pc) {
		if (_vc == null) {
			_vc = new Start_vchat(this, pc);
		} else {
			_vc.on = false;
			_vc = new Start_vchat(this, pc);
		}
		GeneralThreadPool.getInstance().execute(_vc);
	}

	public void vchat_exit() {
		if (_vc == null) {
			return;
		}
		_vc.on = false;
	}

	/*
	 * 발라카스:누가 나를 깨우는가?18869 데스나이트:발라카스! 드디어 너를 만나게 되는구나..18870 발라카스:나의 잠을 깨운
	 * 댓가는..나의 노예가 되어 평생 갚게 되리라..18871 데스나이트:그런 말은 내가 패배했을 때 해도 늦지 않는다.18872
	 * 발라카스:크크..자신감이 넘치는구나..18873 발라카스:죽어라 인간!18874
	 */
	class Start_vchat implements Runnable {
		L1NpcInstance _npc = null;
		L1PcInstance _pc = null;
		boolean on = true;

		public Start_vchat(L1NpcInstance npc, L1PcInstance pc) {
			_npc = npc;
			_pc = pc;
		}

		@Override
		public void run() {
			while (on) {
				try {
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _npc.getId(), "$18869", _npc.getX(), _npc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _pc.getId(), "$18870", _pc.getX(), _pc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _npc.getId(), "$18871", _npc.getX(), _npc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _pc.getId(), "$18872", _pc.getX(), _pc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _npc.getId(), "$18873", _npc.getX(), _npc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					BMSG(_pc, _npc.getId(), "$18874", _npc.getX(), _npc.getY());
					Thread.sleep(2000);
					if (!fdcheck(_npc, _pc)) {
						on = false;
						return;
					}
					on = false;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		/** 화살 위치 객체 표시 안되게. by. 케인 **/
		try {
			if (getNpcTemplate().get_npcId() != 5000092
					&& getNpcTemplate().get_npcId() != 100087) {
				perceivedFrom.sendPackets(new S_NPCPack(this), true);
				if (getNpcId() == 100282 || getNpcId() == 100807) {
					perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 3),
							true);
				}
			}
			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.TRUE_TARGET)) {
				if (tt_clanid == perceivedFrom.getClanid()
						|| tt_partyid == perceivedFrom.getPartyID()) {
					perceivedFrom.sendPackets(new S_PacketBox(
							S_PacketBox.IMAGE_SPAWN, getId(), 12299, true));
				}
			}
		} catch (Exception e) {
		}
		onNpcAI();
	}

	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		try {
			if (aStar != null) {
				aStar.clear();
			}
			aStar = null;
		} catch (Exception e) {
			// e.printStackTrace();
		}

		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(
				this);
		if (players.size() > 0) {
			S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
			for (L1PcInstance pc : players) {
				if (pc != null) {
					pc.getNearObjects().removeKnownObject(this);
					// if(!L1Character.distancepc(user, this))
					pc.sendPackets(s_deleteNewObject);
				}
			}
		}

		getNearObjects().removeAllKnownObjects();

		try {
			mobSkill = null;
			iPath = null;
			if (tail != null)
				tail.clear();
			tail = null;
			if (marble != null && marble.size() > 0)
				marble.clear();
			marble = null;
			if (marble2 != null && marble2.size() > 0)
				marble2.clear();
			marble2 = null;
			if (tro != null && tro.size() > 0)
				tro.clear();
			tro = null;
		} catch (Exception e) {
		}
		getSkillEffectTimerSet().clearSkillEffectTimer();

		L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
		if (mobGroupInfo == null) {
			if (isReSpawn() && getNpcTemplate().get_npcId() != 100859) {
				onDecay(true);
			}
		} else {
			if (mobGroupInfo.removeMember(this) == 0) {
				setMobGroupInfo(null);
				if (isReSpawn() && getNpcTemplate().get_npcId() != 100859) {
					onDecay(false);
				}
			}
		}
	}

	public void groupDeleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(
				this);
		if (players.size() > 0) {
			S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
			for (L1PcInstance pc : players) {
				if (pc != null) {
					pc.getNearObjects().removeKnownObject(this);
					// if(!L1Character.distancepc(user, this))
					pc.sendPackets(s_deleteNewObject);
				}
			}
		}
		getNearObjects().removeAllKnownObjects();
	}

	public void ReceiveManaDamage(L1Character attacker, int damageMp) {
	}

	public void receiveDamage(L1Character attacker, int damage) {
	}

	public void setDigestItem(L1ItemInstance item) {
		if (item == null)
			return;
		_digestItems.put(new Integer(item.getId()), new Integer(
				getNpcTemplate().get_digestitem()));
		if (!_digestItemRunning) {
			DigestItemTimer digestItemTimer = new DigestItemTimer();
			GeneralThreadPool.getInstance().execute(digestItemTimer);
		}
	}

	public void onGetItem(L1ItemInstance item) {
		refineItem();
		getInventory().shuffle();
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
	}

	public void approachPlayer(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(60)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(97)) {
			return;
		}
		if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
					appearOnGround(pc);
				}
			} else {
				if (getNpcId() == 100420) // 샌드웜
					searchItemFromGround();
			}
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
					appearOnGround(pc);
				}
			} else {
				// if (getNpcTemplate().get_npcId() != 45681) {
				searchItemFromAir();
				// }
			}
		}

	}

	public void appearOnGround(L1Character pc) {
		if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
			setActionStatus(0);
			Broadcaster.broadcastPacket(this, new S_NPCPack(this), true);
			if (!pc.getSkillEffectTimerSet().hasSkillEffect(60) && !pc.getSkillEffectTimerSet().hasSkillEffect(97)) {
				if (pc instanceof L1PcInstance && ((L1PcInstance) pc).isGm()) {
				} else {
					_hateList.add(pc, 0);
					_target = pc;
				}
			}
			onNpcAI();
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Movedown), true);
			setActionStatus(0);
			Broadcaster.broadcastPacket(this, new S_NPCPack(this), true);
			if (!pc.getSkillEffectTimerSet().hasSkillEffect(60)
					&& !pc.getSkillEffectTimerSet().hasSkillEffect(97)) {
				if (pc instanceof L1PcInstance && ((L1PcInstance) pc).isGm()) {
				} else {
					_hateList.add(pc, 0);
					_target = pc;
				}
			}
			onNpcAI();
			startChat(CHAT_TIMING_HIDE);
		}
	}

	public void setDirectionMove(int dir) {
		if (dir < 8 && dir >= 0) {
			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DESPERADO)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POWER_GRIP)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DEMOLITION)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ETERNITY)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_TAB)
					|| isPhantomRippered() || isPhantomDeathed()) {
				return;
			}
			int nx = 0;
			int ny = 0;

			int heading = 0;
			nx = HEADING_TABLE_X[dir];
			ny = HEADING_TABLE_Y[dir];
			heading = dir;
			int nnx = getX() + nx;
			int nny = getY() + ny;

			getMoveState().setHeading(heading);

			L1World.getInstance().Move(this, nnx, nny);

			if (!(this instanceof L1DollInstance)
			/* &&!(this instanceof L1ArrowInstatance) */) {
				getMap().setPassable(getLocation(), true);
			}

			setX(nnx);
			setY(nny);
			if (!(this instanceof L1DollInstance)
			/* &&!(this instanceof L1ArrowInstatance) */) {
				getMap().setPassable(nnx, nny, false);
			}

			updateObject();

			Broadcaster.broadcastPacket(this, new S_MoveCharPacket(this));

			if (getMovementDistance() > 0) {
				if (this instanceof L1GuardianInstance
						|| this instanceof L1GuardInstance
						|| this instanceof L1CastleGuardInstance
						|| this instanceof L1MerchantInstance
						|| this instanceof L1MonsterInstance) {
					if (this instanceof L1GuardianInstance
							&& (getMapId() == 4 || getMapId() == 0)) {// 가디언
						if (getLocation().getLineDistance(
								new Point(getHomeX(), getHomeY())) > 20) {
							teleport(getHomeX(), getHomeY(), getMoveState()
									.getHeading());
						}
					} else if (getLocation().getLineDistance(
							new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
						teleport(getHomeX(), getHomeY(), getMoveState()
								.getHeading());
					}
				}
			} else {
				// 필드 몬스터 40칸 이상 갈시에 제자리 텔
				if ((this instanceof L1MonsterInstance)
						&& (getMapId() == 4 || getMapId() == 0)
						&& getNpcId() != 100420) {// 샌드웜
					if (getLocation().getLineDistance(
							new Point(getHomeX(), getHomeY())) > (getNpcId() == 100338 ? 60
							: 40)) {
						teleport(getHomeX(), getHomeY(), getMoveState()
								.getHeading());
					}
				} else if (this instanceof L1GuardianInstance
						&& (getMapId() == 4 || getMapId() == 0)) {// 가디언
					if (getLocation().getLineDistance(
							new Point(getHomeX(), getHomeY())) > 20) {
						teleport(getHomeX(), getHomeY(), getMoveState()
								.getHeading());
					}
				}
			}
			if (getNpcTemplate().get_npcId() >= 45912
					&& getNpcTemplate().get_npcId() <= 45916) {
				if (getX() >= 32591 && getX() <= 32644 && getY() >= 32643
						&& getY() <= 32688 && getMapId() == 4) {
					teleport(getHomeX(), getHomeY(), getMoveState()
							.getHeading());
				}
			}
		}
	}

	boolean updateCk = false;

	public void updateObject() {
		if (updateCk)
			return;
		updateCk = true;
		try {
			for (L1Object known : getNearObjects().getKnownObjects()) {
				if (known == null) {
					continue;
				}
				if (Config.PC_RECOGNIZE_RANGE == -1) {
					if (!getLocation().isInScreen(known.getLocation())) {
						getNearObjects().removeKnownObject(known);
						if (known instanceof L1Character) {
							if (((L1Character) known).getNearObjects()
									.knownsObject(this)) {
								((L1Character) known).getNearObjects()
										.removeKnownObject(this);
								if (known instanceof L1PcInstance)
									((L1PcInstance) known)
											.sendPackets(new S_RemoveObject(
													this));
							}
						}
					}
				} else {
					if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
						getNearObjects().removeKnownObject(known);
						if (known instanceof L1Character) {
							if (((L1Character) known).getNearObjects()
									.knownsObject(this)) {
								((L1Character) known).getNearObjects()
										.removeKnownObject(this);
								if (known instanceof L1PcInstance)
									((L1PcInstance) known)
											.sendPackets(new S_RemoveObject(
													this));
							}
						}
					}
				}
			}

			for (L1Object visible : L1World.getInstance().getVisibleObjects(
					this, Config.PC_RECOGNIZE_RANGE)) {
				if (visible instanceof L1PcInstance) {
					if (!((L1PcInstance) visible).getNearObjects()
							.knownsObject(this))
						this.onPerceive(((L1PcInstance) visible));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			updateCk = false;
		}
	}

	public int EscapeDirection(int h, int x, int y) {
		if (h < 0 || h > 7) {
			return -1;
		}
		switch (h) {
		case 0:
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 1;
					}
				} else {
					return 5;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 2)) {
							return 2;
						}
					} else {
						return 2;
					}
				}
			} else {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 6)) {
							return 6;
						}
					} else {
						return 6;
					}
				}
			}
			break;
		case 1:
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 5;
					}
				} else {
					return 5;
				}
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 7)) {
							return 7;
						}
					} else {
						return 7;
					}
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 3)) {
							return 3;
						}
					} else {
						return 3;
					}
				}
			}
			break;
		case 2:
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 5;
					}
				} else {
					return 5;
				}
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 4)) {
							return 4;
						}
					} else {
						return 4;
					}
				}
			} else {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 0)) {
							return 0;
						}
					} else {
						return 0;
					}
				}
			}
			break;
		case 3:
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 5)) {
							return 5;
						}
					} else {
						return 5;
					}
				}
			} else {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 1)) {
							return 1;
						}
					} else {
						return 1;
					}
				}
			}
			break;
		case 4:
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 6)) {
							return 6;
						}
					} else {
						return 6;
					}
				}
			} else {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 2)) {
							return 2;
						}
					} else {
						return 2;
					}
				}
			}
			break;
		case 5:
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 7)) {
							return 7;
						}
					} else {
						return 7;
					}
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 3)) {
							return 3;
						}
					} else {
						return 3;
					}
				}
			}
			break;
		case 6:
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 0)) {
							return 0;
						}
					} else {
						return 0;
					}
				}
			} else {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 4)) {
							return 4;
						}
					} else {
						return 4;
					}
				}
			}
			break;
		case 7:
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 1)) {
							return 1;
						}
					} else {
						return 1;
					}
				}
			} else {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 5)) {
							return 5;
						}
					} else {
						return 5;
					}
				}
			}
			break;
		}

		return -1;
	}

	public int moveDirection(int mapid, int x, int y) {
		return moveDirection(mapid, x, y,
				getLocation().getLineDistance(new Point(x, y)));
	}

	public int 체크(int x, int y, int h) {
		switch (h) {
		case 0:
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 6)) {
							return 6;
						}
					} else {
						return 6;
					}
				}
			} else {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 2)) {
							return 2;
						}
					} else {
						return 2;
					}
				}
			}
			break;
		case 1:
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 7)) {
							return 7;
						}
					} else {
						return 7;
					}
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 3)) {
							return 3;
						}
					} else {
						return 3;
					}
				}
			}
			break;
		case 2:
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 1)) {
						return 1;
					}
				} else {
					return 1;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 4)) {
							return 4;
						}
					} else {
						return 4;
					}
				}
			} else {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 0)) {
							return 0;
						}
					} else {
						return 0;
					}
				}
			}
			break;
		case 3:
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 2)) {
						return 2;
					}
				} else {
					return 2;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 5)) {
							return 5;
						}
					} else {
						return 5;
					}
				}
			} else {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 1)) {
							return 1;
						}
					} else {
						return 1;
					}
				}
			}
			break;
		case 4:
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 5;
					}
				} else {
					return 5;
				}
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 3)) {
						return 3;
					}
				} else {
					return 3;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 6)) {
							return 6;
						}
					} else {
						return 6;
					}
				}
			} else {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 2)) {
							return 2;
						}
					} else {
						return 2;
					}
				}
			}
			break;
		case 5:
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 5;
					}
				} else {
					return 5;
				}
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 4)) {
						return 4;
					}
				} else {
					return 4;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 7)) {
							return 7;
						}
					} else {
						return 7;
					}
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 3)) {
							return 3;
						}
					} else {
						return 3;
					}
				}
			}
			break;
		case 6:
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 5)) {
						return 5;
					}
				} else {
					return 5;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 0)) {
							return 0;
						}
					} else {
						return 0;
					}
				}
			} else {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 4)) {
							return 4;
						}
					} else {
						return 4;
					}
				}
			}
			break;
		case 7:
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 7)) {
						return 7;
					}
				} else {
					return 7;
				}
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 0)) {
						return 0;
					}
				} else {
					return 0;
				}
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!(this instanceof L1DollInstance)) {
					if (오브젝트체크(x, y, 6)) {
						return 6;
					}
				} else {
					return 6;
				}
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 1)) {
							return 1;
						}
					} else {
						return 1;
					}
				}
			} else {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!(this instanceof L1DollInstance)) {
						if (오브젝트체크(x, y, 5)) {
							return 5;
						}
					} else {
						return 5;
					}
				}
			}
			break;
		}

		return -1;
	}

	public int moveDirection(int mapid, int x, int y, double d) {
		int dir = 0;
		int calcx = getX() - x;
		int calcy = getY() - y;
		/*
		 * if(getNpcTemplate().get_npcId() >=100750 &&
		 * getNpcTemplate().get_npcId() <= 100757){ dir =
		 * CharPosUtil.targetDirection(this, x, y); dir = 체크(getX(), getY(),
		 * dir); return dir; }
		 */
		if (this.getMapId() != mapid || Math.abs(calcx) > 30
				|| Math.abs(calcy) > 30) {
			allTargetClear();
			return -1;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(40) == true && d >= 2D) {
			return -1;
		} else if (d > 30D) {
			return -1;
		} else if (d > courceRange) {
			dir = CharPosUtil.targetDirection(this, x, y);
			dir = 체크(getX(), getY(), dir);
		} else {
			// if(this instanceof L1DollInstance
			// ||this instanceof L1SummonInstance
			// ||this instanceof L1MonsterInstance
			// ||this instanceof L1PetInstance
			// ||this instanceof L1FollowerInstance){

			dir = _astar(x, y, mapid);
			// broadcastPacket(new S_NpcChatPacket(this, "내 dir 은 몇일까 "+dir));
			// }else{
			// dir = CharPosUtil.targetDirection(this, x, y);
			// dir = 체크(getX(), getY(), dir);
			// }
			/*
			 * if (dir == -1) { dir = CharPosUtil.targetDirection(this, x, y);
			 * if (!isExsistCharacterBetweenTarget(dir)) { dir = 체크(getX(),
			 * getY(), dir); } }
			 */
		}
		return dir;
	}

	/*
	 * private boolean isExsistCharacterBetweenTarget(int dir) { if (!(this
	 * instanceof L1MonsterInstance)) { return false; } if (_target == null) {
	 * return false; }
	 * 
	 * int locX = getX(); int locY = getY(); int targetX = locX; int targetY =
	 * locY;
	 * 
	 * switch(dir){ case 1: targetX = locX + 1; targetY = locY - 1; break; case
	 * 2: targetX = locX + 1; break; case 3: targetX = locX + 1; targetY = locY
	 * + 1; break; case 4: targetY = locY + 1; break; case 5: targetX = locX -
	 * 1; targetY = locY + 1; break; case 6: targetX = locX - 1; break; case 7:
	 * targetX = locX - 1; targetY = locY - 1; break; case 0: targetY = locY -
	 * 1; break; default: break; } L1Character cha = null; L1PcInstance pc =
	 * null;
	 * 
	 * for (L1Object object : L1World.getInstance().getVisibleObjects(this, 1))
	 * { if (object instanceof L1PcInstance || object instanceof
	 * L1SummonInstance || object instanceof L1PetInstance) { cha =
	 * (L1Character) object; if (cha.getX() == targetX && cha.getY() == targetY
	 * && cha.getMapId() == getMapId()) { if (object instanceof L1PcInstance) {
	 * pc = (L1PcInstance) object; if (pc.isGhost()) { continue; } }
	 * _hateList.add(cha, 0); _target = cha; return true; } } } return false; }
	 */

	public int targetReverseDirection(int tx, int ty) {
		int dir = CharPosUtil.targetDirection(this, tx, ty);
		dir += 4;
		if (dir > 7) {
			dir -= 8;
		}
		return dir;
	}

	public int checkObject(int x, int y, short m, int d) {
		L1Map map = L1WorldMap.getInstance().getMap(m);
		switch (d) {
		case 1:
			if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			}
			break;
		case 2:
			if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			}
			break;
		case 3:
			if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			}
			break;
		case 4:
			if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			}
			break;
		case 5:
			if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			}
			break;
		case 6:
			if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			}
			break;
		case 7:
			if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			}
			break;
		case 0:
			if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			}
			break;
		default:
			break;
		}
		return -1;
	}

	private boolean 벽체크(int x, int y, int h) {
		if (this.getNpcId() == 45617 || this.getNpcId() == 45529) {
			return true;
		}
		/*
		 * if(getNpcTemplate().get_npcId() >=100750 &&
		 * getNpcTemplate().get_npcId() <= 100757){ return true; }
		 */
		return World.isThroughObject(x, y, getMapId(), h);
	}

	private boolean 문체크(int x, int y, int h) {
		/*
		 * if(getNpcTemplate().get_npcId() >=100750 &&
		 * getNpcTemplate().get_npcId() <= 100757){ return true; }
		 */
		return !World.door_to_door(x, y, getMapId(), h);
	}

	private boolean 오브젝트체크(int x, int y, int h) {
		try {
			int cx = x + HEADING_TABLE_X[h];
			int cy = y + HEADING_TABLE_Y[h];
			return !World.isMapdynamic(cx, cy, getMapId());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * private void _moveLocation(int[] ary, int d) { ary[0] = ary[0] +
	 * HEADING_TABLE_X[d]; ary[1] = ary[1] + HEADING_TABLE_Y[d]; ary[2] = d; }
	 */

	private int _astar(int x, int y, int m) {
		int dir = 0;
		try {
			aStar.cleanTail();
			tail = aStar.searchTail(this, x, y, m, true);

			try {
				if (tail != null) {
					iCurrentPath = -1;
					while (tail != null) {
						if (tail.x == getX() && tail.y == getY()) {
							// 현재위치 라면 종료
							break;
						}
						if (iCurrentPath >= 299) {
							return -1;
						}
						if (_destroyed || isDead()) {
							return -1;
						}
						iPath[++iCurrentPath][0] = tail.x;
						iPath[iCurrentPath][1] = tail.y;
						tail = tail.prev;

					}
					if (this.getNpcId() == 45617 || this.getNpcId() == 45529) {
						return aStar.calcheading(getX(), getY(), x, y);
						// }else if(this.getNpcId() >= 100750 || this.getNpcId()
						// <= 100757){
						// return aStar.calcheading(getX(), getY(), x, y);
					} else if (iCurrentPath != -1) {
						return aStar.calcheading(getX(), getY(),
								iPath[iCurrentPath][0], iPath[iCurrentPath][1]);
					} else {
						return -1;
					}
				} else {
					aStar.cleanTail();
					tail = aStar.close_up_search(this, x, y, m, true);

					if (tail != null && !(tail.x == getX() && tail.y == getY())) {
						iCurrentPath = -1;
						while (tail != null) {
							if (tail.x == getX() && tail.y == getY()) {
								// 현재위치 라면 종료
								break;
							}
							if (iCurrentPath >= 299) {
								return -1;
							}
							if (_destroyed || isDead()) {
								return -1;
							}
							iPath[++iCurrentPath][0] = tail.x;
							iPath[iCurrentPath][1] = tail.y;
							tail = tail.prev;
						}
						if (this.getNpcId() == 45617
								|| this.getNpcId() == 45529) {
							return aStar.calcheading(getX(), getY(), x, y);
							// }else if(this.getNpcId() >= 100750 ||
							// this.getNpcId() <= 100757){
							// return aStar.calcheading(getX(), getY(), x, y);
						} else if (iCurrentPath != -1) {
							return aStar.calcheading(getX(), getY(),
									iPath[iCurrentPath][0],
									iPath[iCurrentPath][1]);
						} else {
							dir = -1;
						}
					} else {
						if (this.getNpcId() == 45617
								|| this.getNpcId() == 45529) {
							return aStar.calcheading(getX(), getY(), x, y);
							// }else if(this.getNpcId() >= 100750 ||
							// this.getNpcId() <= 100757){
							// return aStar.calcheading(getX(), getY(), x, y);
						}
						dir = -1;
						int chdir = calcheading(this, x, y);
						if (getMoveState().getHeading() != chdir) {
							this.getMoveState().setHeading(
									calcheading(this, x, y));
							Broadcaster.broadcastPacket(this,
									new S_ChangeHeading(this));
						}
					}

					return dir;
				}
			} catch (Exception e) {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}

	}

	/*
	 * private int _serchCource(int x, int y) { int i; int locCenter =
	 * courceRange + 1; int diff_x = x - locCenter; int diff_y = y - locCenter;
	 * 
	 * int[] locBace = { getX() - diff_x, getY() - diff_y, 0, 0 }; int[] locNext
	 * = new int[4]; int[] locCopy; int[] dirFront = new int[5]; boolean
	 * serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
	 * LinkedList<int[]> queueSerch = new LinkedList<int[]>();
	 * 
	 * for (int j = courceRange * 2 + 1; j > 0; j--) { for (i = courceRange -
	 * Math.abs(locCenter - j); i >= 0; i--) { serchMap[j][locCenter + i] =
	 * true; serchMap[j][locCenter - i] = true; } }
	 * 
	 * int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 }; for (i = 0; i < 8; i++) {
	 * System.arraycopy(locBace, 0, locNext, 0, 4); _moveLocation(locNext,
	 * firstCource[i]); if (locNext[0] - locCenter == 0 && locNext[1] -
	 * locCenter == 0) { return firstCource[i]; }
	 * 
	 * if (serchMap[locNext[0]][locNext[1]]) { int tmpX = locNext[0] + diff_x;
	 * int tmpY = locNext[1] + diff_y; boolean found = false; switch(i){ case 0:
	 * found = getMap().isPassable(tmpX, tmpY + 1, i); break; case 1: found =
	 * getMap().isPassable(tmpX - 1, tmpY + 1, i); break; case 2: found =
	 * getMap().isPassable(tmpX - 1, tmpY, i); break; case 3: found =
	 * getMap().isPassable(tmpX - 1, tmpY - 1, i); break; case 4: found =
	 * getMap().isPassable(tmpX, tmpY - 1, i); break; case 5: found =
	 * getMap().isPassable(tmpX + 1, tmpY - 1, i); break; case 6: found =
	 * getMap().isPassable(tmpX + 1, tmpY, i); break; case 7: found =
	 * getMap().isPassable(tmpX + 1, tmpY + 1, i); break; default: break; } /*
	 * switch(i){ case 0: if(벽체크(tmpX, tmpY+1, i) && 문체크(tmpX, tmpY+1, i)){
	 * if(this instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX,
	 * tmpY+1, i)){ found = true; } } } break; case 1: if(벽체크(tmpX-1, tmpY+1, i)
	 * && 문체크(tmpX-1, tmpY+1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX-1, tmpY+1, i)){ found = true; } } } break;
	 * case 2: if(벽체크(tmpX-1, tmpY, i) && 문체크(tmpX-1, tmpY, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX-1, tmpY,
	 * i)){ found = true; } } } break; case 3: if(벽체크(tmpX-1, tmpY-1, i) &&
	 * 문체크(tmpX-1, tmpY-1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX-1, tmpY-1, i)){ found = true; } } } break;
	 * case 4: if(벽체크(tmpX, tmpY-1, i) && 문체크(tmpX, tmpY-1, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY-1,
	 * i)){ found = true; } } } break; case 5: if(벽체크(tmpX+1, tmpY-1, i) &&
	 * 문체크(tmpX+1, tmpY-1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX+1, tmpY-1, i)){ found = true; } } } break;
	 * case 6: if(벽체크(tmpX-1, tmpY, i) && 문체크(tmpX-1, tmpY, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX-1, tmpY,
	 * i)){ found = true; } } } break; case 7: if(벽체크(tmpX+1, tmpY+1, i) &&
	 * 문체크(tmpX+1, tmpY+1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX+1, tmpY+1, i)){ found = true; } } } break; /*
	 * case 0: if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){ if(this instanceof
	 * L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY, i)){ found =
	 * true; } } } break; case 1: if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){
	 * if(this instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX,
	 * tmpY, i)){ found = true; } } } break; case 2: if(벽체크(tmpX, tmpY, i) &&
	 * 문체크(tmpX, tmpY, i)){ if(this instanceof L1DollInstance){ found = true;
	 * }else{ if(오브젝트체크(tmpX, tmpY, i)){ found = true; } } } break; case 3:
	 * if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){ if(this instanceof
	 * L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY, i)){ found =
	 * true; } } } break; case 4: if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){
	 * if(this instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX,
	 * tmpY, i)){ found = true; } } } break; case 5: if(벽체크(tmpX, tmpY, i) &&
	 * 문체크(tmpX, tmpY, i)){ if(this instanceof L1DollInstance){ found = true;
	 * }else{ if(오브젝트체크(tmpX, tmpY, i)){ found = true; } } } break; case 6:
	 * if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){ if(this instanceof
	 * L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY, i)){ found =
	 * true; } } } break; case 7: if(벽체크(tmpX, tmpY, i) && 문체크(tmpX, tmpY, i)){
	 * if(this instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX,
	 * tmpY, i)){ found = true; } } } break; default: break; }
	 */

	/*
	 * switch(i){ case 0: tmpY += 1; break; case 1: tmpX -= 1; tmpY += 1; break;
	 * case 2: tmpX -= 1; break; case 3: tmpX -= 1; tmpY -= 1; break; case 4:
	 * tmpY -= 1; break; case 5: tmpX += 1; tmpY -= 1; break; case 6: tmpX += 1;
	 * break; case 7: tmpX += 1; tmpY += 1; break; default: break; } switch(i){
	 * case 0: found = getMap().isPassable(tmpX, tmpY + 1, i); break; case 1:
	 * found = getMap().isPassable(tmpX - 1, tmpY + 1, i); break; case 2: found
	 * = getMap().isPassable(tmpX - 1, tmpY, i); break; case 3: found =
	 * getMap().isPassable(tmpX - 1, tmpY - 1, i); break; case 4: found =
	 * getMap().isPassable(tmpX, tmpY - 1, i); break; case 5: found =
	 * getMap().isPassable(tmpX + 1, tmpY - 1, i); break; case 6: found =
	 * getMap().isPassable(tmpX + 1, tmpY, i); break; case 7: found =
	 * getMap().isPassable(tmpX + 1, tmpY + 1, i); break; default: break; }
	 */
	/*
	 * if (found){ locCopy = new int[4]; System.arraycopy(locNext, 0, locCopy,
	 * 0, 4); locCopy[2] = firstCource[i]; locCopy[3] = firstCource[i];
	 * queueSerch.add(locCopy); } serchMap[locNext[0]][locNext[1]] = false; } }
	 * locBace = null;
	 * 
	 * while (queueSerch.size() > 0) { locBace = queueSerch.removeFirst();
	 * _getFront(dirFront, locBace[2]); for (i = 4; i >= 0; i--) {
	 * System.arraycopy(locBace, 0, locNext, 0, 4); _moveLocation(locNext,
	 * dirFront[i]); if (locNext[0] - locCenter == 0 && locNext[1] - locCenter
	 * == 0) { return locNext[3]; } if (serchMap[locNext[0]][locNext[1]]) { int
	 * tmpX = locNext[0] + diff_x; int tmpY = locNext[1] + diff_y;
	 * 
	 * 
	 * boolean found = false; switch(i){ case 0: found =
	 * getMap().isPassable(tmpX, tmpY + 1, i); break; case 1: found =
	 * getMap().isPassable(tmpX - 1, tmpY + 1, i); break; case 2: found =
	 * getMap().isPassable(tmpX - 1, tmpY, i); break; case 3: found =
	 * getMap().isPassable(tmpX - 1, tmpY - 1, i); break; case 4: found =
	 * getMap().isPassable(tmpX, tmpY - 1, i); break; default: break;
	 */
	/*
	 * case 0: if(벽체크(tmpX, tmpY+1, i) && 문체크(tmpX, tmpY+1, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY+1,
	 * i)){ found = true; } } } break; case 1: if(벽체크(tmpX-1, tmpY+1, i) &&
	 * 문체크(tmpX-1, tmpY+1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX-1, tmpY+1, i)){ found = true; } } } break;
	 * case 2: if(벽체크(tmpX-1, tmpY, i) && 문체크(tmpX-1, tmpY, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX-1, tmpY,
	 * i)){ found = true; } } } break; case 3: if(벽체크(tmpX-1, tmpY-1, i) &&
	 * 문체크(tmpX-1, tmpY-1, i)){ if(this instanceof L1DollInstance){ found =
	 * true; }else{ if(오브젝트체크(tmpX-1, tmpY-1, i)){ found = true; } } } break;
	 * case 4: if(벽체크(tmpX, tmpY-1, i) && 문체크(tmpX, tmpY-1, i)){ if(this
	 * instanceof L1DollInstance){ found = true; }else{ if(오브젝트체크(tmpX, tmpY-1,
	 * i)){ found = true; } } } break; default: break;
	 */
	// }
	/*
	 * switch(i){ switch(i){ case 0: tmpY += 1; break; case 1: tmpX -= 1; tmpY
	 * += 1; break; case 2: tmpX -= 1; break; case 3: tmpX -= 1; tmpY -= 1;
	 * break; case 4: tmpY -= 1; break; default: break; } case 0: found =
	 * getMap().isPassable(tmpX, tmpY + 1, i); break; case 1: found =
	 * getMap().isPassable(tmpX - 1, tmpY + 1, i); break; case 2: found =
	 * getMap().isPassable(tmpX - 1, tmpY, i); break; case 3: found =
	 * getMap().isPassable(tmpX - 1, tmpY - 1, i); break; case 4: found =
	 * getMap().isPassable(tmpX, tmpY - 1, i); break; default: break; }
	 */
	/*
	 * if (found) { locCopy = new int[4]; System.arraycopy(locNext, 0, locCopy,
	 * 0, 4); locCopy[2] = dirFront[i]; queueSerch.add(locCopy); }
	 * serchMap[locNext[0]][locNext[1]] = false; } } locBace = null; } return
	 * -1; }
	 */
	/*
	 * private void _getFront(int[] ary, int d) { switch(d){ case 1: ary[4] = 2;
	 * ary[3] = 0; ary[2] = 1; ary[1] = 3; ary[0] = 7; break; case 2: ary[4] =
	 * 2; ary[3] = 4; ary[2] = 0; ary[1] = 1; ary[0] = 3; break; case 3: ary[4]
	 * = 2; ary[3] = 4; ary[2] = 1; ary[1] = 3; ary[0] = 5; break; case 4:
	 * ary[4] = 2; ary[3] = 4; ary[2] = 6; ary[1] = 3; ary[0] = 5; break; case
	 * 5: ary[4] = 4; ary[3] = 6; ary[2] = 3; ary[1] = 5; ary[0] = 7; break;
	 * case 6: ary[4] = 4; ary[3] = 6; ary[2] = 0; ary[1] = 5; ary[0] = 7;
	 * break; case 7: ary[4] = 6; ary[3] = 0; ary[2] = 1; ary[1] = 5; ary[0] =
	 * 7; break; case 0: ary[4] = 2; ary[3] = 6; ary[2] = 0; ary[1] = 1; ary[0]
	 * = 7; break; default: break; } }
	 */
	private void useHealPotion(int healHp, int effectId) {
		Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), effectId));
		if (this.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.POLLUTE_WATER)) {
			healHp /= 2;
		}
		if (this instanceof L1PetInstance) {
			((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else if (this instanceof L1SummonInstance) {
			((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else {
			setCurrentHp(getCurrentHp() + healHp);
		}
	}

	public void useHastePotion(int time) {
		Broadcaster.broadcastPacket(this, new S_SkillHaste(getId(), 1, time));
		Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 191));
		getMoveState().setMoveSpeed(1);
		getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_HASTE,
				time * 1000);
	}

	public static final int USEITEM_HEAL = 0;
	public static final int USEITEM_HASTE = 1;
	public static final int[] healPotions = { POTION_OF_GREATER_HEALING,
			POTION_OF_EXTRA_HEALING, POTION_OF_HEALING };
	public static final int[] haestPotions = { B_POTION_OF_GREATER_HASTE_SELF,
			POTION_OF_GREATER_HASTE_SELF, B_POTION_OF_HASTE_SELF,
			POTION_OF_HASTE_SELF };

	public void useItem(int type, int chance) {
		if (getSkillEffectTimerSet().hasSkillEffect(71)) {
			return;
		}

		Random random = new Random();
		if (random.nextInt(100) > chance) {
			random = null;
			return;
		}
		if (type == USEITEM_HEAL) {
			if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
				useHealPotion(75, 197);
			} else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
				useHealPotion(45, 194);
			} else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
				useHealPotion(15, 189);
			}
		} else if (type == USEITEM_HASTE) {
			if (getSkillEffectTimerSet().hasSkillEffect(1001)) {
				random = null;
				return;
			}

			if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
				useHastePotion(2100);
			} else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF,
					1)) {
				useHastePotion(1800);
			} else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(350);
			} else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(300);
			}
		}
		random = null;
	}

	public void sendPackets(ServerBasePacket serverbasepacket) {
		if (getNetConnection() == null) {
			return;
		}

		try {
			getNetConnection().sendPacket(serverbasepacket);
		} catch (Exception e) {
		}
	}

	public boolean nearTeleport(int nx, int ny) {
		int rdir = _random.nextInt(8);
		int dir;
		for (int i = 0; i < 8; i++) {
			dir = rdir + i;
			if (dir > 7) {
				dir -= 8;
			}
			nx += HEADING_TABLE_X[dir];
			ny += HEADING_TABLE_Y[dir];

			if (getMap().isPassable(nx, ny)) {
				dir += 4;
				if (dir > 7) {
					dir -= 8;
				}
				teleport(nx, ny, dir);
				setCurrentMp(getCurrentMp() - 10);
				return true;
			}
		}
		return false;
	}

	public void teleport(int nx, int ny, int dir, int mapid) {
		L1World.getInstance().moveVisibleObject(this, nx, ny, mapid);
		setX(nx);
		setY(ny);
		setMap((short) mapid);
		getMoveState().setHeading(dir);
	}

	public void teleport(int nx, int ny, int dir) {
		L1World.getInstance().moveVisibleObject(this, nx, ny, this.getMapId());
		setX(nx);
		setY(ny);
		getMoveState().setHeading(dir);
	}

	// ----------From L1Character-------------
	private String _nameId;
	private boolean _Agro;
	private boolean _Agrocoi;
	private boolean _Agrososc;
	private int _homeX;

	private int _doorid = 0;
	private int _pos = 0;

	private int _homeY;
	private boolean _reSpawn;
	private int _lightSize;
	private boolean _weaponBreaked;
	private int _hiddenStatus;
	private int _movementDistance = 0;
	private int _tempLawful = 0;

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String s) {
		_nameId = s;
	}

	public boolean isAgro() {
		return _Agro;
	}

	public void setAgro(boolean flag) {
		_Agro = flag;
	}

	public boolean isAgrocoi() {
		return _Agrocoi;
	}

	public void setAgrocoi(boolean flag) {
		_Agrocoi = flag;
	}

	public boolean isAgrososc() {
		return _Agrososc;
	}

	public void setAgrososc(boolean flag) {
		_Agrososc = flag;
	}

	public int getHomeX() {
		return _homeX;
	}

	public void setHomeX(int i) {
		_homeX = i;
	}

	public int getdoorid() {
		return _doorid;
	}

	public void setdoorid(int i) {
		_doorid = i;
	}

	public int getpos() {
		return _pos;
	}

	public void setpos(int i) {
		_pos = i;
	}

	public int getHomeY() {
		return _homeY;
	}

	public void setHomeY(int i) {
		_homeY = i;
	}

	public boolean isReSpawn() {
		return _reSpawn;
	}

	public void setRespawn(boolean flag) {
		_reSpawn = flag;
	}

	public int getLightSize() {
		return _lightSize;
	}

	public void setLightSize(int i) {
		_lightSize = i;
	}

	public boolean isWeaponBreaked() {
		return _weaponBreaked;
	}

	public void setWeaponBreaked(boolean flag) {
		_weaponBreaked = flag;
	}

	public int getHiddenStatus() {
		return _hiddenStatus;
	}

	public void setHiddenStatus(int i) {
		_hiddenStatus = i;
	}

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	public int getTempLawful() {
		return _tempLawful;
	}

	public void setTempLawful(int i) {
		_tempLawful = i;
	}

	public int calcSleepTime(int sleepTime, int type) {
		switch (getMoveState().getMoveSpeed()) {
		case 0:
			break;
		case 1:
			sleepTime -= (sleepTime * 0.25);
			break;
		case 2:
			sleepTime *= 2;
			break;
		}
		if (getMoveState().getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			if (type == ATTACK_SPEED || type == MAGIC_SPEED) {
				sleepTime += (sleepTime * 0.25);
			}
		}
		return sleepTime;
	}

	public int calcSleepTime(int i) {
		int sleepTime = i;
		switch (getMoveState().getMoveSpeed()) {
		case 0:
			break;
		case 1:
			sleepTime -= (sleepTime * 0.25);
			break;
		case 2:
			sleepTime *= 2;
			break;
		}
		if (getMoveState().getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		return sleepTime;
	}

	protected void setAiRunning(boolean aiRunning) {
		_aiRunning = aiRunning;
	}

	protected boolean isAiRunning() {
		return _aiRunning;
	}

	protected void setActived(boolean actived) {
		_actived = actived;
	}

	protected boolean isActived() {
		return _actived;
	}

	protected void setFirstAttack(boolean firstAttack) {
		_firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return _firstAttack;
	}

	protected void setSleepTime(int sleep_time) {
		_sleep_time = sleep_time;
	}

	protected int getSleepTime() {
		return _sleep_time;
	}

	protected void setDeathProcessing(boolean deathProcessing) {
		_deathProcessing = deathProcessing;
	}

	protected boolean isDeathProcessing() {
		return _deathProcessing;
	}

	public int drainMana(int drain) {
		if (_drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
			return 0;
		}
		int result = Math.min(drain, getCurrentMp());
		if (_drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
			result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedMana;
		}
		_drainedMana += result;
		return result;
	}

	public boolean _destroyed = false;
	public boolean _isdelete = false;

	protected void transform(int transformId) {
		stopHpRegeneration();
		stopMpRegeneration();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId != 0) {
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		}
		L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);
		setting_template(npcTemplate);

		Broadcaster.broadcastPacket(this, new S_ChangeShape(getId(), getGfxId()
				.getTempCharGfx()));
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

	}

	public void setRest(boolean _rest) {
		this._rest = _rest;
	}

	public boolean isRest() {
		return _rest;
	}

	public void set제브부활(boolean 제브부활) {
		this._제브부활 = 제브부활;
	}

	public boolean is제브부활() {
		return _제브부활;
	}

	public boolean isResurrect() {
		return _isResurrect;
	}

	public void setResurrect(boolean flag) {
		_isResurrect = flag;
	}

	@Override
	public synchronized void resurrect(int hp) {
		if (_destroyed) {
			return;
		}
		if (_deleteTask != null) {
			if (!_future.cancel(false)) {
				return;
			}
			_deleteTask = null;
			_future = null;
		}
		super.resurrect(hp);
		startHpRegeneration();
		startMpRegeneration();
		L1SkillUse skill = new L1SkillUse();
		skill.handleCommands(null, L1SkillId.CANCELLATION, getId(), getX(),
				getY(), null, 0, L1SkillUse.TYPE_LOGIN, this);
	}

	protected synchronized void startDeleteTimer() {
		if (_deleteTask != null) {
			return;
		}
		_deleteTask = new DeleteTimer(getId());
		_future = GeneralThreadPool.getInstance().schedule(_deleteTask, DELETE_TIME);
	}

	protected class DeleteTimer implements Runnable {
		private int _id;

		protected DeleteTimer(int oId) {
			_id = oId;
			if (!(L1World.getInstance().findObject(_id) instanceof L1NpcInstance)) {
				throw new IllegalArgumentException("allowed only L1NpcInstance");
			}
		}

		@Override
		public void run() {
			try {
				L1NpcInstance npc = (L1NpcInstance) L1World.getInstance()
						.findObject(_id);
				if (npc == null || !npc.isDead() || npc._destroyed) {
					// System.out.println("엔피시 삭제 오류 번호 : "+npc.getNpcId());
					return;
				}
				npc.deleteMe();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isInMobGroup() {
		return getMobGroupInfo() != null;
	}

	public L1MobGroupInfo getMobGroupInfo() {
		return _mobGroupInfo;
	}

	public void setMobGroupInfo(L1MobGroupInfo m) {
		_mobGroupInfo = m;
	}

	public int getMobGroupId() {
		return _mobGroupId;
	}

	public void setMobGroupId(int i) {
		_mobGroupId = i;
	}

	public void startChat(int chatTiming) {
		if (chatTiming == CHAT_TIMING_APPEARANCE && this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_DEAD && !this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_HIDE && this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_GAME_TIME && this.isDead()) {
			return;
		}

		int npcId = this.getNpcTemplate().get_npcId();
		L1NpcChat npcChat = null;
		switch (chatTiming) {
		case CHAT_TIMING_APPEARANCE:
			npcChat = NpcChatTable.getInstance().getTemplateAppearance(npcId);
			break;
		case CHAT_TIMING_DEAD:
			npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
			break;
		case CHAT_TIMING_HIDE:
			npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
			break;
		case CHAT_TIMING_GAME_TIME:
			npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
			break;
		default:
			break;
		}
		if (npcChat == null) {
			return;
		}

		Timer timer = new Timer(true);
		L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
		if (!npcChat.isRepeat()) {
			timer.schedule(npcChatTimer, npcChat.getStartDelayTime());
		} else {
			timer.scheduleAtFixedRate(npcChatTimer,
					npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
		}
	}

	public void 폰인증멘트시작() {
		_폰인증쓰레드 = new 폰인증멘트();
		Timer timer = new Timer();
		timer.schedule(_폰인증쓰레드, 1 * 10 * 1000);// 30초안에 로그인없으면 절단
	}

	private 폰인증멘트 _폰인증쓰레드 = new 폰인증멘트();

	public class 폰인증멘트 extends TimerTask {
		public 폰인증멘트() {
		}

		@Override
		public void run() {
			while (true) {
				Broadcaster
						.broadcastPacket(L1NpcInstance.this,
								new S_NpcChatPacket(L1NpcInstance.this,
										Config.PHONE_VERIFLCATION_MESSAGE, 0), true);
				try {
					Thread.sleep((1000 * 10));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void set_num(int num) {
		this.num = num;
	}

	public int get_num() {
		return num;
	}

	@SuppressWarnings("unused")
	public void randomWalk() {
		tagertClear();
		int dir = checkObject(getX(), getY(), getMapId(), _random.nextInt(20));
		if (dir != -1) {
			setDirectionMove(dir);
			setSleepTime(calcSleepTime(getPassispeed()));
		}
	}

	public LineageClient getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(LineageClient clientthread) {
		_netConnection = clientthread;
	}

	public boolean 펫체크(int x, int y, int h) {
		switch (h) {
		case 0:
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!오브젝트체크(x, y, 0) && 타겟(0))
					return true;
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!오브젝트체크(x, y, 7) && 타겟(7))
					return true;
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!오브젝트체크(x, y, 1) && 타겟(1))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!오브젝트체크(x, y, 6) && 타겟(6))
						return true;
				}
			} else {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!오브젝트체크(x, y, 2) && 타겟(2))
						return true;
				}
			}
			break;
		case 1:
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!오브젝트체크(x, y, 1) && 타겟(1))
					return true;
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!오브젝트체크(x, y, 2) && 타겟(2))
					return true;
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!오브젝트체크(x, y, 0) && 타겟(0))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!오브젝트체크(x, y, 7) && 타겟(7))
						return true;
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!오브젝트체크(x, y, 3) && 타겟(3))
						return true;
				}
			}
			break;
		case 2:
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!오브젝트체크(x, y, 2) && 타겟(2))
					return true;
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!오브젝트체크(x, y, 3) && 타겟(3))
					return true;
			}
			if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
				if (!오브젝트체크(x, y, 1) && 타겟(1))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!오브젝트체크(x, y, 4) && 타겟(4))
						return true;
				}
			} else {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!오브젝트체크(x, y, 0) && 타겟(0))
						return true;
				}
			}
			break;
		case 3:
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!오브젝트체크(x, y, 3) && 타겟(3))
					return true;
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!오브젝트체크(x, y, 4) && 타겟(4))
					return true;
			}
			if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
				if (!오브젝트체크(x, y, 2) && 타겟(2))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!오브젝트체크(x, y, 5) && 타겟(5))
						return true;
				}
			} else {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!오브젝트체크(x, y, 1) && 타겟(1))
						return true;
				}
			}
			break;
		case 4:
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!오브젝트체크(x, y, 4) && 타겟(4))
					return true;
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!오브젝트체크(x, y, 5) && 타겟(5))
					return true;
			}
			if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
				if (!오브젝트체크(x, y, 3) && 타겟(3))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
					if (!오브젝트체크(x, y, 6) && 타겟(6))
						return true;
				}
			} else {
				if (벽체크(x, y, 2) && 문체크(x, y, 2)) {
					if (!오브젝트체크(x, y, 2) && 타겟(2))
						return true;
				}
			}
			break;
		case 5:
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!오브젝트체크(x, y, 5) && 타겟(5))
					return true;
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!오브젝트체크(x, y, 6) && 타겟(6))
					return true;
			}
			if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
				if (!오브젝트체크(x, y, 4) && 타겟(4))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
					if (!오브젝트체크(x, y, 7) && 타겟(7))
						return true;
				}
			} else {
				if (벽체크(x, y, 3) && 문체크(x, y, 3)) {
					if (!오브젝트체크(x, y, 3) && 타겟(3))
						return true;
				}
			}
			break;
		case 6:
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!오브젝트체크(x, y, 6) && 타겟(6))
					return true;
			}
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!오브젝트체크(x, y, 7) && 타겟(7))
					return true;
			}
			if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
				if (!오브젝트체크(x, y, 5) && 타겟(5))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
					if (!오브젝트체크(x, y, 0) && 타겟(0))
						return true;
				}
			} else {
				if (벽체크(x, y, 4) && 문체크(x, y, 4)) {
					if (!오브젝트체크(x, y, 4) && 타겟(4))
						return true;
				}
			}
			break;
		case 7:
			if (벽체크(x, y, 7) && 문체크(x, y, 7)) {
				if (!오브젝트체크(x, y, 7) && 타겟(7))
					return true;
			}
			if (벽체크(x, y, 0) && 문체크(x, y, 0)) {
				if (!오브젝트체크(x, y, 0) && 타겟(0))
					return true;
			}
			if (벽체크(x, y, 6) && 문체크(x, y, 6)) {
				if (!오브젝트체크(x, y, 6) && 타겟(6))
					return true;
			}
			if (_random.nextInt(2) == 0) {
				if (벽체크(x, y, 1) && 문체크(x, y, 1)) {
					if (!오브젝트체크(x, y, 1) && 타겟(1))
						return true;
				}
			} else {
				if (벽체크(x, y, 5) && 문체크(x, y, 5)) {
					if (!오브젝트체크(x, y, 5) && 타겟(5))
						return true;
				}
			}
			break;
		}

		return false;
	}

	private boolean 타겟(int targetDir) {
		int nx = HEADING_TABLE_X[targetDir] + getX();
		int ny = HEADING_TABLE_Y[targetDir] + getY();
		for (L1Object temp : L1World.getInstance().getVisibleObjects(this, 1)) {
			if (temp.getX() == nx && temp.getY() == ny) {
				if (temp instanceof L1SummonInstance
						|| temp instanceof L1PetInstance
						|| temp instanceof L1PcInstance) {
					_target = (L1Character) temp;
					return true;
				}
			}
		}
		return false;
	}

}
