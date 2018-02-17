class MapNode {
	MapNode* next_MapNode;
	MapNode* prev_MapNode;
public:
	char key, value;
	MapNode(char, char);
	MapNode* get_next();
	MapNode* get_prev();
	void set_next(MapNode*);
	void set_prev(MapNode*);
};

class LinkedMap {
	int size;
	MapNode* head_node;
	MapNode* tail_node;
public:
	LinkedMap();
	~LinkedMap();
	void destr_helper(MapNode*);
	void insert(int, char, char);
	void add(char, char);
	void remove(int);
	char getKey(int);
	char getValue(int);
	char getValue(char);
	void set(int, char, char);
	void clear();
	void print();
	void replaceFirstNull(char value);
};