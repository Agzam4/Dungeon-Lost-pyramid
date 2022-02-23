package game;

public class SecretItem {

	private int id, itemID;

	private String name;
	private String description;
	
	SecretItem[] secretItems;
	boolean isLock = true;
	
	public SecretItem(String name, SecretItem[] secretItems) {
		this.name = name;
		this.secretItems = secretItems;
	}
	
	public SecretItem(String name) {
		this.name = name;
		secretItems = new SecretItem[0];
	}
	
	public SecretItem[] getSecretItems() {
		return secretItems;
	}
	
	public int getSecretItemsCount() {
		return secretItems.length;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isLock() {
		for (int i = 0; i < secretItems.length; i++) {
			if(secretItems[i].isLock()) return true;
		}
		return isLock;
	}
	
	public void unlock() {
		isLock = false;
	}
	
	public void lock() {
		isLock = true;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	public int getId() {
		return id;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	@Override
	public String toString() {
		return (isLock() ? "[X]" : "[V]" )+ " " + name;
	}
}
