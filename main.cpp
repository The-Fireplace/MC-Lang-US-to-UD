//#include "LinkedList.h"
//#include "LinkedMap.h"
#include <fstream>
#include <iostream>
#include <string>

using namespace std;

//LinkedMap* charMap;

void initCharacterMap(){
	//charMap = new LinkedMap();
	const wchar_t normal[] = L"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()-_[]{};':\",.<>/?";
	const wchar_t inverse[] = L"∀𐐒Ↄ◖ƎℲ⅁HIſ⋊⅂WᴎOԀΌᴚS⊥∩ᴧMX⅄Zɐqɔpǝɟƃɥıɾʞʃɯuodbɹsʇnʌʍxʎz12Ɛᔭ59Ɫ860¡@#$%^⅋*)(-‾][}{؛,:„'˙></¿";

	wcout << normal << endl;

	for(int i=0;i<sizeof(normal)/sizeof(normal[0]);i++){
		wcout << normal[i];
		//charMap->add(normal[i], inverse[i]);
	}
	wcout << endl;

	wcout << inverse << endl;

	for(int i=0;i<sizeof(inverse)/sizeof(inverse[0]);i++){
		wcout << inverse[i];
		//charMap->add(normal[i], inverse[i]);
	}
	wcout << endl;

	for(int i=0;i<sizeof(inverse)/sizeof(inverse[0]);i++){
		wcout << normal[i] << inverse[i] << endl;
		//charMap->add(normal[i], inverse[i]);
	}
}

int main() {
	initCharacterMap();
	//charMap->print();
	return 0;
}