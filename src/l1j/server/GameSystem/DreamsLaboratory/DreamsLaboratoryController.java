package l1j.server.GameSystem.DreamsLaboratory;

import java.util.ArrayList;
import java.util.Collections;

import l1j.server.Config;
import l1j.server.GameSystem.Dungeon.DungeonInfo;
import l1j.server.GameSystem.Dungeon.DungeonSystem;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CreateItem;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ServerVersion;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class DreamsLaboratoryController implements Runnable {

	public boolean Status = true;
	
	private DungeonInfo DungeonRoom;
	private long GameTiemAttack = 0;
    private int GameMapId = 0, GameType = 0, GameRound = 0;
    
    private L1NpcInstance MainBose;
    
    private int[] FieldBoseNpc = new int[4];
    
    private boolean[] FieldBoseMente = new boolean[4];
    
    private int[][] _FieldBoseSpawn = new int[][]{ 
    		/** map number and direction */
    		{46407, 32800, 32825, 2},
			{46408, 32763, 32864, 4},
			{46406, 32799, 32901, 2},
			{46409, 32838, 32862, 4},
    };
    
    public static final int StartStage = 0;
    public static final int RoundStage1 = 1;
    public static final int RoundStage2 = 2;
    public static final int RoundStage3 = 3;
    public static final int RoundStage4 = 4;
    public static final int RoundStage5 = 5;
    public static final int RoundStage6 = 6;
    public static final int RoundStage7 = 7;
    public static final int RoundStage8 = 8;
    public static final int RoundStage9 = 9;
    public static final int RoundStage10 = 10;
    public static final int RoundStage11 = 11;
    public static final int RoundStage12 = 12;
    public static final int RoundStage13 = 13;
    public static final int RoundStage14 = 14;
    public static final int RoundStage15 = 15;
    public static final int RoundStage16 = 16;
    public static final int RoundStage17 = 17;
    public static final int RoundStage18 = 18;
    public static final int RoundStage19 = 19;
    public static final int RoundStage20 = 20;
    public static final int RoundStage21 = 21;
    public static final int RoundStage22 = 22;
    public static final int RoundStage23 = 23;
    public static final int RoundStage24 = 24;
    public static final int RoundStage25 = 25;
    public static final int RoundStage26 = 26;
    public static final int RoundStage27 = 27;
    public static final int RoundStage28 = 28;
    public static final int RoundStage29 = 29;
    public static final int RoundStage30 = 30;
    public static final int RoundStage31 = 31;
    public static final int RoundStage32 = 32;
   
    private L1NpcInstance DefendTower;
    private ArrayList<L1DoorInstance> FireWall;
    
    private int[][] _FireWallSpawn = new int[][]{ 
    		/** map number and direction */
    		{32797, 32851, 2},
			{32787, 32860, 4},
			{32796, 32875, 2},
			{32812, 32860, 4},
    };
    
    private int[][] _LocationSpawn = new int[][]{ 
    		/** Check the coordinates to be spawned */
    		{32800, 32845, 2},
			{32782, 32863, 4},
			{32800, 32880, 2},
			{32819, 32863, 4},
    };
    
    private int[][] _FireObjid = new int[][]{ 
    		/** map number and direction */
    		{46402, 32798, 32854, 3},
			{46402, 32787, 32864, 3},
			{46402, 32798, 32875, 3},
    		{46402, 32809, 32865, 3},
    };
    
    private int[][] _BuffuffObjid = new int[][]{ 
    		/** map number and direction */
    		{47220, 32800, 32863, 3},
			{47221, 32800, 32863, 3},
    };
    
    private int[][] _1RoundSpawn = new int[][]{ 
    		/** map number and direction */
    		{46411, 46422, 46431, 46441},
			{46411, 46422, 46431, 46441},
			{46410, 46420, 46430, 46440},
    };
    
    private int[][] _2RoundSpawnType1 = new int[][]{ 
    		/** map number and direction */
    		{46412, 46423, 46432, 46442},
			{46412, 46423, 46432, 46442},
    		{46412, 46423, 46432, 46442},
			{46412, 46423, 46432, 46442},
    };
    
    private int[][] _2RoundSpawnType2 = new int[][]{ 
    		/** map number and direction */
    		{46412, 46423, 46432, 46442},
			{46412, 46423, 46432, 46442},
			{46410, 46420, 46430, 46440},
			{46410, 46420, 46430, 46440},
    };
    
    private int[][] _RoundSpawnBonus1 = new int[][]{ 
    		/** map number and direction */
    		{46450, 32802, 32868, 3},
			{46450, 32795, 32860, 3},
			{46450, 32804, 32870, 3},
			{46450, 32806, 32864, 3},
			
			{46450, 32808, 32866, 6},
			{46450, 32793, 32858, 6},
			{46450, 32797, 32854, 6},
			{46450, 32799, 32856, 6},
    };

    private int[][] _RoundSpawnBonus2 = new int[][]{ 
    		/** map number and direction */
    		{46450, 32802, 32868, 3},
    		{46450, 32795, 32860, 3},
    		{46450, 32804, 32870, 3},
    		{46450, 32806, 32864, 3},
    		{46450, 32790, 32861, 3},
    		{46450, 32811, 32863, 3},
    		
    		{46450, 32808, 32866, 6},
    		{46450, 32793, 32858, 6},
    		{46450, 32797, 32854, 6},
    		{46450, 32799, 32856, 6},
    		{46450, 32792, 32863, 6},
    		{46450, 32809, 32861, 6},
    };
    
    private int[][] _RoundSpawnBonus3 = new int[][]{ 
    		/** map number and direction */
    		{46450, 32797, 32870, 3},
			{46450, 32793, 32866, 3},
    		{46450, 32798, 32869, 3},
			{46450, 32804, 32855, 3},
			
    		{46450, 32794, 32865, 3},
			{46450, 32808, 32859, 3},
    		{46450, 32807, 32860, 3},
			{46450, 32803, 32856, 3},
    };
    
    private int[][] _RoundSpawnBonus4 = new int[][]{ 
    		/** map number and direction */
			{46450, 32797,	32870, 3},
			{46450, 32793,	32866, 3},
			{46450, 32798,	32869, 3},
			{46450, 32804,	32855, 3},
			{46450, 32805,	32857, 3},
			{46450, 32795,	32868, 3},
			
			{46450, 32794,	32865, 3},
			{46450, 32808,	32859, 3},
			{46450, 32807,	32860, 3},
			{46450, 32803,	32856, 3},
			{46450, 32805,	32858, 3},
			{46450, 32796,	32867, 3},
    };
    
	/** Part of the story progressing along with the poem */
	public DreamsLaboratoryController(DungeonInfo DungeonInfo, int Type, int Mapid) {
		try{
			boolean StartOpen = true;
			ArrayList<L1PcInstance> User = null;
			int Counter = 0;
			while (StartOpen) {
				if(DungeonInfo == null || Counter == 60) StartOpen = false;
				/** Adjust the counter so that it can rotate for 1 minute after selecting a user */
				User = DungeonInfo.isDungeonInfoUset(); Counter++;
				if(User.size() != DungeonInfo.DungeonList.size()) continue;
				
				StartOpen = false;
				for(L1PcInstance PcList : User)
					if(!PcList.isDungeonInfoCheck()) StartOpen = true;
				if(StartOpen) Thread.sleep(100L);
			}
			
			L1Party Party = new L1Party();
			for(L1PcInstance PcList : User){
				PcList.sendPackets(new S_ServerVersion(S_ServerVersion.Test, PcList, true, DungeonInfo), true);
				PcList.sendPackets(new S_ServerVersion(S_ServerVersion.Test2, PcList, true, DungeonInfo), true);
				PcList.sendPackets(new S_ServerVersion(S_ServerVersion.Test3, PcList, true, DungeonInfo), true);
				for(L1PcInstance UserList : DungeonInfo.isDungeonInfoUset()){
					if(PcList.getId() != UserList.getId())
						PcList.sendPackets(new S_ServerVersion(S_ServerVersion.Test, UserList, false, DungeonInfo), true);
					PcList.sendPackets(new S_ServerVersion(S_ServerVersion.Test3, UserList, false, DungeonInfo), true);
				}
				Party.addInterMember(PcList);
			}
			
			/** game settings section */
			GameType = Type;
			GameMapId = Mapid;
			DungeonRoom = DungeonInfo;
			
			/** Clean up the existing base spawn part */
			DefendTower = Spawn(32800, 32864, (short)GameMapId, 0, 46400, 0, false);
			
			/** Information about door leaf spawning */
			int LocX, LocY, Heading;
			FireWall = new ArrayList<L1DoorInstance>();
			for (int i = 0; i < _FireWallSpawn.length; i++) {
				for (int Loc = 0; Loc < 7; Loc++) {
					if(i == 0 && Loc == 6) continue;
					Heading = _FireWallSpawn[i][2];
					LocX = _FireWallSpawn[i][0] + (Heading == 2 ? Loc : 0);
					LocY = _FireWallSpawn[i][1] + (Heading == 4 ? Loc : 0);
					FireWall.add(DoorSpawn(LocX, LocY, (short)GameMapId, Heading == 2 ? 0 : 1 , 46401));
				}
			}
			
			/** Hardin Action Basically, spray a cycle impactor and rest for a while*/
			Thread.sleep(2000);
			for (L1PcInstance Player : PlayerList())
				Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 4), true);
			
			/** Give Orim Impact to all users */
			Thread.sleep(5000);
			for (L1PcInstance Player : PlayerList())
				Player.sendPackets(new S_CreateItem(true), true);
			
			/** Start message 1 */
			Thread.sleep(20000);
			for (L1PcInstance Player : PlayerList())
				Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "中央の水晶体を保護します。"), true);
			
			/** comment 2 */
			Thread.sleep(10000);
			for (L1PcInstance Player : PlayerList())
				Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "タイムアウトまたは修正が破壊されると失敗します。"), true);
			
			/** comment 3 */
			Thread.sleep(10000);
			int[] AllBuffSkill = { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.NATURES_TOUCH, L1SkillId.IRON_SKIN};
			L1SkillUse L1skilluse = new L1SkillUse();
			for (L1PcInstance Player : PlayerList()){
				Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "森はあなたに小さな力を与えます。"), true);
				for (int i = 0; i < AllBuffSkill.length; i++) 
					L1skilluse.handleCommands(Player, AllBuffSkill[i], Player.getId(), Player.getX(), Player.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
				
				/** Hill Impact and Swan */
				Player.sendPackets(new S_SkillSound(Player.getId(), 9009), true);
				Broadcaster.broadcastPacket(Player, new S_SkillSound(Player.getId(), 9009), true);
				Player.sendPackets(new S_SkillSound(Player.getId(), 11722), true);
				Broadcaster.broadcastPacket(Player, new S_SkillSound(Player.getId(), 11722), true);
			}
			
			/** Comment No. 4 Game play begins*/
			Thread.sleep(4000);
			/** Ÿ�Ծ��� �ð� ��� 60 * 60 * 15 15�� */
			GameTiemAttack = System.currentTimeMillis() + (15 * 60 * 1000);
			for (L1PcInstance Player : PlayerList()){
				Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "呪われた群れが集まり始めます。"), true);
				Player.sendPackets(new S_ServerVersion(DungeonInfo, null), true);
			}
			
			/** Field Boss Spawn: Kaspar crew */
			L1NpcInstance FieldBose;
			for (int Spawn = 0; Spawn < _FieldBoseSpawn.length; Spawn++) {
				FieldBose = Spawn(_FieldBoseSpawn[Spawn][1], _FieldBoseSpawn[Spawn][2], (short)GameMapId, _FieldBoseSpawn[Spawn][3], _FieldBoseSpawn[Spawn][0], 0, false);
				FieldBoseNpc[Spawn] = FieldBose.getId();
				FieldBoseMente[Spawn] = false;
			}
			
			/** time progress thread open*/
			GeneralThreadPool.getInstance().schedule(new GameTiemAttack(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Times thread for updating employee information*/
	class GameTiemAttack implements Runnable {
		public boolean Mente = false;

		@Override
		public void run() {
			try {
				if(GameRound == -1 || !Status) return;
				/** Check field npc to see if it's alive */
				if(GameRound >= 21 && GameRound <= 24){
					L1NpcInstance Npc;
					for (int i = 0; i < FieldBoseNpc.length; i++) {
						if(FieldBoseNpc[i] == 0) continue;  
						Npc = (L1NpcInstance) L1World.getInstance().findObject(FieldBoseNpc[i]);
						if (Npc == null || Npc.isDead() || Npc._destroyed) {
							FieldBoseNpc[i] = 0;
							GeneralThreadPool.getInstance().schedule(new GameTiemMente(i), 100);
						}
					}
				}
				/** If the defense tower is idle or in a die state, the end packet is processed. */
				if (DefendTower == null || DefendTower.isDead()) {
					GameRound = 32;
				}else{
					/** If the current time is higher than the time attack time*/
					if(System.currentTimeMillis() >= GameTiemAttack){
						GameRound = 32;
					}else if(!Mente){
						int Percent = (int) Math.round((double) DefendTower.getCurrentHp() / (double) DefendTower.getMaxHp() * 100);
						if (Percent < 20) {
							for (L1PcInstance Player : PlayerList()){
								Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "守護塔の血はほとんどありません。"), true);
							}
							Mente = true;
						}
					}
				}
				GeneralThreadPool.getInstance().schedule(this, 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class GameTiemMente implements Runnable {
		public int _Type;
		
		public GameTiemMente(int Type){
			_Type = Type;
		}
		
		@Override
		public void run() {
			try {	
				switch (_Type) {	
					/** Cursed Sema */
					case 0:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "セマ..ごめんなさい.."), true);
						
						/** 10 seconds later*/
						Thread.sleep(10 * 1000);
						FieldBoseMente[_Type] = true;
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "炎のモンスター召喚が止まりました。"), true);
						break;
						
					/**Cursed Walthersar*/
					case 1:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "バルタザール..その痛みから君を救ってあげるよ.."), true);
						
						/** 10 seconds later */
						Thread.sleep(10 * 1000);
						FieldBoseMente[_Type] = true;
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "風量のモンスター召喚が止まりました。"), true);
						break;
						
					/** Cursed Caspar*/
					case 2:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "カスパ.. 真実を知るよ.."), true);
						
						/** 10 seconds later */
						Thread.sleep(10 * 1000);
						FieldBoseMente[_Type] = true;
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "大地のモンスター召喚が止まりました。"), true);
						break;
						
					/** Cursed Merchior */
					case 3:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "メルキオール…少し待って…"), true);
						
						/** 10 seconds later*/
						Thread.sleep(10 * 1000);
						FieldBoseMente[_Type] = true;
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "深海のモンスター召喚が止まりました。"), true);
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
        while (Status) {
			try {	
				ArrayList<L1PcInstance> SkillUser;
				int SpawnNpc, Counter, SpawnCounter;
				boolean BonusSpawn;
				L1Location Loc;
				if(PlayerList().size() == 0) Reset(true);
				switch (GameRound) {		
					case StartStage:
						BonusSpawn = GameType == 0 ? false : true;
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage1:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage2:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = _RoundSpawnBonus1[i][0];
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = _RoundSpawnBonus2[i][0];
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
						}
						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2],SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage3:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay */
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage4:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus3.length; i++) {
								SpawnNpc = _RoundSpawnBonus3[i][0];
								Spawn(_RoundSpawnBonus3[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus3[i][3], SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus4.length; i++) {
								SpawnNpc = _RoundSpawnBonus4[i][0];
								Spawn(_RoundSpawnBonus4[i][1], _RoundSpawnBonus4[i][2], (short)GameMapId, _RoundSpawnBonus4[i][3], SpawnNpc, 0, false);
							}
						}
						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					/** poison page */
					case RoundStage5:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "呪われた霧は深刻な汚染ダメージを与えます。"), true);
						}
						
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = _BuffuffObjid[i][0];
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						BonusSpawn = GameType == 0 ? false : true;						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage6:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
			
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = _RoundSpawnBonus1[i][0];
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = _RoundSpawnBonus2[i][0];
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
						}
						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay */
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					case RoundStage7:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						BonusSpawn = GameType == 0 ? false : true;
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						Thread.sleep(5 * 1000);
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = _BuffuffObjid[i][0];
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(10 * 1000);
						GameRound++;
						break;
						
					case RoundStage8:
						/** Flashing for stage change*/
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = _RoundSpawnBonus1[i][0];
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus3.length; i++) {
								SpawnNpc = _RoundSpawnBonus3[i][0];
								Spawn(_RoundSpawnBonus3[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus3[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = _RoundSpawnBonus2[i][0];
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus4.length; i++) {
								SpawnNpc = _RoundSpawnBonus4[i][0];
								Spawn(_RoundSpawnBonus4[i][1], _RoundSpawnBonus4[i][2], (short)GameMapId, _RoundSpawnBonus4[i][3],SpawnNpc, 0, false);
							}
						}
						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						Thread.sleep(5 * 1000);
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = i == 0 ? 46404 : 46405;
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(10 * 1000);
						GameRound++;
						break;
					
					case RoundStage9:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
			
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
						}
						
						for (int i = 0; i < _1RoundSpawn.length; i++) {
							if(i == GameType && BonusSpawn){
								BonusSpawn = false;
								i--;
							}
							for (int Spawn = 0; Spawn < _1RoundSpawn[i].length; Spawn++) {
								SpawnNpc = _1RoundSpawn[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						/** 2 spawns start after 1 minute delay*/
						Thread.sleep(15 * 1000);
						GameRound++;
						break;
						
					/** Need to check for break time page conversion */
					case RoundStage10:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						GameRound++;
						break;
						
					/** Before rushing to the break page
					 * Check if all monsters have been caught*/
					case RoundStage11:
						/** If true, the monster remains.*/
						if(MonList()) continue;
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "私の次の攻撃に備えてください。"), true);
						GameRound++;
						/**rest time */
						Thread.sleep(30 * 1000);
						break;
					
					case RoundStage12:
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 6), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "呪いのオーラがさらに強まっています。"), true);
						}
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);
						
						GameRound++;
						Thread.sleep(20 * 1000);
						break;
						
					case RoundStage13:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						

						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
								SpawnNpc = _2RoundSpawnType2[i][Spawn];
								if(SpawnNpc == 46420) SpawnNpc++;
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						GameRound++;
						Thread.sleep(20 * 1000);
						break;
					
					case RoundStage14:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);
						
						GameRound++;
						Thread.sleep(15 * 1000);
						break;
					
					case RoundStage15:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = _RoundSpawnBonus1[i][0];
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = _RoundSpawnBonus2[i][0];
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
						}
						GameRound++;
						Thread.sleep(5 * 1000);
						break;
						
					case RoundStage16:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						

						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
								SpawnNpc = _2RoundSpawnType2[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						GameRound++;
						Thread.sleep(20 * 1000);
						break;
				
					case RoundStage17:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);
						
						Thread.sleep(15 * 1000);
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = _BuffuffObjid[i][0];
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						GameRound++;
						Thread.sleep(5 * 1000);
						break;
					
					case RoundStage18:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
								SpawnNpc = _2RoundSpawnType2[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						GameRound++;
						Thread.sleep(10 * 1000);
						break;
						
					case RoundStage19:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = _RoundSpawnBonus1[i][0];
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus3.length; i++) {
								SpawnNpc = _RoundSpawnBonus3[i][0];
								Spawn(_RoundSpawnBonus3[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus3[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = _RoundSpawnBonus2[i][0];
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus4.length; i++) {
								SpawnNpc = _RoundSpawnBonus4[i][0];
								Spawn(_RoundSpawnBonus4[i][1], _RoundSpawnBonus4[i][2], (short)GameMapId, _RoundSpawnBonus4[i][3],SpawnNpc, 0, false);
							}
						}
						
						GameRound++;
						Thread.sleep(10 * 1000);
						
					/** comment page */
					case RoundStage20:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "森の涙が...炎の壁を潜む..."), true);
						}
						
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);
						
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "壁を越えて、呪いを召喚する悲しい司祭がいます。"), true);
						
						if(BonusSpawn)
							for (L1DoorInstance Door : FireWall) Door.open();
						
						GameRound++;
						Thread.sleep(20 * 1000);
						break;
					
					/** The door is open from here.
					 * Field boss spawn check */
					case RoundStage21:
						/** Flashing for stage change */
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
								if(FieldBoseMente[Spawn]) continue;
								SpawnNpc = _2RoundSpawnType2[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = _BuffuffObjid[i][0];
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						GameRound++;
						Thread.sleep(20 * 1000);
						break;
							
					case RoundStage22:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									if(FieldBoseMente[Spawn]) continue;
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									if(FieldBoseMente[Spawn]) continue;
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);
						
						GameRound++;
						Thread.sleep(15 * 1000);
						break;
					
					case RoundStage23:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							
							}
						}
						
						GameRound++;
						Thread.sleep(5 * 1000);
						break;
					
					case RoundStage24:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
								if(FieldBoseMente[Spawn]) continue;
								SpawnNpc = _2RoundSpawnType2[i][Spawn];
								Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
							}
							Thread.sleep(1000);
						}
						Thread.sleep(1000);
						
						Thread.sleep(10 * 1000);
						for (int i = 0; i < _FireObjid.length; i++) {
							SpawnNpc = _FireObjid[i][0];
							EffectSpawn(_FireObjid[i][1], _FireObjid[i][2], (short)GameMapId, _FireObjid[i][3], SpawnNpc, 12);
						}
						
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = i == 0 ? 46404 : 46405;
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						
						GameRound++;
						Thread.sleep(10 * 1000);
						break;
						
					/** three monsters page*/
					case RoundStage25:
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "ああ、それはモンスターです。"), true);
						}
						
						for (int i = 0; i < _2RoundSpawnType2.length; i++) {
							if(FieldBoseMente[i]) continue;
							/** If there is a castle boss, please delete it*/
							MonDelete(FieldBoseNpc[i]);
							SpawnNpc = 46461;
							Spawn(_LocationSpawn[i][0], _LocationSpawn[i][1], (short)GameMapId, _LocationSpawn[i][2], SpawnNpc, 0, true);
						}
						
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus3.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus3[i][1], _RoundSpawnBonus3[i][2], (short)GameMapId, _RoundSpawnBonus3[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							}
							for (int i = 0; i < _RoundSpawnBonus4.length; i++) {
								SpawnNpc = 46451;
								Spawn(_RoundSpawnBonus4[i][1], _RoundSpawnBonus4[i][2], (short)GameMapId, _RoundSpawnBonus4[i][3],SpawnNpc, 0, false);
							}
						}
						Thread.sleep(10 * 1000);
						
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = _BuffuffObjid[i][0];
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						Thread.sleep(10 * 1000);
						for (int i = 0; i < _BuffuffObjid.length; i++) {
							SpawnNpc = i == 0 ? 46404 : 46405;
							Loc = new L1Location(_BuffuffObjid[i][1], _BuffuffObjid[i][2], GameMapId).randomLocation(2, 10, true);
							Spawn(Loc.getX(), Loc.getY(), (short)GameMapId, _BuffuffObjid[i][3], SpawnNpc, 120, false);
						}
						
						GameRound++;
						Thread.sleep(5 * 1000);
						break;
						
					case RoundStage26:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						
						/** The maximum number of people spraying flame objects on users / Calculated as 2 */
						SkillUser = PlayerList();
						Collections.shuffle(SkillUser);
						Counter = BonusSpawn ? 2 : 3;  
						SpawnCounter = SkillUser.size() > Counter ? SkillUser.size() / Counter : 1;  
						for (L1PcInstance Player : SkillUser){
							if(SpawnCounter == 0) continue;
							EffectSpawn(Player.getX(), Player.getY(), (short)GameMapId, Player.getMoveState().getHeading(), 46403, 8);
							SpawnCounter--;
						}
						
						if(!BonusSpawn){
							for (int i = 0; i < _2RoundSpawnType1.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType1[i].length; Spawn++) {
									if(FieldBoseNpc[Spawn] == 0) continue;
									SpawnNpc = _2RoundSpawnType1[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}else{
							for (int i = 0; i < _2RoundSpawnType2.length; i++) {
								for (int Spawn = 0; Spawn < _2RoundSpawnType2[i].length; Spawn++) {
									if(FieldBoseNpc[i] == 0) continue;
									SpawnNpc = _2RoundSpawnType2[i][Spawn];
									Spawn(_LocationSpawn[Spawn][0], _LocationSpawn[Spawn][1], (short)GameMapId, _LocationSpawn[Spawn][2], SpawnNpc, 0, true);
								}
								Thread.sleep(1000);
							}
						}
						Thread.sleep(1000);		
						GameRound++;
						break;
					
					case RoundStage27:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 8), true);
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							for (int i = 0; i < _RoundSpawnBonus1.length; i++) {
								SpawnNpc = 46462;
								Spawn(_RoundSpawnBonus1[i][1], _RoundSpawnBonus1[i][2], (short)GameMapId, _RoundSpawnBonus1[i][3],SpawnNpc, 0, false);
							}
						}else{
							for (int i = 0; i < _RoundSpawnBonus2.length; i++) {
								SpawnNpc = 46462;
								Spawn(_RoundSpawnBonus2[i][1], _RoundSpawnBonus2[i][2], (short)GameMapId, _RoundSpawnBonus2[i][3],SpawnNpc, 0, false);
							
							}
						}
						Thread.sleep(1000);
						
						/** The maximum number of people spraying flame objects on users / Calculated as 2*/
						SkillUser = PlayerList();
						Collections.shuffle(SkillUser);
						Counter = BonusSpawn ? 2 : 3;  
						SpawnCounter = SkillUser.size() > Counter ? SkillUser.size() / Counter : 1;  
						for (L1PcInstance Player : SkillUser){
							if(SpawnCounter == 0) continue;
							EffectSpawn(Player.getX(), Player.getY(), (short)GameMapId, Player.getMoveState().getHeading(), 46403, 8);
							SpawnCounter--;
						}
						
						Thread.sleep(20 * 1000);
						GameRound++;
						break;
						
					/** boss page
					 * When you kill all mobs, you turn into a boss. */
					case RoundStage28:
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 7), true);
						GameRound++;
						break;
						
					/** boss page*/
					case RoundStage29:
						/** If true, the monster remains. */
						if(MonList()) continue;
						
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 6), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "呪われたオーラが近づき始めます。"), true);
						}
						
						
						BonusSpawn = GameType == 0 ? false : true;
						if(!BonusSpawn){
							SpawnNpc = 46482;
							MainBose = Spawn(32800, 32845, (short)GameMapId, 3,SpawnNpc, 0, true);
						}else{
							SpawnNpc = 46480;
							MainBose = Spawn(32800, 32845, (short)GameMapId, 3,SpawnNpc, 0, true);
						}
						Thread.sleep(5 * 1000);
						
						/** The maximum number of people spraying flame objects on users / Calculated as 2*/
						for (L1PcInstance Player : PlayerList())
							EffectSpawn(Player.getX(), Player.getY(), (short)GameMapId, Player.getMoveState().getHeading(), 46403, 8);
						
						GameRound++;
						break;
					
					/** Packet after end of boss page */
					case RoundStage30:
						/** If the boss monster is dead or not, switch to the last page*/
						if(MainBose == null || MainBose.isDead() || MainBose._destroyed){
							GameRound++;
						}else continue;
						
					/** End Packet Handling */
					case RoundStage31:
						/** If the boss monster is dead or not, switch to the last page*/
						L1ItemInstance Item = null;
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "すべての攻撃を防ぎました。"), true);
							Player.sendPackets(new S_CreateItem(false), true);
							/** Returning with experience bonus item acquisition*/
							//Player.경험치보상(Player, Config.NewCharactere, 1000);
							int addexp = 0;
							// Quebo Sang 52~64 10% 65~75 5% 76 1
							addexp = (int) (240000 * 180 * Config.RATE_XP);
							double exppenalty = ExpTable.getPenaltyRate(Player.getLevel());
							addexp *= exppenalty;
							if (addexp != 0) {
								int level = ExpTable.getLevelByExp(Player.getExp() + addexp);
								if (level > Config.MAXLEVEL) {
									Player.sendPackets(new S_SystemMessage("レベル制限のため、これ以上経験値を獲得することはできません。"), true);
								} else
									Player.addExp(addexp);
								Player.sendPackets(new S_SkillSound(Player.getId(), 3944), true);
								Broadcaster.broadcastPacket(Player, new S_SkillSound(Player.getId(), 3944), true);
							}
							try {
								Player.save();
							    } catch (Exception e) {
							    	e.printStackTrace();
							}
							Item = Player.getInventory().storeItem(GameType == 0 ? 400200 : 400201, 1);
							Player.sendPackets(new S_ServerMessage(403, Item.getName()), true);
						}
					
						Thread.sleep(15 * 1000);
						
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "森林を助けることに成功しました。"), true);
						Thread.sleep(5 * 1000);
						
						for (L1PcInstance Player : PlayerList())
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "これで施工旅行が終了します。"), true);
						Thread.sleep(10 * 1000);
						
						for (int i = 0; i < 2; i++) {
							if(i == 0){
								SpawnNpc = 46485;
								Spawn(32811, 32863, (short)GameMapId, 6, SpawnNpc, 0, true);
							}else if(i == 1){
								SpawnNpc = 46486;
								EffectSpawn(32808, 32863, (short)GameMapId, 6, SpawnNpc, 600);
							}
						}
						
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "森の前に作成されたテレポート魔法陣に移動します。"), true);
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "あなたの前の日にアインハザードの祝福があります。"), true);
						}
						
						Reset(false);
						GameRound = -1;
						break;
						
					/** game failure handling*/
					case RoundStage32:
						/** Make all users die state
						 * No more page rotation due to thread termination*/
						for (L1PcInstance Player : PlayerList()){
							Player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "ダンジョン攻略に失敗しました。"), true);
							Player.setCurrentHp(0);
							Player.death(null);
						}
						
						Reset(false);
						GameRound = -1;
						break;
							
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try{
					Thread.sleep(100);
				} catch (Exception e) {}
			}
        }
	}

	private ArrayList<L1PcInstance> PlayerList() {
		return L1World.getInstance().getMapPlayers(GameMapId);
	}
	
	private void MonDelete(int NpcId) {
		L1MonsterInstance Mon = L1World.getInstance().getVisibleMapObjects(GameMapId, NpcId);
		if(Mon != null) Mon.deleteMe();
	}
	
	private boolean MonList() {
		boolean Check = false;
		for(L1MonsterInstance Mon : L1World.getInstance().getVisibleMapObjects(GameMapId)){
			/** A partial crystal ball used as a field boss is also included.*/
			if(Mon.getNpcId() >= 46406 && Mon.getNpcId() <= 46409) continue;
			if(Mon.getNpcId() == 46400) continue;
			if(Mon.isDead() || Mon._destroyed) continue;
			Check = true;
		}
		return Check;
	}
	
	private L1NpcInstance Spawn(int X, int Y, short MapId, int Heading, int NpcId, int DeleteTime, boolean Attack) {
		try {
			L1NpcInstance Npc = NpcTable.getInstance().newNpcInstance(NpcId);
			Npc.setId(ObjectIdFactory.getInstance().nextId());
			Npc.setMap(MapId);
			Npc.getLocation().set(X, Y, MapId);
			Npc.getLocation().forward(Heading);
			Npc.setHomeX(Npc.getX());
			Npc.setHomeY(Npc.getY());
			Npc.getMoveState().setHeading(Heading);
			if(Attack) Npc.setTarget(DefendTower);
		
			for (L1PcInstance Pc : L1World.getInstance().getVisiblePlayer(Npc)) {
				Npc.onPerceive(Pc);
				if(Npc.getNpcId() == 46450)
					Pc.sendPackets(new S_DoActionGFX(Npc.getId(), ActionCodes.ACTION_Appear), true);
			}

			L1World.getInstance().storeObject(Npc);
			L1World.getInstance().addVisibleObject(Npc);

			if(Attack) Npc.onNpcAI();
			Npc.getLight().turnOnOffLight();
			Npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (DeleteTime > 0) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(Npc, DeleteTime * 1000);
				timer.begin();
			} else {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(Npc, 3600 * 1000);
				timer.begin();
			}
			return Npc;
		} catch (Exception e) {}
		return null;
	}
	
	private L1EffectInstance EffectSpawn(int X, int Y, short MapId, int Heading, int NpcId, int DeleteTime) {
		try {
			L1EffectInstance Effect = (L1EffectInstance) NpcTable.getInstance().newNpcInstance(NpcId);
			Effect.setId(ObjectIdFactory.getInstance().nextId());
			Effect.setMap(MapId);
			Effect.getLocation().set(X, Y, MapId);
			Effect.setHomeX(Effect.getX());
			Effect.setHomeY(Effect.getY());
			Effect.getMoveState().setHeading(Heading);
		
			for (L1PcInstance Pc : L1World.getInstance().getVisiblePlayer(Effect)) {
				Effect.onPerceive(Pc);
			}

			L1World.getInstance().storeObject(Effect);
			L1World.getInstance().addVisibleObject(Effect);
			
			if (DeleteTime > 0) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(Effect, DeleteTime * 1000);
				timer.begin();
			} else {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(Effect, 3600 * 1000);
				timer.begin();
			}
			return Effect;
		} catch (Exception e) {}
		return null;
	}
	
	private L1DoorInstance DoorSpawn(int X, int Y, short MapId, int Heading, int npcId) {
		try {
			L1DoorInstance Door = (L1DoorInstance) NpcTable.getInstance().newNpcInstance(npcId);
			Door.setId(ObjectIdFactory.getInstance().nextId());
			Door.setDoorId(npcId);
			Door.setMap(MapId);
			Door.getLocation().set(X, Y, MapId);
			Door.setHomeX(Door.getX());
			Door.setHomeY(Door.getY());
			Door.setDirection(Heading);
			Door.setLeftEdgeLocation(Heading == 0 ? Door.getX() : Door.getY());
			Door.setRightEdgeLocation(Heading == 0 ? Door.getX() : Door.getY());
			Door.setMaxHp(0);
			Door.setCurrentHp(0);
			Door.setKeeperId(0);
			
			Door.setOpenStatus(ActionCodes.ACTION_Close);
			for (L1PcInstance Pc : L1World.getInstance().getVisiblePlayer(Door)){
				Door.onPerceive(Pc);
				Pc.sendPackets(new S_DoActionGFX(Door.getId(), ActionCodes.ACTION_Close), true);
			}
			
			L1World.getInstance().storeObject(Door);
			L1World.getInstance().addVisibleObject(Door);
			
			Door.isPassibleDoor(false);
			return Door;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void Reset(boolean Type) {
		try {
			if(Type){
				Object_Delete();
				Status = false;
			}
			DungeonSystem.DungeonInfoRemove(DungeonRoom.RoomNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Object_Delete() {
		for (L1Object ob : L1World.getInstance().getVisibleObjects(GameMapId).values()) {
			if (ob == null 
				|| ob instanceof L1DollInstance 
				|| ob instanceof L1SummonInstance 
				|| ob instanceof L1PetInstance)
				continue;
			if (ob instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) ob;
				if (npc._destroyed || npc.isDead()) continue;
				npc.deleteMe();
			}
		}
		for (L1ItemInstance obj : L1World.getInstance().getAllItem()) {
			if (obj.getMapId() != GameMapId) continue;
			L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
			groundInventory.removeItem(obj);
		}
	}
}