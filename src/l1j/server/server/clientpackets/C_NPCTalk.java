package l1j.server.server.clientpackets;

import java.util.logging.Logger;
import server.LineageClient;
//import l1j.server.IndunSystem.MiniGame.L1Gambling;
//import l1j.server.server.GameClient;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NcoinShop;
import l1j.server.server.serverpackets.S_PremiumShopSellList;
import l1j.server.server.serverpackets.S_TelePortUi;

public class C_NPCTalk extends ClientBasePacket {

	private static final String C_NPC_TALK = "[C] C_NPCTalk";
	private static Logger _log = Logger.getLogger(C_NPCTalk.class.getName());

	private static String[] action = new String[] { "T_talk island", "T_gludio", "T_orc", "T_woodbec", "T_silver knight", "T_kent", "T_giran", "T_heine",
			"T_werldern", "T_oren", "T_aden", "T_scave", "T_behemoth", "T_silveria", "T_pcbang", "T_gludio_lab", "D_talk island", "D_gludio", "D_elven",
			"D_training", "D_barlog", "D_dragon valley", "D_eva king", "D_ivory tower", "D_yahee", "F_shelob", "F_orc forest", "F_ruin of death", "F_desert",
			"F_dragon valley", "F_halpas", "F_valakas", "F_jungle", "F_heine", "F_mirror", "F_elmor", "F_orenwall", "F_lindvior", "F_giant", "D_atuba",
			"D_eva kingdom" };
	// , "T_gludio_lab"
	private static String[] claudiaact = new String[] { "Tel_talking", "Tel_watera", "Tel_waterb", "Tel_spidercave", "Tel_field", "Tel_underworld",
			"Tel_orcvil", "Tel_deathcastle", "Tel_swamp" };

	private static int[] T_talk_island = new int[] { 0, 1175, 1481, 1458, 1884, 1461, 1727, 2123, 2087, 2470, 2429, 2024, 2107, 2470, 0, 140, 1343, 1717, 1905,
			1717, 1945, 2328, 2532, 2663, 47, 1447, 1327, 1647, 1799, 1662, 2085, 1928, 2060, 2251, 2247, 2155, 2493, 1000, 1000, 1000, 1000 };

	private static int[] T_gludio = new int[] { 1175, 0, 321, 283, 709, 298, 552, 948, 927, 1310, 1254, 864, 947, 1405, 0, 1315, 169, 557, 731, 542, 785, 1153,
			1372, 1503, 1207, 287, 167, 473, 639, 487, 925, 768, 885, 1077, 1087, 981, 1318, 1000, 1000, 1000, 1000 };

	private static int[] T_orc = new int[] { 1481, 321, 0, 567, 845, 421, 687, 1083, 671, 989, 1389, 543, 627, 1085, 0, 1554, 340, 237, 866, 677, 464, 1289,
			1051, 1182, 1527, 76, 153, 608, 328, 623, 605, 874, 1021, 1212, 979, 1116, 1453, 1000, 1000, 1000, 1000 };

	private static int[] T_woodbec = new int[] { 1458, 283, 567, 0, 426, 544, 794, 665, 1173, 1556, 971, 1110, 1193, 1651, 0, 1598, 227, 803, 447, 259, 1031,
			870, 1618, 1749, 1411, 533, 413, 189, 885, 453, 1171, 1014, 602, 793, 1333, 1147, 1137, 1000, 1000, 1000, 1000 };

	private static int[] T_silver_knight = new int[] { 1884, 709, 845, 426, 0, 423, 629, 447, 1008, 1391, 590, 945, 1029, 1487, 0, 2024, 541, 832, 59, 266, 866,
			444, 1453, 1584, 1837, 921, 735, 237, 721, 289, 1007, 849, 224, 471, 1169, 982, 973, 1000, 1000, 1000, 1000 };

