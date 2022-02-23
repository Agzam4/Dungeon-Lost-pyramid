package progress;

import java.util.Arrays;

import game.SecretItem;

public class ProgressData {

	private SecretItem secretItem;
	private long[] coins = new long[] {0l, 0l, 0l};
	
	private long[][] itemCount = {
			{0},
			{0, 0},
			{0, 0, 0, 0, 0, 0}
	};

	private long[][] itemLevel = {
			{1},
			{1, 1},
			{1, 1, 1, 1, 1, 1}
	};
	
	private String[][] itemName;
	
	private String[][] itemDescription = {
			{
				"Gives you a chance [LVL50]% to evade crushers and spikes"
			}, 
			{
				"<no description>",
				"Protects from the dark effect & Allows you to determine the presence of a secret room"
			}, 
			{
				"Protects against [LVL] crushers",
				"Gives temporary invulnerability to the crusher and spikes for [5LVL] seconds",
				"The player can make a leap forward",
				"Increases the number of find treasures",
				"Increases the light from the player",
				"Displays a piece of the dungeon map"
			}
	};
	
	int selectedID = -1;
	int selectedITEM_ID = -1;
	
	public void updateItem(int id, int itemId) {
		if(id == -1) return;
		if(itemId == -1) return;
		if(getItemCount(id, itemId) >= getNeedItemCount(id, itemId)) {
			if(getCoins(id) >= getNeedItemCoins(id, itemId)) {
				itemCount[id][itemId] -= getNeedItemCount(id, itemId);
				coins[id] -= getNeedItemCoins(id, itemId);
				itemLevel[id][itemId]++;
			}else {
				System.out.println("[ProgressData] NEED COINS");
			}
		}else {
			System.out.println("[ProgressData] NEED ITEMS");
		}
	}
	
	public long getItemCount(int id, int itemId) {
		return itemCount[id][itemId];
	}
	
	public long getItemLevel(int id, int itemId) {
		try {
			return itemLevel[id][itemId];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("[ProgressData] ID:" + id + " ITEM:" + itemId);
			return -1;
		}
	}

	public long getNeedItemCount(int id, int itemId) {
		return getItemLevel(id, itemId);
	}
	
	public long getNeedItemCoins(int id, int itemId) {
		return getItemLevel(id, itemId)*100;
	}
	
	public String getItemName(int id, int itemId) {
		return itemName[id][itemId];
	}
	
	public String getItemDescription(int id, int itemId) {
		return itemDescription[id][itemId]
				.replaceAll("\\[LVL\\]", "" + getItemLevel(id, itemId))
				.replaceAll("\\[5LVL\\]", "" + getItemLevel(id, itemId)*5)
				.replaceAll("\\[LVL50\\]", ("" + get50(getItemLevel(id, itemId))).replaceAll("\\.", ","));
	}
	
	private double get50(long lvl) {
		return Math.round((50 - 50
				/
				((lvl+100)/100d))*100
				)/100d;
	}
	
	public ProgressData() {
		initSecretItem();
	}

	public long getCoins(int id) {
		return coins[id];
	}

	private void initSecretItem() {
		// TODO: try load
		secretItem = new SecretItem("Water of live",
				new SecretItem[] {
						new SecretItem("Scarab", new SecretItem[] {
								new SecretItem("Unnamed"),
								new SecretItem("Golden Bast"),
								new SecretItem("Wind")
						}),
						new SecretItem("Scroll of Wisdom", new SecretItem[] {
								new SecretItem("Jug of wealth"),
								new SecretItem("Golden Sun"),
								new SecretItem("Map fragment")
						})
				});
		
		itemName = new String[3][];
		itemName[0] = new String[1];
		itemName[0][0] = secretItem.getName();
		fillItemName(secretItem, 1);
		
		for (int i = 0; i < itemName.length; i++) {
			System.out.println("[ProgressData] " + Arrays.toString(itemName[i]));
		}
		
		SecretItem map = getItemForName("Map fragment"); // TODO
		map.unlock();
		setSelectedItem(2-map.getId(), map.getItemID());
	}
	
	public SecretItem getItemForName(String name) {
		if(secretItem.getName().equals(name)) return secretItem;
		return findItem(name, secretItem);
	}
	
	private SecretItem findItem(String name, SecretItem item) {
		for (int i = 0; i < item.getSecretItemsCount(); i++) {
			if(item.getSecretItems()[i].getName().equals(name)) return item.getSecretItems()[i];
			SecretItem find = findItem(name, item.getSecretItems()[i]);
			if(find != null) return find;
		}
		return null;
	}
	
	private void fillItemName(SecretItem item, int level) {
		int start = 0;
		if(item.getSecretItemsCount() > 0) {
			int size = itemName[level] == null ? 0 : itemName[level].length;
			if(size != 0) start = size;
			String[] last = itemName[level];
			itemName[level] = new String[size + item.getSecretItemsCount()];
			if(last != null) {
				for (int i = 0; i < last.length; i++) {
					itemName[level][i] = last[i];
				}
			}
		}
		System.out.print("[ProgressData] ");
		for (int i = 0; i < level; i++) {
			System.out.print("  ");
		}
		System.out.println(item);
		for (int i = 0; i < item.getSecretItemsCount(); i++) {
			itemName[level][start+i] = item.getSecretItems()[i].getName();
			item.getSecretItems()[i].setId(level);
			item.getSecretItems()[i].setItemID(start+i);
			fillItemName(item.getSecretItems()[i], level+1);
		}
	}
	
	public SecretItem getSecretItem() {
		return secretItem;
	}

	public void setSelectedItem(int selectedID, int selectedITEM_ID) {
		System.out.println("[ProgressData] Selected Item: " + selectedID + " " + selectedITEM_ID);
		this.selectedID = selectedID;
		this.selectedITEM_ID = selectedITEM_ID; 
	}

	public int getSelectedID() {
		return selectedID;
	}
	
	public int getSelectedITEM_ID() {
		return selectedITEM_ID;
	}
}
