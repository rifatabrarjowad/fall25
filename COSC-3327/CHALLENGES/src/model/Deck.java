package model;

public interface Deck
{
	public int size();
	public Card top();
	public void removeTop();
	//implement equals:
	public boolean equals(Object obj);
}
