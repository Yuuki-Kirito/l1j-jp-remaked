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
package l1j.server.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.TextMapReader;
import l1j.server.GameSystem.Gamble.GambleInstance;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TeleporterInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;

public class L1World {
	private static Logger _log = Logger.getLogger(L1World.class.getName());

	private final ConcurrentHashMap<String, L1PcInstance> _allPlayers;
	private final ConcurrentHashMap<Integer, L1PetInstance> _allPets;
	private final ConcurrentHashMap<Integer, L1SummonInstance> _allSummons;
	private final ConcurrentHashMap<Integer, L1Object> _allObjects;
	private final ConcurrentHashMap<Integer, L1MerchantInstance> _allMerchant;
	private final ConcurrentHashMap<Integer, L1ItemInstance> _allitem;
	private final ConcurrentHashMap<Integer, L1NpcShopInstance> _allNpcShop;
	private final ConcurrentHashMap<Integer, GambleInstance> _allGamble;
	private final ConcurrentHashMap<Integer, L1RobotInstance> _allRobot;
	private final ConcurrentHashMap<Integer, L1FurnitureInstance> _allFurniture;
	private final ConcurrentHashMap<Integer, L1Object>[] _visibleObjects;
	private final CopyOnWriteArrayList<L1War> _allWars;
	private final ConcurrentHashMap<String, L1Clan> _allClans;
	private final ConcurrentHashMap<Integer, L1FieldObjectInstance> _allFieldObject;
	private final ConcurrentHashMap<Integer, L1TeleporterInstance> _allTeleporter;
	private final ConcurrentHashMap<Integer, L1GuardInstance> _allGuard;
	private final ConcurrentHashMap<Integer, L1CastleGuardInstance> _allCastleGuard;

	private int _weather = 4;

	private boolean _worldChatEnabled = true;

	private boolean _processingContributionTotal = false;

	private static final int MAX_MAP_ID = 25088;

	private static L1World _instance;

	private L1PetMember _PetMember = null;

	private final visibleLocObjects[] _visibleLocMap;

