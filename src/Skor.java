public class Skor {
    private int skor = 0;
    
    public void tambahSkor() {
        skor++;
    }
    
    public void setSkor(int newSkor) {
        this.skor = newSkor;
    }
    
    public int getSkor() {
        return skor;
    }
    
    public void reset() {
        skor = 0;
    }
}
