
class Cell {

    private int state;//0 for hidden, 1 for revealed, 2 for flagged
    private int minesBordered;
    private boolean hasMine;
    Cell(){
        state=0;
        minesBordered = 0;
        hasMine=false;
    }
    int getState(){
        return state;
    }
    void setState(int i) {
        //In Minesweeper, this method is passed 0 for hidden, 1 for revealed, 2 for flagged
        state= i;
    }
    boolean hasMine(){
        return hasMine;
    }
    void addMine(){
        hasMine=true;
    }
    int getMinesBordered(){
        return minesBordered;
    }
    void setMinesBordered(int i){
        minesBordered=i;
    }
}
