package com.vorsk.sandshrew;

public class PCMLinkedList {
	private PCMNode first;
	private PCMNode last;
	private int size = 0;
	
	public void add(short bit){
		if (last != null) //2nd+
		{
			last.next = new PCMNode(size, bit); //memory leak here....
			last = last.next;
		}else if (first == null){ //first node
			first = new PCMNode(0,bit);
			last = first;
		}
		size++;
	}
	
	/*
	public int find(char f){
		int i = 1;
		for (PCMNode trav = first; trav != null; trav = trav.next){
			if (trav.amp == f){
				return i;
			}
			i++;
		}
		return -1; //error
	}*/
	
	public void clear(){
		size=0;
		last=null;
		first=null;
		System.gc();
	}
	
	public PCMNode first(){
		return first; //be carful about this one....
	}
	
	/*
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
	}*/
	/*
	public PCMNode get(int id){
		if (id > size || first == null){
			return null; //error
		}
		return getNode(id);
	}*/
	
	public PCMNode getNode(int id){
		if (id >= size || first == null){
			return null; //out of range
		}
		PCMNode current = first;
		for (int i = 0; i < id; i++){
			current = current.next;
		}
		return current;
	}
	
	public int size(){
		return size;
	}
	
	public PCMNode remove(int id){
		if (id >= size){
			return null;
		}else if (id == 1){
			return removeFirst();
		} else if (id == size-1)
			return null; //removing last not supported for now
		PCMNode current = first;
		PCMNode previous = first;
		for (int i = 1; i < id; i++){ //unlike an array, we start from 1
			previous = current;
			current = current.next;
		}
		previous.next = current.next;
		size--;
		return current;
	}
	
	public PCMNode removeFirst(){
		if (first != null){
			PCMNode out = first;
			first = first.next;
			size--;
			return out;
		}
		return null; //error
	}
	
	/*public String toString(){
		char[] out = new char[size];
		int i = 0;
		for ( Node current = first; current != null; current = current.next){
			out[i] = current.bit;
			i++;
		}
		return new String(out);
	}*/
	
	public class PCMNode{
		public short amp; //the amp
		public int idx; //the id
		public PCMNode next; //next!
		public PCMNode(int idx,short amp){
			this.idx = idx;
			this.amp = amp;
		}
	}
	
}
