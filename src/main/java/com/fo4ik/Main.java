package com.fo4ik;

import com.fo4ik.diaply.Menu;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    void init(){
        Menu menu = new Menu();
        menu.showMenu();
    }
}