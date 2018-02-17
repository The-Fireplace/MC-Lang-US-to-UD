#include "LinkedList.h"
#include <iostream>
using namespace std;

//Construct the ListNode using the content passed to the constructor
ListNode::ListNode(int content) {
	this->content=content;
}

//Gets a pointer to the next ListNode
ListNode* ListNode::get_next() {
	return this->next_ListNode;
}

//Gets a pointer to the previous ListNode
ListNode* ListNode::get_prev() {
	return this->prev_ListNode;
}

//Sets the pointer to the next ListNode
void ListNode::set_next(ListNode* next_ListNode) {
	this->next_ListNode = next_ListNode;
}

//Sets the pointer to the previous ListNode
void ListNode::set_prev(ListNode* prev_ListNode) {
	this->prev_ListNode = prev_ListNode;
}

LinkedList::LinkedList() {
	this->size = 0;
	this->head_node = NULL;
	this->tail_node = NULL;
}

//Destructor
LinkedList::~LinkedList() {
	this->destr_helper(this->head_node);
}

void LinkedList::destr_helper(ListNode* n) {
	if (n == NULL)
		return;
	else{
		this->destr_helper(n->get_next());
		n->content = 0;
		n = NULL;
	}
}

//Inserts the integer passed as the second argument at the position passed to the first argument, or prints an error message if the first argument is an invalid index.
void LinkedList::insert(int index, int content) {
	if (index > this->size || index < 0) {
		cout << "Invalid insertion index " << index << " for LinkedList of size " << this->size << "." << endl;
		return;
	}

	ListNode* new_ListNode = new ListNode(content);

	if (index == 0) {
		new_ListNode->set_next(this->head_node);
		this->head_node = new_ListNode;
		if(this->size == 0)//Ensure that the list has a tail ListNode when the first item is added.
			this->tail_node = this->head_node;
	} else if (index == this->size) {
		this->tail_node->set_next(new_ListNode);
		new_ListNode->set_prev(this->tail_node);
		this->tail_node = new_ListNode;
	} else {
		ListNode* temp_ListNode = head_node;
		for (int i=0; i < index - 1; i++, temp_ListNode = temp_ListNode->get_next());
		new_ListNode->set_next(temp_ListNode->get_next());
		temp_ListNode->set_next(new_ListNode);
		new_ListNode->set_prev(temp_ListNode);
		new_ListNode->get_next()->set_prev(new_ListNode);
	}
	this->size++;
}

//Deletes the ListNode at the index passed, or prints an error message if the index is invalid.
void LinkedList::remove(int index) {
	if (index >= this->size || index < 0) {
		cout << "Invalid deletion index " << index << " for LinkedList of size " << this->size << "." << endl;
		return;
	}

	if (index == 0) {
		ListNode* del_ListNode = this->head_node;
		this->head_node = del_ListNode->get_next();
		del_ListNode->content = 0;
		delete del_ListNode;
	} else if (index == this->size - 1) {
		ListNode* del_ListNode = this->tail_node;
		this->tail_node = del_ListNode->get_prev();
		del_ListNode->content = 0;
		delete del_ListNode;
	} else {
		ListNode* temp_ListNode = head_node;
		for (int i=0; i < index-1; i++, temp_ListNode = temp_ListNode->get_next());
		ListNode* del_ListNode = temp_ListNode->get_next();
		temp_ListNode->set_next(del_ListNode->get_next());
		temp_ListNode->get_next()->set_prev(temp_ListNode);
		del_ListNode->content = 0;
		delete del_ListNode;
	}
	this->size--;
}

//Deletes everything in the list.
void LinkedList::empty() {
	int tmp_size = this->size;
	for(int i=0;i<tmp_size;i++)
		remove(0);
}

//Gets the content of the ListNode at the specified index, or prints an error and returns 0 if the index is invalid.
int LinkedList::get(int index) {
	if (index >= this->size || index < 0) {
		cout << "Attempting to get value at invalid index " << index << " for LinkedList of size " << this->size << "." << endl;
		return 0;
	}
	if(index > this->size/2){//Iterate from the tail backwards if the index is in the second half of the list
		ListNode* temp_ListNode = tail_node;
		for (int i=0; i < index; i++, temp_ListNode = temp_ListNode->get_prev());
		return temp_ListNode->content;
	}else{
		ListNode* temp_ListNode = head_node;
		for (int i=0; i < index; i++, temp_ListNode = temp_ListNode->get_next());
		return temp_ListNode->content;
	}
}

//Sets the content of the ListNode at the specified index to the second argument
void LinkedList::set(int index, int new_value) {
	if (index >= this->size || index < 0) {
		cout << "Attempting to set value at invalid index " << index << " for LinkedList of size " << this->size << "." << endl;
		return;
	}
	ListNode* temp_ListNode = head_node;
	for (int i=0; i < index; i++, temp_ListNode = temp_ListNode->get_next());
	temp_ListNode->content = new_value;
}

//Searches the list for a value, and returns the first index where that value is, or -1 if it isn't found.
int LinkedList::search(int value){
	ListNode* temp_ListNode = head_node;
	for (int i=0; i < this->size; i++, temp_ListNode = temp_ListNode->get_next())
		if(temp_ListNode->content == value)
			return i;
	return -1;
}

//Prints the information in the LinkedList
void LinkedList::print() {
	ListNode* temp_ListNode = this->head_node;
	cout << "{";
	if (temp_ListNode != NULL) {
		cout << " " << temp_ListNode->content;
		temp_ListNode = temp_ListNode->get_next();
		for (int i=1; i<this->size; i++, temp_ListNode = temp_ListNode->get_next())
			cout << ", " << temp_ListNode->content;
	}
	cout << " }" << endl;
}