	@SuppressWarnings("unchecked")
	private L1World() {
		_allPlayers = new ConcurrentHashMap<String, L1PcInstance>(); // 모든 플레이어
		_allPets = new ConcurrentHashMap<Integer, L1PetInstance>(); // 모든 애완동물
		_allSummons = new ConcurrentHashMap<Integer, L1SummonInstance>(); // 모든
																			// 사몬몬스타
		_allMerchant = new ConcurrentHashMap<Integer, L1MerchantInstance>(); // 모든
																				// 상점NPC
		_allitem = new ConcurrentHashMap<Integer, L1ItemInstance>(); // 모든
																		// ItemObject
		_allNpcShop = new ConcurrentHashMap<Integer, L1NpcShopInstance>(); // 모든
																			// NpcShop
		_allGamble = new ConcurrentHashMap<Integer, GambleInstance>();
		_allFurniture = new ConcurrentHashMap<Integer, L1FurnitureInstance>();
		_allRobot = new ConcurrentHashMap<Integer, L1RobotInstance>(); // 모든
																		// NpcShop
		_allTeleporter = new ConcurrentHashMap<Integer, L1TeleporterInstance>(); // 모든
																					// 텔레포터
		_allGuard = new ConcurrentHashMap<Integer, L1GuardInstance>(); // 모든 경비병
		_allCastleGuard = new ConcurrentHashMap<Integer, L1CastleGuardInstance>(); // 모든
																					// 성경비병
		_allObjects = new ConcurrentHashMap<Integer, L1Object>(); // 모든
																	// 오브젝트(L1ItemInstance
																	// 들어가,
																	// L1Inventory는
																	// 없음).setShared(true)
		_visibleObjects = new ConcurrentHashMap[MAX_MAP_ID + 1]; // MAP 마다의
																	// 오브젝트(L1Inventory
																	// 들어가,
																	// L1ItemInstance는
																	// 없음)
		_allWars = new CopyOnWriteArrayList<L1War>(); // 모든 전쟁
		_allClans = new ConcurrentHashMap<String, L1Clan>(); // 모든
																// 크란(Online/Offline
																// 어느쪽이나)
		_allFieldObject = new ConcurrentHashMap<Integer, L1FieldObjectInstance>(); // 모든
																					// 필드오브젝트

		for (int i = 0; i <= MAX_MAP_ID; i++) {
			_visibleObjects[i] = new ConcurrentHashMap<Integer, L1Object>();
		}

		_visibleLocMap = new visibleLocObjects[MAX_MAP_ID + 1];

		try {
			for (int[] mapInfo : TextMapReader.MAP_INFO) {
				short map = (short) mapInfo[TextMapReader.MAPINFO_MAP_NO];
				L1Map m = L1WorldMap.getInstance().getMap(map);
				_visibleLocMap[map] = new visibleLocObjects(m.getX(), m.getWidth(), m.getY(), m.getHeight());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void removeMap(int mapid) {
		_visibleLocMap[mapid] = null;
	}

	public void mapClone(int oldMapId, int newMapId) {
		boolean check = false;
		try {
			visibleLocObjects vl = _visibleLocMap[newMapId];
			if (vl.CHECK())
				check = true;
		} catch (Exception e) {
			check = true;
		}
		// if(_visibleLocMap[newMapId] == null){
		if (check) {
			L1Map m = L1WorldMap.getInstance().getMap((short) oldMapId);
			_visibleLocMap[newMapId] = new visibleLocObjects(m.getX(), m.getWidth(), m.getY(), m.getHeight());
		}
	}

	private static final int visibleLocSize = 10;

	class visibleLocObjects {
		private int _startX;
		private int _startY;
		private int _width;
		private int _height;
		private final ConcurrentHashMap<Integer, L1Object>[][] _visibleLocXY;

		public visibleLocObjects(int startX, int width, int startY, int height) {
			_startX = startX;
			_startY = startY;
			_width = width;
			_height = height;

			// 좌표마다 생성하면 좋지만 메모리가 4기가나 먹음.
			// 그래서 visibleLocSize 단위로 끊어서 저장
			int w = width / visibleLocSize;
			int h = height / visibleLocSize;

			_visibleLocXY = new ConcurrentHashMap[w + 1][h + 1];
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= h; y++) {
					_visibleLocXY[x][y] = new ConcurrentHashMap<Integer, L1Object>();
				}
			}
		}

		public boolean CHECK() {
			if (_startX == 0 && _startY == 0 && _width == 0 && _height == 0)
				return true;
			return false;
		}

		public void store(L1Object object) {
			try {
				int x = (object.getX() - _startX) / visibleLocSize;
				int y = (object.getY() - _startY) / visibleLocSize;
				object.setVisibleX(x);
				object.setVisibleY(y);
				object.setVisibleMapId(object.getMapId());
				object.setVisibleTempX(object.getX());
				object.setVisibleTempY(object.getY());
				_visibleLocXY[x][y].put(object.getId(), object);
			} catch (Exception e) {
			}
		}

		public void store(L1Object object, int newX, int newY) {
			try {
				int x = (newX - _startX) / visibleLocSize;
				int y = (newY - _startY) / visibleLocSize;
				object.setVisibleX(x);
				object.setVisibleY(y);
				object.setVisibleTempX(newX);
				object.setVisibleTempY(newY);
				_visibleLocXY[x][y].put(object.getId(), object);
			} catch (Exception e) {
			}
		}

		public void remove(L1Object object) {
			try {
				_visibleLocXY[object.getVisibleX()][object.getVisibleY()].remove(object.getId());
			} catch (Exception e) {
			}
		}

		public void move(L1Object object, int newX, int newY) {
			try {
				int newx = (newX - _startX) / visibleLocSize;
				int newy = (newY - _startY) / visibleLocSize;
				_visibleLocXY[object.getVisibleX()][object.getVisibleY()].remove(object.getId());
				object.setVisibleX(newx);
				object.setVisibleY(newy);
				object.setVisibleTempX(newX);
				object.setVisibleTempY(newY);
				_visibleLocXY[newx][newy].put(object.getId(), object);
			} catch (Exception e) {
			}
		}

		public ArrayList<L1Object> VisiblePoint(L1Location loc, int radius) {
			ArrayList<L1Object> result = new ArrayList<L1Object>();
			try {
				int cx = (loc.getX() - _startX) / visibleLocSize;
				int cy = (loc.getY() - _startY) / visibleLocSize;
				int ccx = _width / visibleLocSize;
				int ccy = _height / visibleLocSize;
				for (int x = cx - 4; x < cx + 4; x++) {
					if (x > ccx || x < 0)
						continue;
					for (int y = cy - 4; y < cy + 4; y++) {
						if (y > ccy || y < 0)
							continue;
						for (L1Object obj : _visibleLocXY[x][y].values()) {
							if (obj == null)
								continue;
							if (loc.getTileLineDistance(obj.getLocation()) <= radius)
								result.add(obj);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}

		public ArrayList<L1Object> Visible(L1Object object, int radius) {
			ArrayList<L1Object> result = new ArrayList<L1Object>();
			try {
				Point pt = object.getLocation();
				int cx = (object.getX() - _startX) / visibleLocSize;
				int cy = (object.getY() - _startY) / visibleLocSize;
				int ccx = _width / visibleLocSize;
				int ccy = _height / visibleLocSize;
				for (int x = cx - 4; x < cx + 4; x++) {
					if (x > ccx || x < 0)
						continue;
					for (int y = cy - 4; y < cy + 4; y++) {
						if (y > ccy || y < 0)
							continue;
						for (L1Object obj : _visibleLocXY[x][y].values()) {
							if (obj == null || obj.equals(object))
								continue;
							if (radius == -1) {
								if (pt.isInScreen(obj.getLocation()))
									result.add(obj);
							} else if (radius == 0) {
								if (pt.isSamePoint(obj.getLocation()))
									result.add(obj);
							} else {
								if (pt.getTileLineDistance(obj.getLocation()) <= radius)
									result.add(obj);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}
	}

	public void Move(L1Object object, int newX, int newY) {
		try {
			if (object == null)
				return;
			int mapid = object.getVisibleMapId();
			if (mapid <= MAX_MAP_ID)
				_visibleLocMap[mapid].move(object, newX, newY);
		} catch (Exception e) {
		}
	}

	public static L1World getInstance() {
		if (_instance == null) {
			_instance = new L1World();
		}
		return _instance;
	}

	/**
	 * 모든 상태를 클리어 한다.<br>
	 * 디버그, 테스트등이 특수한 목적 이외로 호출해서는 안 된다.
	 */
	public void clear() {
		_instance = new L1World();
	}

	public void storeObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		_allObjects.put(object.getId(), object);
		if (object instanceof L1RobotInstance) {
			_allRobot.put(object.getId(), (L1RobotInstance) object);
			_allPlayers.put(((L1PcInstance) object).getName(), (L1PcInstance) object);
		} else if (object instanceof L1PcInstance) {
			_allPlayers.put(((L1PcInstance) object).getName(), (L1PcInstance) object);
		} else {
			if (object instanceof L1ItemInstance) {
				_allitem.put(object.getId(), (L1ItemInstance) object);
			} else if (object instanceof L1MerchantInstance) {
				_allMerchant.put(object.getId(), (L1MerchantInstance) object);
			} else if (object instanceof L1PetInstance) {
				_allPets.put(object.getId(), (L1PetInstance) object);
			} else if (object instanceof L1SummonInstance) {
				_allSummons.put(object.getId(), (L1SummonInstance) object);
			} else if (object instanceof L1FieldObjectInstance) {
				_allFieldObject.put(object.getId(), (L1FieldObjectInstance) object);
			} else if (object instanceof L1NpcShopInstance) {
				_allNpcShop.put(object.getId(), (L1NpcShopInstance) object);
			} else if (object instanceof GambleInstance) {
				_allGamble.put(object.getId(), (GambleInstance) object);
			} else if (object instanceof L1FurnitureInstance) {
				_allFurniture.put(object.getId(), (L1FurnitureInstance) object);
			} else if (object instanceof L1TeleporterInstance) {
				_allTeleporter.put(object.getId(), (L1TeleporterInstance) object);
			} else if (object instanceof L1GuardInstance) {
				_allGuard.put(object.getId(), (L1GuardInstance) object);
			} else if (object instanceof L1CastleGuardInstance) {
				_allCastleGuard.put(object.getId(), (L1CastleGuardInstance) object);
			}
		}
	}

	public void removeObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		_allObjects.remove(object.getId());
		if (object instanceof L1RobotInstance) {
			((L1RobotInstance) object).updateconnect(false);
			_allRobot.remove(object.getId());
			_allPlayers.remove(((L1PcInstance) object).getName());
		} else if (object instanceof L1PcInstance) {
			_allPlayers.remove(((L1PcInstance) object).getName());
		} else {
			if (object instanceof L1ItemInstance) {
				_allitem.remove(object.getId());
			} else if (object instanceof L1MerchantInstance) {
				_allMerchant.remove(object.getId());
			} else if (object instanceof L1PetInstance) {
				_allPets.remove(object.getId());
			} else if (object instanceof L1SummonInstance) {
				_allSummons.remove(object.getId());
			} else if (object instanceof L1FieldObjectInstance) {
				_allFieldObject.remove(object.getId());
			} else if (object instanceof L1NpcShopInstance) {
				_allNpcShop.remove(object.getId());
			} else if (object instanceof GambleInstance) {
				_allGamble.remove(object.getId());
			} else if (object instanceof L1FurnitureInstance) {
				_allFurniture.remove(object.getId());
			} else if (object instanceof L1TeleporterInstance) {
				_allTeleporter.remove(object.getId());
			} else if (object instanceof L1GuardInstance) {
				_allGuard.remove(object.getId());
			} else if (object instanceof L1CastleGuardInstance) {
				_allCastleGuard.remove(object.getId());
			}
		}
	}

	public L1Object findObject(String name) {
		if (_allObjects.contains(name)) {
			return _allObjects.get(name);
		}
		for (L1PcInstance each : getAllPlayers()) {
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	public L1Object findObject(int oID) {
		return _allObjects.get(oID);
	}

	/*
	 * public L1Object findObject(String name) { if (_allObjects.contains(name)) {
	 * return _allObjects.get(name); } for (L1PcInstance each : getAllPlayers()) {
	 * if (each.getName().equalsIgnoreCase(name)) { return each; } } return null; }
	 */

	// _allObjects의 뷰
	private Collection<L1Object> _allValues;

	public Collection<L1Object> getObject() {
		Collection<L1Object> vs = _allValues;
		return (vs != null) ? vs : (_allValues = Collections.unmodifiableCollection(_allObjects.values()));
	}

	public L1GroundInventory getInventory(int x, int y, short map) {
		int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1; // xy의
																		// 마이너스치를
																		// 인벤트리
																		// 키로서
																		// 사용

		Object object = _visibleObjects[map].get(inventoryKey);
		if (object == null) {
			return new L1GroundInventory(inventoryKey, x, y, map);
		} else {
			return (L1GroundInventory) object;
		}
	}

	public L1GroundInventory getInventory(L1Location loc) {
		return getInventory(loc.getX(), loc.getY(), (short) loc.getMap().getId());
	}

	public void addVisibleObject(L1Object object) {
		if (object == null)
			return;

		short mapid = object.getMapId();
		if (mapid <= MAX_MAP_ID) {
			_visibleObjects[mapid].put(object.getId(), object);
			try {
				_visibleLocMap[mapid].store(object);
			} catch (Exception e) {
			}
			// 맵 인식 변경 by사부
			if (!(object instanceof L1Inventory) && !(object instanceof L1DollInstance) && !(object instanceof L1DoorInstance)
			/* && !(object instanceof L1ArrowInstatance) */) {
				object.getMap().setPassable(object.getLocation(), false);
			}
		}
	}

	public void removeVisibleObject(L1Object object) {
		if (object == null)
			return;
		int mapid = object.getVisibleMapId();
		if (mapid <= MAX_MAP_ID) {
			_visibleObjects[mapid].remove(object.getId());
			try {
				_visibleLocMap[mapid].remove(object);
			} catch (Exception e) {
			}

			/*
			 * if(object instanceof L1NpcInstance){
			 * Broadcaster.broadcastPacket((L1NpcInstance)object, new
			 * S_RemoveObject(object.getId())); }
			 */

			if (!(object instanceof L1Inventory) && !(object instanceof L1DollInstance) && !(object instanceof L1DoorInstance)) {
				if (object instanceof L1MonsterInstance || object instanceof L1SummonInstance || object instanceof L1PetInstance) {
					if (((L1NpcInstance) object).getCurrentHp() < 1 || ((L1NpcInstance) object).isDead())
						return;
				}
				if (object instanceof L1PcInstance) {
					if (((L1PcInstance) object).getCurrentHp() < 1 || ((L1PcInstance) object).isDead())
						return;
				}
				if (object.getMapId() == mapid)
					object.getMap().setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
				else {
					L1Map map = L1WorldMap.getInstance().getMap((short) mapid);
					map.setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
				}
			}
		}
	}

	public void moveVisibleObject(L1Object object, int newx, int newy, int newMap) {
		if (object == null) {
			return;
		}

		int oldMapId = object.getVisibleMapId();
		L1Map oldmap = L1WorldMap.getInstance().getMap((short) oldMapId);

		if (oldMapId != newMap) {
			L1Map newmap = L1WorldMap.getInstance().getMap((short) newMap);
			if (!(object instanceof L1Inventory) && !(object instanceof L1DollInstance) && !(object instanceof L1DoorInstance)) {
				oldmap.setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
				newmap.setPassable(newx, newy, false);
			}
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getNpcTemplate().get_npcId() == 5000091) {
					oldmap.setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
					newmap.setPassable(newx, newy, false);
				}
			}
			if (oldMapId <= MAX_MAP_ID) {
				_visibleObjects[oldMapId].remove(object.getId());
				try {
					_visibleLocMap[oldMapId].remove(object);
				} catch (Exception e) {
				}
			}
			if (newMap <= MAX_MAP_ID) {
				object.setVisibleMapId(newMap);
				_visibleObjects[newMap].put(object.getId(), object);
				try {
					_visibleLocMap[newMap].store(object, newx, newy);
				} catch (Exception e) {
				}
			}
		} else {
			if (!(object instanceof L1Inventory) && !(object instanceof L1DollInstance) && !(object instanceof L1DoorInstance)) {
				oldmap.setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
				oldmap.setPassable(newx, newy, false);
			}
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getNpcTemplate().get_npcId() == 5000091) {
					oldmap.setPassable(object.getVisibleTempX(), object.getVisibleTempY(), true);
					oldmap.setPassable(newx, newy, false);
				}
			}
			try {
				_visibleLocMap[oldMapId].move(object, newx, newy);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 원본 public void removeVisibleObject(L1Object object) { if(object == null)
	 * return; short mapid = object.getMapId(); if (mapid <= MAX_MAP_ID) {
	 * _visibleObjects[mapid].remove(object.getId()); try{
	 * _visibleLocMap[mapid].remove(object); }catch(Exception e){} // 맵 인식 변경 by사부
	 * if(!(object instanceof L1Inventory) && !(object instanceof L1DollInstance) &&
	 * !(object instanceof L1DoorInstance)){ if(object instanceof L1MonsterInstance
	 * ||object instanceof L1SummonInstance ||object instanceof L1PetInstance){
	 * if(((L1NpcInstance)object).getCurrentHp() < 1
	 * ||((L1NpcInstance)object).isDead()) return; } if(object instanceof
	 * L1PcInstance){ if(((L1PcInstance)object).getCurrentHp() < 1
	 * ||((L1PcInstance)object).isDead()) return; }
	 * object.getMap().setPassable(object.getLocation(), true); } } }
	 * 
	 * public void moveVisibleObject(L1Object object, int newx, int newy, int
	 * newMap){ if(object == null){ return; } if(!(object instanceof L1Inventory) &&
	 * !(object instanceof L1DollInstance) && !(object instanceof L1DoorInstance)){
	 * object.getMap().setPassable(object.getLocation(), true);
	 * object.getMap().setPassable(newx, newy, false); } if(object instanceof
	 * L1DoorInstance){ L1DoorInstance door = (L1DoorInstance)object;
	 * if(door.getNpcTemplate().get_npcId() == 5000091){
	 * object.getMap().setPassable(object.getLocation(), true);
	 * object.getMap().setPassable(newx, newy, false); } } int oldMapId =
	 * object.getMapId(); if (oldMapId != newMap) { if (object.getMapId() <=
	 * MAX_MAP_ID) { _visibleObjects[oldMapId].remove(object.getId()); try{
	 * _visibleLocMap[oldMapId].remove(object); }catch(Exception e){} } if (newMap
	 * <= MAX_MAP_ID) { _visibleObjects[newMap].put(object.getId(), object); try{
	 * _visibleLocMap[newMap].store(object, newx, newy); }catch(Exception e){} }
	 * }else{ try{ _visibleLocMap[oldMapId].move(object, newx, newy);
	 * }catch(Exception e){} } }
	 */

	private ConcurrentHashMap<Integer, Integer> createLineMap(Point src, Point target) {
		ConcurrentHashMap<Integer, Integer> lineMap = new ConcurrentHashMap<Integer, Integer>();

		/*
		 * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htm보다
		 */
		int E;
		int x;
		int y;
		int key;
		int i;
		int x0 = src.getX();
		int y0 = src.getY();
		int x1 = target.getX();
		int y1 = target.getY();
		int sx = (x1 > x0) ? 1 : -1;
		int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
		int sy = (y1 > y0) ? 1 : -1;
		int dy = (y1 > y0) ? y1 - y0 : y0 - y1;

		x = x0;
		y = y0;
		/* 기울기가 1 이하의 경우 */
		if (dx >= dy) {
			E = -dx;
			for (i = 0; i <= dx; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				x += sx;
				E += 2 * dy;
				if (E >= 0) {
					y += sy;
					E -= 2 * dx;
				}
			}
			/* 기울기가 1보다 큰 경우 */
		} else {
			E = -dy;
			for (i = 0; i <= dy; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				y += sy;
				E += 2 * dx;
				if (E >= 0) {
					x += sx;
					E -= 2 * dy;
				}
			}
		}

		return lineMap;
	}

	public ArrayList<L1Object> getVisibleLineObjects(L1Object src, L1Object target) {
		ConcurrentHashMap<Integer, Integer> lineMap = createLineMap(src.getLocation(), target.getLocation());

		int map = target.getMapId();
		ArrayList<L1Object> result = new ArrayList<L1Object>();

		if (map <= MAX_MAP_ID) {
			Collection<L1Object> df;
			df = _visibleObjects[map].values();
			for (L1Object element : df) {
				if (element == null || element.equals(src)) {
					continue;
				}

				int key = (element.getX() << 16) + element.getY();
				if (lineMap.containsKey(key)) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public ArrayList<L1Object> getVisibleBoxObjects(L1Object object, int heading, int width, int height) {
		int x = object.getX();
		int y = object.getY();
		int map = object.getMapId();
		L1Location location = object.getLocation();
		ArrayList<L1Object> result = new ArrayList<L1Object>();
		int headingRotate[] = { 6, 7, 0, 1, 2, 3, 4, 5 };
		double cosSita = Math.cos(headingRotate[heading] * Math.PI / 4);
		double sinSita = Math.sin(headingRotate[heading] * Math.PI / 4);

		if (map <= MAX_MAP_ID) {
			Collection<L1Object> df = null;
			df = _visibleObjects[map].values();
			for (L1Object element : df) {
				if (element == null || element.equals(object)) {
					continue;
				}
				if (map != element.getMapId()) {
					continue;
				}
				if (location.isSamePoint(element.getLocation())) {
					result.add(element);
					continue;
				}
				int distance = location.getTileLineDistance(element.getLocation());
				// 직선 거리가 높이, 폭어느 쪽보다 큰 경우, 계산할 것도 없이 범위외
				if (distance > height && distance > width) {
					continue;
				}

				// object의 위치를 원점과하기 위한 좌표 보정
				int x1 = element.getX() - x;
				int y1 = element.getY() - y;

				// Z축회전시키고 각도를 0번으로 한다.
				int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
				int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);

				int xmin = 0;
				int xmax = height;
				int ymin = -width;
				int ymax = width;

				// 깊이가 사정과 맞물리지 않기 때문에 직선 거리로 판정하도록(듯이) 변경.
				// if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <=
				// ymax) {
				if (rotX > xmin && distance <= xmax && rotY >= ymin && rotY <= ymax) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public ArrayList<L1Object> getVisibleObjects(L1Object object) {
		return getVisibleObjects(object, -1);
	}

	public ArrayList<L1Object> getVisibleObjects(L1Object object, int radius) {
		L1Map map = object.getMap();
		Point pt = object.getLocation();
		ArrayList<L1Object> result = new ArrayList<L1Object>();

		boolean ck = true;
		try {
			// 문제 생길시에 로봇만 되게
			if (_visibleLocMap[map.getId()] != null) {
				result = _visibleLocMap[map.getId()].Visible(object, radius);
				ck = false;
			}
		} catch (Exception e) {
			ck = true;
		}
		if (ck) {
			if (map.getId() <= MAX_MAP_ID) {
				Collection<L1Object> df = null;
				df = _visibleObjects[map.getId()].values();
				for (L1Object element : df) {
					if (element == null || element.equals(object)) {
						continue;
					}
					if (map != element.getMap()) {
						continue;
					}

					if (radius == -1) {
						if (pt.isInScreen(element.getLocation())) {
							result.add(element);
						}
					} else if (radius == 0) {
						if (pt.isSamePoint(element.getLocation())) {
							result.add(element);
						}
					} else {
						if (pt.getTileLineDistance(element.getLocation()) <= radius) {
							result.add(element);
						}
					}
				}
			}
		}
		return result;
	}

	public ArrayList<L1Object> getVisiblePoint(L1Location loc, int radius) {
		ArrayList<L1Object> result = new ArrayList<L1Object>();
		int mapId = loc.getMapId(); // 루프내에서 부르면(자) 무겁기 때문에

		if (mapId <= MAX_MAP_ID) {
			boolean ck = true;
			try {
				// 문제 생길시에 로봇만 되게
				if (_visibleLocMap[mapId] != null) {
					result = _visibleLocMap[mapId].VisiblePoint(loc, radius);
					ck = false;
				}
			} catch (Exception e) {
				ck = true;
			}
			if (ck) {
				Collection<L1Object> df = null;
				df = _visibleObjects[mapId].values();
				for (L1Object element : df) {
					if (element == null || mapId != element.getMapId()) {
						continue;
					}

					if (loc.getTileLineDistance(element.getLocation()) <= radius) {
						result.add(element);
					}
				}
			}
		}

		return result;
	}

	public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object) {
		return getVisiblePlayer(object, -1);
	}

	public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
		int map = object.getMapId();
		Point pt = object.getLocation();
		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		Collection<L1PcInstance> pc = null;
		pc = _allPlayers.values();

		for (L1PcInstance element : pc) {
			if (element == null || element.equals(object) || map != element.getMapId()) {
				continue;
			}

			if (radius == -1) {
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
			} else if (radius == 0) {
				if (pt.isSamePoint(element.getLocation())) {
					result.add(element);
				}
			} else {
				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(L1Object object, L1Object target) {
		int map = object.getMapId();
		Point objectPt = object.getLocation();
		Point targetPt = target.getLocation();
		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		Collection<L1PcInstance> pc = null;
		pc = _allPlayers.values();
		for (L1PcInstance element : pc) {
			if (element == null || element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (objectPt.isInScreen(element.getLocation())) {
					if (!targetPt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				}
			} else {
				if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
					if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
						result.add(element);
					}
				}
			}
		}
		return result;
	}

	/**
	 * object를 인식할 수 있는 범위에 있는 플레이어를 취득한다
	 * 
	 * @param object
	 * @return
	 */
	public ArrayList<L1PcInstance> getRecognizePlayer(L1Object object) {
		return getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
	}

	public L1PcInstance[] getAllPlayersToArray() {
		return _allPlayers.values().toArray(new L1PcInstance[_allPlayers.size()]);
	}

	private Collection<L1PcInstance> _allPlayerValues;

	public Collection<L1PcInstance> getAllPlayers() {
		Collection<L1PcInstance> vs = _allPlayerValues;
		return (vs != null) ? vs : (_allPlayerValues = Collections.unmodifiableCollection(_allPlayers.values()));
	}

	public Collection<L1NpcShopInstance> getAllNpcShop() {
		return Collections.unmodifiableCollection(_allNpcShop.values());
	}

	public Collection<L1RobotInstance> getAllRobot() {
		return Collections.unmodifiableCollection(_allRobot.values());
	}

	public Collection<GambleInstance> getAllGamble() {
		return Collections.unmodifiableCollection(_allGamble.values());
	}

	public Collection<L1FurnitureInstance> getAllFurniture() {
		return Collections.unmodifiableCollection(_allFurniture.values());
	}

	public Collection<L1TeleporterInstance> getAllTeleporter() {
		return Collections.unmodifiableCollection(_allTeleporter.values());
	}

	public Collection<L1GuardInstance> getAllGuard() {
		return Collections.unmodifiableCollection(_allGuard.values());
	}

	public Collection<L1CastleGuardInstance> getAllCastleGuard() {
		return Collections.unmodifiableCollection(_allCastleGuard.values());
	}

	/**
	 * 월드내에 있는 지정된 이름의 플레이어를 취득한다.
	 * 
	 * @param name - 플레이어명(소문자·대문자는 무시된다)
	 * @return 지정된 이름의 L1PcInstance. 해당 플레이어가 존재하지 않는 경우는 null를 돌려준다.
	 */
	public L1PcInstance getPlayer(String name) {
		Collection<L1PcInstance> pc = null;
		pc = getAllPlayers();
		for (L1PcInstance each : pc) {
			if (each == null)
				continue;
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	/** 겟 로봇 스래드 */
	public L1RobotInstance getRobot(String name) {
		Collection<L1RobotInstance> pc = getAllRobot();
		for (L1RobotInstance each : pc) {
			if (each == null)
				continue;
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	public L1NpcShopInstance getNpcShop(int id) {
		Collection<L1NpcShopInstance> npc = null;
		npc = getAllNpcShop();
		for (L1NpcShopInstance each : npc) {
			if (each == null)
				continue;
			if (each.getNpcId() == id) {
				return each;
			}
		}
		return null;
	}

	public L1NpcShopInstance getNpcShop(String name) {
		Collection<L1NpcShopInstance> pc = null;
		pc = getAllNpcShop();
		for (L1NpcShopInstance each : pc) {
			if (each == null)
				continue;
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	public GambleInstance getGamble(String name) {
		Collection<GambleInstance> pc = null;
		pc = getAllGamble();
		for (GambleInstance each : pc) {
			if (each == null)
				continue;
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	// _allPets의 뷰
	private Collection<L1PetInstance> _allPetValues;

	public Collection<L1PetInstance> getAllPets() {
		Collection<L1PetInstance> vs = _allPetValues;
		return (vs != null) ? vs : (_allPetValues = Collections.unmodifiableCollection(_allPets.values()));
	}

	// _allSummons의 뷰
	private Collection<L1SummonInstance> _allSummonValues;

	public Collection<L1SummonInstance> getAllSummons() {
		Collection<L1SummonInstance> vs = _allSummonValues;
		return (vs != null) ? vs : (_allSummonValues = Collections.unmodifiableCollection(_allSummons.values()));
	}

	// _allMerchant의 뷰
	private Collection<L1MerchantInstance> _allMerchantValues;

	public Collection<L1MerchantInstance> getAllMerchant() {
		Collection<L1MerchantInstance> vs = _allMerchantValues;
		return (vs != null) ? vs : (_allMerchantValues = Collections.unmodifiableCollection(_allMerchant.values()));
	}

	// _allFieldObject의 뷰
	private Collection<L1FieldObjectInstance> _allFieldObjectValues;

	public Collection<L1FieldObjectInstance> getAllField() {
		Collection<L1FieldObjectInstance> vs = _allFieldObjectValues;
		return (vs != null) ? vs : (_allFieldObjectValues = Collections.unmodifiableCollection(_allFieldObject.values()));
	}

	// _allItem의 뷰
	private Collection<L1ItemInstance> _allItemValues;

	public Collection<L1ItemInstance> getAllItem() {
		Collection<L1ItemInstance> vs = _allItemValues;
		return (vs != null) ? vs : (_allItemValues = Collections.unmodifiableCollection(_allitem.values()));
	}

	public final Map<Integer, L1Object> getAllVisibleObjects() {
		return _allObjects;
	}

	public final Map<Integer, L1Object>[] getVisibleObjects() {
		return _visibleObjects;
	}

	public final Map<Integer, L1Object> getVisibleObjects(int mapId) {
		return _visibleObjects[mapId];
	}

	public Object getRegion(Object object) {
		return null;
	}

	public void addWar(L1War war) {
		if (!_allWars.contains(war)) {
			_allWars.add(war);
		}
	}

	public void removeWar(L1War war) {
		if (_allWars.contains(war)) {
			_allWars.remove(war);
		}
	}

	// 추가
	public L1War[] get_wars() {
		return _allWars.toArray(new L1War[_allWars.size()]);
	}

	// _allWars의 뷰
	private List<L1War> _allWarList;

	public List<L1War> getWarList() {
		List<L1War> vs = _allWarList;
		return (vs != null) ? vs : (_allWarList = Collections.unmodifiableList(_allWars));
	}

	public void storeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp == null) {
			_allClans.put(clan.getClanName(), clan);
		}
	}

	public void removeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp != null) {
			_allClans.remove(clan.getClanName());
		}
	}

	public L1Clan getClan(String clan_name) {
		return _allClans.get(clan_name);
	}

	public L1Clan getClan(int id) {
		for (L1Clan clan : _allClans.values()) {
			if (clan.getClanId() == id)
				return clan;
		}
		return null;
	}

	// _allClans의 뷰
	private Collection<L1Clan> _allClanValues;

	public Collection<L1Clan> getAllClans() {
		Collection<L1Clan> vs = _allClanValues;
		return (vs != null) ? vs : (_allClanValues = Collections.unmodifiableCollection(_allClans.values()));
	}

	public void setWeather(int weather) {
		_weather = weather;
	}

	public int getWeather() {
		return _weather;
	}

	public L1PcInstance[] getAllPlayers3() {
		return _allPlayers.values().toArray(new L1PcInstance[_allPlayers.size()]);
	}

	public void set_worldChatElabled(boolean flag) {
		_worldChatEnabled = flag;
	}

	public boolean isWorldChatElabled() {
		return _worldChatEnabled;
	}

	public void setProcessingContributionTotal(boolean flag) {
		_processingContributionTotal = flag;
	}

	public boolean isProcessingContributionTotal() {
		return _processingContributionTotal;
	}

	public L1PetMember getPetMember() {
		return _PetMember;
	}

	public void setPetMember(L1PetMember pm) {
		_PetMember = pm;
	}

	/**
	 * 월드상에 존재하는 모든 플레이어에 패킷을 송신한다.
	 * 
	 * @param packet 송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacketToAll(ServerBasePacket packet) {
		Collection<L1PcInstance> pclist = null;
		pclist = getAllPlayers();
		_log.finest("players to notify : " + pclist.size());
		for (L1PcInstance pc : pclist) {
			if (pc != null)
				pc.sendPackets(packet);
		}
	}

	/**
	 * 월드상에 존재하는 모든 플레이어에 패킷을 송신한다.
	 * 
	 * @param packet 송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacketToAll(ServerBasePacket packet, boolean clear) {
		Collection<L1PcInstance> pclist = null;
		pclist = getAllPlayers();
		_log.finest("players to notify : " + pclist.size());
		for (L1PcInstance pc : pclist) {
			if (pc != null)
				pc.sendPackets(packet);
		}
		if (clear) {
			packet.clear();
			packet = null;
		}
	}

	public void broadcastPacketToAll(ServerBasePacket packet, boolean clear, boolean clear2) {
		Collection<L1PcInstance> pclist = null;
		pclist = getAllPlayers();
		_log.finest("players to notify : " + pclist.size());
		for (L1PcInstance pc : pclist) {
			if (pc != null && pc._KILMENT) {
				pc.sendPackets(packet);
			}
		}
		if (clear) {
			packet.clear();
			packet = null;
		}
	}

	/** 상점 오브젝트 찾아서 그 값 처리 */
	public L1Object isNpcShop(int Id) {
		Collection<L1MerchantInstance> Npc = getAllMerchant();
		for (L1MerchantInstance each : Npc) {
			if (each == null)
				continue;
			if (each.getNpcId() == Id) {
				return _allObjects.get(each.getId());
			}
		}
		return null;
	}

	/** 월드 맵 플레이어 찾아서 보관해줌 */
	public ArrayList<L1PcInstance> getMapPlayers(int Mapid) {
		L1Map Map = L1WorldMap.getInstance().getMap((short) Mapid);
		ArrayList<L1PcInstance> ObjectList = new ArrayList<L1PcInstance>();
		if (Map.getId() <= MAX_MAP_ID) {
			Collection<L1Object> MapObject = _visibleObjects[Map.getId()].values();
			for (L1Object Object : MapObject) {
				if (Object == null || Map != Object.getMap())
					continue;
				if (Object instanceof L1PcInstance) {
					ObjectList.add((L1PcInstance) Object);
				}
			}
		}
		return ObjectList;
	}

	/** 맵 몬스터 체크 몬스터나 엔피씨라면 그 체크 하도록 세팅 */
	public ArrayList<L1MonsterInstance> getVisibleMapObjects(int Mapid) {
		L1Map Map = L1WorldMap.getInstance().getMap((short) Mapid);
		ArrayList<L1MonsterInstance> ObjectList = new ArrayList<L1MonsterInstance>();
		if (Map.getId() <= MAX_MAP_ID) {
			Collection<L1Object> MapObject = _visibleObjects[Map.getId()].values();
			for (L1Object Object : MapObject) {
				if (Object == null || Map != Object.getMap())
					continue;
				if (Object instanceof L1MonsterInstance) {
					ObjectList.add((L1MonsterInstance) Object);
				}
			}
		}
		return ObjectList;
	}

	/** 맵에 특정 엔피씨 찾아서 그 에이피일때만 정보 넘겨주도록 세팅 */
	public L1MonsterInstance getVisibleMapObjects(int Mapid, int Npcid) {
		L1Map Map = L1WorldMap.getInstance().getMap((short) Mapid);
		if (Map.getId() <= MAX_MAP_ID) {
			Collection<L1Object> MapObject = _visibleObjects[Map.getId()].values();
			for (L1Object Object : MapObject) {
				if (Object == null || Map != Object.getMap())
					continue;
				if (Object instanceof L1MonsterInstance) {
					if (((L1MonsterInstance) Object).getNpcId() == Npcid)
						return ((L1MonsterInstance) Object);
				}
			}
		}
		return null;
	}

	/**
	 * 월드상에 존재하는 모든 플레이어에 서버 메세지를 송신한다.
	 * 
	 * @param message 송신하는 메세지
	 */
	public void broadcastServerMessage(String message) {
		broadcastPacketToAll(new S_SystemMessage(message), true);
	}

	public void broadcastServerMessage(String message, boolean ok) {
		broadcastPacketToAll(new S_SystemMessage(message), true, true);
	}
}