	private static int[] T_kent = new int[] { 1461, 298, 421, 544, 423, 0, 266, 662, 629, 1012, 968, 566, 649, 1107, 0, 1601, 317, 409, 445, 645, 487, 867,
			1074, 1205, 1505, 497, 312, 440, 341, 201, 627, 470, 599, 791, 789, 695, 1032, 1000, 1000, 1000, 1000 };

	private static int[] T_giran = new int[] { 1727, 552, 687, 794, 629, 266, 0, 396, 379, 762, 702, 380, 399, 857, 0, 1867, 567, 675, 571, 895, 275, 601, 824,
			855, 1755, 763, 578, 690, 359, 341, 377, 220, 405, 525, 539, 429, 766, 1000, 1000, 1000, 1000 };

	private static int[] T_heine = new int[] { 2123, 948, 1083, 665, 447, 662, 396, 0, 561, 945, 306, 776, 582, 1040, 0, 2263, 779, 1071, 388, 713, 671, 205,
			1007, 1137, 2076, 1159, 974, 507, 755, 461, 560, 403, 223, 129, 772, 535, 526, 1000, 1000, 1000, 1000 };

	private static int[] T_werldern = new int[] { 2087, 927, 671, 1173, 1008, 629, 379, 561, 0, 383, 718, 364, 111, 479, 0, 2160, 946, 659, 949, 1274, 259, 725,
			445, 576, 2133, 747, 759, 1069, 343, 719, 123, 203, 784, 541, 307, 445, 782, 1000, 1000, 1000, 1000 };

	private static int[] T_oren = new int[] { 2470, 1310, 989, 1556, 1391, 1012, 762, 945, 383, 0, 801, 453, 363, 95, 0, 2543, 1329, 753, 133, 1657, 525, 1109,
			91, 193, 2517, 1023, 1143, 1452, 671, 1103, 385, 542, 1167, 920, 223, 409, 693, 1000, 1000, 1000, 1000 };

	private static int[] T_aden = new int[] { 2429, 1254, 1389, 971, 590, 968, 702, 306, 718, 801, 0, 1082, 829, 897, 0, 2569, 1085, 1377, 531, 856, 977, 307,
			863, 994, 2382, 1465, 1280, 781, 1061, 767, 841, 515, 369, 177, 579, 392, 383, 1000, 1000, 1000, 1000 };

	private static int[] T_scave = new int[] { 2024, 864, 543, 1110, 945, 566, 380, 776, 364, 453, 1082, 0, 253, 541, 0, 2097, 883, 307, 887, 1211, 105, 971,
			508, 639, 2071, 577, 697, 1006, 225, 657, 241, 567, 721, 905, 671, 809, 1146, 1000, 1000, 1000, 1000 };

	private static int[] T_behemoth = new int[] { 2107, 947, 627, 1193, 1029, 649, 399, 582, 111, 363, 829, 253, 0, 458, 0, 2181, 967, 547, 970, 1295, 163, 746,
			425, 555, 2154, 660, 780, 1089, 308, 740, 22, 314, 805, 652, 419, 556, 893, 1000, 1000, 1000, 1000 };

	private static int[] T_silveria = new int[] { 2565, 1405, 1085, 1651, 1487, 1107, 857, 1040, 479, 95, 897, 541, 458, 0, 0, 2639, 1425, 848, 1428, 1753, 621,
			1204, 37, 129, 2612, 1118, 1238, 1547, 766, 1198, 480, 637, 1263, 1015, 318, 505, 747, 1000, 1000, 1000, 1000 };

	private static int[] T_pcbang = new int[] { 2565, 1405, 1085, 1651, 1487, 1107, 857, 1040, 479, 95, 897, 541, 458, 0, 0, 2639, 1425, 848, 1428, 1753, 621,
			1204, 37, 129, 2612, 1118, 1238, 1547, 766, 1198, 480, 637, 1263, 1015, 318, 505, 747, 1000, 1000, 1000, 1000 };

	private static int[] T_claudia = new int[] { 100, 0, 0, 0, 0, 0, 0, 0, 0 };

