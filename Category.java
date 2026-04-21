package expenseTracker;

public enum Category {
	
	FOOD, TRANSPORT, SHOPPING, HEALTH, ENTERTAINMENT, RENT, UTILITIES, OTHER;
	
	//Converts user input "food" → Category.FOOD safely
	public static Category fromString(String s) {
		try {
			return Category.valueOf(s.trim().toUpperCase());  /*Category.valueOf(...) 
																Built-in enum method Converts a 
																String → Enum constant*/
		}
		catch (IllegalArgumentException e) {
			return OTHER;   //never crashes
		}
		
	}
	
	// Converts FOOD → "Food" for nice console display

	public String display(){
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}








/*
  name()
Built-in enum method
Returns enum constant as String

charAt(0)
Gets first character

substring(1)
Gets rest of string

*/