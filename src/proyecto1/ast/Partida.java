/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;
import java.util.List;
/**
 *
 * @author daish
 */
public class Partida {
    public String id;
    public String player1Id;
    public String player2Id;
    public int rounds;
    
    // Scoring
    public int dmgPoint, healPoint, defPoint, victoryBonus, penalty;
    
    // Bonuses
    public List<Accion> mageCombo;
    public int mageComboPoints;
    public List<Accion> warriorCombo;
    public int warriorComboPoints;
    public int lowHealthVictory;

    // Constructor con todos los parámetros
    public Partida(String id, String p1, String p2, int rounds, 
                   int dp, int hp, int defp, int vb, int pen,
                   List<Accion> mCombo, int mPts, List<Accion> wCombo, int wPts, int lhv) {
        this.id = id;
        this.player1Id = p1;
        this.player2Id = p2;
        this.rounds = rounds;
        this.dmgPoint = dp; this.healPoint = hp; this.defPoint = defp;
        this.victoryBonus = vb; this.penalty = pen;
        this.mageCombo = mCombo; this.mageComboPoints = mPts;
        this.warriorCombo = wCombo; this.warriorComboPoints = wPts;
        this.lowHealthVictory = lhv;
    }
}