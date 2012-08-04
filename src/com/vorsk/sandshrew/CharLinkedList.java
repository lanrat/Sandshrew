package com.vorsk.sandshrew;

public class CharLinkedList {
	private Node first;
	private Node last;
	private int size;
	

	public void reverse(){
		Node previous = null;
		Node current = first;
		Node next = null;
		first = last;
		last = current;
		
		while (current != null){
			next = current.next;
			current.next = previous;
			previous = current;
			current = next;
			
		}
	}
	
	public void add(char bit){
		if (last != null)
		{
			last.next = new Node(bit);
			last = last.next;
		}else if (first == null){
			first = new Node(bit);
			last = first;
		}
		size++;
	}
	
	public int find(char f){
		int i = 1;
		for (Node trav = first; trav != null; trav = trav.next){
			if (trav.bit == f){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public char[] getSub(int start, int count){
		if ((start+count-1) > size || first == null){
			return null;
		}
		char[] out = new char[count];
		Node current = getNode(start);
		for (int i = 0; i < count; i++){
			out[i] = current.bit;
			current = current.next;
		}
		return out;
	}
	
	public char get(int id){
		if (id > size || first == null){
			return (Character) null;
		}
		return getNode(id).bit;
	}
	
	private Node getNode(int id){
		if (id > size || first == null){
			return null;
		}
		Node current = first;
		for (int i = 1; i < id; i++){
			current = current.next;
		}
		return current;
	}
	
	public int size(){
		return size;
	}
	
	public char remove(int id){
		if (id > size){
			return (Character) null;
		}else if (id == 1){
			return removeFirst();
		} else if (id == size)
			return (Character) null; //removing last not supported for now
		Node current = first;
		Node previous = first;
		for (int i = 1; i < id; i++){ //unlike an array, we start from 1
			previous = current;
			current = current.next;
		}
		previous.next = current.next;
		size--;
		return current.bit;
	}
	
	public char removeFirst(){
		if (first != null){
			char out = first.bit;
			first = first.next;
			size--;
			return out;
		}
		return (Character) null; //error
	}
	
	public String toString(){
		char[] out = new char[size];
		int i = 0;
		for ( Node current = first; current != null; current = current.next){
			out[i] = current.bit;
			i++;
		}
		return new String(out);
	}
	
	public class Node{
		public char bit;
		public Node next;
		public Node(char bit){
			this.bit = bit;
		}
	}
	
}
