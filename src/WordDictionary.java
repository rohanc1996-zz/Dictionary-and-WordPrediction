/* class WordDictionary
 *
 * COSC 102, Colgate University
 * 
 * YOU MUST IMPLEMENT THIS CODE.
 * DO NOT CHANGE ANY METHOD SIGNATURES.
 * 
 * Code for the Trie data structure to store the dictionary
 */
import java.util.*;
public class WordDictionary
{
	private class TrieNode
	{
		private HashMap<Character, TrieNode> map;//stored in each Node
		private char data;
		private boolean checker;//true means it is a word, false means otherwise
		public TrieNode()
		{
			this((char) 0);
		}
		public TrieNode(char c)
		{
			this.checker=false;
			this.data=c;
			this.map=new HashMap<Character, TrieNode>();
		}
	}
	TrieNode root;
	public WordDictionary()// constructor
	{
		this.root=new TrieNode();//start from a space so that all words can branch out from it
	}
	public boolean add(String str)// adds a word to the dictionary
	{// returns true if added;  false if already there (or a problem)
		str = str.trim().toLowerCase();//converts to lower case
		TrieNode current=root;
		for(int i=0; i<str.length(); i++)
		{
			char c=str.charAt(i);
			if(current.map.get(c)!=null)//if there already is a branch with that particular character from current
				current=current.map.get(c);
			else//if that node isn't already there
			{
				current.map.put(c, new TrieNode(c));
				current=current.map.get(c);
			}
		}
		if(current.checker)//if the word is already present in the trie
			return false;
		current.checker=true;
		return true;
	}
	public Collection<String> getCompletions(String str, int max)// returns a Collection of Strings containing words that begin
	{// with the prefix str, up to _max_ Strings
		ArrayList<String> words=new ArrayList<String>();//to be returned
		TrieNode current=root;
		String currentword="";
		char c;
		for(int i=0; i<str.length(); i++)
		{
			c=str.charAt(i);
			current=current.map.get(c);
		}
		Queue<TrieNode> children=new LinkedList<TrieNode>();
		Queue<String> word= new LinkedList<String>();
		children.add(current);
		word.add(str);
		while(children.peek()!=null && word.peek()!=null && max>0)//BFS
		{
			if(children.peek()!=null && word.peek()!=null)
			{
				currentword=word.remove();
				current=children.remove();
			}
			for(char ch:current.map.keySet())//add the children of the current node to the queue
			{
				children.add(current.map.get(ch));
				word.add(currentword+ch);
			}
			if(current.checker)
			{
				words.add(currentword);
				max--;
			}
			c=current.data;
			currentword+=c;
		}
		return words;
	}
	public Collection<String> findClosest(String str, int dist, int max)// returns a Collection of Strings that are within _dist_ hamming
	{// distance of the target string, up to _max_ Strings
		str = str.trim().toLowerCase();
		ArrayList<HashSet<String>> closest=new ArrayList<HashSet<String>>();//all the strings found
		for(int i=0; i<=dist; i++)
			closest.add(new HashSet<String>());
		ArrayList<String> actual=new ArrayList<String>();//final thing that's returned
		helper(root, "", str, dist, closest);//does the recursion and fills up closest
		int index=dist;
		while(index>=0 && actual.size()<max)//to return only the maximum possible number of items (at most)
		{
			Iterator<String> words=closest.get(index).iterator();//iterates through all the words found in the helper
			while(words.hasNext())
			{
				actual.add(words.next());
				if(actual.size()==max)
					return actual;
			}
			index--;
		}
		return actual;
	}
	public void removeprevious(ArrayList<HashSet<String>> closest, String word)//Method removes the word if it's already there
	{
		for(int i=0; i<closest.size(); i++)
		{
			if(closest.get(i).contains(word))//if the word's already there, it's been added at a higher distance
				closest.get(i).remove(word);//we want to remove it and keep the lower distance version
		}
	}
	public void helper(TrieNode current, String prefix, String suffix, int dist, ArrayList<HashSet<String>> closest)
	{
		if(dist<0)//base case
			return;
		if(suffix.equals("") && current.checker)//if we've found a word
		{
			removeprevious(closest, prefix);//remove it's previous copy since this one's at a lower distance
			closest.get(dist).add(prefix);//if the suffix is empty, that means there are no more options
		}
		if(suffix.length()>0)//to prevent out of bounds
			helper(current, prefix, suffix.substring(1), dist-1, closest);//deletion
		if(suffix.equals(""))//if we've reached max depth, can't do deletion or swap
		{
			for(char c:current.map.keySet())
				helper(current.map.get(c), prefix+c, suffix, dist-1, closest);//insert before the first character in suffix
		}
		else//if we aren't at max depth, we can do all 3
		{
			for(char c:current.map.keySet())
			{
				if(c==suffix.charAt(0))//if it's a match, add the first char from suffix to the end of prefix
					helper(current.map.get(c), prefix+c, suffix.substring(1), dist, closest);//dist is unchanged
				else
				{
					if(suffix.length()>0)//to prevent string index out of bounds
						helper(current.map.get(c), prefix+c+"", suffix.substring(1), dist-1, closest); //swap the first character in suffix
					helper(current.map.get(c), prefix+c+"", suffix, dist-1, closest);//insert before the first character in suffix
				}
			}
		}
	}
	public static void main(String[] args)//for testing
	{
		WordDictionary dict=new WordDictionary();
		dict.add("word");
		dict.add("wor");
		dict.add("worded");
		dict.add("work");
		dict.add("bird");
		dict.add("works");
		dict.add("worker");
		System.out.println(dict.findClosest("word",  3,  7).toString());
	}
}