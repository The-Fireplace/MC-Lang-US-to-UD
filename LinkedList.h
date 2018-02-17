class ListNode {
	ListNode* next_ListNode;
	ListNode* prev_ListNode;
public:
	int content;
	ListNode(int);
	ListNode* get_next();
	ListNode* get_prev();
	void set_next(ListNode*);
	void set_prev(ListNode*);
};

class LinkedList {
	int size;
	ListNode* head_node;
	ListNode* tail_node;
public:
	LinkedList();
	~LinkedList();
	void destr_helper(ListNode*);
	void insert(int, int);
	void remove(int);
	int get(int);
	void set(int, int);
	int search(int);
	void empty();
	void print();
};