	public C_NPCTalk(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);

		int ObjectId = readD();
		L1Object Object = L1World.getInstance().findObject(ObjectId);
		L1PcInstance pc = client.getActiveChar();
		if (pc == null)
			return;

		if(Object != null) {
			if (Object.getTileLineDistance(pc) > 5) {
				return;
			}
		}
		
		/** 엔샵 부분 정리 체크 */
		if (ObjectId == 7626) {
			pc.sendPackets(new S_NcoinShop(pc));
			return;
		} else if (ObjectId == 77221 || ObjectId == 77223 || ObjectId == 70014 || ObjectId == 60187) {
			Object = L1World.getInstance().isNpcShop(ObjectId);
			pc.sendPackets(new S_PremiumShopSellList(Object.getId(), pc));
			return;
		} else if (Object == null) {
			Object = L1World.getInstance().isNpcShop(ObjectId);
			if (Object == null)
				return;
		}

		int npcid = ((L1NpcInstance) Object).getNpcTemplate().get_npcId();
		int mapvalue = 0;
		mapvalue = action.length;
		switch (npcid) {
		case 50015:// 말하는 섬 (루카스)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_talk_island, mapvalue));
			return;
		case 50024:// 글루딘 마을 (아스터)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_gludio, mapvalue));
			return;
		case 50082:// 화전민 마을 (지프란)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_orc, mapvalue));
			return;
		case 50054:// 우드벡 마을 (트레이)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_woodbec, mapvalue));
			return;
		case 50056: // 은기사 마을 (메트)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_silver_knight, mapvalue));
			return;
		case 50020:// 켄트성 마을 (스텐리)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_kent, mapvalue));
			break;
		case 50036:// 기란 마을 (윌마)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_giran, mapvalue));
			return;
		case 50066:// 하이네 마을 (리올)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_heine, mapvalue));
			return;
		case 50039:// 웰던 마을 (레슬리)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_werldern, mapvalue));
			return;
		case 50051:// 오렌 마을 (키리우스)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_oren, mapvalue));
			return;
		case 50044:
		case 50046:// 아덴 마을 (엘레리스)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_aden, mapvalue));
			return;
		case 50079:// 침묵의 동굴 마을 (다니엘)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_scave, mapvalue));
			return;
		case 3000005:// 베히모스 (데카비아)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_behemoth, mapvalue));
			return;
		case 3100005:// 실베리아 (샤리엘)
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_silveria, mapvalue));
			return;
		case 100287:// pc방 마을 피아트
			pc.sendPackets(new S_TelePortUi(ObjectId, action, T_pcbang, mapvalue));
			return;
		case 202055: // 클라우디아 (소피)
			mapvalue = claudiaact.length;
			pc.sendPackets(new S_TelePortUi(ObjectId, claudiaact, T_claudia, mapvalue));
			return;
		}

		// L1Gambling gam = new L1Gambling();
		if (Object != null && pc != null) {
			if (Object instanceof L1NpcInstance) {
				pc.talkingNpcObjid = ObjectId;
			}

			// NPC Talk 정보
			L1NpcAction action = NpcActionTable.getInstance().get(pc, Object);
			if (action != null) {
				L1NpcHtml html = action.execute("", pc, Object, new byte[0]);
				if (html != null) {
					S_NPCTalkReturn snt = new S_NPCTalkReturn(Object.getId(), html);
					pc.sendPackets(snt, true);
				}
				return;
			}
			/** 미니게임 **/
			if (Object instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) Object;
				if (npc.getNpcTemplate().get_npcId() == 400064) {
					// gam.dealerTrade(pc);
				}
			}
			Object.onTalkAction(pc);
		} else {
			_log.severe(pc.getName() + " x:" + pc.getX() + " y:" + pc.getY() + " map:" + pc.getMapId() + " objid=" + ObjectId);
		}
	}

	@Override
	public String getType() {
		return C_NPC_TALK;
	}
}