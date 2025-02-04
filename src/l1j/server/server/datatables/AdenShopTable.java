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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1AdenShopItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class AdenShopTable {

	private static Logger _log = Logger.getLogger(AdenShopTable.class.getName());

	private static AdenShopTable _instance;

	private final HashMap<Integer, L1AdenShopItem> _allShops = new HashMap<Integer, L1AdenShopItem>();
	private List<L1AdenShopItem> list = new ArrayList<L1AdenShopItem>();

	public static int data_length = 0;

	public static AdenShopTable getInstance() {
		if (_instance == null) {
			_instance = new AdenShopTable();
		}
		return _instance;
	}

	public static void reload() {
		AdenShopTable oldInstance = _instance;
		_instance = new AdenShopTable();
		oldInstance._allShops.clear();
		oldInstance.list.clear();
	}

	private AdenShopTable() {
		loadShops();
	}

	private void loadShops() {
		data_length = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM shop_aden ORDER BY id ASC");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemid = rs.getInt("itemid");
				// String itemname = rs.getString("itemname");
				int price = rs.getInt("price");
				int type = rs.getInt("type");
				int status = rs.getInt("status");
				String html = rs.getString("html");
				int pack = rs.getInt("pack");
				int enchant = rs.getInt("enchant");
				if (type < 2 || type > 5) type = 5;
				_allShops.put(itemid, new L1AdenShopItem(itemid, price, pack, html, status, type, enchant));
				list.add( _allShops.get(itemid) );
				L1Item item = ItemTable.getInstance().getTemplate(itemid);
				String itemname = item.getName();
				if (pack > 1) {
					itemname = itemname + "(" + pack + ")";
				}
				if (item.getMaxUseTime() > 0) {
					itemname = itemname + " [" + item.getMaxUseTime() + "]";
				} else if (item.get_enchant() > 0) {
					itemname = + item.get_enchant() + " " + item.getName();
				} else if (item.getItemId() == 65648) {
					itemname = itemname + " [7��]";
				} else if (item.getItemId() >= 30022 && item.getItemId() <= 30025) {
					itemname = itemname + " [18000]";
				} else if (item.getItemId() >= 22320 && item.getItemId() <= 22327) {
					itemname = itemname + " [3����]";
				}
				data_length += 30;
				data_length += itemname.getBytes("UTF-8").length + 2;
				if (!html.equalsIgnoreCase("")) {
					byte[] test = html.getBytes("UTF-8");
					for (int i = 0; i < test.length;) {
						if ((test[i] & 0xff) >= 0x7F)
							i += 2;
						else
							i += 1;
						data_length += 2;
					}
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (Exception e) {

		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public L1AdenShopItem get(int itemid) {
		return _allShops.get(itemid);
	}

	public int Size() {
		return _allShops.size();
	}

	public Collection<L1AdenShopItem> toArray() {
		return _allShops.values();
	}

	public List<L1AdenShopItem> getList() {
		return list;
	}

}
