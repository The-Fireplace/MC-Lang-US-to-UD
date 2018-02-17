#include "LinkedMap.h"
#include <iostream>
using namespace std;

//Construct the MapNode using the content passed to the constructor
MapNode::MapNode(char key, char value) {
	this->key=key;
	this->value=value;
}

//Gets a pointer to the next MapNode
MapNode* MapNode::get_next() {
	return this->next_MapNode;
}

//Gets a pointer to the previous MapNode
MapNode* MapNode::get_prev() {
	return this->prev_MapNode;
}

//Sets the pointer to the next MapNode
void MapNode::set_next(MapNode* next_MapNode) {
	this->next_MapNode = next_MapNode;
}

//Sets the pointer to the previous MapNode
void MapNode::set_prev(MapNode* prev_MapNode) {
	this->prev_MapNode = prev_MapNode;
}

LinkedMap::LinkedMap() {
	this->size = 0;
	this->head_node = NULL;
	this->tail_node = NULL;
}

//Destructor
LinkedMap::~LinkedMap() {
	this->destr_helper(this->head_node);
}

void LinkedMap::destr_helper(MapNode* n) {
	if (n == NULL)
		return;
	else{
		this->destr_helper(n->get_next());
		n->key = NULL;
		n->value = NULL;
		n = NULL;
	}
}

//Inserts the integer passed as the second argument at the position passed to the first argument, or prints an error message if the first argument is an invalid index.
void LinkedMap::insert(int index, char key, char value) {
	if (index > this->size || index < 0) {
		cout << "Invalid insertion index " << index << " for LinkedMap of size " << this->size << "." << endl;
		return;
	}

	MapNode* new_MapNode = new MapNode(key, value);

	if (index == 0) {
		new_MapNode->set_next(this->head_node);
		this->head_node = new_MapNode;
		if(this->size == 0)//Ensure that the list has a tail MapNode when the first item is added.
			this->tail_node = this->head_node;
	} else if (index == this->size) {
		this->tail_node->set_next(new_MapNode);
		new_MapNode->set_prev(this->tail_node);
		this->tail_node = new_MapNode;
	} else {
		MapNode* temp_node = head_node;
		for (int i=0; i < index - 1; i++, temp_node = temp_node->get_next());
		new_MapNode->set_next(temp_node->get_next());
		temp_node->set_next(new_MapNode);
		new_MapNode->set_prev(temp_node);
		new_MapNode->get_next()->set_prev(new_MapNode);
	}
	this->size++;
}

void LinkedMap::add(char key, char value){
	this->insert(this->size, key, value);
}

void LinkedMap::replaceFirstNull(char value){
	MapNode* temp_node = head_node;
	for (int i=0; i < size && temp_node->value != NULL; i++, temp_node = temp_node->get_next());
	if(temp_node != NULL)
		temp_node->value = value;
}

//Deletes the MapNode at the index passed, or prints an error message if the index is invalid.
void LinkedMap::remove(int index) {
	if (index >= this->size || index < 0) {
		cout << "Invalid deletion index " << index << " for LinkedMap of size " << this->size << "." << endl;
		return;
	}

	if (index == 0) {
		MapNode* del_node = this->head_node;
		this->head_node = del_node->get_next();
		del_node->key = NULL;
		del_node->value = NULL;
		delete del_node;
	} else if (index == this->size - 1) {
		MapNode* del_node = this->tail_node;
		this->tail_node = del_node->get_prev();
		del_node->key = NULL;
		del_node->value = NULL;
		delete del_node;
	} else {
		MapNode* temp_node = head_node;
		for (int i=0; i < index-1; i++, temp_node = temp_node->get_next());
		MapNode* del_node = temp_node->get_next();
		temp_node->set_next(del_node->get_next());
		temp_node->get_next()->set_prev(temp_node);
		del_node->key = NULL;
		del_node->value = NULL;
		delete del_node;
	}
	this->size--;
}

//Deletes everything in the list.
void LinkedMap::clear() {
	int tmp_size = this->size;
	for(int i=0;i<tmp_size;i++)
		remove(0);
}

//Gets the content of the MapNode at the specified index, or prints an error and returns 0 if the index is invalid.
char LinkedMap::getKey(int index) {
	if (index >= this->size || index < 0) {
		cout << "Attempting to get value at invalid index " << index << " for LinkedMap of size " << this->size << "." << endl;
		return 0;
	}
	if(index > this->size/2){//Iterate from the tail backwards if the index is in the second half of the list
		MapNode* temp_node = tail_node;
		for (int i=0; i < index; i++, temp_node = temp_node->get_prev());
		return temp_node->key;
	}else{
		MapNode* temp_node = head_node;
		for (int i=0; i < index; i++, temp_node = temp_node->get_next());
		return temp_node->key;
	}
}

char LinkedMap::getValue(int index) {
	if (index >= this->size || index < 0) {
		cout << "Attempting to get value at invalid index " << index << " for LinkedMap of size " << this->size << "." << endl;
		return 0;
	}
	if(index > this->size/2){//Iterate from the tail backwards if the index is in the second half of the list
		MapNode* temp_node = tail_node;
		for (int i=0; i < index; i++, temp_node = temp_node->get_prev());
		return temp_node->value;
	}else{
		MapNode* temp_node = head_node;
		for (int i=0; i < index; i++, temp_node = temp_node->get_next());
		return temp_node->value;
	}
}

char LinkedMap::getValue(char key){
	MapNode* temp_node = head_node;
	for (int i=0; i < this->size; i++, temp_node = temp_node->get_next())
		if(temp_node->key == key)
			return temp_node->value;
	cout << "Unknown Character: " << key << endl;
	return NULL;
}

//Sets the content of the MapNode at the specified index to the second argument
void LinkedMap::set(int index, char new_key, char new_value) {
	if (index >= this->size || index < 0) {
		cout << "Attempting to set value at invalid index " << index << " for LinkedMap of size " << this->size << "." << endl;
		return;
	}
	MapNode* temp_node = head_node;
	for (int i=0; i < index; i++, temp_node = temp_node->get_next());
	temp_node->key = new_key;
	temp_node->value = new_value;
}

//Prints the information in the LinkedMap
void LinkedMap::print() {
	MapNode* temp_node = this->head_node;
	cout << "{";
	if (temp_node != NULL) {
		cout << " [ " << temp_node->key << ", " << temp_node->value << " ] ";
		temp_node = temp_node->get_next();
		for (int i=1; i<this->size; i++, temp_node = temp_node->get_next())
			cout << ", [ " << temp_node->key << ", " << temp_node->value << " ] ";
	}
	cout << " }" << endl;
}
